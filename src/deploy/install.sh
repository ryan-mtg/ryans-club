echo Running install script

DB_PASSWORD=none
POSITIONAL_ARGS=()
INSTALL_DIR=ryans_club/install
DEPLOY_DIR=ryans_club/deploy
SCRIPTS_DIR=ryans_club/scripts

if ! [ -f $INSTALL_DIR/install.sh  ]; then
  echo Error: must run from home directory
  exit 1
fi

if ! [[ -f $INSTALL_DIR/deploy.sh && -f $INSTALL_DIR/stfc.service && -f $INSTALL_DIR/stfc-0.0.1-SNAPSHOT.jar ]]; then
  echo Error: install files not in $INSTALL_DIR
  exit 1
fi

while [[ $# -gt 0 ]]; do
  case $1 in
    -dbpass|--database-password)
      DB_PASSWORD="$2"
      shift # past argument
      shift # past value
      ;;
    -*|--*)
      echo "Error: Unknown option $1"
      exit 1
      ;;
    *)
      POSITIONAL_ARGS+=("$1") # save positional arg
      shift # past argument
      ;;
  esac
done

if [ "$DB_PASSWORD" = "none" ]; then
  echo Error: Database Password not set
  exit 1
fi

echo
echo Installing Java

if hash java 2> /dev/null; then
  echo   Java is already installed
else
  echo Installing Java
  apt-get update -y && apt-get install default-jre

  if hash java 2> /dev/null; then
    echo   Java installed successfully
  else
    echo   Error: Java install fail
  fi
fi

echo
echo Installing MySQL

if hash mysql 2> /dev/null; then
  echo   MySQL is already installed
else
  echo Installing MySQL

  apt-get update -y

  apt-get install gnupg2 --assume-yes --force-yes

  DB_CONFIG_FILE_NAME=mysql-apt-config_0.8.29-1_all.deb
  DB_CONFIG_DIR=ryans_club/mysql
  DB_CONFIG_FILE=$DB_CONFIG_DIR/$DB_CONFIG_FILE_NAME

  echo Downloading mysql deb file
  mkdir -p $DB_CONFIG_DIR
  rm "$DB_CONFIG_DIR/$DB_CONFIG_FILE_NAME*"
  exit 2

  wget --directory-prefix $DB_CONFIG_DIR https://dev.mysql.com/get/$DB_CONFIG_FILE_NAME

  export DEBIAN_FRONTEND="noninteractive"

  dpkg -i $DB_CONFIG_FILE

  debconf-set-selections <<EOD
  mysql-apt-config mysql-apt-config/select-server select mysql-8.0
  mysql-community-server mysql-community-server/root-pass password $DB_PASSWORD
  mysql-community-server mysql-community-server/re-root-pass password $DB_PASSWORD
EOD

  apt-get install mysql-server mysql-client --assume-yes --force-yes

  systemctl start mysql.service
  systemctl enable mysql.service

  if hash mysql 2> /dev/null; then
    echo   MySQL installed successfully
  else
    echo   Error: MySQL install fail
  fi
fi

echo
echo Creating Scripts Directory
  mkdir -p $SCRIPTS_DIR
  chown admin $SCRIPTS_DIR
  cp $INSTALL_DIR/deploy.sh $SCRIPTS_DIR/deploy.sh
  chmod 700 $SCRIPTS_DIR/deploy.sh

echo
echo Creating Deploy Directory
  mkdir -p $DEPLOY_DIR
  chown admin $DEPLOY_DIR

echo
echo Creating Service
  mkdir -p /var/stfc
  chown admin /var/stfc
  cp $INSTALL_DIR/stfc.service /etc/systemd/system
  cp $INSTALL_DIR/stfc-0.0.1-SNAPSHOT.jar /bin

  systemctl enable stfc
  systemctl start stfc