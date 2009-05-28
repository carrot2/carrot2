#!/bin/sh
DCS_HOST=http://localhost:8080/dcs/rest
RESULT=clusters-from-local-file.xml

curl $DCS_HOST -# \
     -F "dcs.c2stream=@../shared/data-mining.xml" \
     -o $RESULT
echo Results saved to $RESULT
