#!/bin/bash
#
# Copyright 2013-2014 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


if [ -z "$BASE_DIR" ] ; then
  PRG="$0"

  # need this for relative symlinks
  while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
      PRG="$link"
    else
      PRG="`dirname "$PRG"`/$link"
    fi
  done
  BASE_DIR=`dirname "$PRG"`/..

  # make it fully qualified
  BASE_DIR=`cd "$BASE_DIR" && pwd`
  #echo "collect master is at $BASE_DIR"
fi

source $BASE_DIR/bin/env.sh

AS_USER=`whoami`
LOG_DIR="$BASE_DIR/logs"
LOG_FILE="$LOG_DIR/server.log"
PID_DIR="$BASE_DIR/logs"
PID_FILE="$PID_DIR/.run.pid"
HOST_NAME=`hostname`

function running(){
	if [ -f "$PID_FILE" ]; then
		pid=$(cat "$PID_FILE")
		process=`ps aux | grep " $pid " | grep -v grep`;
		if [ "$process" == "" ]; then
	    	return 1;
		else
			return 0;
		fi
	else
		return 1
	fi	
}

function start_server() {
	if running; then
		echo "$SERVER_NAME is running."
		exit 1
	fi

    mkdir -p $PID_DIR
    mkdir -p $LOG_DIR
    touch $LOG_FILE
    chown -R $AS_USER $PID_DIR
    chown -R $AS_USER $LOG_DIR
    
    echo "$JAVA $APP_JVM_ARGS -DBASE_HOME=$BASE_HOME -DSERVER_NAME=$SERVER_NAME-$HOST_NAME -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false \
   	  -Dcom.sun.management.jmxremote.port=$JMX_PORT $BASE_APP_ARGS com.iflytek.edu.cloud.frame.jetty.JettyServer"
    sleep 1
    nohup $JAVA $APP_JVM_ARGS -DBASE_HOME=$BASE_HOME -DSERVER_NAME=$SERVER_NAME-$HOST_NAME -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false \
   	  -Dcom.sun.management.jmxremote.port=$JMX_PORT $BASE_APP_ARGS com.iflytek.edu.cloud.frame.jetty.JettyServer >>$LOG_FILE 2>&1 &	
    echo $! > $PID_FILE
    
    chmod 755 $PID_FILE
  	sleep 1;
    tail -f $LOG_FILE
}

function stop_server() {
	if ! running; then
		echo "$SERVER_NAME is not running."
		exit 1
	fi
	count=0
	pid=$(cat $PID_FILE)
	echo "Stopping $SERVER_NAME"
    kill -15 $pid &
    rm $PID_FILE
    tail -f $LOG_FILE
}

function status(){
    if running; then
       echo "$SERVER_NAME is running.";
       exit 0;
    else
       echo "$SERVER_NAME was stopped.";
       exit 1;
    fi
}
 
function help() {
    echo "Usage: server.sh {start|status|stop}" >&2
    echo "       start:             start the $SERVER_NAME server"
    echo "       stop:              stop the $SERVER_NAME server"
    echo "       status:            get $SERVER_NAME current status,running or stopped."
}

command=$1
shift 1
case $command in
    start)
        start_server $@;
        ;;    
    stop)
        stop_server $@;
        ;;
    status)
    	status $@;
        ;; 
    help)
        help;
        ;;
    *)
        help;
        exit 1;
        ;;
esac