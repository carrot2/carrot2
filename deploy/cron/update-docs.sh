#!/bin/bash

#
# This is a Bash script for Cron that updates the official Carrot2
# documentation at the website
# http://www.cs.put.poznan.pl/dweiss/carrot/manual
#

cd /home/dweiss/carrot2/deploy

JAVA_HOME=/usr/java/j2sdk
JAVACMD=${JAVA_HOME}/bin/java
ANT_HOME=/usr/java/ant
PATH=${PATH}:/home/dweiss/xep

export PATH
export JAVA_HOME
export JAVACMD
export ANT_HOME

for counter in `seq 1 40`; do
    if ant -f build.docs.xml cvsupdate; then
        echo "Docs update ok."
        break;
    else
        echo "Docs update failed. Sleeping 60 seconds."
        sleep 60
    fi
    if (($counter == 40)); then
        echo "Problems updating CVS of the docs..." | mail -s "Docs CVS update problem." dawid.weiss@cs.put.poznan.pl  
        exit
    fi
done

ant -f build.docs.xml \
       -DMailLogger.properties.file=cron/build.website.logger \
       -logger org.apache.tools.ant.listener.MailLogger \
       publish

