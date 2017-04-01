#!/bin/bash
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
CONF_DIR=$DEPLOY_DIR/conf


PIDS=`ps  --no-heading -C java -f --width 1000 | grep "$CONF_DIR" |awk '{print $2}'`
if [ -z "$PIDS" ]; then
    echo "ERROR: The service does not started!"
    exit 1
fi

echo -e "Stopping the service ...\c"
for PID in $PIDS ; do
	kill $PID > /dev/null 2>&1
done

COUNT=0
TOTAL=0
while [ $COUNT -lt 1 ]; do    
    echo -e ".\c"
    sleep 1
    COUNT=1
    let TOTAL+=1
    for PID in $PIDS ; do
		PID_EXIST=`ps --no-heading -p $PID`
		if [ -n "$PID_EXIST" ]; then
			COUNT=0
			break
		fi
		if [ $COUNT -gt 30 ]; then
			for PID in $PIDS ; do
				kill -9 $PID > /dev/null 2>&1
			done
			COUNT=0
			break
		fi
	done
done
echo "OK!"
echo "PID: $PIDS"
