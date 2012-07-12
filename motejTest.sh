#!/bin/sh

LIBDIR=target/dependency/
CLASSPATH=`find $LIBDIR -name \*.jar -exec echo -n {}: ';'`:target/impav-1.0-SNAPSHOT.jar

java -cp $CLASSPATH edu.rpi.tw.impav.App $*
