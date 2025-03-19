# Reactor

2025-03-19 add: Scheduler, 编程创建序列 ⭐
2025-03-18 add: Reactor 核心功能 ⭐
2025-03-12 add: Reactive 编程简介
@author Jiawei Mao

***
## 1. 简介

响应式编程（reactive programming）通过声明式语法构建异步处理 pipeline，采用基于事件的模型将数据推送到 consumer，围绕异步和非阻塞建立，比 JDK 有限的基于 callback 的 API 和 `Future` 更强大。

Reactor 是 JVM 非阻塞 reactive 编程基础，具有高效的需求管理。它直接与 Java 8 函数式 API 集成，尤其是 `CompletableFuture`, `Stream` 和 `Duration`。它提供了可组合的异步序列 API：`Flux` (用于  N 个元素)和 `Mono` (用于 0 或 1 个元素)，以广泛实现 [Reactive Streams](https://www.reactive-streams.org/) 规范。

Reactive Streams 规范是行业驱动的结果，旨在标准化 JVM 的响应式编程库。已有库包括 Reactor 3、RxJava、Akka Streams、Vert.x 和 Ratpack。

Reactor 还通过 reactor-netty 项目支持进程间的非阻塞通讯。reactor-netty 适合微服务架构，为 HTTP、TCP 和 UDP 提供 backpressure-ready 网络引擎。

**基本要求**：Reactor 需要 Java 8+。

> [!TIP]
>
> **反压（backpressure）**
>
> 数据流从上游生产者到下游消费者传输过程中，上游生产速度大于下游消费速度，导致下游的 buffer 溢出，这种现象称为出现了 backpressure。
>
> 反压功能的实现指下游消费者能够告诉生产者需要多少数据以防止反压的形成。

### BOM 和版本

Reactor 3 使用 BOM（Bill of Materials）模型（since reactor-core 3.0.4）。

artifacts 采用版本方案为 `AJOR.MINOR.PATCH-QUALIFIER`，而 BOM 采用 `YYYY.MINOR.PATCH-QUALIFIER`，其中：

- `MAJOR` 为主版本，不同主版本之间有重大变化，可能需要进行迁移；
- `YYYY` 指发行周期的第一个 GA 发行的年份
- `.MINOR` 是从 0 开始的数字，每次增加表示一个新的发行周期：
  - 对项目而言，表示很多变化，有少量的迁移工作
  - 对 BOM 而言，如果有两个 BOM 在同一年首次发布，它用于辨别不同的发布周期
- `.PATCH` 是从 0 开始的数字，用于表示服务周期
- `-QUALIFIER` 是文本识别符，在 GA 版本中忽略

因此，按照该约定第一个发行周期是 `2020.0.x`，名称 `Europium`。采用如下识别符：

- `-M1`..`-M9`：里程碑，一个服务周期不超过 9
- `-RC1`..`-RC9`：发布候选版本，每个服务周期不超过 9
- `-SNAPSHOT`：快照

> [!NOTE]
>
> 快照（snapshot）在上面顺序最高，因为它们通常是每个 `PATCH` 最先预发行版本。

### 安装 Reactor

使用 reactor 的最简单方式是使用 BOM，并将相关依赖项添加到项目中。

- Maven

首先在 pom.xml 中导入 BOM

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.projectreactor</groupId>
            <artifactId>reactor-bom</artifactId>
            <version>2024.0.3</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

然后根据需要添加依赖项，只是不需要指定 `<version>`：

```xml
<dependencies>
    <dependency>
        <groupId>io.projectreactor</groupId>
        <artifactId>reactor-core</artifactId>

    </dependency>
    <dependency>
        <groupId>io.projectreactor</groupId>
        <artifactId>reactor-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 2. Publish Subscribe 模式

问题：多个具有依赖关系的并发执行路径，还需要共享数据。同步是最基本的问题，解决同步有许多方案，publish-subscibe 模式追求的是 1:N 关系。

该模式有两个对象：一个 **publisher**，负责生产数据；许多 **subscriber**，负责消费数据

- subscriber 可以订阅 publisher，表示对其数据感兴趣
- publisher 在准备好数据时，可以通知所有 subscribers

与其它同步机制相比，这种模式无需等待，并基于推送（push）机制进行交流。subscriber 不需要刻意等待数据到达，只需提前定义好数据到达时要执行的操作。反之亦然，publisher 也不用考虑 subsciber 是否准备好接收数据，直接推送给他即可。

<img src="./images/fileservlet.png" alt="Publisher and Subscriber" style="zoom:50%;" />

Reactor 还提供了 operator 的概念：

- operator 用于处理数据
- 将多个 operator 串联实现对数据复杂分析
- operator 返回中间 `Publisher`，中间 `Publisher` 可以看作上游 `Publisher` 的 `Subscriber`，下游的 `Publisher`
- 处理好的数据发送给最后的 `Subscriber` 

### Publisher

#### 生命周期

Reactor 中有两个 publisher 实现：`Flux` 和 `Mono`。从数据源创建 publisher 后，可以添加 operator-chain 处理数据，最后传递给 subscriber。因此，publisher 的生命周期有三个阶段：

- 组装
- 订阅
- 执行

在组装阶段，创建 publisher 并定义 operator-chain。然后 subscriber 订阅 publisher，接着执行。

**`subscribe()` 之前无计算**

在 `subscribe()` 之前只是定义数据分析流程，没有执行任何实际操作。最后调用 `subscribe()` 或 `block()` 进行订阅，执行才开始。执行结束时，`subscriber` 接收结果。

#### 后台

publisher 为 immutable。当使用 operator 时，创建并返回新的 immutable publisher。operator-publisher 组合订阅原始 publisher。因此，当在 `Mono` 上定义一系列 operators 时，对每个 operator，创建一个新的 publisher，该 publisher 订阅上一个 publisher，并 publish 到下一个 publisher。

例如，每个 operator 调用后都注释了返回类型：

```java
Mono.just("text") // MonoJust.class
    .map(String::length) // MonoMap.class
    .filter(this::isOdd) //MonoFilter.class
    .subscribe(log::info); //LambdaMonoSubscriber.class
```

注意：订阅时执行仅适用于显式订阅，不适用于内部订阅。因此仅在调用 `subscribe()` 或 `block()` 才执行。

#### hot/cold publisher

有两种 publisher 类型，可以简单描述为：

- hot：已经准备好数据，如 `Mono.just("data")`
- cold：没有准备好数据，需要 request，如 http 客户端请求

**hot publisher**

hot-publisher 是 `subscribe` 前没有任何发生规则的唯一例外，因为数据已经准备好。

**code publisher**

code-publisher 与 Reactor 声明周期描述的那样，订阅后出发执行，数据准备就绪时 push 到 subscriber。

需要注意的是，code-publisher 会为每个 subscriber 启动该过程。因此，如果两个 subscriber 连接到一个 `Mono`执行 http 请求，会得到 2 个 http 请求，即 1:1 关系。也可以使用 `share` 或 `publish` operator 将其转换为 1:n 关系，将数据传送到所有 subscriber，无需出发单独的调用。如果某个 subscriber 来晚了，在数据发送后才 subscribe，则会触发对 source 的调用。

有许多操作可以将 code-publisher 转换为 hot-publisher，如 `cache()`。

## 3. Reactive 编程简介

Reactor 实现 reactive 编程范式，可以概括为：

> [!NOTE]
>
> reactive 编程是与数据流和变化传播相关的异步编程范式。使用它可以轻松地表达静态或动态数据流。
>
> https://en.wikipedia.org/wiki/Reactive_programming

微软首先在 .NET 中创建了 reactive 扩展库（Rx）。然后 RxJava 在 JVM 中实现 reactive 编程。随着时间推移，Reactive Streams 定义了 JVM 上 reactive 库的一组接口和交互规范。其接口已经集成到 Java 9 的 `Flow` 类。

reactive 编程范式在面向对象语言中通常以 Observer 设计模式实现。可以将 reactive 流模式与迭代器设计模式进行比较，一个主要区别是：迭代器基于 pull，reactive 流基于 push。

迭代器是基于命令的编程模式，由开发人员选择何时访问序列的下一个元素。在 reactive-stream 中，对应 `Publisher`-`Subscriber`。`Publisher` 通知 `Subscriber` 有新元素，这是 reactive 的关键。同样，应用于 push 值的操作也是声明式的，而不是命令式：程序员描述计算的逻辑，而不是描述确切的控制流。

`Publisher` 通过调用 `onNext` 向 `Subscriber` 推送新的值，也可以调用 `onErro` 发送错误，或调用 `onComplete` 表示已完成。错误和完成都终止序列。可以总结为：

```
onNext x 0..N [onError | onComplete]
```

这种方法非常灵活。该模式支持没有值、1 个值或 n 个值。

但是，为什么需要这种异步 reactive 库？

### 阻塞是一种浪费

从广义上讲，改善程序性能的方法有两种：

- 并行（parallelize）：使用更多线程和硬件资源
- 效率（efficiency）：在当前资源的使用上寻求更大的效率

Java 开发人员通常使用阻塞代码编程。这种实践很好，直到有性能瓶颈。然后就需要引入其它线程，运行类似的阻塞代码。这种方式很容易引入数据争用等并发问题。

更重要的是，阻塞会浪费资源。一旦程序涉及延时操作，特别是 I/O、数据库请求或网络调用，就会浪费资源，因为线程会闲置下来等待数据。

因此，并行不是万能药，有必要获得硬件的全部能力。

### 异步

编写异步代码，而非阻塞代码，可以让程序切换到另一个使用相同基础资源的任务，然后在异步处理完成后返回当前任务。

但是，在 JVM 上如何编写异步代码？Java 提供了两种异步编程模式：

- **Callbacks**: 异步方法没有返回值，而是在结果可用时采用额外的 `callback` 参数。如 Swing 的 `EventListener`
- `Future`：异步方法直接返回 `Future<T>`。异步过程计算 `T` 值，`Future` 对象包含对其访问。该值无法立即可用，可以进行轮询直到可用为止。例如，`ExecutorService` 使用 `Future` 对象运行 `Callable<T>` 任务。

两种方法各有局限性。

Callbacks 很难聚合，很容易导致难以阅读和维护的代码，称为 "callback hell"。

例如：在 UI 显示用户的 top-5 favorites，如果没有则显示建议。这涉及 3 个服务：一个给出 favorite IDs, 一个获取 favorite 详细信息，一个提供建议的详细信息。如下：

```java
userService.getFavorites(userId, new Callback<List<String>>() { // ①
  public void onSuccess(List<String> list) { // ②
    if (list.isEmpty()) { // ③
      suggestionService.getSuggestions(new Callback<List<Favorite>>() {
        public void onSuccess(List<Favorite> list) { // ④
          UiUtils.submitOnUiThread(() -> { // ⑤
            list.stream()
                .limit(5)
                .forEach(uiList::show); // ⑥
            });
        }

        public void onError(Throwable error) { // ⑦
          UiUtils.errorPopup(error);
        }
      });
    } else {
      list.stream() // ⑧
          .limit(5)
          .forEach(favId -> favoriteService.getDetails(favId, // ⑨
            new Callback<Favorite>() {
              public void onSuccess(Favorite details) {
                UiUtils.submitOnUiThread(() -> uiList.show(details));
              }

              public void onError(Throwable error) {
                UiUtils.errorPopup(error);
              }
            }
          ));
    }
  }

  public void onError(Throwable error) {
    UiUtils.errorPopup(error);
  }
});
```

1. 使用基于 callback 对的服务：`Callback` 接口，当异步过程成功时调用 `onSuccess`，失败时调用 `onError`
2. 第一个服务使用 favorite ID list 调用其 callback
3. 如果 list 为空，转到 `suggestionService`
4. `suggestionService` 将 `List<Favorite>` 发给第一个 callback
5. 由于是处理 UI，所以要确保在 UI 线程上运行
6. 使用 Java 8 `Stream` 将 suggestions 限制到 5，并在 UI 上显示
7. 弹窗显示错误信息
8. 回到 favorite ID，如果服务返回完整 list，需要转到 `favoriteService` 获取详细的 `Favorite` 对象。只需要 5 个。
9. 再次 callback。这次获得了 5 个 `Favorite` 对象，并在 UI 线程显示。

这么多代码，并且具有重复性的内容。下面是等价的 reactor 实现：

```java
userService.getFavorites(userId) // ①
           .flatMap(favoriteService::getDetails) // ②
           .switchIfEmpty(suggestionService.getSuggestions()) // ③
           .take(5) // ④
           .publishOn(UiUtils.uiThreadScheduler()) // ⑤
           .subscribe(uiList::show, UiUtils::errorPopup); // ⑥
```

1. 从 favorite ID flow 开始
2. 异步将它们转换为 `Favorite` 对象 (`flatMap`)，得到 `Favorite` flow
3. 如果 `Favorite` flow 是空的，则通过 `suggestionService` 切换
4. 获取最多 5 个结果
5. 再 UI 线程依次处理数据
6. 描述数据的最终处理方式，成功则在 UI 列表显示，错误则弹窗显示

如果要确保检索 favorite ID 的时间少于 800 ms，或者（如果需要更长时间）从缓存中获取？在基于 callback 的代码中，这是一个复杂的任务。在 Reactor 中通过 `timeout` 操作符很容易实现：

```java
userService.getFavorites(userId)
           .timeout(Duration.ofMillis(800)) // ①
           .onErrorResume(cacheService.cachedFavoritesFor(userId)) // ②
           .flatMap(favoriteService::getDetails) // ③
           .switchIfEmpty(suggestionService.getSuggestions())
           .take(5)
           .publishOn(UiUtils.uiThreadScheduler())
           .subscribe(uiList::show, UiUtils::errorPopup);
```

1. 如果检索时间超过 800 ms，发出错误
2. 对错误，转到 `cacheService`
3. 余下和上例类似

`Future` 对象比 callback 好点，但是即使有 java 8 引入的 `CompletableFuture`，在组合方面依然表现不佳。将多个 `Future` 对象组合起来是可行的，但是并不容易。另外，`Future` 还有其他问题：

- 调用 `get()` 方法，很容易碰到另一种阻塞情况
- 不支持 lazy 计算
- 不支持多个值和高级错误处理

再举个例子：获得一个 ID list，希望获取名称和统计数据，并配对组合在一起，所有这些异步。下面使用 `CompletableFuture` 实现：

```java
CompletableFuture<List<String>> ids = ifhIds(); // ①

CompletableFuture<List<String>> result = ids.thenComposeAsync(l -> { // ②
	Stream<CompletableFuture<String>> zip =
			l.stream().map(i -> { // ③
				CompletableFuture<String> nameTask = ifhName(i); // ④
				CompletableFuture<Integer> statTask = ifhStat(i); // ⑤

				return nameTask.thenCombineAsync(statTask, (name, stat) -> "Name " + name + " has stats " + stat); // ⑥
			});
	List<CompletableFuture<String>> combinationList = zip.collect(Collectors.toList());// ⑦
	CompletableFuture<String>[] combinationArray = combinationList.toArray(new CompletableFuture[combinationList.size()]);

	CompletableFuture<Void> allDone = CompletableFuture.allOf(combinationArray); // ⑧
	return allDone.thenApply(v -> combinationList.stream()
			.map(CompletableFuture::join) // ⑨
			.collect(Collectors.toList()));
});

List<String> results = result.join(); // ⑩
assertThat(results).contains(
		"Name NameJoe has stats 103",
		"Name NameBart has stats 104",
		"Name NameHenry has stats 105",
		"Name NameNicole has stats 106",
		"Name NameABSLAJNFOAJNFOANFANSF has stats 121");
```

1. 从输出 id-list 的 `Future` 开始
2. 得到 list 后，进行更深入的异步处理
3. 对 list 的每个元素
4. 异步获取关联名称
5. 异步获取统计量
6. 合并两个结果
7. 此时已有代表所有组合任务的 `Future` list，为了执行这些任务，将 list 转换为数组
8. 将数组传递给 `CompletableFuture.allOf`，当所有任务完成，它输出一个完成的 `Future`
9. 比较棘手的是 `allOf` 返回 `CompletableFuture<Void>`，所以这里重新迭代 future-list，使用 `join()`收集结果（这里不会阻塞，因为 `allOf` 确保所有 future 完成）
10. 当所有异步 pipeline 被触发，等待它被处理比国内返回，就可以断言结果。

由于 Reactor 包含许多开箱即用的组合操作符，所以可以简化该过程：

```java
Flux<String> ids = ifhrIds(); // ①

Flux<String> combinations =
		ids.flatMap(id -> { // ②
			Mono<String> nameTask = ifhrName(id); // ③
			Mono<Integer> statTask = ifhrStat(id); // ④

			return nameTask.zipWith(statTask, // ⑤
					(name, stat) -> "Name " + name + " has stats " + stat);
		});

Mono<List<String>> result = combinations.collectList(); // ⑥

List<String> results = result.block();// ⑦
assertThat(results).containsExactly( // ⑧
		"Name NameJoe has stats 103",
		"Name NameBart has stats 104",
		"Name NameHenry has stats 105",
		"Name NameNicole has stats 106",
		"Name NameABSLAJNFOAJNFOANFANSF has stats 121"
);
```

1. 这次，从异步提供的 id 序列 `Flux<String>` 开始
2. 对序列的每个元素异步处理两次（`flatMap` 内部）
3. 获取关联名称
4. 获取关联统计量
5. 异步组合这两个值
6. 当值可用时，将其聚合到 list 中
7. 在生产中，可以继续异步使用 `Flux`，进一步组合或订阅。最有可能的是返回 `Mono` 结果。由于这里处于测试阶段，因此改为阻塞，等待处理完成，直接返回聚合的结果。
8. 断言结果

使用 callback 和 `Future` 的风险是类似的，使用 `Publisher-Subscriber` 的 reactive 编程就是为了解决该问题。

### 从命令式编程到响应式编程

reactive 库（如 Reactor）旨在解决 JVM 中经典异步方法的缺点，同时还关注其它方面：

- 可组合性和可读性
- 数据作为流处理，由丰富的运算符进行操作
- 在订阅前什么都不会发生
- 反压或消费者向生产者发出信号表示生产过快的能力
- 高级但高价值的抽象，与并发无关

#### 可组合性和可读性

可组合性（composability）指能够排列多个异步任务，使前面的任务作为后续任务的输入。或者，可以使用 fork-join 的方式运行多个任务。此外，可以在高级系统中将异步任务作为离散组件重用。

排列任务的能力与代码的可读性和可维护性紧密相关。随着异步进程层数和复杂性的增加，编写和阅读代码变得越来越困难。callback 模型很简单，它的主要缺点是，对复杂的任务，需要从 callback 中调用 callback，而 callback 本身又嵌套在另一个 callback 中，依此类推，这种混乱称为 callback-hell。这样的代码很难回溯和推理。

Reactor 提供了丰富的组合选项，代码反映了抽象过程的排列，并且所有内容通常保持在同一级别（嵌套最小化）。

#### 类比装配线

可以将 reactive 处理数据的过程类比为装配线。原材料从原始 `Publisher` 触发，最终成为成品推动给消费者 `Subscriber`。

原材料可以经过各种转化和其它中间步骤，也可能是为一个大型装配线组装中间零件。如果某一点出现故障或堵塞，受影响的工作者可以向上游发出信号以限制原材料的流动。

#### 操作符

在 Reactor 中，操作符就是装配线中的工作站。每个操作符都会向 `Publisher` 添加行为，并将上一步的 `Publisher` 包装到新的实例。整个 chain 就这样串联起来，数据从第一个 `Publisher` 出发，沿着chain 移动，并被每个 link 进行转换。最终，`Subscriber` 完成整个过程。需要注意，在 `Subscriber` 订阅之前不会执行任何操作。

虽然 Reactive Streams 规范没有指定操作符，但 Reactor 等 reactive 库最大的价值之一就是提供了丰富的操作符。这些操作符覆盖很多方面，从简单的转换到过滤到复杂的排列和错误处理等。

#### subscribe 前不执行

在 Reactor 中，在编写 `Publisher` chain 时，默认一开始不会注入数据。通过订阅操作，将 `Publisher` 和 `Subscriber` 绑定，从而触发整个 chain 的数据流。这是 `Subscriber` 内部发出的 `request` 信号实现，该信号向上传播，直到最初的 `Publisher`。

#### Backpressure

向上传播信号也用于实现反压。

Reactive Streams 规范定义的机制与此机制非常接近：订阅者可以在无界模式下工作，让 source 以最快的速度推送所有数据；或者使用 request 机制向 source 发出信号，表示它只能处理最多 n 个元素。

中间操作符也可以在传输过程中更改请求。例如一个缓冲区操作符将元素分为 10 个一组，如果订阅者请求一个缓冲区，则 source 可以生成 10 个元素。有些操作符还实现了预取策略，从而避免 `request(1)` 往返，如果在请求之前生成元素的成本不高，`request(1)` 是有益的。

这将 push 模型转变为 push-pull 混合模型，下游可以从上游拉取 n 个 元素。但如果这些元素还未准备好，上游会在每次生产时 push  元素。

#### Hot vs Cold

Rx 系列 reactive 库将 reactive 序列分为两类：hot 和 cold。主要区别在于 reactive 流如何响应订阅者：

- **Cold**: cold 序列为每个 `Subscriber` 重新开始，包含数据源。例如，如果 source 包装一个 HTTP 调用，则每个订阅都回发出新的 HTTP 请求。
- **Hot**: hot 序列不会为每个 `Subscriber` 从头开始。后期订阅者会订阅后会收到之前发出的信号。注意，有些 hot reactive stream 可以全部或部分缓存/重新发射。从一般角度看，hot 序列甚至可以在没有订阅者时发出信号。

## 4. Reactor 核心功能

Reactor 项目的主要 artifact 为 reactor-core，这是一个实现 Reactive Streams 规范基于 Java 8 实现的 reactive 库。

Reactor 引入了可组合的 reactive 类型，这些类型实现 `Publisher`，同时提供了丰富的操作符，`Flux` 和 `Mono`：

- `Flux` 对象表示 0 到 N 个元素的 reactive 序列
- `Mono` 对象表示 0 或 1 个元素

这种区别带入一些语义信息，指示异步处理的大致基数。例如，一个 HTTP 请求仅生成一个响应，因此执行 `count` 操作没有意义。因此，将此类 HTTP 调用的结果表示为 `Mono<HttpResponse>` 比表示为 `Flux<HttpResponse>` 更有意义，`Mono` 仅提供与 0 或1 项元素相关的操作符。

更改基数的操作符会切换到相应类型。例如，如果 `Flux` 中存在 `count` 运算符，它会返回 `Mono<Long>`。

### Flux 

下图展示 `Flux` 如何转换元素：

<img src="./images/image-20250312191401497.png" alt="image-20250312191401497" style="zoom: 50%;" />

`Flux<T>` 是标准的 `Publisher<T>`，表示一个异步序列，可以生成 0 到 N 个元素，然后完成（成功或失败）。与 Reactive Streams 规范一样，这三种类型的信号会转换为对下游订阅者的 `onNext`, `onComplete` 和 `onError` 方法的调用。

`Flux` 的信号范围很大，是一种通用的 reactive 类型。所有事件（包括终止事件）都是可选的：

- 没有 `onNext` 但有 `onComplete` 事件代表一个空序列，删除 `onComplete` 则得到一个无限空序列（除了测试取消操作，没有其它 用处）
- `Flux.interval(Duration)` 生成一个无限的 `Flux<Long>`，从时钟发出规则的 ticks

API 说明：

- 采用 `Flux` 的 factory 从 sources 创建 `Flux`，或者从几种 callback 类型生成 `Flux`
- `Flux` 的实例方法，即 operators，可用来构建异步处理 pipeline，以生成异步序列
- 每个`Flux#subscribe()` 或多播操作，如 `Flux#publish` 和 `Flux#publishNext`， 会将 pipeline 具体化（materialize），触发数据流

#### 创建 Flux

> [!TIP]
>
> 如果想知道 `Flux` 或 `Mono` 内部情况，可以在返回前调用 `.log()` 输出日志信息。

- 创建空的 `Flux`

空 `Flux`，不生成任何元素，直接完成：

```java
public static <T> Flux<T> empty()
```

- 使用已有数据创建 `Flux`

生成提供的数据后，`Flux` 完成：

```java
public static <T> Flux<T> just(T... data);
public static <T> Flux<T> just(T data);
```

例如：

```java
Flux.just("foo", "bar");
```

- 使用集合创建 `Flux`

```java
public static <T> Flux<T> fromIterable(java.lang.Iterable<? extends T> it);
```

例如：

```java
Flux<String> flux = Flux.fromIterable(Arrays.asList("foo", "bar"));
```

- 异常处理

在命令式同步代码中， 可以很容易采用 `try-catch` 和 `throws` 处理异常。

在异步代码中，处理方式稍有不同。Reactive Streams 定义 `onError` 信号来处理异常。不过这是一个 terminal 事件，即 `Flux` 生成的最后一个事件。

`Flux#error` 升成一个 `Flux`，该 `Flux` 只升成 `onError` 信号，并立即终止。

```java
public static <T> Flux<T> error(java.lang.Throwable error);
public static <T> Flux<T> error(java.util.function.Supplier<? extends 	
                                java.lang.Throwable> errorSupplier);
public static <O> Flux<O> error(java.lang.Throwable throwable,
                                boolean whenRequested)
```

例如：

```java
Flux<String> flux = Flux.error(new IllegalStateException());
```

- 更复杂的示例

可以使用 `interval` 创建一个以固定速度生成 10 个元素的 `Flux`。但是 `interval` 生成的是无限流（类似时钟）。

下面创建一个 `Flux<Long>`，从 0 开始，然后以固定速度 `period` 递增。

1. `period` 指定时间间隔；
2. `delay` 初始延迟时间；
3. `timer` 执行计时器，默认为 global timer。

```java
public static Flux<java.lang.Long> interval(java.time.Duration period);
public static Flux<java.lang.Long> interval(java.time.Duration delay,
                                            java.time.Duration period);
public static Flux<java.lang.Long> interval(java.time.Duration period,
                                            Scheduler timer);
public static Flux<java.lang.Long> interval(java.time.Duration delay,
                                            java.time.Duration period,
                                            Scheduler timer);
```

<img src="./images/image-20250318104423187.png" alt="image-20250318104423187" style="zoom:50%;" />

#### take

- 从 `Flux` 获取前 n 个值。如果 n 为 0，则在订阅后 operator 立即完成。

```java
public final Flux<T> take(long n);
```

该 operator 可以确保上游 request 上限为 n，但如果下游的 request 小于 n，也可以生成更少的元素。

- 在指定时间内从 `Flux` 生成值，如果 `timespan` 为 0，则生成 1 个值后立刻停止

```java
public final Flux<T> take(java.time.Duration timespan);
```

- 

```java
public final Flux<T> take(java.time.Duration timespan,
                          Scheduler timer);
```



### Mono

下图展示 `Mono` 如何转换一个元素：

<img src="./images/image-20250313091254356.png" alt="image-20250313091254356" style="zoom: 50%;" />

`Mono<T>` 是 一个 `Publisher<T>` 实现，它通过 `onNext` 最多生成一个元素，然后以 `onComplete` 信号终止（成功的 `Mono`），失败则仅发射一个 `onError` 信号。

大多数 `Mono` 实现在调用 `onNext` 后立即在 `Subscriber` 上调用 `onComplete`。`Mono.never()` 例外：它不发出任何信号，这在技术上是可行的，不过仅在测试中有用。另外，`onNext` 和 `onError` 的组合是明确禁止的。

`Mono` 仅提供 `Flux` 的部分操作符，并且某些操作符，尤其是将 `Mono` 与另一个 `Publisher` 组合起来的操作符会切换到 `Flux`。例如，`Mono#concatWith(Publisher)` 返回 `Flux`，而 `Mono#then(Mono)` 返回另一个 `Mono`。

对无返回值，类似 `Runnable` 的任务，可以用 `Mono<Void>` 表示。

#### 创建 Mono

- 创建空的  `Mono`

```java
public static <T> Mono<T> empty();
```

例如：

```java
Mono<String> mono = Mono.empty();
```

- 不发出任何信号

`Mono.empty()` 好歹会发出 `onCommplete` 信号，`never()` 不发出任何信号。本质上无限期运行。

```java
public static <T> Mono<T> never();
```

例如：

```java
Mono<String> mono = Mono.never();
```

- 生成一个值

```java
public static <T> Mono<T> just(T data);
```

例如：

```java
Mono<String> mono = Mono.just("foo");
```

- 发出异常信号

在订阅后立即因指定错误而终止。

```java
public static <T> Mono<T> error(java.lang.Throwable error);
public static <T> Mono<T> error(java.util.function.Supplier<? extends 
                                java.lang.Throwable> errorSupplier);
```

### 创建 Flux 或 Mono 并订阅

使用 `Flux` 和 `Mono` 的最简单方法是使用它们各自的**工厂方法**。

例如，创建一个 `String` 序列，可以枚举它们，或者将它们放在一个集合中并创建 `Flux`：

```java
Flux<String> seq1 = Flux.just("foo", "bar", "foobar"); // 直接枚举

List<String> iterable = Arrays.asList("foo", "bar", "foobar");
Flux<String> seq2 = Flux.fromIterable(iterable);// 通过集合
```

其它工厂方法：

```java
Mono<String> noData = Mono.empty(); // ①

Mono<String> data = Mono.just("foo");

Flux<Integer> numbersFromFiveToSeven = Flux.range(5, 3); // ②
```

1. 工厂方法也支持泛型
2. 第一个参数为范围开始，第二个参数是要生成的元素个数

在订阅方面，`Flux` 和 `Mono` 使用 Java 8 lambda。`.subscribe()` 有许多版本，通过 lambda 来实现不同的 callback 组合，如下所示：

```java
subscribe(); // ➊

subscribe(Consumer<? super T> consumer); // ➋

subscribe(Consumer<? super T> consumer,
          Consumer<? super Throwable> errorConsumer); // ➌

subscribe(Consumer<? super T> consumer,
          Consumer<? super Throwable> errorConsumer,
          Runnable completeConsumer); // ➍

subscribe(Consumer<? super T> consumer,
          Consumer<? super Throwable> errorConsumer,
          Runnable completeConsumer,
          Consumer<? super Subscription> subscriptionConsumer); // ➎
```

1. 订阅并触发序列
2. 对生成的每个值执行某些操作
3. 处理值，同时处理错误
4. 处理值和错误，并在序列成功完成时运行一些代码
5. 处理值和错误以及成功完成时运行一些代码，同时对此订阅调用产生的订阅执行某些操作

> [!TIP]
>
> 这些不同版本均返回对订阅的引用，当不需要更多数据时，可以使用该引用取消订阅。取消后，source 应停止生成值并清理其创建的任何资源。这种取消和清理行为在 Reactor 由通用的 `Disposable` 接口表示。

示例：收集 emit 的每个元素，不处理错误，完成时将 `serviceCallCompleted` 设置为 true

```java
AtomicReference<Boolean> serviceCallCompleted = new AtomicReference<>(false);
CopyOnWriteArrayList<String> companyList = new CopyOnWriteArrayList<>();

fortuneTop5()
        .subscribe(companyList::add,e->{},()->serviceCallCompleted.set(true));

Thread.sleep(1000);

assertTrue(serviceCallCompleted.get());
assertEquals(Arrays.asList("Walmart", "Amazon", "Apple", "CVS Health", "UnitedHealth Group"), companyList);
```



#### subscribe 示例

下面介绍 5 个版本的 `subscribe` 的最简单示例。

- 以下为不带参数的示例：

```java
Flux<Integer> ints = Flux.range(1, 3); // ①
ints.subscribe(); // ②
```

1. 设置 `Flux`，当天还订阅者时，生成 3 个值
2. 最简单的订阅方式

上述代码产生以下输出：

```
1
2
3
```

- 为了演示下一个版本，我们故意引入一个错误：

```java
Flux<Integer> ints = Flux.range(1, 4) // ①
      .map(i -> { // ②
        if (i <= 3) return i; // ③
        throw new RuntimeException("Got to 4"); // ④
      });
ints.subscribe(i -> System.out.println(i), // ⑤
      error -> System.err.println("Error: " + error));
```

1. 设置 `Flux`，生成 4 个值
2. 通过 `map`，对不同值以不同方式处理
3. 对大多数值，返回该值
4. 对 4，则抛出错误
5. 使用包含错误处理程序的 subscriber 订阅

现在有 2 个 lambda 表达式：一个用于期望的输出，一个 用于错误。上述代码输出：

```
1
2
3
Error: java.lang.RuntimeException: Got to 4
```

- 下一个 `subscribe` 包含错误处理和完成事件处理

```java
Flux<Integer> ints = Flux.range(1, 4); // ①
ints.subscribe(i -> System.out.println(i),
    error -> System.err.println("Error " + error),
    () -> System.out.println("Done")); // ②
```

1. 设置 `Flux`，生成 4 个值
2. 使用包含完成事件处理程序的 `Subscriber` 进行订阅

错误信号和完成信号都是终止事件，并且彼此独立（不可能同时获得两者）。为了使事件完成 consumer 工作，必须注意不要触发错误。

完成 callback 没有输入，用一对空括号表示：与 `Runnable` 接口的 `run` 方法对应。上述代码的输出：

```
1
2
3
4
Done
```

#### 取消 subscribe

所有这些基于 lambda 的 `subscribe()` 方法都返回 `Disposable` 类型。`Disposable` 接口表示可以通过调用 `dispose()` 方法来取消订阅。

对 `Flux` 或 `Mono`，取消是让 source 停止生成元素的信号。但是，不能保证立即停止，某些 source 特别快，在收到取消指令之前就已经完成。

在 `Disposables` 类中有一些针对 `Disposable` 的辅助工具。其中，`Disposables.swap()` 创建一个 `Disposable` wrapper，可以 atomically 取消并替换为另一个 `Disposable`。例如，在 UI 中，当用户点击按钮，可以通过它取消 request 并替换为其它操作。

另外还有 `Disposables.composite(...)`。它可以收集多个 `Disposable`，例如，多个与一个 service-call 相关的 in-flight request，并随后全部取消。一旦调用 composite 的 `dispose()` 方法，后面添加的 `Disposable` 都会被立即取消。

#### lambda 的替代方案：BaseSubscriber

还有一种更通用的 `subscribe` 方法，需要更成熟的 `Subscriber`，而不是 lambda 的组合。为了辅助编写这种 `Subscriber`，Reactor 提供了可扩展类 `BaseSubscriber`。

> [!WARNING]
>
> `BaseSubscriber` 及其子类的实例是一次性的，因此，如果使用 `BaseSubscriber` 订阅第二个 `Publisher`，则会取消对第一个 `Publisher` 的订阅。这是因为使用相同实例两次会违反 Reactive Streams `onNext` 不能不行调用的规范。因此，匿名实现只有在 `Publisher#subscribe(Subscriber)`直接调用才行。

现在假设实现了一个 `BaseSubscriber`，称其为 `SampleSubscriber`。下面演示如何用它订阅 `Flux`：

```java
SampleSubscriber<Integer> ss = new SampleSubscriber<Integer>();
Flux<Integer> ints = Flux.range(1, 4);
ints.subscribe(ss);
```

下面为  `SampleSubscriber`，是 `BaseSubscriber` 的简单实现：

```java
import org.reactivestreams.Subscription;

import reactor.core.publisher.BaseSubscriber;

public class SampleSubscriber<T> extends BaseSubscriber<T> {

	@Override
	public void hookOnSubscribe(Subscription subscription) {
		System.out.println("Subscribed");
		request(1);
	}

	@Override
	public void hookOnNext(T value) {
		System.out.println(value);
		request(1);
	}
}
```

`BaseSubscriber` 是 Reactor 推荐的用户自定义 `Subscriber` 的抽象类，`SampleSubscriber` 扩展该类。该类提供了可以覆盖的 hooks 以调整 subscriber 的行为。它默认触发一个无限 request，与 `subscribe()` 行为相同。但是，当需要自定义 request，则扩展 `BaseSubscriber` 更合适。

扩展 `BaseSubscriber` 至少需要实现 `hookOnSubscribe(Subscription subscription)` 和 `hookOnNext(T value)`。在 `SampleSubscriber` 中，`hookOnSubscribe` 打印状态并提出第一个 request。`hookOnNext` 打印状态并提出另一个 request。

`SampleSubscriber` 输出如下：

```java
Subscribed
1
2
3
4
```

`BaseSubscriber` 还提供了 `requestUnbounded()` 方法，用于切换到无界模式（等价于 `request(Long.MAX_VALUE)`）；以及 `cancel()` 方法。

另外还有：`hookOnComplete`, `hookOnError`, `hookOnCancel`, `hookFinally`（序列终止时被调用）。

#### backpressure 以及 reshape request 的方法

在 Reactor 中实现反压时，consumer 压力通过发送 `request` 向上游操作符传播实现。当前 request 的加和被称为当前 demand。demand 上限为 `Long.MAX_VALUE`，表示无限 request (表示尽可能快地生产，基本等价于禁用反压)。

第一个 request 来自 final-subscriber，订阅全部内容的最直接方式是触发 `Long.MAX_VALUE` 无界 request：

- `subscribe()` 极其大多数 lambda 版本（包含 `Consumer<Subscription>` 的除外）
- `block()`, `blockFirst()` 和 `blockLast()`
- 迭代 `toIterable()` 或 `toStream()`

自定义原始 request 的最简单方式是实现 `BaseSubscriber` 并覆盖 `hookOnSubscribe`，如下所示：

```java
Flux.range(1, 10)
    .doOnRequest(r -> System.out.println("request of " + r))
    .subscribe(new BaseSubscriber<Integer>() {

      @Override
      public void hookOnSubscribe(Subscription subscription) {
        request(1);
      }

      @Override
      public void hookOnNext(Integer integer) {
        System.out.println("Cancelling after having received " + integer);
        cancel();
      }
    });
```

输出

```
request of 1
Cancelling after having received 1
```

> [!WARNING]
>
> 在操作 request 时，要谨慎提出需求，以避免 Flux 卡住。这是 `BaseSubscriber` 的 `hookOnSubscribe` 默认为无界 request 的原因。在覆盖该 hook 时，至少需要调用一次 `request`。

#### 从下游修改需求的操作符

在 subscribe 水平表达的需求可以被上游的操作符重塑。以 `buffer(N)` 操作符为例：如果它收到 `request(2)`，它将其理解为 2 个状态的 buffer 需求。由于 buffer 需要 N 个元素填满，因此 `buffer` 操作符将需求重塑为 `2xN` 。

另外，有些操作符包含一个 `int` 类型的参数 `prefetch`。这是修改下游去求的另一类操作符。它们通常用于处理内部需求，从每个传入元素生成 `Publisher`，如 `flatMap`。

`prefetch` 是调整内部序列初始需求的 一种方法，如果未指定，大多数操作符初始需求为 32。

这些操作符通常还实现了补充优化：当操作符发现其 75% 预取请求，它会从上游重新要求 75%。这是一种启发式优化。

另外还有操作符直接调整请求：`limitRate` 和 `limitRequest`。

`limitRate(N)` 将下游请求拆分为较小的 batch 传到上游。例如，一个 `100` 的请求传到 `limitRate(10)`，会得到 10 个 request`10`  传递到上游。这也是一种补充优化。

该操作符还有一个可以调整补充量得版本：`limitRate(highTide, lowTide)`。将 `lowTide` 设置为 `0` 会得到严格的 `highTide` 请求，而不是通过补充策略进行优化。

`limitRequest(N)` 限制下游的最大需求。如果单个 `request`不会使总需求超出 `N`，则将该去求向上游传播。当 source 发出对应数量，`limitRequest` 认为序列完成，发出 `onComplete` 信号到下游，并取消 source。

### 编程创建序列

下面以编程方式通用定义 `onNext`, `onError` 和 `onComplete` 等相关事件来创建 `Flux` 和 `Mono`。这些方法都通过 API 触发称为 sink 的事件。

#### 同步: generate

以编程创建 `Flux` 的最简单方式是调用 `generate` 方法通过 generator 函数生成 `Flux`。

该方法同步逐个 emit 元素，即 sink 类型为 `SynchronousSink`，每次 callback 最多调用一次 `next()`。也可以另外调用 `error()` 或 `complete()`，这是可选的。

最有用的版本可能允许保留一个 state，在 sink 时引用该 state 以决定接下来 emit 的元素。生成器函数为 `BiFunction<S, SynchronousSink<T>, S>`，其中 `<S>` 为 state 对象的类型。必须提供一个 `Supplier<S>` 作为初始 state，然后生成器函数在每一轮返回一个新的 state。

例如，可以使用 `int` 作为 state：

```java
Flux<String> flux = Flux.generate(
    () -> 0, // ①
    (state, sink) -> {
      sink.next("3 x " + state + " = " + 3*state); // ②
      if (state == 10) sink.complete(); // ③
      return state + 1; // ④
    });
```

1. 初始 state 值为 0
2. 根据 state 选择 emit 内容（乘法表中  3x 的一行）
3. 根据 state 确定 stop 条件
4. 返回一个新的 state，在下一次调用中使用

以上代码的输出：

```
3 x 0 = 0
3 x 1 = 3
3 x 2 = 6
3 x 3 = 9
3 x 4 = 12
3 x 5 = 15
3 x 6 = 18
3 x 7 = 21
3 x 8 = 24
3 x 9 = 27
3 x 10 = 30
```

也可以使用 mutable `<S>`。例如，使用单个 `AtomicLong` 作为 state 重写上述示例：

```java
Flux<String> flux = Flux.generate(
    AtomicLong::new, // ①
    (state, sink) -> {
      long i = state.getAndIncrement(); // ②
      sink.next("3 x " + i + " = " + 3*i);
      if (i == 10) sink.complete();
      return state; // ③
    });
```

1. 将 mutable 对象作为 state
2. 修改 state 值
3. 返回相同实例作为新的 state

> [!TIP]
>
> 如果你的 state 对象在使用后需要清理资源，请使用 `generate(Supplier<S>, BiFunction, Consumer<S>)` 版本来清理最后一个 state 实例。

下面的示例使用包含 `Consumer` 的 `generate` 方法：

```java
Flux<String> flux = Flux.generate(
    AtomicLong::new,
      (state, sink) -> { // ①
      long i = state.getAndIncrement(); // ②
      sink.next("3 x " + i + " = " + 3*i);
      if (i == 10) sink.complete();
      return state; // ③
    }, (state) -> System.out.println("state: " + state)); // ④
```

1. 使用 mutable 对象作为 state
2. 修改 state 值
3. 返回相同实例作为新的 state
4. 将最后一个 state 值（11）作为此 `Consumer` 的输出

如果 state 包含数据库连接或其它需要在结束时清理的资源，则可以在 `Consumer` 中关闭连接。

#### 异步和多线程：create

`create` 是以编程创建 `Flux` 更高级的形式，适合每次 emit 多次，甚至可以从多线程 emit。

它公开了一个 `FluxSink`，其中包含 `next`, `error` 和 `complete` 方法。与 `generate` 不同，它没有基于 state 的版本，但是能够在 callback 中触发多线程事件。

> [!TIP]
>
> `create` 对于将现有 API 与 reactive 代码连接起来非常有用：如基于 listeners 的异步  API。

> [!WARNING]
>
> 虽然 `create` 可以与异步 API 一起使用，但它不会并行你的代码，也不会使其异步。如果在 `create`  的 lambda 中阻塞，将面临死锁或类似的问题。即使使用 `subscribeOn`，长时间阻塞的 `create` lambda （如 `sink.next(t)` 的无限循环）也可能锁死 pipeline：循环耗竭线程，使得同一线程的 request 无法执行。使用 `subscribeOn(Scheduler, false)` 版本：`requestOnSeparateThread = false` 对 `create` 使用 `Scheduler` 线程，并在原始线程中执行 request。

假设你使用一个 listener API。它按 chunk 处理数据，具有两个时间：（1）data-chunk 就绪；（2）处理完成（terminal-event），如 `MyEventListener` 接口所示：

```java
interface MyEventListener<T> {
    void onDataChunk(List<T> chunk);
    void processComplete();
}
```

可以使用 `create` 将其桥接到 `Flux<T>`：

```java
Flux<String> bridge = Flux.create(sink -> {
    myEventProcessor.register( // ➍
      new MyEventListener<String>() { // ➊

        public void onDataChunk(List<String> chunk) {
          for(String s : chunk) {
            sink.next(s); // ➋
          }
        }

        public void processComplete() {
            sink.complete(); // ➌
        }
    });
});
```

1. 桥接 `MyEventListener`
2. chunk 的每个元素成为 `Flux` 的 一个元素
3. `processComplete` event 转换为 `onComplete`
4. 每当 `myEventProcessor` 执行，这些都是异步完成

此外，由于 `create` 可以桥接异步 API 并管理反压，因此可以通过指定 `OverflowStrategy` 来优化反压行为：

- `IGNORE`：完全忽略下游的反压 request。当下游队列满了，会引发 `IllegalStateException`；
- `ERROR`：当下游跟不上，抛出 `IllegalStateException`；
- `DROP`：当下游没准备好接收信号，则丢弃传入的信号；
- `LATEST`：下游只获取来自上游的最新信号；
- `BUFFER`(默认)：如果下游跟不上，则缓存所有信号（无限制缓冲，因此可能导致 `OutOfMemoryError`）

> [!NOTE]
>
> `Mono` 也有一个 `create` generator。`Mono.create` 的 `MonoSink` 不支持多次 emit。在第一次 emit 后丢弃余下所有信号。

#### 单线程异步：push

`push` 介于 `generate` 和 `create` 之间，适用于处理来自单个 producer 的事件。它与 `create` 类似可以异步，并且可以使用 `create` 支持的所有 `OverflowStrategy` 策略来管理反压。但是，一次只能有一个 producer 线程可以调用 `next`, `complete` 或 `error`。

```java
Flux<String> bridge = Flux.push(sink -> {
    myEventProcessor.register(
      new SingleThreadEventListener<String>() { // ➊

        public void onDataChunk(List<String> chunk) {
          for(String s : chunk) {
            sink.next(s); // ➋
          }
        }

        public void processComplete() {
            sink.complete(); // ➌
        }

        public void processError(Throwable e) {
            sink.error(e); // ➍
        }
    });
});
```

1. 桥接 `SingleThreadEventListener`
2. 来自单个 listener 线程的事件使用 `next` push 给 `sink`
3. 从同一个 listener 线程生成的 `complete` event
4. 从同一个 listener 线程生成的 `error` event

##### 混合 push/pull 模型

多数 Reactor operators，如 `create` 都采用 push/pull 混合模型：尽管大多数处理都是异步的（采用 push 方法），但是有一个小的 pull 组件，即 request。

consumer 从 source pull 数据，在第一次 request 之前，它不会 emit 任何内容。只要数据可用，source 会 push 数据给 consumer，但要在 consumer request 的范围内。

`push()` 和 `create()` 都允许设置 `onRequest` consumer，以管理 request 量，并确保仅在有待处理的 request 才通过 sink push 数据。

```java
Flux<String> bridge = Flux.create(sink -> {
    myMessageProcessor.register(
      new MyMessageListener<String>() {

        public void onMessage(List<String> messages) {
          for(String s : messages) {
            sink.next(s); // ➌
          }
        }
    });
    sink.onRequest(n -> {
        List<String> messages = myMessageProcessor.getHistory(n); // ➊
        for(String s : messages) {
           sink.next(s); // ➋
        }
    });
});
```

1. 提出 request 时的 message-poll
2. 如果 msg 立即可用，push 给 sink
3. 稍后异步到达的 msg 也传递

##### push 或 create 后清理

`onDispose` 和 `onCancel` 两个 callbacks 负责取消或终止时执行清理工作。

`onDispose` 可用于在 `Flux` 完成、出错或取消时执行清理工作。

`onCancel` 可用于在使用 `onDispose` 清理之前执行特定于取消的操作。

```java
Flux<String> bridge = Flux.create(sink -> {
    sink.onRequest(n -> channel.poll(n))
        .onCancel(() -> channel.cancel()) // ➊
        .onDispose(() -> channel.close()) // ➋
    });
```

1. 先 `onCancel` ，仅用于取消信号
2. `onDispose` 在完成、错误或取消信号时调用

#### Handle

`handle` 方法稍有不同，它与通用 operator 一样，是实例方法，在 `Mono` 和 `Flux` 中都有。

与 `generate` 类似，它使用 `SynchronousSink` 并且只允许逐个 emit。不同的是，`handle` 可以从每个 source 元素生成任意值，可以跳过某些元素。它可以作为 `map` 和 `filter` 的组合，其签名如下：

```java
Flux<R> handle(BiConsumer<T, SynchronousSink<R>>);
```

Reactive Streams 规范不允许序列中存在 null 值。如果你想要执行 `map`，但想要使用也有的方法作为映射 函数，但是该方法有时返回 null？

例如，以下方法可以用于整数数据源：

```java
public String alphabet(int letterNumber) {
	if (letterNumber < 1 || letterNumber > 26) {
		return null;
	}
	int letterIndexAscii = 'A' + letterNumber - 1;
	return "" + (char) letterIndexAscii;
}
```

此时，可以用 `handle` 删除 null 值：

```java
Flux<String> alphabet = Flux.just(-1, 30, 13, 9, 20)
    .handle((i, sink) -> {
        String letter = alphabet(i); // ➊
        if (letter != null) // ➋
            sink.next(letter); // ➌
    });

alphabet.subscribe(System.out::println);
```

1. 映射为字符
2. 如果 map 函数返回 null
3. 通过不调用 `sink.next` 进行过滤

输出：

```
M
I
T
```

### 线程和 Schedulers

Reactor 和 RxJava 一样，不强制执行并发模型，而是由开发人员控制。但是，这并不妨碍 Reactor 帮助你处理并发问题。

获取 `Flux` 或 `Mono` 并不表示它一定会在专门的 `Thread` 运行。相反，大多数 operator 会继续在前一个 operator 执行的线程中工作。除非另有说明，否则最顶层（source）会在调用 `subscribe()` 的 `Thread` 运行。下面演示在新线程运行 `Mono`：

```java
public static void main(String[] args) throws InterruptedException {
  final Mono<String> mono = Mono.just("hello "); // ①

  Thread t = new Thread(() -> mono
      .map(msg -> msg + "thread ")
      .subscribe(v -> // ②
          System.out.println(v + Thread.currentThread().getName()) // ③
      )
  );
  t.start();
  t.join();
}
```

1. 在 `main` 线程组装 `Mono<String>`
2. 在 `Thread-0` 线程订阅 `Mono<String>`
3. 因此，`map` 和 `onNext` callback 实际上都在 `Thread-0` 运行

```
hello thread Thread-0
```

在 Reactor 中，执行模型和执行发生的位置由使用的 `Scheduler` 决定。`Scheduler` 与 `ExecutorService` 调度功能类似，但是功能更丰富。

`Schedulers` 类提供访问以下执行上下文的 static 方法：

- 没有执行 context (`Schedulers.immediate()`)：在处理时，提交的 `Runnable`  直接被执行，即在当前 `Thread` 执行；
- 单个可重复使用的线程（`Schedulers.single()`）：该方法为所有调用者重复使用同一个线程，直到 `Scheduler` 被释放。如果想要每个调用都有一个专用线程，可以使用 `Schedulers.newSingle()`；
- 无界弹性线程池（`Schedulers.elastic()`）：随着 `Schedulers.boundedElestic()` 的引入，这个线程池不再是首选，因为它容易隐藏背压问题，同时导致线程过多；
- 有界弹性线程池（`Schedulers.boundedElastic()`）：首选线程池。可以让阻塞进程在单独线程运行。从 3.6.0 开始，可以设置两种不同的实现：
  - 基于 `ExecutorService`，重用平台线程。该实现与 `elastic()` 一样，根据需要创建新的 worker-pool并重用空闲的。空闲时间过长（默认 60s）的 worker-pool 被处理。与 `elastic()` 不同的是，它可以控制后备线程的上限（默认 CPU cores x10）。达到上限后最多支持 100000 个 tasks 入队，在有线程可用时重新安排（如果设置有延迟，延迟从线程可用时开始计算）
  - 每个 task 一个线程，用于在 `VirtualThread` 上运行。使用该功能需要 JDK 21+，并将系统属性 `reactor.schedulers.defaultBoundedElasticOnVirtualThreads` 设置为 `true`。满足以上条件，共享的 `Schedulers.boundedElastic()` 将返回特定的 `BoundedElasticScheduler`  实现，该实现在 `VirtualThread` 的新实例上运行每个 task。该实现的行为与基于 `ExecutorService` 的实现类似，但没有空闲池，并且为每个 task 创建一个新的 `VirtualThread`。
- 为并行任务 `Schedulers.parallel()` 而调优的固定 worker-pool。它会创建与 CPU cores 数量相同的工作线程。

此外，可以使用 `Schedulers.fromExecutorService(ExecutorService)` 从任何现有 `ExecutorService` 创建 `Scheduler`。

也可以使用 `newXXX` 方法创建各种 scheduler 类型。例如，`Schedulers.newParallel(yourScheduleName)` 创建一个名为 `yourScheduleName` 的 parallel-scheduler。

> [!WARNING]
>
> `boundedElastic` 可以帮助处理遗留的阻塞代码，但 `single` 和 `parallel` 不能。在默认的 `single` 和 `parallel` scheduler 中使用 Reactor 阻塞 API，如 `block()`, `blockFirst()`, `blockLast()`  等以及 `toIterable()`, `toStream` 会抛出 `IllegalStateException`。
>
> 通过创建实现 `NonBlocking` 接口的 `Thread` 实例，可以将自定义 `Schedulers` 标记为 "non blocking only"。

有些 operators 使用 `Schedulers` 中的特定 scheduler（一般会提供其它 scheduler 选项）。例如，调用 `Flux.interval(Duration.ofMillis(300))` 工厂方法会在每 300 毫秒生成一个 `Flux<Long>`。该 operator 默认在 `Schedulers.parallel()` 启用。下面将 `Scheduler` 改为类似 `Schedulers.single()` 的实例：

```jade
Flux.interval(Duration.ofMillis(300), Schedulers.newSingle("test"))
```

Reactor 提供了两种在 reactive chain 中切换 `Scheduler` 的方法：

- `publishOn`
- `subscribeOn`

两者都接受一个 `Scheduler`，将执行 context 切换到该 `Scheduler`。但是 `publishOn` 在 chain 中的位置很重要，而 `subscribeOn` 的位置则无关紧要。只要理解在 subscribe 之前什么都不会发生，就能理解这种差异。

在 Reactor 中，在链接 operator 时，可以根据需要将很多 `Flux` 和 `Mono` 包装到彼此内部。在订阅后，将创建一个 `Subscriber` 对象 chain，沿链向上到第一个 publisher。下面来详细看看 `publishOn` 和 `subscribeOn`。

#### publishOn

`publishOn` 的使用方式与 subcriber-chain 中其它 operator 相同。它从上游获取信号，并在关联的 Scheduler 的 worker 中执行 callback，将信号重播到下游。因此，它会影响后续 operator 的执行位置，直到下一个 `publishOn`：

- 将执行 context 更改为 `Scheduler` 选择的 `Thread`
- 根据规范，`onNext` 按顺序发生，因此这会占用单个线程
- 除非在特定 `Scheduler` 工作，否则 `publishOn` 之后的 operator 会继续在同一线程执行

以下示例使用 `publishOn` 方法：

```java
Scheduler s = Schedulers.newParallel("parallel-scheduler", 4); // ①

final Flux<String> flux = Flux
    .range(1, 2)
    .map(i -> 10 + i) // ②
    .publishOn(s) // ③
    .map(i -> "value " + i); // ④

new Thread(() -> flux.subscribe(System.out::println)); // ⑤
```

1. 创建一个包含 4 个 `Thread` 的 `Scheduler`
2. 第一个 `map` 在 <5> 的匿名线程上运行
3. `publishOn` 将整个序列切换到 <1> 中的线程
4. 第二个 `map` 在 <1> 中的线程运行
5. 该匿名线程为 subscription 发生的地方。打印发生在最新的 context 中，即来自 `publishOn` 的 context。

#### subscribeOn

`subscribeOn` 适用于订阅过程，即构建 chain 时。通常建议将其放在数据 source 后面，因为中间的 operators 会影响执行 context。

但是，它不会影响 `publishOn` 对后续行为的影响，`publishOn` 仍然会切换 chain 余下部分的执行 context。

- `subscribeOn` 更改整个 operator-chain 订阅的线程
- 从 `Scheduler` 选择一个线程 

> [!NOTE]
>
> 下游最接近的 `subscribeOn` 调用才能有效地将订阅和 request 信号调度到 source，或拦截它们的操作符。使用多个 `subscribeOn` 调用会引入不必要的线程切换，这些切换毫无意义。

示例：

```java
Scheduler s = Schedulers.newParallel("parallel-scheduler", 4); // ①

final Flux<String> flux = Flux
    .range(1, 2)
    .map(i -> 10 + i) // ②
    .subscribeOn(s) // ③
    .map(i -> "value " + i); // ④

new Thread(() -> flux.subscribe(System.out::println)); // ⑤
```

1. 创建一个包含 4 个线程的 `Scheduler`
2. 第一个 `map` 在 4 个线程之一上 运行
3. 因为 `subscribeOn` 从订阅开始 <5> 就切换了整个序列
4. 第二个 `map` 在相同线程运行
5. 该匿名线程是随处订阅的地方，当 `subscribeOn` 立即将其转移到 `Scheduler` 的线程

### 处理错误

> [!TIP]
>
> 快速查看处理错误的 operators，可参考 [操作符决策树](#处理-errors)。

在 Reactive Streams 中，error 是终止 event。一旦发生错误，它会停止序列，并沿着 operator-chain 传播到最后一步，即 `Subscriber` 的 `onError` 方法。

此类错误应在应用层面处理。例如，在 UI 中显示错误通知。因此，推荐定义 subscriber 的 `onError` 方法。

> [!WARNING]
>
> 如果未定义，`onError` 会抛出 `UnsupportedOperationException`。可以使用 `Exceptions.isErrorCallbackNotImplemented` 进一步检测和分类。



### Sinks



### 示例

- fork (single source, multiple subscribers)

```java
var mono = webclient.get()
    .exchangeToMono(r -> r.bodyToMono(String.class))
    .share(); // calling share to prevent 2 http calls
mono.map(mapper::mapForA).subscribe(subscriberA);
mono.map(mapper::mapForB).subscribe(subscriberB);
```

- join (multiple source, single subscriber)

```java
var callA = webclient.get()
    .exchangeToMono(r -> r.bodyToMono(String.class));
var callB = webclient.get()
    .exchangeToMono(r -> r.bodyToMono(String.class));
Mono.zip(callA, callB, this::joinResults)
    .subscribe(System.out::println);
```

## 5. Reactive

### Reactive to Blocking

有时候，我们只能把部分代码迁移为响应式的，然后在命令式代码中重用响应式序列。

因此，对 `Mono` 需要阻塞直到值可用，可以使用 `Mono#block()`。如果触发 `onError` 事件，会抛出异常。

一般来说，应该尽可能端到端的使用响应式代码。阻塞操作可能导致整个 pipeline 锁死。

- **Flux**


```java
public final T blockLast();
```

订阅 `Flux` 并阻塞，直到上游发出最后一个值或完成。返回最后一个值，如果 `Flux` 为空则返回 null。如果出错则抛出异常。

需要注意的是，`blockLast()` 会触发一个新的订阅，换言之，对 hot-publisher，`blockLast()` 可能会错过信号。

<img src="./images/image-20250318173038609.png" alt="image-20250318173038609" style="zoom:50%;" />

```java
public final T blockLast(java.time.Duration timeout);
```

<img src="./images/image-20250318173059141.png" alt="image-20250318173059141" style="zoom:50%;" />

```java
public final java.lang.Iterable<T> toIterable();
```

将 `Flux` 转换为 `Iterable`，在 `Iteratoe.next()` 调用上阻塞。

<img src="./images/image-20250318173410255.png" alt="image-20250318173410255" style="zoom:50%;" />

### Blocking to Reactive

假设你有阻塞代码，如与数据库的 JDBC 连接，现在希望将其集成到 reactive pipeline 中，同时避免影响性能。

最好的做法是通过 `Scheduler` 将阻塞的部分进行隔离，保持 pipeline 余下部分的高效，并且只在需要时创建额外线程。

对 JDBC 示例，可以用 `fromIterable`，问题是如何防止它阻塞 pipeline 的余下分布。

`subscribeOn` 可以从 `Scheduler` 的开始隔离序列。例如，`Schedulers.boundedElastic()` 会创建一个按需增长的线程池，自动释放在一段时间内未使用的线程。为了避免滥用导致线程过多，`boundedElastic` 可以设置线程数上限。

JDBC 示例:

```java
Flux.defer(() -> Flux.fromIterable(repository.findAll()))
            .subscribeOn(Schedulers.boundedElastic());
```

### defer

```java
public static <T> Flux<T> defer(java.util.function.Supplier<? extends Publisher<T>> supplier)
```

对生成的 `Flux` 进行订阅时，会延迟提供 `Publisher`，因此 source 的实例化推迟到每次订阅，并且 `Supplier` 可以创建特定于 Subscriber 的实例。但是，如果 supplier 不生成新实例，则该 operator 的行为与 `from(Publisher)` 类似。

### subscribeOn

```java
public final Flux<T> subscribeOn(Scheduler scheduler);
```

在指定 `Scheduler` 的 `Scheduler.Worker` 运行 subscribe, onSubscribe 和 request。因此，将此 operator 放在 chain 的任何位置都会影响从 chain 的开头到下一次 `publishOn` 的 `onNext`, `onError`, `onComplete` 的执行上下文。

用于慢速 publisher，如阻塞 IO 和快速 consumer 的场景：

```java
flux.subscribeOn(Schedulers.single()).subscribe() 
```

### publishOn

```jade
public final Flux<T> publishOn(Scheduler scheduler);
```

在指定 Scheduler Worker 运行 `onNext`, `onComplete` 和 `onError`。


## 6. Operators

> [!TIP]
>
> 在下面的讨论中，如果某个 operator 特定于 `Flux` 或 `Mono`，则会加上前缀，如 `Flux#fromArray`。Flux 和 Mono 都有的 operator 没有前缀。

### 创建

- 生成一个已有元素 `T`：`just(Flux|Mono)`
  - 从 `Optional<T>`: `Mono#justOrEmpty(Optional<T>)`
  - 可能为 nul 的 `T`：`Mono#justOrEmpty(T)`

- 生成从方法返回的 `T`：`just(Flux|Mono)`
  - lazy 捕获：`Mono#fromSupplier`
  - 或在 `defer(Flux|Mono)` 中包装 `just(Flux|Mono)`

- 生成可枚举的多个 `T`：`Flux#just(T...)`
- 迭代
  - 数组：`Flux#fromArray`
  - 集合或 iterable：`Flux#fromIterable`
  - 整数范围：`Flux#range`
  - `Stream`：`Flux#fromStream(Supplier<Stream>)`
- 从单个值的 source emits，例如：
  - 从 `Supplier<T>`：`Mono#fromSupplier`
  - 从 task：`Mono#fromCallable`, `Mono#fromRunnable`
  - 从 `CompletableFuture<T>`：`Mono#fromFuture`
- 直接完成：`empty(Flux|Mono)`
- 直接 error：`error(Flux|Mono)`
  - lazy 构建 `Throwable`：`error(Supplier<Throwable>)(Flux|Mono)`
- 不做任何事：`never(Flux|Mono)`
- 在订阅时决定：`defer(Flux|Mono)`
- 取决于 disposable 资源：`using(Flux|Mono)`
- 以编程方式生成事件（可以使用状态）：
  - 逐个同步生成：`Flux#generate`
  - 异步，一次 emit 多个信号：`Flux#create`, `Mono#create` 也可以，但不能 emit 多个信号

#### Mono.empty

```java
public static <T> Mono<T> empty();
```

创建一个不 emit 任何值，直接 complete 的 `Mono`。

#### Mono.never

```java
public static <T> Mono<T> never()
```

`Mono.never()` 不发出任何信号，实际上无限期运行，仅在测试中有用。

### 转换

- **转换已有数据**

  - 1-to-1 转换（如字符串->长度）：`map(Flux|Mono)`
    - 直接 cast：`cast(Flux|Mono)`
    - materialize 每个 source 值的 index：`Flux#index`

  - 1-to-n 转换（如字符串->字符）：`flatMap(Flux|Mono)` + factory 方法

  - 1-to-n 转换，对 source 每个元素进行编程处理：`handle(Flux|Mono)`
  - 为 source 的每个元素运行异步任务（如 url -> http request）：`flatMap(Flux|Mono)`+一个异步 `Publisher` 返回方法
    - 忽略一些数据：在 `flatMap` lambda 中根据条件返回 `Mono.empty()`
    - 保持原始序列顺序：`Flux#flatMapSequential` （立即触发异步进程，但对结果重新排序）
    - 异步任务可以从 `Mono` 源返回多个值：`Mono#flatMapMany`

- 向已有序列添加元素：

  - 添加到开头：`Flux#startWith(T...)`
  - 添加到末尾：`Flux#concatWithValues(T...)`

- 聚合 `Flux`：以下都有前缀 `Flux#`

  - 聚合为 `List`: `collectList`, `collectSortedList`
  - 聚合为 `Map`：`collectMap`, `collectMultiMap`
  - 聚合为任意容器：`collect`
  - 聚合为序列大小：`count`
  - 在元素之间应用函数（如 sum）：`reduce`
    - emit 每个中间值：`scan`
  - 从 predicate 转换为 boolean
    - 应用于所有值（AND）：`all`
    - 至少一个值（OR）：`any`
    - 测试值的存在：`hasElements` (`Mono` 也有)
    - 测试特定值的存在：`hasElement(T)`

- 合并 publishers

  - 按顺序：`Flux#concat` 或 `.concatWith(Other)(Flux|Mono)`
    - 延迟错误直到剩余 Publisher emit：`Flux#concatDelayError`
    - eagerly 订阅后面的 publisher: `Flux#mergeSequential`
  - 按 emit 顺序（按元素到的顺序组合）：`Flux#merge`, `.mergeWith(other)(Flux|Mono)`
    - 不同类型（变换 merge）：`Flux#zip`, `Flux#zipWith`
  - 配对值
    - 2 个 Mono 合并为一个 `Tuple2`：`Mono#zipWith`
    - n 个 `Mono` 完成后合并：`Mono#zip`
  - 协调终止
    - 1 个 `Mono` 和任意 source 到 `Mono<Void>`：`Mono#add`
    - n 个 sources，完成时：`Mono#when`
    - 放入任意容器类型
      - 每次所有  publishers 都 emit 时：`Flux#zip` (直到最小基数)
      - 每当有新值到达：`Flux#combineLatest`
  - 选择第一个 publisher，当：
    - 生成一个值 `onNext`: `firstWithValue(Flux|Mono)`
    - 生成任意信号：`firstWithSignal(Flux|Mono)`
  - 由 source 序列中的元素触发：`switchMap` （每个 source 元素映射到一个 `Publisher`）
  - 由 publisher 序列中的下一个启动的 publisher 触发：`switchOnNext`

- 重复已有序列：`repeat(Flux|Mono)`

  - 按时间间隔：`Flux.interval(duration).flatMap(tick -> myExistingPublisher)`

- 有一个空序列，但是：

  - 需要一个值：`defualtIfEmpty(Flux|Mono)`
  - 需要另一个序列：`switchIfEmpty(Flux|Mono)`

- 有一个序列，但是对序列元素不感兴趣：`ignoreElements(Flux.ignoreElements()|Mono.ignoreELement())`

  - 完成结果为 `Mono<Void>`: `then(Flux|Mono)`
  - 最后等待另一个 task 完成：`thenEmpty(Flux|Mono)`
  - 最后切换到另一个 `Mono`: `Mono#then(mono)`
  - 最后 emit 一个值：`Mono#thenReturn(T)`
  - 最后切换到另一个 `Flux`：`thenMany(Flux|Mono)`

- 有一个 `Mono`，希望推迟完成：

  - 直到另一个从该值派生的 publisher 完成：`Mono#delayUntil(Function)`

- 递归扩展为序列 graph，并 emit 组合：

  - 广度优先扩展：`expand(Function)(Flux|Mono)`
  - 深度优先扩展：`expandDeep(Function)(Flux|Mono)`

#### map

```java
public final <V> Flux<V> map(java.util.function.Function<? super T,? extends V> mapper)
```

对 `Flux` 的每个元素应用同步函数进行转换。

示例：每个值 +1

```java
Flux<Integer> numbersFlux = Flux.range(1, 10)
        .map(val -> val + 1);

StepVerifier.create(numbersFlux)
            .expectNext(2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
            .verifyComplete();
```

示例：如果数字 >0，返回 `>`；等于 0 返回 "="；小于 0 返回  "<"

```java
Flux<Integer> numbersFlux = Flux.just(100, -1, 0, 78, 1);

Flux<String> resultSequence = numbersFlux
        .map(i -> {
            if (i > 0)
                return ">";
            else if (i == 0)
                return "=";
            else
                return "<";
        });

StepVerifier.create(resultSequence)
        .expectNext(">", "<", "=", ">", ">")
        .verifyComplete();
```

#### cast

- **Flux**

```java
public final <E> Flux<E> cast(java.lang.Class<E> clazz);
```

将 `Flux` 当前生成的类型转换为目标类型。

示例：使用 `map` 也能够转换类型，但是 `cast` 更便捷

```java
Flux<Object> objectFlux = Flux.just("1", "2", "3", "4", "5");
Flux<String> numbersFlux = objectFlux
        .cast(String.class);

StepVerifier.create(numbersFlux)
        .expectNext("1", "2", "3", "4", "5")
        .verifyComplete();
```



#### Flux.collectList

```java
public final Mono<java.util.List<T>> collectList();
```

收集 `Flux` emit 的所有元素到一个 `List`，以 `Mono` 的形式返回。如果序列为空，返回空 `List`。

<img src="./images/image-20250318164205353.png" alt="image-20250318164205353" style="zoom:50%;" />

示例：`collectList()` 组合 `block()` 获取值

```java
Flux<String> serviceResult = fortuneTop5();

List<String> results = serviceResult.collectList().block();
assertEquals(Arrays.asList("Walmart", "Amazon", "Apple", "CVS Health", "UnitedHealth Group"), results);
```

#### defaultIfEmpty

```java
public final Mono<T> defaultIfEmpty(T defaultV);
```

如果 `Mono` 完成后没有任何数据，则提供一个默认值。

<img src="./images/image-20250318163843095.png" alt="image-20250318163843095" style="zoom:50%;" />

示例：如果没有值，就返回提供的默认值

```java
Mono<String> maybe_service = Mono.empty();
Mono<String> result = maybe_service
        .defaultIfEmpty("no results");

StepVerifier.create(result)
        .expectNext("no results")
        .verifyComplete();
```

#### Flux.reduce

```java
public final <A> Mono<A> reduce(A initial,
                                java.util.function.BiFunction<A,? super T,A> accumulator)
```

将 Flux 序列的值缩减为与 `initial` 类型匹配的单个值。缩减由 `BiFunction` 执行。

示例：计算数字加和

```java
Flux<Integer> numerical_service = Flux.range(1, 10);
Mono<Integer> sum = numerical_service
        .reduce(0, Integer::sum);

StepVerifier.create(sum)
        .expectNext(55)
        .verifyComplete();
```



### 查看

- 不修改最终序列，想要：
  - 收到通知，或执行额外行为（side-effect）
    - emission: `doOnNext(Flux|Mono)`
    - completion: `Flux#doOnComplete`, `Mono#doOnSuccess`
    - error termination: `doOnError(Flux|Mono)`
    - cancellation: `doOnCancel(Flux|Mono)`
    - 序列开始：`doFirst(Flux|Mono)`
      - 这与 `Publisher#subscribe(Subscriber)` 相关
    - 订阅后：`doOnSubscribe(Flux|Mono)`

#### doOnNext

**Flux**

```java
public final Flux<T> doOnNext(java.util.function.Consumer<? super T> onNext)
```

当 `Flux` emit 一个元素触发的行为（**side-effect**）。

<img src="./images/image-20250319173248646.png" alt="image-20250319173248646" style="zoom:50%;" />

首先执行 `Consumer`，然后 `onNext` 信号向下游传播。



### 过滤序列



### 处理 Errors

**创建 error 序列**：`error(Flux|Mono)`

- 替换一个成功 `Flux` 的 completion: `.concat(Flux.error(e))`
- 替换一个成功 `Mono` 的 emission：`.then(Mono.error(e))`
- onNexts 之间间隔时间过长：`timeout(Flux|Mono)`
- lazy: `error(Supplier<Throwable>)(Flux|Mono)`



### 处理 Time



### 拆分 Flux



### 转到同步

阻塞 operator 通常用于测试，或者在没有其它方法可用，只能返回同步。

> [!NOTE]
>
> 如果从标记为  non-blocking-only 的 `Scheduler`（默认为 `parallel()` 和 `single()`）调用这些方法，会抛出 `UnsupportedOperatorException `（`Mono#toFuture` 除外）。

假设有一个 `Flux<T>`：

- 阻塞直到获取第一个元素：`Flux#blockFirst`
  - 加一个 timeout: `Flux#blockFirst(Duration)`
- 阻塞直到获取最后一个元素（如果为空，则返回 null）：`Flux#blockLast`
  - 加一个 timeout: `Flux#blockLast(Duration)`
- 同步切换到 `Iterable<T>`：`Flux#toIterable`
- 同步切换到 Java 8 `Stream<T>`：`Flux#toStream`

假设有一个 `Mono<T>`：

- 阻塞直到获取值：`Mono#block`
  - 加一个 timeout: `Mono#block(Duration)`
- `CompletableFuture<T>`: `Mono#toFuture`

#### Flux.blockFirst

```java
public final T blockFirst();
```

订阅 `Flux` 并阻塞，直到上游发出第一个值或完成。返回第一个值，如果  `Flux` 为空则返回 null。如果 Flux 出错，则抛出异常。

<img src="./images/image-20250318172301627.png" alt="image-20250318172301627" style="zoom:50%;" />

示例：

```java
Flux<String> serviceResult = Flux.just("valid result")
        .concatWith(Flux.error(new RuntimeException("oops, you collected to many, and you broke the service...")));

String result = serviceResult.blockFirst();

assertEquals("valid result", result);
```



```java
public final T blockFirst(java.time.Duration timeout);
```

订阅 `Flux` 并阻塞，直到上游发出第一个值的信号、完成或超时。返回第一个值，如果 `Flux` 为空则返回 null。如果 `Flux` 出错则抛出错误，如果超时，也抛出错误。

<img src="./images/image-20250318172815985.png" alt="image-20250318172815985" style="zoom:50%;" />

#### Flux.blockLast

#### Flux.toIterable

#### Flux.toStream

#### Mono.block

**block**

```java
public T block()
```

`block()` 订阅 `Mono`，阻塞直到收到下一个信号:

- 返回 `Mono` 的值
- 如果 `Mono` 为空则返回 `null`
- 如果 `Mono` 出错，则抛出异常

<img src="./images/image-20250318171628422.png" alt="image-20250318171628422" style="zoom:50%;" />

例如：

```java
Mono<String> serviceResult = Mono.just("Hello World!");

String result = serviceResult.block();
assertEquals("Hello World!", result);
```

**block(timeout)**

```java
public T block(java.time.Duration timeout)
```

加了时间限制。订阅 `Mono`，阻塞直到收到下一个信号，或者超时。超时也会抛出 `RuntimeException `。其它同上。

另外，每个 `block()` 会触发一个新订阅，因此，对 hot-publisher，该操作可能错过信号。

示例：最多阻塞 1 秒，超时抛出异常

```java
Exception exception = assertThrows(IllegalStateException.class, () -> {
    Mono<String> serviceResult = Mono.never(); // unresponsiveService()

    String result = serviceResult.block(Duration.ofSeconds(1));
});

String expectedMessage = "Timeout on blocking read for 1";
String actualMessage = exception.getMessage();

assertTrue(actualMessage.contains(expectedMessage));
```

#### Mono.blockOptional

```java
public java.util.Optional<T> blockOptional();
public java.util.Optional<T> blockOptional(java.time.Duration timeout);
```

同 `Mono#block`，只是以 `Optional` 的形式返回。

### 将 Flux 多播到多个 Subscriber



### Transform

Reactor 提供了多个 operators，可用于转换数据。

- 示例：将字符串转换为大写

这是一个简单的 1-1 转换，没有延迟，可以采用 `map` operator。下面将 `User` 的所有名称转换为大写：

```java
mono.map(user -> new User(user.getUsername().toUpperCase(),
            user.getFirstname().toUpperCase(),
            user.getLastname().toUpperCase()));
```

- 对 `Flux`，也可以用相同代码映射每个元素

```java
flux.map(user -> new User(user.getUsername().toUpperCase(),
            user.getFirstname().toUpperCase(),
            user.getLastname().toUpperCase()));
```

- 异步映射

现在，假设你需要调用一个 Web 服务来将字符串大写。这个调用可能会有延迟，因此不能使用 `map`，而是用 `Flux` 或 `Mono` 来表示异步调用，使用 `flatMap`。

`flatMap` 采用一个 trans `Function`，返回 `Publisher<U>` 而不是 `U`。`flatMap`订阅内部 publisher，合并为一个全局输出，得到 `Flux<U>`。注意：内部 publisher 生成的值达到时间不同，得到的结果在 `Flux` 中可能交错。

```java
Mono<User> asyncCapitalizeUser(User u) {
    return Mono.just(new User(u.getUsername().toUpperCase(),
            u.getFirstname().toUpperCase(),
            u.getLastname().toUpperCase()));
}

flux.flatMap(user -> asyncCapitalizeUser(user));
```

### Merge

merge 将多个 `Publisher` 的结果合并到一个 `Flux`。

- `mergeWith`

将当前 `Flux` 和一个 `Publisher` 的数据合并，得到一个**交错**合并的序列。

```java
public final Flux<T> mergeWith(Publisher<? extends T> other);
```

<img src="./images/image-20250318142605266.png" alt="image-20250318142605266" style="zoom:50%;" />

- `concatWith`

将当前 `Flux` 和另一个 `Publisher` 的数据拼接，没有交错。

如果希望保持数据的顺序，`concatWith` 与 `mergeWith` 更合适。

```java
public final Flux<T> concatWith(Publisher<? extends T> other);
```

<img src="./images/image-20250318142843507.png" alt="image-20250318142843507" style="zoom:50%;" />

在 `flux1` 元素发送完成后，再发送 `flux2` 的元素。

- `concat`

静态方法 `concat` 串联 `sources` 提供的所有 `Publisher` 的元素。该合并操作按顺序进行，先订阅第一个 source 的元素，完成后再到下一个。任何错误都会导致序列中断，并立即转发到下游

```java
public static <T> Flux<T> concat(java.lang.Iterable<? extends Publisher<? extends T>> sources);
```

<img src="./images/image-20250318143627596.png" alt="image-20250318143627596" style="zoom:50%;" />



```java
public static <I> Flux<I> merge(java.lang.Iterable<? extends Publisher<? extends I>> sources);
```

<img src="./images/image-20250318142305355.png" alt="image-20250318142305355" style="zoom:50%;" />

- 示例：将两个 `Flux` 的元素合并

### Request

反压（backpressure）是一种反馈机制，`Subscriber` 向 `Publisher` 发出信号，告知其能够处理多少数据，从而限制 `Publisher` 生成数据的速率。

这种需求控制在 `Subscription` 水平完成：每次调用 `subscribe()` 都会创建一个 `Subscription`，通过对 `Subscription` 的操作可以取消数据流，或使用 `request(long)` 调整数据需求。

`request(Long.MAX_VALUE)` 表示无限制需求，因此 `Publisher` 会尽可能快地生成数据。

- 示例：`StepVerifier` 也可以调整需求，通过使用相关参数为初始 request `create` 或 `withVirtualTime`，然后在期望中使用 `thenRequest(long)` 进一步 request。

下面使用 `StepVerifier` 先 request 所有值，然后期望收到 4 个值。

```java
StepVerifier.create(flux)
        .expectNextCount(4)
        .expectComplete();
```

- 一次 request 一个元素

初始 request 1 个元素；收到并断言第一个元素后，再 request 1 个元素

```java
StepVerifier.create(flux, 1)
        .expectNext(User.SKYLER)
        .thenRequest(1)
        .expectNext(User.JESSE)
        .thenCancel();
```

如果 request 数不够，source 无法 complete，除非直接取消。如果想确保在指定时间内没有传入信号，可以使用 `.expectTimeout(Duration)`。

- `log`

下面使用 `log` operator 来输出序列内部状态。

`repository` 是包含多个 `User` 的 `Flux`。

```java
repository
        .findAll()
        .log();
```

```
15:04:00.116 [main] INFO reactor.Flux.Zip.1 - onSubscribe(FluxZip.ZipCoordinator)
15:04:00.116 [main] INFO reactor.Flux.Zip.1 - request(1)
15:04:00.225 [parallel-1] INFO reactor.Flux.Zip.1 - onNext(User{username='swhite', firstname='Skyler', lastname='White'})
15:04:00.225 [parallel-1] INFO reactor.Flux.Zip.1 - request(1)
15:04:00.336 [parallel-1] INFO reactor.Flux.Zip.1 - onNext(User{username='jpinkman', firstname='Jesse', lastname='Pinkman'})
15:04:00.336 [parallel-1] INFO reactor.Flux.Zip.1 - request(2)
15:04:00.426 [parallel-1] INFO reactor.Flux.Zip.1 - onNext(User{username='wwhite', firstname='Walter', lastname='White'})
15:04:00.531 [parallel-1] INFO reactor.Flux.Zip.1 - onNext(User{username='sgoodman', firstname='Saul', lastname='Goodman'})
15:04:00.531 [parallel-1] INFO reactor.Flux.Zip.1 - onComplete()
```

- `do` / `doOn`

如果希望指定自定义操作，而不是修改序列中的元素，则可以使用以 `do` 或 `doOn` 开头的带副作用的方法。

例如，如果希望operator 每次收到 request 都打印 "Requested"，则可以使用 `doOnRequest`。

如果每次收到 `subscription`，在发出任何信号之前先打印 "Starting"，则可以使用 `doFirst`。

每个 `doOn` 方法都采用一个 callback 表示相应事件的自定义操作。

在这些 callbacks 中不应该调用具有延迟的操作。

下面：所所有用户，先打印 "Starring:"，然后对每个元素打印 "firstname lastname"，完成后输出 "The end!"。

```java
repository.findAll()
    .doFirst(() -> System.out.println("Starring:"))
    .doOnNext(user -> System.out.println(user.getFirstname() + " " + user.getLastname()))
    .doOnComplete(() -> System.out.println("The end!"));
```

### Error

Reactor 提供了几个用于处理错误的 operators。

- 出错时采用默认值：`onErrorReturn`

当输入 `Mono` 出错时，返回默认 `User.SAUL`，否则不变。

```java
mono.onErrorReturn(User.SAUL);
```

- 出错时采用另一个 `Publisher<T>`：`onErrorResumeWith`

```jva
flux.onErrorResume(throwable -> Flux.just(User.SAUL, User.JESSE));
```

`onErrorReturn` 只能返回一个值，`onErrorREsume` 则可以返回多个值。

- 处理异常

处理异常稍微复杂一些。最简单的方式是在 lambda 表达式中使用 `try-catch` 将其转换为 `RuntimeException`，向下游发出信号。

`Exceptions#propagate` 工具可以将一个异常包装成一个特殊的 runtime 异常，该异常可以由 Reactor Subscriber 和 `StepVerifier` 解包，从而避免在堆栈中看到不相关的 `RuntimeException`。

例如：`capitalizeUser` 会抛出 `GetOutOfHereException`，在 catch 中用 `Exceptions.propagate(e)` 进行包装。

```java
flux.map(user -> {
        try {
            return capitalizeUser(user);
        } catch (GetOutOfHereException e) {
            throw Exceptions.propagate(e);
        }
    });
```

### zip

```java
public static <T1,T2,T3> Flux<Tuple3<T1,T2,T3>> zip(Publisher<? extends T1> source1,
                                                    Publisher<? extends T2> source2,
                                                    Publisher<? extends T3> source3)
```

将三个 source 合并在一起，将等待所有 source 生成一个元素，然后合并这些元素为一个 `Tuple3`。持续该操作，直到任何一个 source 完成。

<img src="./images/image-20250318160602519.png" alt="image-20250318160602519" style="zoom:50%;" />

例如，使用三个 `Flux<String>` 创建用户：`zip` 得到一个 `Tuple3`，然后用 `Tuple3` 创建 `User`：

```java
Flux.zip(usernameFlux, firstnameFlux, lastnameFlux)
		.map(tuple -> new User(tuple.getT1(), tuple.getT2(), tuple.getT3()));
```

### firstWithValue

- **Mono**

```java
public static <T> Mono<T> firstWithValue(java.lang.Iterable<? extends Mono<? extends T>> monos)
```

选择第一个发出任意值的 `Mono`。

包含值的 source 比空 source (仅发出 `onComplete`)或失败的 source（仅发出 `onError`）优先级高。

如果没有任何 source 提供值，`firstWithValue` 失败并抛出 `NoSuchElementException`（前提是至少有两个 sources）。

<img src="./images/image-20250318162717366.png" alt="image-20250318162717366" style="zoom:50%;" />

- **Flux**

```java
public static <I> Flux<I> firstWithValue(java.lang.Iterable<? extends Publisher<? extends I>> sources)
```

选择第一个生成值的 `Publisher`。

<img src="./images/image-20250318162905140.png" alt="image-20250318162905140" style="zoom:50%;" />

### ignoreElements

```java
public final Mono<T> ignoreElements()
```

忽略 `onNext()` 信号，仅传播终止事件。

<img src="./images/image-20250318163043521.png" alt="image-20250318163043521" style="zoom:50%;" />

### then

```java
public final Mono<java.lang.Void> then()
```

返回一个当 `Flux` 完成，同时完成的 `Mono`。该操作忽略序列，仅响应完成或错误信号。

<img src="./images/image-20250318163331468.png" alt="image-20250318163331468" style="zoom:50%;" />



### justOrEmpty

```java
public static <T> Mono<T> justOrEmpty(@Nullable
                                      java.util.Optional<? extends T> data)
public static <T> Mono<T> justOrEmpty(@Nullable
                                      T data)；
```

如果 `Optional.isPresent()`，则创建的 `Mono` 包含该元素，否则 `Mono` 只发出 onComplete 信号。

<img src="./images/image-20250318163635012.png" alt="image-20250318163635012" style="zoom:50%;" />

## 7. Test: StepVerifier

`StepVerifier` 来自 reactor-test artifiact，它能够订阅任何 `Publisher`，如 `Flux`, Akka Stream 等，然后针对该序列断言。

如果事件与断言不一致，`StepVerifier` 抛出 `AssertionError`。

可以使用静态工厂方法 `create` 创建 `StepVerifier`。它提供了一个 DSL 设置对数据的期望，并以终端期望（completion, error, calcel）结束。

`StepVerifier` 使用步骤：

- 创建 `StepVerifier`，使用 `create(Publisher)` 或 `withVirtualTime()`
- 设置期望值，`expectNext(T...)`, `expectNextMatches(Predicate)`, `expectNextCount(long)` 或 `expectNextSequence(Iterable)`
- 设置订阅操作，`thenRequest(long)` 后 `thenCancel()`
- 构建 `StepVerifier`，`expectComplete()`, `expectError()`, `expectError(class)`, `expectErrorMatches(Predicate)`, `thenCancel()`
- 使 `StepVerifier` 订阅 `Publisher`
- 使用 `verify()` 或 `verify(Dueration)` 验证期望

> [!NOTE]
>
> 必须调用 `verify()` 方法或与 terminal 期望结合的 verify 方法，如 `.verifyErrorMessage(String)`，否则 `StepVerifier` 不会订阅序列，也就不会断言任何内容。

```java
StepVerify.create(T<Publisher>)
    .{expectations...}
	.verify();
```

`StepVerify` 提供了许多断言，具体可参考 [API](https://javadoc.io/static/io.projectreactor.addons/reactor-test/3.0.7.RELEASE/reactor/test/StepVerifier.html)。

- `Flux` 断言示例

断言 `Flux` 依次生成 "foo" 和 "bar" 两个元素，然后完成。

```java
Flux<String> flux = Flux.just("foo", "bar");
StepVerifier.create(flux)
        .expectNext("foo")
        .expectNext("bar")
        .expectComplete()
        .verify();
```

- 异常断言

```java
StepVerifier.create(flux)
        .expectNext("foo")
        .expectNext("bar")
        .expectError(RuntimeException.class)
        .verify();
```

- `expectNextMatches` 断言

`expectNextMatches` 可以检查元素是否满足指定 `Predicate`。例如：

断言第一个 `User` 的 userName 为 "swhite"，第二个为 "jpinkman"，然后完成。

```java
StepVerifier.create(flux)
        .expectNextMatches(user -> user.getUsername().equals("swhite"))
        .expectNextMatches(user -> user.getUsername().equals("jpinkman"))
        .expectComplete()
        .verify();
```

- assert 断言

```java
default StepVerifier.Step<T> assertNext(java.util.function.Consumer<? super T> 
                                        assertionConsumer);
StepVerifier.Step<T> consumeNextWith(java.util.function.Consumer<? super T> consumer);
```

`assertNext` 与 `consumeNextWith` 等价。在 `Consumer` 中可以用 Hamcrest, AssertJ, Junit 等断言方法进行断言。

- 计数断言

每秒生成 1 个元素，生成 10 个元素。

```java
Flux<Long> flux = Flux.interval(Duration.ofSeconds(1)).take(10);
StepVerifier.create(flux)
        .expectNextCount(10)
        .verifyComplete();
```

- 虚拟时间

如果 `Flux` 每秒生成一个元素，生成 3600 个元素才完成。

显然我们不希望测试运行数小时，那么如果加速运行，同能能够断言数据？`StepVerifier` 提供了一个虚拟时间选项 ：使用 `StepVerifier.withVirtualTime(Supplier<Publisher>)`，`StepVerifier` 会用 `VirtualTimeScheduler` 临时替换默认的`Scheduler`，即用可操作的虚拟时钟替换实际时钟。

操作示例：

```java
StepVerifier.withVirtualTime(() -> Mono.delay(Duration.ofHours(3)))
            .expectSubscription()
            .expectNoEvent(Duration.ofHours(2))
            .thenAwait(Duration.ofHours(1))
            .expectNextCount(1)
            .expectComplete()
            .verify();
```

在 `Supplier` 参数中提供 `Publisher`。然后通过调用 `thenAwait(Duration)` 或 `expectNoEvent(Duration)` 来推进时间：

- `thenAwait(Duration)` 只是推进时间
- `expectNoEvent(Duration)` 除了推进时间，还要求这段时间内没有时间，否则测试失败

即使没有推进时间，至少有一个 subscription 事件，因此在 `.withVirtualTime()` 后至少要加一个 `expectSubscription()`，才能添加 `expectNoEvent()`。

示例：每秒 1 个元素，3600 个元素。

```java
Supplier<Fulx<Long>> supplier = ()->Flux.interval(Duration.ofSeconds(1)).take(3600);
StepVerifier.withVirtualTime(supplier)
        .thenAwait(Duration.ofHours(1))
        .expectNextCount(3600)
        .verifyComplete();
```

## 9. Adapt

RxJava3 和 Reactor 3 都实现了 Reactive Streams 规范，两者可以交互。

两个库都提供从 `Publisher` 创建的工厂方法，因此转换很容易。

- Reactor `Flux` 转换为 RxJava `Flowable`

```java
Flowable<User> flowable = Flowable.fromPublisher(flux)
```

- 从 `Flowable` 到 `Flux`

```java
Flux<User> flux = Flux.from(flowable);
```

- 从 `Flux` 到 `Observable`

```java
Observable<User> observable = Observable.fromPublisher(flux);
```

- 从 `Observable` 到 `Flux`

```java
Flux<User> flux = Flux.from(observable.toFlowable(BackpressureStrategy.BUFFER));
```

这里需要定义反压策略，因为 RxJava 3 `Observable` 不支持反压。

- 从 `Mono` 到 `Single`

```java
Single<User> single = Single.fromPublisher(mono);
```

- 从 `Single` 到 `Mono`

```java
Mono<User> mono = Mono.from(single.toFlowable());
```

- 从 `Mono` 到 Java 8 `CompletableFuture`

```java
CompletableFuture<User> future = mono.toFuture();
```

- 从 `CompletableFuture` 到 `Mono`

```java
Mono<User> mono = Mono.fromFuture(future);
```

## 10. FAQ

### 理解 marble diagram

marble diagram 以直观的方式解释 operator 的行为，下面介绍如何理解这些图。

有些 operator 为实例方法：它们通过 source `Flux` 的实例调用，如 `Flux<T> output = source.fluxOperator()`：

<img src="./images/image-20250319152709274.png" alt="image-20250319152709274" style="zoom:50%;" />

其它 operator 为 static 方法。它们以 source 为输入参数。例如 `Flux<T> output = Flux.merge(sourceFlux1, sourcePublisher2)`，它们表示如下：

<img src="./images/image-20250319152833782.png" alt="image-20250319152833782" style="zoom:50%;" />

有时需要根据 operator 的输入来表示多个行为，此时只有一个 operator "box"，但是 source 和 output 分开：

<img src="./images/image-20250319153007495.png" alt="image-20250319153007495" style="zoom:50%;" />

这些就是基本情况。有些 operator 更复杂一点。

例如，`ParallelFlux` 创建多个轨道，因此它们具有多个输出 `Flux`。如下所示：

<img src="./images/image-20250319153146484.png" alt="image-20250319153146484" style="zoom:50%;" />

window-operator 会生成 `Flux<Flux<T>>`：main `Flux` 通知每个 window 的打开，而内部 `Flux` 表示 window 的内容和终止。window 表示为 main `Flux` 的分支，如下：

<img src="./images/image-20250319153325611.png" alt="image-20250319153325611" style="zoom:50%;" />

有时，operator 会将 companion-publisher 作为输入。此类 companion-publisher 有助于自定义 operator 的行为，它会使用 companion 的信号触发自身内部的行为。表示如下：

<img src="./images/image-20250319153531223.png" alt="image-20250319153531223" style="zoom:50%;" />

以上介绍了常见的 operator 模式，下面展示一下 `Flux` 或 `Mono` 中可能发出的不同信号、事件和元素的图形：

<img src="./images/image-20250319153701683.png" alt="image-20250319153701683" style="zoom:50%;" />

最后是副作用的图形表示：

<img src="./images/image-20250319153821701.png" alt="image-20250319153821701" style="zoom:50%;" />

### 何时使用 Reactor

与普通同步 java 代码相比，创建 Reactor 的 `Publisher` Flux/Mono 具有明显的性能开销。Reactor 主要用于处理异步调用，对非异步调用不需要用 Reactor。

> [!WARNING]
>
> 创建 Reactor 的开销大概是 for 循环的 5 倍。因此，只在需要时使用 Reator，Reactor 用于异步调用，因此仅在需要异步调用时使用 Reactor。

## 参考

- https://projectreactor.io/docs/core/release/reference/gettingStarted.html
- https://gist.github.com/Lukas-Krickl/50f1daebebaa72c7e944b7c319e3c073
- https://projectreactor.io/learn
- https://github.com/schananas/practical-reactor