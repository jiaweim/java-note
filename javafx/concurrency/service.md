# Service

- [Service](#service)
  - [简介](#简介)
  - [创建 Service](#创建-service)
  - [更新属性](#更新属性)
  - [监听状态](#监听状态)
  - [取消](#取消)
  - [启动](#启动)
  - [重置](#重置)
  - [重启](#重启)

2022-04-27, 16:15
****

## 简介

`Service<V>` 同样实现了 `Worker<V>` 接口，它封装了 `Task<V>`，为 `Task<V>` 添加了启动、取消、重启等功能，从而可以复用 `Task`。

## 创建 Service

`Service` 对 `Task` 进行封装，创建 `Service` 首先要获得一个 `Task`。`Service` 类包含一个 `createTask()` 方法，该方法返回 `Task`。

创建 `Service`，需要继承 `Service`，并实现 `createTask()` 方法。例如：

```java
// Create a service
Service<ObservableList<Long>> service = new Service<ObservableList<Long>>() {
  @Override
  protected Task<ObservableList<Long>> createTask() {
    // Create and return a Task
    return new PrimeFinderTask();
  }
};
```

每次 service 启动或者重启时，`createTask()` 方法都会被调用。

## 更新属性

`Service` 包含 `Worker` 的所有属性，另外还添加了 `java.util.concurrent.Executor` 类型的 `executor` 属性，该属性用于运行 `Service`，如果没有指定，会创建一个守护线程运行 `Service`。

和 `Task` 类不同，`Service` 没有 `updateXxx()` 系列方法，其属性和底层的 `Task<V>` 绑定，在 `Task` 中更新的属性，会直接更新到 `Service`。

## 监听状态

`Service` 类在 `Task` 的基础上添加了 `onReady` 属性，用于监听 `Service` 状态重置为 `READY` 的事件。

`Service` 还有一个 `ready()` 方法，由子类具体实现，当 `Service` 状态转到 `READY` 时调用。

## 取消

使用 `cancel()` 取消 `Service`，该方法将 `state` 设置为 `CANCELLED`。

## 启动

使用 `start()` 方法启动 `Service`。该方法调用 `createTask()` 获得 `Task` 实例并执行。

在调用 `Service` 的 `start()` 方法时，必须处于 `READY` 状态。

```java
Service<ObservableList<Long>> service = create a service
...
// Start the service
service.start();
```

## 重置

`reset()` 重置 `Service` 状态，将其所有属性重置为初始状态。`state` 设置为 `READY`。

`Service` 处于完成状态才允许重置，如 `SUCCEEDED`, `FAILED`, `CANCELLED`, `READY`。如果处在 `SCHEDULED` 或 `RUNNING` 状态，调用 `reset()` 抛出错误。

## 重启

`restart()` 重启 `Service`，内部依次调用 `cancel()`, `reset()` 和 `start()`。
