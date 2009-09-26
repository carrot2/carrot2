#!/bin/sh
DCS_HOST=http://localhost:8080/dcs/rest
RESULT=clusters-from-document-source.xml

curl $DCS_HOST -# \
     -F "dcs.source=etools" \
     -F "query=test" \
     -o $RESULT
echo Results saved to $RESULT
