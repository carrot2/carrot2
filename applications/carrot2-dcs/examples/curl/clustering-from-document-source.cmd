@echo off
set DCS_URL=http://localhost:8080/dcs/rest 

REM With HTTP POST
curl %DCS_URL% -# ^
     -F "dcs.source=etools" ^
     -F "query=test" ^
     -o clusters-from-document-source-post.xml

REM With HTTP GET
curl "%DCS_URL%?dcs.source=etools&query=test" ^
     -o clusters-from-document-source-get.xml

echo Results saved to %RESULT%
