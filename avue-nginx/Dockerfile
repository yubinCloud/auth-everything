FROM nginx:1.25-bookworm

# 用于给 avue-data-server 导出文件
EXPOSE 8095

COPY nginx.conf /etc/nginx/nginx.conf

RUN mkdir /opt/exported-files
