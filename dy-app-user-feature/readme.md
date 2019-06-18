# zaful app 用户特征接口服务
## 线上部署检查
curl --header "Content-Type: application/json" --request POST --data '{"fb_adset_id":"23843238186800488","target":"algorithm"}' http://172.31.33.169:38194/fb-ad-user-feature
curl --header "Content-Type: application/json" --request POST --data '{"fb_adset_id":"23843238186800488","target":"algorithm"}' http://172.31.44.72:38194/fb-ad-user-feature
curl --header "Content-Type: application/json" --request POST --data '{"fb_adset_id":"23843238186800488","target":"algorithm"}' http://172.31.29.140:38194/fb-ad-user-feature
curl --header "Content-Type: application/json" --request POST --data '{"fb_adset_id":"23843238186800488","target":"algorithm"}' http://172.31.27.225:38194/fb-ad-user-feature
### 线上
curl --header "Content-Type: application/json" --request POST --data '{"fb_adset_id":"23843238186800488","target":"algorithm"}' http://172.31.33.169:38294/fb-ad-user-feature

## 用户分层特征
000：老用户，无回访 <br>
010：老用户，有回访 <br>
100：新用户，无回访 <br>
110：新用户，有回访 <br>
111：游客回访

## zaful 用户 Facebook 特征与广告特征接口文档
http://wiki.hqygou.com:8090/pages/viewpage.action?pageId=117637318

## zaful 用户 Facebook 特征与广告特征 flink 任务

|任务描述|任务执行命令|任务运行时间|是否集成到当前工程调度管理|对应flink工程|数据来源|
|---|---|---|---|---|---|
|zaful app 用户基本信息Facebook 广告特征|/usr/local/services/flink/flink-yarn/flink-1.5.0/bin/flink run -d -m yarn-cluster -yn 1 -yjm 1024 -ytm 1024 -s 1 -nm app-user-feature-es /usr/local/services/flink/app-user-feature-es-0.1.jar --job.hdfs.path /user/hive/warehouse/dw_zaful_recommend.db/zaful_app_abset_id_user_fb_cookieid_fb/add_time=20190612/ --index-name dy_app_zaful_user_feature|0:30am|是|http://gitlab.egomsl.com/BTS/flink-realtime-counter/tree/master/app-user-feature-es|大数据hdfs|
|zaful app 用户广告特征|/usr/local/services/flink/flink-yarn/flink-1.5.0/bin/flink run -d -m yarn-cluster -yn 1 -yjm 1024 -ytm 1024 /usr/local/services/flink/fb-ad-user-feature-es-0.1.jar --job.hdfs.path /user/hive/warehouse/dw_zaful_recommend.db/zaful_app_abset_id_user_fb_all_every_hour/add_time=2019032103|每小时全量|是|http://gitlab.egomsl.com/BTS/flink-realtime-counter/tree/master/fb-ad-user-feature-es|大数据hdfs|

## 用户分层数据 oozie 任务文档
http://wiki.hqygou.com:8090/pages/viewpage.action?pageId=136971327