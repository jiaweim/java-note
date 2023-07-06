# Platform

2023-07-06, 13:22
****
## 1. 简介

javafx.application.Platform 是用于支持平台相关功能的工具类。它包含许多 static 方法，如下所示：

| 方法                                              | 说明                     |  
| ------------------------------------------------- | ----------------------------- | 
| `void exit()`                                     | 终止 JavaFX 应用                   |   
| `boolean isFxApplicationThread()`                 | 是否在 JAT 线程                           | 
| `boolean isImplicitExit()`                        | 是否隐式退出（所有窗口关闭后自动终止 JavaFX）|    
| `boolean isSupported(ConditionalFeature feature)` | 当前平台是否支持指定 ConditionalFeature    |
| `void runLater(Runnable runnable)`                | 在 JAT 执行指定 Runnable                |
| `void setImplicitExit(boolean value)`             | 设置隐式退出                           |

## 2. runLater

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

## 3. ConditionalFeature

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

