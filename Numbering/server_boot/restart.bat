@echo off

set "STOP=shutdown.bat"
set "START=startup.bat"

@echo on
call %STOP%
timeout /t 1
call %START%
timeout /t 1