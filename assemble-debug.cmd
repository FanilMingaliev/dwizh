@echo off
setlocal
cd /d "%~dp0"
call gradlew.bat :app:assembleDebug %*
exit /b %ERRORLEVEL%
