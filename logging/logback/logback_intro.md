# logback 概述

- [logback 概述](#logback-概述)
  - [简介](#简介)
  - [Hello World](#hello-world)
  - [参考](#参考)

2024-02-22 ⭐
@author Jiawei Mao
***

## 简介

logback 旨在作为 log4j 的后续版本进行开发，其速度比现有日志框架都要快，占用空间更小。

## Hello World

logback-classic 模块需要 slf4j-api.jar, logback-core.jar, logback-classic.jar 三个依赖项。

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorld1 {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger("chapters.introduction.HelloWorld1");
        logger.debug("Hello world.");
    }
}
```

`HelloWorld1` 类定义在 `chapters.introduction` 包中。首先导入 slf4j 的 `Logger` 和 `LoggerFactory` 类。

静态方法 `LoggerFactory.getLogger("chapters.introduction.HelloWorld1")` 创建名为 `chapters.introduction.HelloWorld1` 的 logger。`debug()` 方法创建 `DEBUG` 级别的日志。

上述示例未引入任何 logback 类。在大多数情况下，只需要导入 slf4j 类，不需要关注 logback 类。

运行该示例，在控制台输出如下信息：

```
12:48:30.575 [main] DEBUG chapters.introduction.HelloWorld1 -- Hello world.
```

这是因为此时没有添加任何配置，logback 默认采用 `ConsoleAppender`，即将日志信息输出到控制台。

logback 可以使用内置系统报告其内部状态信息。可以通过 `StatusManager` 访问 logback 生命周期内的重要事件。下面通过 `StatusPrinter` 的 `print()` 方法打印 logback 内部状态：

```java
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorld2{

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger("chapters.introduction.HelloWorld2");
        logger.debug("Hello world.");

        // print internal state
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(lc);
    }
}
```

```
12:49:22.203 [main] DEBUG chapters.introduction.HelloWorld2 - Hello world.
12:49:22,076 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback.groovy]
12:49:22,078 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback-test.xml]
12:49:22,093 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback.xml]
12:49:22,093 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Setting up default configuration.
```

logback  说它没有找到 logback-test.xml 和 logback.xml 配置文件，因此使用默认配置，即 `ConsoleAppender`。`Appender` 表示日志输出位置，包括控制台、文件、Syslog, TCP Sockets, JMS 等。用户也可以根据自己的需求创建自己的 appenders。

如果出现错误，logback 会自动在控制台输出其内部状态。

上面的例子十分简单，不过大型应用中的日志也差不离，只是配置上有所不同。

在应用中使用logback 只需三步：

1. 配置 logback 环境
2. 在类中通过 `org.slf4j.LoggerFactory` 的 `getLogger()` 方法获得 `Logger` 实例
3. 使用 logger 实例的方法输出日志信息，如 `debug()`, `info()`, `warn()` 和 `error()` 等。

## 参考

- https://logback.qos.ch/manual/introduction.html