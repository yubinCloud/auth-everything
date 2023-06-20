FROM redis:6.2.6

COPY ./wait-for-it.sh /root/wait-for-it.sh
RUN chmod +x /root/wait-for-it.sh

COPY ./redis.conf /usr/local/etc/redis/redis.conf

CMD [ "redis-server", "/usr/local/etc/redis/redis.conf" ]
