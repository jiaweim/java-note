# 自定义样式快速入门

2023-06-19, 16:42
***
## 1. CSS

CSS（Cascading Style Sheet），即层叠样式表，是用于描述 GUI 程序中 UI 元素的外观和样式的语言。CSS 最初为网页而设计，用于实现网页内容和样式的分离。

JavaFX 支持使用 CSS 定义 JavaFX 程序的样式。即通过 JavaFX 类库或 FXML 定义 UI 元素，然后使用 CSS 定义样式。

CSS 通过特定语法指定 UI 元素的属性。基本语法：

- 一个规则（rule）包含选择器（selector）和一系列的键值对
- selector 用于识别 UI 元素，字符串类型
- 键值对包含 UI 元素的属性名称和值，以冒号（`:`）分隔
- 属性之间以分号（`;`）分隔
- 所有的属性都放在大括号（`{}`）中，大括号前为 selector

例如，下面是一条 CSS 规则：

```css
.button {
    -fx-background-color: red;
    -fx-text-fill: white;
}
```

解释：

- `.button` 是选择器，表示该规则用于所有的按钮
- `-fx-background-color: red;` 指定背景色为红色
- `-fx-text-fill: white;` 指定文本颜色为白色

## 2. 样式、皮肤和主题

CSS 规则称为**样式**（style），CSS 规则的集合称为**样式表**（style sheet）。样式（style）、皮肤（skin）和主题（theme），这三个概念很容易混淆。

**样式（style）**，实现了外观和内容的分离，促进 UI 元素的分组，多个 UI 元素可以共享样式。JavaFX 使用 JavaFX CSS 创建样式。  

**皮肤（skin）**，是特定于应用程序的样式集合，定义了应用程序的外观。换皮肤就是更改程序的外观。JavaFX 没有提供特定的换肤功能，不过通过 JavaFX CSS 和 JavaFX API 可以轻松地为 JavaFX 程序定制外观。  

**主题（theme）**，定义操作系统所有 UI 元素的外观。即皮肤是特定于应用程序，而主题特定于操作系统。JavaFX 对主题没有直接的支持。

## 3. 设置样式

演示如何在 JavaFX 中使用 CSS：将所有按钮的背景设置为红色、文本设置为白色。

- 定义样式表文件 `resources/css/buttonstyles.css`

```css
.button {
	-fx-background-color: red;
	-fx-text-fill: white;
}
```

- 添加样式表

`Scene` 和 `Parent` 各包含一个存储 CSS 文件 URLs 字符串的 `ObservableList`，调用 `getStylesheets()` 获得。

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ButtonStyleTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button yesBtn = new Button("Yes");
        Button noBtn = new Button("No");
        Button cancelBtn = new Button("Cancel");

        HBox root = new HBox();
        root.getChildren().addAll(yesBtn, noBtn, cancelBtn);

        Scene scene = new Scene(root);

        String url = ButtonStyleTest.class.getResource("/css/buttonstyles.css")
                                          .toExternalForm();
        scene.getStylesheets().add(url); // 添加样式表

        stage.setScene(scene);
        stage.setTitle("Styling Buttons");
        stage.show();
    }
}
```

![|150](images/Pasted%20image%2020230619164150.png)

## 4. 参考

- [JavaFX CSS 参考指南](https://openjfx.io/javadoc/20/javafx.graphics/javafx/scene/doc-files/cssref.html)
