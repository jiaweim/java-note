# JavaFX 并发框架

- [JavaFX 并发框架](#javafx-并发框架)
  - [简介](#简介)
  - [Worker 接口](#worker-接口)
    - [Worker 状态](#worker-状态)
    - [Worker 属性](#worker-属性)

Last updated: 2022-11-28, 16:01
****

## 简介

JavaFX 并发框架构建在 `java.util.concurrent` 之上，单纯的用于GUI 环境，其类图如下：

![API](images/2020-05-22-16-26-41.png)

很小的一个包，包括 1 个接口，4个类和一个 enum。  

- `Worker` 接口表示需要在后台线程执行的任务，任务的状态可以在 JAT 中查看。  
- `Task`, `Service`, `ScheduledSerivice` 均实现了 Worker 接口，表示不同类型的任务。

| 类 | 说明 |
| ---- | --- |
| `Task`             | 一次性任务，不能重复执行 |
| `Service`          | 可重复执行任务           |
| `ScheduledService` | 根据指定间隔定期重复运行 |

`Worker.State` enum 表示 `Worker` 的不同状态。

`WorkerStateEvent` 表示 `Worker` 状态改变的事件，可以监听这些事件。

## Worker 接口

`Worker<V>` 接口定义了 JavaFX 并发模块执行任务需要的常用方法。

泛型参数 `V` 指定了 `Worker` 返回的结果类型，如果不返回结果，使用 `Void`。

任务执行的状态为 observable，可以发送到 JAT 和 scene graph 交流。

### Worker 状态

`Worker.State` enum 定义 `Worker` 所处的状态。在任务执行期间，Worker 的状态变化如下：

![Worker states](images/2020-05-22-16-35-25.png)

说明：

- 刚创建的 `Worker`，处于 `READY` 状态
- 开始执行前，转到 `SCHEDULED`
- 执行时，处于 `RUNNING`
- 成功执行完成，转到 `SUCCEEDED`
- 执行出错，转到 `FAILED`
- 如果任务被取消，可能从 `READY`, `SCHEDULED`, `RUNNING` 转到 `CANCELLED`

另外，可复用 `Worker` 还可能从 `CANCELLED`, `SUCCEEDED`, `FAILED` 状态转到 `READY`，如上图虚线所示。

### Worker 属性

Worker 包含 9 个只读属性，在创建 `Worker` 时可以指定这些属性，在任务执行过程中也可以更新。

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

在任务执行过程中，一般需要将任务执行的过程更新到UI。JavaFX 并发框架保证 `Worker` 属性更新到 JAT。另外，还可以添加 `Invalidation` 和 `ChangeListener` 到这些属性。
