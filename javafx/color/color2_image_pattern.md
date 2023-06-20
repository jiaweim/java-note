# ImagePattern

2023-06-19, 21:44
****
## 1. 简介

`ImagePattern` 使用图片填充形状。图片可以完全填充形状，也平铺起来填充。使用 `ImagePattern` 的步骤：

1. 使用 `Image` 载入图片文件
2. 定义一个矩形，为图片显示区域，称为锚点矩形

如果待填充的区域大于定义的矩形，则锚点矩形重复出现以填满形状。

`ImagePattern` 有两个构造函数：

- `ImagePattern(Image image)`：使用图片填满整个区域
- `ImagePattern(Image image, double x, double y, double width, double height, boolean proportional)`：如果 `proportional` 为 true，则定义的锚点矩形是相对填充区域的比值，否则，为本地坐标系统值。

例如，下面创建的两个 `ImagePattern` 等价：

```java
ImagePatterm ip1 = new ImagePattern(anImage);
ImagePatterm ip2 = new ImagePattern(anImage, 0.0, 0.0, 1.0, 1.0, true);
```

## 2. 示例

下面是一个 37x25px 的蓝色圆角矩形：

![](blue_rounded_rectangle.png)

使用该图像创建 `ImagePattern`：

```java
Image img = create the image object...
ImagePattern p1 = new ImagePattern(img, 0, 0, 0.25, 0.25, true);
```

`ImagePattern` 会在要填充的形状的 (0,0) 位置创建一个锚点矩形，其宽度和高度均为要填充形状的 25%。因此，锚点矩形和水平和垂直方向均要重复 4 次。

- 填充矩形

```java
Rectangle r1 = new Rectangle(100, 50);
r1.setFill(p1);
```

![](Pasted%20image%2020230619213914.png)

- 填充三角形

```java
Polygon triangle = new Polygon(50, 0, 0, 50, 100, 50);
triangle.setFill(p1);
```

![](Pasted%20image%2020230619213918.png)

- 完整填充

用一张图像完整填充图形，需要将 `proportional` 设置为 true：

```java
ImagePatterm ip = new ImagePattern(yourImage, 0.0, 0.0, 1.0, 1.0, true)
```

- 完整示例

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ImagePatternApp extends Application {

    private Image img;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() {
        // Create an Image object
        final String imgPath = getClass().getResource("/picture/blue_rounded_rectangle.png").toExternalForm();
        img = new Image(imgPath);
    }

    @Override
    public void start(Stage stage) {
        // 锚点矩形从 (0,0) 开始，宽和高均为待填充矩形的 25%
        ImagePattern p1 = new ImagePattern(img, 0, 0, 0.25, 0.25, true);
        Rectangle r1 = new Rectangle(100, 50);
        r1.setFill(p1);
		
		// 锚点矩形从 (0,0) 开始，宽和高为待填充矩形的 50%
        ImagePattern p2 = new ImagePattern(img, 0, 0, 0.5, 0.5, true);
        Rectangle r2 = new Rectangle(100, 50);
        r2.setFill(p2);

        // 锚点矩形使用绝对边框
        ImagePattern p3 = new ImagePattern(img, 40, 15, 20, 20, false);
        Rectangle r3 = new Rectangle(100, 50);
        r3.setFill(p3);

        // 填充圆形
        ImagePattern p4 = new ImagePattern(img, 0, 0, 0.1, 0.1, true);
        Circle c = new Circle(50, 50, 25);
        c.setFill(p4);

        HBox root = new HBox();
        root.getChildren().addAll(r1, r2, r3, c);

        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.setTitle("Using Image Patterns");
        stage.show();
    }
}
```

![](Pasted%20image%2020230619213811.png)

