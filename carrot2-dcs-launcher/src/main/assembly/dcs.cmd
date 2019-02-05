@echo off

rem
rem Suppress Terminate batch job on CTRL+C
rem
if ""%1"" == ""run"" goto delRun
if "%TEMP%" == "" goto mainEntry
if exist "%TEMP%\%~nx0.run" goto mainEntry
echo Y>"%TEMP%\%~nx0.run"
if not exist "%TEMP%\%~nx0.run" goto mainEntry
echo Y>"%TEMP%\%~nx0.Y"
call "%~f0" %* <"%TEMP%\%~nx0.Y"
rem Use provided errorlevel
set RETVAL=%ERRORLEVEL%
del /Q "%TEMP%\%~nx0.Y" >NUL 2>&1
exit /B %RETVAL%
:delRun
rem consume the dummy argument.
shift
:mainEntry
del /Q "%TEMP%\%~nx0.run" >NUL 2>&1

rem
rem Default JVM options here
rem
if not "%DCS_OPTS%"=="" goto optsSet
set DCS_OPTS=-Xmx768m
:optsSet

rem
rem Launch the DCS
rem
java %DCS_OPTS% -Ddcs.war=web/carrot2-dcs-service-@version@.war -jar lib/carrot2-dcs-launcher-@version@.jar %*
