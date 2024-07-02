#!/bin/bash

# ************************** 使用说明 ************************************
# 容器镜像构建脚本：
# - 在线环境下构建：sh docker-build.sh
# - 离线环境下构建：sh docker-build.sh -o
# 
# 指定镜像版本号：
# - 使用 -v：sh docker-build.sh -v v1.0
#
# 构建后保存镜像：
# - 使用 -s：sh docker-build.sh -s
# ***********************************************************************

FILE_NAME=$0

function help() {
    echo -e "Usage: \033[33m $FILE_NAME [-v VERSION] \033[0m"
    echo "Description:"
    echo "  VERSION, the version of all docker images."
    exit
}

function print() {
    echo -e "\033[46;37m 🛠️ BUILDING $1 \033[0m"
}

VERSION="latest"

NET_MODE="online"
SAVE_IMAGE="false"

function initSaveDir() {
    SAVE_IMAGE="true"
    mkdir -p ./build/image
}

function build() {
    MUST_ONLINE=$3
    if [ "${MUST_ONLINE}" ] ; then
        if [ ${NET_MODE} = "online" ]; then
            print "$2"
            docker build "$1" -t "$2":"${VERSION}"
        fi
    else
        print "$2"
        docker build "$1" -t "$2":"${VERSION}"
    fi

    if [ ${SAVE_IMAGE} = "true" ]; then
        docker save -o ./build/image/"$2"-"${VERSION}".tar "$2":"${VERSION}"
    fi
}


while getopts 't:ohs' OPT; do
    case $OPT in
        t) VERSION="$OPTARG";;
        o) NET_MODE="offline";;
        s) initSaveDir;;
        h) help;;
        ?) help;;
    esac
done

# 第一个参数：构建目录
# 第二个参数：image 名
# 第三个参数：无参表示离线也可以构建，参数为 1 表示必须在线构建
build "./mysql8" "aet-mysql8"
build "./xxl-job-mysql" "aet-xxl-job-mysql"
build "./redis6" "aet-redis6"
build "./euser-postgresql" "aet-euser-postgresql"
build "./sentinel-dashboard" "aet-sentinel-dashboard"
build "./nacos" "aet-nacos"
build "./gateway" "aet-gateway"
build "./sso-auth" "aet-sso-auth"
build "./xxl-job/xxl-job-admin" "aet-xxljob-admin"
build "./avue-nginx" "aet-avue-nginx"
build "./avue-data-server" "aet-avue-data-server" 1
build "./avue-helper" "aet-avue-helper" 
build "./uapp-maker" "aet-uapp-maker"
build "./ds-worker" "aet-ds-worker"
build "./ds-coordinator" "aet-ds-coordinator"
build "./simulator" "aet-simulator" 1
build "./euser-gateway" "aet-euser-gateway"
build "./euser-sso" "aet-euser-sso"
build "./api-factory/af-security-gateway" "aet-af-security-gateway"
build "./api-factory/af-leader" "aet-af-leader"
build "./api-factory/af-worker" "aet-af-worker"
build "./dataease/backend" "aet-dataease-backend"
build "./chat-analysis" "aet-chat-analysis"
build "./alibaba-sidecars/jupyter-service" "aet-sidecar-jupyter-service"
build "./alibaba-sidecars/avue-data-server" "aet-sidecar-avue-data-server"
build "./alibaba-sidecars/avue-nginx" "aet-sidecar-avue-nginx"
build "./alibaba-sidecars/nacos-sidecar" "aet-nacos-sidecar"