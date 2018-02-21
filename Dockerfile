FROM airhacks/payara-micro
ENV ARCHIVE_NAME bank.war
COPY ./target/bank.war ${DEPLOYMENT_DIR}
