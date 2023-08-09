# Model View Controller

2023-07-11, 15:33
***
## 1. 简介

GUI 应用包含 3 个基本任务：从用户接收输入，处理输入，显示输出。对应的 GUI 代码有两类：

- 处理特定数据的业务代码
- 处理用户界面界面的展示代码

相同数据以多种方式展示是常见需求。如在 HTML 和 JavaFX 桌面中展示相同数据。为了便于维护应用代码，通常需要将应用进程分为两个逻辑模块，一个模块包含展示代码，一个包含业务代码。

## 2. MVC

MVC（模型-视图-控制器）模式是最经典的模式之一，用于实现业务和视图代码的拆分。MVC 模式包含三个组件：模型（model）、视图（view）和控制器（conroller）。下图展示 MVC 组件及它们之间的交互：

![|500](Pasted%20image%2020230711142535.png)
在 MVC 中：

- model 包含对业务逻辑的建模
- view 和 controller 包含可视化相关对象，如输入、输出以及用户与 GUI 元素的交互
    - controller 接收并处理用户输入，及用户与 controller 直接交互
    - view 在屏幕显示输出
    - controller 与 view 一一对应

屏幕上每个控件都是一个 view，有一个对应的 controller。因此，一个 GUI 通常包含多对 view-controller。controller 修改 model 状态，model 与 view 始终同步：model 通知 view 其状态的变更，view 显示更新后的数据。

model 与 view 之间通过 observer 模式进行交互。model 不包含 view-controller 的任何信息，只是提供了添加 listeners 的方法，使 view 可以监听 model 的变化。任何对 model 感兴趣的 view 都可以添加 listener，在 model 发生变化时，会通知所有订阅的 views。

上面介绍的 MVC 模式是最原始的 MVC 概念，于 1980 年在 Smalltalk-80 语言中开发用户界面时提出。MVC 中展示和业务逻辑拆分的概念依然适用，但是，MVC 中三个组件的职责划分存在问题。除了 model 的状态，view 自身也有状态，如颜色、ListView 中被选择的 item，这些状态不依赖于 model。

## 3. AM-MVC

MVC 中哪个组件存储 view 的逻辑和状态的问题引出了另一个 MVC 变体，称为 Application Model MVC (AM-MVC)。

AM-MVC 多了一个 Application Model (AM) 组件，负责 view 的逻辑和状态。如下图所示：

![|500](Pasted%20image%2020230711150128.png)

和 MVC 模式一样，AM-MVC 的 model 和 view 是解耦的，两者通过 observer 模式同步。AM 用于保存 view 相关逻辑和状态，但不允许直接访问 view。所以 AM 更新 view 属性的代码又丑又繁琐。

## 4. MVP

现代图形操作系统，如 Windows 和 Mac OS 提供了 native widgets，用户可以直接与之交互。

native widgets 将 controller 和 view 的功能合并，产生了 MVC 的另一种变体，称为 model-view-presenter (MVP) 模式。现代 widgets 还支持数据 binding，从而用更少的代码同保护 model 和 view。下图为 MVP 模式示意图：

![|500](Pasted%20image%2020230711151003.png)
在 MVC 中，每个 widget 都是一个 view，对应一个 controller。在 MVP 中，一个 view 由多个 widgets 组成。view 接收输入并转交给 presenter。注意，view 不响应用户输入，只负责接收。

view 通知 presenter 用户输入信息，由 presenter 作出响应。presenter 负责展示逻辑、操作 view、向 model 发出命令。当 presenter 修改 model，view 通过 observer 模式更新。

model 负责存储业务数据和逻辑。和 MVC 一样，MVP 中 model 独立于 view 和 presenter。

MVC 中业务与视图分开的概念已经存在 40 余年，所有 MVC 的变体采用不同的方式实现与经典 MVC 相同的功能。这些 MVC 变体主要差别在于组件的职责不同。

