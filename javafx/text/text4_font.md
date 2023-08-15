# Font

2023-07-26, 15:52
****
## 1. 简介

`Text`的 `font` 属性定义文本字体。默认字体来自 `System` 字体，`Regular` 风格，默认字体大小取决于系统及其桌面设置。

字体包括 _family_ 和 _family name_：

- font family 也称为 _typeface_，定义字符形状（glyph）
- 不同 font family 的相同字符外观不同
- 为 font family 应用不同样式，得到不同变体
- 每个变体名称往往包含 family name 和 style name

例如，"Arial" 是 family name，"Arial Regular", "Arial Bold" 和 "Arial Bold Italic" 是 "Arial" 字体的变体。

## 2. 创建字体

`Font` 类提供了两个构造函数：

- Font(double size)，以 "System" font family 创建指定大小的 `Font` 对象
- Font(String name, double size)，以指定 font full name和大小创建 `Font` 对象，如果没有找到 "full name" 对应的字体，使用默认的系统字体。

字体单位为 points。

**示例：** 创建 "Arial" family 字体

Font 的 getFamily(), getName() 和 getSize() 分别返回 family name, full name 和 size。

```java
// Arial Plain
Font f1 = new Font("Arial", 10);

// Arial Italic
Font f2 = new Font("Arial Italic", 10);

// Arial Bold Italic
Font f3 = new Font("Arial Bold Italic", 10);

// Arial Narrow Bold
Font f4 = new Font("Arial Narrow Bold", 30);
```

如果没找到 full name，使用默认的 "System" font。记住字体的 full name 很困难，为了解决该问题，`Font` 类提供了工厂方法方便字体的创建：

- font(double size)
- font(String family)
- font(String family, double size)
- font(String family, FontPosture posture, double size)
- font(String family, FontWeight weight, double size)
- font(String family, FontWeight weight, FontPosture posture, double size)

`font()` 可以通过 family name, font weight, font posture, font size 创建 `Font`。

`FontWeight` 指定字体的粗细，`FontWeight` enum：`THIN`, `EXTRA_LIGHT`, `LIGHT`, `NORMAL`, `MEDIUM`, `SEMI_BOLD`, `BOLD`, `EXTRA_BOLD`, `BLACK`，从细到粗。

`FontPosture` 指定是否为斜体，FontPosture enum：`REGULAR` 和 `ITALIC`。

使用 `Font` 的 `getDefault()` 静态方法可获得系统的默认字体。

**示例：** 使用 factory 方法创建 `Font`

```java
// Arial Regular
Font f1 = Font.font("Arial", 10);

// Arial Bold
Font f2 = Font.font("Arial", FontWeight.BOLD, 10);

// Arial Bold Italic
Font f3 = Font.font("Arial", FontWeight.BOLD, FontPosture.ITALIC, 10);

// Arial THIN
Font f4 = Font.font("Arial", FontWeight.THIN, 30);
```

**示例：** 创建 Text，设置 font 属性

- 第一个 Text 使用默认 font

```java{.line-numbers}
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TextFontTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Text t1 = new Text();
        t1.setText(t1.getFont().toString());

        Text t2 = new Text();
        t2.setFont(Font.font("Arial", 12));
        t2.setText(t2.getFont().toString());

        Text t3 = new Text();
        t3.setFont(Font.font("Arial", FontWeight.BLACK, 12));
        t3.setText(t2.getFont().toString());

        Text t4 = new Text();
        t4.setFont(Font.font("Arial", FontWeight.THIN, FontPosture.ITALIC, 12));
        t4.setText(t2.getFont().toString());

        VBox root = new VBox(t1, t2, t3, t4);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Setting Fonts for Text Nodes");
        stage.show();
    }
}
```

@import "images/Pasted%20image%2020230726153835.png" {width="450px" title=""}

## 3. 已安装字体

可以访问机器上已安装的字体。下面是 `Font` 类中访问已安装字体的静态方法：

- `List<String> getFamilies()`
- `List<String> getFontNames()`
- `List<String> getFontNames(String family)`

**示例：** 输出已安装字体的 family name

```java
// Print the family names of all installed fonts
for(String familyName: Font.getFamilies()) {
    System.out.println(familyName);
}
```

**示例：** 输出已安装字体的 full name

```java
// Print the full names of all installed fonts
for(String fullName: Font.getFontNames()) {
    System.out.println(fullName);
}
```

**示例：** 输出指定 family name 字体的 full name

```java
// Print the full names of “Times New Roman” family
for(String fullName: Font.getFontNames("Times New Roman")) {
    System.out.println(fullName);
}
```

## 4. 自定义字体

可以从外部加载自定义字体，如从文件或 URL 加载。`Font` 的 loadFont() 加载自定义字体：

- loadFont(InputStream in, double size)
- loadFont(String urlStr, double size)

使用这两个方法，可以载入本地或网络上的字体。

加载自定义字体成功后，loadFont() 使用 JavaFX graphics engine 注册该字体。这样 Font 类就可以使用构造函数或 factory 方法创建 Font。

loadFont() 同时返回指定 size 的 Font 对象，即 `size` 参数的目的是为了加载字体后创建指定 size 的字体。

加载失败返回 null。

**示例：** 从本地文件系统加载字体

- 字体文件名为 4starfac.ttf，放在 `resources/font` 目录。
- 加载成功后，设置第一个 Text 的字体
- 使用加载字体的 family name 创建一个新的 Font，设置第二个 Text 的字体
- 如果字体文件不存在或加载失败，抛出错误信息

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TextCustomFont extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Text t1 = new Text();
        t1.setLineSpacing(10);

        Text t2 = new Text("Another Text node");

        // Load the custom font
        // TODO: book text
        var url = getClass().getResource("/font/4starfac.ttf");
        if (url != null) {
            String urlStr = url.toExternalForm();
            Font customFont = Font.loadFont(urlStr, 16);
            if (customFont != null) {
                // Set the custom font  for the first Text node
                t1.setFont(customFont);

                // Set the text and line spacing
                t1.setText("Hello from the custom font!!! \nFont Family: " +
                        customFont.getFamily());

                // Create an object of the custom font and use it
                Font font2 = Font.font(customFont.getFamily(), FontWeight.BOLD,
                        FontPosture.ITALIC, 24);

                // Set the custom font for the second Text node
                t2.setFont(font2);
            } else {
                t1.setText("Could not load the custom font from " + urlStr);
            }
        } else {
            t1.setText("Could not find the custom font file " +
                    "\"font/4starfac.ttf\"" + ". Used the default font.");
        }

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
        stage.setTitle("Loading and Using Custom Font");
        stage.show();
    }
}
```

![](Pasted%20image%2020230726155014.png)
