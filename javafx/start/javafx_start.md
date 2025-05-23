# JavaFX 入门

2025-05-23
@author Jiawei Mao
***

## 1. JavaFX 概述

### JavaFX 框架

JavaFX 是继 AWT 和 Swing 后，Java 平台的下一代开源 GUI 框架。其主要特性有：

- 纯 Java 编写
- 支持数据绑定
- 可以使用任何 JVM 脚本语言编写，如 Kotlin, Groovy 和 Scala
- JavaFX 提供两种构建 UI 的方式：Java 代码和 FXML。FXML 是一个基于 XML 的标记语言，以声明的方式定义UI。Scene Builder 提供以可视化的方式编辑 FXML 文件。
- 支持多媒体
- 可以内嵌网页
- 提供了开箱即用的特效和动画，这对开发游戏很重要，通过几行代码就可以实现复杂的动画

JavaFX 框架如下所示：

![](images/Pasted%20image%2020230704214212.png)

JavaFX 的 GUI 被构造为 scene-graph。scene-graph 是可视化元素集合，这些可视化元素称为 node，以树形结构排列。scene-graph 中的 nodes 可以处理用户输入和手势，支持特效、变换和状态。`Node` 类型包括简单的 UI 控件，如 `Button`、`TextField`、2D 和 3D Shapes, Image 以及 Media (audio 和 video)、Web 内容、Chart 等。

JavaFX 核心 API 主要包括四个部分：

| 组件                    | 说明                                                         |
| ----------------------- | ------------------------------------------------------------ |
| Prism                   | 渲染 scene-graph 的图形引擎，如果硬件加速渲染不可用（Windows上的 DirectX, Mac Linux 上 的 OpenGL），则使用 Java2D |
| Glass Windowing Toolkit | 提供窗口管理、时间等服务，同时负责管理事件队列。JavaFX 由操作系统级线程 JavaFX 应用线程管理事件队列，所有输入事件和 UI 修改都在该线程上调度 |
| Media Engine            | 为JavaFX提供多媒体支持，media engine 使用单独的线程处理 media frames，使用 JavaFX 应用线程进行同步。media engine 是基于开源的 GStreamer 框架构建 |
| Web Engine              | 用于处理 scene-graph 中内嵌的HTML内容。Prism 负责渲染网页内容。web engine 基于开源的 WebKit 构建，支持HTML5,CSS,JavaScript,DOM和AVG |

Prism 使用单独线程（非 JAT）进行渲染。它通过提前渲染下一帧来加速渲染。当 scene-graph 被修改，如在 `TextField` 中输入文字，Prism 重新渲染 scene-graph。scene-graph 与 Prism 的同步通过 *pulse* 事件完成。当 scene-graph 被修改需要重新渲染时，*pulse* 事件在 JAT 线程排队。*pulse* 事件表示 scene-graph 与 Prism 的渲染层不同步，应渲染 Prism 的最新帧。*pulse* 事件限制为**每秒最多 60 帧**。

Media engine 提供媒体支持，如播放音频、视频。它利用平台上已有的 codecs。media-engine 使用单独的线程处理媒体帧，使用 JAT 将帧与 scene-graph 同步。media-engine 基于开源的多媒体框架 GStreamer 实现。

Web engine 负责处理嵌入在 scene-graph 中的 HTML 内容。Web engine 基于开源 web 浏览器引擎 WebKit 实现，支持 HTML5、CSS、JavaScript 和 DOM。

Quantum toolkit 对 Prism, Glass, media engine 和 web engine 的底层组件进行抽象。

### JavaFX 历史

JavaFX 最初由 SeeBeyond 的 Chris Oliver 开发，那时被称为 F3 (Form Follows Function)。F3 是一种 Java 脚本语言，用于轻松开发 GUI 应用。它提供了声明式的语法、静态类型、类型推断、数据绑定、动画、2D graphics 和 Swing 组件。

- 2007 年 Sun Microsystems 收购了 SeeBeyond，F3 更名为 JavaFX
- 2010 年 Oracle 收购了 Sun Microsystems
- 2013 年 Oracle 开源 JavaFX

JavaFX 的第一个版本于 2008 年第四季度发布。

从 Java 8 开始，JavaFX 的版本号与 Java 一致，从 2.2 跳到 8.0。Java SE 和 JavaFX 的 major 版本同时发布。

从 Java SE 11 开始，JavaFX 不再是 Java SE 运行库的一部分，需要单独下载、安装。

| 发布日期 | 版本          | 说明                                                                                                    |
| -------- | ------------- | ------------------------------------------------------------------------------------------------------- |
| Q4, 2008 | JavaFX 1.0    | JavaFX 初始版本，使用声明式语言 JavaFX 脚本编写 JavaFX 代码                                             |
| Q1, 2009 | JavaFX 1.1    | 引入对 JavaFX Mobile 的支持                                                                             |
| Q2, 2009 | JavaFX 1.2    | –                                                                                                       |
| Q2, 2010 | JavaFX 1.3    | –                                                                                                       |
| Q3, 2010 | JavaFX 1.3.1  | –                                                                                                       |
| Q4, 2011 | JavaFX 2.0    | 放弃对 JavaFX 脚本的支持，使用 Java 编写 JavaFX 代码。删除对 JavaFX Mobile 的支持                       |
| Q2, 2012 | JavaFX 2.1    | 引入对 Mac OS 的支持                                                                                    |
| Q3, 2012 | JavaFX 2.2    | –                                                                                                       |
| Q1, 2014 | JavaFX 8.0    | JavaFX 版本从 2.2 跃升至 8.0。JavaFX 和 Java SE 版本从 Java 8 开始保持一致                              |
| Q2, 2015 | JavaFX 9.0    | 公开了一些内部 API, JEP253                                                                              |
| Q3, 2018 | JavaFX 11.0.3 | JavaFX 不再是 Oracle Java JDK 的一部分，需从 Gluon 公司下载。支持作为端口添加的手持设备和其它嵌入式设备 |
| Q1, 2019 | JavaFX 12.0.1 | Bug 修复和功能增强                                                                                      |
| Q3, 2019 | JavaFX 13.0   | Bug 修复和功能增强                                                                                      |
| Q1, 2020 | JavaFX 14.0   | WebView 支持 HTTP/2. Bug 修复和功能增强                                                                 |
| Q3, 2020 | JavaFX 15.0   | 改进稳定性（内存管理）。Bug 修复和功能增强                                                              |
| Q1, 2021 | JavaFX 16.0   | 必须从 module path 载入 JavaFX modules，从类路径载入发出编译器警告。Bug 修复和功能增强                  |
| Q4, 2021 | JavaFX 17.0.1 | 少量功能增强和 Bug 修复                                                                                 |

JavaFX 11 之后的发布详细信息可参考： https://github.com/openjdk/jfx/tree/master/doc-files

## 2. JavaFX 程序

### JavaFX runtime

`Application` 是 JavaFX 应用入口。在调用 `launch()` 启动 JavaFX 应用时，首先启动 JavaFX runtime。JavaFX runtime 会创建多个线程。其中两个线程在 JavaFX 生命周期中会调用 `Application` 的方法：

- JavaFX 启动线程（JavaFX Launcher Thread, JLT）
- JavaFX 应用线程（**JavaFX Application Thread**, JAT）

**启动 JavaFX runtime**

`Platform.startup` 启动 JavaFX runtime：

```java
public static void startup(Runnable runnable)
```

- `startup()` 启动 JavaFX runtime
- JavaFX runtime 创建 JAT
- 在 JAT 中调用 `runnable`

通常没必要显式调用 `startup()`，因为在构建 JavaFX 应用时会自动调用。当然，直接调用 `startup()` 也没问题，该**方法启动 JavaFX runtime**，此时还没 JAT 线程，所以也可以在 main 线程中调用该方法启动 JavaFX runtime。

`startup()` 不会被调用的 `Runnable` 阻塞，在 `startup()` 返回后，可以调用 `Platform.runLater(Runnable)` 调用更多的 `Runnable`，这些 `Runnable` 也是在 JAT 调用。

以下情况会导致 JavaFX runtime 自动启动：

- 扩展 `Application` 的标准 JavaFX 应用，使用 Java launcher 或 `Application` 中的 launch 启动 JavaFX 应用，在加载 `Application` 类之前，JavaFX runtime 自动初始化
- 使用 `JFXPanel` 显示 FX 内容的 Swing 应用，在第一次构造 `JFXPanel` 实例时初始化 FX runtime
- 使用 `FXCanvas` 显示 FX 内容的 SWT 应用，在第一次构造 `FXCanvas` 实例时初始化 FX runtime

需要注意，只能在 JavaFx runtime 尚未初始化时调用 `startup()`。JavaFX runtime 正在运行时调用 `startup()` 抛出  `IllegalStateException`。

> [!NOTE]
>
> JavaFX 类必须从 module 路径上的一组命名模块 `javafx.*` 中载入。不支持从 classpath 载入。当 JavaFX 类不是从预期的命名模块中载入，在启动 JavaFX runtime 时会发出警告。不管 JavaFX runtime 是调用 `startup()` 启动 ，还是自动启动，都会发出警告。    

### JavaFX 应用的生命周期

加载 JavaFX 应用时，JavaFX runtime 执行如下操作：

1. 启动 JavaFX runtime
2. 构造指定的 `Application` 类：JavaFX runtime 在 JAT 线程中调用 `Application` 的无参构造函数创建指定的 `Applicaiton` 对象
3. 调用 `Application.init()`：默认实现为空
4. 调用 `Application.start(Stage stage)`：为 `abstract` 方法，必须实现
5. 等待应用结束
    - 调用 `Platform.exit()` 结束
    - `Platform` 的 `implicitExit` 属性为 `true`，且关闭所有窗口
6. 调用 `Application.stop()`：默认实现为空

调用 `start()` 后，`launch()` 在 JavaFX 应用完成后返回。JavaFX 执行完毕后，JAT 调用 `Application.stop()`。

`Application.init()` 在 JLT 线程中调用，JLT 线程中不能创建 `Stage` 和 `Scene`，它俩只能在 JAT 线程中创建。但是可以在 JLT 中创建 UI 控件，即可以在 `init()` 方法中提前初始化一些 UI 控件。

!!! attention
    `Stage` 和 `Scene` 只能在 JAT 中创建。

**示例：** 演示 JavaFX 生命周期

```java{.line-numbers}
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXLifeCycleApp extends Application {

    public FXLifeCycleApp() {
        String name = Thread.currentThread().getName();
        System.out.println("FXLifeCycleApp() constructor: " + name);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() {
        String name = Thread.currentThread().getName();
        System.out.println("init() method: " + name);
    }

    @Override
    public void start(Stage stage) {
        String name = Thread.currentThread().getName();
        System.out.println("start() method: " + name);

        Scene scene = new Scene(new Group(), 200, 200);
        stage.setScene(scene);
        stage.setTitle("JavaFX Application Life Cycle");
        stage.show();
    }

    @Override
    public void stop() {
        String name = Thread.currentThread().getName();
        System.out.println("stop() method: " + name);
    }
}
```

```
FXLifeCycleApp() constructor: JavaFX Application Thread
init() method: JavaFX-Launcher
start() method: JavaFX Application Thread
stop() method: JavaFX Application Thread
```

!!! tip
    `Application` 的 `launch()` 方法在关闭所有窗口后，或者使用 `Platform.exit()` 退出后，才返回。    

### 启动 JavaFX 应用

JavaFX 应用类均继承 `Application` 类。`Application` 包含一个 static `launch()` 方法，用于启动 JavaFX 应用。它有两个重载版本：

```java
public static void launch(Class<? extends Application> appClass, String... args)

public static void launch(String... args)
```

> [!WARNING]
>
> JavaFX `Application` 类必须有无参构造函数，否则抛出 runtime 异常。    

**版本一：** 传入 `Application` 引用

第一个 `launch()` 方法很清楚。参数 1 为 `Application` 类的引用，参数 2 为应用参数。例如：

```java
public class MyJavaFXApp extends Application {
    public static void main(String[] args) {
        Application.launch(MyJavaFXApp.class, args);
    }
    // More code goes here
}
```

传入 `launch` 的类不必与调用该方法的类相同，调用 `launch` 的类甚至不用继承 `Application`。例如：

```java
public class MyAppLauncher {
    public static void main(String[] args) {
        Application.launch(MyJavaFXApp.class, args);
    }
    // More code goes here
}
```

**版本二**：不传入 `Application` 引用

第二个 `launch()` 方法根据调用 `launch()` 的类来识别 `Application`。如果调用 `launch()` 方法的类继承自 `Application`，则启动该 JavaFX，否则抛出 runtime 异常。

- 调用类继承自 `Application`

```java
public class MyJavaFXApp extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }
    // More code goes here
}
```

`MyJavaFXApp` 继承自 `Application`，作为 `Application` 启动。

- 调用类没有继承 `Application`

如果调用的类没有继承 `Application`，抛出异常。

```java
public class Test {
    public static void main(String[] args) {
        Application.launch(args);
    }
    // More code goes here
}
```

```
Exception in thread "main" java.lang.RuntimeException: Error: class Test is not a subclass of javafx.application.Application
    at javafx.application.Application.launch(Application.java:308)
    at Test.main(Test.java)
```

- 匿名内部类

```java
public class MyJavaFXApp extends Application {
    public static void main(String[] args) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                Application.launch(args);
            }
        });
        t.start();
    }
    // More code goes here
}
```

```java
Exception in thread "Thread-0" java.lang.RuntimeException: Error: class MyJavaFXApp$1 is not a subclass of javafx.application.Application
    at javafx.application.Application.launch(Application.java:211)
    at MyJavaFXApp$1.run(MyJavaFXApp.java)
    at java.lang.Thread.run(Thread.java:722)
```

这个错误比较隐晦。虽然 `MyJavaFXApp` 继承了 `Application`，但实际调用 `launch()` 的是 `MyJavaFXApp$1` 匿名内部类，该类继承自 `Object`，而不是 `Application`。

> [!TIP]
>
> 

### 终止 JavaFX 应用

可以显式或隐式终止 JavaFX 应用。

- 调用 `Platform.exit()` 显式终止

如果在 `Application` 的 `start()` 之后或在 `start()` 方法中调用 `Platform.exit()`，将调用 `Application.stop()` 方法，然后终止 JAT 线程。此时，如果剩下守护线程在运行，则退出 JVM。

如果在 `Application` 的构造函数或 `init()` 中调用 `Platform.exit()`，则可能不会调用 `stop()` 方法。

直接调用 `System.exit(int)` 也可以退出 JavaFX 应用，但不会调用 `stop()` 方法。

- 隐式终止

当最后一个窗口关闭，JavaFX 隐式终止。使用静态方法 `Platform.setImplicitExit(boolean implicitExit)` 开启或关闭该行为。

隐式终止默认开启，所以关闭所有窗口后，JavaFX 终止。在终止 JAT 之前，会调用 `Application.stop()` 方法。

但是，终止 JAT 线程不一定退出 JVM。只有在所有非守护线程终止后，JVM 才会退出。

如果关闭隐式终止，就只能调用 `Platform.exit()` 显式终止。

## 3. 传入 JavaFX 应用参数

和 Java 程序一样，JavaFX 可以通过命令行或 IDE 的配置传入参数。

JavaFX 将参数保存在 `Application` 的 static 内部类 `Parameters` 中。`Parameter` 将参数分为三类：

- 命名参数（Named parameters）：由 name 和 value 两部分组成
- 非命名参数（Unnamed paramters）：单个值
- 原始参数（包含命名参数和非命名参数）

`Paramters` 类下面三种方法访问这三类参数：

```java
Map<String, String> getNamed();

List<String> getUnnamed();

List<String> getRaw();
```

`Application.getParameters()` 返回 `Application.Paramters` 类的引用。

`Parameters` 在 `Application` 的 `init()` 中可用，在 `Application` 构造函数中不可用。即 `Paramters` 是在 `Application` 构造之后，`init()` 之前创建。

### 示例

**示例：**  访问传入 JavaFX 的所有参数

传入的参数在 `TextArea` 中显示。

```java
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class FXParamApp extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Get application parameters
        Parameters p = this.getParameters();
        Map<String, String> namedParams = p.getNamed();
        List<String> unnamedParams = p.getUnnamed();
        List<String> rawParams = p.getRaw();

        String paramStr = "Named Parameters: " + namedParams + "\n" +
                "Unnamed Parameters: " + unnamedParams + "\n" +
                "Raw Parameters: " + rawParams;

        TextArea ta = new TextArea(paramStr);
        Group root = new Group(ta);
        stage.setScene(new Scene(root));
        stage.setTitle("Application Parameters");
        stage.show();
    }
}
```

- 以如下参数运行上面的示例

```sh
java [options] javafx.example.FXParamApp Anna Lola
```

上面传入了两个非命名参数：Anna 和 Lola。输出如下：

```
Named Parameters: {}
Unnamed Parameters: [Anna, Lola]
Raw Parameters: [Anna, Lola]
```

- 传入命名参数

命名参数的 key 前面有两个 `--`。即格式为：

```
--key=value
```

命令示例：

```sh
java [options] javafx.example.FXParamApp Anna Lola --width=200 --height=100
```

输出：

```
Named Parameters: {height=100, width=200}
Unnamed Parameters: [Anna, Lola]
Raw Parameters: [Anna, Lola, --width=200, --height=100]
```

