# JDialog

## 简介

`JDialog` 是用于显示 `Frame` 相关信息的标准弹窗组件。它功能与 `JFrame` 类似，其 `JRootPane` 包含一个内容窗格和一个可选的 `JMenuBar`，实现了 `RootPaneContainer` 和 `WindowContants` 接口。

## 创建

`JDialog` 有 16 个构造函数：

```java
public JDialog()
public JDialog(Dialog owner)
public JDialog(Dialog owner, boolean modal)
public JDialog(Dialog owner, String title)
public JDialog(Dialog owner, String title, boolean modal)
public JDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc)
public JDialog(Frame owner)
...
```

不同构造函数提供不同形式的定制功能。

## 属性

`JDialog` 属性和 `JFrame` 基本一样。

|Property Name|Data Type|Access|
|---|---|---|
|accessibleContext|AccessibleContext|Read-only|
|contentPane|Container|Read-write|
|defaultCloseOperation|int|Read-write|
|glassPane|Component|Read-write|
|jMenuBar|JMenuBar|Read-write|
|layeredPane|JLayeredPane|Read-write|
|layout|LayoutManager|Write-only|
|rootPane|JRootPane|Read-only|

指定 `defaultCloseOperation` 的常量在 `WindowConstants` 中。默认为 `HIDE_ON_CLOSE`。

## 事件处理

`JDialog` 的事件处理和 `JFrame` 完全一样。

针对对话框，我们可能希望按 `Escape` 键可以取消对话框。实现该功能的最简单方式是对 `JRootPane` 中的键盘操作注册 `Escape` 按钮。
