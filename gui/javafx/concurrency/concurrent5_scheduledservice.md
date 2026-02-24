# ScheduledService

2023-07-12, 11:59
****
## 1. 简介

`ScheduledService<V>` 继承 `Service<V>`，所以在完成或失败后，能够重启。在 `Service<V>` 基础上添加了周期执行的功能。例如，每 10 分钟刷新一次游戏比分，每天刷新一次天气预报等。

## 2. 创建 ScheduledService

创建 ScheduledService 和创建 Service 一样：继承 ScheduledService，实现 createTask()：

```java
ScheduledService<ObservableList<Long>> service =
    new ScheduledService <ObservableList<Long>>() {
        @Override
        protected Task<ObservableList<Long>> createTask() {
            // Create and return a Task
            return new PrimeFinderTask();
        }
};
```

在 ScheduledService 启动或重启（不管手动还是自动）时，createTask() 被调用。ScheduledService 会自动重启，也可以手动调用 start() 和 restart() 启动和重启。

## 3. 更新 ScheduledService 属性

ScheduledService 除了继承 Service 的属性，还添加了以下属性：

| 属性                      | 类型                                     | 说明                                                                                                                                                                                                                                                        |
| ------------------------- | ---------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `lastValue`               | `V`                                      | service 上次执行结果                                                                                                                                                                                                                                        |
| `delay`                   | Duration                                 | service 从启动到执行的延迟。在延迟期间 service 处于 SCHEDULED 状态。仅手动调用 start() 或 restart() 启动 service 遵守延迟时间；service 自动重启是否遵循延迟取决于 service 的状态。例如，service 在计划周期中会立刻重新运行，忽略 delay 属性。delay 默认为 0 |
| `period`                  | `Duration`                               | 两次运行之间的最短时间间隔，默认为 0                                                                                                                                                                                                                        |
| `restartOnFailure`        | boolean                                  | service 失败时是否自动重启，默认 true                                                                                                                                                                                                                       |
| `currentFailureCount`     | int                                      | ScheduledService 失败次数。手动重启 ScheduledService 时重置为 0                                                                                                                                                                                             |
| `maximumFailureCount`     | int                                      | ScheduledService 在转换为 FAILED 之前可以失败的最大次数，之后不再自动重启，默认为 `Integer.MAX_VALUE`。注意：任何时候都可以手动重启 service                                                                                                                 |
| `backoffStrategy`         | `Callback<ScheduledService<?>,Duration>` | 计算每次失败后添加到 period 的 Duration                                                                                                                                                                                                                     |
| `cumulativePeriod`        | `Duration`                               | 使用 backoffStrategy 计算的当前失败运行到下次运行的时间间隔，执行成功重置该属性                                                                                                                                                                             |
| `maximumCumulativePeriod` | `Duration`                               | `cumulativePeriod` 的上限                                                                                                                                                                                                                                   |

`backoffStrategy` 计算每次失败后添加到 `period` 的 `Duration`。`Service`  执行失败后，重试前通常会暂缓一下。假设 `Service` 每 10 分钟执行一次，出现第 1 次失败，你可能希望 15 分钟后再尝试；如果再次出现失败，你可能希望 25 分钟后再尝试。一般根据 period 和当前失败次数计算添加的额外时间间隔。`ScheduledService` 内置了 3 种 backoff 策略：

- `EXPONENTIAL_BACKOFF_STRATEGY`，间隔时间和连续失败次数成指数关系
- `LINEAR_BACKOFF_STRATEGY`，间隔时间和连续失败次数成线性关系
- `LOGARITHMIC_BACKOFF_STRATEGY`，间隔时间和连续失败次数成对数关系

| 策略        | 计算公式（period=0）            | 计算公式（period≠0）                                |
| ----------- | ------------------------------- | --------------------------------------------------- |
| Exponential | Math.exp(currentFailureCount)   | period + (period * Math.exp(currentFailureCount)    |
| Linear      | currentFailureCount             | period + (period * currentFailureCount)             |
| Logarithmic | Math.log1p(currentFailureCount) | period + (period * Math.log1p(currentFailureCount)) |

## 4. 监听 ScheduledService 状态变化

ScheduledService 与 Service 的状态转变一样。成功执行时的变化：READY -> SCHEDULED -> RUNNING。

在实现监听相关方法时，主要调用 super 方法，以保证 ScheduledService 正确执行。

## 5. 示例

使用 PrimeFinderTask 创建 ScheduledService。启动后 ScheduledService 会重复执行。如果失败 5 次，转到 FAILED 状态不再运行。ScheduledService 的使用与 Service 类似，首先创建 ScheduledService：

```java
ScheduledService<ObservableList<Long>> service = 
                        new ScheduledService<ObservableList<Long>>() {
    @Override
    protected Task<ObservableList<Long>> createTask() {
        return new PrimeFinderTask();
    }
};
```


```java
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import static javafx.concurrent.Worker.State.RUNNING;
import static javafx.concurrent.Worker.State.SCHEDULED;

public class PrimeFinderScheduledService extends Application {

    Button startBtn = new Button("Start");
    Button cancelBtn = new Button("Cancel");
    Button resetBtn = new Button("Reset");
    Button exitBtn = new Button("Exit");
    boolean onceStarted = false;

    // Create the scheduled service
    ScheduledService<ObservableList<Long>> service =
            new ScheduledService<ObservableList<Long>>() {
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
        // Configure the scheduled service
        service.setDelay(Duration.seconds(5));
        service.setPeriod(Duration.seconds(30));
        service.setMaximumFailureCount(5);

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
        stage.setTitle("A Prime Number Finder Scheduled Service");
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
        cancelBtn.disableProperty().bind(service.stateProperty().isNotEqualTo(RUNNING));
        resetBtn.disableProperty().bind(
                Bindings.or(service.stateProperty().isEqualTo(RUNNING),
                        service.stateProperty().isEqualTo(SCHEDULED)));
    }
}
```

