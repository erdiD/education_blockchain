# ILV CI - Readme

This project is a a continuous integration solution for the ILV Project. The setup was setup, tested and operated on Ubuntu 16 (on AWS) and 17.

## Setup:
Start the system from the project root using the following script:

    ~$ sh startCIEnv.sh

This will do the following:
- Check if docker and docker-compose exist and install if necessary
- Shut down network with docker-compose and remove containers (should a instance exist) 
- Check if docker proxy is set and set it if necessary
- Copy Jenkins config, including ssh keys to new folder
- Start script to download and restore jenkins plugins
- Finally it will start the project with docker-compose up

### Project structure 

#### projectfolder/
The project root includes the setup scripts and docker-compose configuration

##### jenkins
This folder contains the config folder and Dockerfile of the jenkins container. The config folder holds several xml configuration files, a users folder and a jobs folder.
##### server
This folder contains the Dockerfile for the app server (wildfly)

##### ui
This folder contains the Dockerfile of the ui builder, basically a docker image to build angular apps and return a dist folder

## ILV Pipeline
The applications is built with following steps:
- Initially the Angular project is built in an own container
- Then the build process for the server app starts by copying the angular dist/ folder from the container above and then building the war file
- Finally the image created in the step above is deployed on the CI machine

### Important scripts

##### jenkins/backupJenkins.sh

Creates a backup of the config (jobs, users, etc) in the ~ folder of the machine running the ci. This folder can be mapped to the jenkins ~

##### jenkins/config/backupPluginsList.sh
Backs up the currently installed plugins into a file (jenkins/config/installedPlugins.txt)

##### jenkins/config/restorePlugins.sh
Parses jenkins/config/installedPlugins.txt and downloads plugins into plugins folder. Jenkins will automatically detect new plugins unpack and install them when they are downloaded to the plugins folder


## Important
To pull from the docker registry following steps have to be ensured:
- Allow docker on machine pulling from registry to use a insecure (http instead of https) connection:
    - create /etc/docker/daemon.json and add the following (current registry URI, change if necessary)
    
            {
              "insecure-registries" : ["melanie.dbe.aws.db.de:9100"]
            }

- Add the registry URI to docker NO_PROXY:
    - create /etc/systemd/system/docker.service.d/http-proxy.conf and add the following:

            [Service]
            Environment="HTTP_PROXY=http://webproxy.aws.db.de:8080/" "HTTPS_PROXY=http://webproxy.aws.db.de:8080/" "NO_PROXY=melanie.dbe.aws.db.de"

    - daemon-reload and restart docker service
    
            sudo systemctl daemon-reload
            sudo systemctl restart docker.service
            
            
### Future improvments
- Add dependancy management (nexus)
- Add feedback to gitlab on build status
- Add jobs to build shim and hyperledger images
