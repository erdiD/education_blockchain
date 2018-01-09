cd ../ilv-server-all/rest-api
mvn clean package && scp target/ilv.war ec2-user@10.43.185.247:/home/ec2-user/wildfly-10.1.0.Final/standalone/deployments/ && cd ../../scripts
