# Foreign Function and Memory API 指南

2026-03-02
@author Jiawei Mao
***
## 0. 简介

**外部函数与内存**（Foreign Function and Memory, FFM）API 使 Java 程序能够与 Java runtime 以外的代码和数据进行互操。该 API 使 Java 程序能够调用 native 库以及处理 native 数据，且没有 JNI 的脆弱和危险性。该 API 可以调用外部函数（即 JVM 外部代码），安全地访问不由 JVM 管理的外部内存。

FFM API 在 `java.lang.foreign` 包中。

## 1. 堆内与堆外内存

**堆内**（on-heap） 内存是 Java 堆中的内存，是由垃圾回收器（GC）管理的内存。Java 对象在堆中。Java 程序运行时，堆可以增大或缩小。当堆满了，就会触发垃圾回收机制：JVM 会识别不再使用的对象（不可访问的对象），并回收它们占用的内存，从而为分配新对象腾出空间。

**堆外**（off-heap）内存是在 Java 堆之外的内存。如果要在 Java 程序中调用 C 语言等其它语言的函数或方法，其参数必须存放在堆外内存。与堆内存不同，堆外内存不需要时不会触发 GC 自动回收。而是由我们手动控制堆外内存的释放方式和时间。

你可以通过 `MemorySegment` 对象操作**堆外内存**。使用 `Arena` 分配 `MemorySegment` 对象，它支持指定与 `MemorySegment` 对象关联的堆外内存何时释放。

## 2. Memory Segments 和 Arenas

我们可以借助 FFM API，通过 `MemorySegment` 接口访问堆外和堆内内存。每个**内存段**（memory segment, MS）与一段连续内存区域关联。memory-segment 分两种：

- **heap-segment（HS）**- 其底层内存区域位于 Java 堆内部（即 on-heap）
- **native-segment（NS**） - 其底层内存区域位于 Java 堆外部（即 off-heap）。

下面主要介绍如何分配和访问 native-segment

`Arena` (内存作用域) 控制 native-segment 的生命周期。可以使用 `Arena` 接口提供的方法创建 `Areana`，例如 `Arena.ofConfined()`。然后使用 `Arena` 分配 memory-segment。每个 `Arena` 都有一个作用域，该作用域指定 memory-segment 底层的内存区域何时被释放并失效。只有与 memory-segment 关联的 scope 有效时，才能访问该 memory-segment。

下面描述的大多数示例使用的是受限作用域 (**confined-arena**)：

- 通过 `Arena::ofConfined` 创建
- confined-arena 提供有界的、确定性的生命周期，其 scope 从创建开始，到关闭结束
- confined-arena 有一个 owner 线程，通常是创建它的线程
- 只有 owner 线程可以访问在 confined-arena 中分配的 memory-segment。在 owner 线程外关闭 confined-arena 会抛出异常

此外，还有其它类型的 `Arena`：

- **共享作用域**（shared-arena）：通过 `Arena::ofShared` 创建，没有 owner 线程。shared-arena 分配的 memory-segment 可以多个线程访问，任何线程都可以关闭 shared-arena，并且关闭操作是线程安全且原子性。shared-arena 的示例可参考[内存段切片](#内存段切片)
- **自动作用域**（automatic-arena）：通过 `Arena::ofAuto` 创建。这是由 GC 自动管理的内存区域。任何线程可以访问由 automatic-arena 分配的 memory-segment。对 automatic-arena 调用 `Arena::close` 抛出 `UnsupportedOperationException`
- **全局作用域**（global-arena）：通过 `Arena::global` 创建。任何线程都可以访问由 global-arena 分配的 memory-segment。此外，这些 memory-segment 永远不会被释放，对 global-arena 调用 `Arena::close` 抛出 `UnsupportedOperationException`

**示例**：使用 arena 分配一个内存段，将一个 Java `String` 保存到与内存段关联的堆外内存，然而打印堆外内存的内容。在 `try-with-resource` block 结束时，`Arena` 关闭，且与内存段关联的堆外内存被释放。

```java
String s = "My string";
try (Arena arena = Arena.ofConfined()) {
    // 分配堆外内存
    MemorySegment nativeText = arena.allocateFrom(s);
    
    // 访问堆外内存
    for (int i = 0; i < s.length(); i++) {
        System.out.print((char) nativeText.get(ValueLayout.JAVA_BYTE, i));
    }
} // 释放堆外内存
```

```
My string
```

### 2.1 使用 Arena 分配内存段

`Arena` 接口继承了 `SegmentAllocator` 接口，后者包含**分配堆外内存**并将 Java 数据复制到其中的方法。

在上一个示例中，调用的 `SegmentAllocator::allocateFrom(String)` 方法使用 `Arena` 分配一个内存段，将字符串转换为 UTF-8 编码、以 null 结尾的 C 字符串，然后将其保存到内存段：

```java
String s = "My string";
try (Arena arena = Arena.ofConfined()) {

    // 分配堆外内存
    MemorySegment nativeText = arena.allocateFrom(s);
    // ...
}
```

> [!TIP]
>
> 可以调用 `SegmentAllocator.allocateFrom(String,Charset)` 存储其它编码的字符串。`SegmentAllocator` 接口包含多个 `allocateFrom` 方法，方便将各种类型数据存储到内存段。

关于分配和访问更复杂的 native 数据类型，可以参考 [内存布局和结构化访问](#内存布局和结构化访问)。

### 2.2 打印堆外内存内容

以下代码打印名为 `nativeText` 的 `MemorySegment` 中存储的字符：

```java
// 访问堆外内存
for (int i = 0; i < s.length(); i++) {
    System.out.print((char) nativeText.get(ValueLayout.JAVA_BYTE, i));
}
```

`MemorySegment` 接口包含多种访问方法，是你能够从内存段读取或写入数据。每个方法都需要一个 `ValueLayout` 参数，指定与基本数据类型（如 primitives）相关的内存布局。`ValueLayout` 包含要访问内存块的大小、字节序（byte-order）、位对齐（bit alignement）以及用于执行访问操作的 Java 类型。

例如，`MemoryLayout.get(ValueLayout.OfByte,long)` 的参数为 `ValueLayout.JAVA_BYTE`，该 `ValueLayout` 具有以下特点：

- size 与 Java `byte` 相同
- byte-alignment 设置为 1 - 内存布局存储在 8 bits 倍数的内存地址上
- byte-order 设置为 `ByteOrder.nativeOrder()` - 可以将多字节的字节顺序从高到低（big-endian）或从低到高（little-endian）排序的系统

### 2.3 关闭 Arena

当 arena 关闭，例如通过 `try-with-resources` 语句关闭，`Arena` 的作用域失效：此时与其作用域关联的所有内存段也被标记为无效，并且它们底层的内存区域也会被释放。

访问与已关闭 `Arena` 的作用域关联的内存段将抛出 `IllegalStateException`，例如：

```java
String s = "My string";
MemorySegment nativeText;
try (Arena arena = Arena.ofConfined()) {
    // 分配堆外内存
    nativeText = arena.allocateFrom(s);
} // 释放堆外内存

for (int i = 0; i < s.length(); i++) {
    // 抛出异常
    System.out.print((char) nativeText.get(ValueLayout.JAVA_BYTE, i));
}
```

```
Exception in thread "main" java.lang.IllegalStateException: Already closed
```

## 3. 使用 FFM API 调用 C 库函数

以下示例韩式如何使用 FFM API 调用 `strlen` 函数：

```java
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class StrlenExample {

    // 为 C 函数 strlen 创建一个下调 handle
    static final MethodHandle strlen = strlenMH();

    static MethodHandle strlenMH() {
        // 获取 native linker 实例
        Linker linker = Linker.nativeLinker();

        // 找到 C 函数签名地址
        SymbolLookup stdLib = linker.defaultLookup();
        MemorySegment strlen_addr = stdLib.findOrThrow("strlen");

        // 创建一个 C 函数描述
        FunctionDescriptor strlen_sig =
                FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS);

        // 返回 C 函数下调 handle
        return linker.downcallHandle(strlen_addr, strlen_sig);
    }

    static long invokeStrlen(String s) throws Throwable {
        try (Arena arena = Arena.ofConfined()) {
            // 分配堆外内存，复制一个 Java String 参数到堆外内存
            MemorySegment nativeString = arena.allocateFrom(s);
            // 直接从 Java 调用 C 函数
            return (long) strlen.invokeExact(nativeString);
        }
    }

    static void main(String[] args) {
        StrlenExample myApp = new StrlenExample();
        try {
            System.out.println(myApp.invokeStrlen(args[0]));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
```

以下是 C 标准库中 `strlen` 函数的声明：

```c
size_t strlen(const char *s);
```

该函数接受一个字符串参数，返回该字符串的长度。要从 Java 调用该函数，需要执行以下步骤：

1. 为 `strlen` 函数的参数分配堆外内存，即 Java runtime 以外的内存
2. 将 Java 字符串存储到分配的堆外内存中，在 `invokeStrlen` 中，1-2 两步合并为一个调用

```java
MemorySegment nativeString = arena.allocateFrom(s);
```

3. 构建并调用一个指向 `strlen` 函数的 methodHandle。下面接入如何执行该操作。

### 3.1 创建 Native Linker 实例

以下语句获得 native-linker 的实例，native-linker 提供对底层库的访问能力，这些库遵循 Java runtime 所在平台的调用约定。这些库称为 native 库：

```java
Linker linker = Linker.nativeLinker();
```

### 3.2 定位 C 函数地址

调用如 `strlen` 的 native 方法，需要一个下调方法句柄 methodHandle，它是一个指向 native 函数的 `MethodHandle` 实例。创建该实例需要 native 函数的内存地址。为了获取该地址，需要使用 symbolLookup，symbolLookup 支持检索一个或多个库中检索类似 `strlen` 函数的地址。

以下语句获取 `strlen` 函数的地址：

```java
SymbolLookup stdLib = linker.defaultLookup();
MemorySegment strlen_addr = stdLib.findOrThrow("strlen");
```

由于 `strlen` 是 C 标准库的一部分，因此本例通过调用 `Linker.defaultLookup()` 使用 native-linker 的**默认 lookup**。默认 lookup 包含一组常用库（包括 C 标准库）的符号查找。

> [!NOTE]
>
> 调用  [SymbolLookup.libraryLookup(String, Arena)](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/SymbolLookup.html#libraryLookup(java.lang.String,java.lang.foreign.Arena)) 方法可以根据库名称创建 symbol-lookup。该方法会加载指定库，并将其与一个 `Arena` 关联，由该 `Arena` 控制 symbl-lookup 的生命周期。以下示例指定名称为 `libc.so.6` 的库，这是许多 Linux 系统中 C  标准库的名称。
>
> ```java
> static final SymbolLookup stdLib = SymbolLookup.libraryLookup("libc.so.6", arena);
> static final MemorySegment strlen_addr = stdLib.findOrThrow("strlen");
> ```

> [!TIP]
>
> 调用 [SymbolLookup.loaderLookup()](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/SymbolLookup.html#loaderLookup())  可以查找以 [System.loadLibrary(String)](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/System.html#loadLibrary(java.lang.String)) 加载的库中的符号。

### 3.3 描述 C 函数签名

下调方法句柄 `MethodHandle` 还需要对 native 函数签名的描述信息，该信息由 [FunctionDescriptor](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/FunctionDescriptor.html) 实例表示。function-descriptor 用于描述 native 函数的参数布局及其返回值的布局。

function-descriptor 中的每个布局都映射到一个 Java 类型，该类型是调用生成的下调 method-handle 时应当使用的类型。大多数 value-layouts 映射到 Java 基本类型。例如，[ValueLayout.JAVA_INT](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/ValueLayout.html#JAVA_INT) 映射到 `int` 类型。但是，[ValueLayout.ADDRESS](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/ValueLayout.html#ADDRESS) 映射到一个指针。

复合类型（如 `struct` 和 `union`） 通过 `GroupLayout` 接口建模，该接口是 `StructLayout` 和 `UnionLayout` 的超类。有关如何初始化和访问 C 结构的实例，可参考[内存布局和结构化访问](#内存布局和结构化访问)。

下面为 `strlen` 函数创建一个函数描述符：

```java
FunctionDescriptor strlen_sig =
    FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS);
```

`FunctionDescriptor::of` 方法的第一个参数是 native 函数返回值的布局。本地 primitive-types 通过 value-layout 进行建模，其大小与 native 类型大小一致。这意味着函数描述符是 platform-specific。例如，`size_t` 在 64-bit (x64) 平台上的 layout 为 [JAVA_LONG](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/ValueLayout.html#JAVA_LONG)，而在 32-bit(x86) 平台上的 layout 为 [JAVA_INT](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/ValueLayout.html#JAVA_INT)。

> [!TIP]
>
> 使用 [Linker::canonicalLayouts](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/Linker.html#canonicalLayouts()) 确定 native-linker 为当前平台使用的本地 primitive 类型的布局。

`FunctionDescriptor::of` 方法后续参数是 native 函数参数的局部。在该示例中，只有一个参数，即 [ValueLayout.ADDRESS](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/ValueLayout.html#ADDRESS)，它代表 `strlen` 的唯一参数，即指向字符串的指针。

### 3.4 为 C 函数创建下调句柄

通过函数的地址和函数描述符，就可以为 `strlen` 函数创建一个下调句柄。

```java
// 为 C 函数 strlen 创建下调句柄
static final MethodHandle strlen = strlenMH();  
// ...
static MethodHandle strlenMH() {
    // ...
    // 返回 C 函数的下调句柄
    return linker.downcallHandle(strlen_addr, strlen_sig);
}
```

> [!TIP]
>
> 出于性能考虑，建议将方法句柄声明为 `static final` 

### 3.5 直接从 Java 调用 C 函数

下面最后一句通过传入包含函数参数的内存段调用 `strlen` 函数：

```java
static long invokeStrlen(String s) throws Throwable {
    try (Arena arena = Arena.ofConfined()) {
        // 分配堆外内存，复制一个 Java String 参数到堆外内存
        MemorySegment nativeString = arena.allocateFrom(s);
        // 直接从 Java 调用 C 函数
        return (long) strlen.invokeExact(nativeString);
    }
}
```

在调用方法句柄时，你需要将其强制转换为预期的返回类型；在本例中，预期返回类型为 `long`。

## 4. 上调：将 Java 代码作为函数指针传递给外部函数

**上调**（upcall）是从 native 代码调用 Java 代码。上调是你能够将 Java 代码作为函数指针传递给外部函数。

以标准 C 库函数 `qsort` 为例，它对数组元素进行排序：

```c
void qsort(void *base, size_t nmemb, size_t size,
           int (*compar)(const void *, const void *));
```

它包含 4 个参数：

- `base`: 指向要排序数组的第一个元素的指针
- `nbemb`: 数组中元素数量
- `size`: 数组中每个元素的大小（byte）
- `compar`: 指向比较两个元素的函数的指针

下例调用 `qsort` 函数对 `int` 数组排序。但是，该方法需要一个指向比较数组元素函数的指针。下例定义了一个名为 `Qsort::qsortCompare` 的比较方法，并创建一个标识该方法的句柄 `MethodHandle`，然后从该方法句柄创建一个函数指针：

```java
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class InvokeQsort {

    class Qsort {
        static int qsortCompare(MemorySegment elem1, MemorySegment elem2) {
            return Integer.compare(elem1.get(ValueLayout.JAVA_INT, 0), 
                                   elem2.get(ValueLayout.JAVA_INT, 0));
        }
    }

    // 获取 native-linker 实例
    final static Linker linker = Linker.nativeLinker();

    // 创建 qsort 的下调句柄
    static final MethodHandle qsort = linker.downcallHandle(
            linker.defaultLookup().findOrThrow("qsort"),
            FunctionDescriptor.ofVoid(ValueLayout.ADDRESS,
                    ValueLayout.JAVA_LONG,
                    ValueLayout.JAVA_LONG,
                    ValueLayout.ADDRESS));

    // 创建 qsortCompare 的方法句柄
    static final MethodHandle compareHandle = initCompareHandle();

    static MethodHandle initCompareHandle() {
        MethodHandle ch = null;
        try {
            ch = MethodHandles.lookup()
                    .findStatic(Qsort.class,
                            "qsortCompare",
                            MethodType.methodType(int.class,
                                    MemorySegment.class,
                                    MemorySegment.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return ch;
    }

    static int[] qsortTest(int[] unsortedArray) throws Throwable {

        int[] sorted = null;

        // 创建一个由 Java 实现的 C 函数的 Java 描述
        FunctionDescriptor qsortCompareDesc = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS.withTargetLayout(ValueLayout.JAVA_INT),
                ValueLayout.ADDRESS.withTargetLayout(ValueLayout.JAVA_INT));

        // 创建 qsortCompare 的函数指针
        MemorySegment compareFunc = linker.upcallStub(compareHandle,
                qsortCompareDesc,
                Arena.ofAuto());

        try (Arena arena = Arena.ofConfined()) {
            // 创建堆外内存，并存入 unsortedArray         
            MemorySegment array = arena.allocateFrom(ValueLayout.JAVA_INT,
                    unsortedArray);
            // 调用 qsort        
            qsort.invoke(array,
                    (long) unsortedArray.length,
                    ValueLayout.JAVA_INT.byteSize(),
                    compareFunc);

            // 访问堆外内存
            sorted = array.toArray(ValueLayout.JAVA_INT);
        }
        return sorted;
    }

    static void main() {
        try {
            int[] sortedArray = InvokeQsort.qsortTest(new int[]{0, 9, 3, 4, 6, 5, 1, 8, 2, 7});
            for (int num : sortedArray) {
                System.out.print(num + " ");
            }
            System.out.println();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
```

下面对该示例进行详细解释。

### 4.1 定义比较两个元素的 Java 方法

下面这个类定义了比较两个 元素的 Java 方法，这里比较两个 `int` 值：

```java
class Qsort {
    static int qsortCompare(MemorySegment elem1, MemorySegment elem2) {
        return Integer.compare(elem1.get(ValueLayout.JAVA_INT, 0), 
                               elem2.get(ValueLayout.JAVA_INT, 0));
    }
}
```

在该方法中，`int` 值由 `MemorySegment` 对象表示。memory-segment 提供对连续内存的访问。调用 `get` 方法从其中获取值。该示例调用 [get(ValueLayout.OfInt, long)](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/MemorySegment.html#get(java.lang.foreign.ValueLayout.OfInt,long))，第二个参数是相对内存地址位置的 offset (bytes)。因为本例 memory-segment 只存储一个值，所以第二个参数都是 0.

### 4.2 为 qsort 函数创建下调方法句柄

以下代码为 `qsort` 函数创建下调方法句柄：

```java
// 获取 native-linker 实例
final static Linker linker = Linker.nativeLinker();
    
// 创建 qsort 的下调句柄
static final MethodHandle qsort = linker.downcallHandle(
    linker.defaultLookup().findOrThrow("qsort"),
    FunctionDescriptor.ofVoid(ValueLayout.ADDRESS,
                              ValueLayout.JAVA_LONG,
                              ValueLayout.JAVA_LONG,
                              ValueLayout.ADDRESS));
```

> [!TIP]
>
> 出于性能考虑，建议将方法句柄声明为 `static final`

### 4.3 创建表示 qsortCompare 的方法句柄

以下代码创建一个表示比较方法 `Qsort::qsortCompare` 的方法句柄：

```java
// 创建 qsortCompare 的方法句柄
static final MethodHandle compareHandle = initCompareHandle();
                                  
static MethodHandle initCompareHandle() {
    MethodHandle ch = null;
    try {
        ch = MethodHandles.lookup()
            .findStatic(Qsort.class,
                    "qsortCompare",
                    MethodType.methodType(int.class,
                                          MemorySegment.class,
                                          MemorySegment.class));
    } catch (NoSuchMethodException | IllegalAccessException e) {
        e.printStackTrace();
    }
    return ch;
}
```

[MethodHandles.Lookup.findStatic(Class, String, MethodType)](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/invoke/MethodHandles.Lookup.html#findStatic(java.lang.Class,java.lang.String,java.lang.invoke.MethodType)) 方法为 `static` 方法创建方法句柄。它包含三个参数：

- 方法所属的类
- 方法的名称
- 方法类型：[MethodType::methodType](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/invoke/MethodType.html#methodType(java.lang.Class,java.lang.Class,java.lang.Class...)) 的第一个参数是方法返回值类型，其余参数是该方法各个参数的类型。

### 4.4 为方法句柄 compareHandle 创建函数指针

以下代码从方法句柄 `compareHandle` 创建函数指针：

```java
// 创建一个由 Java 实现的 C 函数的 Java 描述
FunctionDescriptor qsortCompareDesc = FunctionDescriptor.of(
    ValueLayout.JAVA_INT,
    ValueLayout.ADDRESS.withTargetLayout(ValueLayout.JAVA_INT),
    ValueLayout.ADDRESS.withTargetLayout(ValueLayout.JAVA_INT));

// 创建 qsortCompare 的函数指针
MemorySegment compareFunc = linker.upcallStub(compareHandle,
                                              qsortCompareDesc,
                                              Arena.ofAuto());
```

[Linker::upcallStub](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/Linker.html#upcallStub(java.lang.invoke.MethodHandle,java.lang.foreign.FunctionDescriptor,java.lang.foreign.MemorySession)) 方法包含三个参数：

- 用于创建函数指针的方法句柄
- 函数指针的函数描述符，在本例中，[FunctionDescriptor.of](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/FunctionDescriptor.html#of(java.lang.foreign.MemoryLayout,java.lang.foreign.MemoryLayout...)) 的参数对应 `Qsort::qsortCompare` 的返回类型和参数
- 与函数指针关联的 `Arena`。`static` 方法 `Arena.ofAuto()` 创建一个由 GC 自动管理的 arena

###  4.5 分配堆外内存以存储 int 数组

以下代码分配堆外内存，然后存入要排序的 `int` 数组：

```java
int[] sorted = null;
// ...
try (Arena arena = Arena.ofConfined()) {                    
       
    // 创建堆外内存，并存入 unsortedArray             
    MemorySegment array = arena.allocateFrom(ValueLayout.JAVA_INT,
                                              unsortedArray);     
```

### 4.6 调用 qsort 函数

以下代码调用 `qsort` 函数：

```java
// Call qsort        
qsort.invoke(array,
             (long)unsortedArray.length,
             ValueLayout.JAVA_INT.byteSize(),
             compareFunc);
```

其中 [MethodHandle::invoke](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/invoke/MethodHandle.html#invoke(java.lang.Object...)) 的参数对应于标准 C 库 `qsort` 函数的参数。

### 4.7 将排序数组从堆外复制到堆内

最后，以下代码将排序后的数组从堆外复制到堆内：

```java
// Access off-heap memory
sorted = array.toArray(ValueLayout.JAVA_INT);         
```

## 5. 返回指针的外部函数

有些外部函数会分配一块内存区域，然后返回指向该区域的指针。例如，C 标准库函数 `void *malloc(size_t)` 分配指定的内存量（bytes），并返回指向该内存的指针。但是，调用像 `malloc` 返回指针的本地函数，Java runtime 无法了解指针指向内存段的大小和生命周期。因此，FFM API 使用**零长度内存段**来表示这种指针。

下面调用 C 标准库含数据 `malloc`，立即打印一条诊断消息，表明 `malloc` 返回的指针是一个零长度的内存段。

```java
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.util.function.Consumer;

public class MallocExample {

    // 获取 native-linkder 实例
    static final Linker linker = Linker.nativeLinker();

    // 为 malloc() 创建下调句柄
    static final MethodHandle malloc = linker.downcallHandle(
            linker.defaultLookup().findOrThrow("malloc"),
            FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_LONG)
    );

    // 为 free() 创建下调句柄
    static final MethodHandle free = linker.downcallHandle(
            linker.defaultLookup().findOrThrow("free"),
            FunctionDescriptor.ofVoid(ValueLayout.ADDRESS)
    );

    static MemorySegment allocateMemory(long byteSize, Arena arena) throws Throwable {

        // 调用 malloc(), 返回一个指针
        MemorySegment segment = (MemorySegment) malloc.invokeExact(byteSize);

        // malloc() 创建的内存段大小为 0 bytes
        System.out.println("Size, in bytes, of memory segment created by calling malloc.invokeExact("
                + byteSize + "): " + segment.byteSize());

        Consumer<MemorySegment> cleanup = s -> {
            try {
                free.invokeExact(s);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };

        // reintepret 方法:
        // 1. 调整内存段大小，使其与 byteSize 相等
        // 2. 将其与已有 arena 关联
        // 3. 当 arena 关闭，调用 free() 释放 malloc() 分配的内存
        return segment.reinterpret(byteSize, arena, cleanup);
    }

    static void main() {
        try (Arena arena = Arena.ofConfined()) {
            allocateMemory(100L, arena);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
```

该示例输出：

```
Size, in bytes, of memory segment created by calling malloc.invokeExact(100): 0
```

FFM API 使用零长度内存段表示以下情况：

- 从外部函数返回的指针

- 外部函数传递给上调的指针
- 从内存段读取的指针

访问零长度内存段会抛出 `IndexOutOfBoundsException`，因为 Java runtime 无法安全地访问或验证大小未知的内存区域的任何访问操作。此外，零长度内存段与一个始终 alive 的新 scope 关联。因此，虽然你无法直接访问零长度内存段，但是可以将它们传递给其它需要指针的外部函数。

但是，`MemorySegment::reinterpret` 让我们能够使用零长度内存段，安全地访问并将其附加到已有 `Arena`，从而管理该内存段的内存区域的生命周期。该方法包含三个参数：

- 调整内存段大小的 bytes 数：示例中将其大小调整为 `byteSize`
- 与内存段关联的 arena：该示例将其与参数 `arena` 关联
- 当 arena 关闭时执行的动作：该示例调用 C 标准库函数 `void free(void *ptr)` 来释放 `malloc` 分配的内存，该函数释放 `malloc` 返回的指针所引用的内存。注意，这是一个将指向零长度内存段的指针传递给外部函数的示例

> [!NOTE]
>
> `MemorySegment::reinterpret` 是一种受限方法，如果使用不当，可能导致 JVM 崩溃或内存损坏。更多信息请参考[受限方法](#受限方法)

以下示例调用 `allocateMemory(long, Arena)` 用 `malloc` 为 Java 字符串分配内存：

```java
String s = "My string!";
try (Arena arena = Arena.ofConfined()) {
            
    // 使用 malloc() 分配堆外内存
    var nativeText = allocateMemory(
        ValueLayout.JAVA_CHAR.byteSize() * (s.length() + 1), arena);
            
    // 访问堆外内存
    for (int i = 0; i < s.length(); i++ ) {
        nativeText.setAtIndex(ValueLayout.JAVA_CHAR, i, s.charAt(i)); 
    }
            
    // 在末尾添加字符串终止符
    nativeText.setAtIndex(
         ValueLayout.JAVA_CHAR, s.length(), Character.MIN_VALUE);
                 
    // 打印字符串
    for (int i = 0; i < s.length(); i++ ) {
        System.out.print((char)nativeText.getAtIndex(ValueLayout.JAVA_CHAR, i)); 
    }       
    System.out.println();            
} catch (Throwable t) {
    t.printStackTrace();
}
```

更多信息可参考 `java.lang.foreign.MemorySegment` API 规范中的 [Zero-length memory segments](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/MemorySegment.html#wrapping-addresses)，以及 `java.lang.foreign.Linker` API 规范中的 [Functions returning pointers](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/Linker.html#by-ref) 。

## 6. 内存布局和结构化访问

只使用基本操作访问结构化数据，会导致代码难以阅读和维护。为了解决该问题，可以使用内存布局，那个人更有效地初始化和访问复杂的的 native 数据类型，如 C struct。

例如，下面的 C 代码定义了一个 `Point` struct 数组，每个 `Point` 包含两个元素 `Point.x` 和 `Point.y`：

```c
struct Point {
   int x;
   int y;
} pts[10];
```

可以按如下方式初始化和访问这样的内地数组：

```java
public class InitializeNativeArrayExample {

    static void main() {
        InitializeNativeArrayExample myApp = new InitializeNativeArrayExample();
        try {
            myApp.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void initialize() {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment segment = arena.allocate(2 * 4 * 10, 1);

            for (int i = 0; i < 10; i++) {
                int xValue = i;
                int yValue = i * 10;
                segment.setAtIndex(ValueLayout.JAVA_INT, (i * 2), xValue);
                segment.setAtIndex(ValueLayout.JAVA_INT, (i * 2) + 1, yValue);
            }

            for (int i = 0; i < 10; i++) {
                int xVal = segment.getAtIndex(ValueLayout.JAVA_INT, (i * 2));
                int yVal = segment.getAtIndex(ValueLayout.JAVA_INT, (i * 2) + 1);
                System.out.println("(" + xVal + ", " + yVal + ")");
            }
        }
    }
}
```

`Arena::allocate` 方法的第一个参数计算数组所需的 bytes。调用 [MemorySegment::setAtIndex](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/MemorySegment.html#setAtIndex(java.lang.foreign.ValueLayout.OfInt,long,int)) 的参数计算要写入 `Point` 结构中每个成员的内存地址偏移量。[MemorySegment::getAtIndex](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/MemorySegment.html#getAtIndex(java.lang.foreign.ValueLayout.OfInt,long)) 的第一个参数类似。 为了避免这些计算，可以使用内存布局。

为了表示 `Point` 结构数组，下例使用序列内存布局：

```java
import java.lang.foreign.*;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.invoke.VarHandle;

public class MemoryLayoutExample {

    static void main() {
        MemoryLayoutExample myApp = new MemoryLayoutExample();
        try {
            myApp.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static final SequenceLayout ptsLayout
            = MemoryLayout.sequenceLayout(10,
            MemoryLayout.structLayout(
                    ValueLayout.JAVA_INT.withName("x"),
                    ValueLayout.JAVA_INT.withName("y")));

    static final VarHandle xHandle
            = ptsLayout.varHandle(PathElement.sequenceElement(),
            PathElement.groupElement("x"));

    static final VarHandle yHandle
            = ptsLayout.varHandle(PathElement.sequenceElement(),
            PathElement.groupElement("y"));

    void initialize() {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment segment = arena.allocate(ptsLayout);

            for (int i = 0; i < ptsLayout.elementCount(); i++) {
                int xValue = i;
                int yValue = i * 10;
                xHandle.set(segment, 0L, (long) i, xValue);
                yHandle.set(segment, 0L, (long) i, yValue);
            }

            for (int i = 0; i < ptsLayout.elementCount(); i++) {
                int xVal = (int) xHandle.get(segment, 0L, (long) i);
                int yVal = (int) yHandle.get(segment, 0L, (long) i);
                System.out.println("(" + xVal + ", " + yVal + ")");
            }
        }
    }
}
```

以下代码创建一个序列内存布局，由 [SequenceLayout](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/SequenceLayout.html) 对象表示：

```java
static final SequenceLayout ptsLayout
    = MemoryLayout.sequenceLayout(10,
        MemoryLayout.structLayout(
            ValueLayout.JAVA_INT.withName("x"),
            ValueLayout.JAVA_INT.withName("y")));
```

它包含 10 个 struct 布局的序列，struct 布局由  [StructLayout](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/StructLayout.html) 对象表示。[MemoryLayout::structLayout](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/MemoryLayout.html#structLayout(java.lang.foreign.MemoryLayout...)) 方法返回 `StructLayout` 对象。每个 struct 布局包含两个名为 `x` 和 `y` 的 [JAVA_INT](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/ValueLayout.html#JAVA_INT) 值。

预定义值 [ValueLayout.JAVA_INT](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/ValueLayout.html#JAVA_INT) 包含 Java `int` 值所需字节的信息。

接下来的代码创建两个范围内存的 `VarHandle`，用于获取内存地址偏移。`VarHandle` 是对变量或参数定义变量序列的动态强类型引入，包括静态字段、非静态字段、数组元素或堆外数据结构等。

```java
static final VarHandle xHandle
    = ptsLayout.varHandle(PathElement.sequenceElement(),
        PathElement.groupElement("x"));
            
static final VarHandle yHandle
    = ptsLayout.varHandle(PathElement.sequenceElement(),
        PathElement.groupElement("y"));
```

[PathElement.sequenceElement()](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/MemoryLayout.PathElement.html#sequenceElement()) 从序列布局检索内存布局。对该示例，它从 `ptsLayout` 获取一个结构布局。[PathElement.groupElement("x")](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/MemoryLayout.PathElement.html#groupElement(java.lang.String)) 方法检索一名为 `x` 的内存布局。可以使用 [withName(String)](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/MemoryLayout.html#withName(java.lang.String))  创建带名称的内存布局。

`for` 循环中调用 [VarHandle::set](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/invoke/VarHandle.html#set(java.lang.Object...)) 和 [VarHandle::get](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/invoke/VarHandle.html#get(java.lang.Object...)) 用于访问内存，功能同 [MemorySegment::setAtIndex](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/MemorySegment.html#setAtIndex(java.lang.foreign.ValueLayout.OfInt,long,int)) 和 [MemorySegment::getAtIndex](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/MemorySegment.html#getAtIndex(java.lang.foreign.ValueLayout.OfInt,long)).

```java
MemorySegment segment = arena.allocate(ptsLayout);
            
for (int i = 0; i < ptsLayout.elementCount(); i++) {
    int xValue = i;
    int yValue = i * 10;
    xHandle.set(segment, 0L, (long) i, xValue);
    yHandle.set(segment, 0L, (long) i, yValue);
}
            
for (int i = 0; i < ptsLayout.elementCount(); i++) {
    int xVal = (int) xHandle.get(segment, 0L, (long) i);
    int yVal = (int) yHandle.get(segment, 0L, (long) i);
    System.out.println("(" + xVal + ", " + yVal + ")");
}
```

在本例中，`set` 方法使用 4 个参数：

1. `segment`: 用于设置值的内存段
2. `0L`: base-offset, 是一个指向内存起点的 `long` 坐标
3. `(long)i`: 第二个 `long` 值，表示值在数组中的索引
4. `xValue` 和 `yValue`: 要设置的值

`xHandle` 和 `yHandle` 两个`VarHandle` 知道 `Point` 结构的大小（8 字节）和 `int` 成员的大小（4 字节）。因此不需要像 `setAtIndex` 方法那样计算内存地址偏移量。

> [!TIP]
>
> base-offset 通过将额外的 offset 纳入 `VarHandle` 来实现复杂的访问操作。例如是，可以使用内存段和 base-offset 来实现 variable-length 数组。这类数组的大小无法静态确定，因此无法用序列布局表示。这类内存段可以使用 [MemoryLayout::arrayElementVarHandle](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/MemoryLayout.html#arrayElementVarHandle(java.lang.foreign.MemoryLayout.PathElement...)) 访问。有关示例可参考 `MemoryLayout` 接口的 [Working with variable-length arrays](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/MemoryLayout.html#variable-length) 文档。 

## 7. 文件中内存区域的内存段

文件中的内存区域也可以支持内存段。为此，可以使用 `FileChannel::map` 方法将 file-channel 的某个区域映射到内存段。

以下示例与[内存布局与结构化访问](#内存布局和结构化访问)中类似。它创建一个内存段，然后用 10 个 `Point` 结构的本机数组填充。不同之处在于，该内存段映射到 file-channel 中的一个区域。该示例创建并填充一个名为 `point-array.data` 的文件。然后使用一个 read-only file-channel 打开 `point-array.data`，并用 FFM API 检索其内容。

```java
import java.lang.foreign.*;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.invoke.VarHandle;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HexFormat;
import java.util.Set;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static java.nio.channels.FileChannel.MapMode.READ_WRITE;
import static java.nio.file.StandardOpenOption.*;

public class MemoryMappingExample {

    static final SequenceLayout ptsLayout
            = MemoryLayout.sequenceLayout(10,
            MemoryLayout.structLayout(
                    ValueLayout.JAVA_INT.withName("x"),
                    ValueLayout.JAVA_INT.withName("y")));

    static final VarHandle xHandle
            = ptsLayout.varHandle(
            PathElement.sequenceElement(),
            PathElement.groupElement("x"));

    static final VarHandle yHandle
            = ptsLayout.varHandle(
            PathElement.sequenceElement(),
            PathElement.groupElement("y"));

    static void main(String[] args) {
        MemoryMappingExample myApp = new MemoryMappingExample();
        try {
            Files.deleteIfExists(Paths.get("point-array.data"));
            myApp.createFile();
            myApp.readFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void createFile() throws Exception {
        try (var fc = FileChannel.open(Path.of("point-array.data"),
                Set.of(CREATE, READ, WRITE));
             Arena arena = Arena.ofConfined()) {

            MemorySegment mapped = fc.map(READ_WRITE, 0L, ptsLayout.byteSize(), arena);

            System.out.println("Empty mapped segment:");
            System.out.println(toHex(mapped));

            for (int i = 0; i < ptsLayout.elementCount(); i++) {
                int xValue = i;
                int yValue = i * 10;
                xHandle.set(mapped, 0L, (long) i, xValue);
                yHandle.set(mapped, 0L, (long) i, yValue);
            }

            System.out.println("Populated mapped segment:");
            System.out.println(toString(mapped));
            System.out.println("Populated mapped segment in hex:");
            System.out.println(toHex(mapped));
        }
    }


    void readFile() throws Exception {
        try (var fc = FileChannel.open(Path.of("point-array.data"),
                Set.of(SPARSE, READ));
             Arena arena = Arena.ofConfined()) {

            MemorySegment mapped = fc.map(READ_ONLY, 0L, ptsLayout.byteSize(), arena);

            System.out.println("Contents of point-array.data:");
            System.out.println(toString(mapped));
        }
    }

    static String toString(MemorySegment seg) {
        String outputString = "";
        for (int i = 0; i < ptsLayout.elementCount(); i++) {
            int xVal = (int) xHandle.get(seg, 0L, (long) i);
            int yVal = (int) yHandle.get(seg, 0L, (long) i);
            outputString += "(" + xVal + ", " + yVal + ")";
            if ((i + 1 != ptsLayout.elementCount())) outputString += "\n";
        }
        return outputString;
    }

    static String toHex(MemorySegment seg) {
        String outputString = "";
        HexFormat formatter = HexFormat.of();

        byte[] byteArray = seg.toArray(java.lang.foreign.ValueLayout.JAVA_BYTE);

        for (int i = 0; i < byteArray.length; i++) {
            outputString += formatter.toHexDigits(byteArray[i]) + " ";
            if ((i + 1) % 8 == 0 && (i + 1) % 16 != 0) {
                outputString += " ";
            }
            if ((i + 1) % 16 == 0 && (i + 1) < byteArray.length) {
                outputString += "\n";
            }
        }
        return outputString;
    }
}
```

输出：

```
Empty mapped segment:
00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00 
00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00 
00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00 
00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00 
00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00 
Populated mapped segment:
(0, 0)
(1, 10)
(2, 20)
(3, 30)
(4, 40)
(5, 50)
(6, 60)
(7, 70)
(8, 80)
(9, 90)
Populated mapped segment in hex:
00 00 00 00 00 00 00 00  01 00 00 00 0a 00 00 00 
02 00 00 00 14 00 00 00  03 00 00 00 1e 00 00 00 
04 00 00 00 28 00 00 00  05 00 00 00 32 00 00 00 
06 00 00 00 3c 00 00 00  07 00 00 00 46 00 00 00 
08 00 00 00 50 00 00 00  09 00 00 00 5a 00 00 00 
Contents of point-array.data:
(0, 0)
(1, 10)
(2, 20)
(3, 30)
(4, 40)
(5, 50)
(6, 60)
(7, 70)
(8, 80)
(9, 90)
```

可以使用另一个应用程序验证 `point-array.dat` 的内存，例如 UNIX 命令行工具 `hexdump`：

```sh
$ hexdump -C point-array.data
00000000  00 00 00 00 00 00 00 00  01 00 00 00 0a 00 00 00  |................|
00000010  02 00 00 00 14 00 00 00  03 00 00 00 1e 00 00 00  |................|
00000020  04 00 00 00 28 00 00 00  05 00 00 00 32 00 00 00  |....(.......2...|
00000030  06 00 00 00 3c 00 00 00  07 00 00 00 46 00 00 00  |....<.......F...|
00000040  08 00 00 00 50 00 00 00  09 00 00 00 5a 00 00 00  |....P.......Z...|
00000050
```

以下语句创建一个可读写的 `FileChannel`：

```java
try (var fc = FileChannel.open(Path.of("point-array.data"),
              Set.of(CREATE, READ, WRITE));
     Arena arena = Arena.ofConfined()) {
```

该代码指定以下 [StandardOpenOption](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/nio/file/StandardOpenOption.html) 选项：

- `CREATE` : 如果文件不存在，创建新文件
- `READ`: 打开文件进行读
- `WRITE`: 打开文件进行写

以下代码创建一个新的内存段，并将其映射到 file-channel 的一个区域：

```java
MemorySegment mapped = fc.map(READ_WRITE, 0L, ptsLayout.byteSize(), arena);
```

该语句调用 [FileChannel.map(FileChannel.MapMode, long, long, Arena)](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/nio/channels/FileChannel.html#map(java.nio.channels.FileChannel.MapMode,long,long,java.lang.foreign.Arena)) 并创建具有以下特征的内存段：

- 可以读写 `point-array.data` 文件
- 从文件开头开始
- 与文件大小相同
- 其生命周期由之前声明的 confined-arena 控制

该示例采用与[内存布局和结构化访问](#内存布局和结构化访问) 相同的方式填充映射的内存段。

为了读取 `point-array.data` 的内容，该示例创建了一个 read-only file-channel：

```java
var fc = FileChannel.open(Path.of("point-array.data"),
         Set.of(READ)
```

然后创建一个 映射到 `point-array.data` 的 read-only 内存段：

```java
MemorySegment mapped = fc.map(READ_ONLY, 0L, ptsLayout.byteSize(), arena);
```

## 8. 使用 errno 检查 native 错误

有些 C 标准库函数通过设置 `errno` 宏来表示错误。可以使用 FFM API  linker 选项访问该值。

[Linker::downcallHandle](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/Linker.html#downcallHandle(java.lang.foreign.MemorySegment,java.lang.foreign.FunctionDescriptor,java.lang.foreign.Linker.Option...)) 方法包含一个 varargs 参数，可用于指定额外的 linker 选项。这些参数类型为 [`Linker.Option`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/Linker.Option.html).

其中一个 linker 选项是 `Linker.Option.captureCallState(String...)`，在通过下调方法句柄关联的外部函数后，可用于立即保存部分执行状态。可以用它来捕获某些线程本地变量。当与 “errno” 字符串一起使用时，它可以捕获 C 标准库定义的 `errno`值。

## 9. Allocator 和内存段切片

### 内存段切片

## 10. 受限方法

## 11. jextract

`jextract` 工具可以从 native 库的头文件自动生成 Java 绑定。该工具生成的绑定依赖于FFM API。使用此工具，您无需为要调用的函数手动创建 downcall 和 upcall 句柄；`jextract` 工具会自动生成相应的代码。

请从以下站点获取该工具：

https://jdk.java.net/jextract/

请从以下站点获取 jextract 的源代码：

https://github.com/openjdk/jextract

该站点还包含有关如何编译和运行 jextract 的步骤、附加文档和示例。

### 在 Java 程序中运行 Python 脚本

以下步骤展示了如何从 Python 头文件（`Python.h`）生成 Java 绑定，然后使用生成的代码在 Java 应用程序中运行 Python 脚本。该 Python 脚本会打印一个 Java 字符串的长度。

1. 运行以下命令为 `Python.h` 生成 Java 绑定：

```bash
jextract -l :<absolute path of Python shared library> \
  --output <directory containing code generated by jextract> \
  -I <directory containing Python header files> \
  -t org.python <absolute path of Python.h>
```

例如：

```bash
jextract -l :/lib64/libpython3.6m.so.1.0 \
  --output gensrc \
  -I /usr/include/python3.6m \
  -t org.python /usr/include/python3.6m/Python.h
```

在 Linux 上，要获取 Python 共享库的路径，可以运行以下命令：

```bash
ldconfig -p | grep libpython
```

运行此命令会打印出类似于以下的输出：

```text
        libpython3.6m.so.1.0 (libc6,x86-64) => /lib64/libpython3.6m.so.1.0
        libpython3.6m.so (libc6,x86-64) => /lib64/libpython3.6m.so
        libpython3.so (libc6,x86-64) => /lib64/libpython3.so
        libpython2.7.so.1.0 (libc6,x86-64) => /lib64/libpython2.7.so.1.0
        libpython2.7.so (libc6,x86-64) => /lib64/libpython2.7.so
```

`-l` 选项的值是生成的头类应当加载的共享库的路径或名称。如果它以冒号（`:`）开头，则该值会被解释为库路径。否则，它就是一个库名称，例如 `libGL.so` 对应的 `GL`。

`jextract` 工具可以解析动态链接器已知的任何库指定符。因此，您也可以按如下方式运行此命令：

```bash
jextract -l :libpython3.6m.so.1.0 \
  --output gensrc \
  -I /usr/include/python3.6m \
  -t org.python /usr/include/python3.6m/Python.h
```

在 Linux 系统上，如果找不到 `Python.h` 或包含 Python 头文件的目录，您可能需要安装 `python-devel` 包。

2. 在与 `classes` 相同的目录中（该目录应包含 Python 的 Java 绑定），创建以下文件 `PythonMain.java`：

```java
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import static java.lang.foreign.MemorySegment.NULL;
import static org.python.Python_h.*;

public class PythonMain {
    
    public static void main(String[] args) {
        String myString = "Hello world!";
        String script = """
                     string = "%s"
                     print(string, ': ', len(string), sep='')
                     """.formatted(myString).stripIndent();
        Py_Initialize();
        
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment nativeString = arena.allocateFrom(script);
            PyRun_SimpleStringFlags(
                nativeString,
                NULL);
            Py_Finalize();
        }
        Py_Exit(0);
    }
}
```

使用以下命令编译 `PythonMain.java`：

```bash
javac -sourcepath gensrc PythonMain.java
```

使用以下命令运行 `PythonMain`：

```bash
java -cp gensrc:. --enable-native-access=ALL-UNNAMED PythonMain
```

### 从 Java 程序调用 qsort 函数

如前所述，`qsort` 是一个 C 库函数，它需要一个指向比较两个元素的函数的指针。以下步骤将使用 `jextract` 为 C 标准库创建 Java 绑定，为 `qsort` 所需的比较函数创建一个上行调用（upcall）句柄，然后调用 `qsort` 函数。

运行以下命令为 `stdlib.h`（C 标准库的头文件）创建 Java 绑定：

```bash
jextract --output <包含 jextract 生成代码的目录> \
    -t org.unix <stdlib.h 的绝对路径>
```

例如：

```bash
jextract --output gensrc -t org.unix /usr/include/stdlib.h
```

为 `stdlib.h` 生成的 Java 绑定包含一个名为 `stdlib_h` 的 Java 类，该类中包含一个名为 `qsort(MemorySegment, long, long, MemorySegment)` 的 Java 方法，以及一个名为 `__compar_fn_t` 的 Java 接口，该接口包含一个名为 `allocate` 的方法，用于为 `qsort` 函数所需的比较函数创建函数指针。

在生成 `stdlib.h` Java 绑定的同一目录中，创建以下 Java 源文件 `QsortMain.java`：

```java
import static org.unix.stdlib_h.*;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public class QsortMain {
    
    public static void main(String[] args) {
        
        int[] unsortedArray = new int[] { 0, 9, 3, 4, 6, 5, 1, 8, 2, 7 };

        try (Arena a = Arena.ofConfined()) {
            
            // 分配堆外内存并将 unsortedArray 存储在其中
            //
            // stdlib_h.C_INT 是由 jextract 生成的常量
            
            MemorySegment array = a.allocateFrom(C_INT, unsortedArray);            

            // 为比较函数创建上行调用（upcall）存根
            //
            // MemorySegment org.unix.__compar_fn_t.allocate(__compar_fn_t, Arena)
            // 来自由 jextract 生成的 __compar_fn_t.java
            
            MemorySegment comparFunc = org.unix.__compar_fn_t.allocate(
                (addr1, addr2) ->
                    Integer.compare(
                        addr1.get(C_INT, 0),
                        addr2.get(C_INT, 0)),
                    a);
           
            // 调用 qsort
            qsort(array, (long) unsortedArray.length, C_INT.byteSize(), comparFunc);      

            // 将堆外内存复制到新的 int[] 数组中
            int[] sortedArray = array.toArray(C_INT);

            for (int num : sortedArray) {
                System.out.print(num + " ");
            }
            System.out.println();        
        } 
    }
}
```

以下语句通过 Lambda 表达式创建了一个上行调用（upcall）`comparFunc`：

```java
            // 为比较函数创建上行调用
            //
            // MemorySegment org.unix.__compar_fn_t.allocate(__compar_fn_t, SegmentScope)
            // 来自由 jextract 生成的 __compar_fn-t.java
            
            MemorySegment comparFunc = org.unix.__compar_fn_t.allocate(
                (addr1, addr2) ->
                    Integer.compare(
                        addr1.get(C_INT, 0),
                        addr2.get(C_INT, 0)),
                    a);
```

因此，您无需像在“上行调用：将 Java 代码作为函数指针传递给外部函数”中描述的那样，手动为比较函数创建方法句柄（MethodHandle）。

使用以下命令编译 `QsortMain.java`：

```bash
javac -sourcepath gensrc QsortMain.java
```

使用以下命令运行 `QsortMain`：

```bash
java -cp gensrc:. --enable-native-access=ALL-UNNAMED QsortMain
```

## 参考

- https://docs.oracle.com/en/java/javase/25/core/foreign-function-and-memory-api.html