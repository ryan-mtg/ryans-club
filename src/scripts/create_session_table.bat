@echo off

if not exist "src\scripts\create_session_table.bat" (
    echo [91mError:[0m Must run from project directory
    exit /B 1
)

for /F "tokens=1* delims==" %%A IN (secret/deploy/deployment.properties) DO (
    IF "%%A"=="user" set user=%%B
    IF "%%A"=="host" set host=%%B
    IF "%%A"=="keyfile" set keyfilename=%%B
)

for /F "tokens=1* delims==" %%A IN (secret/deploy/database.properties) DO (
    IF "%%A"=="root_password" set database_password=%%B
)

IF [%database_password%] == [] (
    echo Error: Database password not set
    exit /B 1
)

set keyfile=secret\deploy\%keyfilename%
set hostid=%user%@%host%

echo [94mCreate Session Table Script[0m

set install_dir=ryans_club/install
echo:
echo [96mDeploying install files[0m
    call :run_command scp -i %keyfile% src\deploy\mysql.create.session.table.sql %hostid%:~/%install_dir%

echo [94mRunning Remote Install Script[0m
      echo [91mTODO:[0m run database script on remote server

:FAIL
    echo:
    if ERRORLEVEL 1 (
      echo [91mInstall Script Errored[0m
      exit /b %ERRORLEVEL%
    ) else (
      echo [92mInstall Script Complete[0m
      exit /b
    )

:run_command
    echo [93m  %*[0m
    %*
    if ERRORLEVEL 1 goto FAIL
goto :eof
