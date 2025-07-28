# JavaFX 并发

2023-07-11, 20:18
****
## 1. 简介

Java GUI 应用必然是多线程的。JavaFX 和 Swing、AWT 一样，专门使用一个线程处理 UI 事件，即 JavaFX Application Thread (JAT)。

scene graph 中的 nodes 不是线程安全的，由于不用同步，所以更快；缺点是必须从 JAT 线程访问这些 UI nodes。必须从 JAT 线程访问 UI 元素意味着 UI 事件不应该处理耗时任务，否则会导致 GUI 无响应。

**示例：** 无响应 GUI

包含 3 个控件：

- Label 显示 task 进度
- Start 启动 task
- Exit 退出应用

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UnresponsiveUI extends Application {

    Label statusLbl = new Label("Not Started...");
    Button startBtn = new Button("Start");
    Button exitBtn = new Button("Exit");

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Add event handlders to the buttons
        startBtn.setOnAction(e -> runTask());
        exitBtn.setOnAction(e -> stage.close());

        HBox buttonBox = new HBox(5, startBtn, exitBtn);
        VBox root = new VBox(10, statusLbl, buttonBox);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("An Unresponsive UI");
        stage.show();
    }

    public void runTask() {
        for (int i = 1; i <= 10; i++) {
            try {
                String status = "Processing " + i + " of " + 10;
                statusLbl.setText(status);
                System.out.println(status);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

![|120](Pasted%20image%2020230711172331.png)

点击 `Start` 按钮启动任务，该任务耗时 10 秒，直接在 JAT 中执行，在这 10 秒内 UI 无响应。要解决该问题，必须将耗时任务移到另一个线程执行。

scene graph 更新时生成 pulse 事件。pulse 事件处理器也在 JAT 运行。在 for 循环内部，Label 的 text 属性更新 10 次，从而生成 pluse 事件。但是 scene graph 没有刷新以显示 Label 的最新 text，因为 JAT 线程忙着运行 task，没空处理 pulse 事件。

## 2. 新开线程的错误示范

我们无法改变 UI 事件在 JAT 单线程运行的模型，所以唯一的选择是不在 JAT 中执行耗时任务，而是在一个或多个后台线程中执行。

**示例：** 新开线程的错误示范

在非 JAT 线程访问 scene graph。

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BadUI extends Application {

    Label statusLbl = new Label("Not Started...");
    Button startBtn = new Button("Start");
    Button exitBtn = new Button("Exit");

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Add event handlders to the buttons
        startBtn.setOnAction(e -> startTask());
        exitBtn.setOnAction(e -> stage.close());

        HBox buttonBox = new HBox(5, startBtn, exitBtn);
        VBox root = new VBox(10, statusLbl, buttonBox);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("A Bad UI");
        stage.show();
    }

    public void startTask() {
        // Create a Runnable
        Runnable task = () -> runTask();

        // Run the task in a background thread
        Thread backgroundThread = new Thread(task);

        // Terminate the running thread if the application exits
        backgroundThread.setDaemon(true);

        // Start the thread
        backgroundThread.start();
    }

    public void runTask() {
        for (int i = 1; i <= 10; i++) {
            try {
                String status = "Processing " + i + " of " + 10;
                statusLbl.setText(status); // 更新 Label 操作必须在 JAT 线程中执行
                System.out.println(status);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

![|120](Pasted%20image%2020230711174044.png)

点击 "Start"，runTask() 中的下列语句抛出异常：

```java
statusLbl.setText(status);
```

这是因为不能从 JAT 以外的线程访问 live scene graph。

## 3. 新开线程的正确方式

`Platform` 类提供了 2 个 static 方法供其它线程修改 JavaFX UI：

```java
public static boolean isFxApplicationThread()
public static void runLater(Runnable runnable)
```

`isFxApplicationThread()` 判断当前线程是否为 JAT 线程；`runLater(Runnable runnable)` 将指定 `Runnable` 添加到 JAT 线程运行，用于在其它线程更新 UI 组件。

> [!TIP]
>
> JavaFX 的 `Platform.runLater()` 与 Swing 中的 `SwingUtilities.invokeLater()` 功能类似。

下面是前面代码的正确版本：

```java
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ResponsiveUI extends Application {

    Label statusLbl = new Label("Not Started...");
    Button startBtn = new Button("Start");
    Button exitBtn = new Button("Exit");

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Add event handlders to the buttons
        startBtn.setOnAction(e -> startTask());
        exitBtn.setOnAction(e -> stage.close());

        HBox buttonBox = new HBox(5, startBtn, exitBtn);
        VBox root = new VBox(10, statusLbl, buttonBox);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("A Responsive UI");
        stage.show();
    }

    public void startTask() {
        // Create a Runnable
        Runnable task = () -> runTask();

        // Run the task in a background thread
        Thread backgroundThread = new Thread(task);

        // Terminate the running thread if the application exits
        backgroundThread.setDaemon(true);

        // Start the thread
        backgroundThread.start();
    }

    public void runTask() {
        for (int i = 1; i <= 10; i++) {
            try {
                String status = "Processing " + i + " of " + 10;

                // Update the Label on the JavaFx Application Thread
                Platform.runLater(() -> statusLbl.setText(status));

                System.out.println(status);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

![|300](Pasted%20image%2020230711195002.png)

主要将：

```java
statusLbl.setText(status);
```

修改为：

```java
// Update the Label on the JavaFx Application Thread
Platform.runLater(() -> statusLbl.setText(status));
```

另外，设置工作线程为 Daemon 线程 `backgroundThread.setDaemon(true)`，这样先退出界面后，不会还有线程在后台执行。

## 4. 参考

- [Oracle Documentation](https://docs.oracle.com/javase/8/javafx/interoperability-tutorial/concurrency.htm)
- Learn JavaFX 8: Building User Experience and Interfaces with Java 8
