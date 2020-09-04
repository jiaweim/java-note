# Java Annotation

- [Java Annotation](#java-annotation)
  - [简介](#简介)
    - [为什么使用 Annotation](#为什么使用-annotation)
    - [Annotation 工作方式](#annotation-工作方式)
    - [Annotation 使用](#annotation-使用)
    - [Annotation 类型](#annotation-类型)
  - [自定义注释](#自定义注释)
  - [注释的元素声明](#注释的元素声明)
  - [标准注解](#标准注解)
  - [内置注解，元注解](#内置注解元注解)
    - [@Target](#target)
    - [@Retention](#retention)
    - [@Documented](#documented)
    - [@Inherited](#inherited)
  - [用于编译的注释](#用于编译的注释)
  - [用于管理资源的注解](#用于管理资源的注解)
  
2020-04-28, 10:15
***

## 简介

### 为什么使用 Annotation

在JAVA应用中，我们常遇到一些需要使用模版的代码。例如，为了编写一个JAX-RPC web service，我们必须提供一对接口和实现作为模版代码。如果使用annotation对远程访问的方法代码进行修饰的话，这个模版就能够使用工具自动生成。

另外，一些API需要使用与程序代码同时维护的附属文件。例如，JavaBeans需要一个BeanInfo Class与一个Bean同时使用/维护，而EJB则同样需要一个部署描述符。此时在程序中使用annotation来维护这些附属文件的信息将十分便利而且减少了错误。

### Annotation 工作方式

在5.0版之前的Java平台已经具有了一些ad hoc annotation机制。比如，使用 `transient` 修饰符来标识一个成员变量在序列化子系统中应被忽略。而 `@deprecated`这个 javadoc tag也是一个ad hoc annotation，用来说明一个方法已过时。从Java5.0版发布以来，5.0平台提供了一个正式的annotation功能：允许开发者定义、使用自己的annoatation类型。此功能由一个定义annotation类型的语法和一个描述annotation声明的语法，读取annotaion 的API，一个使用annotation修饰的class文件，一个annotation处理工具（apt）组成。

annotation并不直接影响代码语义，但是它能够工作的方式被看作类似程序的工具或者类库，它会反过来对正在运行的程序语义有所影响。annotation可以从源文件、class文件或者以在运行时反射的多种方式被读取。

当然annotation在某种程度上使javadoc tag更加完整。一般情况下，如果这个标记对java文档产生影响或者用于生成java文档的话，它应该作为一个javadoc tag；否则将作为一个annotation.

### Annotation 使用

1. 类型声明方式

通常，应用程序并不是必须定义annotation类型，但是定义annotation类型并非难事。Annotation类型声明于一般的接口声明极为类似，区别只在于它在interface关键字前面使用"@"符号。

annotation类型的每个方法声明定义了一个annotation类型成员，但方法声明不必有参数或者异常声明；方法返回值的类型被限制在以下的范围：primitives、String、Class、enums、annotation和前面类型的数组；方法可以有默认值。

下面是一个简单的annotation类型声明：

```java
/**
 * Describes the Request-For-Enhancement(RFE) that led * to the presence of the
 * annotated API element.
 */
public @interface RequestForEnhancement {
    int id();
    String synopsis();
    String engineer() default "[unassigned]";
    String date() default "[unimplemented]";
}  
```

代码中只定义了一个annotation类型 `RequestForEnhancement`.

2. 修饰方法的annotation声明方式

annotation 是一种修饰符，能够如其它修饰符（如public、static、final）一般使用。习惯用法是annotaions用在其它的修饰符前面。 annotations由"@+annotation类型+带有括号的成员-值列表"组成。这些成员的值必须是编译时常量（即在运行时不变）。

A：下面是一个使用了RequestForEnhancement annotation的方法声明：

```java
@RequestForEnhancement(
        id = 2868724,
        synopsis = "Enable time-travel",
        engineer = "Mr. Peabody",
        date = "4/1/3007"
)
public static void travelThroughTime(Date destination) { ... }
```

B：当声明一个没有成员的annotation类型声明时，可使用以下方式：

```java
/**
    * Indicates that the specification of the annotated API element * is
    * preliminary and subject to change.
    */
public @interface Preliminary {}
```

作为上面没有成员的annotation类型声明的简写方式：

```java
@Preliminary public class TimeTravel { ... }
```

C：如果在annotations中只有唯一一个成员，则该成员应命名为value：

```java
/**
 * Associates a copyright notice with the annotated API element.
 */
public @interface Copyright {
    String value();
}
```

更为方便的是对于具有唯一成员且成员名为value的annotation（如上文），在其使用时可以忽略掉成员名和赋值号（=）：

```java
@Copyright("2002 Yoyodyne Propulsion Systems")
public class OscillationOverthruster { ... }
```

3. 一个使用实例

结合上面所讲的，我们在这里建立一个简单的基于annotation测试框架。首先我们需要一个annotation类型来表示某个方法是一个应该被测试工具运行的测试方法。

```java
import java.lang.annotation.*;
/**
 * Indicates that the annotated method is a test method.
 * This annotation should be used only on parameterless static methods.
 */
@Retention(RetentionPolicy.RUNTIME)     @Target(ElementType.METHOD)     public @interface Test { }
```

### Annotation 类型

Java 注释可以分为3类：

- 标记注释。仅有注释名称，不包含数据。
- 单值注释。与标记注释类似，但提供一段数据。
- 完整注释。有多个数据成员，必须使用更完整的语法，元素的顺序无关紧要

```java
@FullAnnotation(var1="data value 1", var2="data value 2", var3="data value 3")
```

## 自定义注释

步骤如下

1. 定义注释类型 `@interface`
2. 添加成员变量
3. 设置默认值

注释类型看起来很像普通的类，不同的是，可以用注释类注释其他Java代码。

## 注释的元素声明

注解接口中的元素声明实际上是方法声明，一个注解接口的方法可以没有任何参数，没有任何throws语句，并且不能是泛型的。

注解元素的类型为下列之一：

- 基本类型(int, short, long, byte, char, double, float, double)
- String
- Class
- enum
- 注解类型
- 一个由前面所述类型组成的数组

## 标准注解

Java SE在 `java.lang.annotation` 和 `javax.annotation` 包中定义了大量的注解接口，其中四个是元注解，用于描述注解接口的行为属性，其他三个为规则接口，用它们注解你的源代码中的项。

|注解|应用范围|功能|
|---|---|---|
|Deprecated|全部|将项标记为过时的|
|SuppressWarnings|除了包和注解之外的所有情况|阻止某个给定类型的警告信息|
|Override|方法|检查该方法是否覆盖了某一个超类方法|
|PostConstruct|方法|被标记的方法应该在构造之后或移除之前立即本调用|
|PreDestroyResource|类、接口、方法、域|在类或接口上：标记为在其他地方要用到的资源。在方法或域上：为"注入"而标记|
|Resources|类、接口|一个资源数组|
|Generated|全部||
|Target|注解|指定可以应用这个注解的哪些项|
|Retention|注解|指定这个注解可以保留多久|
|Documented|注解|指定这个注解应该包含在注解项的文档中|
|Inherited|注解|指定一个注释，当将它应用于一个类的时候，能够自动被它的子类继承|

## 内置注解，元注解

为注释类型设置相关属性：

### @Target

设置目标范围。

`ElementType` 枚举定义了注释类型可应用的不同程序元素

|enum 值|说明|
|---|---|
|TYPE|类，接口，枚举|
|FIELD|变量|
|METHOD|方法|
|PARAMETER|参数|
|CONSTRUCTOR|构造函数|
|LOCAL_VARIABLE|本地变量|
|ANNOTATION_TYPE|注释类型|
|PACKAGE|包|

### @Retention

和Java编译器处理注释的注释类型的方式有关，有以下几种不同的选择：

- 将注释保留在编译后的类文件中，并在第一次加载类时读取它
- 将注释保留在编译后的类文件中，但在运行时忽略它
- 按照规定使用注释，但是并不将它保留到编译后的类文件中

|`RetentionPolicy`|说明|
|---|---|
|SOURCE|在Java源码中|
|CLASS|在class代码中|
|RUNTIME|在运行时JVM中|

对本地变量的注解只能在源码级别上进行处理。类文件无法描述本地变量。因此，所有的本地变量注解在编译完一个类的时候会被遗弃掉。

同样，对包的注解不能在源码级别之外存在。

注解在文件package-info.java中，只包含包的声明，在这个文件中可以对包进行注解。

### @Documented

添加公共文档
为标记注释，没有成员变量，指注释应该出现在类的Javadoc中，在默认情况下，注释不包括在Javadoc中。

Javadoc使用虚拟机从其类文件(而非源文件)中加载信息。确保JVM从这些类文件中获得生成Javadoc所需信息的唯一方法是将保持性规定为RetentionPolicy.RUNTIME。这样注释就会保留在编译后的类文件中并且又虚拟机加载，然后Javadoc可以从中抽取出来添加到类的HTML文档中。

### @Inherited

父类的注释出现在子类中，大概是这个意思

## 用于编译的注释

- @Override

标记注释，仅用于方法
能够强制方法的代码必须符合超类中该方法的定义格式
检查该方法必须是父类中的方法

- @Deprecated

标记注释，对不应再使用的方法进行注释

- @SuppressWarnings

消除代码的警告

- @Generated

供代码生成工具来使用。任何生成的源代码都可以被注解，从而与程序员提供的代码区分开。例如，代码编辑器可以隐藏生成的代码，或者代码生成器可以移除生成代码的旧版本。每个注解都必须包含一个表示代码生成器的唯一标识符。日期字符串和注解字符串是可选的。

## 用于管理资源的注解

@PostConstruct和@PreDestroy

标记这些注解的方法应该在对象被构件之后，或者在对象被移除之前，紧接着调用。

@Resource

用于资源注入。例如，考虑一下访问数据库的Web应用。当然，数据库访问信息不应该被硬编码到Web应用中。

注释不会改变对编写的程序的编译方式。
元数据是关于数据的数据。在计算机程序的上下文中，元数据是关于代码的数据。
元数据只有在与工具协同工作的时候才有用。
在Java中，注释是当作一个修饰符来使用的，它被置于被注释项之前，中间没有分号。
