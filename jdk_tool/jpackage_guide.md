# jpackage 指南

2024-01-26⭐
***
## 简介

JAR 文件包含编译的 Java 类文件和其它起源，基于 ZIP 格式设计。

可执行 JAR 文件也是 JAR 文件，但包含一个 main 类。manifest 文件会引用 main 类。为了运行 JAR 格式的程序，必须有一个 JRE 环境。

与 JAR 文件不同，特定于平台的可执行文件可以在构建它的平台上运行，为了获得良好的终端用户体验，最好向客户提供特定于平台的可执行文件。

`jpackage` 可以将模块化或非模块化的 Java 程序打包成安装包，支持 Linux, macOS 和 Windows。支持：

- 设置软件图标
- 在特定位置安装程序
- 指定启用应用时的 JVM 选项和程序参数
- 设置关联文件类型，这样在打开关联文件时自动启用应用
- 从系统的菜单启用应用
- 设置多个启动器
- 对包签名（仅支持 macOS）

Java 9 引入 jlink，Java 14 引入的 jpackage 底层使用 jlink。

使用 jpackage 打包有两种方式：

- 分步：先用 jlink 生成 runtime 镜像，然后用 jpackage 生成安装包。
- 一步到位：直接运行 jpackage，jpackage 会自动调用 jlink 生成可执行程序，然后创建安装包，

### 准备系统工具

需要在目标平台上构建应用程序包，用于打包的系统还需要包含：

- 待打包的应用
- JDK
- 打包工具所需的软件

要针对多个平台打包程序，必须在每个平台上运行打包工具。如果需要一个平台上的多种格式，则必须为每种格式运行一次打包工具。

平台、格式以及所需软件：

| 平台    | 打包格式      | 所需工具                                                     |
| ------- | ------------- | ------------------------------------------------------------ |
| Linux   | deb, rpm      | 对 Red Hat Linux，需要 rpm-build package<br />对 Ubuntu Linux，需要 fakeroot package |
| macOS   | pkg, app, dmg | 若使用 `--mac-sign` 选项签名，或使用 `--icon` 选项自定义 DMG image，需要 Xcode 命令行工具 |
| Windows | exe, msi      | WiX 3.0+                                                     |

### 准备应用

要打包应用，首先需要创建必要的JAR 文件或 module 文件。应用所需的资源也必须在系统上可用。

以下是打包所需的应用相关资源：

- 应用的 JAR 文件或 module 文件
- 应用元数据，如 name, version, description, copyright, lice file 等
- 安装选项，如快捷方式、菜单 group、额外启用器，关联文件
- 启用选项，如应用参数，JVM 选项

作为打包过程的一部分，将基于输入目录中的文件创建一个应用程序镜像。在创建安装包之前测试应用，可以使用 `--type app-image` 选项只创建应用镜像。

### 生成镜像

打包工具根据输入创建镜像。

下面在每个平台创建的 Hello World 应用的镜像。其中一些可能修改的细节文件没有列出。

- Linux

```
myapp/
  bin/              // Application launchers
    HelloWorld
  lib/
    app/
      HelloWorld.cfg     // Configuration info, created by jpackage
      HelloWorld.jar     // JAR file, copied from the --input directory
    runtime/             // Java runtime image
```

- macOS

```
HelloWorld.app/
  Contents/
    Info.plist
    MacOS/               // Application launchers
      HelloWorld
    Resources/           // Icons, etc.
    app/
      HelloWorld.cfg     // Configuration info, created by jpackage
      HelloWorld.jar     // JAR file, copied from the --input directory
    runtime/             // Java runtime image
```

- Windows

```
HelloWorld/
  HelloWorld.exe       // Application launchers
  app/
    HelloWorld.cfg     // Configuration info, created by jpackage
    HelloWorld.jar     // JAR file, copied from the --input directory
  runtime/             // Java runtime image
```

生成的镜像适用于大多数应用。如果需要，还可以在打包镜像进行分发前进行修改。

### 准备 Java Runtime

为了避免用户安装 JRE，可以将 Java runtime 打包到应用中。打包工具根据应用所需的包或模块生成 runtime 镜像。

如果没有将 Java runtime 镜像传递给打包工具，`jpackage` 会调用 `jlink` 创建 runtime-image。由打包工具创建的 runtime 镜像不包含 debug 符号、常用的 JDK 命令、man 页面以及 `src.zip` 文件。

- 对由 JAR 文件组成的 non-modular 应用，生成的 runtime 镜像包含的模块与常规 java 启动程序在 unnamed module 中提供给 classpath 的JDK 模块相同。

它不包含可用的 service providers，但是可以通过 `--jlink-options` 选项添加，并添加 `--bind-services jlnk` 选项

```
jpackage --name DynamicTreeDemo \
         --input . --main-jar DynamicTreeDemo.jar \
         --jlink-options --bind-services
```

使用 `--add-modules` 选项添加额外模块：

```
jpackage --name DynamicTreeDemo \
         --input . --main-jar DynamicTreeDemo.jar \
         --module-path $JAVA_HOME/jmods \
         --add-modules java.logging
```

- 对 modular JAR 文件和 JMOD 文件组成的模块化应用，生成的 runtime 镜像包含应用的 main module 以及所有的依赖项。

不包含 service providers，可以通过 `--jlink-options` 和 `--bind-services jlink` 选项添加：

```
jpackage --name Hello --module-path mods \
         --module com.greetings/com.greetings.Main \
         --jlink-options --bind-services
```

使用 `-add-modules` 添加额外模块：

```
jpackage --name Hello --module-path "mods:$JAVA_HOME/jmods" \
         --add-modules java.logging \
         --module com.greetings/com.greetings.Main
```

> [!NOTE]
>
> 如果不指定 `--jlink-options` 选项，`jpackager` 工具默认添加如下 `jlink` 选项：`--strip-native-commands`, `--strip-debug`, `--no-man-pages`, and `--no-header-files`

## 基本打包

如果不需要自定义，或支持多启用、文件关联等功能，打包只需要几个选项。

最简单的打包，需要应用位置，JAR 名称或包含 main 类的模块。

- 打包 non-modular 应用

```
jpackage --input app-directory --main-jar jar-file [--main-class main-class]
```

`app-directory` 是包含 JAR 文件的目录，该路径可以是绝对路径，或相对当前目录的相对路径。

`jar-file` 是包含 main-class 的 JAR 文件名称。

`main-class` 是 main 类名称，在 `MANIFEST.MF` 文件没有指定 main 类时才需要。

- 打包 modular 应用

```
jpackage --module-path module-path --module main-module[/class]
```

`module-path` 为包含 modules 的目录或一个 modular JAR 文件。路径可以是绝对路径，或相对当前目录的相对路径。对多个路径，在 Linux 和macOS 上用冒号 `:` 分隔，在 Windows 上用分号 `;` 分隔，也可以使用多个 `--moddule-path` 选项。

`main-module/class` 为包含 main 类的 module 以及 main 类名称。只要在 main-module 没有知道你个 main-class 时才需要指定 main-class。

### 默认选项

`jpackage` 的一些选项的默认值：

- package 类型
  - Linux, 对 Debian Linux 默认为 deb，对其它 Linux 默认为 rpm
  - macOS，默认为 dmg
  - Windows，默认为 exe

要生成不同类型的 package，用 `--type` 选项。

- 生成的 package 默认写入当前工作目录

用 `--dest` 选项将 package 写入其它位置。

- package 名称由 application 名称和 version 组合而成

如果没有提供 application-name，使用 main-JAR 或 main-module 名称，后面加上 version（默认为 1.0），例如 `HelloWorld-1.0.exe`。

用 `--name` 选项修改 application-name。用 `--app-version` 修改 application version。

- 打包时使用 `jlink` 命令生成 java runtime

可以使用 `--add-modules` 和 `--jlink-options` 将其它项添加到 runtime。

使用 `--runtime-image` 打包自定义 runtime。

- 安装目录
  - Linux, 默认为 `/opt/application-name`
  - macOS, 默认为 `/Applications/application-name`
  - Windows，默认为 `c:\Program Files\application-name`，如果使用了 `--win-per-user-install` 选项，则默认为 `C:\Users\user-name\AppData\Local\application-name`

application 目录的名称默认为 application-name。使用 `--install-dir` 指定不同目录名称。

- application-launcher 名称默认为 application-name

如果包含多个 launcher，使用 `--add-launcher` 来区分。

- 命令行参数

在启用 application 时，默认没有命令行参数和 java runtime 选项。在启用 application 时用户可以从命令传入 application 参数，但是不能传如 java runtime 选项。

- icon

有默认 icon。使用 `--icon` 设置不同图标。

- package name

对 Linux，package 的默认名称为 application-name。使用 `--linux-package-name` 指定其它名称。

- macOS

对 macOS，application 识别符默认为 main-class-name。使用 `--mac-package-identifier` 指定其它识别符。

菜单栏中显示的 application 名称默认为 main-class-name。使用 `--mac-package-name` 指定其它名称。

### 打包 non-modular 应用

打包 non-modular 应用只需要提供要打包文件的位置和 main-JAR 名称。其它使用默认选项。

在 Windows 系统，下面命令将 `mySamples\hwapp` 目录中的 non-modular 应用打包，main-class 在 `HelloWorld.jar` 文件：

```
jpackage --input mySamples\hwapp --main-jar HelloWorld.jar 
```

因为没有指定其它选项，所以采用了如下默认设置：

- 默认打包类型为 `exe`
- 生成的 package 名称为 `HelloWorld-1.0.exe`
- package 输出到当前目录
- 在打包过程会同时生成 runtime-image
- 安装目录为 `c:\Program Files\HelloWorld`
- launcher 名称为 `HelloWorld.exe`
- 采用默认 icon
- 不创建快捷方式，不将应用添加到任何菜单中。用户必须到安装应用的目录来运行它。
- 在启动应用时，没有默认参数和 Java runtime 选项传递给应用。

### 打包 modular 应用

modular 应用打包，只需要提供 modular 位置和 main-module 名称。其它选项参数默认值。

在 Debian Linux 系统运行如下命令，打包 `myModApps` 目录中的 modular 应用，其 main-class 在 `modhw/modhw.HelloWorldMod` 模块：

```
jpackage --module-path myModApps --module modhw/modhw.HelloWorldMod
```

因为没有指定其它选项，余下采用默认值：

- 在 Debian 系统默认打包类型为 `deb`
- 生成的 package 名称为 `HelloWorldMod-1.0.deb`
- 生成的 package 放在当前目录
- 在打包过程中生成 runtime-image
- 安装位置为 `/opt/HelloWorldMod`
- launcher 名称为 `HelloWorldMod`
- 使用默认 icon
- 不创建快捷方式，也没有添加到任何菜单。因此必须到安装应用的目录才能运行
- 启用应用时，没有默认参数和 Java runtime 选项。

### Package Metadata

创建应用后，你可能希望提供程序相关信息，比如描述信息、供应商和版权声明等。

下面以 Windows 系统为例演示如何添加这些信息。

- 设置应用名称

使用 `--name` 选项设置软件名称（用户看到的名称）。默认为 main JAR 或 module 名称。

下面命令为 `DynamicTreeDemo-1.0.exe` 应用创建包：

```
jpackage --name DynamicTreeDemo --input myApps \
   --main-jar DynamicTree.jar 
```

- 设置应用版本

使用 `--app-version` 设置应用版本。如果不指定版本，默认为 1.0.

下面自定义版本号，创建 `DynamicTreeDemo-2.0.exe`：

```
jpackage --name DynamicTreeDemo --app-version 2.0 \
   --input myApps --main-jar DynamicTree.jar
```

- 设置描述信息

使用 `--description` 为应用设置一个简要描述。没有提供默认值。

下面为 DynamicTreeDemo 提供描述信息：

```
jpackage --dest packages --name DynamicTreeDemo \
   --app-version 2.0 --input myApps --main-jar DynamicTree.jar \
   --description "Demo application for testing functionality"
```

> [!NOTE]
>
> 如果描述信息包括空格，必须添加引号。

- 设置 vendor

用 `--vendor` 选项将个人或公司设置为应用的创建者。没有默认值。

下面将 DynamicTreeDemo 的 vendor 设置为 "Small, Inc"。如果 vendor 包含空格，必须加引号。

```
jpackage --dest packages --name DynamicTreeDemo \
   --app-version 2.0 --input myApps --main-jar DynamicTree.jar \
   --description "Demo application for testing functionality" \
   --vendor "Small, Inc"
```

- 设置 copyright

使用 `--copyright` 选项设置版权信息。没有默认值。

下面为 DynamicTreeDemo 设置一个版权声明。如果版权声明包含空格，必须加引号。

```
jpackage --dest packages --name DynamicTreeDemo \
   --app-version 2.0 --input myApps --main-jar DynamicTree.jar \
   --description "Demo application for testing functionality" \
   --vendor "Small, Inc" --copyright "Copyright 2020, All rights reserved"
```

## 应用要求支持

jpackage 支持默认参数、JVM 选项、关联文件、多个 launchers 和签名。

### 默认命令行参数

如果应用接受命令行参数，可以用 `--arguments` 选项定义默认值。在启动应用时用户可以覆盖这些值。

如果提供默认参数打包程序，当用户启动程序且没有提供参数时，这些默认参数将传递给 main-class。jpackage 生成的 app 镜像的 `/app` 目录下的 `app-name.cfg` 文件中的 `[ArgOptions]` 部分显示了这些默认参数。可以检查该文件以确定参数是否定义正确。

下面通过示例演示设置默认参数的方法。

- 设置单个参数的默认值

下面设置 `MyApp` 的单个参数值：

```
jpackage --name MyApp --input samples/myapp --main-jar MyApp.jar \
   --arguments arg1 
```

- 设置多个参数的默认值

使用空格分隔不同参数，并将整个字符串用引号括起来；或者使用多个 `--arguments` 选项。下面演示用不同方式设置 `MyApp` 应用的三个参数的默认值：

```
jpackage --name MyApp --input samples/myapp --main-jar MyApp.jar \
   --arguments "arg1 arg2 arg3" 

jpackage --name MyApp --input samples/myapp --main-jar MyApp.jar \
   --arguments arg1 --arguments "arg2 arg3" 

jpackage --name MyApp --input samples/myapp --main-jar MyApp.jar \
   --arguments arg1 --arguments arg2 --arguments arg3 
```

- 设置带空格的默认值

如果参数包含空格，则需要用两个引号来确保 jpackage 将空格作为值的一部分，而不是作为值之间的分隔符。

将参数用单引号括起来；或者双引号前加转义字符，然后再加上引号。

```
jpackage --name MyApp --input samples/myapp --main-jar MyApp.jar \
   --arguments "\"String 1\" \"String 2\"" 

jpackage --name MyApp --input samples/myapp --main-jar MyApp.jar \
   --arguments "\"String 1\"" --arguments "\"String 2\"" 

jpackage --name MyApp --input samples/myapp --main-jar MyApp.jar \
   --arguments "'String 1'" --arguments "'String 2'" 
```

### 设置 JVM 选项

在打包时使用 `--java-options` 设置 JVM 选项。用户无法为应用提供 JVM 选项。

jpackage 生成的 app-image 的 `/app` 目录下的 `app-name.cfg` 文件中的 `[JavaOptions]` 部分包含定义的 JVM 选项。

下面通过示例演示如何设置 JVM 选项。

- 设置一个 JVM 选项

下面将 `MyApp` 的初始 heap 大小设置为 2MB：

```
jpackage --name MyApp --input samples/myapp --main-jar MyApp.jar \
   --java-options Xms2m 
```

- 设置多个 JVM 选项

多个选项用空格分隔，并加上引号；或使用多个 `--java-options` 选项。

下面是设置初始 heap 最小和最大值的方法：

```
jpackage --name MyApp --input samples/myapp --main-jar MyApp.jar \
   --java-options "Xms2m Xmx10m"

jpackage --name MyApp --input samples/myapp --main-jar MyApp.jar \
   --java-options Xms2m  --java-options Xmx10m
```

- JVM 选项带空格

处理方法与命令行参数一样。

```
jpackage --name MyApp --input samples/myapp --main-jar MyApp.jar \
   --java-options "\"-DAppOption=text string\""

jpackage --name MyApp --input samples/myapp --main-jar MyApp.jar \
   --java-options "'-DAppOption=text string'"
```

- JVM 选项带引号

如果 JVM 选项带引号，则需要转义。

下面将 JVM 选项 `-XX:OnError="userdump.exe %p"` 传递给 jpackage：

```
jpackage --name MyApp --input samples/myapp --main-jar MyApp.jar \
   --java-options "-XX:OnError=\"\\\"userdump.exe %p\\\"\""
```

- 使用 `$APPDIR` 宏

将 app 目录下的图片 `myAppSplash.jpg` 作为启动画面，可以使用 `$APPDIR` 宏实现。

在打包时该图片文件必须在输入目录中。在有些 shells 中，还需要对 `$` 进行转义，即 `\$APPDIR`。

```
jpackage --name MyApp --input samples/myapp --main-jar MyApp.jar \
   --java-options "-splash:\$APPDIR/myAppSplash.jpg"
```



## jar 命令

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

## 创建可执行文件

下面介绍如何创建可执行文件。

### 1. 创建可执行 JAR 文件

创建可执行 JAR 文件非常简单。首先需要一个 Java 项目，其中至少有一个带 `main()` 方法的类。假设有一个主类为 `MySampleGUIAppn` 的示例。

第二步，创建 manifest 文件。将该 manifest 文件命名为 MySampleGUIAppn.mf：

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

### 2. 创建 Windows 可执行文件

有了可执行 JAR 文件，就可以准备生成 Windows 可执行文件：

```sh
jpackage --input . --main-jar MySampleGUIAppn.jar
```

该命令完成所需时间稍长。完成后，它会在当前文件夹生成一个 exe 文件，可执行文件名称与 manifest 中的版本号连一起。可以和启动其它 Windows 程序一样启动。

- 示例 2

```
jpackage --input target/ \
  --name JPackageDemoApp \
  --main-jar JPackageDemoApp.jar \
  --main-class com.jpackagedemoapp.JPackageDemoApp \
  --type dmg \
  --java-options '--enable-preview'
```

说明：

- `--input` 指定包含 jar 文件的目录。
- `--name` 指定安装包名称
- `--main-jar` 指定包含 main-class 的 JAR
- `--main-class` 指定 main-class
- 






## 参考

- https://docs.oracle.com/en/java/javase/21/jpackage/
- https://docs.oracle.com/en/java/javase/21/docs/specs/man/jpackage.html
- https://docs.oracle.com/en/java/javase/21/docs/specs/man/jlink.html