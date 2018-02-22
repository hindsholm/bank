FROM payara/micro
COPY ./target/bank.war /opt/payara/deployments
ENTRYPOINT []
CMD ["/usr/bin/java", "-jar", "/opt/payara/payara-micro.jar", "--nocluster", "--deploymentDir", "/opt/payara/deployments"]
