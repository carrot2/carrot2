#!/bin/sh

yarn npm audit --recursive --all --json | node "etc/audit-to-resolutions.js" "$@"
exit $?