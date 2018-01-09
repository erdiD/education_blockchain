FROM jboss/wildfly

ENV http_proxy=http://webproxy.aws.db.de:8080/
ENV https_proxy=http://webproxy.aws.db.de:8080/
COPY ilv.war /opt/jboss/wildfly/standalone/deployments/

EXPOSE 80/tcp 8080/tcp 9990/tcp
