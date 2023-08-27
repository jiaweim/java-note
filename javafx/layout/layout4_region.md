# Region

2023-08-10, 09:35
modify: 修改样式，完善内容
2023-07-07, 14:01
@author Jiawei Mao
****
## 1. 简介

`Region` 是一个 `Node`，继承自 `Parent`：

- `Region` 的 backgrounds 和 borders 可以通过 CSS 设置样式
- `Region` 通常是圆角矩形，但可以通过 CSS 修改为其它形状
- `Region` 可以包含其它 `Region`（sub-regions）
- 与 `Group` 不同，`Region` 自身有大小
- `Region` 为 resizable 类型

`Region` 不直接作为容器，一般使用 `Pane` 的子类，或者扩展 `Pane` 自定义容器。

`Region` 的绘制区域分为几个部分：

- backgrounds (fills and images)
- **content-area**：绘制 children 的地方
- **padding**：content-area 和 border 之间的可选区域。如果 padding 为 0，则 padding edge 和 content edge 重合
- **borders** (strokes and images)：padding 外周区域。如果 border 为 0，则 border edge 和 padding edge 重合
- **margin**：外边距，Region 在其 parent 内与其它组件之间的空间
- region **insets**：`Region` 的 `layoutBounds` 和 content area 之间的距离为 insets（内边距）。`Region` 类根据属性自动计算 insets，并提供 read-only 属性 `insets`，便于查询

@import "images/Pasted%20image%2020230706193337.png" {width="450px" title=""}

`Region` 按照如下顺序渲染：

|顺序|内容|说明|
|---|---|---|
|1|background fills|可选|
|2|background images|可选|
|3|border strokes|可选|
|4|border images|可选|
|5|content||

content, padding 和 borders 影响 `Region` 的 `layoutBounds`。将 borders 完全绘制在 `Region` 的 `layoutBounds` 的外面时，borders 不影响 `Region` 的 `layoutBounds`。margin 不影响 `Region` 的 `layoutBounds`。

## 2. backgrounds

`Region` 可以具有由 fills 和/或 images 组成的背景。

### 2.1. background fill

`fill` 包括 `color`, radii (四个角), insets (四个边)：

- fill color 定义填充颜色
- radii 定义四个角的半径，0 时为矩形
- insets 定义 `Region` 的 `layoutBounds` 和 `fill` 外周的距离

例如，顶部 10px inset 表示 `layoutBounds` 上边内 10px 没有填充。

!!! tip
    背景填充区域为 `Region` 的 `layoutBounds` 内区域。    

如果 inset 为负数，填充区域会超出 `Region` 的 `layoutBounds`。

下面的 CSS 属性定义 `Region` 的 background fill:

- `-fx-background-color`
- `-fx-background-radius`
- `-fx-background-insets`

**示例：** 将 `Region` 的整个 `layoutBounds` 填充为红色

```css
-fx-background-color: red;
-fx-background-insets: 0;
-fx-background-radius: 0;
```

**示例：** 填充 2 个 fills

```css
-fx-background-color: lightgray, red;
-fx-background-insets: 0, 4;
-fx-background-radius: 4, 2;
```

第 1 个 fill 使用浅灰色填充整个 `Region` (0px insets)；四个角 4px 半径，即圆角矩形。
第 2 个 fill 使用红色填充 `Region`；四个边 4px insets，即 `layoutBounds` 内 4px 宽的区域没有填充，这部分为第一次填充的浅灰色；2px 半径。

也可以通过代码设置 `Region` 的 background。background 由 `Background` 类表示，该类定义的 `Background.EMPTY` 常量表示空背景（无 fill, 无 image）。

!!! tip
    `Background` 是 immutable 对象，可以安全地用作多个 `Region` 的背景。    

一个 `Background`  可以包含 0 到多个 fills 和 images：

- `BackgroundFill` 类表示 fill
- `BackgroundImage` 类表示 image

`Region` 包含 `ObjectProperty<Background>` 类型的 `background` 属性，调用 `Region.setBackground(Background bg)` 可设置背景。

下面的代码片段创建一个包含 2 个 `BackgroundFill` 的 `Background`，效果与上面的 CSS 定义一样：

```java
BackgroundFill lightGrayFill = new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(4), new Insets(0));
BackgroundFill redFill = new BackgroundFill(Color.RED, new CornerRadii(2), new Insets(4));
Background bg = new Background(lightGrayFill, redFill);
```

使用 `CornerRadii` 定义角半径，`Insets` 定义 insets。

**示例：** `Pane` 是 `Region` 的一种，下面演示使用 API 和 CSS 设置 `Pane` 的 background。

```java{.line-numbers}
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class BackgroundFillTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Pane p1 = this.getCSSStyledPane();
        Pane p2 = this.getObjectStyledPane();

        p1.setLayoutX(10);
        p1.setLayoutY(10);

        // Place p2 20px right to p1
        p2.layoutYProperty().bind(p1.layoutYProperty());
        p2.layoutXProperty().bind(p1.layoutXProperty().add(p1.widthProperty()).add(20));

        Pane root = new Pane(p1, p2);
        root.setPrefSize(240, 70);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Setting Background Fills for a Region");
        stage.show();
        stage.sizeToScene();
    }

    public Pane getCSSStyledPane() {
        Pane p = new Pane();
        p.setPrefSize(100, 50);
        // 使用 CSS 设置样式
        p.setStyle("-fx-background-color: lightgray, red;"
                + "-fx-background-insets: 0, 4;"
                + "-fx-background-radius: 4, 2;");

        return p;
    }

    public Pane getObjectStyledPane() {
        Pane p = new Pane();
        p.setPrefSize(100, 50);

        BackgroundFill lightGrayFill = new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(4), new Insets(0));

        BackgroundFill redFill = new BackgroundFill(Color.RED, new CornerRadii(2), new Insets(4));

        Background bg = new Background(lightGrayFill, redFill);
        p.setBackground(bg);

        return p;
    }
}
```

@import "images/Pasted%20image%2020230706204954.png" {width="250px" title=""}

### 2.2. background image

下面的 CSS 属性定义 `Region` 的 background image：

|CSS属性|功能|
|---|---|
|-fx-background-image|背景图片的 CSS URL|
|-fx-background-repeat|图片重复填充方式|
|-fx-background-position|图片在 `Region` 中的位置|
|-fx-background-size|图片相对 `Region` 的大小|

**示例：** 

```css
-fx-background-image: URL('your_image_url_goes_here');
-fx-background-repeat: space;
-fx-background-position: center;
-fx-background-size: cover;
```

下面的代码与上面的 CSS 效果一样：

```java{.line-numbers}
Image image = new Image("your_image_url_goes_here");
BackgroundSize bgSize = new BackgroundSize(100, 100, true, true, false, true);
BackgroundImage bgImage = new BackgroundImage(image, 
                                              BackgroundRepeat.SPACE,
                                              BackgroundRepeat.SPACE,
                                              BackgroundPosition.DEFAULT,
                                              bgSize);
// Create a Background object with an BackgroundImage object
Background bg = new Background(bgImage);
```

## 3. padding

`Region` 的 padding 指定 content-area 和 border 之间的可选区域。`Region` 使用 `ObjectProperty<Insets>` 类型的 `padding` 属性存储该值，使用 `setPadding()` 方法设置。

```java{.line-numbers}
HBox hb = new HBox();

// HBox 所有边的 padding 都为 10px
hb.setPadding(new Insets(10));

// 为 HBox 的不同边设置不同 padding：
// 2px top, 4px right, 6px bottom, 8px left
hb.setPadding(new Insets(2, 4, 6, 8));
```

!!! warning
    并非所有 Region 子类都遵守设置的 padding，如 `Pane` 就不遵守。

## 4. borders

`Region` 的 border 由 strokes 和/或 images 组成，没有指定 stroke 和 image 时，border 为空。

strokes 和 images 按照指定的顺序渲染，所有 strokes 在 images 之前渲染。

### 4.1. border stroke

stroke 包含 5 个属性：

| 属性   | 说明                                |
| ------ | ---------------------------------- |
| color  | stroke 颜色，4 个边可以定义不同颜色 |
| style  | stroke 样式，4 个边可以定义不同样式 |
| radii  | 4 个角的半径，0 对应矩形           |
| width  | stroke 宽度，4 个边可以定义不同宽度 |
| insets | 4 个边的 insets                   |

**style** 定义 stroke 样式，包括：

- 线条样式，如 solid, dashed 等
- 线条位置，如 inside, outside, centered 等

**insets** 定义 stroke 和 layoutBounds 的距离，且正负数对应不同方向，如下图所示：

@import "images/Pasted%20image%2020230706212920.png" {width="600px" title=""}

border stroke 相对 `Parent` 的 layoutBounds 的位置有三类：inside, outside 和 centered。

stroke 相对 `Parent` 的 `layoutBounds` 的位置受 2 个属性的影响，insets 和 styles:

- 如果 style 为 inside，stroke 绘制在 insets 内
- 如果 style 为 outside，stroke 绘制在 insets 外
- 如果 style 为 centered，stroke 一半在 insets 内，一半在 insets 外

如下图所示:

@import "images/Pasted%20image%2020230706213459.png" {width="500px" title=""}

其中虚线矩形为 Region 的 `layoutBounds`。borders 为浅灰色。

下面的 CSS 属性定义 `Region` 的 border stroke：

|CSS属性|说明|实例|
|---|---|---|
|-fx-border-color|边框颜色|red;|
|-fx-border-style|边框线条样式|solid inside;|
|-rx-border-width|边框宽度|10;|
|-fx-border-radius|半径|5;|
|-fx-border-insets|内边距|0;|

**示例：** 宽度为 10px，红色 border

由于 insets 为 0，样式为 inside，所以 boder 外周正好落在 layoutBounds 上。

```css
-fx-border-color: red;
-fx-border-style: solid inside;
-fx-border-width: 10;
-fx-border-insets: 0;
-fx-border-radius: 5;
```

**示例：** 定义 2 个 strokes

第 1 个 stroke 在 layoutBounds 的 inside；第 2 个 stroke 在 layoutBounds 的 outside。

```css
-fx-border-color: red, green;
-fx-border-style: solid inside, solid outside;
-fx-border-width: 5, 2 ;
-fx-border-insets: 0, 0;
-fx-border-radius: 0, 0;
```

!!! tip
    border stroke 落在 `layoutBounds` 外面的部分不影响 `Region` 的 `layoutBounds`，落在 `layoutBounds` 里面的部分则影响。

border 还有 **insets** 和 **outsets**，根据 strokes 和 images 自动计算：

- 如果 strokes 和 images 在 layoutBounds 内，则 layoutBounds 和 border 的 **inner** edges 距离为 border 的 insets
- 如果 strokes 和 images 在 layoutBounds 外，则 layoutBounds 和 border 的 **outer** edges 距离为 boder 的 outsets

stroke insets 确定 stroke 位置，而 border 的 insets 和 outsets 指出 border 与 `Region` 的 `layoutBounds` 的距离。

**示例：** 演示 border 的 insets 和 outsets

虚线是 `Region` 的 layoutBounds，border 有 2 个 strokes：1 个绿色，1 个红色。`Region` 尺寸为 (150px,50px)。

```css
-fx-background-color: white;
-fx-padding: 10;
-fx-border-color: red, green, black;
-fx-border-style: solid inside, solid outside, dashed centered;
-fx-border-width: 10, 8, 1;
-fx-border-insets: 12, -10, 0;
-fx-border-radius: 0, 0, 0;
```

@import "images/Pasted%20image%2020230707090725.png" {width="400px" title=""}

- border 的 insets 在 4 个方向均为 22px，即红色 stroke-insets 12 px 加上 stroke-width 10px
- border 的 outsets 在 4 个方向均为 18px，即绿色 stroke -insets 10px 加上 stroke-width 8px

也可以使用代码设置 border，`Border` 类表示 `Region` border。`Border.EMPTY`  常量表示空 border。`Border` 为 immutable 对象，可以安全地用在多个 `Region` 中。

`Border` 可以有 0 到多个 strokes 和 images。`Region.border` 属性为 `ObjectProperty<Border>`，存储 border 引用。使用 `Region.setBorder(Border b)` 设置 border.

`BorderStroke` 类表示 stroke，BorderImage 表示 image。

`BorderStroke` 构造函数：

```java
public BorderStroke(Paint stroke, BorderStrokeStyle style, 
                    CornerRadii radii, BorderWidths widths)
                    
public BorderStroke(Paint stroke, BorderStrokeStyle style, 
                    CornerRadii radii, BorderWidths widths, Insets insets)
                    
public BorderStroke(Paint topStroke, Paint rightStroke, 
                    Paint bottomStroke, Paint leftStroke, 
                    BorderStrokeStyle topStyle, BorderStrokeStyle rightStyle,  
                    BorderStrokeStyle bottomStyle, BorderStrokeStyle leftStyle,  
                    CornerRadii radii, BorderWidths widths, Insets insets)
```

`BorderStrokeStyle` 类表示 stroke style。`BorderWidths` 表示 stroke 在 4 个边的宽度。

`BorderWidths` 可以使用绝对值或相对 Region 尺寸的百分比指定。

**示例：** 为 `Pane` 指定 `Border`

```java{.line-numbers}
BorderStrokeStyle style = new BorderStrokeStyle(StrokeType.INSIDE, 
                                                StrokeLineJoin.MITER,
                                                StrokeLineCap.BUTT, 
                                                10,
                                                0,
                                                null);
BorderStroke stroke = new BorderStroke(Color.GREEN, style, 
                                       CornerRadii.EMPTY,
                                       new BorderWidths(8),
                                       new Insets(10));
Pane p = new Pane();
p.setPrefSize(100, 50);
Border b = new Border(stroke);
p.setBorder(b);
```

`Border` 类的 `getInsets()` 和 `getOutsets()` 返回 Border 的 insets 和 outsets。两个方法都返回 Insets 对象。记住，`Border` 的 insets 和 outsets 与 stroke insets 不同，这 2 个是根据 strokes 的 insets 和 styles 自动计算。

`Border.getStrokes()` 返回 `List<BorderStroke>`，即所有的 strokes。

`Border.getImages()` 返回 `List<BorderImage>`，即所有的 images.

**示例：** 代码演示 border

创建两个 `Pane`，一个用 CSS 设置 border，一个用代码设置 border。输出 borders 的 insets 和 outsets。

```java{.line-numbers}
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class BorderStrokeTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Pane p1 = this.getCSSStyledPane();
        Pane p2 = this.getObjectStyledPane();

        // Place p1 and p2
        p1.setLayoutX(20);
        p1.setLayoutY(20);
        p2.layoutYProperty().bind(p1.layoutYProperty());
        p2.layoutXProperty().bind(
                p1.layoutXProperty().add(p1.widthProperty()).add(40));

        Pane root = new Pane(p1, p2);
        root.setPrefSize(300, 120);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Setting Background Fills for a Region");
        stage.show();

        // Print borders details
        printBorderDetails(p1.getBorder(), p2.getBorder());
    }

    public Pane getCSSStyledPane() {
        Pane p = new Pane();
        p.setPrefSize(100, 50);
        p.setStyle("-fx-padding: 10;" +
                "-fx-border-color: red, green, black;" +
                "-fx-border-style: solid inside, solid outside, dashed centered;" +
                "-fx-border-width: 10, 8, 1;" +
                "-fx-border-insets: 12, -10, 0;" +
                "-fx-border-radius: 0, 0, 0;");

        return p;
    }

    public Pane getObjectStyledPane() {
        Pane p = new Pane();
        p.setPrefSize(100, 50);
        p.setBackground(Background.EMPTY);
        p.setPadding(new Insets(10));

        // Create three border strokes
        BorderStroke redStroke = new BorderStroke(Color.RED,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(10),
                new Insets(12));

        BorderStrokeStyle greenStrokeStyle = new BorderStrokeStyle(StrokeType.OUTSIDE,
                StrokeLineJoin.MITER,
                StrokeLineCap.BUTT,
                10,
                0,
                null);
        BorderStroke greenStroke = new BorderStroke(Color.GREEN,
                greenStrokeStyle,
                CornerRadii.EMPTY,
                new BorderWidths(8),
                new Insets(-10));

        List<Double> dashArray = new ArrayList<>();
        dashArray.add(2.0);
        dashArray.add(1.4);

        BorderStrokeStyle blackStrokeStyle
                = new BorderStrokeStyle(StrokeType.CENTERED,
                StrokeLineJoin.MITER,
                StrokeLineCap.BUTT,
                10,
                0,
                dashArray);
        BorderStroke blackStroke = new BorderStroke(Color.BLACK,
                blackStrokeStyle,
                CornerRadii.EMPTY,
                new BorderWidths(1),
                new Insets(0));

        // Create a Border object with three BorderStroke objects
        Border b = new Border(redStroke, greenStroke, blackStroke);
        p.setBorder(b);

        return p;
    }

    private void printBorderDetails(Border cssBorder, Border objectBorder) {
        System.out.println("cssBorder insets:" + cssBorder.getInsets());
        System.out.println("cssBorder outsets:" + cssBorder.getOutsets());
        System.out.println("objectBorder insets:" + objectBorder.getInsets());
        System.out.println("objectBorder outsets:" + objectBorder.getOutsets());

        if (cssBorder.equals(objectBorder)) {
            System.out.println("Borders are equal.");
        } else {
            System.out.println("Borders are not equal.");
        }
    }
}
```

@import "images/Pasted%20image%2020230707093712.png" {width="300px" title=""}

```
cssBorder insets:Insets [top=22.0, right=22.0, bottom=22.0, left=22.0]
cssBorder outsets:Insets [top=18.0, right=18.0, bottom=18.0, left=18.0]
objectBorder insets:Insets [top=22.0, right=22.0, bottom=22.0, left=22.0]
objectBorder outsets:Insets [top=18.0, right=18.0, bottom=18.0, left=18.0]
Borders are equal.
```

```css
-fx-padding: 10;
-fx-border-color: red, green, black;
-fx-border-style: solid inside, solid outside, dashed centered;
-fx-border-width: 10, 8, 1;
-fx-border-insets: 12, -10, 0;
-fx-border-radius: 0, 0, 0;
```

### 4.2. border image

border-image 的使用没有 border-stroke 那么直接。

border-image 在 `Region` 中有专门的绘制区域。根据四个边的 border-width 将绘制区域分为 9 个部分，image 同样分为 9 个部分。当 border image 区域与 `Region` 相同：

@import "images/Pasted%20image%2020230707102530.png" {width="600px" title=""}

将 border-image-area 和 image 都分成 9 份后，需要指定每个 image slice 的位置和 resizing。通常丢弃 image 中间的 slice。

border image area 和 `Region` 的 `layoutBounds` 可以相同，也可以不同。下面是 border image area 在 `Region` 的 layoutBounds 内和 layoutBounds 外的情形：

@import "images/Pasted%20image%2020230707103849.png" {width="600px" title=""}

!!! note
    如果 `Region` 的形状不是矩形，则不绘制 border-image。    

下面的 CSS 属性定义 `Region` 的 border images:

- -fx-border-image-source
- -fx-border-image-repeat
- -fx-border-image-slice
- -fx-border-image-width
- -fx-border-image-insets

`-fx-border-image-source` 指定 image 的 CSS URL。对多张 images，使用逗号分开。

`-fx-border-image-repeat` 指定 image slice 填充对应区域的方式。可以为 x 轴和 y 轴方向单独指定该属性。支持：

- no-repeat，缩放 image slice 填充对应区域
- repeat，重复 image slice 填充对应区域
- round，使用整数个 image slice 填充对应区域，必要时缩放 image slice
- space，使用整数个 image slice 填充对应区域，不缩放 image slice，而是在 image slice 均匀分布 space

`-fx-border-image-slice` 指定 image 的 top, right, bottom, left 四个边向内的 offsets，以将 image 划分为 9 份。该属性可以指定为绝对数值或 image 对应边的比例。如果指定 "fill"，保留中心 slice，否则丢掉。

`-fx-border-image-width` 指定 border image area 的 top, right, bottom, left 四个边向内的 offsets，以将 image area 划分为 9 份。**注意**，是将 border image area 划分为 9 份，而不是 `Region`。该属性可以指定为绝对数值或相对 border image area 对应边的比例。

`-fx-border-image-insets` 指定 `Region` 的 layoutBounds 和 border image area 四个边的边距。从 `layoutBounds` 向内为正数，向外为负数。

**示例：** 为尺寸为 (100px, 70px) 的 `Pane` 定义 border image。

使用下面的 image 定义 border image:

@import "images/Pasted%20image%2020230707110752.png" {width="250px" title=""}

```css
-fx-border-image-source: url('image_url_goes_here') ;
-fx-border-image-repeat: no-repeat;
-fx-border-image-slice: 9;
-fx-border-image-width: 9;
-fx-border-image-insets: 10;
-fx-border-color: black;
-fx-border-width: 1;
-fx-border-style: dashed inside;
```

slice 和 width 都是 9px，使 image slice 和 area slice 完全匹配；`-fx-border-image-slice` 没有指定 `fill` 值，所以舍弃中心 slice。使用 border stroke 绘制 `Pane` 的 layoutBounds。指定不同 repeat 的效果：

@import "images/Pasted%20image%2020230707111304.png" {width="500px" title=""}

**示例：** 对上例进行修改，为 `-fx-border-image-slice` 添加 `fill` 值。

```css
-fx-border-image-source: url('image_url_goes_here') ;
-fx-border-image-repeat: no-repeat;
-fx-border-image-slice: 9 fill;
-fx-border-image-width: 9;
-fx-border-image-insets: 10;
-fx-border-color: black;
-fx-border-width: 1;
-fx-border-style: dashed inside;
```

@import "images/Pasted%20image%2020230707111615.png" {width="500px" title=""}

`BorderImage` 类表示 border-image。构造函数：

```java
BorderImage(Image image,
            BorderWidths widths,
            Insets insets,
            BorderWidths slices,
            boolean filled,
            BorderRepeat repeatX,
            BorderRepeat repeatY)
```

`BorderRepeat` enum 包含 `STRETCH`, `REPEAT`, `SPACE`, `ROUND`，与 CSS 的 no-repeat, repeat, space 和 round 一一对应。

**示例：** 代码设置 border image

两个 `Pane` 的 border 相同，只是一个用 CSS 设置，一个用代码设置。

```java{.line-numbers}
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class BorderImageTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        var url = getClass().getResource("/picture/border_with_traingles.jpg").toExternalForm();

        Pane p1 = this.getCSSStyledPane(url);
        Pane p2 = this.getObjectStyledPane(url);

        // Place p1 and p2
        p1.setLayoutX(20);
        p1.setLayoutY(20);
        p2.layoutYProperty().bind(p1.layoutYProperty());
        p2.layoutXProperty().bind(p1.layoutXProperty().add(p1.widthProperty()).add(20));

        Pane root = new Pane(p1, p2);
        root.setPrefSize(260, 100);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Strokes and Images as a Border");
        stage.show();
    }


    public Pane getCSSStyledPane(String imageURL) {
        Pane p = new Pane();
        p.setPrefSize(100, 70);
        p.setStyle("-fx-border-image-source: url('" + imageURL + "') ;" +
                "-fx-border-image-repeat: no-repeat;" +
                "-fx-border-image-slice: 9;" +
                "-fx-border-image-width: 9;" +
                "-fx-border-image-insets: 10;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 1;" +
                "-fx-border-style: dashed inside;");

        return p;
    }

    public Pane getObjectStyledPane(String imageURL) {
        Pane p = new Pane();
        p.setPrefSize(100, 70);
        p.setBackground(Background.EMPTY);

        // Create a BorderImage object
        BorderWidths regionWidths = new BorderWidths(9);
        BorderWidths sliceWidth = new BorderWidths(9);
        boolean filled = false;
        BorderRepeat repeatX = BorderRepeat.STRETCH;
        BorderRepeat repeatY = BorderRepeat.STRETCH;
        BorderImage borderImage = new BorderImage(new Image(imageURL),
                regionWidths,
                new Insets(10),
                sliceWidth,
                filled,
                repeatX,
                repeatY);

        // Set the Pane's boundary with a dashed stroke
        List<Double> dashArray = new ArrayList<>();
        dashArray.add(2.0);
        dashArray.add(1.4);
        BorderStrokeStyle blackStrokeStyle =
                new BorderStrokeStyle(StrokeType.INSIDE,
                        StrokeLineJoin.MITER,
                        StrokeLineCap.BUTT,
                        10,
                        0,
                        dashArray);
        BorderStroke borderStroke = new BorderStroke(Color.BLACK,
                blackStrokeStyle,
                CornerRadii.EMPTY,
                new BorderWidths(1),
                new Insets(0));

        // Create a Border object with a stroke and an image
        BorderStroke[] strokes = new BorderStroke[]{borderStroke};
        BorderImage[] images = new BorderImage[]{borderImage};
        Border b = new Border(strokes, images);

        p.setBorder(b);

        return p;
    }
}
```

@import "images/Pasted%20image%2020230707112118.png" {width="250px" title=""}

## 5. margins

不支持直接为 `Region` 设置 margins。大多数 layoutPanes 支持为其子节点设置 margins。

如果要为 `Region` 设置 margins，可以将其添加到一个 layoutPane（如 `HBox）`，然后使用 layoutpane 设置 margins:

```java
Pane p1 = new Pane();
p1.setPrefSize(100, 20);

HBox box = new HBox();

// Set a margin of 10px around all four sides of the Pane
HBox.setMargin(p1, new Insets(10));
box.getChildren().addAll(p1);
```

同样使用 `box` 获取 `p1` 的 margins。

## CSS



## 参考

- https://openjfx.io/javadoc/20/javafx.graphics/javafx/scene/doc-files/cssref.html#region
