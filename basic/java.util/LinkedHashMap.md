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

`LinkedHashMap` 还可以使用**访问顺序**（不是插入顺序）来遍历元素。每次调用 get 或 put 时，受影响的元素从当前位置移除，并放置到元素链表的末尾（只影响元素在链表中的位置，不影响在哈希  bucket 中的位置）