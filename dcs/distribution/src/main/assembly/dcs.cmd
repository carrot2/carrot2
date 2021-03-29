@ECHO OFF

REM Redirect the user to use the dcs (bash) script under CygWin environment
REM otherwise CTRL-C leaves subprocesses running.
IF DEFINED SHELL (
  IF DEFINED ORIGINAL_PATH (
    ECHO Use bash script dcs on CygWin instead of dcs.cmd
    EXIT /b 1
  )
)

SETLOCAL
TITLE DCS ${product.version}

REM Determine installation home
IF NOT "%DCS_HOME%"=="" GOTO homeSet
SET DCS_HOME=%~dp0
:homeSet

REM Set other non-default options, if not set by the user.
IF NOT "%DCS_OPTS%"=="" GOTO optsSet
SET DCS_OPTS=
:optsSet

REM Use JAVA_CMD, if provided.
IF NOT "%JAVA_CMD%"=="" GOTO javaSet
SET JAVA_CMD=java
:javaSet

REM Launch DCS.
%JAVA_CMD% --illegal-access=deny %JVMARGS% %DCS_OPTS% -jar "%DCS_HOME%\lib\dcs-launcher-${product.version}.jar" %*
SET DCS_EXITVAL=%errorlevel%

REM Set cmd's window title and return with the exit code.
TITLE %comspec%
exit /b %DCS_EXITVAL%
