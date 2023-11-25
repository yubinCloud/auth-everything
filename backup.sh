#!/bin/bash

mkdir -p ./build/backup

# *************************************
# 备份 mysql8
# *************************************

mdkir ./build/backup/mysql8
docker exec -it aet-mysql8 mysqldump -uroot -proot --databases sso_auth > ./build/backup/mysql8/sso_auth.sql
docker exec -it aet-mysql8 mysqldump -uroot -proot --databases antv_data_server > ./build/backup/mysql8/antv_data_server.sql
docker exec -it aet-xxl-job-mysql mysqldump -uroot -proot --databases xxl-job-mysql > ./build/backup/mysql8/xxl-job-mysql.sql
