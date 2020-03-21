- [简介](#%e7%ae%80%e4%bb%8b)
- [Junit 5 快速入门](#junit-5-%e5%bf%ab%e9%80%9f%e5%85%a5%e9%97%a8)
  - [相对 Junit 4的改变](#%e7%9b%b8%e5%af%b9-junit-4%e7%9a%84%e6%94%b9%e5%8f%98)
  - [语法](#%e8%af%ad%e6%b3%95)
  - [命名](#%e5%91%bd%e5%90%8d)
- [Writing Tests](#writing-tests)
  - [注释](#%e6%b3%a8%e9%87%8a)
  - [断言](#%e6%96%ad%e8%a8%80)
  - [参数化测试](#%e5%8f%82%e6%95%b0%e5%8c%96%e6%b5%8b%e8%af%95)
    - [MethodSource](#methodsource)
  - [重复测试](#%e9%87%8d%e5%a4%8d%e6%b5%8b%e8%af%95)
- [运行测试](#%e8%bf%90%e8%a1%8c%e6%b5%8b%e8%af%95)
  - [IDE 支持](#ide-%e6%94%af%e6%8c%81)
- [扩展模型](#%e6%89%a9%e5%b1%95%e6%a8%a1%e5%9e%8b)
  - [注册扩展](#%e6%b3%a8%e5%86%8c%e6%89%a9%e5%b1%95)
    - [声明式注册](#%e5%a3%b0%e6%98%8e%e5%bc%8f%e6%b3%a8%e5%86%8c)
- [高级话题](#%e9%ab%98%e7%ba%a7%e8%af%9d%e9%a2%98)
- [从 JUnit 4 迁移](#%e4%bb%8e-junit-4-%e8%bf%81%e7%a7%bb)
  - [迁移技巧](#%e8%bf%81%e7%a7%bb%e6%8a%80%e5%b7%a7)
- [Junit 5 新特性 - 基础篇](#junit-5-%e6%96%b0%e7%89%b9%e6%80%a7---%e5%9f%ba%e7%a1%80%e7%af%87)
  - [@DisplayName](#displayname)
  - [@Nested](#nested)
- [API 演变](#api-%e6%bc%94%e5%8f%98)
  - [API 版本和状态](#api-%e7%89%88%e6%9c%ac%e5%92%8c%e7%8a%b6%e6%80%81)
- [Reference](#reference)
# 简介
JUnit 5 包含多个模块：

**Junit 5** = ***JUnit Platform*** + ***JUnit Jupiter*** + ***JUnit Vintage***

**JUnit Platform**

JUnit Platform 是在JVM启动测试框架的基础：
- 定义了用于开发测试框架的 `TestEngine` API；
- 定义了 Console Launcher，用于在命令行启动测试；
- JUnit 4 运行框架；

**JUnit Jupiter**

JUnit Jupiter 包含 JUnit 5 用于编写单元测试的最新的编程模型及扩展。

**JUnit Vintage**

JUnit Vintage 提供了运行 JUnit 3 和 JUnit 4测试的平台。

# Junit 5 快速入门

## 相对 Junit 4的改变
## 语法

- 测试方法 `public` 不是必须的，不过依然不能是 `static` 或 `private`。
- ~~timeout~~ 和 ~~expected~~ 功能从 `@Test` 移除

## 命名
测试方法注释略有改变：

|Junit 4|Junit 5|
|-------|-------|
|@Before|@BeforeEach|
|@After|@AfterEach|
|@BeforeClass|@BeforeAll|
|@AfterClass|@AfterAll|
|@Ignore|@Disabled|
|@Category|@Tag|
|@RunWith|@ExtendWith|
|@Rule,@ClassRule|@ExtendWith|


***
注释代码演示：
```java
/**
 * Annotation @BeforeClass was replaced by @{@link BeforeAll}. Needs to be static.
 * Same for @AfterClass.
 */
@BeforeAll
static void beforeAll() {}

/**
 * Annotation @Before was replaced by @{@link BeforeEach}.
 * Same for @After.
 */
@BeforeEach
void beforeEach() {}

/**
 * Annotation @Ignore was replaced by @{@link Disabled}. Sounds less negative.
 * However a reason for the deactivation  will be printed.
 */
@Disabled
@Test
void disabledTest() {}

/**
 * JUnit 4's experimental @Category is now called {@link Tag}/{@link Tags}.
 */
@Tag("abc")
@Test
void taggedTest() {}
```

**`Assert` **和**`Assume`** 也被重新命名为 **`Assertions`** 和 **`Assumptions`**

# Writing Tests

## 注释
JUnit Jupiter 支持如下的注释，用于配置测试。

|注释|说明|
|---|---|
|`@Test`|标记测试方法，无任何属性|
|`@ParameterizedTest`|标记参数化测试。这类方法可被继承|
|`@RepeatedTest`|标记重复测试的测试模板方法。可继承|
|`@BeforeAll`|标记方法在 `@Test`, `@RepeatedTest`, `@ParameterizedTest` 和 `@TestFactory` 之前运行的方法，同 JUnit 4 的 `@BeforeClass`。可继承|
|`@Disabled`|禁用测试方法或类，同 JUnit 4 `@Ignore`|


## 断言
JUnit Jupiter 包含了 JUnit 4 所有的断言，针对 Java 8 lambdas 还额外添加了一些断言。所有的断言都是 `org.junit.jupiter.api.Assertions` 类中的静态方法：
```java
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofMinutes;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import example.domain.Person;
import example.util.Calculator;

import org.junit.jupiter.api.Test;

class AssertionsDemo {

    private final Calculator calculator = new Calculator();

    private final Person person = new Person("Jane", "Doe");

    @Test
    void standardAssertions() {
        assertEquals(2, calculator.add(1, 1));
        assertEquals(4, calculator.multiply(2, 2),
                "The optional failure message is now the last parameter");
        assertTrue('a' < 'b', () -> "Assertion messages can be lazily evaluated -- "
                + "to avoid constructing complex messages unnecessarily.");
    }

    @Test
    void groupedAssertions() {
        // In a grouped assertion all assertions are executed, and all
        // failures will be reported together.
        assertAll("person",
            () -> assertEquals("Jane", person.getFirstName()),
            () -> assertEquals("Doe", person.getLastName())
        );
    }

    @Test
    void dependentAssertions() {
        // Within a code block, if an assertion fails the
        // subsequent code in the same block will be skipped.
        assertAll("properties",
            () -> {
                String firstName = person.getFirstName();
                assertNotNull(firstName);

                // Executed only if the previous assertion is valid.
                assertAll("first name",
                    () -> assertTrue(firstName.startsWith("J")),
                    () -> assertTrue(firstName.endsWith("e"))
                );
            },
            () -> {
                // Grouped assertion, so processed independently
                // of results of first name assertions.
                String lastName = person.getLastName();
                assertNotNull(lastName);

                // Executed only if the previous assertion is valid.
                assertAll("last name",
                    () -> assertTrue(lastName.startsWith("D")),
                    () -> assertTrue(lastName.endsWith("e"))
                );
            }
        );
    }

    @Test
    void exceptionTesting() {
        Exception exception = assertThrows(ArithmeticException.class, () ->
            calculator.divide(1, 0));
        assertEquals("/ by zero", exception.getMessage());
    }

    @Test
    void timeoutNotExceeded() {
        // The following assertion succeeds.
        assertTimeout(ofMinutes(2), () -> {
            // Perform task that takes less than 2 minutes.
        });
    }

    @Test
    void timeoutNotExceededWithResult() {
        // The following assertion succeeds, and returns the supplied object.
        String actualResult = assertTimeout(ofMinutes(2), () -> {
            return "a result";
        });
        assertEquals("a result", actualResult);
    }

    @Test
    void timeoutNotExceededWithMethod() {
        // The following assertion invokes a method reference and returns an object.
        String actualGreeting = assertTimeout(ofMinutes(2), AssertionsDemo::greeting);
        assertEquals("Hello, World!", actualGreeting);
    }

    @Test
    void timeoutExceeded() {
        // The following assertion fails with an error message similar to:
        // execution exceeded timeout of 10 ms by 91 ms
        assertTimeout(ofMillis(10), () -> {
            // Simulate task that takes more than 10 ms.
            Thread.sleep(100);
        });
    }

    @Test
    void timeoutExceededWithPreemptiveTermination() {
        // The following assertion fails with an error message similar to:
        // execution timed out after 10 ms
        assertTimeoutPreemptively(ofMillis(10), () -> {
            // Simulate task that takes more than 10 ms.
            Thread.sleep(100);
        });
    }

    private static String greeting() {
        return "Hello, World!";
    }
}
```

## 参数化测试
`@ParameterizedTest` 需要 **junit-jupiter-params**.

参数化测试可以用不同参数运行一个测试多次。例如：
```java
@ParameterizedTest
@ValueSource(strings = { "racecar", "radar", "able was I ere I saw elba" })
void palindromes(String candidate) {
    assertTrue(StringUtils.isPalindrome(candidate));
}
```
`@ParameterizedTest` 用于标记参数化测试方法；
`@ValueSource` 用于提供参数。

### MethodSource
采用 `@MethodSource` 可以引用测试类或外部类的工厂方法提供参数。
- 测试类中的工厂方法必须为 `static`，除非该类包含 `@TestInstance(Lifecycle.PER_CLASS)` 注释；
- 测试类外部的工厂方法也必须为 `static`.
- 工厂方法不能包含任何参数。

每个工厂方法生成一个参数流，每个参数作为 `@ParameterizedTest` 方法参数。

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

如果在 `@MethodSource` 中没有明确提供工厂方法名称，JUnit 会搜索和当前测试方法相同的工厂方法：
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

对基础类型的支持：
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

对多参数的支持，则必须返回 `Arguments` 实例集合：
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

另外，静态工厂方法可以通过完全的名称引用：
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

## 重复测试
以 `@RepeatedTest` 标记方法可以重复运行一个测试方法多次。例如，重复运行10次：
```java
@RepeatedTest(10)
void repeatedTest() {
    // ...
}
```

# 运行测试
## IDE 支持


# 扩展模型
和 JUnit 4 中相互冲突的的 `Runner`, `TestRule`, `MethodRule` 不同，JUnit Jupiter 扩展模型包含单个一致的 `Extension` API。

## 注册扩展
注册方式：
- 声明式的 `@ExtendWith`
- 编程式的 `@RegisterExtension`
- 自动的 `ServiceLoader` 机制

### 声明式注册
可以通过

# 高级话题

# 从 JUnit 4 迁移

## 迁移技巧
在迁移时需要注意以下方面：
- `@RunWith` 被 `@ExtendWith` 替代

# Junit 5 新特性 - 基础篇
## @DisplayName
用于描述测试的具体目的：
```java
@Test
@DisplayName("Choose a display name")
void displayNameTest() {}
```

## @Nested

# API 演变
JUnit 5 的主要目标是在维护 JUnit 的同时能够添加新的功能。JUnit 4 由于内部添加了过多的东西，导致维护困难。所以 JUnit 5对所有的公有接口、类和方法引入了生命周期。

## API 版本和状态
发布的版本号为 `<major>.<minor>.<patch>`,



# Reference
- https://junit.org/junit5/docs/current/user-guide/