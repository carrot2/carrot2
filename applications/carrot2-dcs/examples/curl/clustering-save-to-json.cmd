@echo off
set DCS_URL=http://localhost:8080/dcs/rest 
set RESULT=clusters-saved-to-json.json

curl %DCS_URL% -# ^
     -F "dcs.source=etools" ^
     -F "query=json" ^
     -F "dcs.output.format=JSON" ^
     -o %RESULT%
echo Results saved to %RESULT%
