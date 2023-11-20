#!/bin/bash

mkdir -p ./build/backup

# *************************************
# 备份 mysql8
# *************************************

mdkir ./build/backup/mysql8
docker exec -it aet-mysql8 /bin/bash
