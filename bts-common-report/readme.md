dy_bts_search_rec_report zaful 搜索算法报表<br>
bin/kafka-console-consumer.sh --bootstrap-server 172.31.22.179:9092 --topic dy_bts_search_rec_report <br>
bin/kafka-topics.sh --zookeeper 172.31.61.192:2181,172.31.59.31:2181,172.31.62.153:2181,172.31.36.227:2181,172.31.40.73:2181 --create --topic dy_bts_search_rec_report --partitions 1 --replication-factor 1<br>
curl http://localhost:38195/report?configPath=/usr/local/services/bts-common-report-1.0-SNAPSHOT/bin/bts_zaful_search_rec_report.json<br>