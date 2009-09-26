@echo off
set DCS_URL=http://localhost:8080/dcs/rest 
set RESULT=clusters-from-raw-post.xml

curl %DCS_URL% -# ^
     --data-binary @clustering-with-raw-post-data.txt ^
     -H "Content-Type: multipart/form-data; boundary=---------------------------191691572411478" ^
     -H "Content-Length: 44389" ^
     -o %RESULT%
echo Results saved to %RESULT%
