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
TITLE ${product.name} DCS ${product.version}

REM Determine installation home
IF NOT "%DCS_HOME%"=="" GOTO homeSet
SET DCS_HOME=%~dp0
:homeSet

REM Set other non-default options, if not set by the user.
IF NOT "%DCS_OPTS%"=="" GOTO optsSet
SET DCS_OPTS=
:optsSet

REM Determine JVM version and set any extra JVM options accordingly.
FOR /F "usebackq delims=" %%i IN (`java -jar "%%DCS_HOME%%\lib\progresso-jvmversion-${jvmversion.version}.jar"`) DO SET JVMVERSION=%%i

IF "%JVMVERSION%"=="6"    GOTO jvmUnsupported
IF "%JVMVERSION%"=="7"    GOTO jvmUnsupported
IF "%JVMVERSION%"=="8"    GOTO jvmUnsupported
IF "%JVMVERSION%"=="9"    GOTO jvmUnsupported
IF "%JVMVERSION%"=="10"   GOTO jvmUnsupported
IF "%JVMVERSION%"=="11"   GOTO jvmUnsupported
IF "%JVMVERSION%"=="12"   GOTO jvmUnsupported
IF "%JVMVERSION%"=="13"   GOTO jvmUnsupported
IF "%JVMVERSION%"=="14"   GOTO jvmUnsupported
IF "%JVMVERSION%"=="15"   GOTO jvmUnsupported
IF "%JVMVERSION%"=="16"   GOTO jvmUnsupported
IF "%JVMVERSION%"=="17"   GOTO jvmUnsupported
IF "%JVMVERSION%"=="18"   GOTO jvmUnsupported
IF "%JVMVERSION%"=="19"   GOTO jvmUnsupported
IF "%JVMVERSION%"=="20"   GOTO jvmUnsupported

REM Set script name for help.
SET SCRIPT_NAME=%~n0

REM Use JAVA_CMD, if provided.
IF NOT "%JAVA_CMD%"=="" GOTO javaSet
SET JAVA_CMD=java
:javaSet

REM Launch DCS.
%JAVA_CMD% %DCS_OPTS% -jar "%DCS_HOME%\lib\dcs-launcher-${product.version}.jar" %*
SET DCS_EXITVAL=%errorlevel%

REM Set cmd's window title and return with the exit code.
TITLE %comspec%
exit /b %DCS_EXITVAL%

:jvmUnsupported
ECHO ^> [ERROR] ${product.name} requires at least Java 21 (%JVMVERSION% detected).
exit /b 1