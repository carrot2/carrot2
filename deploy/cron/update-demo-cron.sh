#!/usr/bin/ssh-agent /bin/bash

. /etc/bash.common.rc

ssh-add /home/dweiss/.ssh/sourceforge.pwdless.key

CVS_RSH=ssh
export CVS_RSH

# First, block access from the outside.
/srv/bin/carrot-down.sh

/home/dweiss/carrot2/deploy/cron/update-demo.sh

# make it available to the public
/srv/bin/carrot-up.sh
