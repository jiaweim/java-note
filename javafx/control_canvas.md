# CONTENTS
- [CONTENTS](#contents)
- [概述](#%e6%a6%82%e8%bf%b0)
- [创建 Canvas](#%e5%88%9b%e5%bb%ba-canvas)
- [在 Canvas 上绘图](#%e5%9c%a8-canvas-%e4%b8%8a%e7%bb%98%e5%9b%be)
  - [绘制基本几何形状](#%e7%bb%98%e5%88%b6%e5%9f%ba%e6%9c%ac%e5%87%a0%e4%bd%95%e5%bd%a2%e7%8a%b6)
  - [绘制文本](#%e7%bb%98%e5%88%b6%e6%96%87%e6%9c%ac)
  - [绘制路径](#%e7%bb%98%e5%88%b6%e8%b7%af%e5%be%84)
  - [绘制图片](#%e7%bb%98%e5%88%b6%e5%9b%be%e7%89%87)
  - [像素修改](#%e5%83%8f%e7%b4%a0%e4%bf%ae%e6%94%b9)
- [清除 Canvas 区域](#%e6%b8%85%e9%99%a4-canvas-%e5%8c%ba%e5%9f%9f)
- [保存和恢复 GraphicsContext 配置](#%e4%bf%9d%e5%ad%98%e5%92%8c%e6%81%a2%e5%a4%8d-graphicscontext-%e9%85%8d%e7%bd%ae)
- [让 Canvas Resizable](#%e8%ae%a9-canvas-resizable)
- [关于线段变模糊](#%e5%85%b3%e4%ba%8e%e7%ba%bf%e6%ae%b5%e5%8f%98%e6%a8%a1%e7%b3%8a)
# 概述
`Canvas` 是一个图片（位图，bitmap），该类扩展自 `Node` 类，所以可以将 `Canvas` 添加到 scene graph，添加特效和转换等所有 `Node` 支持的操作。

`GraphicsContext` 通过一个缓冲区，对 `Canvas` 执行绘图操作。`GraphicsContext` 包含一系列的绘图命令，可实现对基本几何形状、图片、文本等对象的绘制。绘图所需的参数（图形上下文）也保存在其中。

每个 Canvas 只包含一个 GraphicsContext，以及一个缓冲区。如果没添加到 Scene，则可以在任意线程修改，添加到 scene 后，只能在 JavaFX Application Thread 修改。

`Canvas` 和 `GraphicsContext` 一起，可以完成各种绘图工作，可以实现像素级别的绘图操作。

`GraphicsContext` 包含如下的渲染属性：
|Attribute|Save/Restore?|Default Value|Description|
|---|---|---|---|
|Clip|Yes|No Clipping|渲染受限的各种剪辑路径反锯齿交叉点|
|Global Alpha|Yes|1.0|控制每个渲染操作的透明度|
|Global Blend Mode|Yes|SRC_OVER|BlendMode enum 用于控制每个渲染操|作的像素如何合并到现有图像中|
|Transform|Yes|Identity|一个 3x2 的2D 放射变换矩阵，用于控制 |canvas 图像映射到逻辑像素的方式|
|Effect|Yes|null|应用于渲染操作的特效|
|Fill Paint|Yes|BLACK|形状填充色|
|Stroke Paint|Yes|BLACK|形状轮廓颜色|
|Line Width|Yes|1.0|stroke 宽度|
|Line Cap|Yes|SQUARE|线段末端的样式|
|Line Join|Yes|MITER|线段连接样式|
|Miter Limit|Yes|10.0|MITER 连接样式中，相对线段宽度延伸最长的比值|
|Dashes|Yes|null|虚线模式|
|Dash Offset|Yes|0.0|虚线模式距离起点的距离|
|Font|Yes|Default Font|文本字体|
|Text Align|Yes|LEFT|文本水平对齐方式|
|Text Baseline|Yes|BASELINE|文本垂直对齐位置|
|Font Smoothing|Yes|GRAY|文本平滑处理类型|
|Current Path|No|Empty path|构建的路径，用于填充，绘图，剪辑操作|
|Fill Rule|Yes|NON_ZERO|确定路径内部的方法|


# 创建 Canvas
`Canvas` 类包含两个构造函数，一个无参，一个构造时指定大小：
```java
// Create a Canvas of zero width and height
Canvas canvas = new Canvas();

// Set the canvas size
canvas.setWidth(400);
canvas.setHeight(200);

// Create a 400X200 canvas
Canvas canvas = new Canvas(400, 200);
```

# 在 Canvas 上绘图
首先要获得 `GraphicsContext` 实例：
```java
// Get the graphics context of the canvas
GraphicsContext gc = canvas.getGraphicsContext2D();
```

然后使用 `GraphicsContext` 的命令绘图。绘制内容超出 canvas 坐标区间的被截断。在添加 `Canvas` 到 scene graph 之前，可以在任何线程使用 `GraphicsContext`，`Canvas`添加到 scene graph 之后，就只能在 JavaFX 应用线程中使用。

`GraphicsContext` 类包含绘制如下对象的方法：
- 基本几何形状
- 文本
- 路径
- 图像
- 像素点


## 绘制基本几何形状
`GraphicsContext` 提供了两类绘制基本图形的方法。`fillXxx()` 填充图形内部，`strokeXxx()` 绘制图形轮廓。
|fill方法|stroke方法|功能|
|---|---|---|
|fillArc()|strokeArc()|弧线|
|fillOval()|strokeOval()|椭圆|
|fillPolygon()|strokePolygon()|多边形|
|fillRect()|strokeRect()|矩形|
|fillRoundRect()|strokeRoundRect()|圆角矩形|
说明：
- `strokeLine()` 绘制直线
- `strokePolygon()` 绘制多边形

例1：绘制三角形
```java
gc.beginPath(); // 开始绘制路径
gc.moveTo(30.5, 30.5); // 从点 (30.5,30.5) 开始下笔
gc.lineTo(150.5, 30.5);
gc.lineTo(150.5, 150.5);
gc.lineTo(30.5, 30.5);
gc.stroke();
```

## 绘制文本
方法：
```java
filltext(String text, double x, double y)
fillText(String text, double x, double y, double maxWidth)
strokeText(String text, double x, double y)
strokeText(String text, double x, double y, double maxWidth)
```

两个方法都有重载，指定最大宽度的版本，当实际文本的宽度超过最大宽度时，会自动调整文本大小。
可以使用 `fillText()` 和 `strokeText()` 方法绘制文本。
Font Smoothing 属性只能用于 fillText 方法。

```java
Canvas canvas = new Canvas(200, 50);
GraphicsContext gc = canvas.getGraphicsContext2D();
gc.setLineWidth(1.0);
gc.setStroke(Color.BLACK);
gc.strokeText("Drawing Text", 10, 10);
gc.strokeText("Drawing Text", 100, 10, 40);
```

## 绘制路径
`GraphicsContext` 包含如下用于绘制路径的方法：
- beginPath()
- lineTo(double x1, double y1)
- moveTo(double x0, double y0)
- quadraticCurveTo(double xc, double yc, double x1, double y1)
- appendSVGPath(String svgpath)
- arc(double centerX, double centerY, double radiusX, double - radiusY, double startAngle, double length)
- arcTo(double x1, double y1, double x2, double y2, double - radius)
- bezierCurveTo(double xc1, double yc1, double xc2, double yc2, - double x1, double y1)
- closePath()
- stroke()
- fill()

`beginPath()` 和 `closePath()` 开始和关闭路径。`stroke()` 和 `fill()` 则确定是绘制轮廓还是填充，在最后调用。例：
```java
Canvas canvas = new Canvas(200, 50);
GraphicsContext gc = canvas.getGraphicsContext2D();
gc.setLineWidth(2.0);
gc.setStroke(Color.BLACK);
gc.beginPath();
gc.moveTo(25, 0);
gc.appendSVGPath("L50, 25L0, 25");
gc.closePath();
gc.stroke();
```

## 绘制图片
使用 `drawImage()` 方法绘制图片：
- `void drawImage(Image img, double x, double y)`
- `void drawImage(Image img, double x, double y, double w, double h)`
- `void drawImage(Image img, double sx, double sy, double sw, double sh, double dx, double dy, double dw, double dh)`

没啥意思，等要用的时候再来看

## 像素修改
可以直接修改 canvas 上的像素。使用 `GraphicsContext` 的`getPixelWriter()` 方法获得 `PixelWriter`，然后就可以使用 `PixelWriter` 向 canvas 写像素点。

```java
Canvas canvas = new Canvas(200, 100);
GraphicsContext gc = canvas.getGraphicsContext2D();
PixelWriter pw = gc.getPixelWriter();
```

# 清除 Canvas 区域
`canvas` 是透明的。像素点则具有颜色和不透明度。有时候，我们可能需要清理 canvas 的部分区域，从而让其恢复全透明的状态，使用 `clearRect()` 方法：
```java
// Clear the top-left 100X100 rectangular area from the canvas
gc.clearRect(0, 0, 100, 100);
```

# 保存和恢复 GraphicsContext 配置
`GraphicsContext` 的当前配置用于随后的绘制。例如，如果设置 `linewidth=5px`，则随后绘制的线条宽度都是 5px。

有时候，我们需要暂时修改 `GraphicsContext`的配置，随后恢复原来的配置。 `save()` 方法用于保存当前配置，`restore()`用于将配置恢复到之前保存的状态。


# 让 Canvas Resizable
按如下覆盖对应方法。
```java
@Override
public boolean isResizable(){
    return true;
}

@Override
public double prefWidth(double height){
    return getWidth();
}

@Override
public double prefHeight(double width){
    return getHeight();
}
```

然后在构造函数中添加监听器：
```java
public ResizableCanvas(){
    widthProperty().addListener((observable, oldValue, newValue) -> draw());
    heightProperty().addListener((observable, oldValue, newValue) -> draw());
}
```

最后， Canvas 大小和对应的容器绑定：
```java
canvas.widthProperty().bind(root.widthProperty());
canvas.heightProperty().bind(root.heightProperty());
```

# 关于线段变模糊
避免线段边沿模糊的方法：
- 对奇数：采用 0.5 这样的位置比较好。
- 对偶数：采用整数比较好。
