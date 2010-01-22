@echo off
set DCS_URL=http://localhost:8080/dcs/rest 
set RESULT=clusters-with-custom-attributes.xml

curl %DCS_URL% -# ^
     -F "dcs.source=etools" ^
     -F "dcs.algorithm=lingo" ^
     -F "query=berlin" ^
     -F "EToolsDocumentSource.language=GERMAN" ^
     -F "LingoClusteringAlgorithm.factorizationQuality=MEDIUM" ^
     -F "MultilingualClustering.defaultLanguage=GERMAN" ^
     -o %RESULT%
echo Results saved to %RESULT%
