# LinkedHashMap

## 简介

LinkedHashSet 和 `LinkedHashMap` 类会记住元素插入顺序。例如：

```java
var staff = new LinkedHashMap<String, Integer>();
staff.put("144-25-5465", 1);
staff.put("567-24-2546", 2);
staff.put("157-62-7935", 3);
staff.put("456-62-5527", 4);
```

迭代 key 或 value，与插入顺序一致：

```java
for (String key : staff.keySet()) {
    System.out.println(key);
}

for (Integer value : staff.values()) {
    System.out.println(value);
}
```

`LinkedHashMap` 还可以使用**访问顺序**（不是插入顺序）来遍历元素。每次调用 get 或 put 时，受影响的元素从当前位置移除，并放置到元素链表的末尾（只影响元素在链表中的位置，不影响在哈希  bucket 中的位置），构造方法：

```java
public LinkedHashMap(int initialCapacity,
                     float loadFactor,
                     boolean accessOrder)
```

`accessOrder` 对于实现**最近最少使用**的缓存很有用。例如，当散列表满了，可以迭代散列表，删除枚举的前几个元素，这些元素最近使用最少。

甚至可以继承 `LinkedHashMap` 使该过程自动化，只需覆盖下面方法：

```java
protected boolean removeEldestEntry(Map.Entry<K,V> eldest)
```

当该方法返回 `true` 时，添加新元素会导致删除最老的元素。例如，以下最多保存 100 个元素：

```java
var cache = new LinkedHashMap<K, V>(128, 0.75F, true) {
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > 100;
    }
};
```

或者，可以检查最老的元素来决定是否删除它。例如，检查该元素的时间戳。
