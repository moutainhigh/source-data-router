#!/bin/bash
if [ "$1" == "test" ] || [ "$1" == "prod" ];
then
    active=$1
else
    echo "params is invalid"
    exit 2
fi
rm -f tpid
export JAVA_OPTS='-server -Xmx1g -Xms1g -Xmn1g  -XX:MetaspaceSize=1g -XX:MaxMetaspaceSize=1g -Xss1024k -XX:+DisableExplicitGC  -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -Duser.timezone=GMT+8 -XX:+PrintClassHistogram -XX:+PrintGCDetails -XX:+PrintGCTimeStamps  -XX:+PrintGCDateStamps   -XX:+PrintHeapAtGC  -XX:+HeapDumpOnOutOfMemoryError  -XX:HeapDumpPath=/usr/local/services/dy-cube-build-service/logs/cubebuildservice.dump   -Xloggc:/usr/local/services/dy-cube-build-service/logs/cubebuildservice.log -verbose:gc -Xmixed -XX:-CITime -Djava.rmi.server.hostname=127.0.0.1'

logFile=exec/stdout.log
nohup /usr/local/services/jdk/bin/java $JAVA_OPTS -jar /usr/local/services/dy-cube-build-service/app/cubebuildservice-1.0.0.jar --spring.config.location=/usr/local/services/dy-cube-build-service/conf/application-${active}.properties --logging.config=/usr/local/services/dy-cube-build-service/conf/logback-spring.xml  --spring.profiles.active=${active}  >$logFile 2>&1  &
