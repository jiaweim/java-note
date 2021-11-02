# 概述

要将 jar 发布到远程 maven 仓库，需要在 pom.xml 文件中配置 repository 地址，并在 settings.xml 中添加验证信息。

maven 使用 **Apache Maven Deploy Plugin** 执行部署任务，该插件具有两个功能：
- 通过 `deploy:deploy` 实现构建生命周期的 `deploy` 阶段
- 通过 `deploy:deploy-file` 可以将非 Maven 构建的包部署到远程仓库

# deploy:deploy
该功能在构建生命周期的 `deploy` 执行。

要使用该功能，首先要在 POM 文件中定义 `<distributionManagement/>`，并至少在其中定义`<repository/>`提供远程仓库的位置信息。为了区分 release 和 snapshot，还可以指定 `<snapshotRepository/>` 的位置。如果要部署项目网站，在这部分还需要指定 `<site>/` 部分。

例如：
```xml
[...]
  <distributionManagement>
    <repository>
      <id>internal.repo</id>
      <name>MyCo Internal Repository</name>
      <url>Host to Company Repository</url>
    </repository>
  </distributionManagement>
[...]
```

如果是保密仓库，还需要在 settings.xml 定义验证信息。如下所示：
```xml
[...]
    <server>
      <id>internal.repo</id>
      <username>maven</username>
      <password>foobar</password>
    </server>
[...]
```