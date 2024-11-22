# jpackage 指南

2024-01-26⭐
***
## 1.简介

JAR 文件包含编译的 Java 类文件和其它起源，基于 ZIP 格式设计。

可执行 JAR 文件也是 JAR 文件，但包含一个 main 类。manifest 文件会引用 main 类。此外，为了运行 JAR 格式的程序，必须有一个 JRE 环境。

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

- pkg 类型
  - Linux, 对 Debian Linux 默认为 deb，对其它 Linux 默认为 rpm
  - macOS，默认为 dmg
  - Windows，默认为 exe

用 `--type` 选项设置生成的 pkg 类型。

- 生成的 pkg 默认写入当前工作目录

用 `--dest` 选项将 package 写入其它位置。

- pkg 名称由 app 名称和 version 组合而成

如果没有提供 app-name，则使用 main-JAR 或 main-module 名称，后面加上 version（默认为 1.0），例如 `HelloWorld-1.0.exe`。

用 `--name` 选项修改 app-name。用 `--app-version` 修改 app-version。

- 打包时使用 `jlink` 命令生成 java runtime

可以使用 `--add-modules` 和 `--jlink-options` 将其它项添加到 runtime。

使用 `--runtime-image` 打包自定义 runtime。

- 安装目录
  - Linux, 默认为 `/opt/application-name`
  - macOS, 默认为 `/Applications/application-name`
  - Windows，默认为 `c:\Program Files\application-name`，如果使用了 `--win-per-user-install` 选项，则默认为 `C:\Users\user-name\AppData\Local\application-name`

application 目录的名称默认为 application-name。使用 `--install-dir` 指定不同目录名称。

- app-launcher 名称默认为 app-name

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
- 生成的 pkg 名称为 `HelloWorld-1.0.exe`
- pkg 输出到当前目录
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

#### 设置 class 和 module 的路径

jpackage 会生成一个默认的 class-path，包含 `--input` 选项中指定的每个 JAR 文件的路径。但是，通过 `--java-options` 选项指定 `-cp`, `-classpath` 或 `-Djava.class.path` 选项可以覆盖默认路径。

如果使用 `--java-options` 指定 class-path，需要包含每个 JAR 文件的路径。例如，如果你的 app 只包含 `myapp.jar` 一个 JAR 文件，但同时想包含 class-path 的 `classes/` 子目录下的类，实现方法：

```
--java-options "-cp \$APPDIR/myapp.jar:\$APPDIR/classes"
```

对 modular app，jpackage 生成的默认 module-path 为 `$APPDIR/mods`。如果使用 `--java-options` 通过 `--module-path` 指定 module-path，则该 module-path 会追加到默认 module-path，而不是替换。

### 设置关联文件

使用 `--file-associations` 指定关联文件，这样在打开这类文件时，会自动启动该 app。

关联文件通过属性文件设置。对每种文件类型，需要使用单独的属性文件和单独的 `--file-associations` 选项。

使用如下属性定义关联文件，其中 `mime-type` 和 `extension` 必须设置一个：

- `mime-type` - app 关联文件的 MIME 类型
- `extension` - app 关联文件的扩展名
- `icon` - app 关联文件的图标。在打包 app 时，icon 必须在输入目录。如果不指定 icon，则使用默认 icon
- `description` - 对关联文件的简短描述

首先，创建属性文件，下面为 JavaScript 文件和 Groovy 文件分别创建一个属性文件：

- FAjavascript.properties

```properties
mime-type=text/javascript
extension=js
description=JavaScript Source
```

- FAgroovy.properties

```properties
mime-type=text/x-groovy
extension=groovy
description=Groovy Source
```

下面打包 FADemo app，并通过属性文件设置关联文件。当用户打开 `.js` 或 `.groovy` 文件，FADemo 启动：

```
jpackage --name FADemo --input FADemo \
   --main-jar ScriptRunnerApplication.jar \
   --file-associations FAjavascript.properties \
   --file-associations FAgroovy.properties
```

### 添加 launcher

使用 `--add-launcher` 添加更多启动方式：

- 以不同默认参数启动
- 通过 windows console 启动
- 打包多个 apps 共用一个 runtime

添加方式：`--add-launcher launcher-name=properties-file`，其中 *launcher-name* 为 launcher 名称，如果包含空格，记得加引号。

launcher 通过属性文件定义。每个 launcher 使用单独的属性文件和 `--add-launcher` 选项添加。属性文件通过如下属性定义（至少设置一个）：

- `module` - 包含 launcher 的 main-class 的 module 名称。如果 main-module 没有标识 main-class，可以用 ``module=main-module/class` 格式设置
- `main-jar` - 包含 main-class 的 JAR 文件名称
- `main-class` - main-class 名称
- `arguments` - 默认参数，以空格分隔。带空格的参数用引号括起来，例如  `arguments=arg1 "arg 2" arg3`
- `app-version` - 版本号
- `java-options` - JVM 选项，以空格分隔。如果选项包含空格，以引号括起来。
- `icon` - 额外 launcher 的图标
- `win-console` - 设置为 true，在启动 app 时会同时启动 console

下面，先创建属性文件。

- 创建不同 app 参数的 launcher

下面两个属性文件，定义不同默认参数。第一个文件定义 3 个参数，第二个定义 2 个参数。

**MLAppArgs1.properties:**

```properties
arguments=arg1 arg2 arg3
```

**MLAppArgs2.properties:**

```properties
arguments="String 1" "String 2"
```

下面命令打包 MyApp，并用上面的两个属性文件添加两个额外的 launcher：

```
jpackage --name MyApp --input samples/myapp --main-jar MyApp.jar \
   --add-launcher MyApp1=MLAppArgs1.properties \
   --add-launcher MyApp2=MLAppArgs2.properties
```

- 添加 windows console launcher

为用户提供是否通过 console 运行 app 的选择，创建如下属性文件：

**MLConsole.properties:**

```properties
win-console=true
```

打包 HelloWorld app，添加 win-console launcher：

```powershell
jpackage --name HelloWorld --input helloworld \
   --main-jar HelloWorld.jar \
   --add-launcher HWConsole=MLConsole.properties 
```

- 添加其它入口

在一个包中包含多个 app，通过添加 launcher，每个 app 都可以单独启动。

如果 FADemo 和 Dynamic Tree 被打包在一起，其中 main-launcher 用于 FADemo，因此为 Dynamic Tree 添加一个 launcher。先创建属性文件：

**MLDynamicTree.properties**

```properties
main-jar=DynamicTree.jar
main-class=webstartComponentArch.DynamicTreePanel
icon=DTDemo.ico
```

下面将两个 app 打包在一起，并添加额外 launcher：

```powershell
jpackage --name MLDemo --input MLDemo \
   --main-jar ScriptRunnerApplication.jar \
   --add-launcher "Dynamic Tree"=MLDynamicTree.properties
```

### app 签名（macOS）

对 macOS 平台，可以使用 `--mac-sign` 给 app 签名。这样包含 app 镜像（`.app`）的 disk-image (`.dmg`)或 package (`.pkg`) 可以进行公证。

对用的 jpackage 选项取决于你是否需要将 app 放到 Mac App Store。

#### 证书

如果在 Mac App Store 外分发 app，需要如下两个证书：

- "Developer ID Application: *<user or team name>*"
- "Developer ID Installer: *<user or team name>*"

如果在 Mac App Store 分发，则需要如下两个证书：

- "3rd Party Mac Developer Application: *<user or team name>*"
- "3rd Party Mac Developer Installer: *<user or team name>*"

#### macOS app 签名选项

使用如下 jpackage 选项签名 macOS app：

- `--mac-sign`: 要求为 macOS 包签名
- `--mac-signing-key-user-name user_or_team_name`: Apple 签名认证的名称部分

此外，还有如下选项：

- `--mac-package-signing-prefix prefix`: When signing the application bundle, this value is prefixed to all components that need to be signed that don't have an existing bundle identifier. If you don't specify this option, then the prefix is the (unqualified) main class name followed by a period (`.`).

- `--mac-signing-keychain keychain_name`: If a keychain other than the standard keychain is used, then specify the name of the keychain as show in the Keychain Access app. The name should end in `.keychain`.

- `--type type`: If you want to create an application image (`.app`), specify `app-image`; if you want to create a package (`.pkg`), specify `pkg`. If you don't specify this option, then this option creates a disk image (`.dmg`).

- `--mac-entitlements path`: Path to the file containing entitlements to use when signing executables and libraries in the bundle.

  If you don't specify the `--mac-entitlements` option nor the `--mac-app-store` option, then `jpackage` uses the entitlements file `default.plist`, which is a built-in resource (see [Resources Used in Packaging](https://docs.oracle.com/en/java/javase/21/jpackage/override-jpackage-resources.html#GUID-405708DC-0243-49FC-84D9-B2A7F0A011A9)). It contains entitlements that enable your signed application to run the JDK.

下面生成一个 disk-image `.dmg`，它包含使用 "Developer ID Application: developer.example.com" 证书签名的 app-image。disk-image 用 `com.example.developer.OurApp` 生成，team 名称为 `developer.example.com`：

```powershell
jpackage --name DynamicTreeDemo --input myApps --main-jar DynamicTree.jar \
   --mac-sign --mac-package-signing-prefix com.example.developer.OurApp. \
   --mac-signing-key-user-name "developer.example.com"
```

## app 安装管理

设置 app 安装和启动方式。通过 jpackage，可以指定要接受的 license，在哪里安装 app，以及是否需要 console。

### 包含 license

如果你希望用户接受在 Windows 或 macOS 上安装你的 app 的条款和条件，可以在打包时用 `--license-file` 选项指定。

如果包含 app 的目录同时包含 license 文件，则该文件将与 app 仪器安装到用户计算机上。如果提供的 license 文件不在 app 目录，那么用户在安装 app 时展示 license 后，该 license 文件不会和 app 一起安装。此外，对静默安装和其它类型的安装，不显示 license 文件。

下面的命令将 license 文件 `myApps/myLicense.txt` 打包到 Dynamic Tree app:

```powershell
jpackage --type exe --name DynamicTreeDemo --input myApps \
   --main-jar DynamicTree.jar --license-file myApps/myLicense.txt
```

### 创建快捷方式

在打包时使用 `--linux-shortcut` 或 `--win-shortcut` 选项，让用户在安装 app 时创建快捷方式。使用 `--icon` 选项为快捷方式设置 icon。

仅 Linux 和 Windows 平台支持快捷方式。如果不提供 icon，则使用默认 icon。如果在 Linux 提供自定义 icon，则会自动创建快捷方式，不需要使用 `--linux-shortcut` 选项。自定义 icon 的格式必须符合平台要求。

下面为 Dynamic Tree app 在 Linux 上安装时创建带默认 icon 的快捷方式：

```shell
jpackage --name DynamicTreeDemo --input myApps --main-jar DynamicTree.jar \
   --linux-shortcut
```

下面为 Dynamic Tree app 在 Window 上安装时创建带自定义 icon 的快捷方式：

```shell
jpackage --name DynamicTreeDemo --input myApps --main-jar DynamicTree.jar \
   --icon DTDemo.ico --win-shortcut
```

### 设置安装位置

使用 `--install-dir` 修改 app 安装目录。在 Windows 可以用 `--win-dir-chooser` 让用户选择安装位置。app 默认安装目录为 package-name。

下面将 Dynamic Tree 安装到 `c:\Program Files\DTDemo` 而非 `c:\Program Files\DynamicTreeDemo`：

```shell
jpackage --type exe --name DynamicTreeDemo --input myApps \
   --main-jar DynamicTree.jar --install-dir DTDemo
```

让用户选择安装目录：

```shell
jpackage --type exe --name DynamicTreeDemo --input myApps \
   --main-jar DynamicTree.jar --win-dir-chooser
```

### 添加到开始菜单

使用 `--linux-menu-group` 或 `--win-menu` 和`--win-menu-group` 将 app 添加到开始菜单。

在 linux 平台，如果不使用 `--linux-menu-group` 选项，则将 app 添加到菜单的未知 group (具体取决于 window-manager)。

在 Windows，可以将 app 添加到开始菜单的特定 group。如果所选 group 不存在，则创建它。如果不提供 group-name，则将 app 添加到 unknown-group。只有使用 `--win-menu` 选项 `--win-menu-group` 选项才有意义。

下面将  Dynamic Tree app 添加到 Windows 开始菜单的 "Small, Inc" group。当名称包含空格时需要引号。

```shell
jpackage --type exe --name DynamicTreeDemo --input myApps \
   --main-jar DynamicTree.jar --win-menu --win-menu-group "Small, Inc"
```

在 macOS，app 在菜单栏显示。显示名称默认为 pkg-name。下面使用 `--mac-package-name` 选项修改显示名称：

```shell
jpackage --name DynamicTreeDemo --input myApps --main-jar DynamicTree.jar \
   --mac-package-name DTDemo
```

### 在 Console 启动

如果 app 需要从 console 启动，或需要 console 交互，可使用 `--win-console` 选项设置从 console 启动 app。

下面的命令告诉 Windows 用 console 启动 Hello World app：

```shell
jpackage --input mySamples\hwapp --main-jar HelloWorld.jar --win-console
```

## 修改 Image 和 Runtime

打包工具生成的 app-image 和 runtime 适用于大多数 app。但是，你依然可以根据需要修改 app-image 和 runtime，然后在打包时使用修改后的版本。

### 修改 app-image

可能因为如下原因修改 app-image：添加或删除文件、添加资源或修改 runtime。如果需要修改 image，则要打包两次。

第一次，使用 `--type app-image` 选项仅创建 app-image：

```shell
jpackage --type app-image --name HelloWorld --input helloworld \
   --main-jar HelloWorld.jar
```

此时会在当前目录创建一个 `HelloWorld` 目录，其中包含 app-image。但是没有创建安装包。

根据需要修改 app-image 后，再次运行打包工具，用修改后的 app-image 创建安装包：

```shell
jpackage --type msi --app-image HelloWorld --name HelloWorld
```

说明：

- 打包 app-image 需要指定 `--name` 选项
- `--runtime-image` 选项不能与 `--type app-image` 同时使用。如果需要使用不同的 runtime，在创建 app-image 时设置。

### 修改 runtime

要自定义 runtime-image，可以在打包前使用 jlink。然后打包时使用 `--runtime-image` 指定 runtime。

需要自定义 runtime-image 的可能原因：

- 有更多选项控制 runtime 的创建
- 使用与运行 jpackage 版本不同的 java 打包
- 对多个 app 使用同一个 runtime

例如，创建包含 JavaFX13 的 JDK 14 runtime，然后将该 runtime 与 app 打包：

```shell
jlink --output jdk-14+fx --module path javafx-jmods-13 \
   --add modules javafx.web,javafx.media,javafx.fxml,java.logging

jpackage --name myapp --input lib --main-jar myApp.jar \
   --runtime-image jdk-14+fx
```

如果打包的 app 需要较早版本的 java-runtime，使用 `--runtime-image` 选项。例如，将 JDK 11 runtime 打包到 app：

```shell
jpackage --name myapp --input lib --main-jar myApp.jar \
   --runtime-image jdk-11.0.5
```

如果 app 需要基于较早版本 JDK 的自定义 runtime，则使用较早版本运行 jlink 创建 runtime-image，然后使用当前 JDK 运行 jpackage，传入自定义 runtime。下面使用 JDK 11.0.5 创建自定义 runtime，然后用 JDK 14 打包：

```shell
c:\Program Files\Java\jdk-11.0.5\bin\jlink output my-jdk11 \
   --add-modules java.desktop,java.datatransfer 

c:\Program Files\Java\jdk-14\bin\jpackage --name myapp --input lib \
   --main-jar myApp.jar --runtime-image my-jdk11
```

## jpackage 资源设置

通过设置 jpackage 资源可以设置高级自定义设置，如设置背景图像等。`--resource-dir` 提供该功能。

如果打包 app 时 jpackage 使用的默认资源不满足需求，可以创建一个目录，放入自定义文件。如果覆盖一个文件，自定义文件必须包含默认文件所含的所有属性。使用 `--resource-dir` 将目录传递给 jpackage。可以为绝对目录，或相对目录。

> [!NOTE]
>
> icon, app-version, app-description, copyright 等可以从命令行设置。建议尽量通过命令设置。

### 打包所用资源

打包工具在打包 app 时有默认模板和其它资源。

这些资源因平台而异。在多数情况，用命令行选项指定的资源优先于资源目录中的资源。对不能从命令选项指定的资源，再将自定义文件添加到传递给 jpackage 的资源目录。

#### Linux (all versions)

- Icon file, `launcher.png`, for the main launcher and any additional launchers. Each launcher can have a separate icon. The file name must match the name of the application or the name of a launcher. If an icon file is not provided for a launcher, the default icon is used.
- Desktop shortcut file, `launcher.desktop`, for the main launcher and any additional launchers The file name must match the name of the application or the name of a launcher.

#### Linux DEB

- Control template, `control`. File that contains information about the application.
- Pre-installation script, `preinst`. Script that is run before the application is installed.
- Pre-removal script, `prerm`. Script that is run before the application is uninstalled.
- Post-installation script, `postinst`. Script that is run after installation completes.
- Post-removal script, `postrm`. Script that is run after the application is uninstalled.
- Copyright file, `copyright`. File that contains copyright and license information.

#### Linux RPM

- Specification for packaging, `package-name.spec`. Instructions for packaging the application.

#### macOS (all formats)

- Icon file, `launcher.icns`, for the main launcher and any additional launchers. More than one file can be provided. The file name must match the name of the application or the name of a launcher. If an icon file is not provided for a launcher, the default icon is used.
- Runtime properties list, `Runtime-Info.plist`.
- Information properties list, `Info.plist`.
- Default entitlements file, `default.plist`.
- Default entitlements file for application packages distributed through Mac App Store, `sandbox.plist`.
- Post-image script, `application-name-post-image.sh`. Custom script that is executed after the application image is created and before the DMG or PKG installer is built. No default script is provided.\

#### macOS DMG

- DMG setup script, `application-name-dmg-setup.scpt`.
- Applications license properties list, `application-name-license.plist`.
- Background file, `application-name-background.tiff`.
- Drive icon, `application-name-volume.icns`.

#### macOS PKG

- Pre-installation script, `preinstall`. Script that is run before the application is installed.
- Post-installation script, `postinstall`. Script that is run after installation completes.
- Background image for Light Mode, `application-name-background.png`.
- Background image for Dark Mode, `application-name-background-darkAqua.png`.

#### Windows

- Post-image script, `application-name-post-image.wsf`. Custom script that is executed after the application image is created and before the MSI installer is built for both `.msi` and `.exe` packages. No default script is provided.
- Main WiX source file, `main.wxs`.
- WiX source file with WiX variables overrides, `overrides.wxi`. Values in this file override values in the main WiX file.
- Icon file, `launcher.ico`, for the main launcher and any additional launchers. More than one file can be provided. The file name must match the name of the application or the name of a launcher. If an icon file is not provided for a launcher, the default icon is used.
- Launcher properties file, `launcher.properties`.

### 查看资源

使用 jpackage 的 `--verbose` 和 `--temp` 选项后去打包所有资源。

在决定是否覆盖 jpackage 默认资源前，可以先查看默认值。

- 使用 `--verbose` 选项查看

`--verbose` 选项提供创建 pkg 过程的详细信息。该信息还包含自定义资源的说明，如添加到资源目录的文件的名称。

下面的示例展示在 Windows 上用 jpackage 打包 Dynamic Tree app 的示例，后面是 `--verbose` 选项输出的一部分，包含所使用的默认资源。

> [!NOTE]
>
> 覆盖 `WinLauncher.template`，需要 `DynamicTree.properties` 文件。
>
> 覆盖 `main.wxs` 需要 `main.wxs` 文件。

```shell
jpackage --input DynamicTree --main-jar DynamicTree.jar --verbose
WARNING: Using incubator modules: jdk.incubator.jpackage
Running [candle.exe, /?]
Running [C:\Program Files (x86)\WiX Toolset v3.11\bin\candle.exe, /?]
Windows Installer XML Toolset Compiler version 3.11.1.2318

   ...

Using default package resource java48.ico [icon] (add DynamicTree.ico to the resource-dir to customize).
Using default package resource WinLauncher.template [Template for creating executable properties file] 
(add DynamicTree.properties to the resource-dir to customize).

   ...

Using default package resource main.wxs [Main WiX project file] (add main.wxs to the resource-dir to 
customize).
Using default package resource overrides.wxi [Overrides WiX project file] (add overrides.wxi to the 
resource-dir to customize).

   ...

```

- 使用 `--temp` 选项保留临时文件以供审查

`--temp` 选项为 jpackage 提供一个目录，将打包过程生成的临时文件写入该目录。（可以为绝对或相对路径）使用 `--temp` 选项，生成的临时文件在打包结束后不会删除。

查看临时文件目录可以查看打包用到的资源。检查每个文件，确定属性和值是否是你想覆盖的。如果覆盖某个文件，自定义文件必须包含默认文件所含的所有属性。

下面示例显示在 Windows 上创建的目录。`config` 目录包含可以覆盖的资源：

```
jpackage --input DynamicTree --main-jar DynamicTree.jar \
   --temp DTtempfiles

\DTtempfiles
   \config
      DynamicTree.ico
      DynamicTree.properties
      main.wxs
      MsiInstallerStrings_en.wxl
      MsiInstallerStrings_ja.wxl
      MsiInstallerStrings_zh.wxl
      overrides.wxi
   \images
   \wixobj
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

  


## 参考

- https://docs.oracle.com/en/java/javase/21/jpackage/
- https://docs.oracle.com/en/java/javase/21/docs/specs/man/jpackage.html
- https://docs.oracle.com/en/java/javase/21/docs/specs/man/jlink.html