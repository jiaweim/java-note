# picocli

2025-11-27
@author Jiawei Mao
***
## 1. 简介

`Picocli` 是一个用于辅助创建命令行应用的工具包，支持多种 JVM 语言。包含如下特性：

- 只有一个源文件，把源码放在项目中就能使用
- 支持 `POSIX`, `GNU` 和 `MS-DOS`等多种命令行语法风格
- 支持通过 ANSI 颜色和样式自定义帮助信息
- 命令输入支持 TAB 自动补全
- 支持选项、选项参数和子命令
- 可以为应用生成漂亮的文档，包括 HTML，PDF, Unix man pages 格式

`picocli` 通过注释完成字段的识别，然后将输入转换为字段对应的类型。

### 示例

下面是一个简短但功能齐全的基于 picocli 的 `checksum` 命令行应用：

```java
@Command(name = "checksum", mixinStandardHelpOptions = true, version = "checksum 4.0",
        description = "Prints the checksum (SHA-256 by default) of a file to STDOUT.")
class CheckSum implements Callable<Integer> {

    @Parameters(index = "0", description = "The file whose checksum to calculate.")
    private File file;

    @Option(names = {"-a", "--algorithm"}, description = "MD5, SHA-1, SHA-256, ...")
    private String algorithm = "SHA-256";

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        byte[] fileContents = Files.readAllBytes(file.toPath());
        byte[] digest = MessageDigest.getInstance(algorithm).digest(fileContents);
        System.out.printf("%0" + (digest.length * 2) + "x%n", new BigInteger(1, digest));
        return 0;
    }

    // 这里实现 Callable，因此解析、错误处理和帮助信息、版本都可以一行代码完成
    static void main(String... args) {
        int exitCode = new CommandLine(new CheckSum()).execute(args);
        System.exit(exitCode);
    }
}
```

实现 `Runnable` 或 `Callable` 接口，一行代码就能解析并运行命令。上例在 `main` 中调用 `CommandLine.execute` 解析命令行、处理错误、使用信息、版本信息以及调用业务逻辑。应用程序可以使用返回的退出代码调用 `System.exit` 向调用者发出成功或失败的信号。

`mixinStandardHelpOptions` 属性添加 `--help` 和 `--version` 选项。 

## 2. 入门

picocli 可以作为外部依赖项使用，也可以直接包含其源代码。
### 2.1 添加依赖项

以下配置 Gradle 或 Maven，将 picocli 作为外部依赖项添加到项目：

```xml
<dependency>
  <groupId>info.picocli</groupId>
  <artifactId>picocli</artifactId>
  <version>4.7.7</version>
</dependency>
```
```groovy
dependencies {
    implementation 'info.picocli:picocli:4.7.7'
}
```

### 2.2 包含源码

从 [picocli GitHub](https://github.com/remkop/picocli/blob/main/src/main/java/picocli/CommandLine.java) 下载源码，直接将 `CommandLine.java` 添加到项目。
### 2.3 Annotation Processor

`picocli-codegen` 模块包含一个注释处理器，该处理器可以在编译时（而不是运行时）从 picocli 注释构建模型。

在项目中启用该注释处理器是可选的，但是**强烈推荐**。它支持：

- **编译时错误检查**：注释处理器在编译时会显示无效注释和错误属性，而不是在运行时才反馈错误，从而缩短反馈周期
- **GraalVm native images:** 注释处理器在编译时生成并更新 `META-INF/native-image/picocli-generated/$project` 下的 GraalVM 配置文件，并包含在 jar 文件中。这包含发射、资源和动态代理的配置文件。通过嵌入这些文件，jar 可以快速启用 Graal。在大多数情况下不需要进一步配置就可以生成 native-imge。

#### Processor 选项：`project`

picocli 注释处理器支持许多选项，其中最重要的是设置输出子目录的 `project` 选项：生成的文件被写入 `META-INF/native-image/picocli-generated/${project}`。一个常用选择是设置为 Maven `${project.groupId}/${project.artifactId}`，一个 unique 子目录可以确保你的 jar 可以与其它可能包含配置文件的 jar 一起。

要配置该选项，将 `-Aproject=<some value>` 传递给 javac 编译器。下面展示如何在 Maven 或 Gradle 执行该操作。

#### 启用注释处理器

skip

### 2.4 运行应用

编译 `CheckSum` 应用后，就可以运行了。在运行前，创建一个示例文件 hello.txt，包含文本 "hello"。

假设我们创建了一个名为 `checksum.jar` 的 jar，包含编译后的 `CheckSum.class`，可以使用 `java -cp <classpath> <MainClass> [OPTIONS]` 运行。例如：

```sh
java -cp "picocli-4.7.7.jar:checksum.jar" CheckSum --algorithm SHA-1 hello.txt
```

通过调整打包方式，还可以使用如下简短命令来调用程序：

```sh
checksum --algorithm SHA-1 hello.txt
```

具体方式参考 [打包应用](#29-打包应用)。


## 3. 选项和参数
命令行参数可以分为选项（*options*）和位置参数（*positional parameters*）两类。
- 选项（option）包含名称
- 位置参数（positional parameter）则是跟着选项后的参数值，两者也可以混合

如下图所示：

<img src="picocli_1.png" width="450" />

对这两类参数，picocli 分别使用 `@Option` 和 `@Parameters` 进行注释。

### 3.1 选项（Options）
选项为命名参数，至少要设置一个名称，默认区分大小写（可以设置）。

> [!TIP]
>
> [常见命名方式](http://catb.org/~esr/writings/taoup/html/ch10s05.html#id2948149)，遵循该命名方式，有经验的用户可以更快上手。

**示例**：展示包含一个或多个名称的选项

```java
class Tar {
    @Option(names = "-c", description = "create a new archive")
    boolean create;

    @Option(names = {"-f", "--file"}, paramLabel = "ARCHIVE", description = "the archive file")
    File archive;

    @Parameters(paramLabel = "FILE", description = "one or more files to archive")
    File[] files;

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
    private boolean helpRequested = false;
}
```

picocli 匹配选项名称和字段：

- `create` 字段选项名称只有一个，即 `-c`
- `archive` 和 `helpRequested` 字段选项名称有两个

```java
String[] args = { "-c", "--file", "result.tar", "file1.txt", "file2.txt" };
Tar tar = new Tar();
new CommandLine(tar).parseArgs(args);

assert !tar.helpRequested;
assert  tar.create;
assert  tar.archive.equals(new File("result.tar"));
assert  Arrays.equals(tar.files, new File[] {new File("file1.txt"), new File("file2.txt")});
```

### 3.2 交互选项（密码）

picocli 3.5 开始支持密码：标记为 `interactive` 的选项和参数，会在控制台提示用户输入值。对 Java 6 以上的版本，picocli 使用 `Console.readPassword` 读取值，以防止密码回显到控制台。

从 picocli 4.6 开始，应用可以通过设置 `echo=true` 选择将用户输入回显到控制台，并支持 `prompt` 文本设置在要求用户输入时在控制台显示的内容。

> [!NOTE]
>
> 交互参数有个限制：它后面必须跟有非交互式位置参数。当前不支持最后一个位置参数为 `interactive`。

#### 示例

下面演示使用交互式选项输入密码。从 picocli 3.9.6 开始，交互选项可以使用 `char[]` 而非 `String` 类型，允许应用在使用后清空数组，避免敏感信息驻留在内容。

```java
class Login implements Callable<Integer> {
    @Option(names = {"-u", "--user"}, description = "User name")
    String user;

    @Option(names = {"-p", "--password"}, description = "Passphrase", interactive = true)
    char[] password;

    public Integer call() throws Exception {
        byte[] bytes = new byte[password.length];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) password[i];
        }

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(bytes);

        System.out.printf("Hi %s, your password is hashed to %s.%n", user, base64(md.digest()));

        // null out the arrays when done
        Arrays.fill(bytes, (byte) 0);
        Arrays.fill(password, ' ');

        return 0;
    }

    private String base64(byte[] arr) { /* ... */ }
}
```

当使用如下方式调用：

```java
new CommandLine(new Login()).execute("-u", "user123", "-p");
```

控制台会提示用户输入值：

```sh
Enter value for --password (Passphrase):
```

在 Java 6+ 上，用户输入不会回显到控制台。用户输入密码并按 Enter 键后，`call()` 方法被调用，并打印如下内容：

```sh
Hi user123, your passphrase is hashed to 75K3eLr+dx6JJFuJ7LwIpEpOFmwGZZkRiB84PURz6U8=.
```

#### 可选交互
交互选项默认会使程序等待标准输入。对需要同时以交互模式和批处理模式运行的命令，如果选项可以选择性地从命令行选择参数会非常有用。

交互选项的 `arity`默认为 0，即不带参数。从 picocli 3.9.6 开始，如果设置 `arity="0..1"`，交互选项也可以从命令行获取值，避免控制台输入需求。例如：

```java
@Option(names = "--user")
String user;

@Option(names = "--password", arity = "0..1", interactive = true)
char[] password;
```

输入如下参数，密码字段被初始化为 "123"，不会提示用户输入：
```
--password 123 --user Joe
```

在批处理时，就不需要暂停输入密码。

但是，如果未输入密码，控制台会提示用户输入一个值。如下所示，密码选项没有参数，因此系统会提示用户在控制台输入值：

```sh
--password --user Joe
```

> [!TIP]
>
> **为批处理脚本提供密码**
>
> 在命令行或脚本中以纯文本形式指定密码不安全。如何安全地提供密码？
>
> 一种方法是添加一个单独的选项，如 `--password:file`，该选项以 `File` 或 `Path` 为参数，因公从文件中读取密码。另一种方法也是添加一个单独选项，如 `--password:env` 以环境变量名为参数，应用从用户的环境变量读取密码。
>
> 将其中任意一种与交互 `--password` 选项结合（默认的 `arity="0"`）使用户无需在命令行以纯文本指定密码。此类命令既可以交互执行，也能以批处理方式执行。

#### 强制交互输入

picocli 只在指定交互选项，且没有提供参数时才提示用户：

```sh
$ myprogram                             # option not specified: no prompting
You provided value 'null'

$ myprogram --interactive-option=abc    # option specified with parameter: no prompting
You provided value 'abc'

$ myprogram --interactive-option        # option specified WITHOUT parameter: prompt for input
Enter value for --interactive-option (...):    #  <--- type xxx and hit Enter
You provided value 'xxx'
```

如果需要程序在未指定选项时提示用户，则需要在业务逻辑中执行该操作。例如：

```java
@Command
public class Main implements Runnable {
    @Option(names = "--interactive", interactive = true)
    String value;

    public void run() {
        if (value == null && System.console() != null) {
            // alternatively, use Console::readPassword
            value = System.console().readLine("Enter value for --interactive: ");
        }
        System.out.println("You provided value '" + value + "'");
    }

    public static void main(String[] args) {
        new CommandLine(new Main()).execute(args);
    }
}
```


### 3.3 Short 选项
Picocli 支持 POSIX 短选项聚类：多个不含参数值的单字符选项，后面最多一个包含参数的选项，可以用一个  '-' 合并。例如：
```java
class ClusteredShortOptions {
    @Option(names = "-a") boolean aaa;
    @Option(names = "-b") boolean bbb;
    @Option(names = "-c") boolean ccc;
    @Option(names = "-f") String  file;
}
```

下面几种命令形式等价：
```sh
<command> -abcfInputFile.txt
<command> -abcf=InputFile.txt
<command> -abc -f=InputFile.txt
<command> -ab -cf=InputFile.txt
<command> -a -b -c -fInputFile.txt
<command> -a -b -c -f InputFile.txt
<command> -a -b -c -f=InputFile.txt
```

### 3.4 Boolean 选项

布尔选项通常不需要参数，只需要在命令行指定选项名称即可。

```java
class BooleanOptions {
    @Option(names = "-x") boolean x;
}
```

`x` 的值默认为 `false`，在命令行指定 `-x` 选项，则将其设置为 `true`。如果命令行多次指定 `-x` 选项，则 `x` 的值始终为 `true`。

在大多数情况这足够了，但 picocli 

### 位置参数（Positional Parameters）

非选项参数命令均被解析为位置参数，一般放在选项后面。


## 4. 强类型
在解析命令行参数时，字符串被转换为对应类型。

### 4.1 内置类型

picocli 开箱即用，可以将命令行参数字符串转换为多种常见类型。

支持 Java 5 的大多数内置类型，对 Java 7 的 `Path` 以及 Java 8 的 `Duration` picocli 也提供了默认 converter。这些 converters 通过反射加载，仅在支持它们的 Java 版本上运行才可用。具体：

- 任意 java primitive 类型和包装类
- 任意 `enum`
- `String`, `StringBuilder`, `CharSequence`
- `java.math.BigDecimal`, `java.math.BigInteger`
- `java.nio.Charset`
- `java.io.File`
- `java.net.InetAddress`
- `java.util.regex.Pattern`
- `java.util.Date` (for values in `"yyyy-MM-dd"` format)
- `java.net.URL`, `java.net.URI`
- `java.util.UUID`
- `java.lang.Class` (from picocli 2.2, for the fully qualified class name)
- `java.nio.ByteOrder` (from picocli 2.2, for the Strings `"BIG_ENDIAN"` or `"LITTLE_ENDIAN"`)
- `java.util.Currency` (from picocli 2.2, for the ISO 4217 code of the currency)
- `java.net.NetworkInterface` (from picocli 2.2, for the InetAddress or name of the network interface)
- `java.util.TimeZone` (from picocli 2.2, for the ID for a TimeZone)

使用反射加载的 converters：

- `java.nio.file.Path` (from picocli 2.2, requires Java 7 or higher)
- `java.time` value objects: `Duration`, `Instant`, `LocalDate`, `LocalDateTime`, `LocalTime`, `MonthDay`, `OffsetDateTime`, `OffsetTime`, `Period`, `Year`, `YearMonth`, `ZonedDateTime`, `ZoneId`, `ZoneOffset` (from picocli 2.2, requires Java 8 or higher, invokes the `parse` method of these classes)
- `java.sql.Time` (for values in any of the `"HH:mm"`, `"HH:mm:ss"`, `"HH:mm:ss.SSS"`, or `"HH:mm:ss,SSS"` formats)
- `java.sql.Timestamp` (from picocli 2.2, for values in the `"yyyy-MM-dd HH:mm:ss"` or `"yyyy-MM-dd HH:mm:ss.fffffffff"` formats)
- `java.sql.Connection` (from picocli 2.2, for a database url of the form `jdbc:subprotocol:subname`)
- `java.sql.Driver` (from picocli 2.2, for a database URL of the form `jdbc:subprotocol:subname`)

### 4.2 自定义 converter
注册自定义类型 converter 以处理内置类型以外的数据类型。

#### 单参数 converter

自定义 converter 需要实现 `picocli.CommandLine.ITypeConverter` 接口：

```java
public interface ITypeConverter<K> {
    /**
     * Converts the specified command line argument value to some domain object.
     * @param value the command line argument String value
     * @return the resulting domain object
     * @throws Exception an exception detailing what went wrong during the conversion
     */
    K convert(String value) throws Exception;
}
```
例如：

```java
import javax.crypto.Cipher;

class CipherConverter implements ITypeConverter<Cipher> {
    public Cipher convert(String value) throws Exception {
        return Cipher.getInstance(value);
    }
}
```

自定义类型 converter 可以使用 `converter` 注释属性指定。例如：

```java
class App {
    @Option(names = "-a", converter = CipherConverter.class)
    javax.crypto.Cipher cipher;
}
```

或者使用 `CommandLine.registerConverter(Class<K> cls, ITypeConverter<K> converter)` 在每个 command 中注册该 converter。所有具有指定类型的选项和位置参数都将由指定的 converter 进行转换。

注册自定义 converter 后，调用注册 converter 的 `CommandLine` 实例的 `execute(String...)` 或 `parseArgs(String...)` 方法。例如：

```java
class App {
    @Parameters java.util.Locale locale;
    @Option(names = "-a") javax.crypto.Cipher cipher;
}
```

```java
App app = new App();
CommandLine commandLine = new CommandLine(app)
    .registerConverter(Locale.class, s -> new Locale.Builder().setLanguageTag(s).build())
    .registerConverter(Cipher.class, s -> Cipher.getInstance(s));

commandLine.parseArgs("-a", "AES/CBC/NoPadding", "en-GB");
assert app.locale.toLanguageTag().equals("en-GB");
assert app.cipher.getAlgorithm().equals("AES/CBC/NoPadding");
```

> [!WARNING]
>
> 对子命令，为了确保 converter 被注册到所有子命令，要添加完子命令后注册 converter。

#### 多参数 converter




## 5. 默认值
可以为选项或位置参数指定默认值。

配置默认值可保证 `@Option` 或 `@Paramters` 注释字段被设置，注释方法被调用，当使用编程 API 时，即使未在命令行指定选项或位置参数，`ArgSpec.setValue` 方法也会被调用。

### 5.1 defaultValue 注释

设置默认值的**推荐方式**是使用 `defaultValue` 注释属性。该方式适用于 `@Option` 和 `@Paramters`，允许注释处理器检测和使用该默认值。

对 `@Option`, `@Paramters` 和 `@Command` 注释方法，只能使用 `defaultValue` 注释属性。例如，对一个注释接口：

```java
interface Spec {
    @Option(names = "-c", defaultValue = "123", description = "... ${DEFAULT-VALUE} ...")
    int count();
}
```

在 command 方法中使用 `defaultValue` 属性：

```java
class CommandMethod {
    @Command(description = "Do something.")
    void doit(@Option(names = "-c", defaultValue = "123") int count) {
        // ...
    }
}
```

注意，可以在选项或位置参数的 `description` 中使用 `${DEFAULT-VALUE}$` 变量，picocli 会显示实际的默认值。

### 5.2 字段值

对注释字段，可以直接声明字段值：

```java
@Option(names = "-c", description = "The count (default: ${DEFAULT-VALUE})")
int count = 123; // default value is 123
```

> [!WARNING]
>
> 在字段声明中定义默认值有一些局限性：
>
> - 当该选项在参数 group 中使用时，usage-help 无法显示默认值
> - picocli 的注释处理器只能检测注释中的默认值，无法检测字段声明中的默认值



## 6. 多个值

多值选项和位置参数可以从命令行捕获多个值。

### 6.1 多次出现


### 6.3 Arity
2025-11-27⭐
使用 `arity` 属性可以控制一个选项对应几个参数值。

`arity` 属性可以指定准确参数数量，也可以指定一个范围或任意数目（使用 "*"）。例如：

```java
class ArityDemo {
    @Parameters(arity = "1..3", description = "one to three Files")
    File[] files;

    @Option(names = "-f", arity = "2", description = "exactly two floating point numbers")
    double[] doubles;

    @Option(names = "-s", arity = "1..*", description = "at least one string")
    String[] strings;
}
```

如果命令行中指定的参数值个数达不到指定的最小量，抛出 `MissingParameterException`。

当获得了最小量的参数，picocli 会检查后续命令行参数，以确实它们是额外参数值还是新的选项。例如：

```sh
ArityDemo -s A B C -f 1.0 2.0 /file1 /file2
```

选项 `-s` 的 arity 为 ：`1..*`，但是它没有消耗所有参数，`-f` 参数被识别为单独选项。

## 7. 必要参数

### 7.1 必要选项

将选项标记为 `required`，要求用户必须在命令行中指定。未指定在 `parse` 方法会抛出 `MissingParameterException` 异常。例如：

```java
class MandatoryOption {
    @Option(names = "-n", required = true, description = "mandatory number")
    int number;

    @Parameters
    File[] files;
}
```

### 7.2 必要参数



### 多值参数（Multiple Values）
多值参数的一个参数对应多个值。

### 重复选项（Repeated Options）
创建重复值选项的最简单方式是创建一个带注释的数组、集合或map。例：
```java
@Option(names = "-option")
int[] values;
```
在命令行通过多次使用选项指定多个值：
```bat
<command> -option 111 -option 222 -option 333
```
所有选项值被添加到集合或数组中。

### 多个位置参数（Multiple Positional Parameters）
注释：

```java
@Parameters
List<TimeUnit> units;
```

示例命令：
```bat
<command> SECONDS HOURS DAYS
```

### 多个Boolean选项
注释：
```java
@Option(names = "-v", description = { "Specify multiple -v options to increase verbosity.", "For example, `-v -v -v` or `-vvv`"})
boolean[] verbosity;
```

参数：
```bat
<command> -v -v -v -vvv
```
上面的命令，会生成6个 true 参数，添加到 `verbosity` 数组。

## 8. Argument Group

## 9. 执行命令

解析命令行参数是第一步，一个强大的应用需要处理许多场景：

1. 用户输入无效
2. 用户请求帮助（可能涉及子命令）
3. 用户请求版本（可能涉及子命令）
4. 运行业务逻级（可能是子命令）
5. 业务逻辑可能抛出异常

picocli 4.0 引入 `execute` 方法用于处理上述场景。例如：

```java
new CommandLine(new MyApp()).execute(args);
```

使用 `execute` 方法，应用代码可以非常紧凑：

```java
@Command(name = "myapp", mixinStandardHelpOptions = true, version = "1.0")
class MyApp implements Callable<Integer> {

    @Option(names = "-x") int x;

    @Override
    public Integer call() { // business logic
        System.out.printf("x=%s%n", x);
        return 123; // exit code
    }

    public static void main(String... args) { // bootstrap the application
        System.exit(new CommandLine(new MyApp()).execute(args));
    }
}
```

虽然只有 15 行，但这是一个完整的程序。除了 `-x` 选项，还提供了 `--help` 和 `--version` 选项：

- 如果用户请求，`execute` 方法会显示帮助或版本信息
- 如果用户输入无效，会弹出有用的错误信息
- 如果用户输入有效，则调用业务逻辑
- `execute` 最后方法一个 exit-code，可用于调用 `System.exit`

> [!WARNING]
>
> 只有实现 `Runnable` 或 `Callable` 接口，或者是带 `@Command` 注释的方法，才能用 `execute` 执行。

### 9.1 Exit Code

许多命令行应用返回一个 exit-code，以表示运行成功还是失败。通常 0 表示成功，非 0 表示出错误。

picocli 4.0 引入的 `CommandLine.execute` 返回一个 `int`，应用程序可以使用其返回值调用 `System.exit`。例如：

```java
public static void main(String... args) {
  int exitCode = new CommandLine(new MyApp()).execute(args);
  System.exit(exitCode);
}
```

### 9.2 生成 Exit Code



## 10. 验证
2025-11-27⭐
### 10.1 内置验证

picocli 提供了一些验证方法：

- [必要选项](#71-必要选项)
- [必要位置参数](#72-必要参数)
- [指定数量参数的选项](#63-arity)
- [允许/不允许单指参数指定多次](#113-覆盖单一选项)
- [mutually dependent](https://picocli.info/#_mutually_dependent_options) options
- [mutually exclusive](https://picocli.info/#_mutually_exclusive_options) options
- [repeating groups](https://picocli.info/#_repeating_composite_argument_groups) of exclusive or dependent options
- [required subcommands](https://picocli.info/#_required_subcommands)
- [type conversion](https://picocli.info/#_handling_invalid_input)
- [unmatched input](https://picocli.info/#_unmatched_input)
- [unknown options](https://picocli.info/#_unknown_options)

### 10.2 自定义验证

许多应用需要进行额外验证以满足业务要求。

使用 `execute` API 在验证失败时抛出 `ParameterException` 比较有用：任何 `ParameterException` 都会被 picocli 内置的错误处理程序捕获，该程序以红色加粗显示错误信息，并附加 usage-help 信息。

构造 `ParameterException` 需要 `CommandLine` 实例。这可以从 `CommandSpec` 获得，而 `CommandSpec` 又可以通过 `@Spec` 注释字段获得。

#### 单值验证

以 `@Option` 和 `@Paramters` 注释的方法对无效参数可以很容易抛出 `ParameterException` 来验证输入参数。

**示例**：验证 `--prime` 选项指定的参数是否为质数

```java
class SingleOptionValidationExample {
    private int prime;

    @Spec CommandSpec spec; // injected by picocli

    @Option(names = {"-p", "--prime"}, paramLabel = "NUMBER")
    public void setPrimeNumber(int value) {
        boolean invalid = false;
        for (int i = 2; i <= value / 2; i++) {
            if (value % i == 0) {
                invalid = true;
                break;
            }
        }
        if (invalid) {
            throw new ParameterException(spec.commandLine(),
                    String.format("Invalid value '%s' for option '--prime': " +
                            "value is not a prime number.", value));
        }
        prime = value;
    }
    // ...
}
```

#### 验证选项组合

一种常见场景是验证多个选项和位置参数的组合是否有效。实现的方法一般是在业务逻辑开头执行验证。

**示例**：验证至少指定了 `--xml`, `--csv` 和 `--json` 选项之一

```java
@Command(name = "myapp", mixinStandardHelpOptions = true, version = "myapp 0.1")
class MultiOptionValidationExample implements Runnable {
    @Option(names="--xml")  List<File> xmlFiles;
    @Option(names="--csv")  List<File> csvFiles;
    @Option(names="--json") List<File> jsonFiles;

    @Spec CommandSpec spec; // injected by picocli

    public static void main(String... args) {
        System.exit(new CommandLine(new MultiOptionValidationExample()).execute(args));
    }

    public void run() {
        validate();

        // remaining business logic here
    }

    private void validate() {
        if (missing(xmlFiles) && missing(csvFiles) && missing(jsonFiles)) {
            throw new ParameterException(spec.commandLine(),
                    "Missing option: at least one of the " +
                    "'--xml', '--csv', or '--json' options must be specified.");
        }
    }

    private boolean missing(List<?> list) {
        return list == null || list.isEmpty();
    }
}
```

#### JSP-380 BeanValidation

如果你想保持验证为声明式的注释，可以查看 [JSR 380](https://jcp.org/en/jsr/detail?id=380).

JSR-380 是一个用于 Bean 验证的 Java API 规范，是 JavaEE 和 JavaSE 的一部分，它使用 `@NotNull`, `@Min` 和 `@Max` 等注释确保 Bean 属性满足特定条件。

picocli wiki 包含一个[完整示例](https://github.com/remkop/picocli/wiki/JSR-380-BeanValidation)，下面是一段摘录：

```java
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.*;
import java.util.Set;

// Example inspired by https://www.baeldung.com/javax-validation
public class User implements Runnable {

    @NotNull(message = "Name cannot be null")
    @Option(names = {"-n", "--name"}, description = "mandatory")
    private String name;

    @Min(value = 18, message = "Age should not be less than 18")
    @Max(value = 150, message = "Age should not be greater than 150")
    @Option(names = {"-a", "--age"}, description = "between 18-150")
    private int age;

    @Email(message = "Email should be valid")
    @Option(names = {"-e", "--email"}, description = "valid email")
    private String email;

    @Spec CommandSpec spec;

    public User() { }

    @Override
    public String toString() {
        return String.format("User{name='%s', age=%s, email='%s'}", name, age, email);
    }

    public static void main(String... args) {
        new CommandLine(new User()).execute(args);
    }

    @Override
    public void run() {
        validate();

        // remaining business logic here
    }

    private void validate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<User>> violations = validator.validate(this);

        if (!violations.isEmpty()) {
            String errorMsg = "";
            for (ConstraintViolation<User> violation : violations) {
                errorMsg += "ERROR: " + violation.getMessage() + "\n";
            }
            throw new ParameterException(spec.commandLine(), errorMsg);
        }
    }
}
```

#### 自定义执行策略

上述 JSR-380 BeanValidation 也可以通过自定义 `IExecutionStrategy` 实现，即在执行命令前先进行验证。这将有验证逻辑移到一个单独的类中。picocli 调用该逻辑，将验证方法从业务逻辑中移除。示例：

```java
class ValidatingExecutionStrategy implements IExecutionStrategy {
    public int execute(ParseResult parseResult) {
        validate(parseResult.commandSpec());
        return new CommandLine.RunLast().execute(parseResult); // default execution strategy
    }

    void validate(CommandSpec spec) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Object>> violations = validator.validate(spec.userObject());
        if (!violations.isEmpty()) {
            String errorMsg = "";
            for (ConstraintViolation<?> violation : violations) {
                errorMsg += "ERROR: " + violation.getMessage() + "\n";
            }
            throw new ParameterException(spec.commandLine(), errorMsg);
        }
    }
}
```

该应用可以按如下方式设置自定义执行策略：

```java
public static void main(String... args) {
    new CommandLine(new MyApp())
            .setExecutionStrategy(new ValidatingExecutionStrategy())
            .execute(args);
}
```

## 11. 配置解析器

### 11.1 区分大小写

默认所有选项和子命令区分大小写。从 picocli 4.3 开始，区分大小写是可配置的。可以全局关闭区分大小写，也可以单独关闭某个命令。

### 11.3 覆盖单一选项
2025-11-27⭐
当在命令多次指定一个单值选项，默认抛出 `OverwrittenOptionException`。例如：

```java
@Option(names = "-p") int port;
```

以下输入或抛出 `OverwrittenOptionException`：

```sh
<command> -p 80 -p 8080
```

在解析命令前调用 `CommandLine::setOverwrittenOptionsAllowed` 设置为 `true` 可以修改该行为。当允许覆盖选项时，指定的最后一个值生效（上述的 `-p 8080`），并安静 WARN-level 信息打印到控制台。

## 14. Usage Help

### 14.1 Compact Example

picocli 默认的使用帮助信息样式：

```sh
Usage: cat [-AbeEnstTuv] [--help] [--version] [FILE...]
Concatenate FILE(s), or standard input, to standard output.
      FILE                 Files whose contents to display
  -A, --show-all           equivalent to -vET
  -b, --number-nonblank    number nonempty output lines, overrides -n
  -e                       equivalent to -vE
  -E, --show-ends          display $ at end of each line
  -n, --number             number all output lines
  -s, --squeeze-blank      suppress repeated empty output lines
  -t                       equivalent to -vT
  -T, --show-tabs          display TAB characters as ^I
  -u                       (ignored)
  -v, --show-nonprinting   use ^ and M- notation, except for LFD and TAB
      --help               display this help and exit
      --version            output version information and exit
Copyright(c) 2017
```

帮助信息从注释属性生成，如下：

```java
@Command(name = "cat", footer = "Copyright(c) 2017",
         description = "Concatenate FILE(s), or standard input, to standard output.")
class Cat {

    @Parameters(paramLabel = "FILE", description = "Files whose contents to display")
    List<File> files;

    @Option(names = "--help", usageHelp = true, description = "display this help and exit")
    boolean help;

    @Option(names = "-t",                 description = "equivalent to -vT")  boolean t;
    @Option(names = "-e",                 description = "equivalent to -vE")  boolean e;
    @Option(names = {"-A", "--show-all"}, description = "equivalent to -vET") boolean all;

    // ...
}
```

### 14.2 Command Name



## 15. ANSI 颜色和样式

## 16. Usage Help API

为了进一步定制 usage-help 信息,picocli 提供了一个 Help API。`Help` 类提供了许多高级操作和组件,如 `Layout`, `TextTable`, `IOptionRenderer` 等，可用于构建自定义 help 信息。Help API 的细节不在本文讨论范围,下面提供一些基本概念。

## 17. Subcommands

## 18. Reuse

如果你发现在许多命令行应用中定义许多相同选项、参数或命令属性。为了减少重复，picocli 提供了两种重复使用选项和属性的方法：subclassing 和 mixin。

### 18.1 Subclassing

## 19. 国际化

从 3.6 开始，usage-help 中选项和位置参数的描述信息可以用 resource-bundle 指定。resource-boundle 可以通过注释和编程指定。

为了快速入门，[`picocli-examples`](https://github.com/remkop/picocli/blob/main/picocli-examples) 提供了一个 i18n 示例，用 Java 和 Kotlin 编写。

### 19.1 配置

注释示例：

```java
@Command(name = "i18n-demo", resourceBundle = "my.org.I18nDemo_Messages")
class I18nDemo {}
```

编程示例：

```java
@Command class I18nDemo2 {}

CommandLine cmd = new CommandLine(new I18nDemo2());
cmd.setResourceBundle(ResourceBundle.getBundle("my.org.I18nDemo2_Messages"));
```

### 19.2 Resource Bundle 示例

属性资源包示例：

```properties
# Usage Help Message Sections
# ---------------------------
# Numbered resource keys can be used to create multi-line sections.
usage.headerHeading = This is my app. It does stuff. Good stuff.%n
usage.header   = header first line
usage.header.0 = header second line
usage.descriptionHeading = Description:%n
usage.description.0 = first line
usage.description.1 = second line
usage.description.2 = third line
usage.synopsisHeading = Usage:\u0020

# Leading whitespace is removed by default.
# Start with \u0020 to keep the leading whitespace.
usage.customSynopsis.0 =      Usage: ln [OPTION]... [-T] TARGET LINK_NAME   (1st form)
usage.customSynopsis.1 = \u0020 or:  ln [OPTION]... TARGET                  (2nd form)
usage.customSynopsis.2 = \u0020 or:  ln [OPTION]... TARGET... DIRECTORY     (3rd form)

# Headings can contain the %n character to create multi-line values.
usage.parameterListHeading = %nPositional parameters:%n
usage.optionListHeading = %nOptions:%n
usage.commandListHeading = %nCommands:%n
usage.footerHeading = Powered by picocli%n
usage.footer = footer

# Option Descriptions
# -------------------
# Use numbered keys to create multi-line descriptions.

# Example description for an option `@Option(names = "-x")`
x = This is the description for the -x option

# Example multi-line description for an option `@Option(names = "-y")`
y.0 = This is the first line of the description for the -y option
y.1 = This is the second line of the description for the -y option

# Example descriptions for the standard help mixin options:
help = Show this help message and exit.
version = Print version information and exit.

# Exit Code Description
# ---------------------
usage.exitCodeListHeading = Exit Codes:%n
usage.exitCodeList.0 = \u00200:Successful program execution. (notice leading space '\u0020')
usage.exitCodeList.1 = 64:Usage error: user input for the command was incorrect.
usage.exitCodeList.2 = 70:An exception occurred when invoking the business logic of this command.

# @file Description
# -----------------
picocli.atfile=One or more argument files containing options, commands and positional parameters.

# End-of-Options (--) Delimiter Description
# -----------------------------------------
picocli.endofoptions = Separates options from positional parameters.
```



## 20. 变量插入



## 21. 技巧和窍门

### 21.3 @Command 方法

从 picocli 3.6 开始，可以用 `@Command` 注释方法。方法参数提供命令选项和参数。例如：

```java
class Cat {
    public static void main(String[] args) {
        Method doit = CommandLine.getCommandMethods(Cat.class, "cat").get(0);
        CommandLine cmd = new CommandLine(doit);
        int exitCode = cmd.execute(args);
    }

    @Command(description = "Concatenate FILE(s) to standard output.",
             mixinStandardHelpOptions = true, version = "4.1.3")
    int cat(@Option(names = {"-E", "--show-ends"}) boolean showEnds,
             @Option(names = {"-n", "--number"}) boolean number,
             @Option(names = {"-T", "--show-tabs"}) boolean showTabs,
             @Option(names = {"-v", "--show-nonprinting"}) boolean showNonPrinting,
             @Parameters(paramLabel = "FILE") File[] files) {
        // process files
        return CommandLine.ExitCode.OK;
    }
}
```

该命令的 usage-help 样式：

```sh
Usage: cat [-EhnTvV] [FILE...]
Concatenate FILE(s) to standard output.
      [FILE...]
  -E, --show-ends
  -h, --help               Show this help message and exit.
  -n, --number
  -T, --show-tabs
  -v, --show-nonprinting
  -V, --version            Print version information and exit.
```



## 22. 依赖注入

## 23. Java 9 JPMS Modules



## 24. OSGi Bundle

⭐

从 4.0 开始，picocli JAR 称为 OSGi bundle，名称为 `picocli`。

## 25. Tracing

## 26. TAB 自动完成

2025-11-27⭐

picocli 可以在 Bash 或 Zsh Unix shell 中实现命令补全。请参考  [Autocomplete for Java Command Line Applications](https://picocli.info/autocomplete.html) 手册了解如何生成适合你应用的自动补全脚本。

## 27. 生成手册文档

## 28. 测试应用

## 29. 打包应用

完成应用后，如何打包和分发给用户？

运行应用的一种方式：

```java
java -cp "picocli-4.7.7.jar;myapp.jar" org.myorg.MyMainClass --option=value arg0 arg1
```

这比较繁琐。你可能希望用户可以直接通过命令名称调用，例如：

```sh
mycommand --option=value arg0 arg1
```

毕竟，在 `@Command(name = "mycommand")` 中定义了命令名称，我们希望用户可以直接使用该名称运行应用。

以下是关于如何实现该目标的一些想法。

### 29.1 alias



## 参考
- <https://github.com/remkop/picocli>
- https://picocli.info/