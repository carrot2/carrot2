@echo off

REM #
REM # Strongly influenced by ANT's start script ;)
REM #

SET CARROT_BIN_PATH=%~dp0
SET CARROT_UTIL_REMOTE=%CARROT_BIN_PATH%../components/carrot2-util-remote/tmp/dist;%CARROT_BIN_PATH%../components/carrot2-util-remote/tmp/dist/deps-carrot2-util-remote-jar

SET CMD_LINE_ARGS=%1
if ""%1""=="""" goto doneStart
shift
:setupArgs
if ""%1""=="""" goto doneStart
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setupArgs
:doneStart

java -Djava.ext.dirs=%CARROT_UTIL_REMOTE% com.dawidweiss.carrot.tools.QueryFilterComponent %CMD_LINE_ARGS%
