# Reactor 测试

2025-03-24 ⭐
@author Jiawei Mao
***
## 简介

`StepVerifier` 来自 reactor-test artifiact，它能够订阅任何 `Publisher`，如 `Flux`, Akka Stream 等，然后针对该序列断言。

添加依赖项：

```xml
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-test</artifactId>
    <version>xxx</version>
</dependency>
```

reactor-test 的三个主要用途：

- `StepVerifier`：分步测试一个序列是否满足给定方案；
- `TestPublisher`：生成数据测试下游 operators 的行为
- `PublisherProbe`：测试可能包含多个 `Publisher` 的序列（如包含 `switchIfEmpty` operator 的 chain）

## StepVerifier

测试 Reactor 序列最常见的情况是：定义 `Flux` 或 `Mono`，测试在订阅时的行为。

使用 `StepVerifier` 可以逐步定义期望事件，验证：

- 下一个事件是什么？
- `Flux` emit 特定值：
- 或者接下来 300 ms 什么都不做？

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

**示例**：假设你定义了如下方法来装饰 `Flux`

```java
public <T> Flux<T> appendBoomError(Flux<T> source) {
	return source.concatWith(Mono.error(new IllegalArgumentException("boom")));
}
```

为了测试它，需要验证：`Flux` 首先 emit `thing1`,然后 emit `thing2`，然后生成错误，错误信息为 "boom"。

将其转换为 `StepVerifier`，如下：

```java
@Test
public void testAppendBoomError() {
  Flux<String> source = Flux.just("thing1", "thing2");// ➊

  StepVerifier.create( // ➋
    appendBoomError(source)) // ➌
    .expectNext("thing1") // ➍
    .expectNext("thing2")
    .expectErrorMessage("boom") // ➎
    .verify(); // ➏
}
```

➊ 由于待测试方法需要一个 `Flux`，所以这里定义了一个简单的 `Flux`

➋ 创建一个 `StepVerifier`，用于包装并验证 `Flux`

➌ 传入要测试的 `Flux`

➍ 期望在订阅后第一个信号是 `onNext`，值为 "thing1"

➎ 期望最后一个信号为 `onError`，异常信息为 "boom"

➏  通过 `verify()` 触发测试

该 API 为 builder 模式。从创建 `StepVerifier`，传入待测试序列开始。支持：

- 指定下一个信号的期望值。如果收到其他信号，或信号内容与期望值不一致，整个测试失败，抛出 `AssertionError`。
- consume 下一个信号。当需要跳过序列的一部分，或对信号内容执行自定义测试（例如，断言有下一个 `onNext` 事件，并且 emit 的是大小为 5 的 list），使用 `consumeNextWith(Consumer<T>)`
- 采取其它操作，如暂停或运行任意代码。如用 `thenAwait(Duration)` 和 `then(Runnable)` 操作 context

终端事件，调用如 `expectComplete()` 和 `expectError()` 等方法后，无法再设置期望。再最后，只能执行 `StepVerifier` 配置，然后触发验证，通过调用 `verify()` 或其变体。此时，`StepVerifier` 订阅待测试的 `Mono` 或 `Flux`，传播序列，在测试中验证每个信号。

> [!IMPORTANT]
>
> `verify()` 触发验证。另外还有几个方法合并终端期望与 `verify()`：`verifyComplete()`, `verifyError()`, `verifyErrorMessage(String)` 等。

`verify()` 及其派生方法默认没有超时（timeout），即可是无限期阻塞。可以使用 `StepVerifier.setDefaultTimeout(Duration)` 为这些方法设置全局 timeout，或者使用 `verify(Duration)`。


### 断言

- `Flux` 断言示例

**示例**：断言 `Flux` 依次生成 "foo" 和 "bar" 两个元素，然后完成。

```java
Flux<String> flux = Flux.just("foo", "bar");
StepVerifier.create(flux)
        .expectNext("foo")
        .expectNext("bar")
        .expectComplete()
        .verify();
```

还可以更简洁：

```java
StepVerifier.create(Flux.just("foo", "bar"))
        .expectNext("foo", "bar")
        .verifyComplete();
```

- 异常断言

**示例**：断言 `Flux` 依次 emit "foo" 和 "bar"，然后抛出 `RuntimeException`

```java
Flux<String> flux = Flux.just("foo", "bar")
        .concatWith(Mono.error(new RuntimeException()));
StepVerifier.create(flux)
        .expectNext("foo", "bar")
        .verifyError(RuntimeException.class);
```

- `expectNextMatches` 断言

`expectNextMatches` 可以检查元素是否满足指定 `Predicate`。例如：

断言第一个 `User` 的 userName 为 "swhite"，第二个为 "jpinkman"，然后完成。

`assertNext` 采用 testNG 进行断言，和使用 `expectNextMatches` 效果类似，但是更为方便。

```java
StepVerifier.create(flux)
        .expectNextMatches(user -> user.getUsername().equals("swhite"))
        .assertNext(user -> assertThat(user.getUsername()).isEqualTo("jpinkman"))
        .verifyComplete();
```

- assert 断言

```java
default StepVerifier.Step<T> assertNext(Consumer<? super T> assertionConsumer);
StepVerifier.Step<T> consumeNextWith(Consumer<? super T> consumer);
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

这个测试至少需要云信 10 秒。

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

**示例**：每秒 1 个元素，3600 个元素。

```java
Supplier<Fulx<Long>> supplier = ()->Flux.interval(Duration.ofSeconds(1)).take(3600);
StepVerifier.withVirtualTime(supplier)
        .thenAwait(Duration.ofHours(1))
        .expectNextCount(3600)
        .verifyComplete();
```

### request

反压（backpressure）是一种反馈机制，`Subscriber` 向 `Publisher` 发出信号，告知其能够处理多少数据，从而限制 `Publisher` 生成数据的速率。

这种需求控制在 `Subscription` 水平完成：每次调用 `subscribe()` 都会创建一个 `Subscription`，通过对 `Subscription` 的操作可以取消数据流，或使用 `request(long)` 调整数据需求。

`request(Long.MAX_VALUE)` 表示无限制需求，因此 `Publisher` 会尽可能快地生成数据。

`StepVerifier` 可以调整需求，通过使用相关参数为初始 request `create` 或 `withVirtualTime`，然后在期望中使用 `thenRequest(long)` 进一步 request。

**示例**：使用 `StepVerifier` 先 request 所有值，然后期望收到 4 个值。

```java
StepVerifier.create(flux)
        .expectNextCount(4)
        .expectComplete();
```

**示例**：一次 request 一个元素

初始 request 1 个元素；收到并断言第一个元素后，再 request 1 个元素

```java
StepVerifier.create(flux, 1)
        .expectNext(User.SKYLER)
        .thenRequest(1)
        .expectNext(User.JESSE)
        .thenCancel();
```

如果 request 数不够，source 无法 complete，除非直接取消。如果想确保在指定时间内没有传入信号，可以使用 `.expectTimeout(Duration)`。

### 识别失败测试

`StepVerifier` 提供两个确定测试失败位置的方法：

- `as(String)`：用在 `expect*` 方法后面，为断言添加描述信息。如果断言失败，错误信息会包含该描述信息。不过终端断言和 `verify` 不能用该方法。
- `StepVerifierOptions.create().scenarioName(String)`：使用 `StepVerifierOptions` 创建 `StepVerifier`，可以使用 `scenarioName()`  方法为整个测试命名，该名称也用在断言错误消息中。

## 时间

如果 `Flux` 每秒生成一个元素，需要生成 3600 个元素。测试该 `Flux` 需要一个小时，显然我们不希望测试这么久，那么如果加速测试，同时能够断言数据？`StepVerifier` 提供了一个虚拟时间选项 `StepVerifier.withVirtualTime(Supplier<Publisher>)`：定时 operators 的默认 `Scheduler` 为 `Schedulers.parallel()`，将其替换为 `VirtualTimeScheduler` ，即用可操作的虚拟时钟替换实际时钟。

`withVirtualTime` 的参数 `Supplier` 执行 lazy 初始化，确保 `Scheduler` 初始化完成后再长那就 `Flux`。

> [!IMPORTANT]
>
> 要确保 `Supplier<Publisher<T>>` 能以 lazy 的方式使用，否则无法保证虚拟时间。特定是要避免在测试代码之前实例化 `Flux`，然后在 `Supplier` 中直接返回该实例。而是应该在 `Supplier` 的 lambda 表达式中实例化。

有两个涉及时间的断言方法，不管有没有虚拟时间，它们都有效：

- `thenAwait(Duration)`：暂停验证一段时间（期间允许发生信号）
- `expectNoEvent(Duration)`：断言序列在一段时间内没有信号，否则测试失败

在经典模式下，两种方法都会暂停给定线程一段时间；在虚拟模式下则推进虚拟时钟。

> [!TIP]
>
> `expectNoEvent` 将 `subscription` 也视为事件，因此应该将 `expectNoEvent` 放在订阅后面，即 `expectSubscription().expectNoEvent(duration)`。

**示例**：虚拟时间示例

```java
StepVerifier.withVirtualTime(() -> Mono.delay(Duration.ofDays(1)))
    .expectSubscription() // ➊
    .expectNoEvent(Duration.ofDays(1)) // ➋
    .expectNext(0L) // ➌
    .verifyComplete(); // ➍
```

➊ 如上面 tip 所示

➋ 断言一整天没有事件

➌ 断言 emit 0

➍ 断言完成，并触发验证

这里也可以使用 `thenAwait(Duration.ofDays(1))`，不过 `expectNoEvent`  还可以保证期间不会发生任何其它事。

`verify()` 返回 `Duration`类型，这是整个测试实际所花时间。

> [!WARNING]
>
> 虚拟时间不是银弹。所有 `Scheduler` 被相同的 `VirtualTimeScheduler` 替代。在某些情况在可能锁定测试，因为虚拟时间没有向前移动，导致断言无法执行。大多时候，需要推进虚拟时钟以使序列 emit。对无限序列，虚拟时钟用处也不大。

## 执行后断言




## 参考

- https://projectreactor.io/docs/core/release/reference/testing.html