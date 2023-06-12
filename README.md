# auth-everything

<font color=red>开发中...</font>

为一切后端加入认证授权机制，而后端不需要做任何改动！

## 项目描述

当完成一个后端项目后，为了使其上线，我们往往需要加入认证授权机制来保护后端的访问，但这个认证授权机制需要我们花费很多时间去完成，甚至很多后端系统难以改动。

本项目通过引入一个 API 网关和单点登录服务，使得由单点登录服务执行认证授权，由 API 网关完成权限校验，并将我们的后端系统作为一个服务被 API 网关保护起来，从而实现 auth everything。

因此，借助于本项目，当你想为一个后端系统加入认证授权机制的话，你所需要的做就是：

1. 将其打包为 Docker
2. 在 docker-compose.yml 中添加一个 service
3. 在 Gateway 中加入一条转发规则

All finished!

## 技术栈

- Spring Boot 3.1
- Spring Cloud Gateway
- [Sa Token](https://sa-token.dev33.cn/)
- [Nacos](https://nacos.io/)
- MySQL 8
- Vue 3
- docker-compose
