FROM payara/micro
COPY ./target/bank.war /opt/payara/deployments
ENTRYPOINT []
CMD java -jar /opt/payara/payara-micro.jar --deploymentDir /opt/payara/deployments
