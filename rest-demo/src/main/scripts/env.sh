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