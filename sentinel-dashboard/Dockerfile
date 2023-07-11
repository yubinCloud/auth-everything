FROM openjdk:11

ENV TZ=Asia/Sh
ENV PARAMS=""

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /root

EXPOSE 10010

ADD sentinel.jar.protected app.jar

CMD [ "sh", "-c", "java -Dauth.enabled=false $JAVA_OPS -jar app.jar --server.servlet.context-path=/sentinel $PARAMS" ]