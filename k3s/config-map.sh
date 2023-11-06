#!/bin/bash

# 从公共的 .env 创建 public-env
kubectl create configmap public-env --from-env-file=../.env -n aet