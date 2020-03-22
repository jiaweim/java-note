# Apache Maven Assembly Plugin

- [Apache Maven Assembly Plugin](#apache-maven-assembly-plugin)
  - [简介](#%e7%ae%80%e4%bb%8b)
  - [描述符](#%e6%8f%8f%e8%bf%b0%e7%ac%a6)
    - [assembly](#assembly)
  - [预定义描述符](#%e9%a2%84%e5%ae%9a%e4%b9%89%e6%8f%8f%e8%bf%b0%e7%ac%a6)
    - [bin](#bin)
    - [Assembly](#assembly-1)
  - [Goals](#goals)
  - [使用](#%e4%bd%bf%e7%94%a8)

***

## 简介

程序集（assembly）插件用于将项目输出、将其依赖项、模块、文档和其它文件打包为一个可分发的压缩包。

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

## 描述符

Assembly 插件根据指定的描述符执行。其包含的预定义描述符可以满足一些常见的打包需求。理解打包描述符后还能灵活的使用 assembly 插件自定义打包。

assembly描述符指定压缩包类型、内容以及指定打包依赖项的方式。

```xml
<assembly xmlns="http://maven.apache.org/ASSEMBLY/2.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/ASSEMBLY/2.0.0 http://maven.apache.org/xsd/assembly-2.0.0.xsd">
  <id/>
  <formats/>
  <includeBaseDirectory/>
  <baseDirectory/>
  <includeSiteDirectory/>
  <containerDescriptorHandlers>
    <containerDescriptorHandler>
      <handlerName/>
      <configuration/>
    </containerDescriptorHandler>
  </containerDescriptorHandlers>
  <moduleSets>
    <moduleSet>
      <useAllReactorProjects/>
      <includeSubModules/>
      <includes/>
      <excludes/>
      <sources>
        <useDefaultExcludes/>
        <outputDirectory/>
        <includes/>
        <excludes/>
        <fileMode/>
        <directoryMode/>
        <fileSets>
          <fileSet>
            <useDefaultExcludes/>
            <outputDirectory/>
            <includes/>
            <excludes/>
            <fileMode/>
            <directoryMode/>
            <directory/>
            <lineEnding/>
            <filtered/>
          </fileSet>
        </fileSets>
        <includeModuleDirectory/>
        <excludeSubModuleDirectories/>
        <outputDirectoryMapping/>
      </sources>
      <binaries>
        <outputDirectory/>
        <includes/>
        <excludes/>
        <fileMode/>
        <directoryMode/>
        <attachmentClassifier/>
        <includeDependencies/>
        <dependencySets>
          <dependencySet>
            <outputDirectory/>
            <includes/>
            <excludes/>
            <fileMode/>
            <directoryMode/>
            <useStrictFiltering/>
            <outputFileNameMapping/>
            <unpack/>
            <unpackOptions>
              <includes/>
              <excludes/>
              <filtered/>
              <lineEnding/>
              <useDefaultExcludes/>
              <encoding/>
            </unpackOptions>
            <scope/>
            <useProjectArtifact/>
            <useProjectAttachments/>
            <useTransitiveDependencies/>
            <useTransitiveFiltering/>
          </dependencySet>
        </dependencySets>
        <unpack/>
        <unpackOptions>
          <includes/>
          <excludes/>
          <filtered/>
          <lineEnding/>
          <useDefaultExcludes/>
          <encoding/>
        </unpackOptions>
        <outputFileNameMapping/>
      </binaries>
    </moduleSet>
  </moduleSets>
  <fileSets>
    <fileSet>
      <useDefaultExcludes/>
      <outputDirectory/>
      <includes/>
      <excludes/>
      <fileMode/>
      <directoryMode/>
      <directory/>
      <lineEnding/>
      <filtered/>
    </fileSet>
  </fileSets>
  <files>
    <file>
      <source/>
      <outputDirectory/>
      <destName/>
      <fileMode/>
      <lineEnding/>
      <filtered/>
    </file>
  </files>
  <dependencySets>
    <dependencySet>
      <outputDirectory/>
      <includes/>
      <excludes/>
      <fileMode/>
      <directoryMode/>
      <useStrictFiltering/>
      <outputFileNameMapping/>
      <unpack/>
      <unpackOptions>
        <includes/>
        <excludes/>
        <filtered/>
        <lineEnding/>
        <useDefaultExcludes/>
        <encoding/>
      </unpackOptions>
      <scope/>
      <useProjectArtifact/>
      <useProjectAttachments/>
      <useTransitiveDependencies/>
      <useTransitiveFiltering/>
    </dependencySet>
  </dependencySets>
  <repositories>
    <repository>
      <outputDirectory/>
      <includes/>
      <excludes/>
      <fileMode/>
      <directoryMode/>
      <includeMetadata/>
      <groupVersionAlignments>
        <groupVersionAlignment>
          <id/>
          <version/>
          <excludes/>
        </groupVersionAlignment>
      </groupVersionAlignments>
      <scope/>
    </repository>
  </repositories>
  <componentDescriptors/>
</assembly>
```

### assembly

assembly 定义从项目生成的文件集合，并打包为一个压缩文件，如 zip, tar 或 tar.gz 等。

|元素|类型|功能|
|---|---|---|
|`id`|`String`|assembly id。除了用在归档文件命名外，还可以用作部署时的分类器|
|`formats/format*`|`List<String>`|设置 assembly 格式。不过在 goal 参数处指定更合适|

## 预定义描述符

Assembly 插件包含四个预定义的描述符。它们的 ID 分别为：

- bin，创建项目的二进制分发存档
- jar-with-dependencies，
- src
- project

### bin

使用 `bin` 作为 `descriptorRef` 的参数创建二进制分发存档。该描述符支持三种存档格式：tar.gz, tar.bz2 和 zip。


### Assembly

Assembly 是打包在一起的一组文件、目录和依赖项的集合。

Assembly 插件根据的 assembly 描述符执行。



## Goals

该插件包含两个 Goals：

|Goal|功能|
|---|---|
|`assembly:help`|显示 maven-assembly-plugin 的帮助信息。使用 `mvn assembly:help -Ddetail=true -Dgoal=<goal-name>` 显示详细参数|
|`assembly:single`|根据打包符打包。该 goal既可以和 lifecycle 绑定，也可以直接从命令行调用|

## 使用

