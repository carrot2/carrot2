@ECHO OFF

set CMD_LINE_ARGS=%1
if ""%1""=="""" goto doneStart
shift
:setupArgs
if ""%1""=="""" goto doneStart
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setupArgs
:doneStart

SET OPTS=-Xms128m -Xmx384m -XX:NewRatio=1
SET MAIN_CLASS=org.carrot2.dcs.http.DCSApp
SET DEPLIB=WEB-INF/lib
SET LAUNCHER=-jar %DEPLIB%\carrot2-launcher.jar -cp WEB-INF/classes -cpdir %DEPLIB%

java %OPTS% %LAUNCHER% %MAIN_CLASS% %CMD_LINE_ARGS%