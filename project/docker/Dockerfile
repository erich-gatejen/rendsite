FROM openjdk:8-jre-slim

WORKDIR /

COPY setup.sh /
COPY internalrender.sh /
COPY rendsite-binary-package.zip /

RUN chmod 755 setup.sh && \
    chmod 755 internalrender.sh && \
    ./setup.sh && \
    rm -f setup.sh
