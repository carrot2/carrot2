#!/bin/sh

if [ -z "$DCS_OPTS" ]; then
  DCS_OPTS="-Xms64m -Xmx768m"
fi

java $DCS_OPTS -Ddcs.war=web/carrot2-dcs-service-@version@.war -jar lib/carrot2-dcs-launcher-@version@.jar $@
