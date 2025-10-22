# Mockito

2025-10-20⭐
@author Jiawei Mao
***
## 简介
在软件开发中，要确保对象按照我们预想的去做。一种方法是创建一个自动测试框架，然后对每个行为进行测试。但是这样的测试框架写起来十分麻烦。甚至超过原本的代码量。Mock测试框架在这种情况下诞生。

Mockito 是一种mock工具框架。
什么是mock？说的直白一点，大家都知道unit test应该是尽可能独立的。对一个class的unit test不应该再和其他class有任何交互。

现在有一个类，扫描一个目录并将找到的文件都上传到FTP server。该类对于不同的FTP响应(找不到FTP server 或 上传成功，或上传失败)，有一些后续操作。

在写这个类的UT时，我们就必须虚构出来一个FTP对象。这样在UT中，这个虚构的对象能够代替真正的FTP，对被测试类的调用做出一定的响应。从而知道被测试类是否正确的调用了FTP并做出一些正确的期望的响应。从而达到测试的目的。

mock可以模拟各种各样的对象，从而代替真正的对象做出希望的响应。
1) 模拟对象：模拟对象使用 `mock()`;
2) 模拟返回值：该步骤称为stubbing，比如指定让mock FTP对象第一次被调用时返回“找不到FTP server”，。代码形式是：when (mockedList.get(0)).thenReturn("first")。
3) 验证： 验证被测试类是否正确工作，使用verify()。例如，验证当mockFTP对象返回“找不到FTP server”时，测试代码是否按要求重试。
    测试完成！

### inline mock

从 Java 21 开始，JDK 限制 java 库添加代理到 JVM 的能力。因此，如果不显式启用，inline mock 可能无法正常运行，并且 JVM 会发出警告。下面介绍如何显式将 mockito-core 设置为  java 代理。

在测试执行期间显式附加 Mockito，需要将 mockito 的 jar 文件指定为 `-javaagent` 作为 JVM 参数。在 Gradle 中，可以使用 Kotlin DSL 将 Mockito 添加到所有测试任务：

```kotlin
 val mockitoAgent = configurations.create("mockitoAgent")
 dependencies {
     testImplementation(libs.mockito)
     mockitoAgent(libs.mockito) { isTransitive = false }
 }
 tasks {
     test {
         jvmArgs.add("-javaagent:${mockitoAgent.asPath}")
     }
 }
```

使用 Groovy DSL 也可以实现：

```groovy
 configurations {
     mockitoAgent
 }
 dependencies {
     testImplementation(libs.mockito)
     mockitoAgent(libs.mockito) {
         transitive = false
     }
 }
 tasks {
     test {
         jvmArgs += "-javaagent:${configurations.mockitoAgent.asPath}"
     }
 }
```

Mockito 的版本声明：

```groovy
 [versions]
 mockito = "5.14.0"

 [libraries]
 mockito = { module = "org.mockito:mockito-core", version.ref = "mockito" }
```

将 Mockito 添加到 Maven 的 surefire 插件：

```xml
 <plugin>
     <groupId>org.apache.maven.plugins</groupId>
     <artifactId>maven-dependency-plugin</artifactId>
     <executions>
         <execution>
             <goals>
                 <goal>properties</goal>
             </goals>
         </execution>
     </executions>
 </plugin>
 <plugin>
     <groupId>org.apache.maven.plugins</groupId>
     <artifactId>maven-surefire-plugin</artifactId>
     <configuration>
         <argLine>@{argLine} -javaagent:${org.mockito:mockito-core:jar}</argLine>
     </configuration>
 </plugin>
```

需要注意，在 surefire 执行替换时，`@{argLine}` 需要存在，否则会导致 VM 崩溃。

## 验证行为
下面模拟一个 `List`，因为大家都比较熟悉（在实际中直接使用 `List` 即可）：
```java
 //Let's import Mockito statically so that the code looks clearer
 import static org.mockito.Mockito.*;

@Test
void testList(){
    // mock creation
    List mockList = mock(List.class);

    // using mock object
    mockList.add("one");
    mockList.clear();

    // verification
    verify(mockList).add("one");
    verify(mockList).clear();
}
```
创建之后，mock会记住所有交互操作，然后可以选择感兴趣的交互进行验证。

## 记录操作
```java
LinkedList mockedList = mock(LinkedList.class);

// stubbing
when(mockedList.get(0)).thenReturn("first");
when(mockedList.get(1)).thenThrow(new RuntimeException());

// print "first"
System.out.println(mockedList.get(0));

// throw runtime exception
System.out.println(mockedList.get(1));

// print null becuase get(999) was not stubbed
System.out.println(mockedList.get(999));

verify(mockedList).get(0);
```
说明：
- 默认情况下，对所有会返回值的方法，mock 会酌情返回 `null`，基本类型或其包装类型或空的集合。
- 对没有定义情况，返回 `null`。

## 参数匹配
Mockito 通过 `equals` 方法验证参数值。如果你有额外的要求，可以使用参数匹配：


## 验证调用次数
```java
LinkedList mockedList = mock(LinkedList.class);
//using mock
mockedList.add("once");
mockedList.add("twice");
mockedList.add("twice");
mockedList.add("three times");
mockedList.add("three times");
mockedList.add("three times");

//following two verifications work exactly the same - times(1) is used by default
verify(mockedList).add("once");
verify(mockedList, times(1)).add("once");

//exact number of invocations verification
verify(mockedList, times(2)).add("twice");
verify(mockedList, times(3)).add("three times");

//verification using never(). never() is an alias to times(0)
verify(mockedList, never()).add("never happened");

//verification using atLeast()/atMost()
verify(mockedList, atMostOnce()).add("once");
verify(mockedList, atLeastOnce()).add("three times");
verify(mockedList, atLeast(2)).add("three times");
verify(mockedList, atMost(5)).add("three times");
```
默认为 `times(1)`。

## 无返回值函数抛出异常
```java
doThrow(new RuntimeException()).when(mockedList).clear();

//following throws RuntimeException:
mockedList.clear();
```

## 

## 模拟异常
```java
// 模拟获取第二个元素时，抛出 RuntimeException
when(mockedList.get(1)).thenThrow(new RuntimeException());
// 此时抛出 RuntimeException
System.out.println(mockedList.get(1));
```
没有返回值的方法也可以模拟抛出异常：
```java
doThrow(new RuntimeException()).when(mockedList).clear();
```

# Reference

- https://site.mockito.org/
- https://javadoc.io/static/org.mockito/mockito-core/3.1.0/org/mockito/Mockito.html