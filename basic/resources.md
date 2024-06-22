# 资源访问

## 概述

资源（resource）是程序需要以**独立于代码位置**访问的数据（图像、音频、文本等）。

`Class` 和 `ClassLoader` 类提供了与位置无关的方法来访问资源。

resource 由一个字符串标识，该字符串由一系列子字符串组成，由斜杠 `/` 分隔，后面为 resource-name。每个子字符串必须是有效的 Java 标识符。resource-name 的形式为 `shortName` 或 `shortName.extension`，其中 `shortName` 和 `extension` 都必须是 Java 标识符。

## java.lang.Class

`Class.getResource()` 返回 resource 的 URL。URL 与实现和 JVM 有关（即在一个 runtime 中获得的 URL 在另一个 runtime 中可能无效）。它的协议通常与加载 resource 的 `ClassLoader` 直接相关。如果 resource 不存在、或由于安全考虑不可见，该方法返回 `null`。

```java
public URL getResource(String name)
```

找到给定名称的资源。

如果该类位于命名 module 中，则该方法尝试在 module 中查找资源，委托给 module 的 `ClassLoader.findResource(String,String)`  实现，以 module 名和资源绝对名称为参数。命名 module 中的资源受 `Module.getResourceAsStream`  方法指定的封装规则约束，因此，当资源是包中 non-".class" 资源，且该资源没有对调用 module open 时，返回 null。

如果该类不在命名 module 中，则搜索资源的规则由该类的 `ClassLoader` 实现。如果该对象由 bootstrap class loader 加载，则委托给 `ClassLoader.getSystemResource`。

### 路径规则

在委托之前，使用下面的算法将**相对路径**转换为**绝对路径**：

- 如果 resource-name 以 '/' 开头，则绝对路径是名称中 '/' 后面的部分；
- 如果 resource-name 不以 `/` 开头，则认为该路径是相对类 package 的相对路径，形式如下：

`modified_package_name/name`

其中 `modified_package_name` 是该类的 package 名称，将 '.' 替换为 '/'。

假设我们在 `org/test/resource` 目录定义了资源文件 `example.txt`。此外，在 `org/text/resource` package 中定义了 `GetResourceExample` 类。

此时可以按照如下**绝对路径**查询资源：

```java
URL resourceAbsolutePath = GetResourceExample.class
    .getResource("/com/test/resource/example.txt");
assertNotNull(resourceAbsolutePath);
```

由于 resource 和类在相同 package 中，所以可以使用如下的**相对路径**：

```java
URL resourceRelativePath = GetResourceExample.class.getResource("example.txt");
assertNotNull(resourceRelativePath);
```

> 只有在 resource 和类在相同 package 中，才能使用相对路径。

## ClassLoader

`ClassLoader` 用来加载类的类。每个 `Class` 实例包含对其 `ClassLoader` 的 引用。

`ClassLoader` 使用代理模型搜索类和资源。此外，每个 `ClassLoader` 实例都有一个 parent `ClassLoader`。

`ClassLoader` 在被要求查找资源时，首先委托其 parent `ClassLoader`，然后再自己尝试查找资源。

如果 parent `ClassLoader` 不存在，则使用 JVM 内置的 `ClassLoader`，称为 bootstrap `ClassLoader`。bootstrap `ClassLoader` 没有 parent，可以将其看作 root ClassLoader。

`ClassLoader` 的资源名总是以**绝对路径**处理。以上例为例：

```java
URL resourceAbsolutePath = GetResourceExample.class.getClassLoader()
    .getResource("com/test/resource/example.txt");
assertNotNull(resourceAbsolutePath);
```

在调用 `ClassLoader.getResource()` 时，要去掉绝对路径前面的斜杠 `/`。

使用 `ClassLoader`，不能使用相对路径：

```java
URL resourceRelativePath = GetResourceExample.class.getClassLoader()
    .getResource("example.txt");
assertNull(resourceRelativePath);
```

## 总结

类加载器的两种方式

```java
A.class.getResource("/") == A.class.getClassLoader().getResource("")
```

`ClassLoader` 总是以绝对路径处理输入的字符串。在输入时要省略开头的 `/`。

假设文件为 resources 目录下的 message.properties，获取路径方式：

- class.getResource

```java
URL resource = A.class.getResource("/message.properties");
```

path 不以 '/' 开头时，默认为此类所在的 package 获取资源

path 以 '/' 开头时，从 classPath 根目录下获取资源：**对应 IDEA 的 target 目录**，Eclipse 的 bin 目录。

- class.getClassLoader().getResource

```java
URL resource = A.class.getClassLoader().getResource("message.properties");
```

- getResourceAsStream 也是如此

```java
InputStream i=A.class.getResourceAsStream("/message.properties")
InputStream is = A.class.getClassLoader().getResourceAsStream("message.properties")
```

```ad-summary
`class.getResource` 和 `ClassLoader.getResource` 本质上一样，最终都是调用 `ClassLoader.getResource` 加载资源。

`class.getResource` 在调用 `ClassLoader.getResource` 之前，会先获取文件路径，如果 path 以 '/' 开头，就从根目录获取，否则从当前类所在包下获取。
```

## 参考

- https://docs.oracle.com/javase/8/docs/technotes/guides/lang/resources.html