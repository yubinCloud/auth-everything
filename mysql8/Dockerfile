FROM mysql:8.0.32

ENV LANG=C.UTF-8

ADD ./initdb/*.sql /docker-entrypoint-initdb.d/

COPY ./wait-for-it.sh /root/wait-for-it.sh
RUN chmod +x /root/wait-for-it.sh
