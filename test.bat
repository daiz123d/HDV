@echo off
setlocal

if exist out rmdir /s /q out
mkdir out\test
javac -encoding UTF-8 -d out\test src\main\java\RestDataClient.java src\test\java\*.java
if errorlevel 1 exit /b 1

java -cp out\test RestDataClientTest
