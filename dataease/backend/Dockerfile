FROM openjdk:8

WORKDIR /opt

RUN mkdir -p /opt/dataease/conf \
    && mkdir -p /opt/dataease/logs \
    && mkdir -p /opt/dataease/data/feature \
    && mkdir -p /opt/dataease/drivers

COPY mapFiles/full  /opt/dataease/data/feature/
COPY drivers/* /opt/dataease/drivers/

COPY target/backend-1.8.0.jar app.jar

CMD ["java", "-jar", "app.jar"]