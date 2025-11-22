@echo off
echo Building Application...
if not exist bin mkdir bin
if not exist lib (
    echo ERROR: Folder 'lib' tidak ditemukan!
    echo Silakan copy mysql-connector-j-9.1.0.jar ke folder lib/
    pause
    exit /b
)

javac -cp "lib/*;bin" -d bin src\*.java
if %errorlevel% == 0 (
    echo Build SUCCESS!
) else (
    echo Build FAILED!
)
pause