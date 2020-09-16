# Semaphore

- [Semaphore](#semaphore)
  - [简介](#简介)
  - [工作原理](#工作原理)
  - [构造](#构造)
  - [Reference](#reference)

2020-09-12, 14:21
@jiawei
***

## 简介

在计算机科学中，信号量（semaphore）是用于控制并发系统（如多任务操作系统）中多个进程对共享资源访问的一种变量或抽象数据结构。即信号量就是一个简单的变量，用于控制某个资源可以被同时访问的个数。比如在 Windows 下可以设置共享文件的最大客户端访问个数。

Java 中的信号量维护了一组许可

- 使用 `acquire()` 申请获得一个许可，如果此时没有可用许可，就一直等待，直到有可用许可（阻塞）。
- 使用  `release()` 释放一个许可。

## 工作原理

可以将信号量看作可以递增或递减的计数器。用一个数字初始化信号量（如 3），这样，该信号量在计数到0前，就可以减少 3 次；归零后，该信号量有可以最多递增 3 次。即信号量的计数值一直在 $0\le n\le 3$ 之间。

除了计数功能，信号量还可以控制线程，当计数器归零，



## 构造

Semaphore 有两个构造函数，参数为许可的个数 `permits` 和是否公平竞争 `fair`。

信号量，设置并发访问的线程数量。

一般要结对使用：
```
try{s.acquire();}finally{s.release()}
```

## Reference

- [wikipedia](https://en.wikipedia.org/wiki/Semaphore_(programming))
- [API](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Semaphore.html)
