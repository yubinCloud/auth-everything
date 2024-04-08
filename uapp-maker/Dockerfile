FROM openjdk:21

ENV TZ=Asia/Sh
ENV PARAMS=""
RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
WORKDIR /root
EXPOSE 8228
ADD target/uapp-maker-*.jar app.jar

CMD ["sh", "-c", "java -jar $JAVA_OPTS app.jar $PARAMS"]