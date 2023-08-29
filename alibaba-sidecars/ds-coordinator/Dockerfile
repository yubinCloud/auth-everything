FROM openjdk:17-slim

ENV TZ=Asia/Sh
ENV PARAMS=""

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /root

EXPOSE 10014

ADD target/ds-coordinator-*.jar app.jar

CMD [ "sh", "-c", "java -jar $JAVA_OPTS app.jar $PARAMS" ]
