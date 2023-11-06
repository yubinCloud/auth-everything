#!/bin/bash

service=ds-worker

function print() {
    echo -e "\033[46;37m $1 \033[0m"
}

print "🍀 DEPLOY ${service}"

# 构建镜像
print "🛠️ BUILDING IMAGES ${service}"
docker build . -t aet-ds-worker

# 创建 gateway 的 Service
print "🎇 CREATE svc ${service}"
kubectl apply -f ./k8s/ds-worker-svc.yaml
