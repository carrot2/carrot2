@echo off
set DCS_URL=http://localhost:8080/dcs/rest 
set RESULT=clusters-from-raw-post.xml

curl %DCS_URL% -# ^
     --data-binary @clustering-with-raw-post-data.bin ^
     -H "Content-Type: multipart/form-data; boundary=----------------------------f223859fc094" ^
     -o %RESULT%
echo Results saved to %RESULT%
