# JComponent

- [JComponent](#jcomponent)
  - [简介](#简介)
  - [JComponent 功能](#jcomponent-功能)
  - [JComponent API](#jcomponent-api)
    - [自定义组件外观](#自定义组件外观)
    - [设置和获取组件状态](#设置和获取组件状态)
    - [处理事件](#处理事件)
    - [绘制组件](#绘制组件)
    - [分层结构](#分层结构)
    - [布置组件](#布置组件)
    - [大小和位置](#大小和位置)
    - [指定绝对大小和位置](#指定绝对大小和位置)
  - [参考](#参考)

2023-12-21, 23:40⭐
****

## 简介

除了顶层容器，所以以 "J" 开头的 Swing 组件都继承自`JComponent`。

`JComponent` 继承自 `Container`，`Container` 又继承 `Component`：

- `Component` 类包含支持布局、绘制和事件等所有内容。
- `Container` 支持向容器添加组件，并进行布局

所有继承 `JComponent` 的组件都是基本组件类，需要放在中间容器展示。下面总结 `Component`, `Container` 和 `JComponent` 中常用方法。

## JComponent 功能

JComponent 为其子类添加了如下功能：

| 功能       | 说明                                                         |
| ---------- | ------------------------------------------------------------ |
| tool-tip   | `setToolTipText` 指定字符串，为组件提供帮助信息。光标停留在组件上，指定的字符串将在组件附近出现的一个小窗口中显示。详情参考 [如何使用 Tool Tip](tool-tip.md) |
| 绘制和边框 | `setBorder` 指定在组件边缘周围显示的边框。覆盖 `paintComponent` 可以绘制组件内部。详情参考[边框](border.md)和[自定义绘制](custom_painting.md) |
| 可插入 Laf | 在底层，每个 `JComponent` 都有一个对应的 `ComponentUI` 对象，用于执行该 `JComponent` 的绘制、事件处理、尺寸调整等操作。而使用哪个 `ComponentUI` 取决于当前的 Laf，通过 `UIManager.setLookAndFeel` 可以设置，参考[设置 Laf](../laf/设置%20laf.md) |
|  自定义属性 | `JComponent` 可以关联一个或多个属性。使用 `putClientProperty` 和 `getClientProperty` 设置和获得属性值。在现实开发中经常用到|
|支持 layout|`JComponent` 在 `Component` 的基础上提供了多个设置布局的方法：<br>- `setPreferredSize`<br>- `setMinimumSize`<br>- `setMaximumSize`<br>- `setAlignmentX`<br>- `setAlignmentY`|
|无障碍|无障碍技术用于支持残疾人士使用计算机，一般包括语音接口、屏幕阅读器等输入设备组成，不仅对残疾人士，对在非办公室环境使用计算机的人来说都非常有用，例如堵车时，可以使用无障碍技术通过语音输入和输出来查收电子邮件|
|支持拖放|JComponent 支持设置组件的传输 handler，这是拖放的基础|
|双缓冲|使用双缓冲技术能改进频繁变化组件的显示效果。`JComponent` 组件默认双缓冲，可以使用 `setDoubleBuffered(false)` 关闭双缓冲|

## JComponent API

### 自定义组件外观

- 设置或获取组件的边框，参考[边框](border.md)

```java
void setBorder(Border)
Border getBorder()
```

- 设置组件的前景色和背景色。前景色通常是绘制组件文本的颜色，背景色指组件背景颜色（如果组件不透明）

```java
void setForeground(Color)
void setBackground(Color)
```

- 获取组件的前景色或背景色

```java
Color getForeground()
Color getBackground()
```

- 设置或获取组件是否不透明，不透明组件使用 background-color 填充背景

```java
void setOpaque(boolean)
boolean isOpaque()
```

- 设置或获取组件字体，如果没有为组件设置字体，则返回父组件的字体

```java
void setFont(Font)
Font getFont()
```

- 设置或获取在组件及其子组件上显示的光标。例如 `aPanel.setCursor(Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR))`

```java
void setCursor(Cursor)
Cursor getCursor()
```

### 设置和获取组件状态

- 设置 `JPopupMenu` 

UI 负责注册并添加必要的 listener，如在适当的时候显示 `JPopupMenu`。何时显示 `JPopupMenu` 取决于 Laf：有些可能在鼠标事件上显示，有些可能绑定键盘。

如果 popup 为 null，并且 getInheritsPopupMenu 返回 true，则 getComponentPopupMenu 将委托给父组件。

```java
void setComponentPopupMenu(JPopupMenu)
```

- 设置或删除 `transferHandler` 属性

`TransferHandler` 支持通过剪切、复制、粘贴以及拖放来交换数据。

```java
void setTransferHandler(TransferHandler)
TransferHandler getTransferHandler()
```

- 提示文本

```java
void setToolTipText(String)
```

- 组件名称

当需要将文本与不显示文本的组件关联，可以使用。

```java
void setName(String)
String getName()
```

- 组件是否在屏幕上显示。组件必须可见，且位于可见和现实的容器中。

```java
boolean isShowing()
```

- 是否启用组件。启用的组件可以响应组件输入并生成事件

```java
void setEnabled(boolean)
boolean isEnabled()
```

- 组件是否可见。除了顶级组件，其它组件默认可见

```java
void setVisible(boolean)
boolean isVisible()
```

### 处理事件

- 添加或删除指定的分层 listener，以便在此容器所属的分层结构发生改变时从此组件接受分层结构更改事件。如果 `l` 为 null，不抛出异常，也不会执行任何操作。

```java
void addHierarchyListener(HierarchyListener l)
void removeHierarchyListener(HierarchyListener l)
```

- 从组件添加或删除 mouseListener

```java
void addMouseListener(MouseListener)
void removeMouseListener(MouseListener)
```

- 从组件添加或删除 mouseMotionListener

```java
void addMouseMotionListener(MouseMotionListener)
void removeMouseMotionListener(MouseMotionListener)
```

- 添加或删除键盘监听器

```java
void addKeyListener(KeyListener)
void removeKeyListener(KeyListener)
```

- 添加或删除 componentListener。当被监听的组件隐藏、显示、移动或调整大小时，通知监听器

```java
void addComponentListener(ComponentListener)
void removeComponentListener(ComponentListener)
```

- 确定指定的点是否在组件内。应该根据组件的坐标系统来指定参数

```java
boolean contains(int, int)
boolean contains(Point)
```

- 返回指定点位置的组件

在组件重叠的情况下，返回最顶层的组件。

```java
Component getComponentAt(int, int)
Component getComponentAt(Point)
```

- 将指定组件移动到容器中的 z-order 索引处

如果组件是其它容器的子组件，则在添加到此容器钱将从该容器删除。该方法与 `java.awt.Container.add(Component, int)` 的主要差别在于，该方法将组件从之前的容器移除时不会调用 `removeNotify` 。如果该组件持有键盘焦点，移到到新位置时保持焦点。

> z-order 决定组件的绘制顺序。z-order 最高的组件最先绘制，最小的最后绘制。组件重叠时，z-order 小的组件会覆盖 z-order 高的组件。

```java
Component setComponentZOrder(component comp, int index)
Component getComponentZOrder(component comp)
```

### 绘制组件

- 重绘组件的全部或部分区域。4 个 int 参数指定重绘区域 (x, y, width, height)

```java
void repaint()
void repaint(int, int, int, int)
```

- 要求重绘组件指定区域

```java
void repaint(Rectangle)
```

- 要求组件及受影响的容器重新布局。通常不需要调用此方法，除非在组件可见后显式更改其大小、对齐，或者在组件可见后更改其层次结构。调用 `revalidate` 需要调用 `repaint` 

```java
void revalidate()
```

- 绘制组件。重写此方法以实现自定义组件的绘制

```java
void paintComponent(Graphics)
```

### 分层结构

- 将指定组件添加到容器

此方法的单参数版本将组件添加到容器末尾。`int` 参数指定组件在容器中的位置。`Object` 参数为当前 layout 提供布局约束。

```java
Component add(Component)
Component add(Component, int)
void add(Component, Object)
```

- 从容器删除组件

```java
void remove(int)
void remove(Component)
void removeAll()
```

- 返回包含组件的 root-pane

```java
JRootPane getRootPane()
```

- 获取组件的最顶层容器，一般为 `Window`，如果组件还没添加到任何容器，返回 null

```java
Container getTopLevelAncestor()
```

- 获取组件的直接容器

```java
Container getParent()
```

- 获取该容器中的组件数目

```java
int getComponentCount()
```

- 获取此容器的一个或所有组件，`int` 参数指定要获取组件的位置

```java
Component getComponent(int)
Component[] getComponents()
```

- 返回容器中组件的 z-order。组件在 z-order 结构中位置越高，其索引越低。z-order 索引最低的组件最后绘制，在其它子组件之上。

```java
Component getComponentZOrder(int)
Component[] getComponentZOrder()
```

### 布置组件

- 设置组件的首选、最大和最小尺寸（单位为像素）。首选尺寸表示组件的组价尺寸。组件应不大于其最大尺寸，不小于其最小尺寸。

```java
void setPreferredSize(Dimension)
void setMaximumSize(Dimension)
void setMinimumSize(Dimension)
```

> **NOTE：** 这些尺寸只是建议，布局管理器可能忽略。

- 获取组件的首选、最大和最小尺寸。许多 JComponent 都有 setter 和 getter 方法。对没有相应 setter 方法的非 JComponent 子类，可以通过创建子类并覆盖这些方法来设置这些尺寸。

```java
Dimension getPreferredSize()
Dimension getMaximumSize()
Dimension getMinimumSize()
```

- 设置沿 x 或 y 轴的对齐方式。这些值表示该组件如何相对其它组件对其。该值介于 0 到 1 之间， 0 表示沿原点对齐，1 表示离原点最远的点，0.5 表示居中。

```java
void setAlignmentX(float)
void setAlignmentY(float)
```

> **NOTE：** 这些对齐值只是建议，布局管理器可能忽略。

- 获取组件沿 x 轴或 y 轴的对齐方式。对于没有相应 setter 方法的非 JComponent 子类，可以通过创建创建子类并覆盖这些方法来设置组件的对齐方式

```java
float getAlignmentX()
float getAlignmentY()
```

- 设置或获取组件的布局管理器。布局管理器负责调整容器内组件的大小和位置。

```java
void setLayout(LayoutManager)
LayoutManager getLayout()
```

- 设置此容器及子组件的 `ComponentOrientation` 属性。

```java
void applyComponentOrientation(ComponentOrientation) 
void setComponentOrientation(ComponentOrientation)
```

### 大小和位置

- 获取组件的宽度或高度，单位：像素

```java
int getWidth()
int getHeight()
```

- 获取组件当前尺寸（像素）。该方法的单参数版本，由调用方创建返回的 Dimension 实例

```java
Dimension getSize()
Dimension getSize(Dimension)
```

- 获取组件原点相对于父组件左上角的坐标 x 和 y

```java
int getX()
int getY()
```

- 获取组件边界（像素）。边界指定组件相对父容器的宽度、高度和原点。单参数版本，由调用方创建 Rectangle 实例

```java
Rectangle getBounds()
Rectangle getBounds(Rectangle)
```

- 获取组件相对父容器左上角的位置（像素）。单参数版本由调用者创建 `Point` 

```java
Point getLocation()
Point getLocation(Point)
```

- 返回相对屏幕左上角的位置

```java
Point getLocationOnScreen()
```

- 获取组件边框的大小

```java
Insets getInsets()
```

### 指定绝对大小和位置

- 设置组件相对父组件左上角的位置（像素）。不使用布局管理器时，使用这些方法来设置组件位置

```java
void setLocation(int, int)
void setLocation(Point)
```

- 设置组件大小（像素）。不使用布局管理器时，使用这些方法来设置组件大小

```java
void setSize(int, int)
void setSize(Dimension)
```

- 设置组件相对父容器左上角的大小和位置（像素）。4 个 int 分别指定 x, y, width, height。不使用布局管理器时使用。

```java
void setBounds(int, int, int, int)
void setBounds(Rectangle)
```

## 参考

- https://docs.oracle.com/javase%2Ftutorial%2F/uiswing/components/jcomponent.html
