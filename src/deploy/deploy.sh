echo ' -> running'
cp /home/ec2-user/install/stfc-0.0.1-SNAPSHOT.jar /bin
echo ' -> moved'
sudo systemctl restart stfc
echo ' -> restarted'