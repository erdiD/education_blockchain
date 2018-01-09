#! /bin/sh

echo "Going to plugins directory"
echo "The backup file will be found there as well"
cd ~/plugins
find -maxdepth 1 -type d -printf '%P\n' > installedPlugins.txt