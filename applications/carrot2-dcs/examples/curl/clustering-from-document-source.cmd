@echo off
set DCS_URL=http://localhost:8080/dcs/rest 
set RESULT=clusters-from-document-source.xml 

curl %DCS_URL% -# ^
     -F "dcs.source=etools" ^
     -F "query=test" ^
     -o %RESULT%
echo Results saved to %RESULT%
