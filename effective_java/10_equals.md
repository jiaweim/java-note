# 10. 重写 equals 时遵守通用约定



## equals vs. ==

- `==` 测试引用是否相当，即是否在内存的相同位置
- `equals()` 测试值是否相等

如果测试两个字符串的值是否相同，可以使用 `Objects.equals()`，`Objects.equals()` 会先测试值是否为 `null`，然后判断值是否相等。

两个 equals 的对象，应该有相同的 hash-code，所以在覆盖 `equals` 时，也要覆盖 `hashCode` 方法。
