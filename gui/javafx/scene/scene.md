# JavaFX Scene

2025-05-23 ⭐
update
2023-07-31
add: Hello World
2023-07-06
@author Jiawei Mao

***
## Scene 概述

`Stage` 是本地操作系统窗口的 JavaFX 表示；`Scene` 包含可视化内容，在 `Stage` 中展示；一个 `Stage` 最多绑定一个 `Scene`。将已有 `Stage` 的 `Scene` 添加到另一个 `Stage`，会自动从上一个 `Stage` 分离。

`Scene` 是 scene-graph 的容器，scene-graph 是所有可视化元素组成的树形结构。树的节点以 `javafx.scene.Node` 类表示。

scene-graph 的树形结构如下所示：

<img src="images/Root%20Node.png" style="zoom:50%;" />

JavaFX 对 scene-graph 中的分支节点和叶节点都提供对应的类。

在 scene-graph 中，parent-node 的许多属性被 children-nodes 共享。例如，应用于 parent-node 的变换或事件会递归并应用于其 children。

### Node 类图

`javafx.scene.Node` 是所有节点的超类。下面是 nodes 的部分类图：

<img src="images/Pasted%20image%2020230706102448.png" style="zoom: 33%;" />

`Scene` 总有一个根节点。如果根节点 resizable，如 `Region` 及其子类，当 scene 调整大小，根节点也会随之调整大小。

`Group` 是 nonresizable 的 `Parent` 节点，也可以作为 `Scene` 的根节点，此时 scene graph 不会随 Scene 的大小的变化而变化，因此 scene graph 可能被截断，只显示一部分。

`Parent` 是 branch nodes 的基类。`ImageView`, `Canvas`, `Shape` 则是 leaf node 类型。

### Scene 属性

Scene 常用属性

|类型|名称|说明|
|---|---|---|
|`ObjectProperty<Cursor>`|cursor|Scene 的鼠标光标|
|`ObjectProperty<Paint>`|fill|Scene 的填充背景|
|`ReadOnlyObjectProperty<Node>`|focusOwner|Scene 中持有焦点的 node|
|`ReadOnlyDoubleProperty`|height|Scene 高度|
|`ObjectProperty<Parent>`|root|scene graph 的根节点|
|`ReadOnlyDoubleProperty`|width|Scene 宽度|
|`ReadOnlyObjectProperty<Window>`|window|Scene 的 Window|
|`ReadOnlyDoubleProperty`|x|Scene 在 Window 的水平位置|
|`ReadOnlyDoubleProperty`|y|Scene 在 Window 的垂直位置|

### Hello World

下面是 "Hello World" scene graph 的一个例子：

<img src="images/Pasted%20image%2020230731214055.png" style="zoom:50%;" />

其代码实现很简单：

```java
public class HelloApp extends Application {

    private Parent createContent() {
        return new StackPane(new Text("Hello World"));
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(createContent(), 300, 300));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

<img src="images/Pasted%20image%2020230731214349.png" style="zoom:50%;" />

## 图形渲染模式

Scene graph 在屏幕上呈现 JavaFX 应用起着至关重要的作用。

在屏幕上渲染图形的 API 有两类：

- 即时模式
- 保留模式

在**即时模式**，应用负责执行绘图命令，图形直接渲染到屏幕。当需要重新渲染时，应用进程需要重新向屏幕发出绘图命令。Java2D 为即时模式。

在**保留模式**，应用负责创建 graph。图形库（graphics library）将 graph 保留在内存，在需要时将 graph 渲染到屏幕。应用代码只负责创建 graph（what），图形库负责保存和渲染 graph（when and how）。保留模式渲染 API 将开发人员从渲染图形工作中解放出来。

对比即时模式，保留模式占用更多内存。但是使用更简单，JavaFX scene graph 使用保留模式。下图是这两种 API 的示意图：

@import "images/Pasted%20image%2020230706111657.png" {width="400px" title=""}

JavaFX 这种保留模式对游戏开发不太好，因为游戏开发需要绝对控制渲染，通常会创建自己的游戏循环。在其中控制渲染。

这在 JavaFX 中无法实现，因为 scene graph 最高更新频率为额60 fps，没有办法修改这个渲染过程。另外，与即时渲染模式相比，scene graph 会有额外开销，从而影响性能。scene graph 包含的 nodes 越多，需要的算力和内存越大。

可以想象，如何你创建一个包含大量图形元素的复杂控件，该控件在使用时很可能降低程序性能。为了解决该问题，JavaFX 提供了 Canvas。

在 Canvas 单个 Node 上可以绘制复杂的图形，而渲染速率依然是 60 fps。

## 设置 Cursor

`javafx.scene.Cursor` 类表示鼠标光标。`Cursor` 类中定义了许多常量，如 `HAND`, `CLOSED_HAND`, `DEFAULT`, `TEXT`, `NONE`, `WAIT` 等，代表不同光标类型。

### 设置光标

将 Scene 的光标设置为 `WAIT`：

```java
Scene scene;
...
scene.setCursor(Cursor.WAIT);
```

### 自定义光标

`Cursor` 类包含如下 static 方法：

```java
public static Cursor cursor(final String identifier)
```

如果 `identifier` 是标准光标名称，则返回对应的标准光标；否则将 `identifier` 视为 bitmap 的 URL，以该 bitmap 创建新的光标。例如，下面以 mycur.png 创建光标：

```java
// 以 bitmap 自定义光标
URL url = getClass().getClassLoader().getResource("mycur.png");
Cursor myCur = Cursor.cursor(url.toExternalForm());
scene.setCursor(myCur);

// 使用名称获取标准光标
Cursor waitCur = Cursor.cursor("WAIT")
scene.setCursor(waitCur);
```

## Focus Owner

`Scene` 中只能有一个 `Node` 持有焦点。`Scene` 的 `focusOwner` 属性保存持有焦点的 `Node` 类。

`focusOwner` 是 read-only 属性。如果需要 Scene 中特定 node 持有焦点，调用 `Node.requestFocus()` 即可。

Scene.getFocusOwner() 返回 scene 中持有焦点的节点。如果没有任何 node 持有焦点，返回 null。

需要注意，focusOwner 和 haveFocus 是两码事。每个 Scene 可以有一个 focusOwner。例如，如果你打开了两个窗口，有两个 Scene，因此可以有两个 focusOwner。然后，一次只能有一个 haveFocus，活动窗口的某个 node 持有焦点。

要确定 focusOwner 是否持有焦点，可以用 Node.focused 属性检查。

下面是使用 focusOwner 的典型逻辑：

```java
Scene scene;
...
Node focusOwnerNode = scene.getFocusOwner();
if (focusOwnerNode == null) {
    // scene 中没有 focusOwner
} else if (focusOwnerNode.isFocused()) {
    // focusOwner 持有 focus
} else {
    // focusOwner 没有 focus
}
```

## Platform

`javafx.application.Platform` 是用于支持平台相关功能的工具类。它包含许多 static 方法，如下所示：

| 方法                                              | 说明                     |  
| ------------------------------------------------- | ----------------------------- | 
| `void exit()`                                     | 终止 JavaFX 应用                   |   
| `boolean isFxApplicationThread()`                 | 是否在 JAT 线程                           | 
| `boolean isImplicitExit()`                        | 是否隐式退出（所有窗口关闭后自动终止 JavaFX）|    
| `boolean isSupported(ConditionalFeature feature)` | 当前平台是否支持指定 ConditionalFeature    |
| `void runLater(Runnable runnable)`                | 在 JAT 执行指定 Runnable                |
| `void setImplicitExit(boolean value)`             | 设置隐式退出                           |

### runLater

`runLater()` 提交 Runnable 任务到 JAT 线程。

```ad-note
`Platform.runLater()` 在其它线程调用：即在其它线程创建好任务，然后调用 `Platform.runLater()` 将任务提交到 JAT 线性执行。
```

**示例：** runLater() 

```java
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RunLaterApp extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() {
        System.out.println("init(): " + Thread.currentThread().getName());

        // Create a Runnable task
        Runnable task = () -> System.out.println("Running the task on the "
                + Thread.currentThread().getName());

        // Submit the task to be run on the JavaFX Aplication Thread
        Platform.runLater(task);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(new Group(), 400, 100));
        stage.setTitle("Using Platform.runLater() Method");
        stage.show();
    }
}
```

```
init(): JavaFX-Launcher
Running the task on the JavaFX Application Thread
```

可以看到，Application 的 init() 方法在 JLT 线程执行，在 init() 中调用 Platform.runLater() 将任务提交到了 JAT 线程。

### ConditionalFeature

JavaFX 实现有部分功能是可选的，这些功能有些平台不支持。这些可选功能定义在 enum 类型 javafx.application.ConditionalFeature 中：

| Enum               | 说明                       |
| ------------------ | -------------------------- |
| EFFECT             | 特效，如镜像、阴影等       |
| INPUT_METHOD       | 文本输入方法               |
| SCENE3D            | 3D 特征                    |
| SHAPE_CLIP         | 以任何形状裁剪 Node 的功能 |
| TRANSPARENT_WINDOW | 完整窗口透明度             |

假设你需要使用 JavaFX 3D 功能，可以使用如下方式检查是否启用该功能：

```java
import javafx.application.Platform;
import static javafx.application.ConditionalFeature.SCENE3D;
...
if (Platform.isSupported(SCENE3D)) {
    // Enable 3D features
} else {
    // Notify the user that 3D features are not available
}
```

## Host 环境

`javafx.appliction.HostServices` 类提供托管 JavaFX 应用的启动环境相关的服务。无法直接创建 HostServices，而是通过 Application.getHostServices() 获得：

```java
HostServices host = getHostServices();
```

`HostServices` 包含如下方式：

- String getCodeBase()
- String getDocumentBase()
- String resolveURI(String base, String relativeURI)
- void showDocument(String uri)

`getCodeBase()` 返回应用代码库的 URI。在独立模式，返回启动应用 JAR 所在目录的 URI。如果是从 class 文件启动，返回空字符串。

`getDocumentBase()` 返回文档库的 URI。在独立模式，返回应用进程当前目录的 URI。

`resolveURI()` 使用指定的 base URI 和相对 URI 生成解析后的 URI。

`showDocument(String uri)` 在浏览器打开指定 URI。

**示例：** 演示 HostServices 的所有方法

```java
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.HashMap;
import java.util.Map;

public class KnowingHostDetailsApp extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        String yahooURL = "http://www.yahoo.com";
        Button openURLButton = new Button("Go to Yahoo!");
        openURLButton.setOnAction(e -> getHostServices().showDocument(yahooURL));

        Button showAlert = new Button("Show Alert");
        showAlert.setOnAction(e -> showAlert());

        VBox root = new VBox();

        // Add buttons and all host related details to the VBox
        root.getChildren().addAll(openURLButton, showAlert);

        Map<String, String> hostdetails = getHostDetails();
        for (Map.Entry<String, String> entry : hostdetails.entrySet()) {
            String desc = entry.getKey() + ": " + entry.getValue();
            root.getChildren().add(new Label(desc));
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Knowing the Host");
        stage.show();
    }

    protected Map<String, String> getHostDetails() {
        Map<String, String> map = new HashMap<>();
        HostServices host = this.getHostServices();

        String codeBase = host.getCodeBase();
        map.put("CodeBase", codeBase);

        String documentBase = host.getDocumentBase();
        map.put("DocumentBase", documentBase);

        String splashImageURI = host.resolveURI(documentBase, "splash.jpg");
        map.put("Splash Image URI", splashImageURI);

        return map;
    }

    protected void showAlert() {
        Stage s = new Stage(StageStyle.UTILITY);
        s.initModality(Modality.WINDOW_MODAL);

        Label msgLabel = new Label("This is an FX alert!");
        Group root = new Group(msgLabel);
        Scene scene = new Scene(root);
        s.setScene(scene);

        s.setTitle("FX Alert");
        s.show();
    }
}
```

![](images/Pasted%20image%2020230706133816.png)

## NodeHelper

```java
public static void markDirty(Node node, DirtyBits dirtyBit)
```

当 `NodeHelper.markDirty` 被调用，整个 scene/subscene 在下一个 pulse 被重绘。尽管调用该方法的 node 知道哪些 nodes 变脏了，依然会全部重绘。

