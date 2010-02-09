#!/bin/sh
DCS_HOST=http://localhost:8080/dcs/rest
RESULT=clusters-with-custom-attributes.xml

curl $DCS_HOST -# \
     -F "dcs.source=etools" \
     -F "dcs.algorithm=lingo" \
     -F "query=berlin" \
     -F "EToolsDocumentSource.language=GERMAN" \
     -F "LingoClusteringAlgorithm.factorizationQuality=MEDIUM" \
     -F "MultilingualClustering.defaultLanguage=GERMAN" \
     -o $RESULT
echo Results saved to $RESULT
