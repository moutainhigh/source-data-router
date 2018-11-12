dy_bts_search_rec_report zaful 搜索算法报表<br>
bin/kafka-console-consumer.sh --bootstrap-server 172.31.22.179:9092 --topic dy_bts_search_rec_report <br>
bin/kafka-topics.sh --zookeeper 172.31.61.192:2181,172.31.59.31:2181,172.31.62.153:2181,172.31.36.227:2181,172.31.40.73:2181 --create --topic dy_bts_search_rec_report --partitions 1 --replication-factor 1<br>
curl http://localhost:38195/report?configPath=/usr/local/services/bts-common-report-1.0-SNAPSHOT/bin/bts_zaful_search_rec_report.json<br>
## 订单处理逻辑设计
1、消费 binlog，获取订单表、订单商品表数据，以站点名 + 订单 id 为 key 放入 redis，数据结构为 list，过滤掉订单状态大于 8 的订单，订单商品表 log 事件到达时，<br>
   1、订单状态为 0 ，计算下单商品数、GMV；<br>
   2、订单状态为1、8 时，计算销售额（分两个字段计算，1：加购数量*单价 goods_price*goods_number 2：计算 goods_pay_amount 字段 ）<br>
   3、在处理商品时，标记已处理的商品 <br>
   4、排除订单商品的更新事件 <br>
2、每次有事件到达，都循环一次缓存 list 根据 userid 和 sku 去 redis 缓存中查找，前缀通过接口动态新增，查询到埋点数据，则生成订单数据，将订单数据放入埋点中，发送到订单 topic；
   每次发送完成后，删除该 key，更新状态，重新设置 list，订单 topic 名字为：dy_log_cart_order_info; <br>
3、订单计算 job，配置项，zaful 统一指标，