#!/bin/bash

# Start script to initialise jenkins setup

echo "sudo elivation required"
sudo echo ""
echo "checking for docker and docker-compose"
sh checkDocker.sh
echo "checking if docker proxy config is available"
sh setupDockerProxy.sh
echo "shutting down existing docker network and containers"
docker-compose down -v
docker rm -f $(docker ps -aq)
echo "checking if jenkins user exists"
sh jenkins/addJenkinsUser.sh
echo "adding jenkins config and changing ownership"
sudo rm -rf jenkins/home/ jenkins/.ssh && mkdir -p jenkins/home
sudo cp -rf jenkins/config/* jenkins/home/
cd jenkins/home/ && mkdir plugins
#sudo chmod -R 777 .
cd .. && mkdir .ssh && cp -rf ~/.ssh/* .ssh/
sudo chown -R 1000:1000 home .ssh
cd home
echo "restoring and downloading jenkins plugins"
sudo sh restorePlugins.sh "$(cat installedPlugins.txt)"
cd ../..
echo "starting network"
docker-compose up  -d --force-recreate
