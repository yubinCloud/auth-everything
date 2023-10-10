#!/bin/bash

# 从 my.cnf 创建 ConfigMap
kubectl create configmap mysql8-config-cm --from-file=./volume/cnf/my.cnf -n aet

# 创建 mysql8 的 StatefulSet 和 Service
kubectl apply -f mysql8-svc.yaml