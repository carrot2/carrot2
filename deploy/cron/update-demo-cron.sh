#!/usr/bin/ssh-agent /bin/bash

ssh-add /home/dweiss/.ssh/sourceforge.pwdless.key

CVS_RSH=ssh
export CVS_RSH

/home/dweiss/carrot2/deploy/cron/update-demo.sh

