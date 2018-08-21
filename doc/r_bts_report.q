--@author ZhanRui
--@date 2018年6月11日 
--@desc  ABTest报表,增加按不同推荐位统计

SET mapred.job.name=abtest_report;
SET mapred.max.split.size=128000000;
SET mapred.min.split.size=128000000;
SET mapred.min.split.size.per.node=128000000;
SET mapred.min.split.size.per.rack=128000000;
SET hive.exec.reducers.bytes.per.reducer = 128000000;
SET hive.merge.mapfiles=true;
SET hive.merge.mapredfiles= true;
SET hive.input.format=org.apache.hadoop.hive.ql.io.CombineHiveInputFormat;
SET hive.merge.size.per.task=256000000;
SET hive.exec.parallel = true; 

--查询获取计划、桶、算法、cookie对应信息
insert overwrite table dw_proj.abtest_info_tmp
SELECT
  x.bind_id,
  x.plan_id,
  x.plan_name,
  x.bucket_id,
  x.bucket_name,
  x.service_id,
  x.service_name,
  p.cookie_code,
  '${ADD_TIME}' as add_time
from
  (
    SELECT
      m.bind_id,
      m.plan_id,
      m.plan_name,
      m.bucket_id,
      m.bucket_name,
      n.service_id,
      n.service_name
    FROM
      dw_proj.b_bucket_bind m
      join dw_proj.b_bts_plan n on m.plan_id = n.plan_id
    WHERE
      m.status = 1
      AND n.status = 1
  ) x
  join dw_proj.r_bts_result p on x.plan_id = p.plan_id
  AND x.bucket_id = p.bucket_id
  AND x.service_id = p.service_id
;

--过滤cookie ID,得到统计所用的部分埋点信息
insert overwrite table dw_proj.abtest_log_tmp
SELECT
  m.log_id,
  m.glb_od,
  m.glb_b,
  m.glb_plf,
  m.glb_t,
  m.glb_ubcta,
  m.glb_x,
  m.glb_pm,
  m.glb_skuinfo,
  n.bucket_id,
  concat_ws('-', m.year, m.month, m.day) as add_time
FROM
  (
    SELECT
      log_id,
      glb_od,
      glb_b,
      glb_plf,
      glb_t,
      glb_ubcta,
      glb_x,
      glb_pm,
      glb_skuinfo,
      year,
      month,
      day
    FROM
      stg.zf_pc_event_info
    WHERE
      concat_ws('-', year, month, day) BETWEEN '${ADD_TIME_W}'
      AND '${ADD_TIME}'
  ) m
  JOIN dw_proj.abtest_info_tmp n ON m.glb_od = n.cookie_code
;


--报表信息统计

--页面PV
INSERT OVERWRITE TABLE dw_proj.report_pv_tmp
SELECT
  count(*) as pv_num,
  bucket_id,
  glb_plf,
  glb_b,
  '${ADD_TIME}' as add_time
FROM
  dw_proj.abtest_log_tmp
WHERE
  add_time = '${ADD_TIME}'
  and glb_t = 'ie'
  and glb_ubcta = ''
GROUP BY bucket_id,glb_plf,glb_b
;


--页面UV
INSERT OVERWRITE TABLE dw_proj.report_uv_tmp
SELECT
  count(*) AS uv_num,
  m.bucket_id,
  m.glb_plf,
  m.glb_b,
  '${ADD_TIME}' as add_time
FROM
  (
    SELECT
      glb_od,
      bucket_id,
      glb_plf,
      glb_b
    FROM
      dw_proj.abtest_log_tmp
    WHERE
      add_time = '${ADD_TIME}'
    GROUP BY
      glb_od,
      bucket_id,
      glb_plf,
      glb_b
  ) m
GROUP BY m.bucket_id,m.glb_plf,m.glb_b
;


--商品曝光数
INSERT OVERWRITE TABLE dw_proj.report_exp_tmp
SELECT
  count(m.sku) AS exposure_num,
  n.bucket_id,
  n.glb_plf,
  n.glb_b,
  m.mrlc,
  '${ADD_TIME}' as add_time
FROM
  (
    SELECT
      log_id,
      get_json_object(glb_ubcta_col, '$.sku') as sku,
      get_json_object(glb_ubcta_col, '$.mrlc') as mrlc
    FROM
      stg.zf_pc_event_ubcta_info
    WHERE
      concat_ws('-', year, month, day) = '${ADD_TIME}'
      AND get_json_object(glb_ubcta_col, '$.sku') <> ''
  ) m
 JOIN (
    SELECT
      log_id,
      bucket_id,
      glb_plf,
      glb_b
    FROM
      dw_proj.abtest_log_tmp
    WHERE
      add_time = '${ADD_TIME}'
      AND glb_t = 'ie'
      AND glb_pm = 'mr'
      AND glb_ubcta <> ''
  ) n ON m.log_id = n.log_id
GROUP BY n.bucket_id,n.glb_plf,n.glb_b,m.mrlc
;


--商品点击数
INSERT OVERWRITE TABLE dw_proj.report_click_tmp
SELECT
  count(*) AS click_num,
  bucket_id,
  glb_plf,
  glb_b,
  get_json_object(glb_ubcta, '$.mrlc') as mrlc,
  '${ADD_TIME}' as add_time
FROM
 dw_proj.abtest_log_tmp
WHERE
  add_time = '${ADD_TIME}'
  AND glb_t = 'ic'
  AND glb_pm = 'mr'
  AND glb_x = 'sku'
  AND glb_skuinfo <> ''
  AND glb_ubcta <> ''
GROUP BY bucket_id,glb_plf,glb_b,get_json_object(glb_ubcta, '$.mrlc') 
;


--商品加购数
INSERT OVERWRITE TABLE dw_proj.report_cart_tmp
SELECT
  count(glb_t) AS addcart_num,
  bucket_id,
  glb_plf,
  get_json_object(glb_ubcta, '$.fmd') as fmd,
  '${ADD_TIME}' as add_time
FROM
  dw_proj.abtest_log_tmp
WHERE
  add_time = '${ADD_TIME}'
  AND glb_t = 'ic'
  AND glb_x = 'ADT'
GROUP BY bucket_id,glb_plf,get_json_object(glb_ubcta, '$.fmd') 
;



--中间表report_sku_user_tmp：取sku,user_id
INSERT OVERWRITE TABLE dw_proj.report_sku_user_map_tmp
SELECT
  m.sku,
  n.glb_u,
  m.bucket_id,
  m.glb_plf,
  m.fmd
FROM
  (
    SELECT
      glb_od,
      regexp_extract(glb_skuinfo, '(.*?sku":")([0-9a-zA-Z]*)(".*?)', 2) AS sku,
      bucket_id,
      glb_plf,
      get_json_object(glb_ubcta, '$.fmd') AS fmd
    FROM
      dw_proj.abtest_log_tmp
    WHERE
      add_time BETWEEN '${ADD_TIME_W}'
      AND '${ADD_TIME}'
      AND glb_t = 'ic'
      AND glb_x = 'ADT'
      AND glb_skuinfo <> ''
      AND glb_ubcta <> ''
  ) m
JOIN dw_zaful_recommend.zaful_od_u_map n ON m.glb_od = n.glb_od
GROUP BY
  m.sku,
  n.glb_u,
  m.bucket_id,
  m.glb_plf,
  m.fmd
;

--中间表report_sku_user_tmp：取sku,user_id
INSERT OVERWRITE TABLE dw_proj.report_sku_user_tmp
SELECT
  p.goods_sn,
  x.user_id,
  x.order_status,
  p.goods_price * p.goods_number as pay_amount
FROM
  (
    SELECT
      order_id,
      user_id,
      add_time,
      order_status
    FROM
      stg.zaful_eload_order_info
    WHERE
      from_unixtime(add_time, 'yyyy-MM-dd') = '${ADD_TIME}'
  ) x
JOIN stg.zaful_eload_order_goods p ON x.order_id = p.order_id
;


--商品下单数
INSERT OVERWRITE TABLE dw_proj.report_order_tmp
SELECT
  count(*) AS order_num,
  x1.bucket_id,
  x1.glb_plf,
  x1.fmd,
  '${ADD_TIME}' as add_time
from
  dw_proj.report_sku_user_map_tmp x1
  JOIN dw_proj.report_sku_user_tmp x2 ON x1.user_id = x2.user_id
  AND x1.sku = x2.goods_sn
GROUP BY x1.bucket_id,x1.glb_plf,x1.fmd
;


--支付订单数
INSERT OVERWRITE TABLE dw_proj.report_purchase_tmp
SELECT
  count(*) AS pay_num,
  x1.bucket_id,
  x1.glb_plf,
  x1.fmd,
  '${ADD_TIME}' as add_time
from
  dw_proj.report_sku_user_map_tmp x1
  JOIN dw_proj.report_sku_user_tmp x2 ON x1.user_id = x2.user_id
  AND x1.sku = x2.goods_sn
where
  x2.order_status not in ('0', '11')
GROUP BY x1.bucket_id,x1.glb_plf,x1.fmd
;


--购买金额
INSERT OVERWRITE TABLE dw_proj.report_pay_amount_tmp
SELECT
  sum(x2.pay_amount) as pay_total,
  x1.bucket_id,
  x1.glb_plf,
  x1.fmd,
  '${ADD_TIME}' as add_time
from
  dw_proj.report_sku_user_map_tmp x1
  JOIN dw_proj.report_sku_user_tmp x2 ON x1.user_id = x2.user_id
  AND x1.sku = x2.goods_sn
where
  x2.order_status not in ('0', '11')
GROUP BY x1.bucket_id,x1.glb_plf,x1.fmd
;


INSERT OVERWRITE TABLE  dw_proj.r_bts_report PARTITION (add_time = '${ADD_TIME}')
SELECT
  x.bind_id,
  x.plan_id,
  x.plan_name,
  x.service_id,
  x.service_name,
  x.bucket_id,
  x.bucket_name,
 '${ADD_TIME}' as report_date,
  NVL(b.uv_num,0),
  NVL(a.pv_num,0),
  NVL(c.exposure_num,0),
  NVL(d.click_num,0),
  NVL(d.click_num / c.exposure_num,0),
  NVL(e.addcart_num,0),
  NVL(e.addcart_num / d.click_num,0),
  NVL(f.order_num,0),
  NVL(f.order_num / e.addcart_num,0),
  NVL(g.pay_num,0),
  NVL(h.pay_total,0),
  NVL(g.pay_num / f.order_num,0),
  NVL(g.pay_num / c.exposure_num,0),
  '首页推荐位'
FROM
(
SELECT
  bind_id,
  plan_id,
  plan_name,
  bucket_id,
  bucket_name,
  service_id,
  service_name,
  add_time
FROM 
dw_proj.abtest_info_tmp 
GROUP BY
  bind_id,
  plan_id,
  plan_name,
  bucket_id,
  bucket_name,
  service_id,
  service_name,
  add_time
)x
left join   (SELECT  sum(pv_num) as pv_num,add_time,bucket_id from  dw_proj.report_pv_tmp where glb_plf in ('pc','m') and glb_b='a' group by add_time,bucket_id ) a 
on x.add_time=a.add_time and x.bucket_id=a.bucket_id
left join   (SELECT sum(uv_num) as uv_num,add_time,bucket_id  from  dw_proj.report_uv_tmp where glb_plf in ('pc','m') and glb_b='a' group by add_time,bucket_id ) b 
on x.add_time=b.add_time and x.bucket_id=b.bucket_id
left join  (SELECT sum(exposure_num) as exposure_num,add_time,bucket_id from  dw_proj.report_exp_tmp where (glb_b='a' and glb_plf='pc' and  mrlc='T_1') or 
(glb_b='a' and glb_plf='m' and mrlc='T_1') group by add_time,bucket_id) c 
on x.add_time=c.add_time and x.bucket_id=c.bucket_id
left join  (SELECT sum(click_num) as click_num,add_time,bucket_id from  dw_proj.report_click_tmp where (glb_b='a' and glb_plf='pc' and  mrlc='T_1') or 
(glb_b='a' and glb_plf='m' and mrlc='T_1') group by add_time,bucket_id) d
on x.add_time=d.add_time and x.bucket_id=d.bucket_id
left join  (SELECT sum(addcart_num) as addcart_num,add_time,bucket_id from  dw_proj.report_cart_tmp where (glb_plf='pc' and fmd='mr_T_1') or 
(glb_plf='m' and fmd='mr_T_1') group by add_time,bucket_id) e 
on x.add_time=e.add_time and x.bucket_id=e.bucket_id
left join  (SELECT sum(order_num) as order_num,add_time,bucket_id from   dw_proj.report_order_tmp where (glb_plf='pc' and fmd='mr_T_1') or 
(glb_plf='m' and fmd='mr_T_1') group by add_time,bucket_id) f
on x.add_time=f.add_time and x.bucket_id=f.bucket_id
left join  (SELECT sum(pay_num) as pay_num,add_time,bucket_id from dw_proj.report_purchase_tmp where (glb_plf='pc' and fmd='mr_T_1') or 
(glb_plf='m' and fmd='mr_T_1') group by add_time,bucket_id) g
on x.add_time=g.add_time and x.bucket_id=g.bucket_id
left join  (SELECT sum(pay_total) as pay_total,add_time,bucket_id from  dw_proj.report_pay_amount_tmp where (glb_plf='pc' and fmd='mr_T_1') or 
(glb_plf='m' and fmd='mr_T_1') group by add_time,bucket_id) h 
on x.add_time=h.add_time and x.bucket_id=h.bucket_id

union all

SELECT
  x.bind_id,
  x.plan_id,
  x.plan_name,
  x.service_id,
  x.service_name,
  x.bucket_id,
  x.bucket_name,
 '${ADD_TIME}' as report_date,
  NVL(b.uv_num,0),
  NVL(a.pv_num,0),
  NVL(c.exposure_num,0),
  NVL(d.click_num,0),
  NVL(d.click_num / c.exposure_num,0),
  NVL(e.addcart_num,0),
  NVL(e.addcart_num / d.click_num,0),
  NVL(f.order_num,0),
  NVL(f.order_num / e.addcart_num,0),
  NVL(g.pay_num,0),
  NVL(h.pay_total,0),
  NVL(g.pay_num / f.order_num,0),
  NVL(g.pay_num / c.exposure_num,0),
  '商详页推荐位'
FROM
(
SELECT
  bind_id,
  plan_id,
  plan_name,
  bucket_id,
  bucket_name,
  service_id,
  service_name,
  add_time
FROM 
dw_proj.abtest_info_tmp 
GROUP BY
  bind_id,
  plan_id,
  plan_name,
  bucket_id,
  bucket_name,
  service_id,
  service_name,
  add_time
)x
left join   (SELECT  sum(pv_num) as pv_num,add_time,bucket_id from  dw_proj.report_pv_tmp where glb_plf in ('pc','m') and glb_b='c' group by add_time,bucket_id ) a 
on x.add_time=a.add_time and x.bucket_id=a.bucket_id
left join   (SELECT sum(uv_num) as uv_num,add_time,bucket_id  from  dw_proj.report_uv_tmp where glb_plf in ('pc','m') and glb_b='c' group by add_time,bucket_id) b 
on x.add_time=b.add_time and x.bucket_id=b.bucket_id
left join  (SELECT sum(exposure_num) as exposure_num,add_time,bucket_id from  dw_proj.report_exp_tmp where (glb_b='c' and glb_plf='pc' and  mrlc='T_3') or 
(glb_b='c' and glb_plf='m' and mrlc='T_2') group by add_time,bucket_id) c 
on x.add_time=c.add_time and x.bucket_id=c.bucket_id
left join  (SELECT sum(click_num) as click_num,add_time,bucket_id from  dw_proj.report_click_tmp where (glb_b='c' and glb_plf='pc' and  mrlc='T_3') or 
(glb_b='c' and glb_plf='m' and mrlc='T_2') group by add_time,bucket_id) d
on x.add_time=d.add_time and x.bucket_id=d.bucket_id
left join  (SELECT sum(addcart_num) as addcart_num,add_time,bucket_id from  dw_proj.report_cart_tmp where (glb_plf='pc' and fmd='mr_T_3') or 
(glb_plf='m' and fmd='mr_T_2') group by add_time,bucket_id) e 
on x.add_time=e.add_time and x.bucket_id=e.bucket_id
left join  (SELECT sum(order_num) as order_num,add_time,bucket_id from   dw_proj.report_order_tmp where (glb_plf='pc' and fmd='mr_T_3') or 
(glb_plf='m' and fmd='mr_T_2') group by add_time,bucket_id) f
on x.add_time=f.add_time and x.bucket_id=f.bucket_id
left join  (SELECT sum(pay_num) as pay_num,add_time,bucket_id from dw_proj.report_purchase_tmp where (glb_plf='pc' and fmd='mr_T_3') or 
(glb_plf='m' and fmd='mr_T_2') group by add_time,bucket_id) g
on x.add_time=g.add_time and x.bucket_id=g.bucket_id
left join  (SELECT sum(pay_total) as pay_total,add_time,bucket_id from  dw_proj.report_pay_amount_tmp where (glb_plf='pc' and fmd='mr_T_3') or 
(glb_plf='m' and fmd='mr_T_2') group by add_time,bucket_id) h 
on x.add_time=h.add_time and x.bucket_id=h.bucket_id

union all
SELECT
  x.bind_id,
  x.plan_id,
  x.plan_name,
  x.service_id,
  x.service_name,
  x.bucket_id,
  x.bucket_name,
 '${ADD_TIME}' as report_date,
  NVL(b.uv_num,0),
  NVL(a.pv_num,0),
  NVL(c.exposure_num,0),
  NVL(d.click_num,0),
  NVL(d.click_num / c.exposure_num,0),
  NVL(e.addcart_num,0),
  NVL(e.addcart_num / d.click_num,0),
  NVL(f.order_num,0),
  NVL(f.order_num / e.addcart_num,0),
  NVL(g.pay_num,0),
  NVL(h.pay_total,0),
  NVL(g.pay_num / f.order_num,0),
  NVL(g.pay_num / c.exposure_num,0),
  '购物车页推荐位'
FROM
(
SELECT
  bind_id,
  plan_id,
  plan_name,
  bucket_id,
  bucket_name,
  service_id,
  service_name,
  add_time
FROM 
dw_proj.abtest_info_tmp 
GROUP BY
  bind_id,
  plan_id,
  plan_name,
  bucket_id,
  bucket_name,
  service_id,
  service_name,
  add_time
)x
left join   (SELECT  sum(pv_num) as pv_num,add_time,bucket_id from  dw_proj.report_pv_tmp where glb_plf ='pc' and glb_b='d' group by add_time,bucket_id ) a 
on x.add_time=a.add_time and x.bucket_id=a.bucket_id
left join   (SELECT sum(uv_num) as uv_num,add_time,bucket_id  from  dw_proj.report_uv_tmp where glb_plf ='pc' and glb_b='d' group by add_time,bucket_id) b 
on x.add_time=b.add_time and x.bucket_id=b.bucket_id
left join  (SELECT sum(exposure_num) as exposure_num,add_time,bucket_id from  dw_proj.report_exp_tmp where glb_b='d' and glb_plf='pc' and  mrlc='T_8' group by add_time,bucket_id) c 
on x.add_time=c.add_time and x.bucket_id=c.bucket_id
left join  (SELECT sum(click_num) as click_num,add_time,bucket_id from  dw_proj.report_click_tmp where glb_b='d' and glb_plf='pc' and  mrlc='T_8' group by add_time,bucket_id) d
on x.add_time=d.add_time and x.bucket_id=d.bucket_id
left join  (SELECT sum(addcart_num) as addcart_num,add_time,bucket_id from  dw_proj.report_cart_tmp where glb_plf='pc' and fmd='mr_T_8' group by add_time,bucket_id) e 
on x.add_time=e.add_time and x.bucket_id=e.bucket_id
left join  (SELECT sum(order_num) as order_num,add_time,bucket_id from   dw_proj.report_order_tmp where glb_plf='pc' and fmd='mr_T_8' group by add_time,bucket_id) f
on x.add_time=f.add_time and x.bucket_id=f.bucket_id
left join  (SELECT sum(pay_num) as pay_num,add_time,bucket_id from dw_proj.report_purchase_tmp where glb_plf='pc' and fmd='mr_T_8' group by add_time,bucket_id) g
on x.add_time=g.add_time and x.bucket_id=g.bucket_id
left join  (SELECT sum(pay_total) as pay_total,add_time,bucket_id from  dw_proj.report_pay_amount_tmp where glb_plf='pc' and fmd='mr_T_8' group by add_time,bucket_id) h 
on x.add_time=h.add_time and x.bucket_id=h.bucket_id
;



INSERT OVERWRITE TABLE  dw_proj.r_bts_report_exp
SELECT  
bind_id         
,plan_id         
,plan_name       
,service_id      
,service_name    
,bucket_id       
,bucket_name     
,report_date     
,uv_num          
,pv_num          
,exposure_num    
,click_num       
,click_rate      
,addcart_num     
,addcart_rate    
,order_num       
,order_rate      
,pay_num         
,pay_total       
,pay_rate        
,total_rate
,recoment_type      
FROM dw_proj.r_bts_report  WHERE add_time = '${ADD_TIME}'
;