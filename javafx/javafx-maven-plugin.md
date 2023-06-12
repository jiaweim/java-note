# javafx-maven-plugin


***
## 使用

创建 maven 项目，使用如下格式：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>org.openjfx</groupId>
  <artifactId>hellofx</artifactId>
  <packaging>jar</packaging>
  <version>1.0-SNAPSHOT</version>
  <name>demo</name>
  <url>http://maven.apache.org</url>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <javafx.version>16</javafx.version>
    <javafx.maven.plugin.version>0.0.8</javafx.maven.plugin.version>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.openjfx</groupId>
      <artifactId>javafx-controls</artifactId>
      <version>${javafx.version}</version>
    </dependency>
  </dependencies>
  <build>
    <plugins>
      <plugin>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-maven-plugin</artifactId>
        <version>${javafx.maven.plugin.version}</version>
        <configuration>
          <mainClass>HelloFX</mainClass>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

- 编译项目

```sh
mvn compile
```

这一步可选，也可以使用 maven-compiler-plugin 完成。

- 运行项目

```sh
mvn javafx:run
```

- 对 modular 项目，创建和运行自定义 image

```sh
mvn javafx:jlink
target/image/bin/java -m hellofx/org.openjfx.App
```



### javafx:compile 选项

如果使用 `javafx:compile` 编译，可以按如下方式指定 JDK，默认为 11，下面个将其指定为 JDK 12：

```xml
<plugin>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-maven-plugin</artifactId>
    <version>0.0.4</version>
    <configuration>
        <source>12</source>
        <target>12</target>
        <release>12</release>
        <mainClass>org.openjfx.hellofx/org.openjfx.App</mainClass>
    </configuration>
</plugin>
```

还可以指定编译参数：

```xml
<plugin>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-maven-plugin</artifactId>
    <version>0.0.4</version>
    <configuration>
        <compilerArgs>
            <arg>--add-exports</arg>
            <arg>javafx.graphics/com.sun.glass.ui=org.openjfx.hellofx</arg>
        </compilerArgs>
        <mainClass>org.openjfx.hellofx/org.openjfx.App</mainClass>
    </configuration>
</plugin>
```

## javafx:run

该插件默认包括：`--module-path`, `--add-modules` 以及 `-classpath` 选项。

其它可选项包括：

- `mainClass`， 主类，完全限定名，可以包含 module 名
- `workingDirectory`，当前工作目录
- `skip`，跳过运行，值：`false` (default), true
- `outputFile`，重定向运行输出文件
- `options`, VM 选项
- `commandlineArgs`，程序执行参数，空格分隔
- `includePathExceptionsInClasspath`，解析模块路径时，该值设置为 true，添加产生 classpath 异常的依赖项，默认 false，即不包含这些依赖性。
- `runtimePathOption`，该插件会将依赖项放在 modulepath 或 classpath（基于某些因素）。当设置 `runtimePathOption` 插件将所有依赖项只放在 modulepath 或 classpath。

如果设置为 `MODULEPATH`，则需要一个 module descriptor。所有依赖项要么能模块化，要么包含自动模块名称。

如果设置为 `CLASSPATH`，则需要一个 Launcher 类运行 JavaFX 应用。忽略已有的 module-info。

## 示例

- 添加 VM 选项和命令行参数

```xml
<plugin>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-maven-plugin</artifactId>
    <version>0.0.4</version>
    <configuration>
        <mainClass>org.openjfx.hellofx/org.openjfx.App</mainClass>
        <options>
            <option>--add-opens</option>
            <option>java.base/java.lang=org.openjfx.hellofx</option>
        </options>
        <commandlineArgs>-Xmx1024m</commandlineArgs>
    </configuration>
</plugin>
```

- 也可以使用本地 SDK，而不是 Maven

```xml
<properties>
    <sdk>/path/to/javafx-sdk</sdk>
</properties>

<dependencies>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx.base</artifactId>
        <version>1.0</version>
        <scope>system</scope>
        <systemPath>${sdk}/lib/javafx.base.jar</systemPath>
    </dependency>
    ...
</dependencies>
```

## javafx:jlink 选项

可以使用与 jlink 相同的参数。

- `stripDebug`: Strips debug information out. Values: false (default) or true
- `stripJavaDebugAttributes`: Strip Java debug attributes out (since Java 13), Values: false (default) or true
- `compress`: Compression level of the resources being used. Values: 0 (default), 1, 2.
- `noHeaderFiles`: Removes the includes directory in the resulting runtime image. Values: false (default) or true
- `noManPages`: Removes the man directory in the resulting runtime image. Values: false (default) or true
- `bindServices`: Adds the option to bind services. Values: false (default) or true
- `ignoreSigningInformation`: Adds the option to ignore signing information. Values: false (default) or true
- `jlinkVerbose`: Adds the verbose option. Values: false (default) or true
- `launcher`: Adds a launcher script with the given name.
  - If `options` are defined, these will be passed to the launcher script as vm options.
  - If `commandLineArgs` are defined, these will be passed to the launcher script as command line arguments.
- `jlinkImageName`: The name of the folder with the resulting runtime image
- `jlinkZipName`: When set, creates a zip of the resulting runtime image
- `jlinkExecutable`: The jlink executable. It can be a full path or the name of the executable, if it is in the PATH.
- `jmodsPath`: When using a local JavaFX SDK, sets the path to the local JavaFX jmods

例如：

```xml
<plugin>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-maven-plugin</artifactId>
    <version>0.0.4</version>
    <configuration>
        <stripDebug>true</stripDebug>
        <compress>2</compress>
        <noHeaderFiles>true</noHeaderFiles>
        <noManPages>true</noManPages>
        <launcher>hellofx</launcher>
        <jlinkImageName>hello</jlinkImageName>
        <jlinkZipName>hellozip</jlinkZipName>
        <mainClass>hellofx/org.openjfx.MainApp</mainClass>
    </configuration>
</plugin>
```

可以通过如下方式创建自定义镜像：

```mvn
mvn clean javafx:jlink

target/hello/bin/hellofx
```


## 参考

- https://github.com/openjfx/javafx-maven-plugin
