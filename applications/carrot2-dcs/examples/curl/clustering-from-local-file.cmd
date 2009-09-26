@echo off
set DCS_URL=http://localhost:8080/dcs/rest 
set RESULT=clusters-from-local-file.xml

curl %DCS_URL% -# ^
     -F "dcs.c2stream=@../shared/data-mining.xml" ^
     -o %RESULT%
echo Results saved to %RESULT%
