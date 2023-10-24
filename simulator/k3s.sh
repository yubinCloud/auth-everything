#!/bin/bash

service=simulator

function print() {
    echo -e "\033[46;37m $1 \033[0m"
}

print "🍀 DEPLOY ${service}"

# 构建镜像
print "🛠️ BUILDING IMAGES ${service}"
docker build . -t aet-simulator

# 创建 svc
print "🎇 CREATE svc ${service}"
kubectl apply -f ./k8s/simulator-svc.yaml
