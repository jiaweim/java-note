# Hamcrest 教程

- [Hamcrest 教程](#hamcrest-教程)
  - [1. 简介](#1-简介)
  - [2. 第一个 Hamcrest 测试](#2-第一个-hamcrest-测试)
  - [3. 其它测试框架](#3-其它测试框架)
  - [4. Core Matcher](#4-core-matcher)
    - [4.1. is](#41-is)
    - [4.2. equalTo](#42-equalto)
    - [4.3. not](#43-not)
    - [4.4. nullValue](#44-nullvalue)
    - [4.5. notNullValue](#45-notnullvalue)
    - [4.6. instanceOf](#46-instanceof)
    - [4.7. isA](#47-isa)
    - [4.8. sameInstance](#48-sameinstance)
    - [4.9. any](#49-any)
    - [4.10. allOf 和 anyOf](#410-allof-和-anyof)
    - [4.11. hasItem 和 hasItems](#411-hasitem-和-hasitems)
    - [4.12. both 和 either](#412-both-和-either)
  - [5. Number Matcher](#5-number-matcher)
    - [5.1. isClose-double](#51-isclose-double)
    - [5.2. isClose-BigDecimal](#52-isclose-bigdecimal)
    - [5.3. 比较大小-Integer](#53-比较大小-integer)
    - [5.4. 比较大小-String](#54-比较大小-string)
    - [5.5. 比较大小-LocalDate](#55-比较大小-localdate)
    - [5.6. 比较大小-自定义类](#56-比较大小-自定义类)
    - [5.7. NaN Matcher](#57-nan-matcher)
  - [6. Object Matcher](#6-object-matcher)
    - [6.1. toString](#61-tostring)
    - [6.2. isCompatibleType](#62-iscompatibletype)
  - [7. Bean Matcher](#7-bean-matcher)
    - [7.1. hasProperty](#71-hasproperty)
    - [7.2. samePropertyValuesAs](#72-samepropertyvaluesas)
    - [7.3. getPropertyDescriptor](#73-getpropertydescriptor)
    - [7.4. propertyDescriptorsFor](#74-propertydescriptorsfor)
  - [8. Collections](#8-collections)
    - [8.1. 数组](#81-数组)
    - [8.2. Map](#82-map)
    - [8.3. Iterable](#83-iterable)
  - [9. Text Matcher](#9-text-matcher)
    - [9.1. equalToIgnoringCase](#91-equaltoignoringcase)
    - [9.2. equalToCompressingWhiteSpace](#92-equaltocompressingwhitespace)
    - [9.3. blankString](#93-blankstring)
    - [9.4. emptyString](#94-emptystring)
    - [9.5. matchesPattern](#95-matchespattern)
    - [9.6. containsString 和 containsStringIgnoringCase](#96-containsstring-和-containsstringignoringcase)
    - [9.7. stringContainsInOrder](#97-stringcontainsinorder)
    - [9.8. startsWith 和 startsWithIgnoringCase](#98-startswith-和-startswithignoringcase)
    - [9.9. endsWith 和 endsWithIgnoringCase](#99-endswith-和-endswithignoringcase)
  - [10. File](#10-file)
    - [10.1. aFileNamed](#101-afilenamed)
    - [10.2. aFileWithCanonicalPath 和 aFileWithAbsolutePath](#102-afilewithcanonicalpath-和-afilewithabsolutepath)
    - [10.3. aFileWithSize](#103-afilewithsize)
    - [10.4. aReadableFile 和 aWritableFile](#104-areadablefile-和-awritablefile)
    - [10.5. 文件是否存在](#105-文件是否存在)
  - [11. 自定义 Matcher](#11-自定义-matcher)
    - [11.1. 自定义示例一](#111-自定义示例一)
    - [11.2. 自定义示例二](#112-自定义示例二)
    - [11.3. 自定义示例三](#113-自定义示例三)
  - [12. 参考](#12-参考)

2023-08-25, 21:13
@author Jiawei Mao
****

## 1. 简介

Hamcrest 是一个用于编写 matcher 的框架，允许以声明方式定义 match 规则。matchers 在很多情况都非常有用，例如 UI 验证、数据过滤，在测试领域应用最多。

下面介绍如何使用 Hamcrest 进行单元测试。

## 2. 第一个 Hamcrest 测试

下面编写一个简单的 JUnit5 测试，但不使用 JUnit 的 `assertEquals` 方法，而是使用 Hamcrest 的 `assertThat` 构造和标准 matchers 集合：

```java
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;

public class StringMatcherTest {
    @Test
    public void given2Strings_whenEqual_thenCorrect() {
        String a = "foo";
        String b = "FOO";
        assertThat(a, equalToIgnoringCase(b));
    }
}
```

`assertThat` 是一个程式化的句子，用于编写断言测试。在上例中，断言的对象为 `String`，它是 `assertThat` 的第一个参数，第二个参数是 `String` 对象的 matcher。

如果测试中有多个断言，可以在断言中包含待测试的描述信息：

```java
assertThat("chocolate chips", theBiscuit.getChocolateChipCount(), equalTo(10)); 

assertThat("hazelnuts", theBiscuit.getHazelnutCount(), equalTo(3));
```

## 3. 其它测试框架

Hamcrest 支持不同测试框架，例如 Hamcrest 可以和 JUnit (所有版本) 和 TestNG 一起使用。

通过使用 adapter 将 mock 框架的 matcher 链接到 Hamcrest 的 matcher，Hamcrest 也可以与 mock 框架一起使用。例如，JMock 1 的 constraints 是 Hamcrest 的 matchers。Hamcrest 提供了 JMock 1 adapter，从而可以在 JMock 1 测试中使用 Hamcrest。JMock 2 不需要 adapter，因为 JMock 2 本身就是使用 Hamcrest 作为 matcher 库。

Hamcrest 还提供了 EasyMock 2 的 adaptor。

## 4. Core Matcher

- `anything`

总是匹配，不关心测试对象是什么时使用。

- `describedAs` 

decorator，添加自定义失败描述。

### 4.1. is

- `is(T)` 以对象为参数，检查相等性。
- `is(Matcher<T>)` 以另一个 matcher 为参数，使其可读性更好。

```java
@Test
public void testIsForMatch() {

    // GIVEN
    String testString = "hamcrest core";

    // 下面两个语句等价
    assertThat(testString, is("hamcrest core"));
    assertThat(testString, is(equalTo("hamcrest core")));
}
```

### 4.2. equalTo

`equalTo(T)` 测试两个对象是否相等。一般与 `is` 一起使用：

```java
@Test
void testEqualTo() {
    String actualString = "equalTo match";
    List<String> actualList = Lists.newArrayList("equalTo", "match");

    assertThat(actualString, is(equalTo("equalTo match")));
    assertThat(actualList, is(equalTo(Lists.newArrayList("equalTo", "match"))));
}
```

`equalToObject(Object operand)` 检查相等，不强制要求两个对象必须为相同静态类型。

```java
@Test
public void testDifferentStaticTypeUsingEqualToObject() {

    // GIVEN
    Object original = 100;

    // ASSERT
    assertThat(original, equalToObject(100));
}
```

### 4.3. not

`not(T)` 和 `not(Matcher<T>)` 用于检查给定对象的不相等性。

```java
@Test
public void testNot() {

    String testString = "hamcrest";

    assertThat(testString, not("hamcrest core"));
    assertThat(testString, is(not(equalTo("hamcrest core"))));
    assertThat(testString, is(not(instanceOf(Integer.class))));
}
```

### 4.4. nullValue

`nullValue()` 检查对象是否为 null。`nullValue(Class<T>)` 检查是否为指定类型的 null 值。

```java
@Test
public void testNullValue() {

    Integer nullObject = null;

    assertThat(nullObject, is(nullValue()));
    assertThat(nullObject, is(nullValue(Integer.class)));
}
```

### 4.5. notNullValue

`is(not(nullValue()))` 的快捷方式，检查对象不为 `null`。

```java
@Test
void testNotNull() {
    Integer testNumber = 123;

    assertThat(testNumber, is(notNullValue()));
    assertThat(testNumber, is(notNullValue(Integer.class)));
}
```

### 4.6. instanceOf

`instanceOf(Class<?>)` 检查指定对象是否为特定类型。

```java
@Test
public void testInstanceOf() {

    assertThat("hamcrest", is(instanceOf(String.class)));
}
```

### 4.7. isA

`isA(Class<T> type)` 是 `instanceOf(Class<?>)` 的简写形式。

```java
@Test
public void testIsA() {

    assertThat("hamcrest core", isA(String.class));
}
```

### 4.8. sameInstance

`sameInstance()` 测试两个引用变量是否指向同一个对象：

```java
@Test
public void testSameInstance() {

    String string1 = "hamcrest";
    String string2 = string1;

    assertThat(string1, is(sameInstance(string2)));
}
```

### 4.9. any

`any(Class<T>)` 功能同 `instanceOf(Class<?>)`，只是要求 `any` 参数类型必须与 测试对象相同。

```java
@Test
public void testAny() {

    assertThat("hamcrest", is(any(String.class)));
    assertThat("hamcrest", is(any(Object.class)));
}
```

### 4.10. allOf 和 anyOf

`allOf(Matcher<? extends T>…)` 测试对象是否符合指定的所有条件：

```java
@Test
public void testAllOf() {

    String testString = "Hamcrest Core";

    assertThat(testString, allOf(startsWith("Ham"), endsWith("ore"), containsString("Core")));
}
```

`anyOf(Matcher<? extends T>…)` 测试对象是否符合指定条件之一：

```java
@Test
void testAnyOf() {
    String testString = "Hamcrest Core";

    assertThat(testString, anyOf(startsWith("Ham"), containsString("baeldung")));
}
```

### 4.11. hasItem 和 hasItems

`hasItem` 检查集合是否有元素匹配指定条件：

```java
@Test
public void testHasItem() {

    List<String> list = Lists.newArrayList("java", "spring", "baeldung");

    assertThat(list, hasItem("java"));
    assertThat(list, hasItem(isA(String.class)));
}
```

`hasItems` 检查多个元素条件：

```java
@Test
public void testHasItems() {

    List<String> list = Lists.newArrayList("java", "spring", "baeldung");

    assertThat(list, hasItems("java", "baeldung"));
    assertThat(list, hasItems(isA(String.class), endsWith("ing")));
}
```

### 4.12. both 和 either

`both(Matcher<? extends T>)` 测试对象是否**同时**满足指定的两个条件。

```java
@Test
public void testBoth() {

    String testString = "Hamcrest Core Matchers";

    assertThat(testString, both(startsWith("Ham")).and(containsString("Core")));
}
```

`either(Matcher<? extends T>)` 测试对象是否满足两个条件之一。

```java
@Test
public void testEither() {

    String testString = "Hamcrest Core Matchers";

    assertThat(testString, either(startsWith("Bael")).or(containsString("Core")));
}
```

## 5. Number Matcher

### 5.1. isClose-double

检查 `actual` 是否在 [operand-error, operand+error] 范围。

```java
@Test
public void testCloseTo() {
    double actual = 1.3;
    double operand = 1;
    double error = 0.5;
    assertThat(actual, is(closeTo(operand, error)));
}
```

也可以检查不在该范围。

```java
@Test
public void testNotCloseTo() {
    double actual = 1.6;
    double operand = 1;
    double error = 0.5;
    assertThat(actual, is(not(closeTo(operand, error))));
}
```

### 5.2. isClose-BigDecimal

`isClose` 为重载方法，可以用来检查 BigDecimal 对象：

```java
@Test
public void testBigDecimalCloseTo() {
    BigDecimal actual = new BigDecimal("1.0003");
    BigDecimal operand = new BigDecimal("1");
    BigDecimal error = new BigDecimal("0.0005");
    assertThat(actual, is(closeTo(operand, error)));
}

@Test
public void testBigDecimalNotCloseTo() {
    BigDecimal actual = new BigDecimal("1.0006");
    BigDecimal operand = new BigDecimal("1");
    BigDecimal error = new BigDecimal("0.0005");
    assertThat(actual, is(not(closeTo(operand, error))));
}
```

!!! note
    `is` Matcher 用于修饰其它 matcher，不添加额外逻辑，只是使整个断言可读性更好。

### 5.3. 比较大小-Integer

包括：

- `comparesEqualTo`
- `greaterThan`
- `greaterThanOrEqualTo`
- `lessThan`
- `lessThanOrEqualTo`

Integer 示例：

```java
@Test
public void given5_whenComparesEqualTo5_thenCorrect() {
    Integer five = 5;
    assertThat(five, comparesEqualTo(five));
}

@Test
public void given5_whenNotComparesEqualTo7_thenCorrect() {
    Integer seven = 7;
    Integer five = 5;
    assertThat(five, not(comparesEqualTo(seven)));
}

@Test
public void given7_whenGreaterThan5_thenCorrect() {
    Integer seven = 7;
    Integer five = 5;
    assertThat(seven, is(greaterThan(five)));
}

@Test
public void given7_whenGreaterThanOrEqualTo5_thenCorrect() {
    Integer seven = 7;
    Integer five = 5;
    assertThat(seven, is(greaterThanOrEqualTo(five)));
}

@Test
public void given5_whenGreaterThanOrEqualTo5_thenCorrect() {
    Integer five = 5;
    assertThat(five, is(greaterThanOrEqualTo(five)));
}

@Test
public void given3_whenLessThan5_thenCorrect() {
    Integer three = 3;
    Integer five = 5;
    assertThat(three, is(lessThan(five)));
}

@Test
public void given3_whenLessThanOrEqualTo5_thenCorrect() {
    Integer three = 3;
    Integer five = 5;
    assertThat(three, is(lessThanOrEqualTo(five)));
}

@Test
public void given5_whenLessThanOrEqualTo5_thenCorrect() {
    Integer five = 5;
    assertThat(five, is(lessThanOrEqualTo(five)));
}
```

### 5.4. 比较大小-String

比较 Matcher 适用于任何实现 Comparable 的类，所以也可以用来比较 String：

```java
@Test
public void testStringGreaterThan() {
    String amanda = "Amanda";
    String benjamin = "Benjamin";
    assertThat(benjamin, is(greaterThan(amanda)));
}

@Test
public void testStringLessThan() {
    String amanda = "Amanda";
    String benjamin = "Benjamin";
    assertThat(amanda, is(lessThan(benjamin)));
}
```

`String` 的 `Comparable` 接口实现按字母顺序排序，所以 "Amanda" 在 "Benjamin" 前面，这里理解为更小。

### 5.5. 比较大小-LocalDate

和 String 一样，也可以比较 `LocalDate`。

```java
@Test
public void testDateGreaterThan() {
    LocalDate today = LocalDate.now();
    LocalDate yesterday = today.minusDays(1);
    assertThat(today, is(greaterThan(yesterday)));
}

@Test
public void testDateLessThan() {
    LocalDate today = LocalDate.now();
    LocalDate tomorrow = today.plusDays(1);
    assertThat(today, is(lessThan(tomorrow)));
}
```

### 5.6. 比较大小-自定义类

只要实现 Comparable 接口，就能够利用比较 Matchers。

```java
class Person implements Comparable<Person> {

    String name;
    int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public int compareTo(Person o) {
        if (this.age == o.getAge()) return 0;
        if (this.age > o.age) return 1;
        else return -1;
    }
}
```

在 `compareTo` 方法中比较 `Person` 的 `age` 字段。

然后就能使用比较 Matchers：

```java
@Test
public void testPersonGreaterThan() {
    Person amanda = new Person("Amanda", 20);
    Person benjamin = new Person("Benjamin", 18);
    assertThat(amanda, is(greaterThan(benjamin)));
}

@Test
public void testPersonLessThan() {
    Person amanda = new Person("Amanda", 20);
    Person benjamin = new Person("Benjamin", 18);
    assertThat(benjamin, is(lessThan(amanda)));
}
```

### 5.7. NaN Matcher

```java
@Test
public void givenNaN_whenIsNotANumber_thenCorrect() {
    double zero = 0d;
    assertThat(zero / zero, is(notANumber()));
}
```

## 6. Object Matcher

Object matcher 检查对象属性，为 Java 对象提供 `Object` 相关测试。

|Matcher|功能|
|---|---|
|`equalTo`|使用 `Object.equals` 测试对象是否相等|
|`hasToString`|测试 `Object.toString` 方法|
|`instanceOf`, `isCompatibleType`|测试类型|
|`notNullValue`, `nullValue`|测试 null|
|`sameInstance`|测试对象相等|

**示例：** 先定义不包含属性的 Bean

```java
public class Location {}
```

然后定义一个 `City` bean：

```java
public class City extends Location {

    String name;
    String state;

    public City(String name, String state) {
        this.name = name;
        this.state = state;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        if (this.name == null && this.state == null) return null;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("Name: ");
        sb.append(this.name);
        sb.append(", ");
        sb.append("State: ");
        sb.append(this.state);
        sb.append("]");
        return sb.toString();
    }
}
```

### 6.1. toString

`hasToString` 测试 `toString` 返回值。

```java
@Test
public void testHasToString() {
    City city = new City("San Francisco", "CA");

    assertThat(city, hasToString("[Name: San Francisco, State: CA]"));
}
```

- 串联 matcher

```java
@Test
public void testHasToStringEqualToIgnoringCase() {
    City city = new City("San Francisco", "CA");

    assertThat(city, hasToString(equalToIgnoringCase("[NAME: SAN FRANCISCO, STATE: CA]")));
}
```

- `hasToString` 为重载方法，可以使用字符串或文本 matcher 作为参数，下面以 matcher 为参数

```java
@Test
public void testHasToStringEmptyOrNullString() {
    City city = new City(null, null);

    assertThat(city, hasToString(emptyOrNullString()));
}
```

### 6.2. isCompatibleType

`isCompatibleType` 测试 is-a 关系

测试 `City` 是 `Location` 的子类。

```java
@Test
public void testTypeCompatibleWith() {
    City city = new City("San Francisco", "CA");

    assertThat(city.getClass(), is(typeCompatibleWith(Location.class)));
}
```

`City` 不是 `String` 子类

```java
@Test
public void testTypeNotCompatibleWith() {
    City city = new City("San Francisco", "CA");

    assertThat(city.getClass(), is(not(typeCompatibleWith(String.class))));
}
```

所有 Java 类都是 `Object` 的子类：

```java
@Test
public void testTypeCompatibleWithObject() {
    City city = new City("San Francisco", "CA");

    assertThat(city.getClass(), is(typeCompatibleWith(Object.class)));
}
```

## 7. Bean Matcher

Bean Matcher 用于检查 POJOs 对象。

### 7.1. hasProperty

检查某个 bean 是否包含由属性名称标识的特定属性。

```java
@Test
public void testHasProperty() {
    City city = new City("San Francisco", "CA");

    assertThat(city, hasProperty("name"));
}

@Test
public void testNotHasProperty() {
    City city = new City("San Francisco", "CA");

    assertThat(city, not(hasProperty("country")));
}
```

检查属性值：

```java
@Test
public void testPropertyWithValue() {
    City city = new City("San Francisco", "CA");

    assertThat(city, hasProperty("name", equalTo("San Francisco")));
}
```

使用 matcher 检查属性值：

```java
@Test
public void testHasPropertyWithValueEqualToIgnoringCase() {
    City city = new City("San Francisco", "CA");

    assertThat(city, hasProperty("state", equalToIgnoringCase("ca")));
}
```

### 7.2. samePropertyValuesAs

当需要检查 bean 的许多属性时，可以先创建一个新的 bean 来检查其它 bean。

`samePropertyValuesAs` 表示测试 bean 与新创建的 bean 所有属性值相等。

```java
@Test
public void testSamePropertyValuesAs() {
    City city = new City("San Francisco", "CA");
    City city2 = new City("San Francisco", "CA");

    assertThat(city, samePropertyValuesAs(city2));
}
```

```java
@Test
public void testNotSamePropertyValuesAs() {
    City city = new City("San Francisco", "CA");
    City city2 = new City("Los Angeles", "CA");

    assertThat(city, not(samePropertyValuesAs(city2)));
}
```

### 7.3. getPropertyDescriptor

提供查询类结构的功能：

```java
@Test
public void testGetPropertyDescriptor() {
    City city = new City("San Francisco", "CA");
    PropertyDescriptor descriptor = getPropertyDescriptor("state", city);

    assertThat(descriptor
            .getReadMethod()
            .getName(), is(equalTo("getState")));
}
```

`PropertyDescriptor` 包含属性状态的大量信息。

### 7.4. propertyDescriptorsFor

功能同上，但是用于所有 bean 属性。

```java
@Test
public void testGetPropertyDescriptorsFor() {
    City city = new City("San Francisco", "CA");
    PropertyDescriptor[] descriptors = propertyDescriptorsFor(city, Object.class);
    List<String> getters = Arrays
            .stream(descriptors)
            .map(x -> x.getReadMethod().getName())
            .collect(toList());

    assertThat(getters, containsInAnyOrder("getName", "getState"));
}
```

从 `City` bean 获取属性描述符直到 `Object` level 停止。

## 8. Collections

- 检查是否包含单个元素

```java
@Test
public final void testSingleElement() {
    final List<String> collection = Lists.newArrayList("ab", "cd", "ef");
    assertThat(collection, hasItem("cd"));
    assertThat(collection, not(hasItem("zz")));
}
```

- 检查是否包含多个元素

```java
@Test
public final void testMultipleElements() {
    final List<String> collection = Lists.newArrayList("ab", "cd", "ef");
    assertThat(collection, hasItems("ef", "cd"));
}
```

### 8.1. 数组

- 检查数组是否包含指定元素

```java
@Test
public void testHasItemInArray() {
    String[] hamcrestMatchers = {"collections", "beans", "text", "number"};
    assertThat(hamcrestMatchers, hasItemInArray("text"));
}
```

- 也可以反过来检查数组包含指定元素

```java
@Test
public void testOneOf() {
    String[] hamcrestMatchers = {"collections", "beans", "text", "number"};
    assertThat("text", is(oneOf(hamcrestMatchers)));
}
```

- 还可以直接用 `in` 检查

```java
@Test
public void testIn() {
    String[] array = new String[]{"collections", "beans", "text",
            "number"};
    assertThat("beans", is(in(array)));
}
```

- arrayContainingInAnyOrder

数组是否正好包含指定的所有元素，不关心顺序。

```java
@Test
public void testArrayContainingInAnyOrder() {
    String[] hamcrestMatchers = {"collections", "beans", "text", "number"};
    assertThat(hamcrestMatchers,
            arrayContainingInAnyOrder("beans", "collections", "number", "text"));
}
```

- arrayContaining

数组是否正好包含指定的所有元素，顺序也一致。

```java
@Test
public void testArrayContaining() {
    String[] hamcrestMatchers = {"collections", "beans", "text", "number"};
    assertThat(hamcrestMatchers,
            arrayContaining("collections", "beans", "text", "number"));
}
```


- 检查所有元素

严格顺序

```java
@Test
public final void testContains() {
    final List<String> collection = Lists.newArrayList("ab", "cd", "ef");
    assertThat(collection, contains("ab", "cd", "ef"));
}
```

任意顺序

```java
@Test
public final void testContainsInAnyOrder() {
    final List<String> collection = Lists.newArrayList("ab", "cd", "ef");
    assertThat(collection, containsInAnyOrder("cd", "ab", "ef"));
}
```

- 检查集合是否为空

```java
@Test
public final void testCollectionIsEmpty() {
    final List<String> collection = Lists.newArrayList();
    assertThat(collection, empty());
}
```

- 检查数组是否为空

```java
@Test
public final void testEmptyArray() {
    final String[] array = new String[]{"ab"};
    assertThat(array, not(emptyArray()));
}
```

### 8.2. Map

- 检查 `Map` 是否为空

```java
@Test
public final void testEmptyMap() {
    final Map<String, String> collection = Maps.newHashMap();
    assertThat(collection, equalTo(Collections.EMPTY_MAP));
}
```

- `Map` 是否包含 key

```java
@Test
void testHasKey() {
    Map<String, String> myMap = new HashMap<>();
    myMap.put("bar", "a");
    assertThat(myMap, hasKey("bar"));
}
```

- `Map` 是否包含 value

```java
@Test
public void testMapHasValue() {
    Map<String, String> map = new HashMap<>();
    map.put("blogname", "baeldung");
    assertThat(map, hasValue("baeldung"));
}
```

- Map 是否包含 entry

```java
@Test
public void testMapHasEntry() {
    Map<String, String> map = new HashMap<>();
    map.put("blogname", "baeldung");
    assertThat(map, hasEntry("blogname", "baeldung"));
}
```

### 8.3. Iterable

- 检查 `Iterable` 是否为空

```java
@Test
public final void testIterableIsEmpty() {
    final Iterable<String> collection = Lists.newArrayList();
    assertThat(collection, is(emptyIterable()));
}
```

- 检查数组大小

```java
@Test
public void testArrayWithSize() {
    String[] hamcrestMatchers = {"collections", "beans", "text", "number"};
    assertThat(hamcrestMatchers, arrayWithSize(4));
}
```

- 检查集合大小

```java
@Test
public final void testCollectionSize() {
    final List<String> collection = Lists.newArrayList("ab", "cd", "ef");
    assertThat(collection, hasSize(3));
}
```

- 检查 Iterable 大小

```java
@Test
public final void testIterableSize() {
    final Iterable<String> collection = Lists.newArrayList("ab", "cd", "ef");
    assertThat(collection, iterableWithSize(3));
}
```

- 检查每个元素是否满足条件

```java
@Test
void testEachItem() {
    final List<Integer> collection = Lists.newArrayList(15, 20, 25, 30);
    assertThat(collection, everyItem(greaterThan(10)));
}
```

## 9. Text Matcher

|Matcher|功能|
|---|---|
|equalToIgnoringCase|测试字符串相等性（忽略大小写）|
|equalToIgnoringWhiteSpace|测试字符串相等性（忽略空白）|
|containsString, endsWith, startsWith|测试字符串匹配|

除了标准的 `isEqual()`，Hamcrest 还为 `String` 类型提供了 `equalToIgnoringCase` 和 `equalToIgnoringWhiteSpace` 等 matchers。

### 9.1. equalToIgnoringCase

匹配字符串时忽略大小写。

```java
@Test
public final void testEqual() {
    String first = "hello";
    String second = "Hello";

    assertThat(first, equalToIgnoringCase(second));
}
```

### 9.2. equalToCompressingWhiteSpace

```java
@Test
public final void testEqualWithWhiteSpace() {
    String first = "hello";
    String second = "   hello   ";

    assertThat(first, equalToIgnoringWhiteSpace(second));
}
```

不过现在不推荐使用 `equalToIgnoringWhiteSpace`，而是 `equalToCompressingWhiteSpace`，忽略前后空白，中间的空白都压缩为一个 space：

```java
@Test
public final void testEqualWithWhiteSpace() {
    String first = "hello";
    String second = "   hello   ";

    assertThat(first, equalToCompressingWhiteSpace(second));
}
```

### 9.3. blankString

`blankString()` 检查是否为 blank 字符串（只包含 whitespace），`blankOrNullString()` 检查是否为 blank 字符串或 null。

```java
@Test
public final void testStringIsBlank() {
    String first = "  ";
    String second = null;

    assertThat(first, is(blankString()));
    assertThat(first, is(blankOrNullString()));
    assertThat(second, is(blankOrNullString()));
}
```

### 9.4. emptyString

`emptyString()` 检查是否为空字符串：

```java
@Test
public final void testStringIsEmpty() {
    String first = "";
    String second = null;

    assertThat(first, is(emptyString()));
    assertThat(first, is(emptyOrNullString()));
    assertThat(second, is(emptyOrNullString()));
}
```

### 9.5. matchesPattern

`matchesPattern()` 检查字符串是否匹配正则表达式。

```java
@Test
public final void testStringMatchPattern() {
    String first = "hello";

    assertThat(first, matchesPattern("[a-z]+"));
}
```

### 9.6. containsString 和 containsStringIgnoringCase

`containsString()` 和 `containsStringIgnoringCase()` 测试是否包含指定子字符串

```java
@Test
public final void testContains() {
    String first = "hello";

    assertThat(first, containsString("lo"));
    assertThat(first, containsStringIgnoringCase("EL"));
}
```

### 9.7. stringContainsInOrder

`stringContainsInOrder` 指定包含子字符串的顺序。

```java
@Test
public final void testContainsInOrder() {
    String first = "hello";

    assertThat(first, stringContainsInOrder("e", "l", "o"));
}
```

### 9.8. startsWith 和 startsWithIgnoringCase

测试字符串是否以指定字符串开始。

```java
@Test
public final void testStartsWith() {
    String first = "hello";

    assertThat(first, startsWith("he"));
    assertThat(first, startsWithIgnoringCase("HEL"));
}
```

### 9.9. endsWith 和 endsWithIgnoringCase

测试字符串是否以指定字符串结尾。

```java
@Test
public final void testEndsWith() {
    String first = "hello";

    assertThat(first, endsWith("lo"));
    assertThat(first, endsWithIgnoringCase("LO"));
}
```

## 10. File

### 10.1. aFileNamed

`aFileNamed` 用于验证文件名，与 String matcher 结合使用。

```java
@Test
public final void testFileName() {
    File file = new File("src/test/resources/test1.in");

    assertThat(file, aFileNamed(equalToIgnoringCase("test1.in")));
}
```

### 10.2. aFileWithCanonicalPath 和 aFileWithAbsolutePath

```java
@Test
public void whenVerifyingFilePath_thenCorrect() {
    File file = new File("src/test/resources/test1.in");
    
    assertThat(file, aFileWithCanonicalPath(containsString("src/test/resources")));
    assertThat(file, aFileWithAbsolutePath(containsString("src/test/resources")));
}
```

### 10.3. aFileWithSize

```java
@Test
public final void testFileSize() {
    File file = new File("src/test/resources/test1.in");

    assertThat(file, aFileWithSize(11));
    assertThat(file, aFileWithSize(greaterThan(1L)));
}
```

### 10.4. aReadableFile 和 aWritableFile

```java
@Test
public final void testFileIsReadableAndWritable() {
    File file = new File("src/test/resources/test1.in");

    assertThat(file, aReadableFile());
    assertThat(file, aWritableFile());
}
```

### 10.5. 文件是否存在

```java
@Test
public final void whenVerifyingFileOrDirExist_thenCorrect() {
    File file = new File("src/test/resources/test1.in");
    File dir = new File("src/test/resources");

    assertThat(file, anExistingFile());
    assertThat(dir, anExistingDirectory());
    assertThat(file, anExistingFileOrDirectory());
    assertThat(dir, anExistingFileOrDirectory());
}
```

## 11. 自定义 Matcher

自定义 Matcher 主要继承 `TypeSafeMatcher` 类：

- `matchesSafely(T t)` 包含主要逻辑
- `describeTo(Description description)` 指定测试失败信息

当你发现一段代码在不同测试中反复出现测试同一组属性时，将这段代码打包到断言中，编写自己的 Matcher，可以消除代码重复并使测试更易读。

### 11.1. 自定义示例一

实现断言：当字符串只包含数字，返回 true。

- 首先继承 `TypeSafeMatcher`

```java
public class IsOnlyDigits extends TypeSafeMatcher<String> {
   
    @Override
    protected boolean matchesSafely(String s) {
        // ...
    }

    @Override
    public void describeTo(Description description) {
        // ...
    }
}
```

因为要测试的 `String` 类型，所以这里指定 `TypeSafeMatcher` 的参数化类型为 `String`。

- 实现

```java
public class IsOnlyDigits extends TypeSafeMatcher<String> {

    @Override
    protected boolean matchesSafely(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException nfe){
            return false;
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("only digits");
    }
}
```

在 `matchesSafely` 方法内视图将输入的字符串解析为整数。如果成功，返回 true；否则返回 false。

`describeTo` 方法中附件了期望了测试失败信息。

- 最后，还需要一个 `static` 方法，从而与内置的 matchers 一样

```java
public static Matcher<String> onlyDigits() {
    return new IsOnlyDigits();
}
```

- 使用创建的 matcher

```java
@Test
public void givenAString_whenIsOnlyDigits_thenCorrect() {
    String digits = "1234";

    assertThat(digits, onlyDigits());
}
```

### 11.2. 自定义示例二

如果我们想创建一个 Matcher 来定义一个数是否可以被另一个数整除，此时需要将其中一个参数存储起来。

实现：

```java
public class IsDivisibleBy extends TypeSafeMatcher<Integer> {

    private Integer divider;

    // constructors

    @Override
    protected boolean matchesSafely(Integer dividend) {
        if (divider == 0) {
            return false;
        }
        return ((dividend % divider) == 0);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("divisible by " + divider);
    }

    public static Matcher<Integer> divisibleBy(Integer divider) {
        return new IsDivisibleBy(divider);
    }
}
```

这里只需要添加一个新的属性。

```java
@Test
public void givenAnEvenInteger_whenDivisibleByTwo_thenCorrect() {
    Integer ten = 10;
    Integer two = 2;

    assertThat(ten,is(divisibleBy(two)));
}

@Test
public void givenAnOddInteger_whenNotDivisibleByTwo_thenCorrect() {
    Integer eleven = 11;
    Integer two = 2;

    assertThat(eleven,is(not(divisibleBy(two))));
}
```

### 11.3. 自定义示例三

例如，测试 double 是否为 NaN：

```java
@Test
public void testSquareRootOfMinusOneIsNotANumber() { 
  assertThat(Math.sqrt(-1), is(notANumber())); 
}
```

下面为实现：

```java
import org.hamcrest.Description; 
import org.hamcrest.Matcher; 
import org.hamcrest.TypeSafeMatcher;

public class IsNotANumber extends TypeSafeMatcher {

    @Override 
    public boolean matchesSafely(Double number) { 
        return number.isNaN(); 
    }

    public void describeTo(Description description) { 
        description.appendText("not a number"); 
    }

    public static Matcher notANumber() { 
        return new IsNotANumber(); 
    }
} 
```

`assertThat` 是一个泛型方法，它接受一个 matcher。这里断言 Double 值，所以需要 Matcher<Double>。

自定义 Matcher 最方便的方式是继承 `TypeSafeMatcher`，它为我们完成类型转换。我们只需要实现 `matchesSafely` 方法，检查 `Double` 是否为 `NaN`，和 `describeTo` 方法 （测试失败时生成失败消息）。下面是一个失败消息示例：

```java
assertThat(1.0, is(notANumber()));

// fails with the message

java.lang.AssertionError: Expected: is not a number got : <1.0>
```

最后一个是便利的 factory 方法。静态导入该方法便于测试：

```java
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;
import static org.hamcrest.examples.tutorial.IsNotANumber.notANumber;

public class NumberTest {
  @Test
  public void testSquareRootOfMinusOneIsNotANumber() { 
    assertThat(Math.sqrt(-1), is(notANumber())); 
  } 
} 
```

## 12. 参考

- https://hamcrest.org/JavaHamcrest/tutorial
- https://www.baeldung.com/tag/hamcrest
- https://www.baeldung.com/hamcrest-file-matchers
- https://www.baeldung.com/hamcrest-text-matchers
- https://www.baeldung.com/hamcrest-core-matchers
- https://www.baeldung.com/hamcrest-collections-arrays