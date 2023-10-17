#!/bin/bash

service=avue-nginx

function print() {
    echo -e "\033[46;37m $1 \033[0m"
}

print "🍀 DEPLOY ${service}"

# 构建镜像
print "🛠️ BUILDING IMAGES ${service}"
docker build . -t aet-avue-nginx

# 创建 deploy 和 svc
print "🎇 CREATE svc ${service}"
kubectl apply -f ./k8s/avue-nginx-svc.yaml
