# Java 并发

- [并发的基本概念](concurrency_design/1_concurrency_concepts.md)
	- [临界区](critical_section.md)
	- [并发可能出现的问题](2_concurrency_problem.md)
	- [并发算法设计](concurrency_design/3_concurrent_methodology.md)
	- [Java 并发 API 概览](concurrency_design/4_concurrency_java_api.md)
	- [并发设计模式](concurrency_design/5_concurrency_design_pattern.md)
	- [设计并发算法的技巧](concurrency_design/6_design_concurrent_tips.md)
- [线程基础](thread1_create.md)
	- [线程的生命周期](thread2_state.md)
	- [中断线程](thread3_interrupt.md)
	- [响应中断请求](thread4_interrupt_advanced.md)
	- [暂停线程](thread5_sleep.md)
	- [等待线程结束](thread6_finish.md)
	- [守护线程](thread7_daemon.md)
	- [线程异常处理](thread8_exception.md)
	- [线程局部变量](thread9_local_variable.md)
	- [线程分组](thread10_group.md)
	- [用工厂设计模式创建线程](thread11_factory.md)
- [synchronized - 方法同步](sync/sync1_method.md)
	- [条件同步](sync/sync2_condition.md)
	- [基于 Lock 的同步](sync/sync3_lock.md)
	- [读写锁](sync/sync4_read_write_lock.md)
	- [多条件锁](syncs/sync5_multiple_condition_lock.md)
	- [StampedLock](sync/sync6_stampedlock.md)

  - [JDK 并发 API 的演变](evolution.md)
  - [多线程特点](thread_characteristic.md)
  - [线程方法](5_thread_methods.md)
- [多线程问题](6_sync.md)
- [同步](7_sync_method.md)
- [线程池](6_thread_pool.md)
- [Process](process.md)

![](images/2019-10-24-19-25-27.png)

## 参考

- [The Java Tutorials - Concurrency](https://docs.oracle.com/javase/tutorial/essential/concurrency/index.html)
- [Baeldung](https://www.baeldung.com/java-concurrency)
- Java 并发编程实战
- Java 并发编程：设计原则与模式
- [并发编程网](http://ifeve.com/)
- [jenkov.com](http://tutorials.jenkov.com/java-util-concurrent/index.html)
- https://howtodoinjava.com/java-concurrency-tutorial/
- https://www.codejava.net/java-core/concurrency
- Java 高并发线程详解
- [https://segmentfault.com/blog/df007df](https://segmentfault.com/blog/df007df)
