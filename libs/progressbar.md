# Progressbar

2026-05-15⭐
@author Jiawei Mao
***
## 简介

Progressbar 是一款运行时开销极低的 JVM 控制台进度条。

```xml
<dependency>
    <groupId>me.tongfei</groupId>
    <artifactId>progressbar</artifactId>
    <version>$VERSION</version>
</dependency>
```

## 声明式用法

**声明式用法**成为使用进度条的首选方式。

简单来说，你可以通过 `ProgressBar.wrap(...)` 方法对流或集合进行包装，后续在遍历、读写该对象时，进度条会自动同步追踪执行进度。对象经进度条包装后，其原有集合 / 流类型不会发生改变。目前支持的集合与流类型如下：

- `T[]`;
- `java.lang.Iterable<T>`;
- `java.util.Iterator<T>`;
- `java.io.InputStream` (can be regarded as an `Iterator<Byte>`);
- `java.io.Reader` (can be regarded as an `Iterator<Char>`);
- `java.io.OutputStream` (dual of `InputStream`);
- `java.io.Writer` (dual of `Reader`);
- `java.util.Spliterator<T>`;
- `java.util.Stream<T>` (actually any `S` such that `S extends BaseStream<T, S>`. When wrapping over a primitive stream, boxing overhead may be incurred).

方法调用的语法为：

```java
ProgressBar.wrap(collection, taskName)
```

也可以使用**Builder 模式**自定义进度条：

```java
ProgressBarBuilder pbb = new ProgressBarBuilder()
    .setXXX().setYYY();  // setting the builder
ProgressBar.wrap(iterable, pbb)
```

### 示例1：遍历 java 集合

如果已知集合大小，进度条的最大值会自动设为该集合容量；否则进度条将显示为**未知 indeterminate 无限滚动样式**。

```java
for (T x : ProgressBar.wrap(collection, "Traversing")) {
    ...
}
```

### 示例2：遍历整数范围

支持 sequential 或 parallel stream：

```java
ProgressBar.wrap(IntStream.range(left, right).parallel(), "Task").forEach(i -> {
        ...
    });
```

### 示例3：读取大文件

对 `java.io.InputStream` 进行包装时，程序会自动检测其是否为文件字节流。若检测成功，将获取文件总字节大小并设为进度条最大值；否则进度条将处于**不确定加载状态**。

```java
ProgressBarBuilder pbb = new ProgressBarBuilder()
    .setTaskName("Reading")
    .setUnit("MiB", 1048576); // setting the progress bar to use MiB as the unit

try (Reader reader = new BufferedReader(new InputStreamReader(
        ProgressBar.wrap(new FileInputStream(f), pbb)
    ))) 
{
    ...
}
```

> [!NOTE]
>
> 对一个 2GB 的文件，读取时间约 2 秒，引入 `ProgressBar` 会引入大约 0.5 秒的延迟。
>
> 使用自定义的 `ProgressInputStream` 会引入约 0.2 秒的延迟。

## 命令式用法

采用 Java **try-with-resources** 语法模式，以确保进度条线程能够安全终止。

若需以**命令式方式**使用进度条，并支持在执行过程中修改进度条状态（例如手动移动进度光标），请使用以下语法：

```java
try (ProgressBar pb = new ProgressBar("Test", 100)) { // name, initial max
 // Use ProgressBar("Test", 100, ProgressBarStyle.ASCII) if you want ASCII output style
  for (T x : collection) {
    ...
    pb.step(); // step by 1
    pb.stepBy(n); // step by n
    ...
    pb.stepTo(n); // step directly to n
    ...
    pb.maxHint(n);
    // reset the max of this progress bar as n. This may be useful when the program
    // gets new information about the current progress.
    // Can set n to be less than zero: this means that this progress bar would become
    // indefinite: the max would be unknown.
    ...
    pb.setExtraMessage("Reading..."); // Set extra message to display at the end of the bar
  }
} // progress bar stops automatically after completion of try-with-resource block
```

## 样式

目前该进度条支持三套视觉样式方案：

1. **COLORFUL_UNICODE_BLOCK（默认）**

   采用带 ANSI 色彩的 Unicode 制表符渲染。适合使用 Menlo、Fira Mono、Source Code Pro、SF Mono 等字体，且终端支持 ANSI 色彩的场景。

2. **UNICODE_BLOCK**

   仅使用 Unicode 制表符进行渲染。

3. **ASCII**

   纯 ASCII 字符渲染。终端字体为 Consolas 或 Andale Mono 时，推荐选用此样式。

可通过进度条 builder 调用 `setStyle` 方法，传入上述任一枚举值，即可完成样式设置。

```java
ProgressBarBuilder pbb = new ProgressBarBuilder()
    .setStyle(ProgressBarStyle.<STYLE>);
```

从 0.10.0 版本开始，你也可以通过builder模式来自定义进度条样式。

```java
ProgressBarBuilder pbb = ProgressBar.builder()
    // ...
    .setStyle(ProgressBarStyle.builder()
                      .colorCode((byte) 33)  // the ANSI color code
                      .leftBracket("{")
                      .rightBracket("}")
                      .block('-')
                      .rightSideFractionSymbol('+')
                      .build()
   )
   // ...
```

## Builder

从 0.7.0 版本起，除标准构造方法外，你还可使用**建造者模式**来自定义进度条。

下文所有以`setXXX`开头的配置方法（包含`showSpeed`）均为可选配置。

```java
ProgressBarBuilder pbb = ProgressBar.builder()
    .setInitialMax(<initial max>)
    .setStyle(ProgressBarStyle.<style>)
    .setTaskName(<taskName name>)
    .setUnit(<unit name>, <unit size>)
    .setUpdateIntervalMillis(<update interval>)
    .setMaxRenderedLength(<max rendered length in terminal>)
    .showSpeed();
  // or .showSpeed(new DecimalFormat("#.##")) to customize speed display
    .setEtaFunction(state -> ...)
  // This function is of type `ProgressState -> Optional<Duration>` 
  // that should output the estimated ETA of the progress.
  // Returning `Optional.empty()` means that ETA is not available.
for (T x : ProgressBar.wrap(collection, pbb)) {
    ...
}
```

```sh
Hello World  11% │█▉               │  238895104/2147483647 (0:00:02 / 0:00:15) 
```

| 属性                                          | 说明                                               |
| --------------------------------------------- | -------------------------------------------------- |
| taskName=""                                   | 任务名称，在进度最左侧显示                         |
| initialMax=-1                                 | 任务总量，比如读取文件，就是文件的 bytes 数        |
| updateIntervalMillis=1000                     | 更新时间间隔，默认 1000 毫秒                       |
| continuousUpdate=false                        | 无论进度数值是否变化，每到达更新间隔就重新渲染一次 |
| style=ProgressBarStyle.COLORFUL_UNICODE_BLOCK | 样式                                               |
| consumer=ProgressBarConsumer                  | 输出进度的方式                                     |
| clearDisplayOnFinish=false                    | 在完成后是否清空 consumer                          |
|                                               |                                                    |

## 集成Logger

与日志框架（如 SLF4J）集成时，需要调整进度条的处理方式。

实现该集成需使用专用的 `DelegatingProgressBarConsumer`，并将日志输出方法（如 `logger::info` 或其他日志级别）以 Lambda 表达式形式传入。

```java
// create logger using slf4j
final Logger logger = LoggerFactory.getLogger("Test");

try (ProgressBar pb = new ProgressBarBuilder()
        .setInitialMax(100)
        .setTaskName("Test")
        .setConsumer(new DelegatingProgressBarConsumer(logger::info))
        .build()) {
    // your taskName here
}
```

## 参考

- https://tongfei.me/progressbar/