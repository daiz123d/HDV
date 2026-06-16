@echo off
setlocal

if /i "%1"=="data" (
  java src\main\java\DataRestClient.java %2 %3 %4
) else if /i "%1"=="character" (
  java src\main\java\CharacterRestClient.java %2 %3 %4
) else (
  echo Usage:
  echo   run.bat data
  echo   run.bat character
  exit /b 1
)
