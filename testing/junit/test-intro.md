# Junit5 概述

## 单元测试简介

**单元测试**测的是独立的工作单元。在Java应用程序中，“独立的一个工作单元” 通常指方法（但并不总是如此）。作为对比，**集成测试**和**接收测试**则检查多个组件如何交互。一个单元测试是一项任务，它不依赖于其他任何任务的完成。

API契约：应用程序接口(API)可以看作是调用者和被调用者之间的正式协定。单元测试常常可以通过证实期待的结果来帮助定义 API 契约。API 契约的说法来自伴随 Eiffel 编程语言而流行的 Design by Contract 实践。

单元测试的一条核心原则：若程序某项功能没有经过自动测试，那该功能基本等于不存在。

测试的是代码，而不是自己。

代码走读：开发人员之间随机的互相阅读代码，检查其编写正确与否的代码检查方式。

Annotation 一般翻译成元数据，元数据就是描述数据的数据。在 Java 里面可以用来和 public,static 等关键字一样来修饰类名、方法名、变量名，用以描述这个数据是做什么用的。

## 注释

JUnit Jupiter 支持以下注释，用于配置测试和扩展框架。

如果没有另外说明，所有注释都位于 `junit-jupiter-api` 模块的 `org.junit.jupiter.api` 包中。

|注释|说明|
|---|---|
|`@Disabled`|禁用测试方法或类，同 JUnit 4 `@Ignore`|



|注释|说明|
|---|---|
|@Test|标记测试方法。与 JUnit4 的 `@Test` 注释不同的是，该注释没有任何属性|
|@ParameterizedTest|标记方法为参数化测试|
|@RepeatedTest|标记方法为重复测试的模板|
|@TestFactory|标记方法为动态测试的测试工厂|
|@TestTemplate|标记方法为测试用例模板|
|@TestClassOrder|用于配置注释测试类中 `@Nested` 测试类的执行顺序|
|@TestMethodOrder|用于配置注释测试类中测试方法的执行顺序，类似 JUnit4 中的 `@FixMethodOrder`|
|@TestInstance|用于配置注释测试类的测试实例生命周期|
|@DisplayName|用于定义测试类或测试方法的显示名称|
|@DisplayNameGeneration|为测试类指定显示名称生成器|
|@BeforeEach|标记方法在当前类的**每个** @Test, @RepeatedTest, @ParameterizedTest 和 @TestFactory **之前**执行，类似 JUnit4 的 @Before|
|@AfterEach|标记方法在当前类的**每个** @Test, @RepeatedTest, @ParameterizedTest 和 @TestFactory **之后**执行，类似 JUnit4 的 @After|
|@BeforeAll|标记方法在当前类的所有 @Test, @RepeatedTest, @ParameterizedTest 和 @TestFactory 之前执行，类似 JUnit4 的 @BeforeClass|
|@AfterAll|标记方法在当前类的**所有** @Test, @RepeatedTest, @ParameterizedTest 和 @TestFactory **之后**执行，类似 JUnit4 的 @BeforeClass|
|@Nested|标记注释测试类为non-static 嵌套测试类。在 Java 8 到 Java 15，@BeforeAll 和 @AfterAll 方法不能直接在 @Nested 测试类中使用，除非使用 per-class 测试实例生命周期。从 Java 16 开始，@BeforeAll 和 @AfterAll 方法可以在 @Nested 测试类中声明为 static 方法|
|@Tag|在类或方法级别声明测试过滤 test，类似 TestNG 的 test-groups 或 JUnit4 中的 Categories|
|@Disabled|用于禁用测试类或测试方法，类似 JUnit4 的 @Ignore|
|@Timeout|标记测试、测试 factory、测试模板或声明周期方法超时则失败|
|@ExtendWith|注册扩展|
|@RegisterExtension|通过字段注册扩展|
|@TempDir|在生命周期方法或测试方法中通过字段或参数注入临时目录|

### 元注释和组合注释

JUnit Jupiter 注释可以作为元注释。这表示可以定义自己的组合注释，它自动继承元注释的语义。

例如，与其在整个代码库中复制粘贴 `@Tag("fast")`，还不如创建一个名为 @Fast 的自定义组合注释。如下所示：

```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Tag("fast")
public @interface Fast {
}
```

下面的 @Test 方法演示如何使用 @Fast 注释：

```java
@Fast
@Test
void myFastTest() {
    // ...
}
```

甚至可以进一步定义 @FastTest 注释，用于替代 @Tag("fast") 和 @Test：

```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Tag("fast")
@Test
public @interface FastTest {
}
```

JUnit 自动将下面的方法识别为包含 "fast" tag 的 @Test 方法：

```java
@FastTest
void myFastTest() {
    // ...
}
```

## 概念

- **容器（Container）**

测试树中包含其它容器或测试作为子节点的节点，如测试类（test class）。

- **测试（Test）**

测试树中的节点，如 @Test 方法。

- **生命周期方法（Lifecycle method）**

使用 @BeforeAll, @AfterAll, @BeforeEach 或 @AfterEach 注释的方法。

- **测试类（Test Class）**

包含至少一个测试方法的 top 类，静态类或 @Nested 类，即容器。测试类不能为 abstract，且只能有一个构造函数。

- **测试方法（Test Method）**

使用 @Test, @RepeatedTest, @ParameterizedTest, @TestFactory 或 @TestTemplate 直接注释或元注释的实例方法。除了 @Test 外，它们会在测试树中创建一个容器。

## 测试类和测试方法

