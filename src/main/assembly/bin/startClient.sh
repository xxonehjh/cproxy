#!/bin/bash
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
CONF_DIR=$DEPLOY_DIR/conf
LIB_DIR=$DEPLOY_DIR/lib
LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`
cd bin
echo>nohup.out
nohup java -Dfile.encoding=UTF-8 -Xms16m -Xmx1024m -XX:MaxPermSize=64M -classpath $CONF_DIR:$LIB_JARS com.xxonehjh.cproxy.client.ClientMain 2>&1 &
sleep 1s
PIDS=`ps -ef | grep java | grep "ClientMain" |awk '{print $2}'`
if [ -z "$PIDS" ]; then
    echo "ERROR: start false!"
    exit 1
fi

echo "start success"
echo $PIDS
cat nohup.out
