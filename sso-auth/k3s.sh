#!/bin/bash

service=sso-auth

function print() {
    echo -e "\033[46;37m $1 \033[0m"
}

print "🍀 DEPLOY ${service}"

# 构建镜像
print "🛠️ BUILDING IMAGES ${service}"
docker build . -t aet-auth

# 创建 gateway 的 Service
print "🎇 CREATE svc ${service}"
kubectl apply -f ./k8s/sso-auth-svc.yaml
