FROM mysql:8.0.32

WORKDIR /docker-entrypoint-initdb.d

ENV LANG=C.UTF-8

ADD ./initdb/*.sql ./
