@echo off
set CURRENT_DIR=%~dp0
set JAVA_HOME=%CURRENT_DIR%.tools\jdk
set M2_HOME=%CURRENT_DIR%.tools\mvn
set PATH=%JAVA_HOME%\bin;%M2_HOME%\bin;%PATH%
call "%M2_HOME%\bin\mvn.cmd" %*
