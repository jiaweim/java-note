# Apache Maven Assembly Plugin

- [Apache Maven Assembly Plugin](#apache-maven-assembly-plugin)
  - [简介](#%e7%ae%80%e4%bb%8b)
    - [Assembly](#assembly)
  - [Goals](#goals)
  - [使用](#%e4%bd%bf%e7%94%a8)

***

## 简介

Assembly 插件主要用于将项目输出及其依赖项、模块、文档和其它文件打包为一个可分发压缩包。

使用预定义的打包描述符（descriptor）打包很方便。

支持格式：

- zip
- tar
- tar.gz (or tgz)
- tar.bz2 (or tbz2)
- tar.snappy
- tar.xz (or txz)
- jar
- dir
- war
- ArchiveManager 配置的任意其它格式

### Assembly

Assembly 是打包在一起的一组文件、目录和依赖项的集合。例如，假设一个 Maven 项目定义了一个 JAR 包，该 JAR 包含一个 console 应用

## Goals

该插件包含两个 Goals：

|Goal|功能|
|---|---|
|`assembly:help`|显示 maven-assembly-plugin 的帮助信息。使用 `mvn assembly:help -Ddetail=true -Dgoal=<goal-name>` 显示详细参数|
|`assembly:single`|根据打包符打包。该 goal既可以和 lifecycle 绑定，也可以直接从命令行调用|

## 使用

