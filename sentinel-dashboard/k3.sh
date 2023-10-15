#!/bin/bash

service=sentinel-dashboard

function print() {
    echo -e "\033[46;37m $1 \033[0m"
}

print "🍀 DEPLOY ${service}"

# 构建镜像
print "🛠️ BUILDING IMAGES ${service}"
docker build . -t aet-sentinel-dashboard

# 创建 mysql8 的 StatefulSet 和 Service
print "🎇 CREATE svc ${service}"
kubectl apply -f ./k8s/sentinel-dashboard-svc.yaml
