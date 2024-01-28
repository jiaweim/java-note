# jpackage

2024-01-26⭐
***
## 基础

JAR 文件包含编译的 Java 类文件和其它起源，基于 ZIP 格式设计。

可执行 JAR 文件也是 JAR 文件，但包含一个 main 类。manifest 文件会引用 main 类。为了运行 JAR 格式的程序，必须有一个 JRE 环境。

与 JAR 文件不同，特定于平台的可执行文件可以在构建它的平台上运行，为了获得良好的终端用户体验，最好向客户提供特定于平台的可执行文件。

jpackage 用于打包自包含的 Java 应用。语法：

```java
jpackage [options]
```

jpackage 把 Java 应用程序和
### jar 命令

创建 JAR 文件的通用语法是：

```sh
jar cf jar-file input-file(s)
```

`jar` 命令选项：

- `c` 表示想要创建 JAR 文件
- `f` 表示希望输出到一个文件
- `m` 表示已有 manifest 文件的信息
- `jar-file` 为生成 JAR 文件的名称。JAR 文件一般包含 `.jar` 后缀，但非必需
- `input-file(s)` 是一个以空格分隔的文件名列表，用于包含在 JAR 文件中，可以使用通配符 `*`

可以用如下命令查看 JAR 文件内容：

```sh
jar tf jar-file
```

其中 `t` 表示希望列出 JAR 文件内容，`f` 表示需要检查的 JAR 文件在命令行中指定。
### jpackage 命令

`jpackage` 命令工具可以为模块化和非模块化 Java 程序生成可安装包。

它使用 `jlink` 命令生成 Java 运行镜像。因此，可以获得针对特定平台的自包含应用程序包。

因为应用程序包是为目标平台构建，因此该系统必需包含：

- 应用本身
- JDK
- 打包工具需要的软件，对 Windows，jpackage 需要 WiX3.0+

jpackage 命令的常用形式：

```sh
jpackage --input . --main-jar MyAppn.jar
```

## 创建可执行文件

下面介绍如何创建可执行文件。

### 创建可执行 JAR 文件

创建可执行 JAR 文件非常简单。首先需要一个 Java 项目，其中至少有一个带 `main()` 方法的类。假设我们创建一个主要为 `MySampleGUIAppn` 的示例。

第二步，创建 manifest 文件。我们将该 manifest 文件命名为 MySampleGUIAppn.mf：

```manifest
Manifest-Version: 1.0
Main-Class: MySampleGUIAppn
```

准备好 manifest 文件后，就可以创建可执行 JAR：

```sh
jar cmf MySampleGUIAppn.mf MySampleGUIAppn.jar MySampleGUIAppn.class MySampleGUIAppn.java
```

查看创建的 JAR 文件的内容：

```sh
jar tf MySampleGUIAppn.jar
```

```
META-INF/
META-INF/MANIFEST.MF
MySampleGUIAppn.class
MySampleGUIAppn.java
```

接下来，就可以通过 CLI 或 GUI 运行 JAR 可执行文件：

```sh
java -jar MySampleGUIAppn.jar
```

### 创建 Windows 可执行文件

有了可执行 JAR 文件，就可以准备生成 Windows 可执行文件：

```sh
jpackage --input . --main-jar MySampleGUIAppn.jar
```

该命令完成所需时间稍长。完成后，它会在当前文件夹生成一个 exe 文件，可执行文件名称与 manifest 中的版本号连一起。我们可以和启动其它 Windows 程序一样启动。

jpackage 命令还有许多其它 Windows 相关的选项：

- `–type`：可以指定为 `msi` 而不是默认的 `exe` 格式
- `–win-console`：用 console 窗口启动程序
- `–win-shortcut`：在 Windows 开始菜单创建快捷方式
- `–win-dir-chooser`：让用户指定自定义目录安装可执行文件
- `–win-menu –win-menu-group`：让用户在开始菜单指定自定义目录

## jpackage 选项

- `--type` or `-t`

打包 package 类型，包括：{"app-image", "exe", "msi", "rpm", "deb", "pkg", "dmg"}。

app-image 对应绿色版 exe。
## 示例


## 参考

- https://docs.oracle.com/en/java/javase/17/docs/specs/man/jpackage.html