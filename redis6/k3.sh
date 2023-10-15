#!/bin/bash

service=redis6

function print() {
    echo -e "\033[46;37m $1 \033[0m"
}

print "🍀 DEPLOY ${service}"

# 构建镜像
print "🛠️ BUILDING IMAGES ${service}"
docker build . -t aet-redis6

# 创建 mysql8 的 StatefulSet 和 Service
print "🎇 CREATE svc ${service}"
kubectl apply -f ./k8s/redis6-svc.yaml
