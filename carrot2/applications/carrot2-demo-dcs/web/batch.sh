#!/bin/bash

DEPLIB=WEB-INF/lib
OPTS="-Xms128m -Xmx384m -XX:NewRatio=1"

java $OPTS -jar ${DEPLIB}/carrot2-launcher.jar -cp WEB-INF/classes -cpdir ${DEPLIB} org.carrot2.dcs.cli.BatchApp $@
