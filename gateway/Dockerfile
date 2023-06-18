FROM openjdk:17

EXPOSE 10010

WORKDIR /root

ENV PARAMS=""

ADD ./wait-for-it.sh wait-for-it.sh
RUN chmod +x ./wait-for-it.sh
ADD target/gateway-*.jar app.jar

CMD [ "sh", "-c", "java -jar $JAVA_OPTS app.jar $PARAMS" ]
