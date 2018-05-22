#!/bin/sh
DCS_HOST=http://localhost:8080/dcs/rest
RESULT=clusters-saved-to-json.json

curl $DCS_HOST -# \
     -F "dcs.source=web" \
     -F "query=json" \
     -F "dcs.output.format=JSON" \
     -o $RESULT
echo Results saved to $RESULT
