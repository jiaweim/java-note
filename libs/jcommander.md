- [概述](#%e6%a6%82%e8%bf%b0)
- [类型](#%e7%b1%bb%e5%9e%8b)
  - [基本类型](#%e5%9f%ba%e6%9c%ac%e7%b1%bb%e5%9e%8b)
  - [List](#list)
  - [Password](#password)
- [自定义类型](#%e8%87%aa%e5%ae%9a%e4%b9%89%e7%b1%bb%e5%9e%8b)
  - [单值](#%e5%8d%95%e5%80%bc)
    - [通过注释](#%e9%80%9a%e8%bf%87%e6%b3%a8%e9%87%8a)
    - [factory](#factory)
- [多个选项名称](#%e5%a4%9a%e4%b8%aa%e9%80%89%e9%a1%b9%e5%90%8d%e7%a7%b0)
- [必须参数和可选参数](#%e5%bf%85%e9%a1%bb%e5%8f%82%e6%95%b0%e5%92%8c%e5%8f%af%e9%80%89%e5%8f%82%e6%95%b0)
- [Main parameter](#main-parameter)
- [Parameter delegates](#parameter-delegates)
- [参数分隔符](#%e5%8f%82%e6%95%b0%e5%88%86%e9%9a%94%e7%ac%a6)
- [Usage](#usage)
- [Default values](#default-values)
- [Dynamic parameters](#dynamic-parameters)
- [更复杂的语法](#%e6%9b%b4%e5%a4%8d%e6%9d%82%e7%9a%84%e8%af%ad%e6%b3%95)
- [Reference](#reference)

# 概述
JCommander 是一个用于解析命令行的小的Java框架。对参数字段进行注释：
```java
private static class Args1
{
    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = {"-log", "-verbose"}, description = "Level of verbosity")
    private Integer verbose = -1;

    @Parameter(names = "-groups", description = "Comma-separated list of group names to be run")
    private String groups;

    @Parameter(names = "-debug", description = "Debug mode")
    private boolean debug = false;
}

@Test
public void testArgs1()
{
    Args1 arg = new Args1();
    String[] argv = {"-log", "2", "-groups", "unit"};
    JCommander.newBuilder()
            .addObject(arg)
            .build()
            .parse(argv);
    assertEquals(arg.verbose.intValue(), 2);
    assertEquals(arg.groups, "unit");
    assertTrue(arg.parameters.isEmpty());
    assertFalse(arg.debug);
}
```

再来个例子：
```java
private class Args2
{
    @Parameter(names = {"--length", "-l"})
    int length;
    @Parameter(names = {"--pattern", "-p"})
    int pattern;
}

@Test
public void testArgs2()
{
    String[] args = {"-l", "512", "--pattern", "2"};
    Args2 param = new Args2();
    JCommander.newBuilder().addObject(param)
            .build().parse(args);
    assertEquals(param.length, 512);
    assertEquals(param.pattern, 2);
}
```

# 类型
JCommander 默认支持基本类型，对其它类型需要自定义转换器。

## 基本类型
对 `Parameter` 注释的 boolean 类型，JCommander 将其解析为 arity=0 的选项，即后面不需要带参数：
```java
@Parameter(names = "-debug", description = "Debug mode")
private boolean debug = false;
```
当 JCommander 检测到 `-debug` 选项，自动将 `debug` 设置为 true。如果你想将 `debug` 默认设置为 true，则可以设置 arity=1，这样 `-debug` 后面可以带参数，此时 `-debug` 选项后面就必须指定参数：
```java
@Parameter(names = "-debug", description = "Debug mode", arity = 1)
private boolean debug = true;
```
此时的调用方式为：
```
program -debug true
program -debug false
```

对带 `Parameter` 注释的 `String`, `Integer`, `int`, `Long`, `long` 类型，JCommander 会解析对应参数，并常数转换为该类型：
```java
@Parameter(names = "-log", description = "Level of verbosity")
private Integer verbose = 1;
```

## List
对 List 类型的参数，JCommander 以该参数可以出现多次来解析，如：
```java
@Parameter(names = "-host", description = "The host")
private List<String> hosts = new ArrayList<>();
```
你可以按如下方式输入参数：
```
$ java Main -host host1 -verbose -host host2
```

## Password
如果你的参数为密码，可以将其类型设置为 password，这样在输入命令时，JCommander 会让你手动输入参数：
```java
public class ArgsPassword {
  @Parameter(names = "-password", description = "Connection password", password = true)
  private String password;
}
```
在运行程序时，会出现如下弹窗：
```
Value for -password (Connection password):
```
你需要输入对应的值，JCommander 才能继续执行。

# 自定义类型
将参数和自定义类型绑定，或者自定义参数分隔符（默认是逗号），JCommander 提供了两个接口 `IStringConverter` 和 `IParameterSplitter`。

## 单值
对包含单个值的参数，可以使用 `@Parameter` 的 `converter=` 属性，或者实现 `IStringConverterFactory`。

### 通过注释
JCommander 解析参数默认只转换为基本类型。如果需要更为复杂的类型，可以实现如下接口：
```java
public interface IStringConverter<T> {
  T convert(String value);
}
```

例如，将字符串转换为 `File`:
```java
public class FileConverter implements IStringConverter<File> {
  @Override
  public File convert(String value) {
    return new File(value);
  }
}
```
然后在声明字段时指定对应的转换器即可：
```java
@Parameter(names = "-file", converter = FileConverter.class)
File file;
```

如果转换器用于 `List` 字段，如：
```java
@Parameter(names = "-files", converter = FileConverter.class)
List<File> files;
```
然后以如下方式输入命令：
```
$ java App -files file1,file2,file3
```
JCommander 会自动将 `file1,file2,file3` 分为三个字段存入 List.

### factory
如果你使用的自定义类型需要在很多地方使用，在每个参数注释的地方都指定转换器就很无聊，此时可以使用 `IStringConverterFactory`：
```java
public interface IStringConverterFactory {
  <T> Class<? extends IStringConverter<T>> getConverter(Class<T> forType);
}
```

例如，假定你要解析包含 host 和 port 的字符串：
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

定义工厂类：
```java
public class Factory implements IStringConverterFactory {
  public Class<? extends IStringConverter<?>> getConverter(Class forType) {
    if (forType.equals(HostPort.class)) return HostPortConverter.class;
    else return null;
  }
```

然后使用 `HostPort` 类型不再需要定义 `converter` 属性：
```java
public class ArgsConverterFactory {
  @Parameter(names = "-hostport")
  private HostPort hostPort;
}
```
不过在使用 JCommander 时需要注册对应的工程类：
```java
ArgsConverterFactory a = new ArgsConverterFactory();
JCommander jc = JCommander.newBuilder()
    .addObject(a)
    .addConverterFactory(new Factory())
    .build()
    .parse("-hostport", "example.com:8080");

Assert.assertEquals(a.hostPort.host, "example.com");
Assert.assertEquals(a.hostPort.port.intValue(), 8080);
```

# 多个选项名称
可以指定多个选项名称，如：
```java
@Parameter(names = { "-d", "--outputDirectory" }, description = "Directory")
private String outputDirectory;
```

# 必须参数和可选参数
如果有些参数是必须的，可以通过 `required` 属性指定，该属性默认为 `false`：
```java
@Parameter(names = "-host", required = true)
private String host;
```
必须参数如果没指定，JCommander 会抛出异常告诉你缺哪些参数。

# Main parameter
定义 main 参数相对其它参数的不同是，其 `@Parameter` 注释不需要 `names` 属性。可以定义一个且最多一个无需 `names` 属性的参数，可以是 `List<String>`，也可以是单个字段：
```java
@Parameter(description = "Files")
private List<String> files = new ArrayList<>();

@Parameter(names = "-debug", description = "Debugging level")
private Integer debug = 1;
```

上面的 `files` 就是这类参数，在指定时不需要指定名称：
```
$ java Main -debug file1 file2
```
`files` 接受参数 "file1" 和 "file2"。

# Parameter delegates
参数代理，可以将参数传递到另外的类进行解析：
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
解析方法和一个类一样：
```
MainParams p = new MainParams();
JCommander.newBuilder().addObject(p).build()
    .parse("-v", "-port", "1234");
Assert.assertTrue(p.isVerbose);
Assert.assertEquals(p.delegate.port, 1234);
```


# 参数分隔符
参数默认以空格分隔，不过可以使用 `@`Parameters 设置不同的分隔符：
```java
@Parameters(separators = "=")
public class SeparatorEqual {
  @Parameter(names = "-level")
  private Integer level = 2;
}
```

# Usage
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

设置程序名称：
```java
jCommander.setProgramName("Program Name");
```

# Default values
设置默认值的最方便的方式是在声明时提供值：
```java
private Integer logLevel = 3;
```

对更为复杂的情况，你可能希望多个类使用相同的默认参数，或者在统一的地方指定默认值，如 `.properteis` 或 XML文件。此时可以使用 `IDefaultProvider`：
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
将实现该接口的类提供给 `JCommander` 对象，可以指定对什么选项返回什么默认值。需要注意的是，该方法返回值随后由 string converter 转换类型。

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

# Dynamic parameters
JCommander 允许指定编译时未知的参数，例如 "-Da=b -Dc=d"。这类参数通过 `@DynamicParameter` 指定，而且必须是 `Map<STring, String` 类型。动态参数可以在命令行中出现多次：
```java
@DynamicParameter(names = "-D", description = "Dynamic parameters go here")
private Map<String, String> params = new HashMap<>();
```
通过 `assignment` 属性可以指定不同的分隔符，而不必须是 `=`。

# 更复杂的语法
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

然后通过 `JCommander` 对象注册这些 commands。解析之后，可以通过 `getParsedCommand()` 获得命令对象，然后根据返回的命令对象查看参数：
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


# Reference
- http://jcommander.org/
- [命名建议](http://catb.org/~esr/writings/taoup/html/ch10s05.html#id2948149)