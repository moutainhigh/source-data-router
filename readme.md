###1、hbase 建表\操作<br>
	create 'h_source_bts','plan','plan_version','plan_result'<br>
	create 'h_source_zaful' ,'order','order_goods' <br>
	create 'h_source_bts_recommend','run_result'<br> 
	create 'dy_cookie_userid_rel','cookie_userid'<br> 
	create 'dy_gb_cookie_userid_rel','cookie_userid'<br> 
	scan 'dy_cookie_userid_rel', { COLUMNS => 'cookie_userid:user_id', LIMIT => 10, FILTER => "ValueFilter( =, 'binaryprefix:14838230')" }<br>
	
###2、package<br>
    clean install -Dmaven.test.skip=true -e -Ptest<br>
    clean install -Dmaven.test.skip=true -e -Pprod<br>
    
###3、mysql<br>
  nohup ./maxwell --user='maxwell' --password='Qwert@123456' --client_id=3 --replica_server_id=3 --host='172.31.24.20' --port=3306 --include_dbs=bts_kylin_report_test   --producer=kafka --kafka.bootstrap.servers=172.18.0.2:9092 --kafka_topic=bts_mysql_binlog_kylin > bts.log 2>&1 & <br>
  nohup ./maxwell --user='maxwell' --password='Qwert@123456' --client_id=4 --replica_server_id=4 --host='172.31.24.20' --port=3306 --include_dbs=bts_zaful_order   --producer=kafka --kafka.bootstrap.servers=172.18.0.2:9092 --kafka_topic=zaful_mysql_binlog_kylin > zaful.log 2>&1 & <br>
###4、docker<br>
    docker exec -it cluster-master /bin/bash <br>
    docker cp /sou cluster-master:/usr/local/services <br>
###5、kafka<br>
    bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic bts_mysql_binlog_kylin --from-beginning <br>
    bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic bury_point_log_default --from-beginning <br>
    bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic kylin_bts_report --from-beginning <br>
    bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic kylin_bts_zaful_report --from-beginning <br>
    bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic kylin_bts_zaful_log --from-beginning <br>
    bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic zaful_mysql_binlog_kylin --from-beginning <br>
    bin/kafka-console-producer.sh --broker-list localhost:9092 --topic bury_point_log_default <br>
    bin/kafka-topics.sh --list --zookeeper localhost:2181 <br>
    bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic kylin_bts_zaful_report --partitions 1 --replication-factor 1 <br>
    bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic kylin_bts_zaful_log --partitions 1 --replication-factor 1 <br>
    $KAFKA_HOME/bin/kafka-consumer-groups.sh --new-consumer --bootstrap-server 172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092 --group mysql-bin-log-zaful --describe<br>
    $KAFKA_HOME/bin/kafka-consumer-groups.sh --new-consumer --bootstrap-server 172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092 --group bts-log-dimensions_zafu_recommend --describe<br>
    $KAFKA_HOME/bin/kafka-console-consumer.sh --bootstrap-server 172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092 --topic dy_bts_report_recommend_zaful --from-beginning<br>
    $KAFKA_HOME/bin/kafka-console-consumer.sh --bootstrap-server 172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092 --topic dy_bts_gb_gd_rec_report --from-beginning<br>
###6、cube <br>
curl -X PUT --user ADMIN:KYLIN -H "Content-Type: application/json;charset=utf-8" -d '{ "sourceOffsetStart": 0, "sourceOffsetEnd": 9223372036854775807, "buildType": "BUILD"}' http://localhost:7070/kylin/api/cubes/pv_uv_test/build2 <br>
###7、sql
select minute_start,BTS_PLAN_ID, count(*),count(distinct GLB_OD),GLB_T from BTS_LOG_1 group by minute_start,BTS_PLAN_ID,GLB_T order by minute_start <br>


### docker server info:<br>
Ai.globalegrow.com <br>
ssh 172.18.0.2/3/4 

<br>
scp /var/ftp/pub/log-data-dimensions-1.0-SNAPSHOT root@172.18.0.2:/usr/local/services/log-data-dimensions-1.0-SNAPSHOT/lib <br>

AIBTSdatanode01     公网：52.205.9.42                   内网：172.31.59.31 <br>
AIBTSdatanode02     公网：52.3.181.190                 内网：172.31.62.153  <br>
AIBTSdatanode03     公网：52.45.136.158               内网：172.31.36.227 <br>
AIBTSdatanode04     公网：52.73.124.151               内网：172.31.40.73 <br>
AIBTSmaster01         公网：52.20.83.154                 内网：172.31.61.192 <br>
AIBTSmaster02         公网：54.236.170.61               内网：172.31.62.7 <br>

172.31.59.31  bts-datanode01 bts-datanode01.cdh.gw-internel.com <br>
172.31.62.153 bts-datanode02 bts-datanode02.cdh.gw-internel.com <br>
172.31.36.227 bts-datanode03 bts-datanode03.cdh.gw-internel.com <br>
172.31.40.73  bts-datanode04 bts-datanode04.cdh.gw-internel.com <br>
172.31.61.192 bts-master     bts-master.cdh.gw-internel.com <br>
172.31.62.7   bts-masterbak  bts-masterbak.cdh.gw-internel.com <br>

### kafka zk:
172.31.51.168:2181,172.31.37.64:2181,172.31.27.205:2181 <br>
### kafka bootstrap:dy_bts_report_recommend_zaful
172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092 <br>

bts mysql db test: root Hqyg@123456 <br>

KYLIN_PROD: ADMIN:Dy@ai2018<br>
getengq
###redis
cd /opt/cachecloud/bin/ <br>info<br>
./redis-cli -p 6383 -h 172.31.42.160<br>
172.31.42.160:6383> auth 6e1KWyC29w<br>

### GB db
db_url=jdbc:mysql://169.60.204.58:3306/gb_order?tinyInt1isBit=false<br>
db_user=etl_big<br>
db_password=mPe@@1rYGUbV<br>
