# Canvas 操作

2023-07-13, 14:37
添加 GraphicsContext 详解
2023-06-12
****
## 1. 创建 Canvas

`Canvas` 类包含两个构造函数，一个无参，一个构造时指定大小：

```java
// 默认高度和宽度为 0
Canvas canvas = new Canvas();

// 设置 canvas 尺寸
canvas.setWidth(400);
canvas.setHeight(200);

// 直接创建 400x200 的 canvas
Canvas canvas = new Canvas(400, 200);
```

## 2. 绘制操作

创建 canvas 后，需要使用 `getGraphicsContext2D()` 获得其 graphicsContext：

```java
GraphicsContext gc = canvas.getGraphicsContext2D();
```

所有绘图命令都在 `GraphicsContext` 类中。绘制内容超出 canvas 区域的被截断。在添加 `Canvas` 到 scene graph 之前，可以在任何线程使用 `GraphicsContext`，`Canvas`添加到 scene graph 之后，就只能在 JavaFX 应用线程中使用。

`GraphicsContext` 类包含绘制如下对象的方法：

- 基本几何形状
- 文本
- 路径
- 图像
- 像素点

### 2.1. 基本形状

`GraphicsContext` 提供了两类绘制基本图形的方法：

- `fillXxx()` 填充图形内部
- `strokeXxx()` 绘制图形轮廓

| fill方法          | stroke方法          | 功能     |
| ----------------- | ------------------- | -------- |
| `fillArc()`       | `strokeArc()`       | 弧线     |
| `fillOval()`      | `strokeOval()`      | 椭圆     |
| `fillPolygon()`   | `strokePolygon()`   | 多边形   |
| `fillRect()`      | `strokeRect()`      | 矩形     |
| `fillRoundRect()` | `strokeRoundRect()` | 圆角矩形 |
|                   | `strokeLine()`      | 线段     |
|                   | `strokePolyline()`  | 折线     |

例如，绘制一个 100px 宽 50px 高的矩形：

```java
Canvas canvas = new Canvas(200, 100);
GraphicsContext gc = canvas.getGraphicsContext2D();
gc.setLineWidth(2.0);
gc.setStroke(Color.RED);
gc.strokeRect(0, 0, 100, 50);
```

### 2.2. 绘制文本

`GraphicsContext` 提供 `fillText()` 和 `strokeText()` 绘制文本：

```java
filltext(String text, double x, double y)
fillText(String text, double x, double y, double maxWidth)
strokeText(String text, double x, double y)
strokeText(String text, double x, double y, double maxWidth)
```

指定最大宽度的重载版本，当实际文本宽度超过最大宽度时，会自动调整文本大小。

```java
Canvas canvas = new Canvas(200, 50);
GraphicsContext gc = canvas.getGraphicsContext2D();
gc.setLineWidth(1.0);
gc.setStroke(Color.BLACK);
gc.strokeText("Drawing Text", 10, 10);
gc.strokeText("Drawing Text", 100, 10, 40);
```

```ad-note
Font Smoothing 属性只能用于 `fillText` 方法
```

### 2.3. 绘制路径

可以使用 path 命令和 SVG 路径字符串创建任意形状。一条路径由多条子路径组成。`GraphicsContext` 包含如下绘制路径的方法：

- `beginPath()`
- `lineTo(double x1, double y1)`
- `moveTo(double x0, double y0)`
- `quadraticCurveTo(double xc, double yc, double x1, double y1)`
- `appendSVGPath(String svgpath)`
- `arc(double centerX, double centerY, double radiusX, double radiusY, double startAngle, double length)`
- `arcTo(double x1, double y1, double x2, double y2, double - radius)`
- `bezierCurveTo(double xc1, double yc1, double xc2, double yc2, - double x1, double y1)`
- `closePath()`
- `stroke()`
- `fill()`

`beginPath()` 和 `closePath()` 开始和关闭路径。`stroke()` 和 `fill()` 则确定是绘制轮廓还是填充，在最后调用。例如，绘制一个三角形：

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

### 2.4. 绘制图像

使用 `drawImage()` 方法绘制图片：

- `void drawImage(Image img, double x, double y)`
- `void drawImage(Image img, double x, double y, double w, double h)`
- `void drawImage(Image img, double sx, double sy, double sw, double sh, double dx, double dy, double dw, double dh)`

可以绘制完整图像，也可以只绘制一部分。绘制的图像可以在 canvas 上拉伸或压缩。

- 在 (10,10) 坐标绘以原始尺寸绘制整个图像

```java
Image image = new Image("your_image_URL");
Canvas canvas = new Canvas(400, 400);
GraphicsContext gc = canvas.getGraphicsContext2D();
gc.drawImage(image, 10, 10);
```

- 将整个图像绘制在 100x150px 的区域。图像时拉伸还是压缩取决于其原始大小

```java
gc.drawImage(image, 10, 10, 100, 150);
```

- 绘制一部分图像，假设图像大于 100x150 px。从图像的左上角开始 (0,0)，绘制图像 100x150px 部分；而 canvas 从 (10,10) 开始，将截取的图像绘制在 200x200px 区域

```java
gc.drawImage(image, 0, 0, 100, 150, 10, 10, 200, 200);
```

### 2.5. 绘制像素

可以直接修改 canvas 上的像素。`GraphicsContext` 的 `getPixelWriter()` 方法返回 `PixelWriter`，可以直接在关联的 canvas 上写入像素点。

```java
Canvas canvas = new Canvas(200, 100);
GraphicsContext gc = canvas.getGraphicsContext2D();
PixelWriter pw = gc.getPixelWriter();
```

## 3. 清除 canvas 区域

`canvas` 是一个透明区域。像素点则具有颜色和不透明度。有时候，我们可能需要清理 canvas 的部分区域，从而让其恢复全透明的状态，使用 `clearRect()` 方法：

```java
// Clear the top-left 100X100 rectangular area from the canvas
gc.clearRect(0, 0, 100, 100);
```

## 4. 保存和恢复 GraphicsContext 配置

`GraphicsContext` 的当前配置用于随后所有的绘制操作。例如，如果设置 `linewidth=5px`，则随后绘制的线条宽度都是 5px。

有时候，我们需要暂时修改 `GraphicsContext` 的配置，执行某些操作后，再恢复原来的配置：

- `save()` 方法用于保存当前配置
- `restore()`用于将配置恢复到之前保存的状态

例如，你需要按顺序执行如下操作：

- 画一个没有特效的矩形
- 画一个带镜像特效的字符串
- 画一个没有特效的矩形

实现这一目标（错误尝试）：

```java
Canvas canvas = new Canvas(200, 120);
GraphicsContext gc = canvas.getGraphicsContext2D();

gc.strokeRect(10, 10, 50, 20);
gc.setEffect(new Reflection());
gc.strokeText("Chatar", 70, 20);
gc.strokeRect(120, 10, 50, 20);
```

由于没有将 `GraphicsContext` 配置复原，第二个矩形也有镜像特效。

为了解决该问题，可以在绘制文本后将 `Effect` 设置为 `null`。但是，如果你需要修改多个属性，手动复原就比较麻烦；如果你收到的是 GraphicsContext 的引用，更是不知道之前的状态。这种手动设置的方法不实用。

`save()` 将 `GraphicsContext` 的当前状态保存在堆栈中，`restore()` 将 `GraphicsContext` 恢复上上次保存的状态。

正确实现上述功能：

```java
Canvas canvas = new Canvas(200, 120);
GraphicsContext gc = canvas.getGraphicsContext2D();

gc.strokeRect(10, 10, 50, 20);
// 保存当前状态
gc.save();
// 修改状态以绘制特效文本
gc.setEffect(new Reflection());
gc.strokeText("Chatar", 70, 20);
// 回复到上次调用 save() 时的状态，绘制第二个矩形
gc.restore();
gc.strokeRect(120, 10, 50, 20);
```

## 5. Canvas 示例

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.nio.ByteBuffer;

public class CanvasTest extends Application {

    private static final int RECT_WIDTH = 20;
    private static final int RECT_HEIGHT = 20;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {

        Canvas canvas = new Canvas(400, 100);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 设置线宽和填充颜色
        gc.setLineWidth(2.0);
        gc.setFill(Color.RED);

        // 画一个圆角矩形
        gc.strokeRoundRect(10, 10, 50, 50, 10, 10);

        // 填充一个椭圆
        gc.fillOval(70, 10, 50, 20);

        // 画一段文本
        gc.strokeText("Hello Canvas", 10, 85);

        // 画一个图像
        String imagePath = CanvasTest.class.getResource("ksharan.jpg").toExternalForm();
        Image image = new Image(imagePath);
        gc.drawImage(image, 130, 10, 60, 80);

        // Write custom pixels to create a pattern
        writePixels(gc);

        Pane root = new Pane();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Drawing on a Canvas");
        stage.show();
    }

    private void writePixels(GraphicsContext gc) {
    
        byte[] pixels = this.getPixelsData();
        PixelWriter pixelWriter = gc.getPixelWriter();

        // Our data is in BYTE_RGB format
        PixelFormat<ByteBuffer> pixelFormat = PixelFormat.getByteRgbInstance();

        int spacing = 5;
        int imageWidth = 200;
        int imageHeight = 100;

        // Roughly compute the number of rows and columns
        int rows = imageHeight / (RECT_HEIGHT + spacing);
        int columns = imageWidth / (RECT_WIDTH + spacing);

        // Write the pixels to the canvas
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                int xPos = 200 + x * (RECT_WIDTH + spacing);
                int yPos = y * (RECT_HEIGHT + spacing);
                pixelWriter.setPixels(xPos, yPos,
                        RECT_WIDTH, RECT_HEIGHT,
                        pixelFormat,
                        pixels, 0,
                        RECT_WIDTH * 3);
            }
        }
    }

    private byte[] getPixelsData() {
        // Each pixel in the w X h region will take 3 bytes
        byte[] pixels = new byte[RECT_WIDTH * RECT_HEIGHT * 3];

        // Height to width ration
        double ratio = 1.0 * RECT_HEIGHT / RECT_WIDTH;

        // Generate pixel data
        for (int y = 0; y < RECT_HEIGHT; y++) {
            for (int x = 0; x < RECT_WIDTH; x++) {
                int i = y * RECT_WIDTH * 3 + x * 3;
                if (x <= y / ratio) {
                    pixels[i] = -1;  // red -1 means 255 (-1 & 0xff = 255)
                    pixels[i + 1] = 0; // green = 0
                    pixels[i + 2] = 0; // blue = 0
                } else {
                    pixels[i] = 0;    // red = 0
                    pixels[i + 1] = -1; // Green 255
                    pixels[i + 2] = 0;  // blue = 0
                }
            }
        }
        return pixels;
    }
}

```

![[Pasted image 20230612095146.png]]

## 6. GraphicsContext 详解

GraphicsContext 通过 buffer 向 Canvas 发送绘图命令。GraphicsContext 的绘图命令会将必要的参数推送到 buffer，然后在 pulse 结束前由渲染线程将这些内容渲染到 Canvas。

每个 Canvas 有且只有一个 GraphicsContext 和一个 buffer。将 Canvas 添加到 scene 前，可以在任何线程修改。将 Canvas 添加到 scene 后，则只能在 JAT 线程修改。

GraphicsContext 还管理了一个 state 对象堆栈，可以随时保存或恢复。

## 7. GraphicsContext attributes

`GraphicsContext` 维护了以下渲染属性，这些属性会影响渲染方法：

| Attribute              | Save/Restore? | Default value | Description                                |
| ---------------------- | ------------- | ------------- | ------------------------------------------ |
| **常用渲染 Attribute** |               |               |                                            |
| Clip                   | Yes           | No Clipping   |                                            |
| Global Alpha           | Yes           | 1.0           |                                            |
| Global Blend Mode      | Yes           | SRC_OVER      |                                            |
| Transform              | Yes           | Identity      |                                            |
| Effect                 | Yes           | null          |                                            |
| **Fill Attributes**    |               |               |                                            |
| Fill Paint             | Yes           | BLACK         | 填充操作中应用于形状内部的 Paint           |
| **Stroke Attributes**  |               |               |                                            |
| Stroke Paint           | Yes           | BLACK         | 描边操作（stroke）中应用于形状边界的 Paint |
| Line Width             | Yes           | 1.0           | 描边操作中应用于形状边界的 stroke 宽度     |
| Line Cap               |               |               |                                            |
| Line Join              |               |               |                                            |
| Miter Limit            |               |               |                                            |
| Dashes                 | Yes           | null          | stroke 操作中应用于形状边界的虚线长度数组  |
| Dash Offset            | Yes           | 0.0           |                                            |
| **Text Attributes**    |               |               |                                            |
| Font                   |               |               |                                            |
| Text Align             |               |               |                                            |
| Text Baseline          |               |               |                                            |
| Font Smoothing         |               |               |                                            |
| **Path Attributes**    |               |               |                                            |
| Current Path           |               |               |                                            |
| Fill Rule              |               |               |                                            |
| **Image Attributes**   |               |               |                                            |
| Image Smoothing        |               |               |                                            |



**lineWidth**

```java
public void setLineWidth(double lw)
public double getLineWidth()
```

**lineDashes**

```java
public void setLineDashes​(double... dashes)
public double[] getLineDashes()
```

**stroke**

```java
public void setStroke​(Paint p)
public Paint getStroke()
```

## 8. 方法属性对照表

`GraphicsContext` 不同方法使用的渲染属性如下表所示。

| 方法 | 常用属性 | Fill 属性 | Stroke 属性 | Text 属性 | Path 属性 | Image 属性 |
| ---- | -------- | --------- | ----------- | --------- | --------- | ---------- |
|      |          |           |             |           |           |            |