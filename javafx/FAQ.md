# JavaFX FAQ

## Error 1

2023-06-01
****
Error: JavaFX runtime components are missing, and are required to run this application

Java 9 之后对所有 non-modular JavaFX 项目都会出现该错误。可以通过在命令行参数添加 JavaFX 模块来修正。

### 方法一：快捷修复方式

临时欺骗 Java 启动进程 `LauncherHelper`，使其不知道这是 JavaFX 程序。额外创建一个类作为程序入口：

```java
public class MyLauncher {
    public static void main(String[] args){
        MyApp.main(args);
    }
}
```

这种方法在 IDE 和打包的 jar 中，都能消除该错误。但存在潜在风险。

### 为什么 JavaFX 需要 "runtime components"？

JavaFX 使用自定义的窗口工具 `glass` 以及图形引擎 `prism`。两者都在 `javafx.graphics` 模块中。添加 JavaFX 后，会修改 Java 启动器（launcher），从而允许 JavaFX 在程序启动前设置自己。该功能在 `LauncherHelper` 中实现，当运行任何类或 jar 时都会调用该类。

当启动程序，`LauncherHelper` 会检查是否为 JavaFX 程序，如果是，就将初始化任务交给 `FXLauncher` 类，该类会执行相关的检查和初始化设置。

实际上，`FXLauncher` 加载的 main 类并不是要运行的 JavaFX Application，而是 `FXHelper`。大致流程如下：

![[Pasted image 20230601122041.png|500]]

### 方法二：转换为 module

解决该问题的最佳方式是将项目转换为 module。话虽如此，它有时候无法实现。目前很多库不支持 Java 9 module，如 apache poi。

要减少最终发布程序的大小，需要付出的代价就是，需要指定程序需要哪些模块。

**添加 modules**

```java
module my.project {
    requires javafx.fxml;
    requires javafx.controls;
    opens my.project to javafx.graphics;
    exports my.project;
}
```

### 方法三：添加命令行参数

该方式非常灵活，可以在任何 IDE 中完成，在运行 jar 时也需要提供相同参数。

- 用 `--module-path` 指定 JavaFX SDK 目录，例如：

```sh
--module-path /path/to/javafx-sdk-14/lib
```

- 用 `--add-modules` 指定需添加的模块

```sh
--add-modules javafx.controls,javafx.fxml
```

- 完整命令

```sh
--module-path /path/to/javafx-sdk-14/lib --add-modules javafx.controls,javafx.fxml
```

然后，加这些参数添加到 IDE 运行的 VM 参数：

![[Pasted image 20230601130256.png]]

### 方法四：为 Jar 添加参数

如果依赖项或者项目限制无法转换为 module 形式，依然可以为 jar 他提供运行时参数来启动。例如：

```sh
java -jar myJar.jar --module-path /path/to/javafx-sdk-14/lib --add-modules javafx.controls,javafx.fxml
```

```sh
--module-path $JAVAFX_SDK$ --add-modules=javafx.graphics,javafx.fxml,javafx.media --add-reads javafx.graphics=ALL-UNNAMED --add-opens javafx.controls/com.sun.javafx.charts=ALL-UNNAMED --add-opens javafx.controls/com.sun.javafx.scene.control.inputmap=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.iio=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.iio.common=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.css=ALL-UNNAMED --add-opens javafx.base/com.sun.javafx.runtime=ALL-UNNAMED --add-exports javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED --add-exports javafx.graphics/com.sun.javafx.scene.traversal=ALL-UNNAMED --add-exports javafx.graphics/com.sun.javafx.scene.layout=ALL-UNNAMED --add-exports javafx.graphics/com.sun.javafx.util=ALL-UNNAMED --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED
```

## Error 2

module javafx.graphics does not export com.sun.javafx.util to unnamed module @0x16ffb0fa

添加 VM 参数：

```sh
--add-exports javafx.graphics/com.sun.javafx.util=ALL-UNNAMED
```

```sh
--add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED
```