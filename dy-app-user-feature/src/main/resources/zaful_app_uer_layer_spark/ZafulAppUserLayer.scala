package com.globalegrow.dy.tianchi.statistics

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

/**
  * 计算 zaful app 用户分层数据
  */
object ZafulAppUserLayer {

  def main(args: Array[String]): Unit = {

    val spark: SparkSession = SparkSession.builder()
      .appName("statistics_zaful_app_user_layer")
      .config("spark.debug.maxToStringFields", "500")
      .config("spark.dynamicAllocation.enabled", "false")
      .config("hive.metastore.uris", "thrift://172.31.61.192:9083,thrift://172.31.62.7:9083")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .config("spark.sql.shuffle.partitions", "600")
      .config("spark.default.parallelism", "600")
      .config("spark.network.timeout", "300")
      .enableHiveSupport()
      .getOrCreate()

      val df1 = new SimpleDateFormat("yyyy-MM-dd")
      val df2 = new SimpleDateFormat("yyyyMMdd")
      val c: Calendar = Calendar.getInstance
      c.setTime(new Date())


     //val bak_today: String = df2.format(c.getTime)

    c.add(Calendar.DATE, -1)
    // dt yyyy-MM-dd
    val dt: String = df1.format(c.getTime)

    val order_dt: String = df2.format(c.getTime)

    val zaful_app_user_visit: String =
      s"""insert OVERWRITE table tmp.zaful_app_user_visit
        |  select platform,appsflyer_device_id,
        |  sum(case when event_name in ('af_add_to_wishlist', 'af_add_to_bag') or (event_name='af_view_product' AND get_json_object(event_value, '$$.af_changed_size_or_color') = '0') then 1
        |           else 0 end) event_count,'$dt' dt from ods.ods_app_burial_log where lower(site) = 'zaful'
        |  AND dt BETWEEN date_add('$dt',-90) AND '$dt' group by platform,appsflyer_device_id
      """.stripMargin
// SET hive.mapred.reduce.tasks.speculative.execution = false;
    //SET mapreduce.map.speculative = false;
    //SET mapreduce.reduce.speculative = false;
    sql(zaful_app_user_visit)

    val zaful_old_user: String =
      s"""insert OVERWRITE table dw.zaful_old_user PARTITION(dt='$dt')
         |  select distinct user_id from ods.ods_m_zaful_eload_order_info where order_status not in(0,11,13) and dt='$order_dt' AND from_unixtime(pay_time,'yyyy-MM-dd') BETWEEN date_add('$dt',-360) AND '$dt'
       """.stripMargin

    sql(zaful_old_user)

    val zaful_app_return_visit_dw =
      s"""insert OVERWRITE table dw.zaful_app_return_visit PARTITION(dt = '$dt')
         |
         |  select a.platform,a.appsflyer_device_id,b.user_id,a.event_count
         |  from (select platform,appsflyer_device_id,event_count from tmp.zaful_app_user_visit) a
         |    left  join (select identify,user_id from (select identify,user_id,ROW_NUMBER() OVER(PARTITION BY identify ORDER BY mapping_date desc) AS rn from dw.zaful_dim_user_cookie_mapping c where c.site = 'zaful' and c.platform in ('ios','android') and c.mapping_date BETWEEN date_add('${dt}',-90) AND '${dt}'  AND c.user_id is not null AND c.user_id <> 0 and c.user_id <> '') c1 where c1.rn = 1) b
         |      on a.appsflyer_device_id = b.identify
       """.stripMargin

    sql(zaful_app_return_visit_dw)

    val zaful_app_user_new_and_old_dw =
      s"""insert OVERWRITE table dw.zaful_app_user_new_and_old PARTITION (dt='$dt')
         |  select a.appsflyer_device_id,concat_ws('',case when a.user_id is null then '1' else '0' end, a.return_visit) as type from
         |  ( select c.appsflyer_device_id,case when c.event_count = 0 then '00' when c.event_count > 0 and c.user_id <> '' then '10' when c.event_count > 0 and c.user_id = '' then '11'  else '00' end as return_visit,b.user_id from  (select appsflyer_device_id,event_count,user_id from dw.zaful_app_return_visit visit where visit.dt='$dt' ) c
         |    left join (select user_id from dw.zaful_old_user old_user where old_user.dt = '$dt') b
         |      on c.user_id = b.user_id) a
       """.stripMargin

    sql(zaful_app_user_new_and_old_dw)
    sql("add jar hdfs:///user/wangzhongfu/elasticsearch-hadoop-5.6.4.jar")
    sql(s"insert OVERWRITE table tmp.zaful_app_user_layer_spark select appsflyer_device_id,type as layer_type,dt from dw.zaful_app_user_new_and_old where dt = '$dt'")


    spark.stop();
  }

}
