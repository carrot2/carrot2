#!/bin/bash

#
# This is a Bash script for Cron that updates, compiles and
# runs a carrot2 demo
#


cd /home/dweiss/carrot2/deploy

JAVACMD=${JAVA_HOME}/bin/java
export JAVACMD

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

# stop tomcat.
ant -f build.demo.xml stop.tomcat

# zip tomcat logs.
mkdir -p /home/dweiss/carrot2/logs-tomcat
zip -r /home/dweiss/carrot2/logs-tomcat/tomcat-logs-`date +%Y-%m-%d_%H-%M` /home/dweiss/carrot2/runtime/logs/*

ant -f build.demo.xml clean.webapps
ant -f build.demo.xml copy.logs


if ant -Dno.cvsupdate=true -f build.demo.xml \
       -listener org.apache.tools.ant.XmlLogger \
       -DMailLogger.properties.file=cron/build.demo.logger \
       -logger org.apache.tools.ant.listener.MailLogger \
       build; \
then
    # stop tomcat first, restart it in 'success' mode.
    ant -f build.demo.xml copy.webapps

    # copy webapps to nightly binaries folder.
    rm -f /srv/www/vhosts/carrot/static/download/nightly/*.war
    rm -f /srv/www/vhosts/carrot/static/download/nightly/*.zip
    cp /home/dweiss/carrot2/runtime/context-webapps/*.war /srv/www/vhosts/carrot/static/download/nightly/
    zip /srv/www/vhosts/carrot/static/download/nightly/shared-libraries.zip /home/dweiss/carrot2/runtime/shared/lib/*.jar

    # override webapps if needed.
    cp -f /home/dweiss/carrot2/override-modules/*.war /home/dweiss/carrot2/runtime/context-webapps/

    if ant -f build.demo.xml tests
    then
        echo "Unit tests finished ok."
    else
        echo "Unit tests failed. mail info to admin"
        echo "Unit tests failed." | mail -s "Unit tests failed." dawid.weiss@cs.put.poznan.pl
    fi

    # run tomcat in the background, wait and test it after a couple of minutes
    (ant -f build.demo.xml start.tomcat.success)&

	sleep 10
    ant -f build.demo.xml waitforserver
    if ant -f build.tests.xml build
    then
        echo "External tests finished ok."
    else
        echo "Tests failed. mail info to admin"
        echo "Some of the tests failed..." | mail -s "Tests failed." dawid.weiss@cs.put.poznan.pl
    fi
else
    # stop tomcat first, restart it in 'failure' mode.
    ant -f build.demo.xml start.tomcat.failure
fi

