#!/bin/sh
DCS_HOST=http://localhost:8080/dcs/rest
RESULT=clusters-from-raw-post.xml

curl $DCS_HOST -# \
     --data-binary @clustering-with-raw-post-data.bin \
     -H "Content-Type: multipart/form-data; boundary=----------------------------f223859fc094" \
     -o $RESULT
echo Results saved to $RESULT
