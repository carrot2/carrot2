#!/bin/sh

#
# Add extra JVM options to DCS_OPTS
#
DCS_OPTS="$DCS_OPTS -Xms64m -Xmx256m"

java $DCS_OPTS -Ddcs.war=war/carrot2-dcs.war -jar invoker.jar -cpdir lib org.carrot2.dcs.DcsApp $@
