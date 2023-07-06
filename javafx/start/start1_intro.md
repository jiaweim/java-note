# JavaFX 概述

2023-07-04, 23:33
****
## 1. JavaFX 框架

JavaFX 是继 AWT 和 Swing 后，Java 平台的下一代开源 GUI 框架。其主要特性有：

- 纯 Java 编写
- 支持数据绑定
- 可以使用任何 JVM 脚本语言编写，如 Kotlin, Groovy 和 Scala
- JavaFX 提供两种构建 UI 的方式：Java 代码或 FXML。FXML 是一个基于 XML 的标记语言，以声明的方式定义UI。Scene Builder 提供以可视化的方式编辑 FXML 文件。
- 支持多媒体
- 可以内嵌网页
- 提供了开箱即用的特效和动画，这对开发游戏很重要，通过几行代码就可以实现复杂的动画

JavaFX 框架如下所示：

![](Pasted%20image%2020230704214212.png)

JavaFX 的 GUI 被构造为 Scene graph。Scene graph 是可视化元素的集合，这些可视化元素称为 node，以分层方式排列。scene graph 中的 nodes 可以处理用户输入和手势，支持特效、变换和状态。Node 类型包括简单的 UI 控件，如 Button、TextField、2D 和 3D Shapes, Image 以及 Media (audio 和 video)、Web 内容、Chart。

JavaFX 核心 API 主要包括四个部分：

| 组件                    | 说明                                                                                                                                      |
| ----------------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| Prism                   | 渲染 scene graph 的图形引擎，如果硬件加速渲染不可用（Windows上的 DirectX, Mac Linux 上 的 OpenGL），则使用 Java2D                              |
| Glass Windowing Toolkit | 提供窗口管理、时间等服务，同时负责管理事件队列。JavaFX 由操作系统级线程 JavaFX 应用线程管理事件队列，所有输入事件和 UI 修改都在该线程上调度      |
| Media Engine            | 为JavaFX提供多媒体支持，media engine 使用单独的线程处理 media frames，使用 JavaFX 应用线程进行同步。media engine 是基于开源的 GStreamer 框架构建 |
| Web Engine              | 用于处理 scene graph 中内嵌的HTML内容。Prism 负责渲染网页内容。web engine 基于开源的 WebKit 构建，支持HTML5,CSS,JavaScript,DOM和AVG            |

Prism 使用单独线程（非 JAT）进行渲染。它通过提前渲染下一帧来加速渲染。当 scene graph 被修改，如在 `TextField` 中输入文字，Prism 重新渲染 scene graph。scene graph 与 Prism 的同步通过 *pulse* 事件完成。当 scene graph 被修改需要重新渲染时，*pulse* 事件在 JAT 线程排队。*pulse* 事件表示 scene graph 与 Prism 的渲染层不同步，应渲染 Prism 的最新帧。*pulse* 事件限制为每秒最多 60 帧。

Media engine 提供媒体支持，如播放音频、视频。它利用平台上已有的 codecs。media engine 使用单独的线程处理媒体帧，使用 JAT 将帧与 scene graph 同步。media engine 基于开源的多媒体框架 GStreamer 实现。

Web engine 负责处理嵌入在 scene graph 中的 HTML 内容。Web engine 基于开源 web 浏览器引擎 WebKit 实现，支持 HTML5、CSS、JavaScript 和 DOM。

Quantum toolkit 对 Prism, Glass, media engine 和 web engine 的底层组件进行抽象。

## 2. JavaFX 历史

JavaFX 最初由 SeeBeyond 的 Chris Oliver 开发，那时被称为 F3 (Form Follows Function)。F3 是一种 Java 脚本语言，用于轻松开发 GUI 应用。它提供了声明式的语法、静态类型、类型推断、数据绑定、动画、2D graphics 和 Swing 组件。

- 2007 年 Sun Microsystems 收购了 SeeBeyond，F3 更名为 JavaFX
- 2010 年 Oracle 收购了 Sun Microsystems
- 2013 年 Oracle 开源 JavaFX

JavaFX 的第一个版本于 2008 年第四季度发布。

从 Java 8 开始，JavaFX 的版本号与 Java 一致，从 2.2 跳到 8.0。Java SE 和 JavaFX 的 major 版本同时发布。

从 Java SE 11 开始，JavaFX 不再是 Java SE 运行库的一部分，需要单独下载、安装。

| 发布日期 | 版本          | 说明                                                                                                    |
| -------- | ------------- | ------------------------------------------------------------------------------------------------------- |
| Q4, 2008 | JavaFX 1.0    | JavaFX 初始版本，使用声明式语言 JavaFX 脚本编写 JavaFX 代码                                             |
| Q1, 2009 | JavaFX 1.1    | 引入对 JavaFX Mobile 的支持                                                                             |
| Q2, 2009 | JavaFX 1.2    | –                                                                                                       |
| Q2, 2010 | JavaFX 1.3    | –                                                                                                       |
| Q3, 2010 | JavaFX 1.3.1  | –                                                                                                       |
| Q4, 2011 | JavaFX 2.0    | 放弃对 JavaFX 脚本的支持，使用 Java 编写 JavaFX 代码。删除对 JavaFX Mobile 的支持                       |
| Q2, 2012 | JavaFX 2.1    | 引入对 Mac OS 的支持                                                                                    |
| Q3, 2012 | JavaFX 2.2    | –                                                                                                       |
| Q1, 2014 | JavaFX 8.0    | JavaFX 版本从 2.2 跃升至 8.0。JavaFX 和 Java SE 版本从 Java 8 开始保持一致                              |
| Q2, 2015 | JavaFX 9.0    | 公开了一些内部 API, JEP253                                                                              |
| Q3, 2018 | JavaFX 11.0.3 | JavaFX 不再是 Oracle Java JDK 的一部分，需从 Gluon 公司下载。支持作为端口添加的手持设备和其它嵌入式设备 |
| Q1, 2019 | JavaFX 12.0.1 | Bug 修复和功能增强                                                                                      |
| Q3, 2019 | JavaFX 13.0   | Bug 修复和功能增强                                                                                      |
| Q1, 2020 | JavaFX 14.0   | WebView 支持 HTTP/2. Bug 修复和功能增强                                                                 |
| Q3, 2020 | JavaFX 15.0   | 改进稳定性（内存管理）。Bug 修复和功能增强                                                              |
| Q1, 2021 | JavaFX 16.0   | 必须从 module path 载入 JavaFX modules，从类路径载入发出编译器警告。Bug 修复和功能增强                  |
| Q4, 2021 | JavaFX 17.0.1 | 少量功能增强和 Bug 修复                                                                                 |

JavaFX 11 之后的发布详细信息可参考： https://github.com/openjdk/jfx/tree/master/doc-files

