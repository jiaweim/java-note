# Tooltip

2023-07-25, 11:03
****
## 1. 简介

tool-tip 是一个弹窗组件，用于显示 node 附加信息：

- 鼠标悬停在 node 上时显示 tool-tip
- 从鼠标悬停到 tool-tip 显示有一个延迟
- tool-tip 显示一段时间后自动隐藏
- 鼠标离开 node tool-tip 也会隐藏

下图黑框中为 tool-tip 弹窗：

@import "images/2019-12-03-20-43-51.png" {width="400px" title=""}

tool-tip 由 `Tooltip` 类表示。`Tooltip` 继承自 `PopupControl`，可以包含 text 和 graphic。内部由 `Label` 实现。

## 2. 创建 Tooltip

```java{.line-numbers}
// 创建空 Tooltip，没有 text 和 graphic
Tooltip tooltip1 = new Tooltip();

// 创建带 text 的 Tooltip
Tooltip tooltip2 = new Tooltip("Closes the window");
```

## 3. 为 node 设置 Tooltip

- 使用 `Tooltip` 的静态方法 `install()` 为 node 添加 `Tooltip`
- 使用 `Tooltip.uninstall()`  删除 tool-tip

```java{.line-numbers}
Button saveBtn = new Button("Save");
Tooltip tooltip = new Tooltip("Saves the data");

// 添加 tooltip
Tooltip.install(saveBtn, tooltip);

...
// 删除 tooltip
Tooltip.uninstall(saveBtn, tooltip);
```

- 由于 `Tooltip` 使用十分频繁，所以 JavaFX 提供了更简单的设置方法

`Control` 类包含一个 `tooltip` 属性，为 `Tooltip` 类型的 `ObjectProperty`。可以使用 `Control.setTooltip()` 设置 tool-tip。对非 `Control` 类型，如 `Circle`，则只能使用 `install()` 方法。

```java{.line-numbers}
Button saveBtn = new Button("Save");

// Install a tooltip
saveBtn.setTooltip(new Tooltip("Saves the data"));
...
// Uninstall the tooltip
saveBtn.setTooltip(null);
```

!!! info
    tool-tip 可以在多个 nodes 之间共享。tool-tip 使用 `Label` 显示其 `text` 和 `graphic`。

## 4. Tooltip 属性

| 属性           | 类型                  | 说明                                   |
| -------------- | --------------------- | -------------------------------------- |
| text           | `String`              | 要显示的文本                           |
| graphic        | `Node`                | 要显示的 icon                          |
| contentDisplay | enum `ContentDisplay` | graphic 相对 text 的位置，默认 `LEFT`  |
| textAlignment  | enum `TextAlignment`  | 文本对齐方式                           |
| textOverrun    | enum `OverrunStyle`   | 当空间不足以显示文本时采取的策略       |
| wrapText       | boolean               | 当文本过长时，是否换行，default=false  |
| graphicTextGap | double                | text 和 graphics 之间的距离，default=4 |
| font           | Font                  | 文本字体                               |
| activated      | boolean               | 鼠标悬停时为true，显示 tool tip        |
|hideDelay|||
|showDelay|||
|showDuration|||

`textAlignment` 为 `TextAlignment` enum 类型，指定多行文本时的文本对齐方式。TextAlignment enum: LEFT, RIGHT, CENTER, JUSTIFY.

`textOverrun` 为 OverrunStyle enum 类型，指定没有足够空间显示所有文本时的行为，默认使用省略号。

`wrapText` 为 `boolean` 类型，指定文本超过 tool-tip 宽度时，文本是否换行，默认 false。

`graphicTextGap` 为 double 类型指定 text 和 graphic 之间的 gap，默认 4px。

font 为 Font 类型，指定 text 的默认字体。

`activated` 为 read-only 的 boolean 属性，tool-tip 激活时为 true。
 
**示例：** tool-tip

使用 `X` icon 作为 tool-tip 的 graphic，放在 text 上面

```java{.line-numbers}
// Create and configure the Tooltip
Tooltip closeBtnTip = new Tooltip("Closes the window");
closeBtnTip.setStyle("-fx-background-color: yellow; -fx-text-fill: black;");

// Display the icon above the text
closeBtnTip.setContentDisplay(ContentDisplay.TOP);

Label closeTipIcon = new Label("X");
closeTipIcon.setStyle("-fx-text-fill: red;");
closeBtnTip.setGraphic(closeTipIcon);

// Create a Button and set its Tooltip
Button closeBtn = new Button("Close");
closeBtn.setTooltip(closeBtnTip);
```

@import "images/Pasted%20image%2020230725102147.png" {width="200px" title=""}

**示例：** 创建、配置和设置 tool-tips

鼠标悬停在 name-field, save-button 和 close-button，会显示 tool-tips。

```java{.line-numbers}
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TooltipTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label nameLbl = new Label("Name:");
        TextField nameFld = new TextField();
        Button saveBtn = new Button("Save");
        Button closeBtn = new Button("Close");

        // Set an ActionEvent handler
        closeBtn.setOnAction(e -> stage.close());

        // Add tooltips for Name field and Save button
        nameFld.setTooltip(new Tooltip("Enter your name\n(Max. 10 chars)"));
        saveBtn.setTooltip(new Tooltip("Saves the data"));

        // Create and configure the Tooltip for Close button
        Tooltip closeBtnTip = new Tooltip("Closes the window");
        closeBtnTip.setStyle("-fx-background-color: yellow; " +
                " -fx-text-fill: black;");

        // Display the icon above the text
        closeBtnTip.setContentDisplay(ContentDisplay.TOP);

        Label closeTipIcon = new Label("X");
        closeTipIcon.setStyle("-fx-text-fill: red;");
        closeBtnTip.setGraphic(closeTipIcon);

        // Set its Tooltip for Close button
        closeBtn.setTooltip(closeBtnTip);

        HBox root = new HBox(nameLbl, nameFld, saveBtn, closeBtn);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Tooltip Controls");
        stage.show();
    }
}
```

@import "images/Pasted%20image%2020230725110136.png" {width="350px" title=""}

## 5. CSS

`Tooltip` 的默认 CSS 样式类名为 tooltip。

`Tooltip` 添加了如下 CSS 属性：

- -fx-text-alignment
- -fx-text-overrun
- -fx-wrap-text
- -fx-graphic
- -fx-content-display
- -fx-graphic-text-gap
- -fx-font

示例：

```css
.tooltip {
    -fx-background-color: yellow;
    -fx-text-fill: black;
    -fx-wrap-text: true;
}
```
