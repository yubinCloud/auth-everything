#!/bin/bash

service=avue-data-server

function print() {
    echo -e "\033[46;37m $1 \033[0m"
}

print "🍀 DEPLOY ${service}"

# 创建 PVC
print "🎇 CREATE pvc ${service}"
kubectl apply -f ./k8s/af-etcd-data-pvc.yaml
sleep 3

# 创建 svc
print "🎇 CREATE svc ${service}"
kubectl apply -f ./k8s/af-etcd-svc.yaml
