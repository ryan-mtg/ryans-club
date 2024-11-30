@echo off

if not exist "src\scripts\deploy.bat" (
    echo [91mError:[0m Must run from project directory
    exit /B 1
)

for /F "tokens=1* delims==" %%A IN (secret/deploy/deployment.properties) DO (
    IF "%%A"=="user" set user=%%B
    IF "%%A"=="host" set host=%%B
    IF "%%A"=="keyfile" set keyfilename=%%B
)

echo [94mStarting Deploy Script[0m

set keyfile=secret\deploy\%keyfilename%
set hostid=%user%@%host%
set deploy_dir=ryans_club/deploy
set scripts_dir=ryans_club/scripts

echo [96mCopying assembly[0m
    call :run_command scp -i %keyfile% build\libs\stfc-0.0.1-SNAPSHOT.jar %hostid%:~/%deploy_dir%

echo [96mRunning remote deploy script[0m
    call :run_command ssh -i %keyfile% %hostid% "sudo bash %scripts_dir%/deploy.sh"

:FAIL
    echo:
    if ERRORLEVEL 1 (
      echo [91mDeploy Script Errored[0m
      exit /b %ERRORLEVEL%
    ) else (
      echo [92mDeploy Script Complete[0m
      exit /b
    )

:run_command
    echo [93m  %*[0m
    %*
    if ERRORLEVEL 1 goto FAIL
goto :eof