@echo off

REM This script generates an SSL certificate used for the production instance of this service.
REM Run in the project's root directory (password must match what's in the secret\deploy):
REM    src\scripts\generate_certificate.bat <password>

set certbot_dir=c:\Certbot\live\ryans-club.com
set resources_dir=secret\deploy\resources
set keystore_dir=%resources_dir%\keystore

IF "%1" == "" GOTO USAGE
set password=%1

IF EXIST "build.gradle" GOTO GENERATE
GOTO DIRECTORY

:GENERATE
    echo Invoking certificate challenge.
    echo.
    certbot certonly --manual --preferred-challenges dns -d ryans-club.com -d www.ryans-club.com

    DEL secret\deploy\stfc.p12
    echo.
    echo Converting certificate to p12
    echo openssl pkcs12 -export -in %certbot_dir%\cert.pem -inkey %certbot_dir%\privkey.pem -out secret\deploy\stfc.p12 -name www.ryans-club.com -CAfile %certbot_dir%\fullchain.pem -caname "Let's Encrypt Authority X3" -password pass:%password%
    openssl pkcs12 -export -in %certbot_dir%\cert.pem -inkey %certbot_dir%\privkey.pem -out secret\deploy\stfc.p12 -name stfc -CAfile %certbot_dir%\fullchain.pem -caname "Let's Encrypt Authority X3" -password pass:%password%

    MKDIR %keystore_dir%
    echo.
    echo Converting to keystore
    echo keytool -importkeystore -deststorepass %password% -destkeypass %password% -deststoretype pkcs12 -srckeystore secret\deploy\stfc.p12 -srcstoretype PKCS12 -srcstorepass %password% -destkeystore %keystore_dir%\stfc.keystore -alias stfc
    keytool -importkeystore -deststorepass %password% -destkeypass %password% -deststoretype pkcs12 -srckeystore secret\deploy\stfc.p12 -srcstoretype PKCS12 -srcstorepass %password% -destkeystore %keystore_dir%\stfc.keystore -alias stfc
GOTO END

:USAGE
    echo "Usage: %0 <certificate password>"
GOTO END

:DIRECTORY
    echo This command must be run in the root directory of the project

:END
