# Class

## getResource

```java
public URL getResource(String name)
```

找到给定名称的资源。

如果该类位于命名 module 中，则该方法尝试在 module 中查找资源，委托给 module 的 `ClassLoader.findResource(String,String)`  实现，以 module 名和资源绝对名称为参数。命名 module 中的资源受 `Module.getResourceAsStream`  方法指定的封装规则约束，因此，当资源是包中 non-".class" 资源，且该资源没有对调用 module open 时，返回 null。

如果该类不在命名 module 中，则搜索资源的规则由该类的 `ClassLoader` 实现。如果该对象由 bootstrap class loader 加载，则委托给 `ClassLoader.getSystemResource`。

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

- 类加载器的两种方式

```java
A.class.getResource("/") == A.class.getClassLoader().getResource("")
```

假设文件为 resources 目录下的 message.properties，获取路径方式：

- `class.getResource`

```java
URL resource = A.class.getResource("/message.properties");
```

path 不以 '/' 开头时，默认为此类所在的 package 获取资源

path 以 '/' 开头时，从 classPath 根目录下获取资源：**对应 IDEA 的 target 目录**，Eclipse 的 bin 目录。

- `class.getClassLoader().getResource`

```java
URL resource = A.class.getClassLoader().getResource("message.properties");
```

- `getResourceAsStream` 也是如此

```java
InputStream i=A.class.getResourceAsStream("/message.properties")
InputStream is = A.class.getClassLoader().getResourceAsStream("message.properties")
```

> [!NOTE]
>
> `class.getResource` 和 `ClassLoader.getResource` 本质上一样，最终都是调用 `ClassLoader.getResource` 加载资源。
>
> `class.getResource` 在调用 `ClassLoader.getResource` 之前，会先获取文件路径，如果 path 以 '/' 开头，就从根目录获取，否则从当前类所在包下获取。

