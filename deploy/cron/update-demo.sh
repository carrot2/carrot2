#!/bin/bash

#
# This is a Bash script for Cron that updates, compiles and
# runs a carrot2 demo
# at http://ophelia.cs.put.poznan.pl:2001
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

if ant -Dno.cvsupdate=true -f build.demo.xml \
       -listener org.apache.tools.ant.XmlLogger \
       -DMailLogger.properties.file=cron/build.demo.logger \
       -logger org.apache.tools.ant.listener.MailLogger \
       build; \
then
    # stop tomcat first, restart it in 'success' mode.
    ant -f build.demo.xml stop.tomcat
    ant -f build.demo.xml clean.webapps
    ant -f build.demo.xml copy.logs
    ant -f build.demo.xml copy.webapps
    ant -f build.demo.xml start.tomcat.success
else
    # stop tomcat first, restart it in 'failure' mode.
    ant -f build.demo.xml stop.tomcat
    ant -f build.demo.xml clean.webapps
    ant -f build.demo.xml copy.logs
    ant -f build.demo.xml start.tomcat.failure
fi

