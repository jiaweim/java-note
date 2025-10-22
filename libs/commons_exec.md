# Apache Commons Exec



## 简介

在 Java 执行外部进程是一个比较麻烦的问题，它依赖于平台，要求开发人员了解并测试平台相关的行为，JRE 对此支持非常有限，如 `ProcessBuilder` 类。可靠地执行外部进程还需要在执行命令前了解环境变量。commons-exec 提供相关的辅助工具。

目前有几个不同的库围绕 `Runtime.exec()` 实现框架处理上述问题。

## 主要类

- `CommandLine` 负责解析和构建命令行，处理参数引号，支持变量替换
- 

## 指南

下面通过示例来解释 commons-exec 的功能。

首先添加 Maven 依赖项：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-exec</artifactId>
    <version>1.5.0</version>
</dependency>
```

### 第一个进程

实现从 Java 中打印 PDF 文档。调用 Adobe Acrobat 执行打印功能。假设在路径中可以找到 Acrobat Reader，打印命令为 "AcroRd32.exe /p /h file"：

```java
String line = "AcroRd32.exe /p /h " + file.getAbsolutePath();
CommandLine cmdLine = CommandLine.parse(line);
DefaultExecutor executor = DefaultExecutor.builder().get();
int exitValue = executor.execute(cmdLine);
```

这里成功打印了第一个 PDF 文档，但最后抛出了异常？Acrobat Reader 执行成功会返回 1，这通常被视为执行失败。因此需要调整代码来解决该问题，即将 "1" 定义为成功执行：

```java
String line = "AcroRd32.exe /p /h " + file.getAbsolutePath();
CommandLine cmdLine = CommandLine.parse(line);
DefaultExecutor executor = DefaultExecutor.builder().get();
executor.setExitValue(1);
int exitValue = executor.execute(cmdLine);
```

### Watchdog

如果打印过程因为各种原因挂起，例如缺纸？commons-exec 提供了 watchdog。下面代码在 60 秒后失败的进程：

```java
String line = "AcroRd32.exe /p /h " + file.getAbsolutePath();
CommandLine cmdLine = CommandLine.parse(line);
DefaultExecutor executor = DefaultExecutor.builder().get();
executor.setExitValue(1);
ExecuteWatchdog watchdog = 	
    	ExecuteWatchdog.builder().setTimeout(Duration.ofSeconds(60)).get();
executor.setWatchdog(watchdog);
int exitValue = executor.execute(cmdLine);
```

### 引号

路径中包含空格会导致命令解析错粗，例如：

```sh
> AcroRd32.exe /p /h C:\Document And Settings\documents\432432.pdf
```

加入引号 ，commons-exec 就会将文件作为单个命令行参数处理：

```java
String line = "AcroRd32.exe /p /h \"" + file.getAbsolutePath() + "\"";
CommandLine cmdLine = CommandLine.parse(line);
DefaultExecutor executor = DefaultExecutor.builder().get();
executor.setExitValue(1);
ExecuteWatchdog watchdog = 
    ExecuteWatchdog.builder().setTimeout(Duration.ofSeconds(60)).get();
executor.setWatchdog(watchdog);
int exitValue = executor.execute(cmdLine);
```

### 逐步构建命令行

前面由于路径包含空格导致解析命令出错。以增量的方式构建命令行不容易出错：

```java
Map map = new HashMap();
map.put("file", new File("invoice.pdf"));
CommandLine cmdLine = new CommandLine("AcroRd32.exe");
cmdLine.addArgument("/p");
cmdLine.addArgument("/h");
cmdLine.addArgument("${file}"); // 不需要单独处理空格问题
cmdLine.setSubstitutionMap(map);
DefaultExecutor executor = DefaultExecutor.builder().get();
executor.setExitValue(1);
ExecuteWatchdog watchdog = 
    ExecuteWatchdog.builder().setTimeout(Duration.ofSeconds(60)).get();
executor.setWatchdog(watchdog);
int exitValue = executor.execute(cmdLine);
```

### 非阻塞

上面构建的打印进程会导致当前工作线程阻塞，直到打印完成或被 watchdog 终结。

因此，更合理的方式是异步执行，下面创建一个 `ExecuteResultHandler` 并传入 `Execute` 使得进程异步执行。`resultHandler` 捕获异常或进程的 exit-code。

```java
CommandLine cmdLine = new CommandLine("AcroRd32.exe");
cmdLine.addArgument("/p");
cmdLine.addArgument("/h");
cmdLine.addArgument("${file}");
HashMap map = new HashMap();
map.put("file", new File("invoice.pdf"));
cmdLine.setSubstitutionMap(map);

DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

ExecuteWatchdog watchdog = 
    ExecuteWatchdog.builder().setTimeout(Duration.ofSeconds(60)).get();
Executor executor = DefaultExecutor.builder().get();
executor.setExitValue(1);
executor.setWatchdog(watchdog);
executor.execute(cmdLine, resultHandler);

// some time later the result handler callback was invoked so we
// can safely request the exit value
resultHandler.waitFor();
int exitValue = resultHandler.getExitValue();
```

## 构建命令行

创建要执行的命令有两种方式：

- 解析整个命令行字符串
- 逐步构建命令行

不管使用哪种方式，commons-exec 都会根据需要修改命令行参数

- 当可执行文件路径包含斜杠
- 当命令行参数包含不带引号的字符串

例如，对如下参数：

```sh
./bin/vim
```

在 Windows 中 commons-exec 会将修改为：

```sh
.\\bin\\vim
```

### 解析完整命令字符串

解析完整的命令字符串使用容易，但是在一些场景会出问题。

**命令行参数中的空格**

下面我们想调用一个 batch 文件，该文件的路径包含空格：

```powershell
cmd.exe /C c:\was51\Web Sphere\AppServer\bin\versionInfo.bat
```

由于文件路径包含空格，必须用单引号或双引号将其括起来，否则将解析为 `c:\was51\Web` 和 `Sphere\AppServer\bin\versionInfo.bat` 两个命令行参数。

```
String line = "cmd.exe /C 'c:\\was51\\Web Sphere\\AppServer\\bin\\versionInfo.bat'";
```

### 逐步构建命令行

推荐采用逐步构建命令行的方法。

**一个简单示例**

现在想构建以下命令：

```
runMemorySud.cmd 10 30 -XX:+UseParallelGC -XX:ParallelGCThreads=2
```

使用如下代码：

```java
CommandLine cmdl = new CommandLine("runMemorySud.cmd");
cmdl.addArgument("10");
cmdl.addArgument("30");
cmdl.addArgument("-XX:+UseParallelGC");
cmdl.addArgument("-XX:ParallelGCThreads=2");
```

**一个复杂示例**

下面是从网上看到的一个命令：

```
dotnetfx.exe /q:a /c:"install.exe /l ""\Documents and Settings\myusername\Local Settings\Temp\netfx.log"" /q"
```

下面使用预先加引号的参数和可变扩展构建命令行：

```java
File file = new File("/Documents and Settings/myusername/Local Settings/Temp/netfx.log");
Map map = new HashMap();
map.put("FILE", file);

cmdl = new CommandLine("dotnetfx.exe");
cmdl.setSubstitutionMap(map);
cmdl.addArgument("/q:a", false);
// 下面的 ${FILE} 引用 map 中的 File 对象
cmdl.addArgument("/c:\"install.exe /l \"\"${FILE}\"\" /q\"", false);
```



## 参考

- https://commons.apache.org/proper/commons-exec/index.html
- https://github.com/apache/commons-exec