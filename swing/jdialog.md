# JDialog

## 简介

`JDialog` 是类似于 `JFrame` 的顶层容器类，其 `JRootPane` 包含一个内容窗格和一个可选的 `JMenuBar`，实现了 `RootPaneContainer` 和 `WindowContants` 接口。

Java 提供了 `JDialog` 和 `JOptionPane` 两个创建对话框的类，`JOptionPane` 只是将内容放到 `JDialog` 的 `contentPane` 中。

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
