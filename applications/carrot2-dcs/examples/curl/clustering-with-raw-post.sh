#!/bin/sh
DCS_HOST=http://localhost:8080/dcs/rest
RESULT=clusters-from-raw-post.xml

curl $DCS_HOST -# \
     --data-binary @clustering-with-raw-post-data.txt \
     -H "Content-Type: multipart/form-data; boundary=---------------------------191691572411478" \
     -H "Content-Length: 44389" \
     -o $RESULT
echo Results saved to $RESULT
