FROM openjdk:17

ENV TZ=Asia/Sh
ENV PARAMS=""

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /root

EXPOSE 9590

ADD target/sso-auth-*.jar app.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar app.jar $PARAMS" ]
