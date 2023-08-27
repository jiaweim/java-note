# 安装

2023-08-16, 16:04
****
## 前提条件

Gradle 需要 Java JDK 8 及以上版本才能运行，因此需要先安装 JDK，检查 java 版本：

```bash
>java -version
openjdk version "17.0.8" 2023-07-18
OpenJDK Runtime Environment Temurin-17.0.8+7 (build 17.0.8+7)
OpenJDK 64-Bit Server VM Temurin-17.0.8+7 (build 17.0.8+7, mixed mode, sharing)
```

## 其它资源

- [免费的在线培训](https://gradle.com/training/)
- 除了[文档](https://docs.gradle.org/current/userguide/userguide.html)，还提供各种语言中的使用[教程](https://gradle.org/guides/)
- Gradle 有一个可视化构建检查工具 [Build Scan](https://scans.gradle.com/)
- 最后，[Gradle 简报](https://newsletter.gradle.org/)是了解最近功能的好方法，每个月都有 issue

## 使用 package manager 安装

[SDKMAN](http://sdkman.io/) 是大多数基于 Unix 系统上管理软件的工具。

```sh
$ sdk install gradle 8.2.1
```

## 手动安装

1. [下载](https://gradle.org/releases/)最新版的 Gradle

目前最新版为 v8.2.1，2023.07.10 发布。zip 分发文件有两种：

- binary-only
- complete, 包含文档和源码

2. 解压 zip
3. 设置环境变量
4. 验证

```
C:\Users\jiawe>gradle -v

Welcome to Gradle 8.2.1!

Here are the highlights of this release:
 - Kotlin DSL: new reference documentation, assignment syntax by default
 - Kotlin DSL is now the default with Gradle init
 - Improved suggestions to resolve errors in console output
 - Reduced sync memory consumption

For more details see https://docs.gradle.org/8.2.1/release-notes.html


------------------------------------------------------------
Gradle 8.2.1
------------------------------------------------------------

Build time:   2023-07-10 12:12:35 UTC
Revision:     a38ec64d3c4612da9083cc506a1ccb212afeecaa

Kotlin:       1.8.20
Groovy:       3.0.17
Ant:          Apache Ant(TM) version 1.10.13 compiled on January 4 2023
JVM:          17.0.8 (Eclipse Adoptium 17.0.8+7)
OS:           Windows 11 10.0 amd64
```

## 参考

- https://gradle.org/install/

