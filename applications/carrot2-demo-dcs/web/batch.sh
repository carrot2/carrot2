#!/bin/bash

DEPLIB=WEB-INF/lib
OPTS="-Xms128m -Xmx384m -XX:NewRatio=1"

java $OPTS -jar ${DEPLIB}/carrot2-launcher.jar -cpdir ${DEPLIB} org.carrot2.dcs.cli.BatchApp $@
