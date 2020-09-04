# Apache Maven Compiler Plugin

- [Apache Maven Compiler Plugin](#apache-maven-compiler-plugin)
  - [简介](#简介)
  - [指定 `-source` 和 `-target`](#指定--source-和--target)

2020-09-02, 08:46
@jiaweiM
***

## 简介

Compiler 插件用于编译项目源码。从 3.0 开始，默认编译器为 `javax.tools.JavaCompiler` （如果使用 Java 1.6）。可以使用 `forceJavaCompilerUse` 选项强制使用 `javac` 编译。

默认 `source`和 `target` 是 1.6，和你使用的 JDK 互相独立。强烈推荐修改该默认设置，将 `source` 和 `target` 修改和你使用的 JDK 对应。

## 指定 `-source` 和 `-target`
