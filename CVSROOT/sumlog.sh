#!/bin/sh

# This is the second-stage script for sumlog.awk.
# Its task is to monitor the timestamp on some file for some time,
# and if it does not change, mail it.

# The target e-mail to send the files to
  email="jedit-cvs@lists.sourceforge.net"
# Subject line
  subject=" Change log"
# Time to sleep between timestamp checks
  time=120

# Get arguments
  lstfile=$1
  msgfile=$2
  tmp=$3

  if [ -z "$lstfile" -o -z "$msgfile" ]; then
    echo "ERROR: $0 should never be invoked manually"
    exit 1
  fi

# Check if file exists
  [ ! -f "$lstfile" ] && exit 2

# Allright, first of all we should close all in and out file descriptors.
# If we won't do it, CVS will wait for our pipes to close, and due to
# sleep below this can be very long. Since we're spawned from CVS, we
# already have a number of file descriptors besides stdin/out/err opened,
# we'll just close all the file descriptors we can.
  exec 0</dev/null
  exec 1>/dev/null 2>&1 3>&1 4>&1 5>&1 6>&1 7>&1 8>&1 9>&1 10>&1

  timestamp1=
  timestamp2=`ls -l --full-time $lstfile`

  while [ "$timestamp1" != "$timestamp2" ]; do
    timestamp1="$timestamp2"
    sleep $time
    [ ! -f "$lstfile" ] && exit 2
    timestamp2=`ls -l --full-time $lstfile`
  done

# Now mail the files
  cat $msgfile $lstfile | mail -s "$subject" "$email"

# Now remove the files
  rm -f $lstfile $msgfile
  rmdir $tmp >/dev/null 2>&1
  exit 0
