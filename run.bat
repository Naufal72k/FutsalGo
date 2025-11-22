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