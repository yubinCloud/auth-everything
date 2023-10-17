#!/bin/bash

service=nacos-sidecar

function print() {
    echo -e "\033[46;37m $1 \033[0m"
}

print "🍀 DEPLOY ${service}"

# 构建镜像
print "🛠️ BUILDING IMAGES ${service}"
docker build . -t aet-nacos-sidecar

print "🎇 CREATE svc sidecar-jupyter-service"
kubectl apply -f ./k8s/jupyter-service.yaml
