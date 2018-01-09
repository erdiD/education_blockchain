# We label our stage as 'builder'
FROM node:boron as builder

ENV http_proxy=http://webproxy.aws.db.de:8080/
ENV https_proxy=http://webproxy.aws.db.de:8080/

RUN mkdir -p /usr/src/app
ADD . /usr/src/app
RUN apt-get update && apt-get install -y git
WORKDIR /usr/src/app

RUN cd /usr/src/app
RUN npm config set proxy $http_proxy
RUN npm config set https-proxy $https_proxy

RUN npm i


# Run build and find dist in built:
# docker build -t angularbuilder . && docker run -v $(pwd)/built:/usr/src/app/built angularbuilder /bin/sh -c '$(npm bin)/ng build && cp -r dist built/'



