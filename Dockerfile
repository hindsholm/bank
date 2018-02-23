FROM payara/micro
COPY ./target/bank.war /opt/payara/deployments
COPY ./src/test/payara/post-boot-commands.txt /opt/payara
COPY ./src/test/payara/password.txt /opt/payara
ENTRYPOINT []
CMD ["/usr/bin/java", "-jar", "/opt/payara/payara-micro.jar", "--nocluster", "--deploymentDir", "/opt/payara/deployments", "--postbootcommandfile", "/opt/payara/post-boot-commands.txt"]
