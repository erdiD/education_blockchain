
# We label our stage as 'builder'
FROM jenkins/jenkins

ENV http_proxy=http://webproxy.aws.db.de:8080/
ENV https_proxy=http://webproxy.aws.db.de:8080/
USER root
RUN apt-get update \
      && apt-get install -y sudo \
      && apt-get install -y maven libltdl-dev \
      && rm -rf /var/lib/apt/lists/*
RUN echo "jenkins ALL=NOPASSWD: ALL" >> /etc/sudoers

#COPY ./config/ /var/jenkins_home/

#RUN cd /var/jenkins_home && mkdir plugins && sh restorePlugins.sh $(cat installedPlugins.txt)

USER jenkins