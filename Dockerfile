FROM payara/micro
COPY ./target/bank.war /opt/payara/deployments
ENTRYPOINT []
CMD java -jar /opt/payara/payara-micro.jar --deploymentDir /opt/payara/deployments

# FROM airhacks/payara-micro
# ENV ARCHIVE_NAME bank.war
# COPY ./target/bank.war ${DEPLOYMENT_DIR}
