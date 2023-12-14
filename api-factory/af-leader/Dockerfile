FROM openjdk:21

ENV TZ=Asia/Sh
ENV PARAMS=""
RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
WORKDIR /root
EXPOSE 8979
ADD build/libs/af-leader-*-SNAPSHOT.jar app.jar
CMD [ "sh", "-c", "java $JAVA_OPTS -jar app.jar $PARAMS" ]