# Maven Install 插件

- [Maven Install 插件](#maven-install-插件)
  - [简介](#简介)
  - [Goals](#goals)
  - [使用](#使用)
    - [install:install](#installinstall)
    - [install:install-file](#installinstall-file)
  - [参考](#参考)

2021-07-06, 18:57
***

## 简介

Install 插件在 Maven 的 `install` 阶段将打包好的 artifact 添加到本地仓库。Install 插件使用 POM 信息（groudId, artifatId, version）确定 artifact 存储位置。

本地仓库（local repository）默认位于用户主目录 `(~/.m2/repository)`，可以在 `~/.m2/settings.xml` 文件中使用 `<localRepository>` 元素设置本地仓库位置。

## Goals

Install 插件包含 3 个 goals:

|Goal|功能|
|---|---|
|`install:install`|用于自动安装项目的主要 artifact（JAR）、对应的 POM 以及附加的 artifact（sources, javadoc 等）|
|`install:install-file`|用于将外部创建的 artifact 及其 POM 安装到本地仓库|
|`install:help`|显示帮助信息|

## 使用

Maven 采用一个二级策略解析和分发 artifact。

第一级为本地仓库（local repository），为本地缓存的 artifact。当 Maven 执行时，它会首先尝试在本地缓存中查找 artifacts，如果在本地找不到，再从远程仓库查找。如果在远程仓库找到，就将其下载保存到本地仓库。

而 `maven-install-plugin` 插件的工作，是将你的 artifact 保存到本地仓库，要将 artifact 上传到远程仓库，使用 `maven-deploy-plugin`。

### install:install

`install:install` 基本不需要额外配置，在默认的构建周期中，该插件根据 POM 自动在 `install` 阶段将 artifact 添加到本地仓库。

### install:install-file

将 artifact 文件添加到本地仓库，一般用于添加不是由 Maven 构建的 artifacts。支持的参数如下：

```shell
mvn install:install-file -Dfile=your-artifact-1.0.jar \
                         [-DpomFile=your-pom.xml] \
                         [-Dsources=src.jar] \
                         [-Djavadoc=apidocs.jar] \
                         [-DgroupId=org.some.group] \
                         [-DartifactId=your-artifact] \
                         [-Dversion=1.0] \
                         [-Dpackaging=jar] \
                         [-Dclassifier=sources] \
                         [-DgeneratePom=true] \
                         [-DcreateChecksum=true]
```

## 参考

- https://maven.apache.org/plugins/maven-install-plugin/
