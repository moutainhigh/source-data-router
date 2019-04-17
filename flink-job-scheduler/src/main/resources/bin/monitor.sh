#!/usr/bin/env bash
ps -ef | grep /usr/local/services/flink/flink-job-scheduler-1.0-SNAPSHOT/ |grep -v grep
if [ $? -ne 0 ]
then
echo "start process....."
 exec /usr/local/services/flink/flink-job-scheduler-1.0-SNAPSHOT/bin/start.sh
else
echo "runing....."
fi
#####