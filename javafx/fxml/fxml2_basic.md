# FXML 基础

2023-07-12, 15:46
****
## 1. 简介

下面通过构建一个简单的 JavaFX 应用来介绍 FXML 基础。

UI 包含一个 VBox，一个 Label，一个 Button。VBox 的 spacing=10px。Label 的 text 为 "FXML is cool!"，Button 的 text 为 "Say Hello"。点击 Button，Label 的 text 变为 "Hello from FXML!"。

代码实现：

```java
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelloJavaFX extends Application {

    private final Label msgLbl = new Label("FXML is cool!");
    private final Button sayHelloBtn = new Button("Say Hello");

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Set the preferred width of the lable
        msgLbl.setPrefWidth(150);

        // Set the ActionEvent handler for the button
        sayHelloBtn.setOnAction(this::sayHello);

        VBox root = new VBox(10);
        root.getChildren().addAll(msgLbl, sayHelloBtn);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Hello FXML");
        stage.show();
    }

    public void sayHello(ActionEvent e) {
        msgLbl.setText("Hello from FXML!");
    }
}
```

初始界面：

![|200](Pasted%20image%2020230712130606.png)

点击 Button 后的界面：

![|200](Pasted%20image%2020230712130621.png)

## 2. 创建 FXML 文件

创建 `sayhello.fxml` 文件，将文件保存到 `resources/fxml` 目录。

## 3. 添加 UI 元素

FXML 文件的 root 元素是 scene graph 的 root 容器，这里为 VBox，所以 FXML 的 root 元素为：

```xml
<VBox>
</VBox>
```

要让 FXML 解析器能识别 `<VBox>`，要么在文档开头添加导入语句：

```xml
<?import javafx.scene.layout.VBox?>
```

要么使用 VBox 的完全限定名称：

```xml
<javafx.scene.layout.VBox>
</javafx.scene.layout.VBox>
```

为 VBox 添加 children:

```xml
<VBox>
    <Label></Label>
    <Button></Button>
</VBox>
```

这样就定义好了 scene graph 的基本框架。

VBox 的 children 从技术上归属于 `children` 树形，不直接属于 VBox。所以上面的 FXML 还可以写成：

```xml
<VBox>
    <children>
        <Label></Label>
        <Button></Button>
    <children>
</VBox>
```

那么，我们如何直到是否可以忽略 `<children>` tag？`javafx.beans` 包中有一个 `DefaultProperty` annotation 用于注释 class。该 annotation 有一个 String 类型的值，指定被注释类在 FXML 中的默认属性。

如果 FXML 中某个 child 元素不属于其 parent 元素的属性，那么必然属于 parent 默认属性的 child 元素。VBox 继承 Pane，Pane 的声明如下：

```java
@DefaultProperty(value="children")
public class Pane extends Region {...}
```

该注释使 `children` 属性成为  `Pane` 在 FXML 中的默认属性。VBox 从 Pane 继承了该注释。所以在上面的 FXML 中可以忽略 `<children>` tag，

## 4. 在 FXML 中导入 Java 类型

为了在 FXML 中使用 Java 类的简单名称，必须和 Java 一样导入类。另外，在 Java 中可以不导入 java.lang 包，在 FXML 中任何类都需要导入。

下面导入 VBox, Label 和 Button 类：

```xml
<?import javafx.scene.layout.VBox?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.control.Button?>
```

下面导入 `javafx.scene.control` 和 java.lang 中的所有类：

```xml
<?import javafx.scene.control.*?>
<?import java.lang.*?>
```

FXML 不支持静态导入，末尾不需要分号。

## 5. 在 FXML 中设置属性

属性声明符合 JavaBean 约定的对象，可以在 FXML 中设置属性值。设置方式有两种：

- 使用 FXML 元素的 attributes
- 使用 property 元素

property 元素的 attribute 名称和属性名称一样。

使用 attribute 设置 Label 的 `text` property：

```xml
<Label text="FXML is cool!"/>
```

使用 property 元素设置 Label 的 text property:

```xml
<Label>
    <text>FXML is cool!</text>
</Label>
```

创建 Rectangle，使用 attributes 设置 x, y, width, height 和 fill 属性：

```xml
<Rectangle x="10" y="10" width="100" height="40" fill="red"/>
```

FXML 中所有 attributes 都是 String 类型。JavaFX 在解析 FXML 时会自动将 String 转换为所需类型。如，上面的 fill 属性值 "red" 被转换为 Color 对象，width 属性值 "100" 被转换为 double 值。

使用 property 元素设置对象属性更灵活。可以从 String 自动转换为所需类型时使用 attributes。假设你要将 Person 类设置为对象的属性值，可以使用 property 元素实现：

```xml
<MyCls>
    <person>
        <Person>
        <!-- Configure the Person object here -->
        </Person>
    </person>
</MyCls>
```

read-only property 只有 getter。在 FXML 中有两种特殊的 read-only properties 可以使用 property 元素设置：

- read-only List property
- read-only Map property

例如，设置 VBox 的 read-only children property:

```xml
<VBox>
    <children>
        <Label/>
        <Button/>
    <children>
</VBox>
```

下面定义了一个 Item 类，包含一个 read-only map property:

```java
public class Item {
    private Map<String, Integer> map = new HashMap<>();
    public Map getMap() {
        return map;
    }
}
```

下面的 FXML 创建一个 Item 对象，其 map property 包含 2 个 attributes (“n1”, 100) 和 (“n2”, 200)。其中 n1 和 n2 为 Map 中的 keys:

```xml
<Item>
    <map n1="100" n2="200"/>
</Item>
```

### 5.1. static property

static property 不在对象的类上声明，而是用另一个类的静态方法设置。如，在 VBox 中设置 Button 的 margin，JavaFX 代码如下：

```java
Button btn = new Button("OK");
Insets insets = new Insets(20.0);;
VBox.setMargin(btn, insets);
VBox vbox = new VBox(btn);
```

在 FXML 中通过设置 Button 的 `VBox.margin` property 实现相同效果：

```xml
<?import javafx.scene.layout.VBox?>
<?import javafx.scene.control.Button?>
<?import javafx.geometry.Insets?>

<VBox>
    <Button text="OK">
        <VBox.margin>
            <Insets top="20.0" right="20.0" bottom="20.0" left="20.0"/>
        </VBox.margin>
    </Button>
</VBox>
```

因为不能直接从 String 创建 Insets，所以不能使用 attribute 设置 margin property。

在 FXML 中设置 GridPane 的 rowIndex 和 columnIndex 的方法：

```xml
<?import javafx.scene.layout.GridPane?>
<?import javafx.scene.control.Button?>

<GridPane>
    <Button text="OK">
        <GridPane.rowIndex>0</GridPane.rowIndex>
        <GridPane.columnIndex>0</GridPane.columnIndex>
    </Button>
</GridPane>
```

因为 rowIndex 和 columnIndex properties 可以用 String 表示，所以可以用 attributes 设置：

```xml
<GridPane>
    <Button text="OK" GridPane.rowIndex="0" GridPane.columnIndex="0"/>
</GridPane>
```

## 6. FXML 命名空间

FXML 没有 XML schema，而是使用前缀 "fx" 指定命名空间，如：

```xml
<VBox xmlns:fx="http://javafx.com/fxml">...</VBox>
```

还可以在命名空间 URI 中追加 FXML 版本。FXML 解析器会验证后面的 XML 内容是否合规。目前只支持 1.0：

```xml
<VBox xmlns:fx="http://javafx.com/fxml/1.0">...</VBox>
```

FXML 版本可以包含点号、下划线和短线。FXML 解析器只看第一个下划线或短线前面的部分。所以下面的 FXML 版本都是 1.0：

```xml
<VBox xmlns:fx="http://javafx.com/fxml/1">...</VBox>
<VBox xmlns:fx="http://javafx.com/fxml/1.0-ea">...</VBox>
<VBox xmlns:fx="http://javafx.com/fxml/1.0-rc1-2014_03_02">...</VBox>
```

## 7. 识别符

使用 `fx:id` attribute 为 JavaFX 对象分配识别符，`Node` 的 `id` property 与其对应。

示例：为 Label 设置 `fx:id`

```xml
<Label fx:id="msgLbl" text="FXML is cool!"/>
```

## 18. 添加 Event Handler

在 FXML 中可以添加 eventHandler。设置 eventHandler 的方式与设置 properties 类似。JavaFX 类定义了 `onXxx` 属性用于为 Xxx 事件设置 event handler。例如，Button 包含一个 onAction property 用于设置 ActionEvent handler。在 FXML 中可以设置两类 event handlers:

- Script event handlers
- Controller event handlers

Controller event handlers 能够更好的分离 UI 和事件逻辑，为首选。

## 9. 加载 FXML 文件

FXML 文件定义 JavaFX 应用的 view。需要加载 FXML 文件，获取 scene graph。使用 `FXMLLoader` 类加载 FXML 文件。

FXMLLoader 类提供了多个构造函数，可以指定 FXML 位置、字符集、资源包等内容。加载 FXML 文件：

```java
String fxmlDocUrl = "file:///C:/resources/fxml/test.fxml";
URL fxmlUrl = new URL(fxmlDocUrl);
FXMLLoader loader = new FXMLLoader();
loader.setLocation(fxmlUrl);
VBox root = loader.<VBox>load();
```

`load()` 方法返回类型为泛型。上面指定泛型参数为 VBox 使 load() 返回 VBox。忽略泛型参数也没问题：

```java
// Will work
VBox root = loader.load();
```

FXMLLoader 也可以从 InputStream 加载 FXML 文件：

```java
FXMLLoader loader = new FXMLLoader();
String fxmlDocPath = "C:\\resources\\fxml\\test.fxml";
FileInputStream fxmlStream = new FileInputStream(fxmlDocPath);
VBox root = loader.<VBox>load(fxmlStream);
```

FXMLLoader 包含多个 load() 版本。有些为实例方法，有些为静态方法。如，用静态方法加载 FXML 文件：

```java
String fxmlDocUrl = "file:///C:/resources/fxml/test.fxml";
URL fxmlUrl = new URL(fxmlDocUrl);
VBox root = FXMLLoader.<VBox>load(fxmlUrl);
```

`FXMLLoader` 内部使用 `javax.xml.stream` API 读取FXML文件。

```ad-note
`FXMLLoader` 不区分大小写，所以 fxml 文件命名最好都采用小写
```

加载 FXML 文件后，FXML 的任务就完成了。

**示例：** 

FXML 文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.Button?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.layout.VBox?>
<VBox spacing="10" xmlns:fx="http://javafx.com/fxml">
    <Label fx:id="msgLbl" text="FXML is cool!" prefWidth="150"/>
    <Button fx:id="sayHelloBtn" text="Say Hello"/>
    <style>
        -fx-padding: 10;
        -fx-border-style: solid inside;
        -fx-border-width: 2;
        -fx-border-insets: 5;
        -fx-border-radius: 5;
        -fx-border-color: blue;
    </style>
</VBox>
```


```java
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SayHelloFXML extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        URL fxmlUrl = getClass().getResource("/fxml/sayhello.fxml");

        // Load the FXML document
        VBox root = FXMLLoader.<VBox>load(fxmlUrl);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Hello FXML");
        stage.show();
    }
}

```

![|200](Pasted%20image%2020230712144710.png)

## 10. Controller

controller 主要用于定义 UI 元素的事件监听器。FXML 文件中使用 `fx:controller` attribute 指定控制器类，只能在 FXML 的 root 元素中声明该 attribute，一个FXML文件只允许指定一个 controller。例：

```xml
<VBox fx:controller="com.jdojo.fxml.SayHelloController"
    xmlns:fx="http://javafx.com/fxml">
</VBox>
```

`fx:id` 属性指定元素的名称，该名称和 controller 类中字段的名称一一对应。

controller 具有如下特征：

- controller 类由 FXML loader 实例化
- 必须有无参 public 构造函数
- controller 的 accessible 方法可以在  FXML 文件中用作 event handler
- FXML loader 会自动查找 controller 包含的实例变量，如果变量名和 `fx:id` 一致，则会自动将FXML 中的对象引用复制给 controller 的实例变量，便于在 controller 中使用 UI 元素
- controller 可以定义一个 `initialize()` 方法，在载入 FXML 文件后自动运行该方法

**示例：** controller

```java
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class SayHelloController {

    // The refernce of msgLbl will be injected by the FXML loader
    @FXML
    private Label msgLbl;

    // location and resources will be automatically injected by the FXML laoder
    @FXML
    private URL location;

    @FXML
    private ResourceBundle resources;

    // Add a public no-args construtcor explicitly just to 
    // emphasize that it is needed for a controller
    public SayHelloController() {}

    @FXML
    private void initialize() {
        System.out.println("Initializing SayHelloController...");
        System.out.println("Location = " + location);
        System.out.println("Resources = " + resources);
    }

    @FXML
    private void sayHello() {
        msgLbl.setText("Hello from FXML!");
    }
}
```

### 10.1. @FXML

上面 controller 的部分成员包含 @FXML 注释。@FXML 注释可用于字段和方法，不能用于类和构造函数。

含义：FXML loader 可以访问该成员，即使是 private 成员。

public 成员无需 @FXML 注释，FXMLLoader 也可以访问。为 public 成员添加 @FXML 注释也不算错。建议将 FXMLLoader 使用的所有成员，不管使 private 还是 public，都用 @FXML 标记。

下面的 FXML 将 controller 的 sayHello() 方法设置为 Button 的 eventHandler：

```xml
<VBox fx:controller="com.jdojo.fxml.SayHelloController"
xmlns:fx="http://javafx.com/fxml">
    <Button fx:id="sayHelloBtn" text="Say Hello" onAction="#sayHello"/>
...
</VBox>
```

在 controller 中可以声明两个特殊的实例变量，它们会由 FXMLLoader 自动注入：

```java
@FXML
private URL location;
@FXML
private ResourceBundle resources;
```

`location` 是 FXML 文件的位置。`resources` 是 FXML 中可能使用的 `ResourceBundle` 的引用。

### 10.2. eventHandler

当 FXML 中的 eventHandler attribute 以 `#` 开头，表示该 eventHandler 在 controller 中定义。controller 中定义的 eventHandler 方法应符合以下规则：

- 没有参数或一个参数。如果有一个参数，则参数类型要与处理的事件兼容
- 如果同时定义了无参和一个参数两个版本，则 FXMLLoader 使用一个参数的版本
- eventHandler 方法通常返回 void
- eventHandler 方法对 FXMLLoader 必须 accessible，要么是 public，要么以 @FXML 注释

### 10.3. initialize

controller 包含一个 `initialize()` 方法，FXMLLoader 载入 fxml 文件后，会调用该方法。可用于执行 GUI 元素的个性化设置等、载入资源文件等。该方法也需要对 FXMLLoader accessible，即为 public 方法或以 @FXML 注释。

执行顺序：

1. 载入 FXML 文件
2. controller 构造函数
3. `@FXML` 字段
4. `initialize()` 方法

### 10.4. 设置 controller

在代码中可以使用 FXMLLoader.setController() 设置 controller，使用 FXMLLoader.getController() 返回 controller。

FXMLLoader 有 7 个 load() 方法，其中 5 个为 static 方法。使用 getController() 必须使用 FXMLLoader 对象，并使用 load() 实例方法而非 static 方法。否则FXMLLoader.getController() 返回 null。例如：

```java
URL fxmlUrl = new URL("file:///C:/resources/fxml/test.fxml");

// Create an FXMLLoader object – a good start
FXMLLoader loader = new FXMLLoader();

// Load the document -- mistake
VBox root = loader.<VBox>load(fxmlUrl);

// loader.getController() will return null
Test controller = loader.getController();
// controller is null here
```

下面是全部的 load() 方法，只有前面 2 个是实例方法：

```java
public <T> T load() throws IOException
public <T> T load(InputStream inputStream) throws IOException

public static <T> T load(URL location) throws IOException
public static <T> T load(URL location, ResourceBundle resources)
public static <T> T load(URL location, ResourceBundle resources, 
                         BuilderFactory builderFactory)
public static <T> T load(URL location, ResourceBundle resources, 
                         BuilderFactory builderFactory, 
                         Callback<Class<?>, Object> controllerFactory)  
public static <T> T load(URL location, ResourceBundle resources, 
                         BuilderFactory builderFactory, 
                         Callback<Class<?>, Object> controllerFactory, 
                         Charset charset)
```

**示例：** 包含 controller 的 FXML

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.Button?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.layout.VBox?>
<VBox fx:controller="mjw.study.javafx.fxml.SayHelloController" spacing="10" xmlns:fx="http://javafx.com/fxml">
    <Label fx:id="msgLbl" text="FXML is cool!" prefWidth="150"/>
    <Button fx:id="sayHelloBtn" text="Say Hello" onAction="#sayHello"/>
    <style>
        -fx-padding: 10;
        -fx-border-style: solid inside;
        -fx-border-width: 2;
        -fx-border-insets: 5;
        -fx-border-radius: 5;
        -fx-border-color: blue;
    </style>
</VBox>
```

```java
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SayHelloFXMLMain extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Construct a URL for the FXML document
        URL fxmlUrl = getClass().getResource("/fxml/sayhellowithcontroller.fxml");
        VBox root = FXMLLoader.<VBox>load(fxmlUrl);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Hello FXML");
        stage.show();
    }
}
```

![|250](Pasted%20image%2020230712154531.png)

```
Initializing SayHelloController...
Location = file:/C:/repositories/java-note/target/classes/fxml/sayhellowithcontroller.fxml
Resources = null
```

