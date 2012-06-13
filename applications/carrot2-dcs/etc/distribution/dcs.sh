#!/bin/sh

#
# Add extra JVM options here
#
OPTS="-Xms64m -Xmx256m"

java $OPTS -Djava.ext.dirs=lib -Ddcs.war=war/carrot2-dcs.war org.carrot2.dcs.DcsApp $@
