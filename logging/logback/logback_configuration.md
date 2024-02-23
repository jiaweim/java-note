# logback 配置

## 简介

下面介绍配置 logback 的方法，并提供许多配置脚本。在后面会介绍 logback 依赖的配置框架 Joran。

## 初始化时的配置

logback 既可以通过编程方式配置，也可以使用 XML、Groovy 或序列化模型格式配置。另外，log4j 用户可以使用 [PropertiesTranslator](https://logback.qos.ch/translator/) 网页应用将 log4j.properties 属性文件转换为 logback.xml 配置文件。

logback 初始化配置的步骤：

1. logback 使用 `ServiceLoader` 搜索自定义 `Configurator` providers。如果找到任何这样的自定义程序，则优于 logback 的配置，如 `DefaultJoranConfigurator` 。

自定义配置器是 `ch.qos.logback.classic.spi.Configurator` 接口的实现，logback 通过查找位于 `META-INF/services/ch.qos.logback.classic.spi.Configurator` 中的类来搜索自定义 `Configurator`。该文件的内容应该指定所需 `Configurator` 实现的完全限定名。

2. **SINCE 1.3.9/1.4.9** 如果在上一步中没有找到用户自定义配置器，logback 将实例化一个 `SerializedModelConfigurator`。

- 如果设置了 *logback.scmoFile* 系统属性，`SerializedModelConfigurator` 将尝试查找该系统属性指定的文件，并根据文件内容进行配置。
- 如果未设置上述系统属性，或无法找到指定文件，`SerializedModelConfigurator` 将在类路径中搜索序列化配置模型文件 *logback-test.scmo*。如果找到该文件，读取并配置。
- 如果找不到上述文件，`SerializedModelConfigurator` 将搜索序列化配置文件 *logback-test.scmo*。如果找到该文件，读取并配置。
- 如果没找到任何序列化配置文件，`SerializedModelConfigurator` 将返回一个执行状态，请求下一个配置器，即 `DefaultJoranConfigurator`。

从序列化模型文件的配置执行速度更快，并且不需要任何 XML 库。结合 GraalVM，能获得更小的可执行文件，启动速度更快。

3. 如果前面的配置器无法找到所需配置文件，将创建并调用 `DefaultJoranConfigurator`。

- 如果设置了系统属性 *logback.configurationFile*，`DefaultJoranConfigurator` 读取系统属性对应的文件，配置 logback；
- 如果上一步失败，`DefaultJoranConfigurator` 将尝试在类路径上搜索 *logback-text.xml* 配置文件，读取并配置 logback；
- 如果上一步失败，`DefaultJoranConfigurator` 继续尝试在类路径上搜索 *logback-xml* 配置文件，读取并配置 logback；
- 如果没找到任何配置文件，`DefaultJoranConfigurator` 返回一个执行状态，并请求下一个配置器，即 `BasicConfigurator`。

4. 如果以上步骤都没有成功，logback-classic 将使用 `BasicConfigurator`，将日志记录输出到控制台。 

最后一步是在没有配置文件下的默认选择。

**GROOVY**

考虑到 GROOVY 已经是一个成熟的语言，logback 放弃了 logback.groovy 支持。

如果根据 Maven 的文件夹结构使用构建工具，那么将 logback-test.xml 放在 src/text/resources 文件夹，Maven 能保证它不会包含在生成的文件中。因此，可以在测试期间使用 logback-test.xml，在生产中使用 logback.xml。

**快速启动**

Joran 解析 logback 配置文件大概需要 100 毫秒。为了减少启动时间，可以使用 `ServiceLoader`（上面的第一项）来加载自定义的 `Configurator` 类，可以参考 `BasicConfigurator` 来自定义实现 `Configurator`。

将 XML 文件转换为序列化模型格式也能减少启动时间。

## logback 默认配置

配置 logback 的最简单方法是让 logback 回滚到默认配置。例如：

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyApp1 {
    final static Logger logger = LoggerFactory.getLogger(MyApp1.class);

    public static void main(String[] args) {
        logger.info("Entering application.");

        Foo foo = new Foo();
        foo.doIt();
        logger.info("Exiting application.");
    }
}
```

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Foo {
    static final Logger logger = LoggerFactory.getLogger(Foo.class);

    public void doIt() {
        logger.debug("Did it again!");
    }
}
```

假设没有 logback-test.xml 和 logback.xml 配置文件，logback 使用默认的 `BasicConfigurator`：

- 输出位置：输出日志到控制台（`ConsoleAppender`）
- 输出格式(`PatternLayoutEncoder`)，`%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n`
- 默认级别：DEBUG

因此，上例在控制台有类似如下输出：

```
16:06:09.031 [main] INFO  chapters.configuration.MyApp1 -- Entering application.
16:06:09.046 [main] DEBUG chapters.configuration.Foo -- Did it again!
16:06:09.046 [main] INFO  chapters.configuration.MyApp1 -- Exiting application.
```

## 使用 logback-test.xml 或 logback.xml 进行配置

logback 会尝试在类路径下查找 logback-test.xml 和 logback.xml 文件进行配置。下面的 XML 配置文件(*sample0.xml*)等效于 `BasicConfigurator` 默认配置：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration>

<configuration>
  <import class="ch.qos.logback.classic.encoder.PatternLayoutEncoder"/>
  <import class="ch.qos.logback.core.ConsoleAppender"/>

  <appender name="STDOUT" class="ConsoleAppender">
    <encoder class="PatternLayoutEncoder">
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} -%kvp- %msg%n</pattern>
    </encoder>
  </appender>

  <root level="debug">
    <appender-ref ref="STDOUT"/>
  </root>
</configuration>
```

将 *sample0.xml* 文件重命名为 *logback.xml* 或 *logback-text.xml*，并放在类路径可访问的目录中。运行 `MyApp1`，其效果与上例类似。

## 输出错误配置信息

如果在解析配置文件时出错，logback 会自动将错误信息输出到控制台；如果已指定状态监听器，logback 会禁用该功能。

配置没有出错，也可以主动查询 logback 内部状态，方法有两种：

### 使用 StatusPrinter

可以使用 `StatusPrinter.print()` 查看 logback 内部状态：

```java
public static void main(String[] args) {
    // 假设SLF4J和logback绑定
    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
    // 打印 logback 的内部状态
    StatusPrinter.print(lc);
    …
}
```

### 状态数据

启用状态数据的输出对日志返回问题的诊断很有帮助。需要注意的是，错误可能发生在配置之后，如磁盘已满，或日志文件由于权限问题无法归档等。因此，强烈建议注册一个 `StatusListener`。

下例演示 `OnConsoleStatusListener`：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration>

<configuration>
  <import class="ch.qos.logback.core.status.OnConsoleStatusListener"/>
  <statusListener class="OnConsoleStatusListener"/>
</configuration>
```

使用配置文件设置 `StatusListener` 的要求：

1. 能找到配置文件
2. 配置文件是良好的 XML 格式


将 configuration 元素的 `debug` 属性设置为 `true` 也能注册 `OnConsoleStatusListener`：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration>

<configuration debug="true">
  <import class="ch.qos.logback.classic.encoder.PatternLayoutEncoder"/>
  <import class="ch.qos.logback.core.ConsoleAppender"/>

  <appender name="STDOUT" class="ConsoleAppender">
    <encoder class="PatternLayoutEncoder">
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} -%kvp- %msg%n</pattern>
    </encoder>
  </appender>

  <root level="debug">
    <appender-ref ref="STDOUT"/>
  </root>
</configuration>
```

设置 `debug="true"` 与前面安装 `OnConsoleStatusListener` 完全等效。

## 直接调用 JoranConfigurator

logback 的配置依赖于 Joran 库，该库为 logback-core 的一部分。Logback的默认配置机制调用JoranConfigurator 处理类路径上的默认配置文件。如果要修改覆盖这种行为，可直接调用JoranConfigurator类。

logback 在找到配置文件后会调用 JoranConfigurator 进行配置，如下所示：

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

public class MyApp3 {
  final static Logger logger = LoggerFactory.getLogger(MyApp3.class);

  public static void main(String[] args) {
    // assume SLF4J is bound to logback in the current environment
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

    try {
      JoranConfigurator configurator = new JoranConfigurator();
      configurator.setContext(context);
      // Call context.reset() to clear any previous configuration, e.g. default
      // configuration. For multi-step configuration, omit calling context.reset().
      context.reset();
      configurator.doConfigure(args[0]);
    } catch (JoranException je) {
      // StatusPrinter will handle this
    }
    StatusPrinter.printInCaseOfErrorsOrWarnings(context);

    logger.info("Entering application.");

    Foo foo = new Foo();
    foo.doIt();
    logger.info("Exiting application.");
  }
}
```

先获得当前起作用的LoggerContext，然后创建JoranConfigurator实例，设置其作用的context实例，重置context，然后载入配置文件进行配置。

## 停止 logback-classic

通过停止logback context，可以释放logback-classic占用的资源。停止context会关闭所有和对应logger连接的appender，并停止对应的线程：

```java
import org.sflf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;
...

// assume SLF4J is bound to logback-classic in the current environment
LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
loggerContext.stop();
```

## 配置文件语法


