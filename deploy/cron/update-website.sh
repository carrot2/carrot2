#!/bin/bash

#
# This is a Bash script for Cron that updates the official Carrot2 website
# at http://www.cs.put.poznan.pl/dweiss/carrot
#

cd /home/dweiss/carrot2/deploy

JAVA_HOME=/usr/java/j2sdk1.4.1_02
JAVACMD=${JAVA_HOME}/bin/java
ANT_HOME=/usr/java/ant
PATH=${PATH}:/home/dweiss/xep

export PATH
export JAVA_HOME
export JAVACMD
export ANT_HOME

if ant -f build.website.xml -listener org.apache.tools.ant.XmlLogger \
       -DMailLogger.properties.file=cron/build.website.logger \
       -logger org.apache.tools.ant.listener.MailLogger \
       publish; then
    echo "Website update ok."
else
    echo "Website update failed."
fi

