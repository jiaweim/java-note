# Text 概述

2023-07-26, 14:57
****
## 1. 简介

`Text` 类用于渲染文本，包含多个属性用于自定义文本样式。

文本相关类都在 `javafx.scene.text` 包中，包括  `Text`, `TextAlignment`, `Font`, `FontWeight` 等。

`Text` 类继承自 `Shape`，所以 `Shape` 相关的属性都可以应用在 `Text` 上。Text 也是 Node 类型，所以 Node 相关特性，如特效和变换，也可以用在 Text>

下面显示了 3 个 Text：

- 第一个是简单的 Text
- 第二个字体加粗、加大
- 第三个应用 Reflection 特效，字体加大，stroke, fill

![|350](Pasted%20image%2020230726145257.png)

不同行通过 `\n` 来区分。

## 2. 创建 Text

`Text` 包含如下几个构造函数：

```java
Text()
Text(String text)
Text(double x, double y, String text)
```

无参构造函数没有文本。其它构造函数指定文本及位置。

Text 类的 text 属性指定 Text 包含的文本。x 和 y 属性指定 text 原点坐标。

```java
// Create an empty Text Node and later set its text
Text t1 = new Text();
t1.setText("Hello from the Text node!");

// Create a Text Node with initial text
Text t2 = new Text("Hello from the Text node!");

// Create a Text Node with initial text and position
Text t3 = new Text(50, 50, "Hello from the Text node!");
```

```ad-tip
Text 的 width 和 height 自动根据 font 计算。Text 默认使用系统默认字体渲染文本。
```

**示例：** 创建 Text

创建 3 个 Text，设置不同属性，添加到 HBox 显示。

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TextTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Text t1 = new Text("Hello Text Node!");

        Text t2 = new Text("Bold and Big");
        t2.setFont(Font.font("Tahoma", FontWeight.BOLD, 16));

        Text t3 = new Text("Reflection");
        t3.setEffect(new Reflection());
        t3.setStroke(Color.BLACK);
        t3.setFill(Color.WHITE);
        t3.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        HBox root = new HBox(t1, t2, t3);
        root.setSpacing(20);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Text Nodes");
        stage.show();
    }
}
```

![|350](Pasted%20image%2020230726145721.png)

## 3. Text Fill, Stroke

`Text` 继承自 `Shape`，所以也有 fill 和 stroke。`Text` 的默认 stroke 为 null，默认 fill 为 `Color.BLACK`。

**示例：** 设置 Text 的 stroke 和 fill
创建 2 个 Text:

- 第一个 red stroke, white fill
- 第二个 black stroke, while fill, stroke style 采用虚线

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TextFillAndStroke extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Text t1 = new Text("Stroke and fill!");
        t1.setStroke(Color.RED);
        t1.setFill(Color.WHITE);
        t1.setFont(new Font(36));

        Text t2 = new Text("Dashed Stroke!");
        t2.setStroke(Color.BLACK);
        t2.setFill(Color.WHITE);
        t2.setFont(new Font(36));
        t2.getStrokeDashArray().addAll(5.0, 5.0);

        HBox root = new HBox(t1, t2);
        root.setSpacing(20);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Stroke and Fill for Text Nodes");
        stage.show();
    }
}
```

![|500](Pasted%20image%2020230726155637.png)

## 4. 文字装饰

`Text` 类包含两个 boolean 属性，用于添加文字装饰：

- strikethrough, 删除线
- underline, 下划线

两个属性默认为 false。

设置方法很简单，如下：

```java
Text t1 = new Text("It uses the \nunderline decoaration.");
t1.setUnderline(true);

Text t2 = new Text("It uses the \nstrikethrough decoration.");
t2.setStrikethrough(true);
```

![](Pasted%20image%2020230726155815.png)

## 5. 字体平滑

`Text` 的 `fontSmoothingType` 属性，用于设置灰度或 LCD 字体平滑。

`FontSmoothingType` enum: `GRAY`, `LCD`。默认为 `GRAY`。

**示例：** 创建 2 个 Text

- 第一个采用 LCD
- 第二个采用 GRAY

```java
Text t1 = new Text("Hello world in LCD.");
t1.setFontSmoothingType(FontSmoothingType.LCD);

Text t2 = new Text("Hello world in GRAY.");
t2.setFontSmoothingType(FontSmoothingType.GRAY);
```

![|250](Pasted%20image%2020230726160522.png)

## 6. Text CSS

`Text` 没有默认的 CSS 样式类名，除了 `Shape` 支持的 CSS 属性，`Text` 添加了如下 CSS 属性：

- `-fx-font`
- `-fx-font-smoothing-type`：lcd 或 gray
- `-fx-text-origin`：baseline, top, bottom
- `-fx-text-alignment`
- `-fx-strikethrought`
- `-fx-underline`

**示例：** 创建 *my-text* 样式

设置 font 和 linear-gradient fill

```css
.my-text {
    -fx-font: 36 Arial;
    -fx-fill: linear-gradient(from 0% 0% to 100% 0%,
                                lightgray 0%, black 100%);
    -fx-font-smoothing-type: lcd;
    -fx-underline: true;
}
```

创建 Text 并应用 my-text:

```java
Text t1 = new Text("Styling Text Nodes!");
t1.getStyleClass().add("my-text");
```

![|200](Pasted%20image%2020230726160925.png)

