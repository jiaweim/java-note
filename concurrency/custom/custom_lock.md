# 自定义 Lock

## 简介

Lock 提供了两个操作：

- lock(): 访问临界区时调用。当有其它线程占用临界区，线程挂起
- unlock(): 访问临界区后调用
