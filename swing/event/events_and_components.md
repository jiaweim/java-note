# Swing 组件支持的 Listener

- [Swing 组件支持的 Listener](#swing-组件支持的-listener)
  - [简介](#简介)
  - [所有 Swing 组件支持的 listener](#所有-swing-组件支持的-listener)
  - [Swing 组件支持的其它 Listener](#swing-组件支持的其它-listener)

2023-12-28, 13:14⭐
***

## 简介

通过查看组件上可以注册的 event-listener 类型，可以判断组件可以触发哪些类型的事件。例如，`JComboBox` 类定义了如下注册 listener 的方法：

```java
addActionListener
addItemListener
addPopupMenuListener
```

因此，除了从 `JComponent` 继承的 listener 方法，`JComboBox` 还支持 action, item 和 context-menu-listener。

Swing 组件支持的 listener 分为两类：

- 所有 Swing 组件支持的 listener
- Swing 组件特定的 listener

## 所有 Swing 组件支持的 listener

所有 Swing 组件都继承自 `Component`，因此可以在 Swing 组件是行注册以下 listener：

|listener|监听内容|
|---|---|
|component-listener|组件的大小、位置和可见性的变化|
|focus-listener|组件获得或丢失键盘焦点|
|key-listener|键盘按键，key-event 只能由持有键盘 focus 的组件触发|
|mouse-listener|mouse-click, mouse-press, mouse-release 以及鼠标进出组件区域|
|mouse-motion-listener|组件上鼠标光标位置变化|
|mouse-wheel-listener|组件上鼠标滚轮的移动|
|hierarchy-listener|组件包含分层结构的变化|
|hierarchy-bounds-listener|组件包含分层结构的移动或调整大小|

所有 Swing 组件派生自 AWT `Container` 类，但大多数组件不能作为容器。因此，从技术上讲，任何 Swing 组件都可以触发容器事件，通知从容器添加或删除组件的事件。然而，实际上只有容器（panels 和 frame）和复合组件（如 combo-box）触发容器事件。

JComponent 添加了另外三种 listener 类型，ancestor-listener 监听组件的包含祖先添加到容器或从容器删除、隐藏、显示或移动。

另外两种 listener 类型用于支持 JavaBeans。所有 Swing 组件支持绑定和约束属性，并将属性的更改通知 listener。property-change-listener 监听绑定属性的变化，多个 Swing 组件使用，如 formatted-text-field。

## Swing 组件支持的其它 Listener

下表列出了 Swing 组件支持的专用  listener，不包括所有组件 `Component`, `Container` 和 `JComponent` 支持的 listener。大多数事件直接从组件触发，少数从组件的数据或选择模型触发。

| Component            | Action Listener | Caret Listener | Change Listene | Document Listener, Undoable Edit Listener | Item Listener | List Selection Listener | Window Listener | 其它              |
| -------------------- | --------------- | -------------- | -------------- | ----------------------------------------- | ------------- | ----------------------- | --------------- | ----------------- |
| button               | ✔️               |                | ✔️              |                                           | ✔️             |                         |                 |                   |
| check box            | ✔️               |                | ✔️              |                                           | ✔️             |                         |                 |                   |
| color chooser        |                 |                | ✔️              |                                           |               |                         |                 |                   |
| combo box            | ✔️               |                |                |                                           | ✔️             |                         | ✔️               |                   |
| dialog               |                 |                |                |                                           |               |                         | ✔️               |                   |
| editor pane          |                 | ✔️              |                | ✔️                                         |               |                         |                 | HyperlinkListener |
| file chooser         | ✔️               |                |                |                                           |               |                         |                 |                   |
| formatted text field | ✔️               | ✔️              |                | ✔️                                         |               |                         |                 |                   |
| frame                |                 |                |                |                                           |               |                         | ✔️               |                   |
| internal frame       |                 |                |                |                                           |               |                         |                 |                   |
| list                 |                 |                |                |                                           |               | ✔️                       |                 |                   |
| menu                 |                 |                |                |                                           |               |                         |                 |                   |
| option pane          |                 |                |                |                                           |               |                         |                 |                   |
| password field       | ✔️               | ✔️              |                | ✔️                                         |               |                         |                 |                   |
| popup menu           |                 |                |                |                                           |               |                         |                 |                   |
| progress bar         |                 |                | ✔️              |                                           |               |                         |                 | PopupMenuListener |
| radio button         | ✔️               |                | ✔️              |                                           | ✔️             |                         |                 |                   |
| slider               |                 |                | ✔️              |                                           |               |                         |                 |                   |
| spinner              |                 |                | ✔️              |                                           |               |                         |                 |                   |
| tabbed pane          |                 |                | ✔️              |                                           |               |                         |                 |                   |
| table                |                 |                |                |                                           |               | ✔️                       |                 |                   |
| text area            |                 | ✔️              |                | ✔️                                         |               |                         |                 |                   |
| text field           | ✔️               | ✔️              |                | ✔️                                         |               |                         |                 |                   |
| text pane            |                 | ✔️              |                | ✔️                                         |               |                         |                 | HyperlinkListener |
| toggle button        | ✔️               |                | ✔️              |                                           | ✔️             |                         |                 |                   |
| tree                 |                 |                |                |                                           |               |                         |                 |                   |
| scroll pane          |                 |                | ✔️              |                                           |               |                         |                 |                   |

