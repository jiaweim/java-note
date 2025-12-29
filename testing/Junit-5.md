# Junit 5 快速入门

2025-06-23
@author Jiawei Mao
***
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

## 重复测试
以 `@RepeatedTest` 标记方法可以重复运行一个测试方法多次。例如，重复运行10次：
```java
@RepeatedTest(10)
void repeatedTest() {
    // ...
}
```

# 扩展模型
和 JUnit 4 中相互冲突的的 `Runner`, `TestRule`, `MethodRule` 不同，JUnit Jupiter 扩展模型包含单个一致的 `Extension` API。

## 注册扩展
注册方式：
- 声明式的 `@ExtendWith`
- 编程式的 `@RegisterExtension`
- 自动的 `ServiceLoader` 机制

# Junit 5 新特性 - 基础篇
## @DisplayName
用于描述测试的具体目的：
```java
@Test
@DisplayName("Choose a display name")
void displayNameTest() {}
```

# API 演变
JUnit 5 的主要目标是在维护 JUnit 的同时能够添加新的功能。JUnit 4 由于内部添加了过多的东西，导致维护困难。所以 JUnit 5对所有的公有接口、类和方法引入了生命周期。

## API 版本和状态
发布的版本号为 `<major>.<minor>.<patch>`,

# Reference
- https://junit.org/junit5/docs/current/user-guide/