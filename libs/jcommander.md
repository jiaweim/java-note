# JCommander

2025-10-09: 根据最新版 JCommander 3.0 更新
2021-03-13 ⭐
@author Jiawei Mao
***

## 1. 简介

JCommander 是一个用于解析命令行的 Java 框架。首先注释参数字段：

```java
import com.beust.jcommander.Parameter;

public class Args {
    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = {"-log", "-verbose"}, description = "Level of verbosity")
    private Integer verbose = 1;

    @Parameter(names = "-groups", description = "Comma-separated list of group names to be run")
    private String groups;

    @Parameter(names = "-debug", description = "Debug mode")
    private boolean debug = false;

    private Integer setterParameter;

    @Parameter(names = "-setterParameter", description = "A parameter annotation on a setter method")
    public void setParameter(Integer value) {
        this.setterParameter = value;
    }
}
```

然后使用 JCommander 解析命名：

```java
Args args = new Args();
String[] argv = { "-log", "2", "-groups", "unit" };
JCommander.newBuilder()
  .addObject(args)
  .build()
  .parse(argv);

assertEquals(arg.verbose.intValue(), 2);
assertEquals(arg.groups, "unit");
assertTrue(arg.parameters.isEmpty());
assertFalse(arg.debug);
```

再例如：

```java
class Main {
    @Parameter(names = {"--length", "-l"})
    int length;
    @Parameter(names = {"--pattern", "-p"})
    int pattern;

    public static void main(String... argv) {
        Main main = new Main();
        JCommander.newBuilder()
                .addObject(main)
                .build()
                .parse(argv);
        main.run();
    }

    public void run() {
        System.out.printf("%d %d", length, pattern);
    }
}
```

```sh
$ java Main -l 512 --pattern 2
512 2
```

## 2. 参数类型

JCommander 默认支持基本类型，对其它类型需要自定义类型转换器。JCommander 已经为许多常用类型提供转换器，如 `char[]`, `File`, `Path`, `URI` 等。

### 2.1 Boolean

对 `boolean` 或 `Boolean` 类型参数，JCommander 将其解析为 arity=0 的选项，即后面不需要带参数值：
```java
@Parameter(names = "-debug", description = "Debug mode")
private boolean debug = false;
```
`arity=0` 类型参数在命令行中不需要添加参数值，在解析过程中检测到这类参数，会将相应字段设置为 `true`。所以，当 JCommander 检测到 `-debug` 选项，自动将 `debug` 设置为 true，`-debug` 后面不需要额外参数值。

如果需要 `debug` 默认为 `true`，则可以设置 `arity=1`，这样 `-debug` 后面需要带参数，此时 `-debug` 选项后面就必须指定参数值：

```java
@Parameter(names = "-debug", description = "Debug mode", arity = 1)
private boolean debug = true;
```
此时的调用方式为：
```sh
program -debug true
program -debug false
```

对 `String`, `Integer`, `int`, `Long`, `long` 类型字段，如果检测到 `Parameter` 注释，JCommander 会解析对应参数，并转换为对应类型：
```java
@Parameter(names = "-log", description = "Level of verbosity")
private Integer verbose = 1;
```

```sh
java Main -log 3
```

解析得到 `verbose` 为 3。如下命令则因类型不兼容报错：

```sh
$ java Main -log test
```

### 2.2 List 和 Set

对 `List` 或 `Set` 类型参数，JCommander 默认这类参数可以出现多次。例如：

```java
@Parameter(names = {"-host", "-hosts"}, description = "Host option can be used multiple times, and may be comma-separated")
private List<String> hosts = new ArrayList<>();
```

可以按如下方式输入参数：
```sh
$ java Main -host host1 -verbose -host host2
```

或者输入逗号分隔的值：

```sh
$ java Main -hosts host1,host2
```

解析完成后，`hosts` 字段包含 "host1" 和 "host2"。

### 2.3 Password

如果参数为密码，或者不希望出现在历史记录中的值，则可以将其类型设置为 `password`，这样在输入命令时，JCommander 会让你在控制台中手动输入参数：
```java
public class ArgsPassword {
  @Parameter(names = "-password", description = "Connection password", password = true)
  private String password;
}
```
在运行程序时，会出现如下弹窗：
```sh
Value for -password (Connection password):
```
你需要输入对应的值，JCommander 才能继续执行。

### 2.4 Echo Input

在 Java 6+ 中，默认无法看到在提示符处输入的密码（Java 5 以下则始终显示密码）。通过设置 `eachInput=true` 则可以显示密码（仅 `password=true` 该设置才有效）：

```java
public class ArgsPassword {
	@Parameter(names = "-password", description = "Connection password", password = true, echoInput = true)
	private String password;
}
```

## 3. 自定义类型（converter 和 splitter）

将参数和自定义类型绑定，或自定义**参数分隔符**（默认是逗号），JCommander 提供了两个接口 `IStringConverter` 和 `IParameterSplitter`。
### 3.1 自定义类型：单值

对单值参数，可以使用 `@Parameter` 的 `converter=` 属性，或者实现 `IStringConverterFactory`。
#### by annotation

JCommander 默认只将命令解析为基本类型（string, boolean, integer 和 long）。对复杂类型，可以实现如下接口：

```java
public interface IStringConverter<T> {
    T convert(String value);
}
```

例如，将字符串转换为 `File` 的 `IStringConverter` 实现:

```java
public class FileConverter implements IStringConverter<File> {
  @Override
  public File convert(String value) {
    return new File(value);
  }
}
```

然后在声明字段时指定对应的转换器：
```java
@Parameter(names = "-file", converter = FileConverter.class)
File file;
```

> [!TIP]
>
> JCommander 提供了许多常用的 converters，具体可参考 `IStringConverter` 的实现类。

如果转换器用于 `List` 类型字段，如：

```java
@Parameter(names = "-files", converter = FileConverter.class)
List<File> files;
```

可以按如下方式输入命令：
```
$ java App -files file1,file2,file3
```
JCommander 会自动将 `file1,file2,file3` 分为三个字段存入 List.

#### by factory

如果使用的自定义类型需要在很多地方使用，在每个参数注释的地方都指定转换器比较繁琐，此时可以使用 `IStringConverterFactory`：

```java
public interface IStringConverterFactory {
  <T> Class<? extends IStringConverter<T>> getConverter(Class<T> forType);
}
```

例如，解析包含 host 和 port 的字符串：
```
$ java App -target example.com:8080
```

定义对应的类：
```java
public class HostPort {
  public HostPort(String host, String port) {
     this.host = host;
     this.port = port;
  }

  final String host;
  final Integer port;
}
```

定义相应的转换类：
```java
class HostPortConverter implements IStringConverter<HostPort> {
  @Override
  public HostPort convert(String value) {
    String[] s = value.split(":");
    return new HostPort(s[0], Integer.parseInt(s[1]));
  }
}
```

**定义工厂类**：

```java
public class Factory implements IStringConverterFactory {
  public Class<? extends IStringConverter<?>> getConverter(Class forType) {
    if (forType.equals(HostPort.class)) return HostPortConverter.class;
    else return null;
  }
```

然后就可以直接使用 `HostPort` 类型，不再需要 `converter` 属性：

```java
public class ArgsConverterFactory {
  @Parameter(names = "-hostport")
  private HostPort hostPort;
}
```

不过在使用 JCommander 时需要注册对应的 factory 类：
```java
ArgsConverterFactory a = new ArgsConverterFactory();
JCommander jc = JCommander.newBuilder()
    .addObject(a)
    .addConverterFactory(new Factory()) // 注册 factory
    .build()
    .parse("-hostport", "example.com:8080");

Assert.assertEquals(a.hostPort.host, "example.com");
Assert.assertEquals(a.hostPort.port.intValue(), 8080);
```

使用 `IStringConverterFactory` 的另一个优点是，该类可以来自依赖注入框架。

### 3.2 自定义类型：List

为 `@Parameter` 添加 `listConverter=` 属性，并设置自定义 `IStringConverter` 实现将字符串转换为 `List`。

#### by annotation

如果需要复杂类型 `List`，需要先实现类型转换接口 `IStringConverter`：

```java
public interface IStringConverter<T> {
  T convert(String value);
}
```

其中 `T` 为 `List`。

例如，下面将字符串转换为 `List<File>`：

```java
public class FileListConverter implements IStringConverter<List<File>> {
  @Override
  public List<File> convert(String files) {
    String [] paths = files.split(",");
    List<File> fileList = new ArrayList<>();
    for(String path : paths){
        fileList.add(new File(path));
    }
    return fileList;
  }
}
```

然后在注释参数时声明该 `listConverter`：

```java
@Parameter(names = "-files", listConverter = FileListConverter.class)
List<File> file;
```

接着就可以在调用程序时提供参数：

```sh
$ java App -files file1,file2,file3
```

参数 `file1,file2,file3` 提供给 `listConverter` 进行转换。

JCommander 默认支持 `String` 类型。

### 3.3 Splitting

使用 `@Parameter` 的 `splitter=` 属性指定自定义 `IParameterSplitter` 实现。

#### by annotation

对 `List` 字段类型，JCommander 默认使用逗号拆分参数。

可以实现如下接口自定义拆分方式：

```java
public interface IParameterSplitter {
  List<String> split(String value);
}
```

例如，用分号拆分字符串：

```java
public static class SemiColonSplitter implements IParameterSplitter {
    public List<String> split(String value) {
      return Arrays.asList(value.split(";"));
    }
}
```

然后在注释时加上 `converter` 和 `splitter` 属性即可：

```java
@Parameter(names = "-files", converter = FileConverter.class, splitter = SemiColonSplitter.class)
List<File> files;
```

## 4. 参数验证

> [!TIP]
>
> 参数验证可以在类初始化中完成，JCommander 提供的功能属于锦上添花，不是太有必要。

参数验证有两种方式：

- 单个参数水平
- 全局水平

### 4.1 单参数验证

通过实现以下接口来要求 JCommander 对参数执行早期验证：

```java
public interface IParameterValidator {
 /**
   * Validate the parameter.
   *
   * @param name The name of the parameter (e.g. "-host").
   * @param value The value of the parameter that we need to validate
   *
   * @throws ParameterException Thrown if the value of the parameter is invalid.
   */
  void validate(String name, String value) throws ParameterException;
}
```

以下是一个示例实现，确保参数为正整数：

```java
public class PositiveInteger implements IParameterValidator {
 public void validate(String name, String value)
      throws ParameterException {
    int n = Integer.parseInt(value);
    if (n < 0) {
      throw new ParameterException("Parameter " + name + " should be positive (found " + value +")");
    }
  }
}
```

然后在 `@Parameter` 注释的 `validateWith` 属性指定实现类的名称：

```java
@Parameter(names = "-age", validateWith = PositiveInteger.class)
private Integer age;
```

此时，如果传入负整数给 `age`，会抛出 `ParameterException` 异常。

可以同时指定多个 validator：

```java
@Parameter(names = "-count", validateWith = { PositiveInteger.class, CustomOddNumberValidator.class })
private Integer value;
```

### 4.2 全局参数验证

使用 JCommander 解析参数后，可能需要对这些参数执行其它验证，例如，确保没有同时指定两个互斥的参数。通过实现 `IParametersValidator` 接口实现该功能：

```java
public interface IParametersValidator {
    /**
     * Validate all parameters.
     *
     * @param parameters
     *            Name-value-pairs of all parameters (e.g. "-host":"localhost").
     *
     * @throws ParameterException
     *             Thrown if validation of the parameters fails.
     */
    void validate(Map<String, Object> parameters) throws ParameterException;
}
```

例如，确保 `--quiet` 和 `--verbose` 不会同时启用：

```java
public static class QuietAndVerboseAreMutualExclusive implements IParametersValidator {
    @Override
    public void validate(Map<String, Object> parameters) throws ParameterException {
        if (parameters.get("--quiet") == TRUE && parameters.get("--verbose") == TRUE)
            throw new ParameterException("--quiet and --verbose are mutually exclusive");
    }
}
```

然后在类的 `@Parameters` 注释的 `parametersValidators` 属性指定该实现类：

```java
@Parameters(parametersValidators = QuietAndVerboseAreMutualExclusive.class)
class Flags {
    @Parameter(names = "--quiet", description = "Do not output anything")
    boolean quiet;

    @Parameter(names = "--verbose", description = "Output detailed information")
    boolean verbose;
}
```

此时，同时启用 `--quiet` 和 `--verbose` 会抛出 `ParameterException`。

可以同时指定多个 validators：

```java
@Parameters(paremetersValidators = { QuietAndVerboseAreMutualExclusive.class, VerboseNeedsLevel.class })
class Flags {
    @Parameter(names = "--quiet", description = "Do not output anything")
    boolean quiet;

    @Parameter(names = "--verbose", description = "Output detailed information")
    boolean verbose;

    @Parameter(names = "--level", description = "Detail level of verbose information")
    Integer level;
}
```


## 5. Main parameter

前面介绍的所有 `@Parameter` 注释都定义了一个 `names` 属性。我们可以定义一个（**最多一个**）没有 `names` 属性的参数，该参数称为 **main 参数**。该参数可以是 `List<String>`，也可以是单个字段（`String` 或包含 Converter 的类型，如 `File`）：

```java
@Parameter(description = "Files")
private List<String> files = new ArrayList<>();

@Parameter(names = "-debug", description = "Debugging level")
private Integer debug = 1;
```

上面的 `files` 就是这类参数，在指定时不需要指定名称：

```sh
$ java Main -debug file1 file2
```

`files` 接受参数 "file1" 和 "file2"。

## 6. Privage 参数

参数可以是 private 的：

```java
public class ArgsPrivate {
  @Parameter(names = "-verbose")
  private Integer verbose = 1;

  public Integer getVerbose() {
    return verbose;
  }
}
ArgsPrivate args = new ArgsPrivate();
JCommander.newBuilder()
    .addObject(args)
    .build()
    .parse("-verbose", "3");
Assert.assertEquals(args.getVerbose().intValue(), 3);
```

## 7. Parameter separators

参数默认以空格分隔，不过可以使用 `@Parameters` 注释设置不同的分隔符：
```java
@Parameters(separators = "=")
public class SeparatorEqual {
  @Parameter(names = "-level")
  private Integer level = 2;
}
```

## 8. Multiple descriptions

可以将参数的描述分散到多个类中。例如，自定义以下两个类：

```java
public class ArgsMaster {
  @Parameter(names = "-master")
  private String master;
}

public class ArgsSlave {
  @Parameter(names = "-slave")
  private String slave;
}
```

然后将这两个对象传递给 JCommander：

```java
ArgsMaster m = new ArgsMaster();
ArgsSlave s = new ArgsSlave();
String[] argv = { "-master", "master", "-slave", "slave" };
JCommander.newBuilder()
    .addObject(new Object[] { m , s })
    .build()
    .parse(argv);

Assert.assertEquals(m.master, "master");
Assert.assertEquals(s.slave, "slave");
```

## 9. @syntax

JCommander 支持 @ 语法，将所有选项放入一个文件，然后将此文件作为参数传递：

`/tmp/parameters` 参数文件内容：

```
-verbose
file1
file2
file3
```

使用方法：

```sh
$ java Main @/tmp/parameters
```

## 10. Arities (multiple values for parameters)

> [!TIP]
>
> 作用有限，通过自定义 converter 或者自定义 splitter 也可以实现相同功能。

### 10.1 Fixed arities

如果某些参数需要多个值。例如，在 `-pairs` 后需要两个值：

```sh
$ java Main -pairs slave master foo.xml
```

此时，需要为参数设置 `arity` 属性，并将参数类型设置为 `List<String>` 类型：

``` java
@Parameter(names = "-pairs", arity = 2, description = "Pairs")
private List<String> pairs;
```

不需要为 `boolean` 或 `Boolean` 设置 arity（默认为 0），也不需要为 `String`, `Integer`, `int`, `Long` 和 `long` 设置（默认为 1）。

> [!IMPORTANT]
>
> 只支持为 `List<String>` 类型设置 arity。对其它类型需要自己手动转换（java 类型擦除的限制）。

### 10.2 Variable arities

可以将参数指定为接收无穷值，直到下一个选项。例如：

```sh
program -foo a1 a2 a3 -bar
program -foo a1 -bar
```

这类参数有两种解析方式。

#### 10.2.1 list

如果参数的数量未知，则参数类型必须为 `List<String>`，并将 `variableArity` 设置为 `true`：

```java
@Parameter(names = "-foo", variableArity = true)
public List<String> foo = new ArrayList<>();
```

#### 10.2.2 class

也可以定义一个类，根据参数出现的顺序存储参数：

```java
static class MvParameters {
  @SubParameter(order = 0)
  String from;

  @SubParameter(order = 1)
  String to;
}

@Test
public void arity() {
  class Parameters {
    @Parameter(names = {"--mv"}, arity = 2)
    private MvParameters mvParameters;
  }

  Parameters args = new Parameters();
  JCommander.newBuilder()
          .addObject(args)
          .args(new String[]{"--mv", "from", "to"})
          .build();

  Assert.assertNotNull(args.mvParameters);
  Assert.assertEquals(args.mvParameters.from, "from");
  Assert.assertEquals(args.mvParameters.to, "to");
}
```

## 11.  Multiple option names

可以指定多个选项名称：

```java
@Parameter(names = { "-d", "--outputDirectory" }, description = "Directory")
private String outputDirectory;
```

支持如下两种语法：

```sh
$ java Main -d /tmp
$ java Main --outputDirectory /tmp
```

## 12. 其它选项配置

设置选项的查找方式：

- `JCommander#setCaseSensitiveOptions(boolean)`：是否区分大小写，如果设置为 `false`，那么 "-param" 和 "-PARAM" 视为等价
- `JCommander#setAllowAbbreviatedOptions(boolean)`: 用户是否可以使用缩写。如果将其设置为 `true`，那么用户可以使用 "-par" 来指定名为 "-param" 的选项。如果缩写出现重叠，JCommander 会抛出 `ParameterException`。


## 13. 必须参数和可选参数

如果有些参数是必须的，可以通过 `required` 属性指定，该属性默认为 `false`：
```java
@Parameter(names = "-host", required = true)
private String host;
```
必须参数如果没指定，JCommander 会抛出异常告诉你缺哪些参数。

## 14. 默认值

为参数设置默认值的最常用的方式是在声明时初始化字段：
```java
private Integer logLevel = 3;
```

对更复杂的情况，如希望多个类使用相同的默认参数，或者在统一的地方指定默认值，如 `.properteis` 或 XML 文件。此时可以使用 `IDefaultProvider`：
```java
public interface IDefaultProvider {
  /**
   * @param optionName The name of the option as specified in the names() attribute
   * of the @Parameter option (e.g. "-file").
   *
   * @return the default value for this option.
   */
  String getDefaultValueFor(String optionName);
}
```
将实现该接口的类提供给 `JCommander` 对象，可以指定对什么选项使用什么默认值。需要注意的是，该方法返回值随后由 `IStringConverter` 转换类型。

例如，下面将除 "-debug" 以外的所有参数的默认值设置为 42：
```java
private static final IDefaultProvider DEFAULT_PROVIDER = new IDefaultProvider() {
  @Override
  public String getDefaultValueFor(String optionName) {
    return "-debug".equals(optionName) ? "false" : "42";
  }
};

// ...

JCommander jc = JCommander.newBuilder()
    .addObject(new Args())
    .defaultProvider(DEFAULT_PROVIDER)
    .build()
```

对一些常见类型不需要实现 `IDefaultProvider`，因为 JCommander 提供了 `PropertyFileDefaultProvider` 和 `EnvironmentVariableDefaultProvider`，分别从属性文件（默认为 `jcommander.properties`）和环境变量（默认为 `JCOMMANDER_OPTS`）读取默认值。

## 15. Help 参数

如果某个参数用于显示帮助或用法信息，此时需要使用 `help` 属性：

```java
@Parameter(names = "--help", help = true)
private boolean help;
```

如果没有这个 boolean 参数，没有指定必需参数时 JCommander 会发出错误消息。而添加该参数后，JCommander 对必需参数的缺失不报错。使用范式：

```java
@Parameter(names = "--help", help = true, description = "Print help information")
private boolean help;

commander.parse(args);
if (runner.help) {
    commander.usage();
}
```

## 16. 更复杂的语法

很多工具如 *git* 或 *svn* 都有一整套命令，并语法各自不同：
```
$ git commit --amend -m "Bug fix"
```

上面的 "commit" 在 JCommander 中称为 "commands"，可以通过创建单独的参数对象指定 "commands":
```java
@Parameters(separators = "=", commandDescription = "Record changes to the repository")
private class CommandCommit {

  @Parameter(description = "The list of files to commit")
  private List<String> files;

  @Parameter(names = "--amend", description = "Amend")
  private Boolean amend = false;

  @Parameter(names = "--author")
  private String author;
}

@Parameters(commandDescription = "Add file contents to the index")
public class CommandAdd {

  @Parameter(description = "File patterns to add to the index")
  private List<String> patterns;

  @Parameter(names = "-i")
  private Boolean interactive = false;
}
```

然后通过 `JCommander` 注册这些 commands。解析之后，可以通过 `getParsedCommand()` 获得命令对象，然后根据返回的命令对象查看参数：

```java
CommandMain cm = new CommandMain();
CommandAdd add = new CommandAdd();
CommandCommit commit = new CommandCommit();
JCommander jc = JCommander.newBuilder()
    .addObject(cm)
    .addCommand("add", add);
    .addCommand("commit", commit);
    .build();

jc.parse("-v", "commit", "--amend", "--author=cbeust", "A.java", "B.java");

assertTrue(cm.verbose);
assertEquals(jc.getParsedCommand(), "commit");
assertTrue(commit.amend);
assertEquals(commit.author, "cbeust");
assertEquals(commit.files, Arrays.asList("A.java", "B.java"));
```

## 17. Exception

当 JCommander 检测到错误，会抛出 `ParameterException`。这是一个 Runtime 异常。

另外，`ParameterException` 包含 JCommander 实例引用，因此出错后可以调用 `usage()` 显示帮助信息。

## 18. Usage

通过 `JCommander` 实例的 `usage()` 方法可以输出参数相关的帮助信息，例如：

```java
JCommander jCommander = JCommander.newBuilder().addObject(finder).build();
try {
    jCommander.parse(args);
} catch (Exception e) {
    jCommander.usage();
    return;
}
```

这样在参数不对时，就可以输出正确的参数信息。

```sh
Usage: <main class> [options]
  Options:
    -debug          Debug mode (default: false)
    -groups         Comma-separated list of group names to be run
  * -log, -verbose  Level of verbosity (default: 1)
    -long           A long number (default: 0)
```

带星号的必需参数。

- 可以使用 `JCommander.setProgramName()` 设置程序名称

```java
jCommander.setProgramName("Program Name");
```

- 通过设置 `@Parameter` 注释中的 `order` 属性，可以设置参数在 `usage()` 输出中的顺序

```java
class Parameters {
    @Parameter(names = "--importantOption", order = 0)
    private boolean a;

    @Parameter(names = "--lessImportantOption", order = 3)
    private boolean b;
```

- 可以使用 `defaultValueDescription` 设置默认值的描述信息

```java
@Parameter(names = "--start", defaultValueDescription = "The default value is a random number")
int start = Math.random * 100;
```

## 19. 隐藏参数

如果不希望某些参数出现在 usage 中，可以将其标记为 "hidden"：

```java
@Parameter(names = "-debug", description = "Debug mode", hidden = true)
private boolean debug = false;
```

## 20.Internationalization

可以将参数描述信息国际化。首先，在类的顶部使用 `@Parameters` 注释定义 message-bundle 的名称，然后使用 `descriptionKey` 属性指定 message 在 message-bundle 中的 key：

```java
@Parameters(resourceBundle = "MessageBundle")
private class ArgsI18N2 {
  @Parameter(names = "-host", description = "Host", descriptionKey = "host")
  String hostName;
}
```

在 bundle 中需要定义该 key：

```properties
host: Hôte
```

JCommander 会使用默认 locale 来解析描述信息。

## 21.参数代理

在同一个项目中编写多个工具，其中大多数工具可以共享配置。虽然可以使用对象继承来避免代码重复，但继承限制了代码灵活性。此时可以考虑使用参数代理。

当 JCommander 在对象中遇到 `@ParameterDelegate` 注释，可以将参数传递到另外的类进行解析：

```java
class Delegate {
  @Parameter(names = "-port")
  private int port;
}

class MainParams {
  @Parameter(names = "-v")
  private boolean verbose;

  @ParametersDelegate
  private Delegate delegate = new Delegate();
}
```
只需要将 `MainParams` 类添加到 JCommander 解析：
```java
MainParams p = new MainParams();
JCommander.newBuilder().addObject(p).build()
    .parse("-v", "-port", "1234");
Assert.assertTrue(p.isVerbose);
Assert.assertEquals(p.delegate.port, 1234);
```

## 22.动态参数

JCommander 允许指定编译时未知的参数，例如 `-Da=b -Dc=d`。这类参数通过 `@DynamicParameter` 指定，且必须是 `Map<STring, String` 类型。动态参数可以在命令行中出现多次：
```java
@DynamicParameter(names = "-D", description = "Dynamic parameters go here")
private Map<String, String> params = new HashMap<>();
```
通过 `assignment` 属性可以指定不同的分隔符，不限于 `=`。

## 23.自定义 usage 格式

JCommander 支持自定义 `JCommander.usage()` 方法的输出格式，即实现 `IUsageFormatter` 接口，然后调用 `JCommander#setUsageFormatter(IUsageFormatter)`。

例如，下面仅打印参数名称：

```java
class ParameterNamesUsageFormatter implements IUsageFormatter {
    // Extend other required methods as seen in DefaultUsageFormatter

    // This is the method which does the actual output formatting
    public void usage(StringBuilder out, String indent) {
        if (commander.getDescriptions() == null) {
            commander.createDescriptions();
        }

        // Create a list of the parameters
        List<ParameterDescription> params = Lists.newArrayList();
        params.addAll(commander.getFields().values());

        // Append all the parameter names
        if (params.size() > 0) {
            out.append("Options:\n");

            for (ParameterDescription pd : params) {
                out.append(pd.getNames()).append("\n");
            }
        }
    }
}
```

## 参考

- http://jcommander.org/
- [命名建议](http://catb.org/~esr/writings/taoup/html/ch10s05.html#id2948149)
