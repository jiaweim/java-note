# Stream

- [Stream 基础](stream_basic.md)
- [创建 Stream](create_stream.md)
- [Stream 操作](stream_ops.md)
- [Optional](optional.md)
- [终结操作](terminal_ops.md)
- [收集为 Map](toMap.md)
- [group](group.md)
- [reduce](reduce.md)
- [parallel](parallel.md)

## 操作总结

|终结操作|功能|
|---|---|
|`.collect(Collectors.toMap())`|转换为 Map|

## 使用建议

**使用**

- 简单逻辑，使用迭代器
- 多步处理，使用 Stream
- 单核 CPU，使用串行 Stream
- 多核 CPU，使用并行 Stream


**性能**

- 少量数据（<1000）

串行 Stream 效率不如迭代器，但这类业务本身运行时间很少，效率差异对业务基本不影响，而 Stream 代码更简洁。

- 大量数据（>10,000）

穿行 Stream 效率高于迭代器。

- 并行 Stream

性能取决于 CPU 内核数。
