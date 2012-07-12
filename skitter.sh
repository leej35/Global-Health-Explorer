#!/bin/sh

LIBDIR=target/dependency/
CLASSPATH=`find $LIBDIR -name \*.jar -exec echo -n {}: ';'`:target/skitter-1.0-SNAPSHOT.jar

#PROFILE_ARGS='-agentpath:/home/jim/.netbeans/6.9/lib/deployed/jdk15/linux-amd64/libprofilerinterface.so=/home/jim/.netbeans/6.9/lib,5140'

java -Xmx512m $PROFILE_ARGS -cp $CLASSPATH edu.rpi.tw.impav.App $*
