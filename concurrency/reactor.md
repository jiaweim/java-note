# Reactor

2025-03-12 add: Reactive 编程简介
@author Jiawei Mao

***
## 1. 简介

Reactor 是 JVM 非阻塞 reactive 编程基础，具有高效的需求管理。它直接与 Java 8 函数式 API 集成，尤其是 `CompletableFuture`, `Stream` 和 `Duration`。它提供了可组合的异步序列 API：`Flux` (用于  N 个元素)和 `Mono` (用于 0 或 1 个元素)，比广泛实现 [Reactive Streams](https://www.reactive-streams.org/) 规范。

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

## 2. Reactive 编程简介

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

## 3. Reactor 核心功能

Reactor 项目的主要 artifact 为 reactor-core，这是一个实现 Reactive Streams 规范基于 Java 8 实现的 reactive 库。

Reactor 引入了可组合的 reactive 类型，这些类型实现 `Publisher`，同时提供了丰富的操作符，`Flux` 和 `Mono`：

- `Flux` 对象表示 0 到 N 个元素的 reactive 序列
- `Mono` 对象表示 0 或 1 个元素

这种区别带入一些语义信息，指示异步处理的大致基数。例如，一个 HTTP 请求仅生成一个响应，因此执行 `count` 操作没有意义。因此，将此类 HTTP 调用的结果表示为 `Mono<HttpResponse>` 比表示为 `Flux<HttpResponse>` 更有意义，`Mono` 仅提供与 0 或1 项元素相关的操作符。

更改处理最大基数的操作符会切换到相应类型。例如，如果 `Flux` 中存在 `count` 运算符，它会返回 `Mono<Long>`。

### Flux 

下图展示 `Flux` 如何转换元素：

<img src="./images/image-20250312191401497.png" alt="image-20250312191401497" style="zoom: 50%;" />

`Flux<T>` 是标准的 `Publisher<T>`，表示一个异步序列，可以发出 0 到 N 个元素，然后完成（成功或失败）。与 Reactive Streams 规范一样，这三种类型的信号会转换为对下游订阅者的 `onNext`, `onComplete` 和 `onError` 方法的调用。

由于可能的信号范围很大，所以 `Flux` 是一种通用的 reactive 类型。所有事件（包括终止事件）都是可选的：

- 没有 `onNext` 但有 `onComplete` 事件代表一个空序列，删除 `onComplete` 则得到一个无限空序列（除了测试取消操作，没有其它 用处）
- `Flux.interval(Duration)` 生成一个无限的 `Flux<Long>`，从时钟发出规则的 ticks

### Mono

下图展示 `Mono` 如何转换一个元素：

<img src="./images/image-20250313091254356.png" alt="image-20250313091254356" style="zoom: 50%;" />

`Mono<T>` 是 一个 `Publisher<T>` 实现，它通过 `onNext` 最多发射一个元素，然后以 `onComplete` 信号终止（成功的 `Mono`），如果失败则仅发射一个 `onError` 信号。

大多数 `Mono` 实现在调用 `onNext` 后立即在 `Subscriber` 上调用 `onComplete`。`Mono.never()` 例外：它不发出任何信号，这在技术上是可行的，不过仅在测试中有用。另外，`onNext` 和 `onError` 的组合是明确禁止的。

`Mono` 仅提供 `Flux` 的部分操作符，并且某些操作符，尤其是将 `Mono` 与另一个 `Publisher` 组合起来的操作符会切换到 `Flux`。例如，`Mono#concatWith(Publisher)` 返回 `Flux`，而 `Mono#then(Mono)` 返回另一个 `Mono`。

另外，可以使用 `Mono` 表示无返回值的异步进程，类似 `Runnable`，创建空的 `Mono<Void>` 即可。

### 创建 Flux 或 Mono 并订阅的简单方法

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

在订阅方面，`Flux` 和 `Mono` 使用 Java 8 lambda。`.subscribe()` 有许多版本，这些版本采用 lambda 来实现不同的 callback 组合，如下所示：

```java
subscribe(); // ①

subscribe(Consumer<? super T> consumer); // ②

subscribe(Consumer<? super T> consumer,
          Consumer<? super Throwable> errorConsumer); // ③

subscribe(Consumer<? super T> consumer,
          Consumer<? super Throwable> errorConsumer,
          Runnable completeConsumer); // ④

subscribe(Consumer<? super T> consumer,
          Consumer<? super Throwable> errorConsumer,
          Runnable completeConsumer,
          Consumer<? super Subscription> subscriptionConsumer); // ⑤
```

1. 订阅并触发序列
2. 对生成的每个值执行某些操作
3. 处理值，同时处理错误
4. 处理值和错误，并在序列成功完成时运行一些代码
5. 处理值和错误以及成功完成时运行一些代码，同时对此订阅调用产生的订阅执行某些操作

> [!TIP]
>
> 这些不同版本均返回对订阅的引用，当不需要更多数据时，可以使用该引用取消订阅。取消后，source 应停止生成值并清理其创建的任何资源。这种取消和清理行为在 Reactor 由通用的 `Disposable` 接口表示。

#### subscribe 示例

下面介绍 `subscribe` 的 5 个版本的最简单示例。

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

下面通用定义 `onNext`, `onError` 和 `onComplete` 等相关事件来创建 `Flux` 和 `Mono`。所有这些方法都通过 API 触发称为 sink 的事件。

#### 同步 generate

通过编程创建 `Flux` 的最简单形式是调用 `generate` 方法通过 generator 函数生成 `Flux`。

这是一个同步的一对一 push 元素，即 sink 类型为 `SynchronousSink`，且每次 callback 最多调用一次 `next()`。也可以另外调用 `error()` 或 `complete()`，但是可选的。

最有用的版本

## Publish Subscribe 模式

问题：多个具有依赖关系的并发执行路径，还需要共享数据。同步是最基本的问题，解决同步有许多方案，publish-subscibe 模式追求的是 1:N 关系。

该模式有两个对象：一个 publisher，负责生产数据；许多 subscriber，负责消费数据

- subscriber 可以订阅 publisher，表示对其数据感兴趣
- publisher 在准备好数据时，可以通知所有 subscribers

与其它同步机制相比，这种模式可以同时发生，无需等待，并基于推送（push）的机制进行交流。subscriber 不需要刻意等待数据到达，只需定义好数据到达时要执行的操作，然后做其它工作。反之亦然，publisher 也不用等待 subsciber 准备好接收数据，而是直接推送给他。

### Publisher

#### 生命周期

Reactor 中对 publisher 有两个实现：`Flux` 和 `Mono`。从数据源创建 publisher 后，可以添加 operator-chain 处理数据，然后传递给 subscriber。因此，publisher 的生命周期有三个阶段：

- 组装
- 订阅
- 执行

在组装阶段，创建 publisher 并定义 operator-chain。

## 注意事项

与普通同步 java 代码相比，创建 Reactor 的 `Publisher` Flux/Mono 具有明显的性能开销。Reactor 主要用于处理异步调用，对非异步调用不需要用 Reactor。

> [!WARNING]
>
> 创建 Reactor 的开销大概是 for 循环的 5 倍。

## 参考

- https://projectreactor.io/docs/core/release/reference/gettingStarted.html
- https://gist.github.com/Lukas-Krickl/50f1daebebaa72c7e944b7c319e3c073