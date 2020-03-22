# 仓库镜像

- [仓库镜像](#%e4%bb%93%e5%ba%93%e9%95%9c%e5%83%8f)
  - [简介](#%e7%ae%80%e4%bb%8b)
  - [镜像](#%e9%95%9c%e5%83%8f)
  
***

## 简介

仓库镜像将对远程仓库的请求重定向到镜像地址。

在 `${user.home}/.m2/settings.xml` 中配置镜像。例如 Maven 中心仓库的ID默认为 `centrl`，下面将其替换：

```xml
<settings>
  ...
  <mirrors>
    <mirror>
      <id>other-mirror</id>
      <name>Other Mirror Repository</name>
      <url>https://other-mirror.repo.other-company.com/maven2</url>
      <mirrorOf>central</mirrorOf>
    </mirror>
  </mirrors>
  ...
</settings>
```

## 镜像

目前国内镜像有阿里云和开源中国等。

阿里云：

```xml
<mirror>
    <id>nexus-aliyun</id>
    <name>Nexus aliyun</name>
    <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
    <mirrorOf>central</mirrorOf>
</mirror>
```

JBoss:

```xml
<mirror>
    <id>jboss-public-repository-group</id>
    <mirrorOf>central</mirrorOf>
    <name>JBoss Public Repository Group</name>
    <url>http://repository.jboss.org/nexus/content/groups/public</url>
</mirror>
```

Maven UK 仓库：

```xml
 <mirror>
    <id>ui</id>
    <name>Mirror from UK</name>
    <url>http://uk.maven.org/maven2/</url>
    <mirrorOf>central</mirrorOf>
</mirror>
```
