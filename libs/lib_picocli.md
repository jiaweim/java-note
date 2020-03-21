- [简介](#%e7%ae%80%e4%bb%8b)
- [选项（Options）和参数（Parameters）](#%e9%80%89%e9%a1%b9options%e5%92%8c%e5%8f%82%e6%95%b0parameters)
  - [选项（Options）](#%e9%80%89%e9%a1%b9options)
  - [交互式选项（Interactive Options, Password）](#%e4%ba%a4%e4%ba%92%e5%bc%8f%e9%80%89%e9%a1%b9interactive-options-password)
    - [实例](#%e5%ae%9e%e4%be%8b)
    - [可选交互模式](#%e5%8f%af%e9%80%89%e4%ba%a4%e4%ba%92%e6%a8%a1%e5%bc%8f)
  - [Short Options](#short-options)
  - [位置参数（Positional Parameters）](#%e4%bd%8d%e7%bd%ae%e5%8f%82%e6%95%b0positional-parameters)
- [强类型（Strongly Typed）](#%e5%bc%ba%e7%b1%bb%e5%9e%8bstrongly-typed)
  - [内置类型](#%e5%86%85%e7%bd%ae%e7%b1%bb%e5%9e%8b)
  - [自定义类型转换](#%e8%87%aa%e5%ae%9a%e4%b9%89%e7%b1%bb%e5%9e%8b%e8%bd%ac%e6%8d%a2)
- [默认值](#%e9%bb%98%e8%ae%a4%e5%80%bc)
- [必需参数（Required Arguments）](#%e5%bf%85%e9%9c%80%e5%8f%82%e6%95%b0required-arguments)
  - [必需选项（Required Options）](#%e5%bf%85%e9%9c%80%e9%80%89%e9%a1%b9required-options)
- [多值参数（Multiple Values）](#%e5%a4%9a%e5%80%bc%e5%8f%82%e6%95%b0multiple-values)
  - [多次出现（Multiple Occurrences）](#%e5%a4%9a%e6%ac%a1%e5%87%ba%e7%8e%b0multiple-occurrences)
    - [重复选项（Repeated Options）](#%e9%87%8d%e5%a4%8d%e9%80%89%e9%a1%b9repeated-options)
    - [多个位置参数（Multiple Positional Parameters）](#%e5%a4%9a%e4%b8%aa%e4%bd%8d%e7%bd%ae%e5%8f%82%e6%95%b0multiple-positional-parameters)
    - [多个Boolean选项](#%e5%a4%9a%e4%b8%aaboolean%e9%80%89%e9%a1%b9)
  - [正则表达式分隔参数](#%e6%ad%a3%e5%88%99%e8%a1%a8%e8%be%be%e5%bc%8f%e5%88%86%e9%9a%94%e5%8f%82%e6%95%b0)
  - [元数（Arity）](#%e5%85%83%e6%95%b0arity)
- [Usage Help](#usage-help)
- [参考](#%e5%8f%82%e8%80%83)
# 简介
`Picocli` 是一个创建 Java 命令行应用的开源辅助工具包，包含如下特性：
- 源码文件只有一个，把源码放在项目中就能使用。
- 支持 `POSIX`, `GNU` 和 `MS-DOS`等多种命令行语法样式。
- 生成的帮助信息可通过ANSI 颜色和样式自定义。
- 命令输入支持 TAB 自动完成。

`picocli` 通过注释完成字段的识别，然后将输入转换为字段对应的类型。例：
```java
import org.testng.annotations.Test;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;

public class Example
{
    @Option(names = {"-v", "--verbose"}, description = "Be verbose.")
    private boolean verbose = false;

    @Parameters(arity = "1..*", paramLabel = "FILE", description = "File(s) to process.")
    private File[] inputFiles;

    @Test
    public void test()
    {
        String[] args = {"-v", "inputFile1", "inputFile2"};
        Example app = CommandLine.populateCommand(new Example(), args);
        assert app.verbose;
        assert app.inputFiles != null && app.inputFiles.length == 2;
    }
}

```
注释类的字段后，使用 `CommandLine.parse` 或 `CommandLine.populateCommand` 即可实现参数的解析。

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

# Usage Help


# 参考
- <https://github.com/remkop/picocli>