# Stage

## 简介

`Stage` 是 JavaFX 的顶层容器，用于托管 `Scene`。`Scene` 包含所有的可视化组件。和主屏幕对应的主 `Stage` 由 JavaFx 平台创建，传递给 `Application` 类的 `start(Stage s)`方法。可以根据需要创建其它 `Stage`。

```ad-tip
`Stage` 是顶层容器并不代表它必须单独显示，如在 Web 环境中，`Stage`都是内嵌在网页中显示。
```

下面是 `Stage` 的类图：

![|350](Pasted%20image%2020230704205908.png)
Window 是窗口类容器的超类，包含窗口相关的通用功能：

- 隐藏和显示窗口：show() 和 hide() 方法
- 设置位置、高度和宽度：x, y, width, height 属性
- 透明度：opacity 属性

setScene() 设置 Window 托管的 Scene。

Stage 定义的 close() 方法，效果与 Window 的 hide() 一样。

`Stage` 的创建和修改必须在 JavaFX 应用线程上进行。Application 的

### 显示主 Stage

JavaFX 应用线程在调用 `Platform.exit()` 方法或者最后可见的 `stage` 被关掉，该线程才会终止。只有在所有的非守护线程死掉，JVM才会终止。JavaFX 应用线程不是守护线程。

如果 `stage` 没有显示，调用 `close()` 方法无效，就无法终止 JavaFX 应用线程。关闭 JavaFX 应用线程的方法有：

- 调用 `Platform.exit()`
- 先调用 `show()`，然后调用 `close()`
- 关闭显示的窗口
- 直接通过OS操作终止

> NOTE:  `Stage` 的 `close()` 方法和 `Window` 类的 `hide()` 方法效果一样。如果窗口没有显示，调用 `close()` 方法无效。

### 设置 Stage 的边界

stage 的边界由 x, y, width, height 四个属性决定。
当 stage 不包含 scene，也没有设置其位置和大小，则其位置和大小由 platform 设置。

stage 默认在屏幕居中(`centerOnScreen`)：

- 水平居中
- 垂直方向，左上角为屏幕高度的 1/3 减去stage高度

`sizeToScene()`  
Stage 大小根据 scene 的内容来设置。

### Stage 样式

`Stage` 的区域可以分为两块：内容区域（Content area）和装饰（Decoration）。

- 内容区域用于显示 `Scene` 的内容，即可视化组件;
- 装饰部分包含标题栏和边框。

标题栏及其内容根据平台不同有所差别，并且有些装饰不仅仅是外观差异，还提供了额外的功能。例如，可以通过标题栏将 stage 拖曳到不同的地方；标题栏上的最大化、最小化、还原和关闭按钮；边框可用于调整 stage 大小等功能。

`Stage` 的 Style 属性用于设置 `Stage` 的装饰部分和背景色，根据风格不同分为以下五类:

|`StageStyle`|说明|
|---|---|
|`DECORATED`|白色背景，基于平台风格的装饰|
|`UNDECORATED`|白色背景，无装饰(无标题栏)|
|`TRANSPARENT`|透明背景，无装饰(无标题栏)|
|`UNIFIED`|背景色和装饰一致，平台风格装饰，装饰和client area间无边框|
|`UTILITY`|白色背景，最低的平台风格装饰配置|

> NOTE: stage 的样式仅仅指定装饰，而背景色由 scene 的背景设置决定，默认为白色。如果将 stage 的风格设置为 `TRANSPARENT`,对应的 stage 依然为白色背景，要获得统一的透明 stage，需要调用scene的 `setFill(null)`方法。

**设置 stage 的样式：**  
`initStyle(StageStyle style)`  
该方法的调用，必须在 stage 显示之前，否则会抛出 runtime 异常。

### 初始化 Stage 模态

GUI应用的窗口可以分为两类：模态(modal)和非模态(modeless)。模态窗口显示时，不能和该应用的其他窗口交互，直到模态窗口关闭；非模态窗口则没有此限制，可以在多个非模态窗口间随意切换。

模态：显示模态窗口时，无法操作应用中的其他窗口，直到模态窗口关闭。

JavaFX 的 Stage 有三种模态类型，由 `Modality` enum 定义：
|Modal|值|说明|
|---|---|---|
|None|Modality.None|默认值，不阻止其他窗口显示，效果等同于 modeless|
|Window modal|Modality.WINDOW_MODAL|阻止其 owner 内的其他窗口，如果没有owner，则和None效果相同|
|Application modal|Modality.APPLICATION_MODAL|阻止该应用内所有其他的窗口|

通过 `Stage` 的 `initModality(Modality m)` 方法设置。
> NOTE: 模态的设置，需要在 Stage 显示之前进行。

**initOwner(Window owner)**  
Stage 可有一个 Window 类型的 owner, 该 owner 为其他的 `Window`。通过 initOwner(Window owner)方法设置。需要在stage显示前设置。当 owner 最小化、隐藏等，stage也会最小化或隐藏，owner可以为null。

例：有四个 stages: s1, s2, s3, s4。 s1， s4 的modal为None，并且没有owner，s1是s2的owner，s2是s3的owner，四个stages都显示。

- 设置s3的modality为WINDOW_MODAL，则s3和s4可同时显示，s3和s1, s2不能同时显示。
- 设置s4的modality为APPLICATION_MODAL，则在s4关闭前，其他的窗口都不能显示。

### Stage 透明度设置

```java
setOpacity(double opacity)
getOpacity()
```

值范围：[0.0, 1.0]。

0.0 表示完全透明，1.0表示完全不透明。

### Stage 大小设置

```java
setResizable(boolean resizable)
setMinWidth()
setMaxWidth()
setMinHeight()
setMaxHeight()
```

上面的方法只能设置禁用用户缩放窗口，即使 `setResizable(false)`，依然可以通过程序设置 Stage 的缩放。

### 拖动 Stage

由于 Undecorated 和 Transparent 风格的Stage没有标题栏，无法用鼠标拖动移动窗口。此时只能通过添加鼠标事件实现拖动窗口功能
