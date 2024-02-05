# picocli

@author Jiawei Mao
***
## 简介

`Picocli` 是一个用于辅助创建命令行应用的工具包，支持多种 JVM 语言。包含如下特性：

- 源码文件只有一个，把源码放在项目中就能使用；
- 支持 `POSIX`, `GNU` 和 `MS-DOS`等多种命令行语法风格；
- 生成的帮助信息可通过 ANSI 颜色和样式自定义；
- 命令输入支持 TAB 自动补全。

`picocli` 通过注释完成字段的识别，然后将输入转换为字段对应的类型。

### 示例

下面是一个简短但功能齐全的基于 picocli 的 `checksum` 命令行应用：

```java
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.concurrent.Callable;

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
        System.out.printf("%0" + (digest.length*2) + "x%n", new BigInteger(1, digest));
        return 0;
    }

    // this example implements Callable, so parsing, error handling and handling user
    // requests for usage help or version help can be done with one line of code.
    public static void main(String... args) {
        int exitCode = new CommandLine(new CheckSum()).execute(args);
        System.exit(exitCode);
    }
}
```

上例调用 `CommandLine.execute` 解析命令行。
## 快速入门

picocli 可以作为外部依赖项使用，也可以直接包含其源代码。
### 添加外部依赖

```xml
<dependency>
  <groupId>info.picocli</groupId>
  <artifactId>picocli</artifactId>
  <version>4.7.5</version>
</dependency>
```
### 包含源码

从 [picocli GitHub](https://github.com/remkop/picocli/blob/main/src/main/java/picocli/CommandLine.java) 下载源码，直接添加到项目中。
### Annotation Processor




# 选项（Options）和参数（Parameters）
命令行参数可以分为选项（*options*）和位置参数（*positional parameters*）两类。
- 选项（option）具有名称的参数；
- 位置参数（positional parameter）则是跟着选项后的参数值。

如下图所示：

![picocli_1](picocli_1.png)

对这两类参数，picocli 分别使用 `@Option` 和 `@Parameters` 进行注释。

## 选项（Options）
选项为命名参数，而且一个选项可以包含多个名称。例如：

```java
import org.testng.annotations.Test;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.Arrays;

public class Tar
{
    @Option(names = "-c", description = "create a new archive")
    boolean create;

    @Option(names = {"-f", "--file"}, paramLabel = "ARCHIVE", description = "the archive file")
    File archive;

    @Parameters(paramLabel = "FILE", description = "one or more files to archive")
    File[] files;

    @Option(names = {"-h", "--hep"}, usageHelp = true, description = "display a help message")
    private boolean helpRequested = false;

    @Test
    public void test()
    {
        String[] args = {"-c", "--file", "result.tar", "file1.txt", "file2.txt"};
        Tar tar = new Tar();
        new CommandLine(tar).parse(args);

        assert !tar.helpRequested;
        assert tar.create;
        assert tar.archive.equals(new File("result.tar"));
        assert Arrays.equals(tar.files, new File[]{new File("file1.txt"), new File("file2.txt")});
    }
}
```

解释：
- `create` 字段选项名称只有一个，即 `-c`；
- `archive` 和 `helpRequested` 字段选项名称有两个

## 交互式选项（Interactive Options, Password）

对标记为 `interactive` 的选项和参数，会提示在控制台输入值。对 Java 6 以上的版本，
picocli 使用 `Console.readPassword` 读取值，以防止密码在控制台显示。

### 实例

下面演示使用交互式选项输入密码。例：

```java
import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.Callable;

public class Login implements Callable<Object>
{
    @Option(names = {"-u", "--user"}, description = "User name")
    String user;

    @Option(names = {"-p", "--password"}, description = "Passphrase", interactive = true)
    char[] password;

    @Override public Object call() throws Exception
    {
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

        return null;
    }

    private String base64(byte[] arr)
    {
        return Base64.getEncoder().encodeToString(arr);
    }

    public static void main(String[] args)
    {
        CommandLine.call(new Login(), "-u", "user123", "-p");
    }
}
```

运行时，控制台弹出提示：

```
Enter value for --password (Passphrase):
```

输入密码，例如 `123`，回车，`call()` 方法被调用，输出如下内容：
```
Hi user123, your password is hashed to pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=.
```

### 可选交互模式
交互选项，默认会使程序等待标准输入。对需要同时以交互模式和批处理模式运行的命令，

在交互选项的`arity`的为默认值0，即不带参数。从 picocli 3.9.6 开始，设置 `arity="0..1"`后
交互选项也可以带参数值，避免控制台输入需求。例如：

```java
@Option(names = "--user")
String user;

@Option(names = "--password", arity = "0..1", interactive = true)
char[] password;
```

如果输入参数中自带`password`的初始化值：
```
--password 123 --user Joe
```

在批处理时，就不再输入反复输入密码。


## Short Options
Picocli 支持将多个不含参数值的单字符的选项和最多一个包含参数的选项合并在一个 '-' 后面。
例如：
```java
class ClusteredShortOptions {
    @Option(names = "-a") boolean aaa;
    @Option(names = "-b") boolean bbb;
    @Option(names = "-c") boolean ccc;
    @Option(names = "-f") String  file;
}
```

下面的几种命令形式等价：
```
<command> -abcfInputFile.txt
<command> -abcf=InputFile.txt
<command> -abc -f=InputFile.txt
<command> -ab -cf=InputFile.txt
<command> -a -b -c -fInputFile.txt
<command> -a -b -c -f InputFile.txt
<command> -a -b -c -f=InputFile.txt
```

## 位置参数（Positional Parameters）

非选项参数命令均被解析为位置参数，一般放在选项后面。


# 强类型（Strongly Typed）
在解析命令行参数时，字符串被转换为对应类型。

## 内置类型

## 自定义类型转换
自定义类型转换，实现 `picocli.CommandLine.ITypeConverter` 接口：
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
然后使用 `CommandLine.registerConverter(Class<K> cls, ITypeConverter<K> converter)` 注册该转换器。如下所示：
```java
CommandLine cl = new CommandLine(app)
cl.registerConverter(Locale.class, s -> new Locale.Builder().setLanguageTag(s).build());
cl.registerConverter(Cipher.class, s -> Cipher.getInstance(s));
```

注册之后，使用 `parseArgs(String...)` 方法解析。（此处不能用静态方法 `populateCommand`）。例如：
```java
class App {
    @Parameters java.util.Locale locale;
    @Option(names = "-a") javax.crypto.Cipher cipher;
}
App app = new App();
CommandLine commandLine = new CommandLine(app)
    .registerConverter(Locale.class, s -> new Locale.Builder().setLanguageTag(s).build())
    .registerConverter(Cipher.class, s -> Cipher.getInstance(s));

commandLine.parseArgs("-a", "AES/CBC/NoPadding", "en-GB");
assert app.locale.toLanguageTag().equals("en-GB");
assert app.cipher.getAlgorithm().equals("AES/CBC/NoPadding"));
```


# 默认值
为字段提供默认的方式有三种。

##

# 必需参数（Required Arguments）

## 必需选项（Required Options）

# 多值参数（Multiple Values）
多值参数的一个参数对应多个值。

## 多次出现（Multiple Occurrences）

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

## 正则表达式分隔参数

## 元数（Arity）
使用 `arity` 属性可以控制一个选项对应几个参数值。

`arity` 属性可以指定准确数目的参数，也可以指定一个范围或任意数目（使用 "*"）。例如：
```java
class ArityDemo {
    @Parameters(arity = "1..3", descriptions = "one to three Files")
    File[] files;

    @Option(names = "-f", arity = "2", description = "exactly two floating point numbers")
    double[] doubles;

    @Option(names = "-s", arity = "1..*", description = "at least one string")
    String[] strings;
}
```

如果参数值个数达不到指定数目，抛出 `MissingParameterException`。

## Version Help

### Static Version Information


## Usage Help

### Compact Example

picocli 默认的使用帮助信息类似：

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


## Usage Help API



# 参考
- <https://github.com/remkop/picocli>
- https://picocli.info/