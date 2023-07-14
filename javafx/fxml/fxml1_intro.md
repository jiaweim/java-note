# FXML 概述

2023-07-12, 12:56
****
## 1. 简介

FXML 是基于 FXML 的标记语言，用于构建 JavaFX 界面。可以使用 FXML 构建界面，从而将 UI 构建从软件逻辑中分离出来。如果要修改 UI，都不需要重新编译 JavaFX 代码，只需要修改 FXML 文件中的对应内容。

JavaFX scene graph 是 Java 对象的树形结构，和 XML 格式完美契合。除了构建 scene graph，FXML 还可以创建 Java 对象。

现在构建一个`VBox`，包含 `Label` 和 `Button`，使用代码构建的方式如下：

```java
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

VBox root = new VBox();
root.getChildren().addAll(new Label("FXML is cool"), new Button("Say Hello"));
```

使用 FXML 构建：

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.layout.VBox?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.control.Button?>
<VBox>
    <children>
        <Label text="FXML is cool"/>
        <Button text="Say Hello"/>
    </children>
</VBox>
```

第一行是标准 XML 声明，由 XML 解析器使用，为可选项。没有指定时，默认为 1 和 UTF-8.

下面 3 行与 Java 中的 import 语句功能一样。UI 元素名称，如 VBox, Label 和 Button 和 JavaFX 类名一样。`<children>` tag 指定 VBox 的 children。

## 2. Scene Builder

FXML 文件后缀为 `.fxml`，是纯文本文件，所以可以直接编辑。但是，当 XML 变大，直接用文本编辑器编辑很麻烦。

Gluon 公司提供了一个可视化编辑器 SceneBuilder，基于FXML构建 JavaFX 界面，下载链接：

[https://gluonhq.com/products/scene-builder/](https://gluonhq.com/products/scene-builder/)

