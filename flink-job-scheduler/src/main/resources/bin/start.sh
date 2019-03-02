#!/bin/bash
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
CONF_DIR=$DEPLOY_DIR/conf

SERVER_NAME="mqcustomer"
SERVER_PORT=""

if [[ -z "$SERVER_NAME" ]]; then
    SERVER_NAME=`hostname`
fi

PIDS=`ps -ef | grep java | grep "$DEPLOY_DIR" |awk '{print $2}'`
if [[ -n "$PIDS" ]]; then
    echo "ERROR: The $SERVER_NAME already started!"
    echo "PID: $PIDS"
    exit 1
fi

if [[ -n "$SERVER_PORT" ]]; then
    SERVER_PORT_COUNT=`netstat -tln | grep $SERVER_PORT | wc -l`
    if [[ $SERVER_PORT_COUNT -gt 0 ]]; then
        echo "ERROR: The $SERVER_NAME port $SERVER_PORT already used!"
        exit 1
    fi
fi

JAVA_DEBUG_OPTS=""
if [[ "$1" = "debug" ]]; then
    JAVA_DEBUG_OPTS=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n "
fi
# -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Djava.rmi.server.hostname=35.153.241.61
JAVA_JMX_OPTS=""
if [[ "$1" = "jmx" ]]; then
    JAVA_JMX_OPTS=" -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false "
fi

LIB_DIR=$DEPLOY_DIR/lib

LIB_JARS="$CONF_DIR"
for JAR in `ls $LIB_DIR/*.jar`
do
    LIB_JARS="$LIB_JARS:$JAR"
done

STDOUT_FILE=bin/stdout.log
#-Xmx256m -Xmx2g -Xmx2g
JAVA_OPTS="-server -XX:OnOutOfMemoryError=bin/kill_ppid"
MAIN_CLASS=com.globalegrow.ServiceStart

CLASSPATH="$CONF_DIR:$DEPLOY_DIR/templates:$DEPLOY_DIR/static"
echo -e "Starting the $SERVER_NAME ...\c"
nohup java $JAVA_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS -cp $LIB_JARS $MAIN_CLASS > $STDOUT_FILE 2>&1 &

echo "OK!"
PIDS=`ps -f | grep java | grep "$DEPLOY_DIR" | awk '{print $2}'`
echo "PID: $PIDS"
echo "STDOUT: $STDOUT_FILE"