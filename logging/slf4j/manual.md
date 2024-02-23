# SLF4J 用户手册

- [SLF4J 用户手册](#slf4j-用户手册)
  - [简介](#简介)
  - [Hello World](#hello-world)
  - [使用的典型模式](#使用的典型模式)
  - [Fluent Logging API](#fluent-logging-api)
  - [日志框架](#日志框架)
  - [参考](#参考)

@author Jiawei Mao
***

## 简介

Java 简单日志外观（Simple Logging Facade for Java, SLF4J）作为它日志框架的抽象层，从而可以在部署时使用任何自己喜欢的日志框架。如 java.util.logging, log4j 1.x, reload4j 以及 logback。

SLF4J 只有 slf4j-api-x.x.xx.jar 一个必需依赖项。如果在类路径上没有任何绑定框架，则 SLF4J 默认不执行任何操作。
## Hello World

按照惯例，下面是使用 SLF4J 输出 "Hello World" 的最简单方法。它首先吼的一个名为 "HelloWorld" 的 logger，然后用该 logger 记录 "Hello World" 日志消息。

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorld{
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(HelloWorld.class);
        logger.info("Hello World");
    }
}
```

如果类路径上只有 slf4j-api.jar，运行后控制台输出如下信息：

```
SLF4J: No SLF4J providers were found.
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See https://www.slf4j.org/codes.html#noProviders for further details.
```

如果使用的是 SLF4J 1.7 之前的版本，信息稍有不同：

```
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See https://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
```

该信息表示在类路径没有找到 slf4j provider。

在类路径添加 provider 后该警告信息会消失。slf4j-simple.jar 是最简单的日志框架（将日志输出到控制台），将其添加到类路径：

- slf4j-api-2.0.10.jar
- slf4j-simple-2.0.10.jar

编译并运行，在控制台得到如下输出：

```
[main] INFO mjw.slf4j.HelloWorld - Hello World
```
## 使用的典型模式

下面的示例代码演示 SLF4J 的典型使用模式。

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wombat {

    final Logger logger = LoggerFactory.getLogger(Wombat.class);
    Integer t;
    Integer oldT;

    public void setTemperature(Integer temperature) {
        oldT = t;
        t = temperature;

        logger.debug("Temperature set to {}. Old value was {}.", t, oldT);

        if (temperature.intValue() > 50) {
            logger.info("Temperature has risen above 50 degrees.");
        }
    }
}
```

```ad-note
上面的 {} 被后面的参数依次替代。
```

## Fluent Logging API

SLF4J 2.0.0（需要 Java 8）引入了向后兼容的 fluent logging API，向后兼容，指不需要修改现有日志框架就能受益于 fluent API。

其思想是使用 `LoggingEventBuilder` 逐步构建日志事件，在完全构建日志后再记录。`org.slf4j.Logger` 接口中加入的 `atTrace()`, `atDebug()`, `atInfo()`, `atWarn()` 和 `atError()` 方法都返回 `LoggingEventBuilder` 实例。对禁用的 log-level，返回的 `LoggingEventBuilder` 不执行任何操作，因此保留了传统日志接口的纳秒级性能。

在使用 fluent API 时，最后必须调用 `log()` 或其变体方法。忘记调用 log() 方法会导致日志没有记录上。幸运的是，如果忘记调用 `log()`，有些 IDE 会发出[编译期警告](https://slf4j.org/faq.html#missingLogMethodCall)。

下面是一些使用示例。

语句：

```java
logger.atInfo().log("Hello world.");
```

等价于：

```java
logger.info("Hello world.");
```

以下日志语句输出相同：

```java
int newT = 15;
int oldT = 16;

// 传统 API
logger.debug("Temperature set to {}. Old value was {}.", newT, oldT);

// fluent API
logger.atDebug().log("Temperature set to {}. Old value was {}.", newT, oldT);
   
// fluent API：逐个添加参数，最后 log 消息
logger.atDebug().setMessage("Temperature set to {}. Old value was {}.").addArgument(newT).addArgument(oldT).log();

// fluent API：使用 Supplier 提供参数
// Assume the method t16() returns 16.
logger.atDebug().setMessage("Temperature set to {}. Old value was {}.").addArgument(() -> t16()).addArgument(oldT).log();
```

fluent API 允许将许多不同类型的数据添加到 `org.slf4j.Logger`，还不会导致 `Logger` 接口方法过多。

现在可以传入多个 marker，传递带 Supplier 的参数，或传入多个 key-value 对。其中 key-value 对与日志数据分析工具结合使用非常方便。

如下日志语句等价：

```java
int newT = 15;
int oldT = 16;

// using classical API
logger.debug("oldT={} newT={} Temperature changed.", oldT, newT);

// using fluent API 
logger.atDebug().setMessage("Temperature changed.").addKeyValue("oldT", oldT).addKeyValue("newT", newT).log();
```

key-value 对作为单独对象保存。org.slf4j.Logger 中的默认实现将 key-value 对放在消息前面。日志后端可以重新定制行为。
## 日志框架

如前所述，SLF4J 旨在作为各种日志框架的包装层。SLF4J 提供了几个称为 provider 的 jar 文件，每个对应一个支持的日志框架。

|框架|说明|
|---|---|
|slf4j-log4j12-2.0.10.jar |绑定log4j 1.2，一个应用广泛的日志框架。由于 log4j 1.x 在 2015 年和2022年宣布 EOL，SLF4J-log4j 模块从 1.7.35 开始自动重定向到 SLF4J-reload4j 模块。如果需要继续使用 log4j 1.x 框架，建议使用 slf4j-reload4j。|
|slf4j-reload4j-2.0.10.jar| SINCE 1.7.33：reload4j 框架的 provider。Reload4j 是 log4j 1.2.7 的替代品。同时需要将 reload4j.jar 放在类路径|
|slf4j-jdk14-2.0.10.jar|绑定 java.util.logging，即 JDK 1.4 logging|
|slf4j-nop-2.0.10.jar|绑定NOP，即丢弃所有日志|
|slf4j-simple-2.0.10.jar|绑定简单实现，输出所有日志到 `System.err`。只输出 level 高于或等于 INFO 的信息|
|slf4j-jcl-2.0.10.jar|绑定 Apache Commons Logging 框架，即 Jakarta Commons Logging（JCL）|
|logback-classic-1.4.6.jar|需要 logback-core-1.4.6.jar，用于 Jakarta EE|
|logback-classic-1.3.6.jar|用于 Javax EE，需要 logback-core-1.3.6.jar|

```ad-note
logback 本地实现了 SLF4J。Logback 的 `ch.qos.logback.classic.Logger` 类直接实现了 `org.slf4j.Logger`，因此 SLF4J 与 logback 结合使用，额外开销最小。
```

要切换日志框架，只需替换类路径上的 slf4j-bindings。例如，要从 java.util.logging 切换到 reload4j，只需将 slf4j-jdk14-2.0.10.jar 替换为 slf4j-reload4j-2.0.10.jar。

**SINCE 2.0.0**，SLF4J bindings 改名为 providers。不同总体思路一样，SLF4J 2.0.0 通过 ServiceLoader 机制查找日志后端。

下面是总体思路图示：

![](images/2024-02-22-11-14-46.png)

SLF4J 接口及其各种 adapter 非常简单。大多数熟悉 Java 语言的开发人员应该能在一小时内阅读并完全理解代码。

## 参考

- https://slf4j.org/manual.html