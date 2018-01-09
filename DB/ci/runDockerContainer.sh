#! /bin/bash


DOCKER_REPO="$(DOCKERREG)/ilv-app"
TAG=$1
CONTAINER_NAME=ilv_app_deployed

containerId=`docker ps -qa --filter "name=$CONTAINER_NAME"`
echo "#################################################"
echo $DOCKERREG
echo $DOCKER_REPO
if [ -n "$containerId" ]
then
	echo "Stopping and removing existing ilv-app container"
	docker stop $CONTAINER_NAME
	docker rm $CONTAINER_NAME
fi

docker run -d -p 8080:8080 -p 9990:9990 -p 8443:8443 --name $CONTAINER_NAME $DOCKER_REPO:$TAG /bin/sh -c 'sh /opt/jboss/wildfly/bin/standalone.sh -b=0.0.0.0 -bmanagement=0.0.0.0'


