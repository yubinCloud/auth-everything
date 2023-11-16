#!/bin/bash

mkdir ./backup

# *************************************
# 备份 mysql8
# *************************************

mdkir ./backup/mysql8
docker exec -it aet-mysql8 /bin/bash
