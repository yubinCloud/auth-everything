FROM local/avue-base:v1.0

EXPOSE 10002

WORKDIR /src/
COPY ./avue-data-server/ ./


CMD [ "pm2-runtime", "start", "pm2.json" ]
