FROM openjdk:17

ENV TZ=Asia/Sh
ENV PARAMS=""
RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
WORKDIR /root
EXPOSE 9592
ADD target/euser-sso-*.jar app.jar
CMD [ "sh", "-c", "java $JAVA_OPTS -jar app.jar $PARAMS" ]