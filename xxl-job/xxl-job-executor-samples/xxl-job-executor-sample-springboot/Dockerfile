FROM openjdk:11


EXPOSE 8081
EXPOSE 9999

ENV PARAMS=""

ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN sed -i '$a\export PATH=/opt/miniconda/bin:$PATH' /etc/profile

ADD target/xxl-job-executor-sample-springboot-*.jar /app.jar

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /app.jar $PARAMS"]
