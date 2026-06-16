@echo off
setlocal

if not exist out mkdir out
javac -encoding UTF-8 -d out src\main\java\*.java
if errorlevel 1 exit /b 1

java -cp out RestDataClient %*
