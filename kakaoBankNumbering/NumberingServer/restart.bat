@echo off

set "CATALINA_HOME=./"
set "STOP=%CATALINA_HOME%\shutdown.bat"
set "START=%CATALINA_HOME%\startup.bat"

@echo on
call %STOP%
timeout /t 1
call %START%
timeout /t 1