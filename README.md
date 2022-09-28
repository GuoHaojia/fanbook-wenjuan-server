## 前言

##### 1. 导入数据库 目前只支持mysql5.7及以上版本
创建数据名为`tduck`
下载`tduck-v3.sql`文件 并且导入创建的数据中

##### 2. 下载项目jar文件
下载项目jar包文件`tduck-api.jar`到本地

##### 3.修改配置并启动项目
下载`application-custom.yml `配置文件
把下载的jar包和配置文件放在同一个目录下
修改为自己的配置，主要修改位置为数据库以及文件存储配置

##### 4.运行项目
运行如下命令启动项目
`java -Dfile.encoding=UTF-8 -jar tduck-api.jar --spring.profiles.active=custom`
出现如下提示代表启动完成

##### 5.访问项目
浏览器地址栏输入http://localhost:8999/
默认账号密码：test@tduck.com/12356789
