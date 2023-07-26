# Paths

2023-07-24, 19:54
****
## 简介

Java 时编译型语言，Java 源代码在 JVM 中执行前，会先被编译成字节码。在编译过程中，Java 编译器需要直到源文件、库和其它依赖项的位置。

sourcepath, classpath 和 buildpath 分别指定源码、编译后的字节码和 libraries 的位置。

## sourcepath

sourcepath 指定 java 项目源码文件 (.java) 的位置。它告诉编译器在哪里可以找到需要编译的源文件。sourcepath 通常在编译阶段使用。

在使用 Java 编译器 (javac) 时，使用 `-sourcepath` 选项指定源码位置。例如：

```sh
javac -sourcepath src -d bin src/com/example/Main.java
```

- `-sourcepath src` 告诉编译器源码在 `src` 目录
- `-d bin` 指定编译的字节码放到 `bin` 目录
- `src/com/example/Main.java` 是需要编译的特定源文件路径

Apache Maven 和 Gradle 这类构建工具为项目配置了特定 sourcepath。例如，Maven 的源代码在 `src/main/java` 目录。

sourcepath 在编译时至关重要，它确保编译器能够找到源文件进行编译。

## classpath

classpath 指定编译后字节码文件（.class）以及依赖库的位置。在运行行告诉 JVM 在哪里找 classes 和 resources。

在命令行中运行 Java 程序时可以用 `-classpath` 选项指定：

```sh
java -classpath bin com.example.Main
```

- `-classpath bin` 告诉 JVM 在 bin 目录查找编译后的字节码文件
- `com.example.Main` 时包含 main 方法类的完全限定名

多个目录或 JAR 文件可以用平台分隔符分隔（Unix 中为 `:`，Windows 上为 `;`）。例如：

```sh
java -classpath bin:lib/* com.example.Main
```

这里 classpath 包括 `bin` 目录和 `lib` 目录下的所有 JAR 文件。

classpath 对 JVM 在运行时定位和加载所需 classes 和 resources 至关重要。它允许 JVM 解析 class 的依赖关系，找到程序使用的字节码文件或 libraries。

## buildpath

buildpath 是一个与集成开发环境（IDE）和构建工具相关的概念。它定义 IDE 在构建、编译和运行 Java 项目时查找源码、字节码和 libraries 的位置。

buildpath 通常在 IDE或构建工具中配置，它可以包含多个源文件夹，多个编译字节码的输出文件夹、libraries 以及其它 resources。

在 Eclipse 和 Intellij IDEA 这样的 IDE 中，可以通过 project settings 或 build configuration 文件来配置 buildpath。buildpath 通常包含项目的源码文件夹，以及项目依赖项。

Apache Maven 和 Gradle 等构建工具提供配置 buildpath 的机制。它们使用配置文件（如 Maven 的 pom.xml 文件，Gradle 的 build.gradle 文件）指定依赖项和其它项目设置。