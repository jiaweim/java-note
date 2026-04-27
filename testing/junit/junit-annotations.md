# Junit 注释

Junit 支持以下注释，用于测试的设置与扩展。

所有的注释在 `junit-jupiter-api` 的 `org.junit.jupiter.api` package 中。

**@Test**

将方法标记为测试方法。与 Junit 4 的 `@Test` 注释不同，该注释没有任何属性，Junit Jupiter 中的测试扩展有专用注释。

**@ParameterizedTest**

**@TempDir**

在测试类构造函数、生命周期方法和测试方法中通过字段注入或参数注入提供临时目录。位于 `org.junit.jupiter.api.io` package 中。

## 参考

- https://docs.junit.org/current/writing-tests/annotations