cube 自动提交任务：<br>
curl "http://localhost:8094/cube-build/task?cubeUrl=http://172.18.0.2:7070/kylin/api/cubes/BTS_CUBE_TEST_132/build2&username=ADMIN&password=KYLIN&taskName=btsReport&triggerCron=* 5 * * * ? "<br>
?cubeUrl=http://172.18.0.2:7070/kylin/api/cubes/BTS_CUBE_TEST_132/build2&username=ADMIN&password=KYLIN&taskName=btsReport&triggerCron=*%205%20*%20*%20*%20?<br>
curl -G "http://localhost:8094/cube-build/task" --data-urlencode "cubeUrl=http://172.18.0.2:7070/kylin/api/cubes/BTS_CUBE_TEST_132/build2&username=ADMIN&password=KYLIN&taskName=btsReport&triggerCron=* 5 * * * ? "
 