#!/bin/bash
. /etc/profile
PRJ_NAME='dy-bury-log-collect-service'
if [ ! -d "/usr/local/services/$PRJ_NAME/logs" ];then
      mkdir  "/usr/local/services/$PRJ_NAME/logs"
fi
if [ "$1" == "test" ] || [ "$1" == "prod" ];
then
    active=$1
else
    echo "params is invalid"
    exit 2
fi
rm -f tpid
export JAVA_OPTS="-server -Xmx2g -Xms2g -Xmn1g  -XX:MetaspaceSize=1g -XX:MaxMetaspaceSize=1g -Xss1024k -XX:+DisableExplicitGC  -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -Duser.timezone=GMT+8 -XX:+PrintClassHistogram -XX:+PrintGCDetails -XX:+PrintGCTimeStamps  -XX:+PrintGCDateStamps   -XX:+PrintHeapAtGC  -XX:+HeapDumpOnOutOfMemoryError  -XX:HeapDumpPath=/usr/local/services/$PRJ_NAME/logs/msys/msys_heap.dump   -Xloggc:/usr/local/services/$PRJ_NAME/logs/msys_gc.log -verbose:gc -Xmixed -XX:-CITime -Djava.rmi.server.hostname=127.0.0.1 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=18092 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"

nohup $JAVA_HOME/bin/java  $JAVA_OPTS -jar /usr/local/services/$PRJ_NAME/app/$PRJ_NAME-1.0-SNAPSHOT.jar --spring.config.location=/usr/local/services/$PRJ_NAME/conf/application-${active}.properties,/usr/local/services/$PRJ_NAME/conf/service_config.properties --logging.config=/usr/local/services/$PRJ_NAME/conf/logback-spring.xml  --spring.profiles.active=${active}  >nohup 2>&1  &
