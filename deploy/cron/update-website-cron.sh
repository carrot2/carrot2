#!/bin/bash

. /etc/bash.common.rc

ssh-add /home/dweiss/.ssh/sourceforge.pwdless.key

CVS_RSH=ssh
export CVS_RSH


/home/dweiss/carrot2/deploy/cron/update-website.sh
