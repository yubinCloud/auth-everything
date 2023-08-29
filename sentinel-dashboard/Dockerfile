FROM openjdk:11

ENV TZ=Asia/Sh

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /root

EXPOSE 8080

COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

ADD sentinel.jar.protected app.jar

CMD [ "sh", "-c", "java -Dauth.enabled=false -jar app.jar --server.servlet.context-path=/sentinel" ]