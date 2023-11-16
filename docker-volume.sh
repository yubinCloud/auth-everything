#!/bin/bash

# ************************** 使用说明 ************************************
# docker volume 创建脚本：
# - `sh docker-volume.sh create`: 创建所有所需的 volume，会跳过已创建的 volume
# - `sh docker-volume.sh delete`: 删除所有 volume
# ***********************************************************************

FILE_NAME=$0

OPS=$1

VOLUME_LIST=("aet-euser-postgresql-data" "mysql8-data")

function help() {
    echo -e "Usage: \033[33m $FILE_NAME <ops> \033[0m"
    echo "<ops> list:"
    echo "  help: look up all options."
    echo "  create: create all volumes."
    echo "  delete: delete all volumes."
    exit
}

function printCreate() { 
    echo -e "\033[42;37m 📂 CREATE VOLUME $1 \033[0m"
}

function printDelete() {
    echo -e "\033[41;37m 📂 CREATE VOLUME $1 \033[0m"
}

function createOneVolume() {
    VOLUME_NAME=$1
    VOLUME_NUM=$(docker volume ls | grep -c "${VOLUME_NAME}")
    if [ "${VOLUME_NUM}" -lt 1 ]; then
        printCreate "${VOLUME_NAME}"
        docker volume create "${VOLUME_NAME}"
    fi
}

function createVolumes() {
    for VOLUME in "${VOLUME_LIST[@]}"
    do
        createOneVolume "${VOLUME}"
    done
}

function deleteOneVolume() {
    VOLUME_NAME=$1
    VOLUME_NUM=$(docker volume ls | grep -c "${VOLUME_NAME}")
    if [ "${VOLUME_NUM}" -ge 1 ]; then
        printDelete "${VOLUME_NAME}"
        docker volume rm "${VOLUME_NAME}"
    fi
}

function deleteVolumes() {
    for VOLUME in "${VOLUME_LIST[@]}"
    do
        deleteOneVolume "${VOLUME}"
    done
}


case $OPS in
    "help") help;;
    "create") createVolumes;;
    "delete") deleteVolumes;;
esac