# Lock

## 简介

lock 同步机制比 `synchronized` 关键字更强大、更灵活。它基于 java.uti.concurrent.locks 包的 Lock 接口及其实现类 `ReentrantLock`。lock 机制具有以下优点：

- 构建同步代码块的方式更灵活。使用 `synchronized` 关键字只能以结构化的方式控制一个同步代码块，而 Lock 能以更复杂的方式实现临界区。
- 