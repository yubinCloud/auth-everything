FROM openjdk:17

EXPOSE 9590

WORKDIR /root

ENV PARAMS=""

ADD ./wait-for-it.sh wait-for-it.sh
RUN chmod +x ./wait-for-it.sh

ADD target/sso-auth-*.jar app.jar

ENTRYPOINT [ "sh", "-c", "java -jar $JAVA_OPTS app.jar $PARAMS" ]
