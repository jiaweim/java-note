# 单元测试工具

2026-08-03⭐
@author Jiawei Mao
***
Eclipse Collections 提供了一个名为 `eclipse-collections-testutils` 的 jar 文件，用于辅助编写单元测试。依赖项：

```xml
<dependency>
    <groupId>org.eclipse.collections</groupId>
    <artifactId>eclipse-collections-testutils</artifactId>
    <scope>test</scope>
    <version>${eclipse.collections.version}</version>
</dependency>
```

其中核心类为 `Verify`，该类继承 Junit 的 `Assert`。`Verify` 提供大量面向集合的专用断言，例如，下面断言两个 list 是否相等。

```java
MutableList<Integer> list = Lists.mutable.with(1, 2, 3);
Verify.assertListsEqual(Lists.mutable.with(1, 2, 3), list);
Verify.assertSize(2, customersFromLondon);
//instead of

Assert.assertEquals(2, customersFromLondon.size());
Verify.assertEmpty(Lists.mutable.empty());
Verify.assertNotEmpty(Lists.mutable.with(1));
Verify.assertContains(1, Lists.mutable.with(1));
```

