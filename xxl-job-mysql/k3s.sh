#!/bin/bash

service=xxx-job-mysql

function print() {
    echo -e "\033[46;37m $1 \033[0m"
}

print "🍀 DEPLOY ${service}"

# 构建镜像
print "🛠️ BUILDING IMAGES ${service}"
docker build . -t aet-xxl-job-mysql

# 从 my.cnf 创建 ConfigMap
print "🎇 CREATE configmap ${service}"
kubectl create configmap xxl-job-mysql-config-cm --from-file=./volume/conf/my.cnf -n aet
sleep 2

# 创建用于挂载 MySQL 数据的 pvc
print "🎇 CREATE pvc ${service}"
kubectl apply -f ./k8s/xxl-job-mysql-data-pvc.yaml
sleep 5

# 创建 Service
print "🎇 CREATE svc ${service}"
kubectl apply -f ./k8s/xxl-job-mysql-svc.yaml
