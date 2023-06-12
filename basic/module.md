# Java 模块系统

## 简介

面向对象编程的一个重要特征是封装。例如，类的声明由 public 接口和 private 实现组成，在不影响用户的情况下，可以通过修改 private 实现不断改进。module 系统为大规模编程提供了类似的功能。module 可以控制类和包的可访问性。

## module 概念

在面向对象编程中，类提供了初级封装功能。而在 java 中，package 提供一个更大的分组。package 是类的集合。package 也提供了一定程度的封装。protected 访问权限的特性只能在 package 内部访问。

然而，在大型系统中，这种级别的访问控制还不够。任何 public 特性（可以在 package 外访问）都可以在任何地方访问。假设你想修改或删除一个很少使用的特性，如果它是 public，就很难估计这种变化的影响。 

这就是 Java 平台设计者所面临的问题。在过去 20 年，JDK 突飞猛进得发展，有些特性明显已经过时，如 CORBA，然后直到 Java 11 `org.omg.corba` 包才被移除。从 Java 11 开始，如果你需要，则需要添加对应 JAR 文件。再比如 `java.awt`，在服务器端基本不会用到它，除了在 SOAP 中会用到 `java.awt.DataFlavor` 类。

基于这些原因，Java 平台提出了 Java 平台模块系统，它现在是 Java 语言和虚拟机的一部分。

一个 Java 模块包含：

- package 集合
- 资源文件和其它文件，如 native 库（可选）
- 模块中可访问的 package 列表
- 该模块所依赖的其它模块的列表

Java 在编译时和虚拟机中都强制封装和依赖项。

## 模块命名

模块是 package 的集合。模块中的 package 名称不需要相关。例如，`java.sql` 模块包含  `java.sql,` `javax.sql` 和 `javax.transaction.xa` 包。此外，报名与模块名相同也没问题。

和路径名一样，模块名由字母、数字、下划线和点号组成。另外，模块之间没有层级关系。假设有两个模块 `test.hero` 和 `test.hero.hello`，就模块系统而言，它们不相关。



| 命令                            | 缩写命令 | 功能         |
| ------------------------------- | -------- | ------------ |
| `--module-path`                 | `-p`     | 指定模块路径 |
| `--module modulename/classname` | `-m`     | 指定主类     |

# Require
`java.lang` 包放在 `java.base` module 中，默认 required.

# Export

# open
`open` 关键字可以让 runtime 访问对应包中所有的类型和成员，也可以通过 reflection 访问私有成员，例如：
```java
module test{
    opens cn.ac.hello;
}
```
这样，那些依赖于 reflection 的包，如JAXB就可以正常工作。

还可以之间将module声明为open:
```java
open module test{
    requires java.xml.bind;
}
```
这样 `exports` 的所有包都是 open 的。

# service
service 在 java 9 之前通过在 `META-INF/services` 目录添加包含实现类的文本文件实现，module 系统提供了一个更好的方式，不需要文本文件，直接在 module 中添加声明。

模块通过 `provides` 语句列出service接口（可以在任意模块中定义）的实现（必须在该模块中指定）。下面是 `jdk.security.auth` 模块的例子：
```java
module jdk.security.auth {
    ...
    provides javax.security.auth.spi.LoginModule with
        com.sun.security.auth.module.Krb5LoginModule,
        com.sun.security.auth.module.UnixLoginModule,
        com.sun.security.auth.module.JndiLoginModule,
        com.sun.security.auth.module.KeyStoreLoginModule,
        com.sun.security.auth.module.LdapLoginModule,
        com.sun.security.auth.module.NTLoginModule;
}
```
和使用 `META-INF/services` 文件是等价的。

使用服务的模块通过 `uses` 语句：
```java
module java.base {
    ...
    uses javax.security.auth.spi.LoginModule;
}
```
