#!/bin/gawk -f
#
# sumlog.awk: Create and mail a summary CVS log.
# Copyright (C) 2000 Andrew Zabolotny <bit@eltech.ru>
#
# Permission is granted to anybody to use, modify, rent, sell,
# print (and possibly eat), whole or partially, the tail, head,
# any subset of sourcecode lines contained within this file.
#
# Unlike cvs/contrib/log_accum.pl this script tries hardly to avoid
# miscelaneous race conditions caused by the concurrent usage of CVS.
# Howver, there are still some theoretical gaps where a collision can
# occur due to the fact that GAWK is not C++.
#
# External tools used by this script:
#
#	gawk, sh, nohup, sleep, md5sum, ls, mkdir, rm, mail
#
# All should be installed and available along the $PATH
#

BEGIN {
  /* The name of the second stage script */
  script = ENVIRON ["CVSROOT"] "/CVSROOT/sumlog.sh";

  readnext = 1;
  while (1) {
    if (readnext && (getline <= 0))
      break;
    readnext = 1;
    if (match($0, "^Update"))
      directory = substr($0, 11);
    else if (match($0, "^Modified")) {
      while (getline > 0) {
        if (substr($0, 1, 1) != "\t")
          break;
        modfiles = modfiles $0 "\n";
      }
      readnext = 0;
    } else if (match($0, "^Added")) {
      while (getline > 0) {
        if (substr($0, 1, 1) != "\t")
          break;
        addfiles = addfiles $0 "\n";
      }
      readnext = 0;
    } else if (match($0, "^Removed")) {
      while (getline > 0) {
        if (substr($0, 1, 1) != "\t")
          break;
        remfiles = remfiles $0 "\n";
      }
      readnext = 0;
    } else if (match($0, "^Log Message"))
      break;
  }

  /* Eat the rest of the lines since this is the log message */
  while (getline > 0)
    message = message $0 "\n";

# Allright, now we'll store all the gathered information in
# a set of temporary files. Also we'll launch a special copy
# of ourselves which will mail the resulting files after a
# delay.

  tmp = "/tmp/jedit-cvs";
  user = ENVIRON ["USER"];

  /* Create a directory under tmp, if it does not exist */
  system("mkdir " tmp " >/dev/null 2>&1");
  system("chmod +w " tmp " >/dev/null 2>&1");

  /* Allright, now we'll get the checksum of the log message */
  "md5sum --string='" user \
    gensub("'", "'\\\\''", "g", gensub("\n", " ", "g", message)) "'" | getline;
  checksum = gensub(" .*", "", "", $0);

  msgfile = tmp "/" checksum ".message";
  lstfile = tmp "/" checksum ".files";

  /* If the message file does not exists, create it. */
  /* This also acts as a primitive semaphore. */
  if ((getline < msgfile) < 0)
  {
    /* Create the file with log message */
    printf "Summary of changes by " user " on " strftime() ":\n\n" message > msgfile
    /* Add the header to the file with changed file list */
    printf "----------\n" > lstfile
    close(msgfile); close(lstfile);

  # Ahem. Now goes the trick. We'll launch a shell script which will
  # track the timestamp of the "lstfile"; if the file is not modified
  # for more than 30 seconds, consider it finished and mail it.
  #
  # Another trick we use here is to create a shell which will run our
  # child in a nohup process to avoid CVS kill it upon exit.
    system("nohup sh " script " " lstfile " " msgfile " " tmp "&");
  }

  printf "Directory: " directory ":\n" >> lstfile
  if (modfiles != "")
    printf "  Modified:" modfiles >> lstfile
  if (addfiles != "")
    printf "  Added:" addfiles >> lstfile
  if (remfiles != "")
    printf "  Removed:" remfiles >> lstfile
}
