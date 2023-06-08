# Class

## getResource

```java
public URL getResource(String name)
```

找到给定名称的资源。

如果该类位于命名 module 中，则该方法尝试在 module 中查找资源，委托给 module 的 `ClassLoader.findResource(String,String)`  实现，以 module 名和资源绝对名称为参数。命名 module 中的资源受 `Module.getResourceAsStream`  方法指定的封装规则约束，因此，当资源是包中 non-".class" 资源，且该资源没有对调用 module open 时，返回 null。

如果该类不在命名 module 中，则搜索资源的规则由该类的 ClassLoader 实现。如果该对象由 bootstrap class loader 加载，则委托给 `ClassLoader.getSystemResource`。

在委托之前，使用下面的算法将资源名称转换为绝对名称：

- 如果以 '/' 开头，则资源的绝对名称是名称中 '/' 后面的部分；
- 否则，绝对名称为以下形式：

`modified_package_name/name`

其中 `modified_package_name` 是该对象的 package 名称，将 '.' 替换为 '/'。

