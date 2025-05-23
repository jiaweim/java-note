# FXML

2023-07-12⭐
@author Jiawei Mao
***

## FXML 概述

FXML 是基于 FXML 的标记语言，用于构建 JavaFX 界面。使用 FXML 构建界面，可以将 UI 构建从软件逻辑中分离出来。如果要修改 UI，不需要重新编译 JavaFX 代码，只需修改 FXML 文件。

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

### 2. Scene Builder

FXML 文件后缀为 `.fxml`，是纯文本文件，所以可以直接编辑。但是，当 XML 变大，直接用文本编辑器编辑很麻烦。

Gluon 公司提供了一个可视化编辑器 SceneBuilder，基于FXML构建 JavaFX 界面，下载链接：

[https://gluonhq.com/products/scene-builder/](https://gluonhq.com/products/scene-builder/)

## FXML 基础

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

![|200](images/Pasted%20image%2020230712130606.png)

点击 Button 后的界面：

![|200](images/Pasted%20image%2020230712130621.png)

### 2. 创建 FXML 文件

创建 `sayhello.fxml` 文件，将文件保存到 `resources/fxml` 目录。

### 3. 添加 UI 元素

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

### 4. 在 FXML 中导入 Java 类型

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

### 5. 在 FXML 中设置属性

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

#### 5.1. static property

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

### 6. FXML 命名空间

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

### 7. 识别符

使用 `fx:id` attribute 为 JavaFX 对象分配识别符，`Node` 的 `id` property 与其对应。

示例：为 Label 设置 `fx:id`

```xml
<Label fx:id="msgLbl" text="FXML is cool!"/>
```

### 8. 添加 Event Handler

在 FXML 中可以添加 eventHandler。设置 eventHandler 的方式与设置 properties 类似。JavaFX 类定义了 `onXxx` 属性用于为 Xxx 事件设置 event handler。例如，Button 包含一个 onAction property 用于设置 ActionEvent handler。在 FXML 中可以设置两类 event handlers:

- Script event handlers
- Controller event handlers

Controller event handlers 能够更好的分离 UI 和事件逻辑，为首选。

### 9. 加载 FXML 文件

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

<img src="images/Pasted%20image%2020230712144710.png" alt="|200" style="zoom: 67%;" />

### 10. Controller

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

#### 10.1. @FXML

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

#### 10.2. eventHandler

当 FXML 中的 eventHandler attribute 以 `#` 开头，表示该 eventHandler 在 controller 中定义。controller 中定义的 eventHandler 方法应符合以下规则：

- 没有参数或一个参数。如果有一个参数，则参数类型要与处理的事件兼容
- 如果同时定义了无参和一个参数两个版本，则 FXMLLoader 使用一个参数的版本
- eventHandler 方法通常返回 void
- eventHandler 方法对 FXMLLoader 必须 accessible，要么是 public，要么以 @FXML 注释

#### 10.3. initialize

controller 包含一个 `initialize()` 方法，FXMLLoader 载入 fxml 文件后，会调用该方法。可用于执行 GUI 元素的个性化设置等、载入资源文件等。该方法也需要对 FXMLLoader accessible，即为 public 方法或以 @FXML 注释。

执行顺序：

1. 载入 FXML 文件
2. controller 构造函数
3. `@FXML` 字段
4. `initialize()` 方法

#### 10.4. 设置 controller

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
<VBox fx:controller="mjw.javafx.fxml.SayHelloController" spacing="10" xmlns:fx="http://javafx.com/fxml">
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

## 在 FXML 中创建对象

FXML 用于创建 object graph。不同类创建对象的方式不同，如使用构造函数、valueOf() 方法、factory 方法等。FXML 支持这些创建对象的方式。

### 2. 无参构造函数

在 FXML 中使用无参构造函数创建对象很容易。如果元素名称为类名，同时该类有无参构造函数，FXML 通过该元素名创建对象。例如，VBox 具有无参构造函数，下面创建 VBox 对象：

```xml
<VBox>
    ...
</VBox>
```

### 3. static valueOf() 方法

一些 immutable 类提供 valueOf() 方法创建对象。如果 valueOf() 为 static 方法，则接受单个 String 参数，返回一个对象。

通过该方法，可以使用 `fx:value` attribute 创建对象。假设有一个 `Xxx` 类，包含一个 static `valueOf(String s)` 方法。Java 代码：

```java
Xxx x = Xxx.valueOf("a value");
```

在 FXML 定义:

```xml
<Xxx fx:value="a value"/>
```


例如，创建 Long 和 String 对象：

```xml
<Long fx:value="100"/>
<String fx:value="Hello"/>
```

`String` 还包含一个无参构造函数，创建空字符串。因此，如果需要使用空字符串，依然可以使用无参构造函数：

```xml
<!-- Will create a String object with "" as the content -->
<String/>
```

在 FXML 中使用上述内容，需要导入对应类，如 Long 和 String。

还有一点需要注意，使用 `fx:value` attribute 创建对象的类型不是元素类型而是 valueOf() 返回对象类型。例如：

```java
public static Zzz valueOf(String arg);
```

那么：

```xml
<Yyy fx:value="hello"/>
```

`fx:value` 创建的对象类型为 Zzz，而不是 Yyy。

### 4. Factory 方法

如果一个类包含创建对象的无参静态方法，就可以在 FXML 中用 `fx:factory` attribute 创建对象。例如，使用 `LocalDate` 的 `now()` 工厂方法创建 `LocalDate`：

```xml
<?import java.time.LocalDate?>
<LocalDate fx:factory="now"/>
```

`FXCollections` 类包含许多创建集合的静态方法，在 FXML 中可以用它们创建 JavaFX 集合。例如创建 `ObservableList<String>`，并添加四个值：

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

**示例：** 使用 `fx:factory` 创建 `ObservableList`

使用 `fx:factory` attribute 创建 `ObservableList`，并用其初始化 `ComboBox` 的 `items` 属性，随后设置 `ComboBox` 的初始值：

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

### 5. Builder

当使用以上方法都无法创建对象时，FXMLLoader 将查找可以创建该对象的 builder。builder 实现 `javafx.util.Builder` 接口：

```java
public interface Builder<T> {
    public T build();
}
```

FXMLLoader 使用其它方法无法创建类的对象时，会调用 `BuilderFactory` 的 getBuilder() 查找对应类的 Builder 实现。BuilderFactory 接口定义如下：

```java
package javafx.util;

@FunctionalInterface
public interface BuilderFactory {

    public Builder<?> getBuilder(Class<?> type);
}
```

FXMLLoader 使用 `JavaFXBuilderFactory` 作为 BuilderFactory 的默认实现。

FXMLLoader 支持两类 Builder：

- 如果 Builder 实现 Map 接口，则使用 put() 方法将 object properties 传递给 Builder。
- 如果 Builder 没有实现 Map 接口，Builder 必须按照 JavaBean 规则提供 getter 和 setter 方法

#### 5.1. Builder 的 JavaBean 实现

以下面的 Item 类为例：

```java
public class Item {

    private Long id;
    private String name;

    public Item(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "id=" + id + ", name=" + name;
    }
}
```

Item 没有无参构造函数，也没有  valueOf() 方法，更没有 factory 方法。所以 FXML 默认无法创建 Item 对象。

Item 有两个属性，id 和 name。

下面的 FXML 文件创建了一个 ArrayList，其中包含 3 个 Item 对象。如果使用 FXMLLoader 加载该文件，由于无法实例化 Item，会报错：

```xml
<!-- items.fxml -->
<?import com.jdojo.fxml.Item?>
<?import java.util.ArrayList?>

<ArrayList>
    <Item name="Kishori" id="100"/>
    <Item name="Ellen" id="200"/>
    <Item name="Kannan" id="300"/>
</ArrayList>
```

所以，决定创建一个 Builder 来构建 Item 对象。ItemBuilder 类如下：

```java
import javafx.util.Builder;

public class ItemBuilder implements Builder<Item> {

    private Long id;
    private String name;
	
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public Item build() {
        return new Item(id, name);
    }
}
```

`ItemBuilder` 声明了 id 和 name 两个实例变量。FXMLLoader 遇到相关 properties 时，会调用对应的 setter 方法。setters 将解析值保存在实例变量中。最后调用 build() 获得 Item 对象。

接下来还要实现与 Item 对应的 BuilderFactory。如下：

```java
import javafx.fxml.JavaFXBuilderFactory;
import javafx.util.Builder;
import javafx.util.BuilderFactory;

public class ItemBuilderFactory implements BuilderFactory {

    private final JavaFXBuilderFactory fxFactory = new JavaFXBuilderFactory();

    @Override
    public Builder<?> getBuilder(Class<?> type) {
        // You supply a Builder only for Item type
        if (type == Item.class) {
            return new ItemBuilder();
        }

        // Let the default Builder do the magic
        return fxFactory.getBuilder(type);
    }
}
```

#### 5.2. Builder 的 Map 实现

Builder 通过扩展 AbstractMap 实现 Map 接口：

```java
import javafx.util.Builder;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

public class ItemBuilderMap extends AbstractMap<String, Object> implements Builder<Item> {

    private String name;
    private Long id;

    @Override
    public Object put(String key, Object value) {
        if ("name".equals(key)) {
            this.name = (String) value;
        } else if ("id".equals(key)) {
            this.id = Long.valueOf((String) value);
        } else {
            throw new IllegalArgumentException("Unknown Item property: " + key);
        }

        return null;
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Item build() {
        return new Item(id, name);
    }
}
```

对应 BuilderFactory 实现：

```java
import javafx.fxml.JavaFXBuilderFactory;
import javafx.util.Builder;
import javafx.util.BuilderFactory;

public class ItemBuilderFactoryMap implements BuilderFactory {

    private final JavaFXBuilderFactory fxFactory = new JavaFXBuilderFactory();

    @Override
    public Builder<?> getBuilder(Class<?> type) {
        if (type == Item.class) {
            return new ItemBuilderMap();
        }
        return fxFactory.getBuilder(type);
    }
}
```

#### 5.3. Builder 测试

```java
import javafx.fxml.FXMLLoader;
import javafx.util.BuilderFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class BuilderTest {

    public static void main(String[] args) throws IOException {
        // Use the Builder with property getter and setter
        loadItems(new ItemBuilderFactory());

        // Use the Builder with Map
        loadItems(new ItemBuilderFactoryMap());
    }

    public static void loadItems(BuilderFactory builderFactory) throws IOException {
        URL fxmlUrl = BuilderTest.class.getResource("/fxml/items.fxml");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(fxmlUrl);
        loader.setBuilderFactory(builderFactory);
        ArrayList items = loader.<ArrayList>load();
        System.out.println("List:" + items);
    }
}
```

```
List:[id=100, name=Kishori, id=200, name=Ellen, id=300, name=Kannan]
List:[id=100, name=Kishori, id=200, name=Ellen, id=300, name=Kannan]
```

```ad-tip
FXMLLoader.setBuilderFactory() 替换了默认的 BuilderFactory。
```

## 创建可复用对象

有时我们希望定义一个对象，如 `Insets` 或 `Color`，然后在 FXML 文件的多个位置使用。`ToggleGroup` 就是个典型，定义一次，在多个 `RadioButton` 处使用。

使用 `<fx:define>` 定义的对象不是 object graph 的一部分，但是可以在 object graph 中使用 `fx:id` 引用。属性值必须以 `$` 开始。

**示例：** `ToggleGroup` 复用

```xml
<?import javafx.scene.layout.VBox?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.control.Button?>
<?import javafx.geometry.Insets?>
<?import javafx.scene.control.ToggleGroup?>
<?import javafx.scene.control.RadioButton?>

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

上面在 `<fx:define>` 中创建了两个对象，`Insets` 和 `ToggleGroup`，并分配了 `fx:id`。在 object graph 中引用这两个对象。

## 指定 Location

以 @ 开头的 attribute 值表示位置：

- 如果 @ 后跟着 /，则是相对 CLASSPATH 的路径
- 如果 @ 后没有 /，则是相对 FXML 文件的路径

例如，下面的 URL 是相对 FXML 文件的路径：

```xml
<ImageView>
    <Image url="@resources/picture/ksharan.jpg"/>
</ImageView>
```

而下面的 URL 是相对 CLASSPATH 的路径：

```xml
<ImageView>
    <Image url="@/resources/picture/ksharan.jpg"/>
</ImageView>
```

如果希望使用 @ 符号本身，则用 \ 进行转义，如 `\@not-a-location`。

## ResourceBundle

在 FXML 中使用 `ResourceBundle` 比在 Java 代码中使用容易。在属性值中

**示例：** 将 "%greetingText" 作为 `Label` 的 `text` 属性

该属性值以 `%` 

## Including FXML 文件

使用 `<fx:include>` 元素可以在一个 FXML 文件中包含另一个 FXML 文件。

## 使用常量

类、接口和 enum 都可以定义常量，常量是 static final 变量。FXML 可以使用 `fx:constant` 引用这些常量。

属性值为常量名称，元素名称为包含常量的类名。例如，引用 `Long.MAX_VALUE` 的方式：

```xml
<Long fx:constant="MAX_VALUE"/>
```

所有 enum 常量都属于这个类别。例如，访问 `Pos.CENTER` enum 常量：

```xml
<Pos fx:constant="CENTER"/>
```

**示例：** 在 FXML 中访问 Integer, Long 和 Pos enum 的常量

```xml
<?import javafx.scene.layout.VBox?>
<?import javafx.scene.control.TextField?>
<?import java.lang.Integer?>
<?import java.lang.Long?>
<?import javafx.scene.text.FontWeight?>
<?import javafx.geometry.Pos?>

<VBox xmlns:fx="http://javafx.com/fxml">
    <fx:define>
        <Integer fx:constant="MAX_VALUE" fx:id="minInt"/>
    </fx:define>
    <alignment><Pos fx:constant="CENTER"/></alignment>
    <TextField text="$minInt"/>
    <TextField>
        <text><Long fx:constant="MIN_VALUE"/></text>
    </TextField>
</VBox>
```

## 引用另一个元素

在 FXML 文件中，可以使用 `<fx:reference>` 元素引用文件内其它元素。使用 `fx:id` 属性指定引用元素的 `fx:id`。

```xml
<fx:reference source="fx:id of the source element"/>
```

**示例：** 使用 `<fx:reference>` 引用 `Image`

```xml
<?import javafx.scene.layout.VBox?>
<?import javafx.scene.image.Image?>
<?import javafx.scene.image.ImageView?>
<VBox xmlns:fx="http://javafx.com/fxml">
    <fx:define>
        <Image url="resources/picture/ksharan.jpg" fx:id="myImg"/>
    </fx:define>
    <ImageView>
        <image>
            <fx:reference source="myImg"/>
        </image>
    </ImageView>
</VBox>
```

也可以使用变量解引用方法重写上面内容：

```xml
<VBox xmlns:fx="http://javafx.com/fxml">
    <fx:define>
        <Image url="resources/picture/ksharan.jpg" fx:id="myImg"/>
    </fx:define>
    <ImageView image="$myImg"/>
</VBox>
```

## 复制元素

通过复制源对象的属性创建一个新对象。使用 `<fx:copy>` 元素复制：

```xml
<fx:copy source="fx:id of the source object" />
```

复制对象的类必须提供复制构造函数，即以同一个类的对象为参数的构造函数。例如：

```java
public class Item {
    private Long id;
    private String name;

    public Item() {
    }

    // The copy constructor
    public Item(Item source) {
        this.id = source.id + 100;
        this.name = source.name + " (Copied)";
    }
    ...
}
```

下面在 `<fx:define>` 中创建 `Item` 对象。多次复制 Item 对象，添加到 ComboBox 的 items 中。

```xml
<?import javafx.scene.layout.VBox?>
<?import javafx.scene.control.ComboBox?>
<?import javafx.collections.FXCollections?>
<?import com.jdojo.fxml.Item?>

<VBox xmlns:fx="http://javafx.com/fxml">
    <fx:define>
        <Item name="Kishori" id="100" fx:id="myItem"/>
    </fx:define>
    <ComboBox value="$myItem">
        <items>
            <FXCollections fx:factory="observableArrayList">
                <fx:reference source="myItem"/>
                <fx:copy source="myItem" />
                <fx:copy source="myItem" />
                <fx:copy source="myItem" />
                <fx:copy source="myItem" />
            </FXCollections>
        </items>
    </ComboBox>
</VBox>
```

## FXML 绑定属性

FXML 支持简单的属性绑定。

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

## 参考

- https://openjfx.io/javadoc/17/javafx.fxml/javafx/fxml/doc-files/introduction_to_fxml.html
