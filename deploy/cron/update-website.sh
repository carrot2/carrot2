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

for counter in `seq 1 40`; do
    if ant -f build.website.xml cvsupdate; then
        echo "Website update ok."
	echo "Website updated: " `date` >>/home/dweiss/carrot2/logs-cron/website-successes.log
        break;
    else
        echo "Website update failed. Sleeping."
        sleep 60
    fi
    if (($counter == 40)); then
        echo "Problems updating CVS of the website..." | mail -s "Website CVS update problem." dawid.weiss@cs.put.poznan.pl  
        exit
    fi
done

ant -f build.website.xml \
       -DMailLogger.properties.file=cron/build.website.logger \
       -logger org.apache.tools.ant.listener.MailLogger \
       publish

