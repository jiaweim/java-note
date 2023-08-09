# 并发集合

数据结构是编程的基本 元素，几乎每个程序都需要使用数据结构来存储和管理数据。Java API 提供了 Java 集合框架，它实现了许多数据结构的接口、类和算法。

在并发程序中使用集合必须非常小心。大多数集合类不支持并发应用，因为它们不能控制对其数据的并发访问，如 `ArrayList`。

Java 提供了并发专用集合类。基本上可以分为两种类型：

- **阻塞集合**（blocking collection）：这类集合包含添加和删除元素操作。如当集合满了，此时调用线程添加元素操作将被阻塞，直到集合腾出位置可以存放元素
- **非阻塞结合**（non-blocking collection）：这类集合也包含添加和删除元素操作。但是，如果操作无法立即完成，它会直接返回 null 或抛出异常，调用线程不会阻塞

本章介绍这些并发集合，包括：

- non-blocking 双向队列（deque）：ConcurrentLinkedDeque
- blocking deque: LinkedBlockingDeque
- 用于 producer-consumer 模式的 blocking queue: LinkedTransferQueue
- 实现优先级的 blocking queue: PriorityBlockingQueue
- 
