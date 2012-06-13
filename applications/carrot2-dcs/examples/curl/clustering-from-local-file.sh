#!/bin/sh
DCS_HOST=http://localhost:8080/dcs/rest

# HTTP POST multipart
curl $DCS_HOST -# \
     -F "dcs.c2stream=@../shared/data-mining.xml" \
     -o clusters-from-local-file-multipart.xml

# HTTP POST www-form-urlencoded (less efficient)
curl $DCS_HOST -# \
     --data-urlencode "dcs.c2stream@../shared/data-mining.xml" \
     -o clusters-from-local-file-formencoded.xml

echo Results saved to clusters-from-local-file-*
