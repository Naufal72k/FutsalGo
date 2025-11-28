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

@echo off
echo Running...
java -cp "lib/*;bin" FutsalManagementApp
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Aplikasi gagal dijalankan.
    echo Pastikan: 
    echo 1. Database MySQL sudah running
    echo 2. Database sudah dibuat
    echo 3. User 'root' tanpa password
    echo 4. mysql-connector di folder lib/
    pause
)