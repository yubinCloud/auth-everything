# Avue-data 大屏 node 后端

## 本地启动方式

```sh
#导入数据库文件
sql/temp.sql

#修改数据库链接和阿里云OSS
db/config.js

#安装依赖
npm install

#运行主文件
node app.js
```

## Docker 运行

+ 首先在 MySQL 中导入 `sql/temp.sql` 文件后。
+ 在 `docker-compose.yml` 的 environment 中修改相应的数据库配置。
+ 运行 `docker-compose up`。
