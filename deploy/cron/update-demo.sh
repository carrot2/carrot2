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

# update the code
for counter in `seq 1 10`; do
    if ant -f build.demo.xml cvsupdate; \
    then
        echo "Code CVS Update ok."
        break
    else
        echo "Code CVS Update failed. Sleeping 60 secs."
        sleep 60
    fi
    if (($counter == 10)); then
        echo "Problems updating CVS of the demo..." | mail -s "Demo CVS update problem." dawid.weiss@cs.put.poznan.pl  
        exit
    fi    
done
# update the tests (if possible)
for counter in `seq 1 10`; do
    if ant -f build.tests.xml cvsupdate; \
    then
        echo "Tests CVS Update ok."
        break
    else
        echo "Tests CVS Update failed. Sleeping 60 secs."
        sleep 60
    fi
    if (($counter == 10)); then
        echo "Problems updating CVS of the tests..." | mail -s "Tests CVS update problem." dawid.weiss@cs.put.poznan.pl
        # run tests anyway...
    fi    
done


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
    # run tomcat in the background, wait and test it immediately
    (ant -f build.demo.xml start.tomcat.success)&
    sleep 360
    if ant -f build.tests.xml build
    then
        echo "Tests finished ok."
    else
        echo "Tests failed. mail info to admin"
        echo "Some of the tests failed..." | mail -s "Tests failed." dawid.weiss@cs.put.poznan.pl
    fi
else
    # stop tomcat first, restart it in 'failure' mode.
    ant -f build.demo.xml stop.tomcat
    ant -f build.demo.xml clean.webapps
    ant -f build.demo.xml copy.logs
    ant -f build.demo.xml start.tomcat.failure
fi

