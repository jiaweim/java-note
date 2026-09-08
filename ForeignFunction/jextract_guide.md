# jextract 使用指南

## 简介

`jextract` 工具用于解析原生库的头文件（`.h` 文件），并生成 Java 代码（称为“绑定代码”）。这些生成的代码在底层使用了外部函数与内存 API（FFM API），客户端可以通过它们来访问原生库。

通过 FFM API 与原生 C 代码进行交互的工作原理是：首先加载一个原生库（例如 `.so`、`.dll` 或 `.dylib` 文件），该库本质上是一个包含原生函数和全局变量的集合。接着，用户必须使用 `SymbolLookup` 查找想要调用的函数，最后通过 `Linker::downcallHandle` 方法将这些函数链接起来。此外，客户端可能还需要使用 `Linker::upcallStub` 为 Java 函数创建函数指针，通过查找返回的内存地址来访问全局变量，并为需要访问的结构体构建 `MemoryLayout` 实例。`jextract` 工具旨在将这些繁琐的步骤自动化，从而使客户端能够直接开始使用他们感兴趣的原生库。

本指南将向您展示如何运行 `jextract` 工具，以及如何使用它生成的 Java 代码。`samples` 目录下的示例代码也是很好的参考资源。

> [!NOTE]
>
> 目前，`jextract`（以及 FFM API）仅支持 C 语言的头文件。如果您的库是使用其他语言编写的，请参阅“其他语言”相关章节。

## 运行 Jextract

原生库通常包含一个 `include` 目录，其中存放了定义该库接口的所有头文件，并会有一个主头文件。假设我们有一个名为 `mylib` 的库，存储在 `/path/to/mylib` 路径下，其头文件存放在 `/path/to/mylib/include` 目录中。再假设我们在当前正在开发的 Java 项目的根目录下打开了一个终端（Shell），该项目有一个 `src` 源代码目录，对应项目的根包。运行 `jextract` 的典型命令如下：

```bash
$ jextract \
  --include-dir /path/to/mylib/include \
  --output src \
  --target-package org.jextract.mylib \
  --library mylib \
  /path/to/mylib/include/mylib.h
```

在上述命令中：

- `/path/to/mylib/include/mylib.h` 是我们希望为其生成绑定代码的原生库的主头文件。
- `--include-dir /path/to/mylib/include` 指定了头文件搜索目录，用于查找主头文件中通过 `#include` 引入的其他头文件。
- `--output src` 指定了输出文件的根目录。该目录应与项目源代码的根包目录相匹配。
- `--target-package org.jextract.mylib` 指定了生成的类和接口所属的目标包。（注意：`jextract` 会自动在 `--output` 指定的 `src` 目录下创建代表包结构的文件夹）
- `--library mylib` 告诉 `jextract`，生成的绑定代码在运行时应加载名为 `mylib` 的库。（“库加载”相关章节将详细讨论其实现机制）

> [!NOTE]
>
> 向 `jextract` 指定错误的头文件可能会导致解析错误。请查阅相关库的文档，确认应该包含哪个头文件。这个头文件也就是应该传递给 `jextract` 的文件。

如果一个库有多个主头文件，可以在命令行中一并传递给 `jextract`。此外，还有一种特殊的语法可以传递相对于 C 编译器包含路径的头文件。“命令行选项参考”章节对此进行了详细讨论。

传递给 `--library` 的库名称会被映射为特定平台的库文件名，并且必须能通过操作系统的库搜索机制找到。通常的做法是在 `LD_LIBRARY_PATH`（Linux）、`DYLD_LIBRARY_PATH`（Mac）或 `PATH`（Windows）环境变量中指定库所在的目录。更多信息请参阅“库加载”章节。

除了上述选项外，还可以使用 `jextract` 提供的 `--include-XYZ` 系列选项来过滤输出内容。有关更详细的概述，请参阅“过滤”章节。完整的命令行选项列表也可在此处查看。

`jextract` 生成的绝大部分代码都可以通过一个单一的类来访问。默认情况下，该类的名称由传递给 `jextract` 的主头文件名派生而来。例如，如果头文件名为 `mylib.h`，那么派生出的类名将是 `mylib_h`。您也可以使用 `--header-class-name` 选项显式地设置类名。

除了生成的主头文件类之外，`jextract` 还会为 C 头文件中的某些特定声明生成单独的文件。具体来说，结构体（structs）、联合体（unions）、函数指针类型，以及结构体或联合体的类型别名（typedefs）都会生成额外的文件。（所有这些在“使用 Jextract 生成的代码”章节中都有更详细的讨论）。

**跨平台注意事项**

一般来说，`jextract` 生成的绑定代码依赖于运行 `jextract` 时所在的平台。例如，当 C 头文件被 C 预处理器处理时，头文件中的代码可能会使用预处理指令来检测当前平台，并根据平台的不同展开为不同的结果（另请参阅“预处理器定义”章节）。此外，不同的内置 C 类型在不同平台上的格式可能不同（例如，`long` 类型在 Linux 和 Windows 上的大小不同）。这两种情况都会导致 `jextract` 根据运行平台的不同生成不同的输出。

因此，在一个平台上使用 `jextract` 生成绑定代码，然后在另一个平台上使用这些代码时需要格外小心，因为头文件中可能包含依赖于平台的代码，这会导致生成的代码在其他平台上使用时出现异常行为。

不过，C 库也可以编写为不依赖于平台的形式，即所谓的“跨平台库（portable library）”。例如，这些库在所有支持的平台上使用格式相同的数据类型（例如使用 `long long` 代替 `long`，或使用显式指定大小的整数类型如 `int64_t`）。在不同平台之间共享为跨平台库生成的绑定代码通常不会有问题。通常建议的做法是：在计划使用绑定代码的每个平台上分别生成一套绑定代码，然后对比生成的代码，确保各平台之间没有差异后，再共享这一套绑定代码。

**工作流建议**

`jextract` 假定项目使用的原生库版本是相对稳定的。因此，`jextract` 的设计初衷是只需运行一次，然后将生成的源代码添加到项目中。只有当原生库或 `jextract` 本身发生更新时，才需要重新运行 `jextract`。（这也是 `jextract` 自身遵循的工作流：`jextract` 依赖原生的 `libclang` 库来解析 C 源代码）。

这段文本主要介绍了如何通过 C 语言作为中间层，使用 `jextract` 调用其他语言编写的库。以下是为您提供的中文翻译：

## 高级

### 库加载（Library Loading）

当使用 `--library <libspec>` 选项时，生成的代码会在内部使用 `SymbolLookup::libraryLookup` 来加载由 `<libspec>` 指定的库。如果 `<libspec>` 表示的是一个库名称，系统会通过 `System::mapLibraryName` 将其映射为特定平台的文件名。这意味着，例如在 Linux 上指定 `--library mylib` 时，生成的绑定代码会尝试使用 Linux 系统特定的库加载机制（即 `dlopen`）来加载 `libmylib.so`。这种库加载方式还依赖于操作系统特定的搜索机制来查找库文件。在 Linux 上，可以使用 `LD_LIBRARY_PATH` 环境变量来修改搜索路径（详见 `dlopen` 的文档）。在 Mac 上，相关的环境变量是 `DYLD_LIBRARY_PATH`；在 Windows 上，该变量是 `PATH`。不过，Windows 的整体库搜索机制完全不同。在使用 HotSpot JVM 时，还可以使用 `-Xlog:library` 选项来记录 JVM 尝试加载库的位置，这对于排查库加载失败的故障非常有用。

`--library` 选项的 `<libspec>` 参数既可以是库名称，也可以是库文件的路径（相对路径或绝对路径）。如果 `<libspec>` 以 `:` 字符作为前缀，则表示路径，例如 `:mylib.dll`。

了解当前使用平台上的库加载方式非常重要，因为各平台之间的库搜索机制存在差异。此外，也可以使用 JNI 的库加载和搜索机制。当向 `jextract` 指定 `--use-system-load-library` 选项时，生成的绑定代码将尝试通过 `System::loadLibrary` 来加载由 `--library` 指定的库。`System::loadLibrary` 的库搜索路径通过 `java.library.path` 系统属性来指定，而不是依赖操作系统特定的环境变量。不过请注意，如果加载的库存在任何依赖项，这些依赖项仍将再次通过操作系统特定的库加载机制进行加载（这超出了 JVM 的控制范围）。

如果在生成绑定时未指定 `--library` 选项，生成的绑定代码将尝试使用 `SymbolLookup::loaderLookup` 从通过 `System::loadLibrary` 和 `System::load` 加载的库中查找函数，并以 `Linker::defaultLookup` 作为后备（fallback）机制。如果在生成绑定时指定了 `--library`，这两种查找模式同样会被用作后备机制。

在这两种情况下，当加载绑定代码的类加载器（class loader）被垃圾回收（Garbage Collected）时，该库将被卸载。

### 其他语言

正如前言中所述，`jextract` 目前仅支持 C 语言的头文件。但是，许多其他语言也支持与 C 语言的互操作性（C interop），因此 `jextract` 和 FFM API 仍然可以通过一个中间的 C 层来调用使用这些语言编写的库。下表说明了针对各种不同语言的具体操作方法：

| 语言     | 访问方法                                                     |
| -------- | ------------------------------------------------------------ |
| **C++**  | C++ 允许使用 `extern "C"` 来声明 C 方法，并且许多 C++ 库都附带了 C 语言接口。`jextract` 可以直接使用这种 C 语言接口，进而访问相关的 C++ 库。 |
| **Rust** | Rust 生态系统中有一个名为 `cbindgen` 的工具，可用于为 Rust 库生成 C 语言接口。随后，`jextract` 可以解析这种生成的 C 接口，从而实现对目标 Rust 库的访问。 |

## 参考

- https://github.com/openjdk/jextract/blob/master/doc/GUIDE.md