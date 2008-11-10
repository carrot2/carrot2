#!/bin/bash

#
# Simple sanity check
#

if [ ! -f "env.sh" ]; then
	echo "Create 'env.sh' based on 'env.sh-template"
	exit 1
fi

. env.sh

#
# Loop between deploy/stress/undeploy
#
trap "{ echo; exit 1; }" SIGINT SIGTERM
while true
do
    # undeploy
    echo -n "U"
    rm -f $DEPLOY_WAR
    while `wget -O /dev/null -q $TEST_URI`
    do
        echo -n "."
        sleep 1
    done
    
    # deploy
    echo -n "D"
	
	# copy and rename works better under CygWin. We can't simply
	# copy because sometimes the WAR is picked up before it is fully
	# copied.
	cp $SOURCE_WAR $DEPLOY_WAR.TMP
    mv $DEPLOY_WAR.TMP $DEPLOY_WAR

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

    #
    # Issue 40 queries at random, then quit
    #
    cat queries | ./stress.rb -t 5 -m 5 -u $TEST_URI --queries 40 2> /dev/null
done


