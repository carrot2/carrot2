#!/bin/bash

# Source webapp archive
SOURCE_WAR=/home/dweiss/carrot2/carrot2.sf.proto-3.0/tmp/webapp/stable.war

# Where to copy it for automatic deployment
# TODO: we could use tomcat's deploy script here, would take shorter to refresh.
DEPLOY_WAR=/home/dweiss/Applications/apache-tomcat-6.0.18/webapps/stable.war

# Test URI to check if the application is available.
TEST_URI=http://localhost:8080/stable/

#
# Loop deploy/short stress/undeploy
#
trap "{ echo; exit 1; }" SIGINT SIGTERM
while true
do
    # undeploy
    echo -n "U"
    rm $DEPLOY_WAR
    while `wget -O /dev/null -q $TEST_URI`
    do
        echo -n "."
        sleep 1
    done
    
    # deploy
    echo -n "D"
    cp $SOURCE_WAR $DEPLOY_WAR
    while ! `wget -O /dev/null -q $TEST_URI`
    do
        echo -n "."
        sleep 1
    done

    #
    # Put some stress on the application. Change as needed. By default
    # 40 queries are executed at random from the 'queries' file.
    #
    echo -n "S"
    cat queries | ./stress.rb -t 5 -m 5 -u $TEST_URI --queries 40 2 > /dev/null    
done


