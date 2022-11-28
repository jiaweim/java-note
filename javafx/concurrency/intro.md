# JavaFX 并发

- [JavaFX 并发](#javafx-并发)
  - [简介](#简介)
  - [参考](#参考)

Last updated: 2022-11-28, 15:50
****

## 简介

JavaFX 和 Swing、AWT 一样，专门使用一个线程处理 UI 事件，即 JavaFX Application Thread (JAT)。JavaFX 正在使用的 UI 元素只能通过 JAT 访问。`Scene` 中的 UI Node 不是线程安全的，由于不涉及同步，这种设计更快；缺点是必须从 JAT 线程访问这些 UI 元素。必须从 JAT 线程访问 UI 元素隐含了另一个限制，即 UI 事件不应该用于处理耗时任务，否则会导致 GUI 无响应。

例如，下面是一个典型无响应GUI：

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UnresponsiveUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    Label statuslbl = new Label("Not Started...");
    Button startBtn = new Button("Start");
    Button exitBtn = new Button("Exit");

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        startBtn.setOnAction(e -> runTask());
        exitBtn.setOnAction(e -> primaryStage.close());

        HBox buttonBox = new HBox(5, startBtn, exitBtn);
        VBox root = new VBox(10, statuslbl, buttonBox);
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("An Unresponsive UI");
        primaryStage.show();
    }

    private void runTask()
    {
        for (int i = 1; i <= 10; i++) {
            String status = "Processing " + i + " of " + 10;
            statuslbl.setText(status);
            System.out.println(status);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

点击 `Start` 按钮启动任务，该任务耗时 10 秒，直接在 JAT 中执行，在这 10 秒界面无响应。要解决该问题，必须将耗时任务移到另一个线程执行。

另外，如果要在其他线程修改 JavaFX UI，可以通过 `Platform` 的方法：

- `public static boolean isFxApplicationThread()`，判断当前线程是否为 JAT 线程
- `public static void runLater(Runnable runnable)`，将指定 `Runnable` 任务添加到 JAT 线程运行，用于在其它线程更新 UI 组件

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

public class ResponsiveUI extends Application
{
    Label statusLbl = new Label("Not Started...");
    Button startBtn = new Button("Start");
    Button exitBtn = new Button("Exit");

    @Override
    public void start(Stage stage) throws Exception
    {
        // Add event handlers to the buttons
        startBtn.setOnAction(e -> startTask());
        exitBtn.setOnAction(e -> stage.close());

        HBox buttonBox = new HBox(5, startBtn, exitBtn);
        VBox root = new VBox(10, statusLbl, buttonBox);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("A Responsive UI");
        stage.show();
    }

    public void startTask()
    {
        // Create a Runnable
        Runnable task = this::runTask;

        // Run the task in a background thread
        Thread backgroundThread = new Thread(task);

        // Terminate the running thread if the application exits
        backgroundThread.setDaemon(true);

        // Start the thread
        backgroundThread.start();
    }

    public void runTask()
    {
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

需要注意的是，更新 UI 组件方法 `Platform.runLater(() -> statusLbl.setText(status))`。

另外，设置工作线程为 Daemon 线程 `backgroundThread.setDaemon(true)`，这样先退出界面后，不会还有线程在后台执行。

## 参考

- [Oracle Documentation](https://docs.oracle.com/javase/8/javafx/interoperability-tutorial/concurrency.htm)
- Learn JavaFX 8: Building User Experience and Interfaces with Java 8
