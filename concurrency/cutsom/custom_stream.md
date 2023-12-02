# 自定义 Stream 生成器

## 简介

流支持以串行或并行的方式对数据序列执行一系列操作。

其中 `Spliterator` 接口定义遍历和拆分数据所需的方法。然后用 `StreamSupport` 中的 `stream()` 方法生成 Stream 来处理 `Spliterator` 的元素。

很少需要自定义 `Spliterator`，除非你需要对自定义数据结构创建 Stream。

