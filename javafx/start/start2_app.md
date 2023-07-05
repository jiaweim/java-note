# JavaFX 程序

2023-07-05, 12:54
****
## 1. JavaFX runtime

`Application` 是 JavaFX 应用入口。在调用 `launch()` 启动 JavaFX 应用时，首先启动 JavaFX runtime。JavaFX runtime 会创建多个线程。其中两个线程在 JavaFX 生命周期中会调用 `Application` 的方法：

- JavaFX-Launcher Thead (JLT)
- JavaFX Application Thread (JAT)

**启动 JavaFX runtime**

`Platform` 的 `startup` 启动 JavaFX runtime：

```java
public static void startup(Runnable runnable)
```

然后在 **JAT 线程**中调用指定的 `Runnable`。通常没必要显式调用 startup，因为在构建 JavaFX 应用时会自动调用。但是直接调用 `startup()` 也没问题，该方法启动 JavaFX runtime，此时还没 JAT 线程，所以在 main 线程中直接调用该方法。

`startup()` 不会被调用的 `Runnable` 阻塞，在 `startup()` 返回后，可以调用 `Platform.runLater(Runnable)` 调用更多的 `Runnable`，这些 `Runnable` 也是在 JAT 调用。

需要注意，只能在 JavaFx runtime 尚未初始化时调用 `startup()`。

以下情况会导致 JavaFX runtime 自动启动：

- 扩展 `Application` 的标准 JavaFX 应用，使用 Java launcher 或 `Application` 中的 launch 启动 JavaFX 应用，在加载 `Application` 类之前，JavaFX runtime 自动初始化
- 使用 `JFXPanel` 显示 FX 内容的 Swing 应用，在第一次构造 `JFXPanel` 实例时初始化 FX runtime
- 使用 `FXCanvas` 显示 FX 内容的 SWT 应用，在第一次构造 `FXCanvas` 实例时初始化 FX runtime

JavaFX runtime 正在运行时调用 `startup()`，抛出  `IllegalStateException`。

```ad-note
JavaFX 类必须从 module 路径上的一组命名模块 `javafx.*` 中载入。不支持从 classpath 载入。当 JavaFX 类不是从预期的命名模块中载入，在启动 JavaFX runtime 时会发出警告。不管 JavaFX runtime 是调用 startup 启动 ，还是自动启动，都会发出警告。
```

## 2. JavaFX 应用的生命周期

加载 JavaFX 应用时，JavaFX runtime 执行如下操作：

1. 启动 JavaFX runtime
2. 构造指定的 `Application` 类
3. 调用 `Application.init()`
4. 调用 `Application.start(Stage stage)`
5. 等待应用结束
    1. 调用 `Platform.exit()` 结束
    2. `Platform` 的 `implicitExit` 属性为 `true`，且关闭所有窗口
6. 调用 `Application.stop()`

JavaFX runtime 在 JAT 线程中调用 `Application` 的无参构造函数创建指定的 `Applicaiton` 对象。

`start()` 为 `abstract` 方法，必须实现。`init` 和 `stop` 默认实现为空。

调用 `start()` 后，`launch()` 等待直到 JavaFX 应用完成。JavaFX 执行完毕后，JAT 调用 `Application` 的 `stop()`，其默认实现为空。

| 顺序 | 方法             | 调用线程 | 说明                                                                                                |
| ---- | ---------------- | -------- | ---------------------------------------------------------------------------------------------------- |
| 1    | no-args 构造函数 | JAT      | JavaFX runtime 调用 Application 的无参构造函数创建指定 Application 对象                                   |
| 2    | init()           | JLT      | JLT 线程中不允许创建 `Stage` 或 `Scene`，它们只能在 JAT 中创建。但可以创建 UI controls。默认为空，可以根据需要重写 |
| 3    | start()          | JAT      | Application 中的 abstract 方法，需要实现                                                               |
| 4    | stop()           | JAT      |                                                                                                |


**示例：** 演示 JavaFX 生命周期

```java
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


主 `Stage` 由 application launcher 创建，但是只有舞台，舞台上的 scene 需要自定义。

```ad-tip
`Application` 的 `launch()` 方法在关闭所有窗口后，或者使用 `Platform.exit()` 退出后，才返回。
```

## 3. 启动 JavaFX 应用

JavaFX 应用类均继承 `Application` 类。`Application` 包含一个 static `launch()` 方法，用于启动 JavaFX 应用。它有两个重载版本：

```java
public static void launch(Class<? extends Application> appClass, String... args)

public static void launch(String... args)
```

```ad-warning
JavaFX `Application` 类必须有 no-args 构造函数；否则抛出 runtime 异常。
```

**版本一：** 传入 `Application` 引用

第一个 `launch()` 方法很清楚。参数一为 `Application` 类的引用，参数二为应用参数。例如：

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

第二个 `launch()` 方法根据调用 `launch()` 的类来识别 `Application`。如果调用 `launch()` 方法的类继承自 `Application`，则启用该 JavaFX，否则抛出 runtime 异常。

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

```
Exception in thread "Thread-0" java.lang.RuntimeException: Error: class MyJavaFXApp$1 is not a subclass of javafx.application.Application
    at javafx.application.Application.launch(Application.java:211)
    at MyJavaFXApp$1.run(MyJavaFXApp.java)
    at java.lang.Thread.run(Thread.java:722)
```

这个比较隐晦。虽然 `MyJavaFXApp` 继承了 `Application`，但实际调用 `launch()` 的是 `MyJavaFXApp$1` 匿名内部类，该类继承自 `Object`，而不是 `Application`。

## 4. 终止 JavaFX 应用

可以显式或隐式终止 JavaFX 应用。

- 调用 `Platform.exit()` 显式终止

如果在 `Application` 的 `start()` 之后或在 `start()` 方法中调用 `Platform.exit()`，将调用 `Application.stop()` 方法，然后终止 JAT 线程。此时，如果剩下守护线程在运行，则退出 JVM。

如果在 `Application` 的构造函数或 `init()` 中调用 `Platform.exit()`，则可能不会调用 `stop(`) 方法。

直接调用 System.exit(int) 也可以退出 JavaFX 应用，但不会调用 stop() 方法。

- 隐式终止

当最后一个窗口关闭，JavaFX 隐式终止。使用静态方法 `Platform.setImplicitExit(boolean implicitExit)` 开启或关闭该行为。

隐式终止默认开启，所以关闭所有窗口后，JavaFX 终止。在终止 JAT 之前，会调用 `Application.stop()` 方法。

但是，终止 JAT 线程不一定退出 JVM。只有在所有非守护线程终止后，JVM 才会退出。

如果关闭隐式终止，就只能调用 `Platform.exit()` 显式终止。

