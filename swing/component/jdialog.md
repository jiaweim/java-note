# JDialog

- [JDialog](#jdialog)
  - [简介](#简介)
  - [创建](#创建)
  - [属性](#属性)
  - [事件处理](#事件处理)
  - [JOptionPane](#joptionpane)
  - [参考](#参考)


## 简介

`JDialog` 是类似于 `JFrame` 的顶层容器类，其 `JRootPane` 包含一个内容窗格和一个可选的 `JMenuBar`，实现了 `RootPaneContainer` 和 `WindowContants` 接口。

为了方便使用，有几个类可以直接实例化对话框：

- `JOptionPane` 简单标准的对话框
- `ProgressMonitor` 显示进度的对话框
- `JColorChooser` 和 `JFileChooser` 也是提供标准对话框
- 使用 Printing API 可以显示打印对话框
- 自定义对话框，则直接使用 `JDialog`

对话框支持模态。模态对话框显示时，阻止用户与其它窗口的交互。`JOptionPane` 创建的 `JDialog` 是模态的，要创建非模态对话框，需要直接使用 `JDialog`。



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

- `defaultCloseOperation` 和 `JFrame` 的功能一样，默认为 `HIDE_ON_CLOSE，常见操作：

```java
JDialog dialog = new JDialog(this);
dialog.setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
```

- `defaultLookAndFeelDecorated` 属性指定新创建的 `JDialog` 的装饰由当前的 laf 提供。

不过这只是一个提示，如果 laf 不支持此功能，设置为 `true` 也没有任何效果。

- `layout` 属性

在此处说明是因为 `JDialog` 覆盖了 `setLayout` 方法，将其调用转到对 `contentPane` 的 `setLayout` 方法的调用。



## 事件处理

`JDialog` 的事件处理和 `JFrame` 完全一样。

针对对话框，我们可能希望按 `Escape` 键可以取消对话框。实现该功能的最简单方式是对 `JRootPane` 中的键盘操作注册 `Escape` 按钮。

## JOptionPane



## 参考

- https://docs.oracle.com/javase%2Ftutorial%2F/uiswing/components/dialog.html