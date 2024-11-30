@echo off

if not exist "src\scripts\deploy_install.bat" (
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

echo [94mStarting Install Script[0m

set install_dir=ryans_club/install
echo:
echo [96mDeploying install files[0m
    call :run_command ssh -i %keyfile% -o ConnectTimeout=3 %hostid% "mkdir -p %install_dir%"
    call :run_command scp -i %keyfile% src\deploy\install.sh %hostid%:~/%install_dir%
    call :run_command scp -i %keyfile% src\deploy\deploy.sh %hostid%:~/%install_dir%
    call :run_command scp -i %keyfile% src\deploy\stfc.service %hostid%:~/%install_dir%
REM    call :run_command scp -i %keyfile% build\libs\stfc-0.0.1-SNAPSHOT.jar %hostid%:~/%install_dir%

echo [96mUpdating file permissions[0m
    call :run_command ssh -i %keyfile% -o ConnectTimeout=3 %hostid% "chmod 700 %install_dir%/install.sh"

echo [94mRunning Remote Install Script[0m
    call :run_command ssh -i %keyfile% %hostid% "sudo bash %install_dir%/install.sh -dbpass %database_password%"

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