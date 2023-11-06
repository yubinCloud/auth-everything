#!/bin/bash

service=avue-data-server

function print() {
    echo -e "\033[46;37m $1 \033[0m"
}

print "🍀 DEPLOY ${service}"

# 构建镜像
print "🛠️ BUILDING IMAGES ${service}"
docker build . -t aet-avue-data-server

# 创建 PVC
print "🎇 CREATE pvc ${service}"
kubectl apply -f ./k8s/avue-exported-pvc.yaml
sleep 5
kubectl apply -f ./k8s/avue-internal-pvc.yaml
sleep 5

# 创建 svc
print "🎇 CREATE svc ${service}"
kubectl apply -f ./k8s/avue-data-server-svc.yaml
