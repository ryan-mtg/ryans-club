echo Running deploy script

DEPLOY_DIR=ryans_club/deploy
SCRIPTS_DIR=ryans_club/scripts

DEPLOY_JAR=$DEPLOY_DIR/stfc-0.0.1-SNAPSHOT.jar

if ! [ -f $SCRIPTS_DIR/deploy.sh  ]; then
  echo Error: must run from home directory
  exit 1
fi

if ! [ -f $DEPLOY_JAR  ]; then
  echo Error: No assembly found in $DEPLOY_DIR
  exit 1
fi

echo
echo Installing assembly
cp $DEPLOY_DIR/stfc-0.0.1-SNAPSHOT.jar /bin

echo
echo Restarting service
systemctl restart stfc