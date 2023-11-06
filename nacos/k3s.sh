#!/bin/bash

service=nacos

function print() {
    echo -e "\033[46;37m $1 \033[0m"
}

print "🍀 DEPLOY ${service}"

# 构建镜像
print "🛠️ BUILDING IMAGES ${service}"
docker build . -t aet-nacos

# 创建 nacos env 的 configmap
print "🎇 CREATE configmap nacos-env-cm"
kubectl create configmap nacos-env-cm --from-env-file=./nacos.env -n aet

# 创建 nacos 的 Deployment 和 Service
print "🎇 CREATE svc ${service}"
kubectl apply -f ./k8s/nacos-svc.yaml
