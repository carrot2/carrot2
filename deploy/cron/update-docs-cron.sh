#!/bin/bash

. /etc/bash.common.rc

PATH=$PATH:/usr/java/xep
export PATH

/home/dweiss/carrot2/deploy/cron/update-docs.sh
