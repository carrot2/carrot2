#!/bin/sh
DCS_HOST=http://localhost:8080/dcs/rest

# With HTTP POST
curl $DCS_HOST -# \
     -F "dcs.source=web" \
     -F "query=test" \
     -o clusters-from-document-source-post.xml

echo Results saved to clusters-from-document-source-post.xml

# With HTTP GET
curl "${DCS_HOST}?dcs.source=web&query=test" \
     -o clusters-from-document-source-get.xml

echo Results saved to clusters-from-document-source-get.xml
