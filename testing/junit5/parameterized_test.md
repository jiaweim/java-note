# 参数化测试

- [参数化测试](#参数化测试)
  - [简介](#简介)
  - [配置](#配置)
  - [Consuming Arguments](#consuming-arguments)
  - [Sources of Arguments](#sources-of-arguments)
    - [@ValueSource](#valuesource)
    - [Null 和 Empty Sources](#null-和-empty-sources)
    - [@MethodSource](#methodsource)


## 简介

参数化测试可以用不同参数多次运行一个测试。其声明方式与普通的 @Test 方法类似，但使用了 @ParameterizedTest 注解。此外，还需要指定一个源，为每次调用提供参数。

**示例：** 使用 `@ValueSource` 注解指定一个字符串数组为参数源。

```java
@ParameterizedTest
@ValueSource(strings = { "racecar", "radar", "able was I ere I saw elba" })
void palindromes(String candidate) {
    assertTrue(StringUtils.isPalindrome(candidate));
}
```

说明

- `@ParameterizedTest` 用于标记参数化测试方法
- `@ValueSource` 用于提供参数

在执行上述参数化测试时，JUnit 会分别报告每个调用情况。例如，ConsoleLauncher 会输出类似如下内容：

```
palindromes(String) ✔
├─ [1] candidate=racecar ✔
├─ [2] candidate=radar ✔
└─ [3] candidate=able was I ere I saw elba ✔
```

## 配置

`@ParameterizedTest` 需要 **junit-jupiter-params** 模块。

## Consuming Arguments

参数化测试方法通常使用配置参数直接从源

## Sources of Arguments

Junit Jupiter 提供了许多源注解。

### @ValueSource

`@ValueSource` 是最简单的参数源。它允许指定单个字面量数组，只能为参数化测试提供一个参数。

`@ValueSource` 支出如下类型的字面量：

- short
- byte
- int
- long
- float
- double
- char
- boolean
- java.lang.String
- java.lang.Class

例如，下面的 `@ParameterizedTest` 方法会使用值 1，2 和 3 调用三次：

```java
@ParameterizedTest
@ValueSource(ints = { 1, 2, 3 })
void testWithValueSource(int argument) {
    assertTrue(argument > 0 && argument < 4);
}
```

### Null 和 Empty Sources

当输入错误时，为了检查边界情况，并验证软件行为是否正确，将 null 和空值输入参数化测试很有用。

下面的注解用作接受单个参数的参数化测试，提供 null 和空置。

- `@NullSource` 为 `@ParameterizedTest` 方法提供一个 null 参数。

@NullSource 不能用于参数为基本类型的方法。

- `@EmptySource` 为 `@ParameterizedTest` 方法提供一个空参数，支持以下类型：

`java.lang.String`, `java.util.Collection` (包括具有 public 无参构造函数的具体子类), `java.util.List`, `java.util.Set`, `java.util.SortedSet`, `java.util.NavigableSet`, `java.util.Map` (包括具有 public 无参构造函数的具体子类), `java.util.SortedMap`, `java.util.NavigableMap`, 基本类型数组 (e.g., `int[]`, `char[][]`, etc.), 对象数组 (e.g., `String[]`, `Integer[][]`, etc.).

- `@NullAndEmptySource` 合并 `@NullSource` 和 `@EmptySource` 的功能

如果需要提供多种不同类型的空白字符串进行参数化测试，可以使用 `@ValueSource`，例如 `@ValueSource(strings = {" ", "   ", "\t", "\n"})`。

还可以组合使用 @NullSource, @EmptySource 和 @ValueSource 在更广范围测试 null, empty 和 blank 输入。

**示例：** 为字符串参数实现 @NullSource, @EmptySource 和 @ValueSource 的组合参数化测试

```java
@ParameterizedTest
@NullSource
@EmptySource
@ValueSource(strings = { " ", "   ", "\t", "\n" })
void nullEmptyAndBlankStrings(String text) {
    assertTrue(text == null || text.trim().isEmpty());
}
```

### @MethodSource

采用 `@MethodSource` 可以引用测试类或外部类的一个或多个工厂方法提供参数：

- 测试类中的工厂方法必须为 `static`，除非该类包含 `@TestInstance(Lifecycle.PER_CLASS)` 注解
- 测试类外部的工厂方法也必须为 `static`
- 工厂方法不能包含任何参数。

每个工厂方法生成一个参数流，流中的每组参数作为 `@ParameterizedTest` 方法参数。

这个流可以是任意可以转换为流的对象，如 `Stream`, `DoubleStream`, `LongStream`, `IntStream`, `Collection`, `Iterator`, `Iterable`, 数组等。

如果只需要**一个参数**，如下：

```java
@ParameterizedTest
@MethodSource("stringProvider")
void testWithExplicitLocalMethodSource(String argument) {
    assertNotNull(argument);
}

static Stream<String> stringProvider() {
    return Stream.of("apple", "banana");
}
```

如果在 `@MethodSource` 中没有指定工厂方法名称，JUnit 会搜索和当前 `@ParameterizedTest` 测试方法相同的工厂方法：

```java
@ParameterizedTest
@MethodSource
void testWithDefaultLocalMethodSource(String argument) {
    assertNotNull(argument);
}

static Stream<String> testWithDefaultLocalMethodSource() {
    return Stream.of("apple", "banana");
}
```

对基础类型的支持（`DoubleStream`, `IntStream`, `LongStream`）：

```java
@ParameterizedTest
@MethodSource("range")
void testWithRangeMethodSource(int argument) {
    assertNotEquals(9, argument);
}

static IntStream range() {
    return IntStream.range(0, 20).skip(10);
}
```

- 对**多参数**，则必须返回 `Arguments` 实例集合、Stream 或数组

```java
@ParameterizedTest
@MethodSource("stringIntAndListProvider")
void testWithMultiArgMethodSource(String str, int num, List<String> list) {
    assertEquals(5, str.length());
    assertTrue(num >=1 && num <=2);
    assertEquals(2, list.size());
}

static Stream<Arguments> stringIntAndListProvider() {
    return Stream.of(
        arguments("apple", 1, Arrays.asList("a", "b")),
        arguments("lemon", 2, Arrays.asList("x", "y"))
    );
}
```

`arguments(Object…​)` 和 `Arguments.of(Object…​)` 等价。

- 静态工厂方法可以通过完全限定名称引用

```java
package example;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ExternalMethodSourceDemo {

    @ParameterizedTest
    @MethodSource("example.StringsProviders#tinyStrings")
    void testWithExternalMethodSource(String tinyString) {
        // test with tiny string
    }
}

class StringsProviders {

    static Stream<String> tinyStrings() {
        return Stream.of(".", "oo", "OOO");
    }
}
```

工厂方法可以声明参数，这些参数将由注册的 `ParameterResolver` 扩展 API 实现。

**示例：** 下面的工厂方法通过名称引用，因为测试类只有一个该名称的方法。

如果有多个同名的本地方法，还需要提供参数来区分，例如 `@MethodSource("factoryMethod()")` 或 `@MethodSource("factoryMethod(java.lang.String)")`。另外，工厂方法也可以通过它的完全限定名来引用，例如 `@MethodSource("example.MyTests#factoryMethod(java.lang.String)")`。

```java
@RegisterExtension
static final IntegerResolver integerResolver = new IntegerResolver();

@ParameterizedTest
@MethodSource("factoryMethodWithArguments")
void testWithFactoryMethodWithArguments(String argument) {
    assertTrue(argument.startsWith("2"));
}

static Stream<Arguments> factoryMethodWithArguments(int quantity) {
    return Stream.of(
            arguments(quantity + " apples"),
            arguments(quantity + " lemons")
    );
}

static class IntegerResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
            ExtensionContext extensionContext) {

        return parameterContext.getParameter().getType() == int.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext,
            ExtensionContext extensionContext) {

        return 2;
    }
}
```