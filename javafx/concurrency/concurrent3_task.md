# Task

2023-07-11, 23:33
****
## 1. 简介

`Task<V>` 表示一次性任务。任务结束、取消或者失败后，无法重启。其类图如下：

![|400](Pasted%20image%2020230711213057.png)
`Task<V>` 实现了 `Worker<V>` 接口，因此继承了 `Worker` 的属性和方法。

`Task<V>` 继承自 `FutureTask<V>` 类，该类实现了 `Future<V>`, `RunnableFuture<V>` 以及 `Runnable` 接口，因此 `Task<V>` 也实现了所有这些接口。

## 2. 创建 Task

扩展 `Task<V>` 类并实现抽象方法 `call()`。`call()` 方法包含任务逻辑。

```java
// Task, 生成 ObservableList<Long>
public class PrimeFinderTask extends Task<ObservableList<Long>> {
  @Override
  protected ObservableList<Long>> call() {
    // Implement the task logic here...
  }
}
```

## 3. 更新 Task 属性

随着任务的进行，通常需要更新任务属性。任务属性必须在 JAT 线程更新。

`Task` 提供了如下更新属性的方法:

```java
protected void updateMessage(String message)
protected void updateProgress(double workDone, double totalWork)
protected void updateProgress(long workDone, long totalWork)
protected void updateTitle(String title)
protected void updateValue(V value)
```

使用 `updateValue()` 可以提交部分结果，任务的最终结果是 `call()` 的返回值。

对 `updateProgress()`，如果 `workDone` 大于 `totalWork`，或者两者都小于 -1.0，会抛出异常。

所有的 `updateXXX()` 方法**在 JAT 线程执行**，可以在 `call()` 方法中放心调用。

如果在 `Task.call()` 中不用 `updateXXX()` 方法更新属性，则需要将更新属性代码放在 `Platform.runLater()` 中。

## 4. 监听任务进程

`Task` 包含如下几个添加监听器的方法：

- onCalcelled
- onFailed
- onRunning
- onScheduled
- onSucceeded

例如，添加 `onSucceeded` 事件监听器，在任务转到 `SUCCEEDED` 状态时执行：

```java
Task<ObservableList<Long>> task = create a task...
task.setOnSucceeded(e -> {
    System.out.println("The task finished. Let us party!")
});
```

## 5. 取消任务

`cancel()` 有两个重载方法：

```java
public final boolean cancel()
public boolean cancel(boolean mayInterruptIfRunning)
```

第一个将任务从任务队列中移除，如果任务正在执行，则终止任务；第二个表示是否中断正在执行任务的线程。

**在 `call()` 方法中**要处理 `InterruptedException`，检测到该异常，应尽快结束 `call()` 方法，否则调用 `cancel(true)` 可能无法可靠的取消任务。

`cancel()` 方法可以在任何线程调用。

当 `Task` 达到特定状态时，下面的对应方法自动调用：

- `protected void scheduled()`
- `protected void running()`
- `protected void succeeded()`
- `protected void cancelled()`
- `protected void failed()`

这些方法在 `Task` 类中是空的，由继承的子类根据需要实现。

## 6. 执行任务

`Task` 是 `Runnable`，也是 `FutureTask`。可以创建后台线程或 `ExecutorService` 执行 `Task`。

```java
// 将任务放在后台线程执行
Thread backgroundThread = new Thread(task);
backgroundThread.setDaemon(true); // 设置为守护线程，这样退出界面后，不会有后台线程继续执行
backgroundThread.start();

// Use the executor service to schedule the task
ExecutorService executor = Executors.newSingleThreadExecutor();
executor.submit(task);
```

## 7. 示例

实现 `Task<ObservableList<Long>>`，在 call() 方法中查找 lowerLimit 到 upperLimit 范围内的质数，返回包含所有质数的列表。

在每次检查质数前 sleep 0.5 秒，用于模拟耗时任务，在实际应用中不需要。

```java
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class PrimeFinderTask extends Task<ObservableList<Long>> {

    private long lowerLimit = 1;
    private long upperLimit = 30;
    private long sleepTimeInMillis = 500;

    public PrimeFinderTask() {}

    public PrimeFinderTask(long lowerLimit, long upperLimit) {
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
    }

    public PrimeFinderTask(long lowerLimit,
                           long upperLimit,
                           long sleepTimeInMillis) {
        this(lowerLimit, upperLimit);
        this.sleepTimeInMillis = sleepTimeInMillis;
    }

    @Override
    protected ObservableList<Long> call() {
        // An observable list to represent the results
        final ObservableList<Long> results = FXCollections.<Long>observableArrayList();

        // Update the title
        this.updateTitle("Prime Number Finder Task");

        long count = this.upperLimit - this.lowerLimit + 1;
        long counter = 0;

        // Find the prime numbers
        for (long i = lowerLimit; i <= upperLimit; i++) {
            // Check if the task is cancelled
            if (this.isCancelled()) {
                break;
            }

            // Increment the counter
            counter++;

            // Update message
            this.updateMessage("Checking " + i + " for a prime number");

            // Sleep for some time
            try {
                Thread.sleep(this.sleepTimeInMillis);
            } catch (InterruptedException e) {
                // Check if the task is cancelled
                if (this.isCancelled()) {
                    break;
                }
            }

            // Check if the number is a prime number
            if (PrimeUtil.isPrime(i)) {
                // Add to the list
                results.add(i);

                // Publish the read-only list to give the GUI access to the 
                // partial results
                updateValue(FXCollections.<Long>unmodifiableObservableList(results));
            }

            // Update the progress
            updateProgress(counter, count);
        }

        return results;
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        updateMessage("The task was cancelled.");
    }

    @Override
    protected void failed() {
        super.failed();
        updateMessage("The task failed.");
    }

    @Override
    public void succeeded() {
        super.succeeded();
        updateMessage("The task finished successfully.");
    }
}
```

```ad-warning
每次发现一个新的质数，都创建一个新的 unmodifiable list 来发布部分结果 `updateValue(FXCollections.<Long>unmodifiableObservableList(results))`，该方法内存效率太低，在生成环境不会使用。更有效的方式是什么一个 read-only 属性，在 JAT 中更新该 read-only 属性，客户端代码 bind 该 read-only 属性来查看部分结果。
```

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import static javafx.concurrent.Worker.State.READY;
import static javafx.concurrent.Worker.State.RUNNING;

public class OneShotTask extends Application {

    Button startBtn = new Button("Start");
    Button cancelBtn = new Button("Cancel");
    Button exitBtn = new Button("Exit");

    // Create the task
    PrimeFinderTask task = new PrimeFinderTask();

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Add event handlders to the buttons
        startBtn.setOnAction(e -> startTask());
        cancelBtn.setOnAction(e -> task.cancel());
        exitBtn.setOnAction(e -> stage.close());

        // Enable/Disable the Start and Cancel buttons
        startBtn.disableProperty().bind(task.stateProperty().isNotEqualTo(READY));
        cancelBtn.disableProperty().bind(task.stateProperty().isNotEqualTo(RUNNING));
        GridPane pane = new WorkerStateUI(task);
        HBox buttonBox = new HBox(5, startBtn, cancelBtn, exitBtn);
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
        stage.setTitle("A Prime Number Finder Task");
        stage.show();
    }

    public void startTask() {
        // Schedule the task on a background thread
        Thread backgroundThread = new Thread(task);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }
}
```

初始视图：

![|400](Pasted%20image%2020230711233211.png)

运行时视图：

![|400](Pasted%20image%2020230711233251.png)

