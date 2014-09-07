#!/bin/bash

#JAVA_HOME="/usr/java/jdk1.6.0_33"

if [ "$JAVA_HOME" != "" ]; then
  #echo "run java in $JAVA_HOME"
  JAVA_HOME=$JAVA_HOME
fi

if [ "$JAVA_HOME" = "" ]; then
  echo "Error: JAVA_HOME is not set."
  exit 1
fi

JAVA=$JAVA_HOME/bin/java
BASE_HOME=$BASE_DIR
SERVER_NAME="cloudServiceCore"

#JMX port
export JMX_PORT=4001
export CLASSPATH=$BASE_DIR/conf:$(ls $BASE_DIR/lib/*.jar | tr '\n' :)

#JVM args
if test -z "$SUPERDIAMOND_PROFILE"; then
    export SUPERDIAMOND_PROFILE=test
fi

BASE_APP_ARGS="-Dsuperdiamond.host=172.16.79.21 -Dsuperdiamond.port=8283 -Dsuperdiamond.projcode=cloud-service"
BASE_APP_ARGS="$BASE_APP_ARGS -Dexcludes.appkey=HWQIjW2K,C6SakiHO,KtSNKxk3"

BASE_JVM_ARGS_DEVELOPMENT="-Xmx512m -Xms256m -server"
BASE_JVM_ARGS_TEST="-Xmx512m -Xms256m -server"
BASE_JVM_ARGS_BUILD="-Xmx512m -Xms256m -server"
BASE_JVM_ARGS_PRODUCTION="-Xmx512m -Xms256m -server"

if [ $SUPERDIAMOND_PROFILE = "test" ];then
    APP_JVM_ARGS="$BASE_JVM_ARGS_TEST -cp $CLASSPATH"
elif [ $SUPERDIAMOND_PROFILE = "build" ];then
    APP_JVM_ARGS="$BASE_JVM_ARGS_BUILD -cp $CLASSPATH"
elif [ $SUPERDIAMOND_PROFILE = "production" ];then
    APP_JVM_ARGS="$BASE_JVM_ARGS_PRODUCTION -cp $CLASSPATH"
else
    APP_JVM_ARGS="$BASE_JVM_ARGS_DEVELOPMENT -cp $CLASSPATH"
fi