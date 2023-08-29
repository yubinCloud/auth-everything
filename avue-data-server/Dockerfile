FROM keymetrics/pm2:18-alpine

EXPOSE 10002

# Bundle APP files
WORKDIR /src/
COPY ./ .

# Install app dependencies
ENV NPM_CONFIG_LOGLEVEL warn
RUN npm config set registry=https://registry.npmmirror.com/ && \
    npm install


CMD [ "pm2-runtime", "start", "pm2.json" ]
