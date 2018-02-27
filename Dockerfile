FROM payara/micro
# keyfile is missing from payara/micro
RUN mkdir -p /tmp/MICRO-INF/domain &&\
    touch /tmp/MICRO-INF/domain/keyfile &&\
    jar uf /opt/payara/payara-micro.jar -C /tmp MICRO-INF/domain/keyfile
ENTRYPOINT []
CMD ["/usr/bin/java", "-jar", "/opt/payara/payara-micro.jar", "--nocluster", "--deploymentDir", "/opt/payara/deployments", "--postbootcommandfile", "/opt/payara/post-boot-commands.txt"]
COPY ./src/test/payara/post-boot-commands.txt /opt/payara
COPY ./src/test/payara/password.txt /opt/payara
COPY ./target/bank.war /opt/payara/deployments
