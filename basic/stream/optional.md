# Optional

- [Optional](#optional)
  - [1. 简介](#1-简介)
  - [2. 备选值](#2-备选值)
  - [3. 使用 Optional 值](#3-使用-optional-值)
  - [4. pipeline Optional 值](#4-pipeline-optional-值)
  - [5. 不使用 Optional](#5-不使用-optional)
  - [6. 创建 Optional](#6-创建-optional)
  - [7. 使用 flatMap 连续调用](#7-使用-flatmap-连续调用)
  - [8. 将 Optional 转换为 Stream](#8-将-optional-转换为-stream)

2023-11-22, 19:48
@author Jiawei Mao
****

## 1. 简介

`Optional<T>` 用于封装对象，以更安全的方式表示可能缺失的引用对象，以替代 `null` 值。不过其是否更安全，还在于能否正确使用它。

## 2. 备选值

`Optional` 的核心：值不存在时生成备选值，值存在时使用该值。

下面查看 `Optional` 生成备选值的方法。

- 字符串没有时，返回空字符串

```java
String result = optionalString.orElse("");
```

- 使用 lambda 表达式计算默认值

```java
String result = optionalString.orElseGet(() ->
        System.getProperty("myapp.default"));
```

- 没有值时抛出异常

```java
String result = optionalString.orElseThrow(IllegalStateException::new);
```

可选值：

- `Optional.orElse()` 设置默认值
- `Optional.orElseGet()` 计算默认值
- `Optional.orElseThrow` 缺失使抛出异常

## 3. 使用 Optional 值

`ifPresent` 参数为函数类型，当可选值存在时，将其传递给改函数；否则不执行任何操作。

```java
optionalValue.ifPresent(v -> Process v);
```

**示例**：将存在的值添加到 set

```java
optionalValue.ifPresent(v -> results.add(v));
```

或者：

```java
optionalValue.ifPresent(results::add);
```

如果希望 Optional 有值时执行一个操作，没有时指定另一个操作，使用 `ifPresentOrElse`：

```java
optionalValue.ifPresentOrElse(
        v -> System.out.println("Found " + v),
        () -> logger.warning("No match"));
```

执行操作：

- `Optional.ifPresent` 可选值存在时，执行操作
- `Optional.ifPresentOrElse` 有值时执行一个操作，没有时执行另一个操作

## 4. pipeline Optional 值

`map` 使用指定函数对 Optional 的值进行操作，但依然返回 `Optional` 类型。

```java
Optional<String> transformed =
        optionalString.map(String::toUpperCase);
```

如果 `optionalString` 为空，则 `transformed` 也为空。

**示例：** 

```java
optionalValue.map(results::add);
```

`optionalValue` 有值时将值添加到 `results` list，没有时不执行任何操作。

!!! note
    `map` 方法与 `Stream` 接口的 `map` 方法类似。可以将 `Optional` 看作尺寸为 0 或 1 的 `Stream`，结果的尺寸也是 0 或 1，为 1 时应用函数。

类似地，可以使用 `filter` 方法筛选满足指定条件的 `Optional`。如果不满足，产生空结果。

```java
Optional<String> transformed = optionalString
        .filter(s -> s.length() >= 8)
        .map(String::toUpperCase);
```

也可以通过 or 方法为空的 Optional 提供备选 Optional：

```java
Optional<String> result = optionalString.or(() -> // Supply an Optional 
   alternatives.stream().findFirst());
```

如果 `optionalString` 有值，`result` 为 `optionalString`；否则结果为 lambda 表达式的结果。

总结：

- `<U> Optional<U> map(Function<T, U> mapper)`：当 `Optional` 有值，将值传入 `mapper` 函数，返回包含计算结果的 `Optional`；如果 `Optional` 没有值，返回空 `Optional`。
- `Optional<T> filter(Predicate<T> predicate)`：当 `Optional` 有值且满足 `predicate`，返回包含该值的 `Optional`，否则返回空 `Optional`
- `Optional<T> or(Supplier<? extends Optional<T>> supplier)`：当 `Optional` 有值，返回该 `Optional`；否则返回 `supplier` 生成的 `Optional`

## 5. 不使用 Optional

不正确使用 `Optional`，与使用 `null` 没差别。

`get` 返回 `Optional` 封装的值，没有值抛出 `NoSuchElementException`。因此：

```java
Optional<T> optionalValue = . . .; 
optionalValue.get().someMethod()
```

不比下面的方式安全：

```java
T value = . . .; 
value.someMethod();
```

`isPresent` 和 `isEmpty` 方法用于判断 `Optional<T>` 是否有值：

```java
if (optionalValue.isPresent()) 
optionalValue.get().someMethod();
```

但是使用起来也不比下面简洁：

```java
if (value != null) value.someMethod();
```

!!! note
    Java 10 加入了 `orElseThrow` 方法，当调用 `optionalValue.orElseThrow().someMethod()` 时，如果 optionalValue 没有值，会抛出异常。如果你确信有值，建议使用该方法。

`Optional` 使用建议：

- `Optional` 类型变量永远不要为 `null`。
- 不要将 `Optional` 作为字段。在 class 内部，缺失字段使用 `null`。`Optional` 字段无法序列化，还有额外开销。
- 不要将 `Optional` 作为方法参数。如果有参数缺失情况，建议用重载方法。但是 Optional 很适合作为返回类型。
- 不要在 set 中使用 `Optional`，也不用将其作为 map 的 key。

## 6. 创建 Optional

`Optional.of(result)` 和 `Optional.empty()` 可用于创建 `Optional`。例如：

```java
public static Optional<Double> inverse(Double x) 
{ 
   return x == 0 ? Optional.empty() : Optional.of(1 / x); 
}
```

`Optional.ofNullable(obj)`：如果 `obj` 不为 `null`，返回 `Optional.of(obj)`，否则返回 `Optional.empty()`。

## 7. 使用 flatMap 连续调用

`flatMap`：有值则调用方法，无值则返回空 `Optional`。

假设方法 `f` 返回 `Optional<T>`，其中类型 `T` 的方法 `g` 返回 `Optional<U>`。因为 `f` 返回的 `Optional<T>`，所以不能连续调用 `s.f().g()`。此时可以使用 `flatMap`：

```java
Optional<U> result = s.f().flatMap(T::g);
```

如果 `s.f()` 有值，则调用 `g`，否则返回空的 `Optional<U>`。

多个返回 `Optional` 的方法或 lambda 表达式可以通过 flatMap 串联使用。所有步骤都成功才会返回非空的 `Optional`。

例如，实现一个 `squareRoot` 方法：

```java
public static Optional<Double> squareRoot(Double x) 
{ 
   return x < 0 ? Optional.empty() : Optional.of(Math.sqrt(x)); 
}
```

将其与 `reverse` 串联使用：

```java
Optional<Double> result = inverse(x).flatMap(MyMath::squareRoot);
```

或者：

```java
Optional<Double> result = Optional.of(-4.0)
        .flatMap(Demo::inverse)
        .flatMap(Demo::squareRoot);
```

如果 `inverse` 或 `squareRoot` 返回 `Optional.empty()`，则结果为 `Optional.empty()`。

## 8. 将 Optional 转换为 Stream

`stream` 方法将 `Optional<T>` 转换为包含 0 或 1 个元素的 `Stream<T>`。

当方法返回 `Optional` 时可以用到。假设有一个 ID `Stream`，并且有一个以 ID 为参数的方法：

```java
Optional<User> lookup(String id)
```

那么，如何获得 `User` Stream，并且跳过无效的 ID。

可以过滤掉无效 ID，然后用 get 返回余下内容：

```java
Stream<String> ids = . . .; 
Stream<User> users = ids.map(Users::lookup) 
   .filter(Optional::isPresent) 
   .map(Optional::get);
```

但是这里使用了 `isPresent` 和 `get` 这两个不建议的方法。使用 `flatMap` 则更优雅：

```java
Stream<User> users = ids.map(Users::lookup) 
   .flatMap(Optional::stream);
```

`Optional.stream` 返回包含 0 或 1 个元素的 Stream。`flatMap` 将这些元素收集起来，丢掉没有值的 `Optional`。

如果调用遗留代码，返回 `Users.classicLookup(id)` 对无效 id 返回 `null`，而不是 `Optional<User>`，此时可以过滤掉 null 值：

```java
Stream<User> users = ids.map(Users::classicLookup) 
   .filter(Objects::nonNull);
```

使用 `flatMap` 也可以：

```java
Stream<User> users = ids.flatMap( 
   id -> Stream.ofNullable(Users.classicLookup(id)));
```

或者：

```java
Stream<User> users = ids.map(Users::classicLookup) 
   .flatMap(Stream::ofNullable);
```

如果 `obj` 为 `null`，`Stream.ofNullable(obj)` 返回空 `stream`，否则返回包含 `obj` 的 `stream`。

