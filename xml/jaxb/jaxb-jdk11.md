# JAXB-JDK11

JAXB 相关工具在 JDK 11 被移除。

如果要使用相关工具，可以作为依赖项添加。例如，添加 Maven 依赖项：

```xml
<dependency>
    <groupId>jakarta.xml.bind</groupId>
    <artifactId>jakarta.xml.bind-api</artifactId>
    <version>4.0.2</version>
</dependency>
```

以及 API 实现：

```xml
<dependency>
    <groupId>com.sun.xml.bind</groupId>
    <artifactId>jaxb-impl</artifactId>
    <version>4.0.5</version>
</dependency>
```

