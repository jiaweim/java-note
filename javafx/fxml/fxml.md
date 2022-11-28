# FXML

- [FXML](#fxml)
  - [简介](#简介)
  - [Scene Builder](#scene-builder)
  - [FXML 基础](#fxml-基础)
    - [创建 FXML 文件](#创建-fxml-文件)
  - [FXMLLoader 载入 FXML 文件](#fxmlloader-载入-fxml-文件)
  - [Controller](#controller)
    - [@FXML](#fxml-1)
    - [initialize()](#initialize)
  - [在FXML中创建对象](#在fxml中创建对象)
    - [使用工厂方法](#使用工厂方法)
  - [多个FXML文件](#多个fxml文件)
  - [自定义组件](#自定义组件)
  - [创建可重复利用控件](#创建可重复利用控件)
  - [参考](#参考)

2020-05-20, 15:12
***

## 简介

FXML 是用于构建 JavaFX 界面的基于XML的标记语言。可以完全使用 FXML 构建界面，从而将UI构建从软件逻辑中分离出来。如果要修改软件的UI，设置不需要重新编译 JavaFX 代码，只需要修改 FXML 文件中的对应内容即可。

因为 JavaFX scene graph 是 Java 对象的分层结构，和XML格式完全吻合，因此使用XML存储 JavaFX 界面正好合适。

现在我们构建一个`VBox`，包含 `Label` 和 `Button`，使用代码构建的方式如下：

```java
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
VBox root = new VBox();
root.getChildren().addAll(new Label("FXML is cool"), new Button("Say Hello"));
```

使用FXML构建：

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

FXML 文件是后缀为 `.fxml` 的文本文件，所以可以直接编辑，更合适的方式是使用 Scene Builder 编辑。

## Scene Builder

Scene Builder 是基于FXML构建 JavaFX 界面的工具，下载链接：

[https://gluonhq.com//products/scene-builder/](https://gluonhq.com//products/scene-builder/)

## FXML 基础

下面使用FXML创建包含一个 `Text` 和一个 `Button` 的 `VBox` 简单界面。

### 创建 FXML 文件

创建一个 `sayhello.fxml` 文件，向其中添加界面元素：

## FXMLLoader 载入 FXML 文件

使用 `FXMLLoader` 类载入 FXML 源文件，返回 graph 对象。

例如，从相对于加载类的位置加载 FXML 文件，并使用 "com.foo.example" 资源对其本地化。根元素的类型为 `javafx.scene.layout.Pane` 的子类，并假设定义的控制器为 `MyController`:

```java
URL location = getClass().getResource("example.fxml");
ResourceBundle resources = ResourceBundle.getBundle("com.foo.example");
FXMLLoader fxmlLoader = new FXMLLoader(location, resources);

Pane root = (Pane)fxmlLoader.load();
MyController controller = (MyController)fxmlLoader.getController();
```

`FXMLLoader` 内部使用 `javax.xml.stream` API 读取FXML文件。
NOTE: `FXMLLoader` 不能识别大小写，所以，fxml 命名最好都采用小写。

例一：最简单的法子

```java
String fxmlDocUrl = "file:///C:/resources/fxml/test.fxml";
URL fxmlUrl = new URL(fxmlDocUrl);
VBox root = FXMLLoader.<VBox>load(fxmlUrl);
```

其他方法：

```java
<T> T load()
<T> T load(InputStream inputStream)
static <T> T load(URL location)
static <T> T load(URL location, ResourceBundle resources)
static <T> T load(URL location, ResourceBundle resources, BuilderFactory builderFactory)
static <T> T load(URL location, ResourceBundle resources, BuilderFactory builderFactory,Callback<Class<?>,Object> controllerFactory)
static <T> T load(URL location, ResourceBundle resources, BuilderFactory builderFactory,Callback<Class<?>,Object>     controllerFactory, Charset charset)
```

## Controller

控制器（controller）主要用于定义UI元素的事件监听器。FXML 文件中使用 `fx:controller` 属性用于指定控制器类，该属性声明在FXML文件的顶层元素中，一个FXML文件只允许指定一个控制器。例：

```xml
<VBox fx:controller="com.jdojo.fxml.SayHelloController"
    xmlns:fx="http://javafx.com/fxml">
</VBox>
```

`fx:id` 属性指定元素的名称，该名称和 controller 类中字段的名称一一对应。

controller 具有如下特征：

- 由 FXML loader 实例化
- 必须有无参的 public 构造函数
- 可以有可访问方法，用作FXML的 event handler
- FXML loader 会自动查找实例变量，如果变量名和 `fx:id` 一致，则会自动将FXML中的对象引用复制到控制器的实例变量
- 控制器可以有一个 `initialize()` 方法，在载入FXML稳定后自动运行。

### @FXML

该注释用于字段和方法，不能用于类和构造函数。

含义：FXML loader 可以访问该成员，即使是 private 成员。

### initialize()

controller 包含一个可访问的 `initialize()` 方法，在 fxml 文件载入后，该方法被调用。可用于执行GUI element 个性化设置等内容，载入资源文件等。

执行顺序：

1. 构造函数
2. `@FXML` 字段
3. `initialize()` 方法

## 在FXML中创建对象

### 使用工厂方法

如果一个类包含创建对象的无参静态方法，就可以在FXML中用其创建对象。例如，使用 `LocalDate` 的 `now()` 工厂方法创建 `LocalDate`：

```xml
<?import java.time.LocalDate?>
<LocalDate fx:factory="now"/>
```

`FXCollections` 类包含许多创建集合的静态方法，可用于在 FXML 中创建 JavaFX 集合。例如创建一个 `ObservableList<String>`，并添加四个水果名：

```xml
<?import java.lang.String?>
<?import javafx.collections.FXCollections?>
<FXCollections fx:factory="observableArrayList">
    <String fx:value="Apple"/>
    <String fx:value="Banana"/>
    <String fx:value="Grape"/>
    <String fx:value="Orange"/>
</FXCollections>
```

下面，我们使用 `fx:factory` 属性创建一个 `ObservableList`，并用其初始化 `ComboBox` 的 `items` 属性，随后设置 `ComboBox` 的初始值：

```xml
<?import javafx.scene.layout.VBox?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.control.Button?>
<?import javafx.scene.control.ComboBox?>
<?import java.lang.String?>
<?import javafx.collections.FXCollections?>

<VBox xmlns:fx="http://javafx.com/fxml">
    <Label text="List of Fruits"/>
    <ComboBox>
        <items>
            <FXCollections fx:factory="observableArrayList">
                <String fx:value="Apple"/>
                <String fx:value="Banana"/>
                <String fx:value="Grape"/>
                <String fx:value="Orange"/>
            </FXCollections>
        </items>
        <value>
            <String fx:value="Orange"/>
        </value>
    </ComboBox>
</VBox>
```

下面使用 ControlsFX 的 `TextFields`的 `createClearableTextField` 方法创建一个可清除的 `TextField`:

```xml
<TextFields fx:factory="createClearableTextField"/>
```

## 多个FXML文件

因为载入 FXML 文件返回的是 JavaFX `Node` 类型，一个 `Scene` 包含多个 `Node`，所以也可以包含多个 FXML 文件。

除了使用 `FXMLLoader.load()` 方法载入 FXML 文件，也可以

## 自定义组件

使用 `FXMLLoader` 的 `setRoot()` 和 `setController()` 方法设置根节点和控制器。这两个方法在创建基于FXML的自定义组件时使用较多，方便创建可重复使用的组件。

如下，创建一个简单的自定义的 TextField 和 Button，根节点为 `javafx.scene.layout.VBox`:

```xml
<?import javafx.scene.*?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<fx:root type="javafx.scene.layout.VBox" xmlns:fx="http://javafx.com/fxml">
    <TextField fx:id="textField"/>
    <Button text="Click Me" onAction="#doSomething"/>
</fx:root>
```

其中，`<fx:root>`引用预定义的 root element。该element 可以通过 `FXMLLoader` 的 `getRoot()` 获得，在调用 `load()` 方法之前，必须通过 `setRoot()` 方法指定该值。

下面定义的 `CustomControl` 类扩展 `VBox`将其自身设置为载入的FXML文档的 root 和 controller。

```java
package fxml;
import java.io.IOException;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
public class CustomControl extends VBox {

    @FXML private TextField textField;

    public CustomControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("custom_control.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    public String getText() {
        return textProperty().get();
    }
    public void setText(String value) {
        textProperty().set(value);
    }
    public StringProperty textProperty() {
        return textField.textProperty();
    }
    @FXML
    protected void doSomething() {
        System.out.println("The button was clicked!");
    }
}
```

注意构造函数中调用的 `fxmlLoader.setRoot(this)` 和 `fxmlLoader.setController(this)`，这在其中是必须的。

## 创建可重复利用控件

使用 `fx:define` 定义对应的控件。定义 `fx:id` 后，通过 `$` 引用。

```xml
<VBox fx:controller="com.jdojo.fxml.Test" xmlns:fx="http://javafx.com/fxml">
    <fx:define>
        <Insets fx:id="margin" top="5.0" right="5.0" bottom="5.0" left="5.0"/>
        <ToggleGroup fx:id="genderGroup"/>
    </fx:define>
    <Label text="Gender" VBox.margin="$margin"/>
    <RadioButton text="Male" toggleGroup="$genderGroup"/>
    <RadioButton text="Female" toggleGroup="$genderGroup"/>
    <RadioButton text="Unknown" toggleGroup="$genderGroup" selected="true"/>
    <Button text="Close" VBox.margin="$margin"/>
</VBox>
```

## 参考

- https://openjfx.io/javadoc/17/javafx.fxml/javafx/fxml/doc-files/introduction_to_fxml.html
