#!/bin/bash

service=af-postgresql

function print() {
    echo -e "\033[46;37m $1 \033[0m"
}

print "🍀 DEPLOY ${service}"

# 创建用于挂载 PG 数据的 pvc
print "🎇 CREATE pvc ${service}"
kubectl apply -f ./k8s/af-pg-pvc.yaml

# 创建 mysql8 的 StatefulSet 和 Service
print "🎇 CREATE svc ${service}"
kubectl apply -f ./k8s/af-pg-svc.yaml
