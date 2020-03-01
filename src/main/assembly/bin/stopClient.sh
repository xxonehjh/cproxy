#!/bin/bash
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
CONF_DIR=$DEPLOY_DIR/conf

PIDS=`ps -ef | grep java | grep "ClientMain" |awk '{print $2}'`
if [ -z "$PIDS" ]; then
    echo "ERROR: The server does not started!"
    exit 1
fi

echo -e "Stopping the server  ...\c"
for PID in $PIDS ; do
    kill $PID > /dev/null 2>&1
done

echo "stop success!"
echo "PID: $PIDS"