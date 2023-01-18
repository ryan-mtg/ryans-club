@echo off

Rem This script generates an SSL certificate used for testing this service when run locally for development purposes.

IF "%1" == "" GOTO USAGE
    set password=%1

IF EXIST "build.gradle" GOTO GENERATE
GOTO DIRECTORY

:GENERATE
    DEL secret\local\stfc.p12
    echo generating a certificate...
    echo keytool -genkeypair -alias stfc -keyalg RSA -keysize 2048 -keypass %password% -storetype PKCS12 -keystore secret\local\stfc.p12 -storepass %password% -validity 3650
    keytool -genkeypair -alias stfc -keyalg RSA -keysize 2048 -keypass %password% -storetype PKCS12 -keystore secret\local\stfc.p12 -storepass %password% -validity 3650
    echo.
    echo generating a keystore from certificate...
    echo keytool -importkeystore -deststorepass %password% -destkeypass %password% -deststoretype pkcs12 -srckeystore secret\local\stfc.p12 -srcstoretype PKCS12 -srcstorepass %password% -destkeystore secret\local\resources\keystore\stfc.keystore -alias stfc
    keytool -importkeystore -deststorepass %password% -destkeypass %password% -deststoretype pkcs12 -srckeystore secret\local\stfc.p12 -srcstoretype PKCS12 -srcstorepass %password% -destkeystore secret\local\resources\keystore\stfc.keystore -alias stfc
GOTO END

:USAGE
    echo "Usage: %0 <certificate password>"
GOTO END

:DIRECTORY
    echo This command must be run in the root directory of the project

:END