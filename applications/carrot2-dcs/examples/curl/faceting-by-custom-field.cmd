@echo off
set DCS_URL=http://localhost:8080/dcs/rest 
set RESULT=faceting-by-custom-field.xml

curl %DCS_URL% -# ^
     -F "dcs.c2stream=@../shared/sicilia-e-bella.xml" ^
     -F "dcs.algorithm=source" ^
     -F "dcs.clusters.only=true" ^
     -F "ByAttributeClusteringAlgorithm.fieldName=author" ^
     -o %RESULT%
echo Results saved to %RESULT%
