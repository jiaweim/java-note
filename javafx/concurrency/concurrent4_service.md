# Service

2023-07-12, 08:58
****

## 1. 简介

`Service<V>` 同样实现了 `Worker<V>` 接口，它封装了 `Task<V>`，为 `Task<V>` 添加了启动、取消、重启等功能，从而可以复用 `Task`。

## 2. 创建 Service

`Service` 封装了 `Task`，所以创建 `Service` 先要创建 `Task`。`Service` 类包含一个 `createTask()` 方法，该方法返回 `Task`。

创建 `Service`，需要继承 `Service`，并实现 `createTask()` 方法：

```java
Service<ObservableList<Long>> service = new Service<ObservableList<Long>>() {
  @Override
  protected Task<ObservableList<Long>> createTask() {
    // Create and return a Task
    return new PrimeFinderTask();
  }
};
```

每次 service 启动或者重启时，`createTask()` 方法都会被调用。

## 3. 更新属性

`Service` 包含 `Worker` 的所有属性，另外还添加了 `java.util.concurrent.Executor` 类型的 `executor` 属性，用于运行 `Service`，如果没有指定，会创建一个守护线程运行 `Service`。

和 `Task` 类不同，`Service` 没有 `updateXxx()` 系列方法，其属性和底层的 `Task<V>` 绑定，在 `Task` 中更新的属性会同步到 `Service`。

## 4. 监听状态

`Service` 类在 `Task` 的基础上添加了 `onReady` 属性，用于监听 `Service` 状态重置为 `READY` 的事件。Task 在创建后处于 READY 状态，之后不会再回到 READY。而 Service 可能多次转到 READY，创建、重置和重启都使 Service 回到 READY。

`Service` 还有一个 `ready()` 方法，由子类实现，当 `Service` 转到 `READY` 时调用。

## 5. 取消

使用 `cancel()` 取消 `Service`，该方法将 `state` 设置为 `CANCELLED`。

## 6. 启动

使用 `start()` 启动 `Service`。该方法调用 `createTask()` 获得 `Task` 实例并执行。

调用 `Service.start()` 时 Service 必须处于 `READY` 状态。

```java
Service<ObservableList<Long>> service = create a service
...
// Start the service
service.start();
```

## 7. 重置

`reset()` 重置 `Service` 状态，将其所有属性重置为初始状态。`state` 设置为 `READY`。

`Service` 处于完成状态才允许重置，包括  `SUCCEEDED`, `FAILED`, `CANCELLED`, `READY`。如果处在 `SCHEDULED` 或 `RUNNING` 状态，调用 `reset()` 抛出错误。

## 8. 重启

`restart()` 重启 `Service`，内部依次调用 `cancel()`, `reset()` 和 `start()`。

## 9. Prime Finder Service 示例

```java
// PrimeFinderService.java
package mjw.study.javafx.concurrent;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import static javafx.concurrent.Worker.State.RUNNING;
import static javafx.concurrent.Worker.State.SCHEDULED;

public class PrimeFinderService extends Application {

    // Service 启动后，startBtn 的 text 变为 Restart
    Button startBtn = new Button("Start");
    Button cancelBtn = new Button("Cancel");
    Button resetBtn = new Button("Reset");
    Button exitBtn = new Button("Exit");
    boolean onceStarted = false;

    // service 以实例变量保存
    Service<ObservableList<Long>> service = new Service<ObservableList<Long>>() {
        @Override
        protected Task<ObservableList<Long>> createTask() {
            return new PrimeFinderTask();
        }
    };

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Add event handlders to the buttons
        addEventHandlders();

        // Enable disable buttons based on the service state
        bindButtonsState();

        GridPane pane = new WorkerStateUI(service);
        HBox buttonBox = new HBox(5, startBtn, cancelBtn, resetBtn, exitBtn);
        BorderPane root = new BorderPane();
        root.setCenter(pane);
        root.setBottom(buttonBox);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("A Prime Number Finder Service");
        stage.show();
    }

    public void addEventHandlders() {
        // Add event handlders to the buttons
        startBtn.setOnAction(e -> {
            if (onceStarted) {
                service.restart();
            } else {
                service.start();
                onceStarted = true;
                startBtn.setText("Restart");
            }
        });

        cancelBtn.setOnAction(e -> service.cancel());
        resetBtn.setOnAction(e -> service.reset());
        exitBtn.setOnAction(e -> Platform.exit());
    }

    public void bindButtonsState() {
        cancelBtn.disableProperty()
                 .bind(service.stateProperty().isNotEqualTo(RUNNING));
        resetBtn.disableProperty()
                .bind(Bindings.or(service.stateProperty().isEqualTo(RUNNING),
                        service.stateProperty().isEqualTo(SCHEDULED)));
    }
}
```

![|400](Pasted%20image%2020230712085741.png)