# Launch4j

## 简介

Launch4j 用于创建轻量级 Windows native EXEs。功能包括：

- Launch4j 将 jar 封装为 Windows exe 可执行文件
- 支持在 Windows, Linuix 和 Mac OS X 平台
## 运行 launch4j

GUI 模式：

```sh
launch4j.exe
```

用 *launch4jc.exe* 在控制台模式下指定配置文件包装 jar：

```sh
launch4jc.exe config.xml
```

在 Linux 中用 launch4j 脚本：

```sh
launch4j ./demo/l4j/config.xml
```
## 配置文件

Launch4j 通过 xml 配置文件执行。可以使用 launch4j 的 GUI 或任何文本编辑器创建和编辑。所有文件可以使用绝对路径或相对配置文件的相对路径。

```xml
<!--
  **Bold** elements are required.
  Underlined values are the default when the element is not specified.
  %VAR% expands environment/special variables and registry keys.
-->
**<launch4jConfig>**
  **<headerType>**gui|console|jniGui32|jniConsole32**</headerType>**
  **<outfile>**file.exe**</outfile>**
  <jar>file</jar>
  <dontWrapJar>true|false</dontWrapJar>
  <errTitle>application name</errTitle>
  <downloadUrl>http://java.com/download</downloadUrl>
  <supportUrl>url</supportUrl>
  <cmdLine>text</cmdLine>
  <chdir>path</chdir>
  <priority>normal|idle|high</priority>
  <stayAlive>true|false</stayAlive>
  <restartOnCrash>true|false</restartOnCrash>
  <icon>file</icon>
  <obj>header object file</obj>
  ...
  <lib>w32api lib</lib>
  ...
  <var>var=text (%VAR%)</var>
  ...
  <classPath>
    **<mainClass>**main class**</mainClass>**
    **<cp>**classpath (%VAR%)**</cp>**
    ...
  </classPath>
  <singleInstance>
    **<mutexName>**text**</mutexName>**
    <windowTitle>text</windowTitle>
  </singleInstance> 
  **<jre>**
    **<path>**JRE path (%VAR%)**</path>**
    <requiresJdk>true|false</requiresJdk>
    <requires64Bit>true|false</requires64Bit>
    <minVersion>1.x.x[_xxx]|xxx[.xxx[.xxx]]</minVersion>
    <maxVersion>1.x.x[_xxx]|xxx[.xxx[.xxx]]</maxVersion>
    <!-- Heap sizes in MB and % of available memory. -->
    <initialHeapSize>MB</initialHeapSize>
    <initialHeapPercent>%</initialHeapPercent>
    <maxHeapSize>MB</maxHeapSize>
    <maxHeapPercent>%</maxHeapPercent>
    <opt>text (%VAR%)</opt>
    ...
  **</jre>**
  <splash>
    **<file>**file**</file>**
    <waitForWindow>true|false</waitForWindow>
    <timeout>seconds [60]</timeout>
    <timeoutErr>true|false</timeoutErr>
  </splash>
  <versionInfo>
    **<fileVersion>**x.x.x.x**</fileVersion>**
    **<txtFileVersion>**text**</txtFileVersion>**
    **<fileDescription>**text**</fileDescription>**
    **<copyright>**text**</copyright>**
    **<productVersion>**x.x.x.x**</productVersion>**
    **<txtProductVersion>**text**</txtProductVersion>**
    **<productName>**text**</productName>**
    <companyName>text</companyName>
    **<internalName>**filename**</internalName>**
    **<originalFilename>**filename.exe**</originalFilename>**
    <trademarks>text</trademarks>
    <language>
      ALBANIAN|ARABIC|BAHASA|DUTCH_BELGIAN|FRENCH_BELGIAN|BULGARIAN|
      FRENCH_CANADIAN|CASTILIAN_SPANISH|CATALAN|CROATO_SERBIAN_LATIN|
      CZECH|DANISH|DUTCH|ENGLISH_UK|ENGLISH_US|FINNISH|FRENCH|GERMAN|
      GREEK|HEBREW|HUNGARIAN|ICELANDIC|ITALIAN|JAPANESE|KOREAN|
      NORWEGIAN_BOKMAL|NORWEGIAN_NYNORSK|POLISH|PORTUGUESE_BRAZIL|
      PORTUGUESE_PORTUGAL|RHAETO_ROMANIC|ROMANIAN|RUSSIAN|
      SERBO_CROATIAN_CYRILLIC|SIMPLIFIED_CHINESE|SLOVAK|SPANISH_MEXICO|
      SWEDISH|FRENCH_SWISS|GERMAN_SWISS|ITALIAN_SWISS|THAI|
      TRADITIONAL_CHINESE|TURKISH|URDU
    </language>
  </versionInfo>
  <messages>
    **<startupErr>**text**</startupErr>**
    **<jreNotFoundErr>**text**</jreNotFoundErr>**
    **<jreVersionErr>**text**</jreVersionErr>**
    **<launcherErr>**text**</launcherErr>**
    <!-- Used by console header only. -->
    **<instanceAlreadyExistsMsg>**text**</instanceAlreadyExistsMsg>**
  </messages>
**</launch4jConfig>**
```

### headerType

用于包装应用的标头类型。

| Header type | Launcher | Splash screen | Wait for the application to close |
| ---- | ---- | ---- | ---- |
| gui | javaw | yes | 仅在 `stayAlive` 为 true 时等待，否则它立即终止或关闭 splash screen 后终止 |
| console | java | no | 总是等待并返回程序的 exit code |
| jniGui32(beta) | launch4j | yes | 总是等待并返回程序的 exit code |
| jniConsole32(beta) | launch4j | no | 总是等待并返回程序的 exit code |

当使用 JNI headers，JVM 是由 launch4j 包装的可执行文件直接创建的，而不是运行 java/javaw launchers。进程名称与 wrapper 相同。JNI headers 还处于 BETA 阶段，部分特性还不可用。如果用于发布，建议使用经典的 gui/console headers。
### outfile

输出的可执行文件。
### jar

指定要包装的 jar 文件，可选。

如果不包装 jar，而是直接启动，此时输入 jar 相对可执行文件的路径。例如，如果可执行程序 calc.exe 和引用 jar calc.jar 在同一目录，则可以使用 `<jar>calc.jar</jar>`  和 `<dontWrapJar>true</dontWrapJar>` 设置。
### dontWrapJar

可选，默认为 false。默认情况下，Launch4j 将 jar 包装成本机可执行文件，将 `<dontWrapJar>` 设置为 true，意味着取消该默认行为，不包装 jar。此时 exe 作为启动器启动 `<jar>`, `<classPath>` 和 `<mainClass>` 指定的应用。
### errTitle

可选，无法找到 Java，显示的错误消息框的标题。通常应该包含应用程序的名称。在 console header 中，错误消息会包含该前缀。
### cmdLine

可选，常量命令行参数。
### chdir

可选。将当前目录修改为任意相对可执行文件的路径。

- 如果 忽略该属性，或设置为空，则无效。
- 设置为 `.` 表示将当前目录修改为与可执行文件相同的目录。
- 设置为 `..` 表示将可执行文件目录设置为父目录。

```xml
<chdir>.</chdir>
```

```xml
<chdir>../somedir</chdir>
```
### stayAlive

可选，在 GUI-header 中默认为 false，在 console-header 中默认为 true。

启用后，launcher 等待 java 程序完成，并返回 exit-code。
### restartOnCrash

可选，默认为 false。当程序崩溃时，exit-code 不是 0 表示程序崩溃，并重启程序。
### icon

ICO 格式的程序图标。ICON 资源： https://www.iconfinder.com/
### obj

可选，针对自定义 header。指定 header 对象文件列表。
### lib

可选，针对自定义 header。header 使用的库列表。
### singleInstance

可选，只允许应用程序的单个实例。

- **mutexName**

用于识别应用的互斥名称。

- **windowTitle**

可选，仅 GUI-header 识别。要打开窗口的标题部分，而不是运行新的实例。

### jre

JRE 相关设置。

#### `<path>`, `<minVersion>`, `<maxVersion>`

`<path>` 用于指定 JRE 的相对或绝对路径，它不依赖于当前目录或 `<chdir>`。注意，在实际执行应用程序时才检查此路径。

**Launch4j 3.50** 现在 `<path>` 是必需的，在注册表之前搜索，以确保与 runtime 兼容。

**Launch4j 3.50** 在路径和注册表搜索时会`<minVersion>` 和 `<maxVersion>`，之前只在注册表搜索时检查。使用匹配给定范围的第一个 runtime。

- 只有 `<path>

使用指定路径搜索 JRE，如果没找到，停止并抛出错误。

Default: `<path>%JAVA_HOME%;%PATH%</path>`
Bundled: `<path>jre</path>`  
Advanced: `<path>%HKEY_LOCAL_MACHINE\SOFTWARE\UnusualJavaVendor\JavaHome%;%JAVA_HOME%;%PATH%</path>`

- `<path> + <minVersion>  [+ <maxVersion>]`

先搜索路径，如果找不到 Java，则搜索注册表。如果失败，显示一条错误消息，并可以选择打开 Java 下载页面。
#### `<minVersion>, <maxVersion> format - Java < 9`

传统版本的格式为 `1.x.x[_xxx]`，至少需要三部分，例如：

```xml
1.6.0
1.7.0_51
1.8.0_121
```

#### `<minVersion>, <maxVersion> format - Java >= 9`

新的版本格式为 `xxx[.xxx[.xxx]]`，只有第一部分是必需的，不允许下划线后面的更新版本。

```xml
1.6
9
10.0.1
```
#### requiresJdk

可选，默认 false。true 表示需要 JDK 执行软件。

**Launch4j 3.50** 如果在路径和注册表搜索时发现 javac 可用，则进行额外检查。
#### requires64Bit

可选，默认 false。true 表示将 runtime 限制为 64-bit，false 则支持 64-bit 和 32-bit，具体取决于找到哪一个。

该选项适用于路径和注册表搜索。
#### HeapSize, HeapPercent

如果同时指定 size 和 percent，则在运行时会选择内存更多的设置。

如果 runtime 是 32-bit，那么即使在路径和注册表搜索期间有更多内存，也会受限于 32-bit。

**initialHeapSize**

可选，初始 heap 大小，单位 MB。

**initialHeapPercent**

可选，初始 heap 百分比。

**maxHeapSize**

可选，

## 运行时其它 JVM 选项

创建 wrapper 或 launcher 时，所有配置信息都编译到可执行文件中，难以修改。Launch4j 2.1.2 引入了一个新的功能，可以在运行时从 l4j.ini 文件读取 JVM 选项。所以可以在配置文件或 ini 文件中指定 JVM 选项，或者同时使用。

## 环境变量


## Launch4J 设置

![[images/Pasted image 20240126214659.png]]

- Wrapper manifest
可以在这里指定 manifest 文件。
- Change dir
将当前目录更改为相对可执行文件的路径。
- Command line args
指定 java 参数。

当系统不支持 Java，可以指定错误标题（Error title）、下载 Java 的 URL 以及支持 URL。

**Classpath**

在这里可以指定 main 类的 classpath。

**JRE**

用于配置 JRE，如果将 JRE 与应用捆绑在一起，则可以在这里指定路径。

## 参考

- https://sourceforge.net/projects/launch4j/
- https://genuinecoder.com/convert-java-jar-to-exe/
- https://launch4j.sourceforge.net/docs.html