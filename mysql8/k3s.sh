#!/bin/bash

# 构建镜像
echo "🛠️ building images..."
docker build . -t aet-mysql8

# 从 my.cnf 创建 ConfigMap
echo "🎇 creating mysql8 configmap..."
kubectl create configmap mysql8-config-cm --from-file=./volume/conf/my.cnf -n aet

# 创建 mysql8 的 StatefulSet 和 Service
echo "🎇 creating mysql8 svc..."
kubectl apply -f mysql8-svc.yaml -n aet
