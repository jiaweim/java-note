# Gradle Wrapper

## 简介

Gradle Wrapper 时执行 Gradle build 的推荐方式。Wrapper 是一个脚本，它调用声明的 Gradle 版本，必须时下载 Gradle。


Wrapper 功能：

- 在给定 Gradle 版本上标准化项目，使构建更加可靠和健壮
- 为不同用户和执行环境提供新的 Gradle 版本，只需更改 Wrapper 定义

Wrapper 的使用有三种不同的工作流程：

- 创建一个新的 Gradle 项目，为其添加 Wrapper
- 使用已有的 Wrapper 运行项目
- 更新 wrapper 到新版本的 Gradle

下面依次介绍这三种工作流。

## 添加 Gradle Wrapper

