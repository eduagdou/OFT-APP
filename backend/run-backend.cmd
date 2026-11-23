@echo off
setlocal
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.21.10002-hotspot"
set "MVN=%~dp0..\.tools\apache-maven-3.10.2\bin\mvn.cmd"

if not exist "%JAVA_HOME%\bin\java.exe" (
  echo No se encontro JDK 17. Configura JAVA_HOME con una instalacion de JDK 17.
  exit /b 1
)
if not exist "%MVN%" (
  echo No se encontro Maven local. Ejecuta la preparacion del proyecto antes de iniciar el backend.
  exit /b 1
)

set "PATH=%JAVA_HOME%\bin;%PATH%"
call "%MVN%" spring-boot:run
