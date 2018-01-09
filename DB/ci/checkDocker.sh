#!/bin/bash

# Script check if docker and docker-compose are installed
# if not it installs and rechecks
# docker-compose version is currently hardcoded to v1.15
docker --version | grep "Docker version"
if [ $? -eq 0 ]
then
    echo "nothing to do here"
else
    echo "installing docker"
    sudo apt-get update -y
    sudo apt-get install -y \
    apt-transport-https \
    ca-certificates \
    curl \
    software-properties-common
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg --proxy $https_proxy  | sudo apt-key add
    sudo add-apt-repository \
       "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
       $(lsb_release -cs) \
       stable"
    sudo apt-get update -y
    sudo apt-get install docker-ce -y
    echo "adding docker group and your user to it"
    sudo groupadd docker
    sudo usermod -aG docker $USER
    echo "checking if installation was successful"
    docker --version | grep "Docker version"
    if [ $? -eq 0 ]
    then
        echo "installation successful"
    else
        echo "encountered error after installing docker"
        exit
    fi
fi

docker-compose --version | grep "docker-compose version"
if [ $? -eq 0 ]
then
    echo "nothing to do here"
else
    echo "installing docker-compose"
    sudo curl -L https://github.com/docker/compose/releases/download/1.15.0/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose --proxy $https_proxy
    sudo chmod +x /usr/local/bin/docker-compose
    echo "checking if installation was successful"
    docker-compose --version | grep "docker-compose version"
    if [ $? -eq 0 ]
    then
        echo "installations successful"
    else
        echo "encountered a problem after installing docker-compose"
    fi
fi