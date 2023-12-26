# Swing 事件处理

## 简介

概念：

- 事件，即用户对组件发出的动作
- 事件处理，就是用户要对事件源进行操作。
- 事件监听，就是系统接收到所产生的事件，然后根据这些事件作相应的处理。

任意多的事件监听器可以监听来自任意多的事件源的任意类型事件。

![](images/2020-01-07-14-31-04.png)

## 事件监听器

下面将说明在实现事件处理时需要注意的事项。并介绍事件对象，特别是 `EventObject`，该类是所有 AWT 和 Swing 事件的超类。然后，会分别介绍语义事件和低级事件，并建议尽量用语义事件。余下部分介绍实现事件处理的会用到的一些技巧。

## 设计要素

事件监听器的关键是：反应快。

因为所有的绘制和事件监听方法在同一个线程中执行，事件监听方法慢会导致程序反应迟钝，重绘时很慢。如果对某个事件需要执行长时间操作，应该重新启用一个线程执行该操作。

实现事件监听的选择很多，没有一个通用的方法，但是，我们可以给出一些建议并展示一些技巧。例如，你可能选择对不同事件监听器新建不同的类。这个架构很容易维持，但是过多的类会导致性能降低。

在设计程序时，你可能希望在非公有类中实现事件监听。一个private的实现更为安全。

对于一些简单的事件监听，使用 `EventHandler` 类以避免新建新的类更合适。

## Event 对象

每个事件监听方法都有一个继承自 `EventObject` 类的参数。例如，用于处理鼠标事件的MouseEvent。

## 适配器类

扩展适配器类，就只需实现自己需要的方法。AWT 事件监听器接口有 7 个适配器类：

|适配器类|说明|
|---|---|
|ComponentAdapter|组件适配器|
|ContainerAdapter|容器适配器|
|FocusAdapter|焦点适配器|
|KeyAdapter|键盘适配器|
|MouseAdapter|鼠标适配器|
|MouseMotionAdapter|鼠标移动适配器|
|WindowAdapter|窗口适配器|


## 监听器管理

对自定义组件，需要自己维护监听器列表。对 AWT 事件的监听器，可以用 `AWTEventMulticaster` 类管理，所有 AWT 组件都使用该类管理事件监听器列表。该类实现了所有 AWT 事件监听器，包括 `ActionListener`, `AdjustmentListener`, `ComponentListener`, `ContainerListenerr`, `FocusListener`, `HierarchyBoundsListener`, `HierarchyListener`, `InputMethodListener`, `ItemListener`, `KeyListener`, `MouseListener` 等。每当你调用组件的方法来添加或删除监听器，都会使用 `AWTEventMulticaster` 提供支持。

所以，如果你想创建自己的组件，并管理以上 AWT 事件/监听器，可以使用 `AWTEventMulticaster`。



为了便于管理监听器，Swing 提供了 `EventListenerList` 类。
