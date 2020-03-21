# TOC
- [TOC](#toc)
- [final](#final)
  - [inner class](#inner-class)
- [volatile](#volatile)
- [native](#native)

# final
final 可以修饰非抽象类、非抽象成员变量和方法。
final 方法不能被覆盖。

## inner class
Variable is accessed from within inner class, needs to be final or effectively final.

局部变量只能在声明该变量的方法中有效，离开方法就无效了。而内部类在离开方法后依然有效，所以在内部类中引用局部变量会出现该错误。

解决方法1：使用 `IntStream` 生成索引
```java
IntStream.range(0, params.size())
  .forEach(idx ->
    query.bind(
      idx,
      params.get(idx)
    )
  )
;
```

# volatile
在编程语言中，特别是C，C++，C# 以及 Java中，`volatile`关键字声明的变量一般和优化和多线程相关。

简而言之，`volatile` 关键字会阻止编译器对其优化。

在 Java 中，volatile 关键字保证：
- 变量的值在使用之前总是从主内存中再次读取出来；
- 对变量值的修改总会在完成后写回主内存。
- (所有Java版本) 对 `volatile` 变量的读写全局排序。即每个线程访问 `volatile` 变量时都会读取当前值，而不使用缓存值。不过读写的顺序没有保证，所以并不适合构造多线程应用。
- （Java 5之后）`volatile`确定了读写的先后顺序，类似于互斥锁。

如果一个基本变量被 `volatile` 修饰，编译器不将它保存到寄存器中，而是每一次都去访问内存中实际保存该变量的位置。这一点就避免了没有 `volatile` 修饰的变量在多线程的读写中所产生的由于编译器优化所导致的灾难性问题。所以多线程中必须要共享的基本变量一定要加上 `volatile` 修饰符。当然了，`volatile` 还能让你在编译时期捕捉到非线程安全的代码。

使用 `volatile` 可能比锁快，不过某些场合可能无法使用。

# native
`native` 关键字说明其修饰的方法是一个原生方法，该方法的具体实现不在当前文件中，而是以其它语言（如C++）实现。比如 Java 语言本身不能对操作系统底层进行访问和控制，但是可以通过 JNI 接口调用其它语言来实现对底层的访问。

JNI 是 Java 本机接口（Java Native Interface）的缩写，允许 Java 代码使用其它语言编写的代码库。

