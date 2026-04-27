# 参数化测试


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

### @CsvSource

2026-04-09⭐

`@CsvSource` 以逗号分隔值的形式表示参数列表。通过 `@CsvSource` 的 `value` 属性提供的每个字符串对应一条 CSV 记录，并触发参数化测试类或测试方法的一次调用。第条记录可选择性地用作 CSV 表头（详情及示例请参阅 Javadoc 中关于 `useHeadersInDisplayName` 属性的说明）。

```java
@ParameterizedTest
@CsvSource({
	"apple,         1",
	"banana,        2",
	"'lemon, lime', 0xF1",
	"strawberry,    700_000"
})
void testWithCsvSource(String fruit, int rank) {
	assertNotNull(fruit);
	assertNotEquals(0, rank);
}
```

默认分隔符为逗号（,），但可以通过设置 `delimiter` 属性来使用其他字符。此外，通过 `delimiterString` 属性可以使用字符串作为分隔符，而非单个字符。但不能同时设置这两个分隔符属性。

默认情况下，`@CsvSource` 使用单引号（'）作为引号字符，不过可以通过 `quoteCharacter` 属性修改这一设置。参见上文示例以及下表中 `'lemon, lime'` 这一取值。一个带引号的空值（`''`）会生成空字符串，除非设置了 `emptyValue` 属性；而完全空白的取值则会被解析为 `null` 引用。通过指定一个或多个 `nullValues`，可以将自定义的值解析为 `null` 引用（参见下表中的 NIL 示例）。如果 `null` 引用对应的目标类型是基本数据类型，则会抛出 `ArgumentConversionException` 异常。

> [!NOTE]
>
> 无论是否通过 `nullValues` 属性配置了自定义值，**未加引号的空值都会被转换为 null 引用**。

除在引号字符串内部之外，CSV 列中的首尾空白字符默认会被去除。可以通过将 `ignoreLeadingAndTrailingWhitespace` 属性设为 `true` 来改变这一行为。

| Example Input                                                | Resulting Argument List       |
| :----------------------------------------------------------- | :---------------------------- |
| `@CsvSource({ "apple, banana" })`                            | `"apple"`, `"banana"`         |
| `@CsvSource({ "apple, 'lemon, lime'" })`                     | `"apple"`, `"lemon, lime"`    |
| `@CsvSource({ "apple, ''" })`                                | `"apple"`, `""`               |
| `@CsvSource({ "apple, " })`                                  | `"apple"`, `null`             |
| `@CsvSource(value = { "apple, banana, NIL" }, nullValues = "NIL")` | `"apple"`, `"banana"`, `null` |
| `@CsvSource(value = { " apple , banana" }, ignoreLeadingAndTrailingWhitespace = false)` | `" apple "`, `" banana"`      |

如果你所使用的编程语言支持 Java 文本块或类似的多行字符串字面量，你也可以使用 `@CsvSource` 的 `textBlock` 属性。文本块中的每一行对应一条 CSV 记录，并会触发参数化测试类或测试方法的一次调用。通过将 `useHeadersInDisplayName` 属性设为 `true`，可以选择将第一行用作 CSV 表头，如下例所示。

借助文本块，前面的示例可以按如下方式实现。

```java
@ParameterizedTest
@CsvSource(useHeadersInDisplayName = true, textBlock = """
	FRUIT,         RANK
	apple,         1
	banana,        2
	'lemon, lime', 0xF1
	strawberry,    700_000
	""")
void testWithCsvSource(String fruit, int rank) {
	// ...
}
```

上一示例中生成的显示名称包含 CSV 表头名称。

```
[1] FRUIT = "apple", RANK = "1"
[2] FRUIT = "banana", RANK = "2"
[3] FRUIT = "lemon, lime", RANK = "0xF1"
[4] FRUIT = "strawberry", RANK = "700_000"
```

与通过 `value` 属性提供的 CSV 记录不同，**文本块可以包含注释**。任何以 `commentCharacter` 属性值（默认为 `#`）开头的行，都会被当作注释并忽略。需要注意的是，这条规则有一个例外：如果注释字符出现在**带引号的字段内部**，它将失去特殊含义。

注释字符必须是该行的**第一个字符**，前面不能有任何空白。因此建议将文本块的结束分隔符（`"""`）放在最后一行输入的末尾，或者另起一行、与其他输入内容左对齐（如下方示例所示，其格式排版类似表格形式）。

```java
@ParameterizedTest
@CsvSource(delimiter = '|', quoteCharacter = '"', textBlock = """
	#-----------------------------
	#    FRUIT     |     RANK
	#-----------------------------
	     apple     |      1
	#-----------------------------
	     banana    |      2
	#-----------------------------
	  "lemon lime" |     0xF1
	#-----------------------------
	   strawberry  |    700_000
	#-----------------------------
	""")
void testWithCsvSource(String fruit, int rank) {
	// ...
}
```

> [!NOTE]
>
> Java 的文本块特性在编译代码时会**自动去除多余的附带空格**。但其他 JVM 语言（如 Groovy 和 Kotlin）则不会这样做。因此，如果你使用的不是 Java 语言，并且文本块中包含注释或带引号字符串内的换行，就必须确保文本块内部**没有前导空格**。

## 参考

- https://docs.junit.org/current/writing-tests/parameterized-classes-and-tests