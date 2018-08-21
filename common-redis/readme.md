配置文件加入如下：<br>
集群模式配置<br>

```
spring.redis.cluster.nodes[0]=192.168.0.192:6380
spring.redis.cluster.nodes[1]=192.168.0.192:6381
spring.redis.cluster.nodes[2]=192.168.0.192:6382
spring.redis.cluster.nodes[3]=192.168.0.192:6383
spring.redis.cluster.nodes[4]=192.168.0.192:6384
spring.redis.cluster.nodes[5]=192.168.0.192:6385
```


哨兵模式配置<br>
```
spring.redis.sentinel.master=sentinel-172.31.42.160-6383
spring.redis.sentinel.nodes=172.31.34.55:6384,172.31.42.160:6384,172.31.54.133:6383
spring.redis.password=6e1KWyC29w
spring.redis.timeout=86400
```


