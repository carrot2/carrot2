@echo off

REM #
REM # Strongly influenced by ANT's start script ;)
REM #

SET CARROT_BIN_PATH=%~dp0

SET CMD_LINE_ARGS=%1
if ""%1""=="""" goto doneStart
shift
:setupArgs
if ""%1""=="""" goto doneStart
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setupArgs
:doneStart


java -Djava.ext.dirs=%CARROT_BIN_PATH%../tmp/dist;%CARROT_BIN_PATH%../lib com.dawidweiss.carrot.tools.QueryInputComponent %CMD_LINE_ARGS%
