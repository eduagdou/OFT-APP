@echo off
set "ADB=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"

if not exist "%ADB%" (
  echo No se encontro adb en %ADB%
  echo Instala Android SDK Platform-Tools desde Android Studio y vuelve a intentarlo.
  exit /b 1
)

"%ADB%" devices
"%ADB%" reverse tcp:8080 tcp:8080
if errorlevel 1 (
  echo No fue posible conectar el telefono. Autoriza la depuracion USB en el dispositivo.
  exit /b 1
)

echo Telefono conectado al backend local en http://127.0.0.1:8080/
