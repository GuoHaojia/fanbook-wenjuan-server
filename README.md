# 后端应用打包

详细打包流程见 .github/workflow/build-compiler-main.yml

## 1、jdk版本
+ Java version: 1.8.0_332, vendor: Oracle Corporation, runtime: /usr/local/openjdk-8/jre

## 2、mvn版本
+ Apache Maven 3.6.3

## 3、mysql版本
+ 8.0.29-0
+ utf8mb4
+ docker/init-db/dev_tduck.sql

## 4、redis版本
+ redis:6.2.5

## 5、打包配置
+ 基础配置: tduck-api/src/main/resources/application.yaml
+ 环境dev: tduck-api/src/main/resources/application-dev.yaml

## 系统版本
+ ubuntu0.20.04.3

## 6、打包步骤
```shell
mvn clean install -DskipTests
cd tduck-api
sed -i 's#local#dev#g' src/main/resources/application.yml
mvn clean package -DskipTests
```

# 后端应用部署
+ helm包：helm
+ docker镜像：tduck-api/Dockerfile
