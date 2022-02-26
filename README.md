# netty-proxy 网络穿透工具

netty-proxy 是一款基于JAVA 语言实现的网络穿透(代理)工具。目前市面上的网络穿透工具虽然很多有收费，有免费，有安装复杂，功能过于丰富。这款基于JAVA语言开发(目前java开发者居多)，功能简洁，只支持http和tcp代理转发协议，面向开发者开发调试使用。

主要应用场景：
1. 将线上的流量转发本地电脑进行调试。
2. 前后端代码联调。

### 网络图

![alt ss](https://img-blog.csdnimg.cn/c8b370b4ecc2445e8084d17edb926535.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAeGVmZWktc29mdA==,size_20,color_FFFFFF,t_70,g_se,x_16#pic_center)


上图根据配置文件例子：
启动客户端
```
java -DdomainUser=xefei -Dhost=192.168.31.108 -Dport=8080 -jar ProxyClient-1.0-SNAPSHOT-jar-with-dependencies.jar
```
1. 访问 10.0.0.110:1202 映射到访问 192.168.31.107:8080

2. 访问 http://xefei.t.xx.com/api/cc 映射到访问 http://192.168.31.108:8080/api/cc

### 系统需求

+ JDK(1.8以上，推荐1.8)
+ Apache Maven 3.x
+ 准备一台公网机器(有域名最好没有域名就使用host文件)

## Quick start

### 工具部署

+ 方法一 下载源码,自己编译。

(1) 下载源码

```
 git clone git@github.com:dengyifei/netty-proxy.git
```

(2) 通过maven打包

(2.1) 编译服务端

```
$ cd  {netty-proxy}/ProxySever
$ mvn clean package
```

打包成功之后的jar在：
{netty-proxy}/ProxySever/target/ProxySever-1.0-SNAPSHOT-jar-with-dependencies.jar

服务端jar包部署到公网机器上。运行启动jar命令

```
java  -jar ProxySever-1.0-SNAPSHOT-jar-with-dependencies.jar

## 指定配置文件
java -DconfigPath=./proxy.yml -jar ProxySever-1.0-SNAPSHOT-jar-with-dependencies.jar
```

(2.2) 编译客户端

```
$ cd  {netty-proxy}/ProxyClient
$ mvn clean package
```
打包成功之后的jar在：
{netty-proxy}/ProxyClient/target/ProxyClient-1.0-SNAPSHOT-jar-with-dependencies.jar


客户端jar包部署到内网机器上，也可以本机电脑。运行启动jar命令

```
## 默认配置文件
java  -jar ProxyClient-1.0-SNAPSHOT-jar-with-dependencies.jar

## 指定配置
java -DconfigPath=./proxy.yml -DdomainUser=hefei -Dhost=127.0.0.1 -Dport=8080 -jar ProxyClient-1.0-SNAPSHOT-jar-with-dependencies.jar

-DconfigPath=配置文件目录
-DdomainUser=客户端登陆用户名
-Dhost=目标主机
-Dhost=目标端口
```

+ 方法二 直接下载对应的jar包。



### 配置文件

默认配置文件proxy.yml,可以通过启动命令指定：

```
java -DconfigPath=./proxy.yml -jar xx.jar
```

+ 服务端配置文件
```
# 传输通道-服务端
transmitServer:
  port: 5000
  maxFrameLength: 5242880
# http 代理服务
proxyHttpServer:
  port: 9000
  maxContentLength: 5242880
# tcp 代理服务
proxyTcpServer:
    # 代理端口
  - port: 1201
    # 客户端的登陆用户名
    userName: hefei
    # 目标主机
    targetHost: 192.168.1.107
    # 目标服务端口
    targetPort: 8080
    maxContentLength: 5242880
  - port: 1202
    userName: xefei
    targetHost: 192.168.31.107
    targetPort: 8080
    maxContentLength: 5242880
  - port: 1203
    userName: xefei
    targetHost: 192.168.31.108
    targetPort: 22
    maxContentLength: 5242880
```

+ 客户端配置文件

```
# 传输通道-客户端
proxyTransmitClient:
  # 传输通道的服务端地址，即上面配置的transmitServer
  host: 127.0.0.1
  port: 5000
  # 客户端的登陆用户名(可以通过启动命令指定，见下面)
  loginName: efei
http 代理服务(可以通过启动命令指定，见下面)
proxyHttpClient:
  # 目标服务
  host: 192.168.0.100
  # 目标端口
  port: 81
```

```
java -DdomainUser=hefei -Dhost=127.0.0.1 -Dport=8080 -jar ProxyClient-1.0-SNAPSHOT-jar-with-dependencies.jar
-DdomainUser=客户端登陆用户名
-Dhost=目标主机
-Dhost=目标端口
```

### http代理原理

netty-proxy提供更加灵活的http内网穿透到本机访问，通过启动命令就可使用，不需要在服务端复杂的配置。
+ 原理
netty-proxy根据 http协议中host 信息进行转发。详细操作流程如下：

1. 配置公网机器域名解析： 假设: *.z.xx.com 域名>>指向>> 公网机器IP。没有域名解析那就在自己本机上host文件设置一条解析，如： xefei.z.xx.com >>指向>>公网机器IP.

注意:xefei就是第二点启动参数-DdomainUser 一致。

2. 启动客户端
```
## 假设以下启动命令
java -DdomainUser=xefei -Dhost=192.168.31.108 -Dport=8080 -jar ProxyClient-1.0-SNAPSHOT-jar-with-dependencies.jar
```

3. 启动完成之后

访问 http://xefei.z.xx.com:9000/api/cc/get 就可以访问到http://192.168.31.108:8080/api/cc/get

