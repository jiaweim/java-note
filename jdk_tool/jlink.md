# jlink

2024-09-12 ⭐
@author Jiawei Mao
***
## 简介

jlink 将 modules 及依赖项打包为 runtime-image。runtime-image 类似 JDK，但只包含你所需的 modules，以及这些 modules 的依赖项。镜像分两种：

- runtime-image: JDK 的子集
- app-image: 包含项目 modules

jlink 命令：

```
jlink [options] --module-path modulepath --add-modules module [, module...]
```

- **options**

命令行选项，以空格分隔。

- **modulepath**

包含 module 的路径。module 可以是 modular JAR 文件或JMOD 文件。

- **module**

添加到 runtime-image 的 module 名称。jlink 将这些 modules 及 transitive 依赖性添加到 image。

## jlink 选项

- `--add-modules` *mod* [`,` *mod*...]

添加命名模块 `mod` 到默认 root-modules 集合。root-modules 集合默认为空集。

- `--bind-services`

链接 service-provide modules 及依赖项。

- `-c ={0|1|2}` 或 `--compress={0|1|2}`启用资源压缩功能：

  - `0`: No compression

  - `1`: Constant string sharing

  - `2`: ZIP

- `--disable-plugin` *pluginname*

禁用指定插件。

- `--endian { little|big}`

指定生成 image 的 byte-order。默认和系统架构一致。

- `-h` or `--help`

打印帮助信息。

- `--ignore-signing-information`



Suppresses a fatal error when signed modular JARs are linked in the runtime image. The signature-related files of the signed modular JARs aren't copied to the runtime image.

- `--launcher` *command*`=`*module* or `--launcher` *command*`=`*module*`/`*main*

Specifies the launcher command name for the module or the command name for the module and main class (the module and the main class names are separated by a slash (`/`)).

- `--limit-modules` *mod* [`,` *mod*...]

Limits the universe of observable modules to those in the transitive closure of the named modules, `mod`, plus the main module, if any, plus any further modules specified in the `--add-modules` option.

- `--list-plugins`

Lists available plug-ins, which you can access through command-line options; see [jlink Plug-ins](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jlink.html#jlink-plug-ins).

- `-p` or `--module-path` *modulepath*

指定 module 路径。

如果不指定该选项，则默认 module 路径为 `$JAVA_HOME/jmods`。该目录包含 `java.base` 和其它标准 JDK 模块。

如果指定了该选项，但是从中找不到 `java.base` 模块，那么 `jlink` 会将 `$JAVA_HOME/jmods` 附加到 module 路径。

- `--no-header-files`

Excludes header files.

- `--no-man-pages`

Excludes man pages.

- `--output` *path*

指定保存 runtime-image 的位置。

- `--save-opts` *filename*

Saves `jlink` options in the specified file.

- `--suggest-providers` [*name*`,` ...]

Suggest providers that implement the given service types from the module path.

- `--version`

Prints version information.

- `@`*filename*

Reads options from the specified file.

An options file is a text file that contains the options and values that you would typically enter in a command prompt. Options may appear on one line or on several lines. You may not specify environment variables for path names. You may comment out lines by prefixing a hash symbol (`#`) to the beginning of the line.

The following is an example of an options file for the `jlink` command:

```
#Wed Dec 07 00:40:19 EST 2016
--module-path mlib
--add-modules com.greetings
--output greetingsapp
```

## jlink 插件

对需要 pattern-list 的插件，pattern 之间以逗号分隔，pattern 使用如下形式之一：

- *glob-pattern*
- `glob:`*glob-pattern*
- `regex:`*regex-pattern*
- `@filename`
  - *filename* is the name of a file that contains patterns to be used, one pattern per line.

使用 `jlink --list-plugins` 查看所有可用的插件：

```powershell
>jlink --list-plugins

List of available plugins:
  --add-options <options>   Prepend the specified <options> string, which may
                            include whitespace, before any other options when
                            invoking the virtual machine in the resulting image.
  --compress <compress>     Compression to use in compressing resources:
                            Accepted values are:
                            zip-[0-9], where zip-0 provides no compression,
                            and zip-9 provides the best compression.
                            Default is zip-6.
  --dedup-legal-notices [error-if-not-same-content]
                            De-duplicate all legal notices.
                            If error-if-not-same-content is specified then
                            it will be an error if two files of the same
                            filename are different.
  --exclude-files <pattern-list>
                            Specify files to exclude.
                            e.g.: **.java,glob:/java.base/lib/client/**
  --exclude-jmod-section <section-name>
                            Specify a JMOD section to exclude.
                            Where <section-name> is "man" or "headers".
  --exclude-resources <pattern-list>
                            Specify resources to exclude.
                            e.g.: **.jcov,glob:**/META-INF/**
  --generate-cds-archive    Generate CDS archive if the runtime image supports
                            the CDS feature.
  --generate-jli-classes @filename
                            Specify a file listing the java.lang.invoke
                            classes to pre-generate. By default, this plugin
                            may use a builtin list of classes to pre-generate.
                            If this plugin runs on a different runtime version
                            than the image being created then code generation
                            will be disabled by default to guarantee
                            correctness add ignore-version=true
                            to override this.
  --include-locales <langtag>[,<langtag>]*
                            BCP 47 language tags separated by a comma,
                            allowing
                            locale matching defined in RFC 4647.
                            e.g.: en,ja,*-IN
  --order-resources <pattern-list>
                            Order resources.
                            e.g.: **/module-info.class,@classlist,
                            /java.base/java/lang/**
  --release-info <file>|add:<key1>=<value1>:<key2>=<value2>:...|del:<key list>
                            <file> option is to load release properties from
                            the supplied file.
                            add: is to add properties to the release file.
                            Any number of <key>=<value> pairs can be passed.
                            del: is to delete the list of keys in release file.
  --save-jlink-argfiles <filenames>
                            Save the specified argument files that contain
                            the arguments to be prepended to the execution of
                            jlink in the output image. <filenames> is a
                            : (; on Windows) separated list of command-line argument files.
  --strip-debug             Strip debug information from the output image
  --strip-java-debug-attributes
                            Strip Java debug attributes from
                            classes in the output image
  --strip-native-commands   Exclude native commands (such as java/java.exe)
                            from the image.
  --system-modules [batch-size=<N>]
                            The batch size specifies the maximum number of modules
                            be handled in one method to workaround if the generated
                            bytecode exceeds the method size limit. The default
                            batch size is 75.
  --vendor-bug-url <vendor-bug-url>
                            Override the vendor bug URL baked into the build.
                            The value of the system property
                            "java.vendor.url.bug" will be <vendor-url-bug>.
  --vendor-version <vendor-version>
                            Override the vendor version string baked into the
                            build,if any. The value of the system property
                            "java.vendor.version" will be <vendor-version>.
  --vendor-vm-bug-url <vendor-vm-bug-url>
                            Override the vendor VM bug URL baked
                            into the build.  The URL displayed in VM error
                            logs will be <vendor-vm-bug-url>.
  --vm <client|server|minimal|all>
                            Select the HotSpot VM in the output image.
                            Default is all

For options requiring a <pattern-list>, the value will be a comma separated
list of elements each using one the following forms:
  <glob-pattern>
  glob:<glob-pattern>
  regex:<regex-pattern>
  @<filename> where filename is the name of a file containing patterns to be
              used, one pattern per line
```

### compress

```powershell
--compress={0|1|2}[:filter=pattern-list]
```

压缩输出 image 的所有资源：

- Level 0: 不压缩
- Level 1: Constant string sharing
- Level 2: ZIP

可选的 pattern-list 用于列出要包含文件的模式。

### include-locales

```powershell
--include-locales=langtag[,langtag]*
```

包含 locales，其中 `langtag` 为 BCP 47 language tag。该选项支持的 locale 可参考 RFC 4647。使用该选项要确保添加 `jdk.localedata` module。

示例：

```powershell
--add-modules jdk.localedata --include-locales=en,ja,*-IN
```

### order-resources

```powershell
--order-resources=pattern-list
```

按优先级对指定 paths 排序。如果指定 `@filename`，那么 pattern-list 的每一行都必须与要排序的路径完全匹配。

示例：

```powershell
--order-resources=/module-info.class,@classlist,/java.base/java/lang/
```

### strip-debug

```powershell
--strip-debug
```

从输出 image 中提出 debug 信息。

### generate-cds-archive

```powershell
--generate-cds-archive
```

如果 runtime-image 支持 CDS 特性，则生成 CDS 归档文件。

## jlink 示例

### 创建 runtime-image

创建 runtime-image 需要两部分信息：

- `--add-modules` - 添加到 image 的模块
- `--output` - image 的输出目录

jlink 根据 `--add-modules` 指定的模块开始解析模块，但有几个特点：

- 默认不包含 services
- 不解析 optional-dependencies，需要手动添加
- 不支持 automatic-modules

最简单的 runtime-image 只包含 java.base 模块：

```shell
# create the image
$ jlink
    --add-modules java.base
    --output jdk-base
# use the image's java launcher to list all contained modules
$ jdk-base/bin/java --list-modules
> java.base
```

### 创建 app-image

jlink 并不区分 JDK 模块和其它模块，因此，可以创建包含整个 app 和支撑该 app 的 JDK 模块的镜像。创建 app-image 需要：

- 用 `--module-path` 指定 app-module 所在目录
- 用 `--add-modules` 指定 main-module 和其它模块（如包含 services 的模块）

需要注意的时，jlink 不支持 automatic-module。



### 示例演示

- 创建 runtime-image

```powershell
>jlink --output my-runtime --add-modules java.base
```

该命令生成一个 `my-runtime` 目录，该目录包含：

| 内容       | 说明                    |
| ---------- | ----------------------- |
| `bin/`     | 动态库、java 可执行程序 |
| `conf/`    | 配置文件                |
| `include/` | header 文件             |
| `legal/`   | license 文件            |
| `lib/`     | 依赖项                  |
| `release`  | 包含 Java 版本信息      |

不同系统生成的 runtime-image 不同。例如，在 Windows，`bin/` 目录包含 `java.exe`，该文件只能在 Windows 运行。

- 使用 `java --list-modules` 查看 java runtime 包含的模块

查看上面生成的 `my-runtime` image 包含的模块

```powershell
>my-runtime\bin\java --list-modules
java.base@21.0.3
```

- 选择 runtime-image 包含的模块

生成包含 `java.base` 和 `java.logging` 模块的 runtime-image:

```powershell
>jlink --output my-runtime-logging --add-modules java.base,java.logging
```

生成包含所有 Java SE 模块的 runtime-image。这虽然方便，但是会包含不必要的模块：

```powershell
>jlink --output my-runtime-se --add-modules java.se
```

- 使用自定义 runtime 运行模块

只要没有使用 `--strip-native-commands` 选项移除 `java.exe`，就可以用 runtime-image 运行任意模块。

例如，运行 runtime 所含模块中的一个类：

```
myruntime/bin/java --module mymodule/hello.MainWindow
```

要运行不在 runtime 中的 JAR，需要先将该 JAR 添加到 module-path，然后运行。如果 JAR 在 manifest 中定义了 main-class，则直接运行：

```
# Run a module from a JAR using a custom runtime
myruntime/bin/java --module-path .;mylib.jar --module mymodule/MyClass
# Or if the JAR has a main class defined in the JAR manifest
myruntime/bin/java -jar mylib.jar
```

### 更多示例

- 在 `greetingsapp` 目录创建 runtime-image。

该命令链接 `mlib` 目录中的模块 `com.greetings` module。

```
jlink --module-path mlib --add-modules com.greetings --output greetingsapp
```

- 列出 runtime-image `greetingsapp` 包含的模块

```
greetingsapp/bin/java --list-modules
com.greetings
java.base@11
java.logging@11
org.astro@1.0
```

`@` 前为模块名称，`@` 后为模块版本。

- 在 `compressedrt` 目录创建 runtime-image，删除 debug 符号，使用 compression 来减少空间，并包含 French 语言的 locale 信息

```
jlink --add-modules jdk.localedata --include-locales=fr --output fr_rt

du -sh ./compressedrt ./fr_rt
23M     ./compressedrt
36M     ./fr_rt
```

- 列出实现 `java.security.Provider` 的 providers

```
jlink --suggest-providers java.security.Provider

Suggested providers:
  java.naming provides java.security.Provider used by java.base
  java.security.jgss provides java.security.Provider used by java.base
  java.security.sasl provides java.security.Provider used by java.base
  java.smartcardio provides java.security.Provider used by java.base
  java.xml.crypto provides java.security.Provider used by java.base
  jdk.crypto.cryptoki provides java.security.Provider used by java.base
  jdk.crypto.ec provides java.security.Provider used by java.base
  jdk.crypto.mscapi provides java.security.Provider used by java.base
  jdk.security.jgss provides java.security.Provider used by java.base
```

- 创建名为 `mybuild` 的自定义 runtime-image，只包含 `java.naming` 和 `jdk.crypto.cryptoki` 及它俩的依赖项。注意，依赖性必须在 module-path 中

```
jlink --add-modules java.naming,jdk.crypto.cryptoki --output mybuild
```

- 与创建 `greetingsapp` runtime-image 类似，但添加通过 service-binding 从 root-module 解析的所有 modules。

```
jlink --module-path mlib --add-modules com.greetings --output greetingsapp --bind-services
```

- 列出 `greetingsapp` runtime-image 包含的模块

```
greetingsapp/bin/java --list-modules
com.greetings
java.base@11
java.compiler@11
java.datatransfer@11
java.desktop@11
java.logging@11
java.management@11
java.management.rmi@11
java.naming@11
java.prefs@11
java.rmi@11
java.security.jgss@11
java.security.sasl@11
java.smartcardio@11
java.xml@11
java.xml.crypto@11
jdk.accessibility@11
jdk.charsets@11
jdk.compiler@11
jdk.crypto.cryptoki@11
jdk.crypto.ec@11
jdk.crypto.mscapi@11
jdk.internal.opt@11
jdk.jartool@11
jdk.javadoc@11
jdk.jdeps@11
jdk.jfr@11
jdk.jlink@11
jdk.localedata@11
jdk.management@11
jdk.management.jfr@11
jdk.naming.dns@11
jdk.naming.rmi@11
jdk.security.auth@11
jdk.security.jgss@11
jdk.zipfs@11
org.astro@1.0
```

## 参考

- https://docs.oracle.com/en/java/javase/21/docs/specs/man/jlink.html
