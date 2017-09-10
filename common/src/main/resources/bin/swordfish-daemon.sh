#!/usr/bin/env bash

usage="Usage: swordfish-daemon.sh (start|stop) <command> "

# if no args specified, show usage
if [ $# -le 1 ]; then
  echo $usage
  exit 1
fi

startStop=$1
shift
command=$1
shift

echo "Begin $startStop $command......"

BIN_DIR=`dirname $0`
BIN_DIR=`cd "$BIN_DIR"; pwd`
SWORDFISH_HOME=$BIN_DIR/..

export JAVA_HOME=$JAVA_HOME
export HOSTNAME=`hostname`

export SWORDFISH_PID_DIR=/tmp/
export SWORDFISH_LOG_DIR=$SWORDFISH_HOME/logs
export SWORDFISH_CONF_DIR=$SWORDFISH_HOME/conf
export SWORDFISH_LIB_JARS=$SWORDFISH_HOME/lib/*
SWORDFISH_LIB_PATH=$SWORDFISH_HOME/lib/

export SWORDFISH_OPTS="-server -Xmx4g -Xms4g -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70"
export STOP_TIMEOUT=5

if [ ! -d "$SWORDFISH_LOG_DIR" ]; then
  mkdir $SWORDFISH_LOG_DIR
fi

log=$SWORDFISH_LOG_DIR/swordfish-$command-$HOSTNAME.out
pid=$SWORDFISH_PID_DIR/swordfish-$command.pid

cd $SWORDFISH_HOME

if [ "$command" = "web-server" ]; then
  LOG_FILE="-Dlogging.config=conf/webserver_logback.xml"
  CLASS=com.baifendian.swordfish.webserver.RestfulApiApplication
elif [ "$command" = "master-server" ]; then
  LOG_FILE="-Dlogback.configurationFile=conf/masterserver_logback.xml"
  CLASS=com.baifendian.swordfish.masterserver.MasterThriftServer
elif [ "$command" = "exec-server" ]; then
  LOG_FILE="-Dlogback.configurationFile=conf/execserver_logback.xml"
  CLASS=com.baifendian.swordfish.execserver.ExecThriftServer
elif [ "$command" = "spark-sql-server" ]; then
 if [ "$startStop" = "start" ]; then
   which spark-sql  || { echo "Command spark-sql is not found."; exit 1; }
   SPARK_SERVER_LIB=`ls ${SWORDFISH_LIB_PATH}/swordfish-spark-sql-server-*.jar|head -n 1`
   HIVE_JSON_LIB=`find / -name "hive-hcatalog-core.jar" ! -path "/tmp/*" 2>/dev/null|head -n 1`
   SWF_RPC_LIB=`ls ${SWORDFISH_LIB_PATH}/swordfish-rpc*.jar|head -n 1`
   SPARK_LIBS=${HIVE_JSON_LIB},${SWORDFISH_LIB_PATH}/${SPARK_SERVER_LIB},${SWORDFISH_LIB_PATH}/${SWF_RPC_LIB}
   execCmd=" --class com.baifendian.swordfish.server.sparksql.SparkThriftServer --master yarn-client --jars ${SPARK_LIBS}"
   echo ${execCmd}
   nohup spark-sql ${execCmd} > ${log} 2>&1 < /dev/null &
   echo $! > ${pid}
   exit 0
 fi
else
  echo "Error: No command named \`$command' was found."
  exit 1
fi

case $startStop in
  (start)
    [ -w "$SWORDFISH_PID_DIR" ] ||  mkdir -p "$SWORDFISH_PID_DIR"

    if [ -f $pid ]; then
      if kill -0 `cat $pid` > /dev/null 2>&1; then
        echo $command running as process `cat $pid`.  Stop it first.
        exit 1
      fi
    fi

    echo starting $command, logging to $log

    exec_command="$LOG_FILE $SWORDFISH_OPTS -classpath $SWORDFISH_CONF_DIR:$SWORDFISH_LIB_JARS:${SPARK_HOME}/conf/ $CLASS"

    echo "nohup $JAVA_HOME/bin/java $exec_command > $log 2>&1 < /dev/null &"
    nohup $JAVA_HOME/bin/java $exec_command > $log 2>&1 < /dev/null &
    echo $! > $pid
    ;;

  (stop)

      if [ -f $pid ]; then
        TARGET_PID=`cat $pid`
        if kill -0 $TARGET_PID > /dev/null 2>&1; then
          echo stopping $command
          kill $TARGET_PID
          sleep $STOP_TIMEOUT
          if kill -0 $TARGET_PID > /dev/null 2>&1; then
            echo "$command did not stop gracefully after $STOP_TIMEOUT seconds: killing with kill -9"
            kill -9 $TARGET_PID
          fi
        else
          echo no $command to stop
        fi
        rm -f $pid
      else
        echo no $command to stop
      fi
      ;;

  (*)
    echo $usage
    exit 1
    ;;

esac

echo "End $startStop $command."