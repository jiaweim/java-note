# 缓存

## 简介

缓存在许多应用中非常有用。例如，当计算或检索某个值的成本很高，并且需要多次使用该值，则应该考虑使用缓存。

`Cache` 类似于 `ConcurrentMap`，但不完全相同。最根本的区别在于，`ConcurrentMap` 会一直保留添加到它的所有元素，直到显式删除。而 `Cache` 通常配置为**自动删除元素，以限制其内存占用**。在某些情况下，不会删除元素的 `LoadingCache` 也很有用，因为它会自动加载缓存。

一般来说，Guava 缓存工具适用于：

- 你愿意花费一些内存来提高速度
- 预期 keys 会被多次查询
- 缓存内容不会超过 RAM。Guava 缓存只用于应用单次运行的本地缓存，不会将数据存储到文件或服务器以外。如果有这方面需求，可以考虑 [Memcached](https://memcached.org/)

如果适用于这些条件，那么推荐使用 Guava 缓存。

通过 `CacheBuilder` 构建 `Cache`：

```java
LoadingCache<Key, Graph> graphs = CacheBuilder.newBuilder()
       .maximumSize(1000)
       .expireAfterWrite(10, TimeUnit.MINUTES)
       .removalListener(MY_LISTENER)
       .build(
           new CacheLoader<Key, Graph>() {
             @Override
             public Graph load(Key key) throws AnyException {
               return createExpensiveGraph(key);
             }
           });
```

> [!NOTE]
>
> 如果不需要 `Cache` 的功能，`ConcurrentHashMap`的内存效率更高，但使用 `ConcurrentMap` 很难实现 `Cache` 的功能。

## Population

关于缓存的第一个问题是：是否有默认函数来加载或计算与 key 关联的值？

- 如果有，应该用 `CacheLoader`
- 如果没有，或者需要重写默认值，则需要传入 `Callable` 到 `get` 调用

可以使用 `Cache.put` 直接插入元素，但首选自动加载缓存，这样可以更轻松地推断所有缓存内容的一致性。

### CacheLoader

`LoadingCache` 是一个附加 `CacheLoader` 构建的 `Cache`。创建 `CacheLoader` 与实现 `V load(K key) throws Exception` 一样简单，例如：

```java
LoadingCache<Key, Graph> graphs = CacheBuilder.newBuilder()
       .maximumSize(1000)
       .build(
           new CacheLoader<Key, Graph>() {
             public Graph load(Key key) throws AnyException {
               return createExpensiveGraph(key);
             }
           });

...
try {
  return graphs.get(key);
} catch (ExecutionException e) {
  throw new OtherException(e.getCause());
}
```

查询 `LoadingCache` 的规范方法是 `get(K)`。它将返回一个已经缓存的值，或者使用 cache 的 `CacheLoader` 将新值加载到缓存。由于 `CacheLoader` 可能抛出 `Exception`，因此 `LoadingCache.get(K)` 抛出 `ExecutionException`。如果 cache-loader 抛出一个未捕获异常，那么 `get(K)` 抛出一个包装异常的 `UncheckedExecutionException`。也可以使用 `getUnchecked(K)`，它将所有异常包装在 `UncheckedExecutionException`，但是如果底层 `CacheLoader` 能够正常抛出捕获异常，其行为不确定。

```java
LoadingCache<Key, Graph> graphs = CacheBuilder.newBuilder()
       .expireAfterAccess(10, TimeUnit.MINUTES)
       .build(
           new CacheLoader<Key, Graph>() {
             public Graph load(Key key) { // no checked exception
               return createExpensiveGraph(key);
             }
           });

...
return graphs.getUnchecked(key);
```

可以使用 `getAll(Iterable<? extends K>)` 执行批量查询。`getAll` 默认为缓存中不存在的 key 单独发出 `CacheLoader.load` 调用。如果批量检索比多个单独检索更有效时，可以覆盖 `CacheLoader.loadAll` 来利用这一点。`getAll(Iterable)` 的性能会相应提升。

