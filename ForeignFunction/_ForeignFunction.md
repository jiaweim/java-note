# Foreign Function & Memory API

## 简介

Java 环境和语言在应用开发中安全又高效。然而，有些应用需要执行超出纯 Java 程序可完成的任务，例如：

- 与已有遗留代码集成，以避免用 Java 重写
- 实现 Java 类库中不具备的功能
- 与最佳 C/C++ 或 Rust 等语言的代码集成，以利用性能或其它环境特有的特性

JNI（Java Native Interface）已有存在很久，但存在许多问题：

- JNI 需要大量步骤和额外部件
- JNI API 只能与 C 和 C++ 编写的库互操
- 无法自动将 Java 类型系统映射到 C 类型系统

除了 native 方法的 Java API，JNI 还需要 C 头文件（.h）和一个调用 native 库的 C 实现文件。还有很多其它约定。

Java 外部函数与内存接口（Foreign Function and Memory Interface, FFM）为 JNI 带来了现代化的实现。

JNI 调用将执行控制从 JVM 转移到 native 代码。除了所需参数，还需要 1 到 2 个额外的操作：

- 代表 JNI 环境的 `JNIEnv`
- 代表 `this` 对象的 `jobject`

JNI 代码要完成的工作是在本机执行的，因此，JNI 调用代表兑本机操作的封装和向本机操作的切换。

## 参考

- https://openjdk.org/jeps/442
- https://docs.oracle.com/en/java/javase/21/core/foreign-function-and-memory-api.html
- https://developer.ibm.com/articles/j-ffm/
- [使用 JNI 的最佳实践](https://developer.ibm.com/articles/j-jni/)