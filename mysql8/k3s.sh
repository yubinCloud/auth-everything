#!/bin/bash

service=mysql8

function print() {
    echo -e "\033[46;37m $1 \033[0m"
}

print "🍀 DEPLOY ${service}"

# 构建镜像
print "🛠️ BUILDING IMAGES ${service}"
docker build . -t aet-mysql8

# 从 my.cnf 创建 ConfigMap
print "🎇 CREATE configmap ${service}"
kubectl create configmap mysql8-config-cm --from-file=./volume/conf/my.cnf -n aet

# 创建用于挂载 MySQL 数据的 pvc
print "🎇 CREATE pvc ${service}"
kubectl apply -f ./k8s/mysql8-data-pvc.yaml

# 创建 mysql8 的 StatefulSet 和 Service
print "🎇 CREATE svc ${service}"
kubectl apply -f ./k8s/mysql8-svc.yaml
