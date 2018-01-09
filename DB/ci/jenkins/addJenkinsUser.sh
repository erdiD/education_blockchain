#!/bin/bash
id jenkins
if [ $? -eq 0 ]
then
    echo "jenkins user exists no further action required"
else
    echo "adding jenkins user"
	sudo useradd jenkins
fi
