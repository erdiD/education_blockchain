#!/bin/bash
# Script check if docker proxy is configured
cat /etc/systemd/system/docker.service.d/http-proxy.conf > /dev/null
if [ $? -eq 0 ]
then
    echo "proxy config is available"
else
    confDir='/etc/systemd/system/docker.service.d'
    confFile='$confDir/http-proxy.conf'
    echo "adding proxy config"
	sudo mkdir $confDir
	sudo touch $confFile
    echo '# cat /etc/systemd/system/docker.service.d/http-proxy.conf' > $confFile
    echo '[Service]' > $confFile
    echo 'Environment="HTTP_PROXY=https://web-proxy.corp.xxxxxx.com:8080/"' > $confFile
    echo 'Environment="HTTPS_PROXY=https://web-proxy.corp.xxxxxx.com:8080/"' > $confFile
    echo 'Environment="NO_PROXY=localhost,127.0.0.1,localaddress,.localdomain.com"' > $confFile
    sudo systemctl daemon-reload
    sudo systemctl restart docker
fi

