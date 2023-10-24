#!/bin/bash

service=af-apisix

function print() {
    echo -e "\033[46;37m $1 \033[0m"
}

print "🍀 DEPLOY ${service}"

# 从 config.yaml 创建 ConfigMap
print "🎇 CREATE configmap ${service}"
kubectl create configmap af-apisix-config-cm --from-file=./apisix_conf/config.yaml -n aet

# 创建 APISIX 的 svc
print "🎇 CREATE svc ${service}"
kubectl apply -f ./k8s/af-apisix-svc.yaml

##########################################################

service=af-apisix-dashboard

# 从 conf.yaml 创建 ConfigMap
print "🎇 CREATE configmap ${service}"
kubectl create configmap af-apisix-dashboard-config-cm --from-file=./dashboard_conf/conf.yaml -n aet

# 创建 apisix-dashboard 的 svc
print "🎇 CREATE svc ${service}"
kubectl apply -f ./k8s/af-apisix-dashboard-svc.yaml