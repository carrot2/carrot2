#!/bin/sh
DCS_HOST=http://localhost:8080/dcs/rest

RESULT=faceting-by-custom-field.xml
curl $DCS_HOST -# \
     -F "dcs.c2stream=@../shared/sicilia-e-bella.xml" \
     -F "dcs.algorithm=source" \
     -F "dcs.clusters.only=true" \
     -F "ByAttributeClusteringAlgorithm.fieldName=author" \
     -o $RESULT
echo Results saved to $RESULT
