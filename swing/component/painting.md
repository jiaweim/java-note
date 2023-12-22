# AWT 和 Swing 的渲染机制

- [AWT 和 Swing 的渲染机制](#awt-和-swing-的渲染机制)
  - [简介](#简介)
  - [Swing Paint 系统的演化](#swing-paint-系统的演化)
  - [AWT 的绘制](#awt-的绘制)
    - [系统触发 vs. 应用触发](#系统触发-vs-应用触发)
    - [paint 方法](#paint-方法)
    - [paint() vs. update()](#paint-vs-update)
    - [轻量级组件](#轻量级组件)
      - [如何渲染轻量级组件](#如何渲染轻量级组件)
      - [轻量级和系统触发](#轻量级和系统触发)
      - [轻量级组件和透明度](#轻量级组件和透明度)
    - [智能 painting](#智能-painting)
    - [AWT 绘制指南](#awt-绘制指南)
  - [Swing 的绘制](#swing-的绘制)
    - [双缓冲](#双缓冲)
    - [更多绘制属性](#更多绘制属性)
    - [paint 方法](#paint-方法-1)
      - [Painting and the UI Delegate](#painting-and-the-ui-delegate)
    - [Paint 过程](#paint-过程)
    - [Swing 绘制指南](#swing-绘制指南)
  - [参考](#参考)


## 简介

优秀的绘图代码是提高 GUI 性能的关键。在图形系统中，窗口工具箱通常会提供一个框架，使图形用户界面（GUI）能够轻松地在正确的时间将正确的像素呈现（渲染）到屏幕。

AWT（Abstract Windowing Toolkit）和 Swing 都提供了这样一个框架。但是许多开发者没有完全理解该框架的 API，导致程序性能不好。

本文详细讲述了 AWT 和 Swing 的渲染机理，以帮助开发者写出正确高效的 GUI 绘图代码。这篇文章描述了绘制机制(**何时何地渲染**)，但是没有说明如何使用 Swing 的 Graphics APIs 进行渲染，这方面内容可以参考 [Java 2D](https://www.oracle.com/java/technologies/java-2d-api.html) 相关内容。

## Swing Paint 系统的演化

为 JDK 1.0 开发的 AWT API 原始版本，只包含重量级组件（"heavyweight" 表示该组件具有对应的不透明的本地窗口）。该方案使 AWT 严重依赖于不同平台的 paint 子系统，需要仔细考虑 damage detection, clip calculation, z-ordering 等细节。JDK 1.1 引入了轻量级组件（"lightweight" 组件重利用与其最近的重量级的父类组件窗口），AWT 需要通过Java 代码实现轻量级组件的绘制过程。因此，轻量级和重量级组件的绘制工作稍有不同。

JDK 1.1 之后，发布的 Swing 工具包在 paint 组件方面引入了自身特性。Swing 大部分 paint 机制和 AWT 类似或依赖于 AWT，引入的新机制使其 API 在自定义 paint 方面更为简洁。

## AWT 的绘制

### 系统触发 vs. 应用触发

要理解 AWT 的绘图 API 如何工作，首先应该了解在窗口环境中是什么触发了 paint 操作。在 AWT 中，有两种绘图操作：

- 系统触发的 painting
- 应用程序触发的 painting

在系统触发的 painting 操作中，系统一般因为以下原因要求组件渲染其内容：

- 组件首次在屏幕上显示
- 调整了组件大小
- 组件损坏，需要修复。例如，之前覆盖组件的东西已移除，组件被覆盖的部分显示出来

在应用触发的 painting 操作中，组件因为内部状态的改变决定更行其内容。例如，一个按钮检测到被鼠标按下，决定绘制一个 "depressed" 样式的按钮。

### paint 方法

不管如何触发 paint，AWT 均使用 "callback" 机制执行 painting，轻量级和重量级组件都是如此。这意味着，程序必须将组件的渲染代码放在一个特定的 `override` 方法中，然后在需要 paint 时由 AWT 调用该方法。该方法在 `java.awt.Component` 中：

```java
public void paint(Graphics g)
```

当 AWT 调用该方法，其参数 `Graphics` 对象已为绘制该组件配置好属性：

- `Graphics` 的 `color` 设置为组件的 `foreground` 属性
- `Graphics` 的 `font` 为组件的 `font` 属性
- `Graphics` 的 `translation` 配置为，组件的左上角坐标为 (0, 0)
- `Graphics` 的 clip rectangle 设置为组件需要 repainting 的区域

程序必须使用 `Graphics` 对象(或其派生类)渲染输出。可随时根据需要修改 `Graphics` 的状态。

下面是一个简单的 paint callback 示例，在一个组件的边界内渲染出一个实心圆:

```java
public void paint(Graphics g){
	// Dynamically calculate size information
	Dimension size = getSize();
	// diameter
	int d = Math.min(size.width, size.height)l
	int x = (size.width-d)/2;
	int y = (size.height-d)/2;
	// draw circle (color already set to foreground)
	g.fillOval(x, y, d, d);
	g.setColor(Color.black);
	g.drawOval(x, y, d, d);
}
```

一般，应该避免在 `paint` 方法外编写渲染代码，因为这类代码可能在不应该 paint 的时候被调用，例如，在组件可见之前或者能访问有效 `Graphics` 对象之前调用。并且建议直接调用 `paint()`。

要在程序中触发 painting，AWT 在 `java.awt.Component` 中提供了如下方法，以执行异步请求 paint 操作：

```java
public void repaint()
public void repaint(long tm)
public void repaint(int x, int y, int width, int height)
public void repaint(long tm, int x, int y, int width, int height)
```

下面实现了一个 mouse listener，在鼠标按下或释放时，使用 `repaint()` 触发一个按钮组件的更新：

```java
MouseListener l = new MouseAdapter(){
	public void mousePressed(MouseEvent e){
		MyButton b = (MyButton)e.getSource();
		b.setSelected(true);
		b.repaint();
	}
	public void mouseReleased(MouseEvent e){
		MyButton b = (MyButton)e.getSource();
		s.setSelected(false);
		b.repaint():
    }
};
```

对于那些比较复杂的组件，应该调用 `repaint()` 包含参数的重载版本，以指定需要 `repaint` 区域。一个常见错误是，总是调用 `repaint()` 的无参版本，这样会导致重绘整个组件，以致绘制不需要重绘的区域。

### paint() vs. update()

我们为什么要区分系统触发和应用触发的 painting？因为对重量级组件，AWT 对这两种触发的处理的方式稍有不同(轻量级组件后面再讨论)，这是一个导致混淆的原因。

对重量级组件，根据是系统触发还是应用触发，是两种不同的方式执行 painting。

**系统触发**的 painting 流程：

1. AWT 确定组件是部分还是全部需要 paint
2. AWT 通过 EDT 调用组件的 `paint()` 方法

**应用触发**的 painting 流程：

1. 应用根据内部状态的改变，确定组件部分或全部需要 `repaint`
2. 应用调用组件的 `repaint()` 方法，该方法注册一个异步请求到 AWT，说明该组件需要 `repaint`
3. AWT 通过 EDT 调用组件的 `update()` 方法

> 如果在组件 `repaint()` 首次执行前，有多次 `repaint()` 调用，则多次请求被合并为一个 `update()`。何时合并请求取决于具体实现。如果多个请求被合并，则更新矩形区域为多次请求的矩形区域的并集。

如果组件没有重写 `update()`，则 `update()` 的默认实现为：清除组件的背景（当组件不是轻量级组件），然后调用 `paint()`。

由于默认情况下，不管是系统触发还是应用触发，最终的结果都是调用 `paint()`，因此很多人不理解使用单独的 `update()` 方法有何用。原因在于，虽然默认的 `update()` 直接调用 `paint()`，在需要时，我们可以覆盖该方法添加更多绘制操作，这种方式称为增量绘制 (incremental painting)。程序直接调用 `paint()` 意味着整个 graphic clip 需要重新绘制，但是调用 `update()` 就不一定了。

增量绘制对需要在组件已绘制区域上渲染更多内容时很有用。

事实上，大部分 GUI 组件不需要增量绘制功能，所以大多数程序可以忽略 `update()` 方法，直接重写 `paint()` 方法渲染组件当前状态即可。所以说，对大多数组件实现，系统触发和应用触发的渲染是等价的。

### 轻量级组件

从开发者角度看，轻量级组件和重量级组件的 API 基本完全一样，即覆盖 `paint()` 方法，调用 `repaint()` 触发更新。不过，AWT 的轻量级组件完全以 java 代码编写，所以其实现稍有不同。

#### 如何渲染轻量级组件

轻量级组件需要一个重量级的父类容器组件获取 paint 区域。当重量级父类被告知 paint 其窗口时，通过调用重量级父类的 `paint()`，同时调用其所有轻量级子类的 `paint()` 方法。该过程由 `java.awt.Container` 的`paint()` 方法处理，它会依次调用和 paint 矩形重叠的可见轻量级子组件的 `paint()` 方法。所以，`Container` 子类在覆盖 `paint()` 方法时必须要添加 `super.paint(g)`：

```java
public class MyContainer extends Container{
	public void paint(Graphics g){
		// paint my contents first…
		// then, make sure lightweight children paint
		super.paint(g);
	}
}
```

如果缺少 `super.paint()`，则容器的轻量级子类不会显示(JDK 1.1 刚引入轻量级组件时非常常见的问题)。

值得注意的是，`Container.update()` 的默认实现不会递归调用轻量级子类的 `update()` 或 `paint()` 方法。所以希望通过 `update()` 实现增量绘制的 `Container` 的重量级扩展子类，必须保证在必要时递归 `paint` 其轻量级子类。不过，极少重量级容器组件需要增量绘制，所以这个问题对大多数程序都不是问题。

#### 轻量级和系统触发

轻量级组件的窗口行为（显示、隐藏、移动、调整大小）完全以 java 编写。通常，在 Java 实现中，AWT 必须显式让各种轻量级组件 paint(本质上是系统触发)。不过，轻量级框架使用 `repaint()` 方法让组件执行 paint，前面有提到，`repaint()` 调用 `update()` 而不是直接调用 `paint()`。因此，对轻量级组件，系统触发有两条路径：

- 系统触发的绘制请求来自本地系统（即先显示轻量级组件的重量级父容器），导致直接调用 `paint()`；
- 系统触发的绘制请求来自于轻量级框架(如轻量级组件调整大小)，导致 `update()` 调用，其默认实现为调用 `paint()`

总而言之，对轻量级组件，`update()` 和 `paint()` 方法没有实际区别；进一步说明，增量绘制不应该用于轻量级组件。

#### 轻量级组件和透明度

由于轻量级组件是从其重量级父容器“借用”的屏幕区域，所以它们支持透明特性(transparency)。因为轻量级组件从后往前进行绘制，如果上面的轻量组件留下部分区域不绘制，下面的组件就会显示出来。这也是轻量级组件的 `update()` 默认实现不清理背景的原因。

### 智能 painting

虽然 AWT 试图尽可能高效地渲染组件，但组件的 `paint()` 实现对 AWT 的渲染效率有重大影响。建议：

- 使用 clip region 缩小渲染区域
- 使用布局的知识，缩小子类组件的绘制区域(仅限于轻量级组件)

如果你的组件很简单，比如按钮，则不值得去详细设置按钮和 clip 的交叉区域，建议直接重绘制整个组件。

如果组件十分复杂，如 text 组件，则应该利用 clip 信息缩小需要绘制的区域。

对于那些包含许多组件的轻量级组件，则应该利用布局管理器的信息确定需要绘制组件的位置。`Container.paint()` 的默认实现只是查看其子组件的可见性以及与 clip 的重叠性，该操作在部分布局中可能十分低效。例如，如果一个容器为 $100\times 100$ 网格，则网格信息可以帮助快速确定 10000 个组件中哪些和 clip 重叠并需要paint。

### AWT 绘制指南

AWT 为绘制组件提供了一个简单的回调 API。使用指南：

1. 对大多数程序，所有的绘制代码应该放在组件的 `paint()` 方法中。
2. 程序应通过 `repaint()` 间接调用 `paint()`，而不应直接调用 `paint()`
3. 对复杂组件，应该使用 `repaint()` 带参数的重载，以指定绘制区域。
4. 因为 `repaint()` 首先调用 `update()`，而 `update()` 默认调用 `paint()`，重量级组件可以覆盖 `update()`方法实现增量绘制(轻量级组件不支持增量绘制)。
5. 继承 `java.awt.Container` 的类在覆盖 `paint()` 方法时，记得调用 `super.paint()`，确保绘制子对象。
6. 对复杂组件，应该利用 clip 矩形，将绘制区域缩小到与 clip 区域重叠的部分。

## Swing 的绘制

Swing 对 AWT 的绘制模型进行了扩展，以提供性能和可扩展性。和 AWT 一样，Swing 支持绘制回调和使用 `repaint()` 触发更新。另外，Swing 内置双缓冲，添加了更多结构，如边框和 `ComponentUI`。最后，Swing 为自定义绘制机制提供了 `RepaintManager` API。

### 双缓冲

Swing 的一个重要特性是支持双缓冲。Swing 为 `javax.swing.JComponent` 提供了一个 "doubleBuffered"属性：

```java
public boolean isDoubleBuffered()
public void setDoubleBuffered(boolean o)
```
                          
当 Swing 启用双缓冲机制，它会为每个分层结构（一般每个顶层窗口）提供一个屏幕外的缓冲区。虽然可以为每个组件单独设置该属性，但是启用了顶层容器的双缓冲，则其包含的所有轻量级组件都会被缓冲，`不管其doubleBufffered` 属性是否开启。

所有 Swing 组件默认启用双缓冲。但真正重要的是 `JRootPane`，启用其双缓冲使得顶层容器下的所有组件都开启双缓冲。大多时候，Swing 程序除了开启或关闭双缓冲，不需要处理任何双缓冲问题(为了更流程，建议开启)。Swing 内部会处理好一切。

### 更多绘制属性

为了提高内部绘制算法的效率，Swing 在 `JComponent` 中添加两个额外属性。引入这些属性值是为了解决以下两个问题，这两个问题会使绘制轻量级组件称为高代价操作：

- 透明度(*Transparency*)：轻量级组件如果部分或全部透明，则可能不会绘制其部分或全部区域；那么在 repaint 时，就需要先 repaint 它下面的组件。这就要求系统逐层查看以找到该组件下面第一个重量级的父容器，执行 back-to-front paint 操作。
- 重叠组件(Overlapping components)：在 paint 轻量级组件时，可能有其它轻量级组件与其重叠；这意味着，绘制原来的轻量级组件时，其它组件与其重叠的区域也要 repaint。这要求系统逐层查看，检查每个 paint 操作的重叠组件。

**Opacity**

为了提高不透明组件的性能，Swing 为 `javax.swing.JComponent` 添加了一个 `opaque` 属性:

```java
public boolean isOpaque()     
public void setOpaque(boolean o)     
```
                   
设置为：

- true: 组件绘制其矩形边界内的所有区域
- false: 组件不保证绘制其矩形边界内的所有区域。

`opaque` 属性方便 Swing 绘制系统确认一个组件在调用 `repaint` 时，是否需要 repaint 该组件下面的父类组件。Swing 标准组件的 `opaque` 属性的默认值由当前 Laf 设置，大多数组件的值为 `true`。

组件设计的一个常见的错误是：允许 `opaque` 属性默认为 `true`，但没有完全渲染组件边界内区域，从而导致未渲染区域出现screen garbage。在设计组件时，应该仔细考虑 `opaque` 属性，以保证透明度的恰当运用(因为它更耗费资源)、和 paint 系统嵌合良好。

`opaque` 属性的含义常被误解。有时被理解为“使组件的背景透明”，然而，这不是 Swing 的严格定义。例如，有些组件，如按钮，可能将 `opaque` 属性设置为 `false` 以获得非矩形的形状果。在这些情况下，组件的 `opaque` 属性为 `true`，但其背景的大部分区域依然被填充。

如前所述，`opaque` 主要用于 repaint 系统。如果组件使用 `opaque` 属性定义组件的透明度效果，则应该添加详细的文档说明。对部分组件，定义额外属性来控制组件的透明视图更为合适，如 `javax.swing.AbstractButton` 的 `ContentAreaFilled` 属性。

另外一个需要关注的问题是，`opacity` 属性和 Swing 组件的 `border` 属性的关系。组件的 `Border` 对象渲染的区域仍被认为是组件的几何区域。如果组件为 `opaque`，它仍然负责填充 border 占据的区域，border 则在opaque组件上面继续渲染。

如果希望组件允许下面的组件透过其边框显示，如果组件边框通过 `isBorderOpaque()` 返回 `false` 支持透明度，则组件必须将自身定义为 non-opaque，并确保不渲染 border 区域。

**"Optimized" Drawing**

组件的重叠问题更棘手。即使该组件的直接 siblings 组件没有与其重叠，其 non-ancestor relative (如"cousin"或"aunt")也很可能与其重叠。此时，一个复杂 hierarchy 中单个组件的 repainting 需要大量的检查工作以保证正确的 painting。为了减少不必要的遍历，Swing 为 `javax.swing.JComponent` 添加了一个只读属性  `isOptimizedDrawingEnabled`:

```java
public boolean isOptimizedDrawingEnabled()
```
	
含义：

- true: 该组件的所有直接子组件没有重叠。
- false: 该组件不保证其直接子组件没有重叠。

通过检查 `isOptimizedDrawingEnabled` 属性，Swing 在 `repaint` 时可以迅速缩小其搜索重叠组件的空间。

因为 `isOptimizedDrawingEnabled` 是只读的，所以修改默认值的唯一方法是扩展父类并覆盖该方法，返回需要的值。所有 Swing 标准组件的该属性值为 `true`，除了 `JLayeredPane`, `JDesktopPane` 和 `JViewPort`。

### paint 方法

AWT 轻量级组件的规则也适用于 Swing 组件。例如，在需要渲染时调用 `paint()`，只不过 Swing 将 `paint()` 调用分为三步：

```java
protected void paintComponent(Graphics g)
protected void paintBorder(Graphics g)
protected void paintChildren(Graphics g)
```

Swing 程序应该重写 `paintComponent()` 方法而不是 `paint()`。虽然理论上允许，但是一般不要重写 `paintBorder()` 或 `paintChildren()` 方法。此时程序只需要覆盖它需要扩展的部分，不会出现 AWT 中不调用`super.paint()` 就会导致轻量级子组件不显示的问题。

#### Painting and the UI Delegate

Swing 具有可插入式的 Laf，大多数 Swing 标准组件的 Laf 都是通过单独的 laf 对象实现(称为 UI delegates)。即标准组件的大部分绘制工作委托给了UI delegate，具体过程如下：

1. `paint()` 调用 `paintComponent()`
2. 如果 `ui` 属性为 non-null, `paintComponent()` 继续调用 `ui.update()`
3. 如果组件的 `opaque` 属性为 `true`, `ui.udpate()` 为组件填充背景，然后调用 `ui.paint()`
4. `ui.paint()` 绘制组件内容

由此可知，具有 UI delegate 的 Swing 组件子类(对比 `JComponent` 的直接之类)，在覆盖 `paintComponent` 方法时应该调用 `super.paintComponent()`:

```java
public class MyPanel extends JPanel {         
    protected void paintComponent(Graphics g) {            
        // Let UI delegate paint first
        // (including background filling, if I'm opaque) 
        super.paintComponent(g);              
        // paint my contents next....         
    }     
} 
```
            
如果因为某些原因扩展组件不希望 UI delegate 绘制，例如，完全替代组件的外观，则会省略 `super.paintComponent()` 调用，不过，如果 `opaque` 属性为 true，则需要自定义填充背景，如同 `Opaque` 属性讨论那一节所说.

### Paint 过程

Swing 处理"repaint"请求的方式和AWT稍有不同，虽然最终的结果一样 -- paint()被调用。Swing通过这种不同以支持 RepaintManager API(后面会讨论)，并且增强paint效率。在Swing中，painting可能有两种路径：
(A) paint请求来自第一个重量级父类(通常为JFrame, JDialog, JWindow, JApplet)：
	1. EDT调用该父类的paint()；
	2. Container.paint()的默认实现递归调用其轻量级子类的paint()方法；
当调用到第一个Swing组件，JComponent.paint()执行如下操作：
	1. 如果组件的 doubleBuffered属性为true，且RepaintManager的double-buffering已启用，则将Graphics 对象转换为一个合适的offscreen graphics。
	2. 调用paintComponent(如果启用doubled-buffered，则传入offscreen graphics);
	3. 调用paintBorder(如果启用doubled-buffered，则传入offscreen graphics)；
	4. 调用paintChildren()(如果启用doubled-buffered，则传入offscreen graphics)，该方法使用clip, opaque, optimizedDrawingEnabled属性决定哪个子类需要递归调用paint()。
	5. 如果组件的doubleBuffered属性为true，并且组件的RepaintManager的double-buffering已启用，则用原始的on-screen Graphics对象将offscreen image复制到对应的组件。
NOTE：在递归调用paint()时(paintChildren()#4)步骤#1和#5省略，因为一个Swing窗口中所有的轻量级组件都共享一个double-buffering offscreen image。

(B) paint 请求来自于javax.swing.JComponent的repaint()方法：
	1. JComponent.repaint()发送异步 repaint request 到组件的RepaintManger，RepaintManger使用invokeLater()将Runnable排到EDT进行后续处理。
	2. 在EDT运行的runnable触发组件的RepaintManager调用组件的paintImmediately()，paintImmediately执行如下过程：使用clip rectangle, opaque和optimizedDrawingEnabled属性确定paint操作应该从哪个组件开始('root'组件)，主要包括透明度和重叠组件的考虑。
	3. 如果root组件的doubleBuffered属性为true，并且root组件的RepaintManager的doule-buffering启用，则将

### Swing 绘制指南

Swing程序在编写 pain t代码时应该理解如下的意见：

1. 对 Swing 组件，`paint()` 因为system-triggered和app-triggered paint请求调用；Swing组件从来不调用update()；
	• 通过repaint()调用paint()，不要直接调用paint()；
	• 输出复杂的组件，应该调用带参数的repaint()，因减少渲染消耗；
Swing将paint()拆分为3个单独的方法：
	• paintComponent()
	• paintBorder()
	• paintChildren()
实现自定义paint代码的Swing扩展组件应该将其paint代码方法paintComponent()方法中(不是paint())。
Swing 引入了两个额外属性以最大化paint效率：
	• opaque：组件是否会paint其所有区域？
	• optimizedDrawingEnabled：该组件的子组件是否重叠？
如果组件的opque属性为true，表示它会paint其所有区域(包括用paintComponent()清理其背景)，否则会生成screen garbage。
将组件的opque或optimizedDrawingEnabled属性设置为false导致每个组件的paint操作需要更多处理，因此应当谨慎使用transparency 和 重叠组件。
具有UI delegate的Swing扩展组件(包括JPanel)，应当在自身的paintComponent()实现中调用super.paintComponent()。因为UI delegate需要为opaque 组件清理背景。
Swing 通过JComponent 的doubleBuffered属性以支持double-buffering，所有Swing组件该属性默认为true，将Swing容器的该属性为true，或导致其所有子类族类该属性都为true，不管这些子类该属性是何值。
强烈建议保持Swing组件的double-buffering属性为true。
对复杂的组件，应该充分利用clip rectangle以缩小需要paitn的区域。



## 参考

- https://www.oracle.com/java/technologies/painting.html