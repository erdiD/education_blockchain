#!/usr/bin/env bash

BACKUPDIR=~
BACKUPFOLDER=config_backup_"$(date +%s)"
JENKINSHOME=~/ilv/ci/jenkins/home
mkdir $BACKUPDIR/$BACKUPFOLDER
sudo chown -R $USER:$USER $JENKINSHOME && cd $JENKINSHOME
rm -f installedPlugins.txt && cd plugins
find -maxdepth 1 -type d -printf '%P\n' > ../installedPlugins.txt
cd ..
cp --parents jobs/*/config.xml jobs/*/jobs/*/config.xml $BACKUPDIR/$BACKUPFOLDER
cp -rf users $BACKUPDIR/$BACKUPFOLDER
cp *.xml *.txt *.sh $BACKUPDIR/$BACKUPFOLDER
