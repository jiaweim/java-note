# JRootPane

## 简介

每个顶层容器都依赖于一个称为 root-pane 的中间容器。root-pane 复杂管理 content-pane、菜单栏，以及其它几个容器。使用 Swing 组件通常不需要了解 root-pane。但是，如果需要拦截鼠标事件或绘制多个组件，就需要熟悉 root-pane。

下面是 root-pane 提供给 JFrame 以及其它顶层容器的组件列表：

<img src="images/ui-rootPane.gif" width="360" />

content-pane 和可选的菜单栏不再赘述。root-pane 还添加了 layered-pane 和 glass-pane：

- layered-pane 包含菜单栏和 content-pane，支持组件的 Z 序排列。
- glass-pane 通常用于拦截顶层容器上发生的输入事件，也可以在多个组件上进行绘制。

