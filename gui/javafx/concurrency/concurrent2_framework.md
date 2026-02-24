# JavaFX 并发框架

2023-07-11, 20:43
****
## 1. 简介

JavaFX 并发框架构建在 `java.util.concurrent` 之上，单纯用于GUI 环境，其类图如下：

<img src="images/Pasted%20image%2020230711203547.png" width="350" />

<img src="images/Pasted%20image%2020230711203858.png" width="400" />

该框架包含 1 个接口， 4 个类，1 个 enum。

`Worker` 接口表示需要在后台线程执行的任务，任务的状态通过 observable 可以在 JAT 线程查看。

`Task`, `Service`, `ScheduledSerivice` 均实现了 `Worker` 接口，表示不同类型的任务。

| 类 | 说明 |
| ---- | --- |
| `Task`             | 一次性任务，不能重复执行 |
| `Service`          | 可重复执行任务           |
| `ScheduledService` | 继承 `Service`，能以指定周期重复运行 |

`Worker.State` enum 表示 `Worker` 的不同状态。

`WorkerStateEvent` 表示 `Worker` 状态改变的事件，可以监听这些事件。

## 2. Worker 接口

`Worker<V>` 接口定义了 JavaFX 并发模块执行任务需要的常用方法，表示在后台线程执行的任务。

泛型参数 `V` 指定 `Worker` 返回结果的类型，如果不返回结果，使用 `Void`。

任务的状态为 observable，可以发送到 JAT，从而在 scene graph 上显示任务状态。

### 2.1. Worker 状态

在整个生命周期中，`Worker` 会转换不同的状态。`Worker.State` enum 定义 `Worker` 的状态：

- Worker.State.READY
- Worker.State.SCHEDULED
- Worker.State.RUNNING
- Worker.State.SUCCEEDED
- Worker.State.CANCELLED
- Worker.State.FAILED

在任务执行期间，Worker 的状态变化如下：

![Worker states|450](images/2020-05-22-16-35-25.png)

说明：

- 刚创建的 `Worker`，处于 `READY` 状态
- 执行前，转到 `SCHEDULED`
- 执行时，处于 `RUNNING`
- 成功执行完成，转到 `SUCCEEDED`
- 执行出错，转到 `FAILED`
- 如果任务被取消，可能从 `READY`, `SCHEDULED`, `RUNNING` 转到 `CANCELLED`

这些是一次性任务（Task）状态转换过程。可复用 `Worker`，如 Service，还可能从 `CANCELLED`, `SUCCEEDED`, `FAILED` 状态转到 `READY`，如上图虚线所示。

### 2.2. Worker 属性

`Worker` 包含 9 个 read-only 属性用于表示任务状态：

| 属性 | 描述  |
| --- | --- |
| title | 任务标题 |
| message | 任务执行过程中的信息 |
| running | `Worker` 是否在执行，`SCHEDULED` 或 `RUNNING` 状态为 true，否则为 false   |
| state | `Worker` 状态，`Worker.State` enum 类型 |
| totalWork | 任务总量，-1 表示未知 |
| workDone  | 已完成任务数，-1 表示未知 |
| progress  | `workDone` 和 `totalWork` 的比值，-1 表示未知 |
| value     | 任务结果，`Worker` 处于 `SUCCEEDED` 状态时才可能为 non-null |
| exception | 任务执行抛出的异常，处于 `FAILED` 状态时才为 non-null |

在创建 `Worker` 时可以指定这些属性，在任务执行过程中也可以更新。

在任务执行过程中，一般需要将任务执行的过程更新到 UI。JavaFX 并发框架保证 `Worker` 属性更新到 JAT。另外，还可以添加 `Invalidation` 和 `ChangeListener` 到这些属性。

后面会讨论 Worker 接口的具体实现。

## 3. 示例工具类

下面创建几个工具类用于后续演示示例。WorkerStateUI 类构建了一个 GridPane 显示 Worker 属性：

```java
import javafx.beans.binding.When;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class WorkerStateUI extends GridPane {

    private final Label title = new Label("");
    private final Label message = new Label("");
    private final Label running = new Label("");
    private final Label state = new Label("");
    private final Label totalWork = new Label("");
    private final Label workDone = new Label("");
    private final Label progress = new Label("");
    private final TextArea value = new TextArea("");
    private final TextArea exception = new TextArea("");
    private final ProgressBar progressBar = new ProgressBar();

    public WorkerStateUI() {
        addUI();
    }

    public WorkerStateUI(Worker<ObservableList<Long>> worker) {
        addUI();
        bindToWorker(worker);
    }

    private void addUI() {
        value.setPrefColumnCount(20);
        value.setPrefRowCount(3);
        exception.setPrefColumnCount(20);
        exception.setPrefRowCount(3);
        this.setHgap(5);
        this.setVgap(5);
        addRow(0, new Label("Title:"), title);
        addRow(1, new Label("Message:"), message);
        addRow(2, new Label("Running:"), running);
        addRow(3, new Label("State:"), state);
        addRow(4, new Label("Total Work:"), totalWork);
        addRow(5, new Label("Work Done:"), workDone);
        addRow(6, new Label("Progress:"), new HBox(2, progressBar, progress));
        addRow(7, new Label("Value:"), value);
        addRow(8, new Label("Exception:"), exception);
    }

    public void bindToWorker(final Worker<ObservableList<Long>> worker) {
        // Bind Labels to the properties of the worker
        title.textProperty().bind(worker.titleProperty());
        message.textProperty().bind(worker.messageProperty());
        running.textProperty().bind(worker.runningProperty().asString());
        state.textProperty().bind(worker.stateProperty().asString());
        totalWork.textProperty().bind(new When(worker.totalWorkProperty().isEqualTo(-1))
                .then("Unknown")
                .otherwise(worker.totalWorkProperty().asString()));
        workDone.textProperty().bind(new When(worker.workDoneProperty().isEqualTo(-1))
                .then("Unknown")
                .otherwise(worker.workDoneProperty().asString()));
        progress.textProperty().bind(new When(worker.progressProperty().isEqualTo(-1))
                .then("Unknown")
                .otherwise(worker.progressProperty().multiply(100.0)
                        .asString("%.2f%%")));
        progressBar.progressProperty().bind(worker.progressProperty());
        value.textProperty().bind(worker.valueProperty().asString());

        // Display the exception message when an exception occurs in the worker
        worker.exceptionProperty().addListener((prop, oldValue, newValue) -> {
            if (newValue != null) {
                exception.setText(newValue.getMessage());
            } else {
                exception.setText("");
            }
        });
    }
}
```

PrimeUtil 类判断一个数是否为质数：

```java
public class PrimeUtil {

    public static boolean isPrime(long num) {
        if (num <= 1 || num % 2 == 0) {
            return false;
        }

        int upperDivisor = (int) Math.ceil(Math.sqrt(num));
        for (int divisor = 3; divisor <= upperDivisor; divisor += 2) {
            if (num % divisor == 0) {
                return false;
            }
        }
        return true;
    }
}
```

