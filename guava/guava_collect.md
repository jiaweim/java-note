# 集合

## 简介

Guava 对 JDK 的集合生态系统进行了扩充。

## Immutable Collections
2025-10-29⭐
***

immutable 对象有许多优点，包括：

- 使用安全
- 线程安全：可由多个线程使用，没有线程争用的风险
- 不需要支持 mutation 操作，从而节省时间和空间。所有 immutable 集合实现都比其 mutable 实现更节省内存
- 可以作为常量使用

实现对象的 immutable 副本是一种很好的防御式编程技术。Guava 为每种标准集合类型提供了简单易用的 immutable 版本，还包含 Guava 添加的集合类型。

JDK 也提供了 `Collections.unmodifiableXXX` 方法，但是这些方法有些缺点：

- 笨重繁琐，使用不便
- 不安全：只有当无人持有对原始集合的引用时，返回的集合才真正不可变
- 低效：数据结构依然有 mutable 集合的所有开销，包括并发修改检查，hash-table 中的额外空间等

> [!TIP]
>
> 如果不希望修改集合，或者期望集合保持不变，则最好防御性的提供其 immutable 副本。

> [!IMPORTANT]
>
> 每个 Guava immutable 集合实现都不接受 null 值。如果需要使用 null 值，可以考虑使用 `Collections.unmodifiableList`。

### 创建 immutable 集合

可以通过多种方式创建 `ImmutableXXX` 集合：

- 使用 `copyOf` 方法，例如 `ImmutableSet.copyOf(set)`
- 使用 `of` 方法，例如 `ImmutableSet.of("a", "b", "c")` 或 `ImmutableMap.of("a", 1, "b", 2)`
- 使用 `Builder`, 例如

```java
public static final ImmutableSet<Color> GOOGLE_COLORS =
   ImmutableSet.<Color>builder()
       .addAll(WEBSAFE_COLORS)
       .add(new Color(0, 191, 255))
       .build();
```

除了 sorted 集合，在构造时会保留元素顺序。例如：

```java
ImmutableSet.of("a", "b", "c", "a", "d", "b")
```

将按 "a", "b", "c", "d" 的顺序迭代元素。

### copyOf 比你想象中聪明

`ImmutableXXX.copyOf` 会在安全的前提下尝试**避免复制数据**。例如：

```java
ImmutableSet<String> foobar = ImmutableSet.of("foo", "bar", "baz");
thingamajig(foobar);

void thingamajig(Collection<String> collection) {
   ImmutableList<String> defensiveCopy = ImmutableList.copyOf(collection);
   ...
}
```

在该代码中，`ImmutableList.copyOf(foobar)` 会足够智能地直接返回 `foobar.asList()`，这是 `ImmutableSet` 的 constant-time 视图。

作为一般的启发式方法，满足以下条件时 `ImmutableXXX.copyOf(ImmutableCollection)` 会尝试避免线性时间复制：

- 可以在常量时间内使用底层数据结构。例如，`ImmutableSet.copyOf(ImmutableList)` 不能在 constant-time 完成
- 不会导致内存泄漏。例如，如果你有 `ImmutableList<String> hugeList`，然后执行 `ImmutableList.copyOf(hugeList.subList(0, 10))`，则执行显式复制，以避免意外保留 `hugeList` 中不需要的引用
- 不会改变语义。例如，`ImmutableSet.copyOf(myImmutableSortedSet)` 会执行显式复制，因为 `ImmutableSet` 的 `hashCode()` 和 `equals()` 与 `ImmutableSortedSet` 不同

这有助于最大限度地减少防御式编程的性能开销。

### asList

所有 immutable 集合都通过 `asList()` 提供 `ImmutableList` 视图，因此，即使将数据保存为 `ImmutableSortedSet`，依然可以使用 `sortedSet.asList().get(k)` 获得第 `k` 个最小元素。

返回的 `ImmutableList` 通常（并非总是）为 constant 开销视图，而非显式副本。换言之，它通常比常规的 `List` 更高效。

### Immutable 集合总结

| Interface                                                    | JDK or Guava? | Immutable Version                                            |
| ------------------------------------------------------------ | ------------- | ------------------------------------------------------------ |
| `Collection`                                                 | JDK           | [`ImmutableCollection`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/ImmutableCollection.html) |
| `List`                                                       | JDK           | [`ImmutableList`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/ImmutableList.html) |
| `Set`                                                        | JDK           | [`ImmutableSet`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/ImmutableSet.html) |
| `SortedSet`/`NavigableSet`                                   | JDK           | [`ImmutableSortedSet`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/ImmutableSortedSet.html) |
| `Map`                                                        | JDK           | [`ImmutableMap`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/ImmutableMap.html) |
| `SortedMap`                                                  | JDK           | [`ImmutableSortedMap`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/ImmutableSortedMap.html) |
| [`Multiset`](https://github.com/google/guava/wiki/NewCollectionTypesExplained#Multiset) | Guava         | [`ImmutableMultiset`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/ImmutableMultiset.html) |
| `SortedMultiset`                                             | Guava         | [`ImmutableSortedMultiset`](https://guava.dev/releases/12.0/api/docs/com/google/common/collect/ImmutableSortedMultiset.html) |
| [`Multimap`](https://github.com/google/guava/wiki/NewCollectionTypesExplained#Multimap) | Guava         | [`ImmutableMultimap`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/ImmutableMultimap.html) |
| `ListMultimap`                                               | Guava         | [`ImmutableListMultimap`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/ImmutableListMultimap.html) |
| `SetMultimap`                                                | Guava         | [`ImmutableSetMultimap`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/ImmutableSetMultimap.html) |
| [`BiMap`](https://github.com/google/guava/wiki/NewCollectionTypesExplained#BiMap) | Guava         | [`ImmutableBiMap`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/ImmutableBiMap.html) |
| [`ClassToInstanceMap`](https://github.com/google/guava/wiki/NewCollectionTypesExplained#ClassToInstanceMap) | Guava         | [`ImmutableClassToInstanceMap`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/ImmutableClassToInstanceMap.html) |
| [`Table`](https://github.com/google/guava/wiki/NewCollectionTypesExplained#Table) | Guava         | [`ImmutableTable`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/ImmutableTable.html) |

### 示例

```java
public static final ImmutableSet<String> COLOR_NAMES = ImmutableSet.of(
  "red",
  "orange",
  "yellow",
  "green",
  "blue",
  "purple");

class Foo {
  final ImmutableSet<Bar> bars;
  Foo(Set<Bar> bars) {
    this.bars = ImmutableSet.copyOf(bars); // defensive copy!
  }
}
```

## 新集合类型

Guava 引入了许多新集合类型，都是一些 JDK 没有但是比较有用的类型。Guava 集合实现严格遵循 JDK 接口。

### Multiset

计算一个单词在文档中出现的次数，传统的 Java 实现：

```java
Map<String, Integer> counts = new HashMap<String, Integer>();
for (String word : words) {
  Integer count = counts.get(word);
  if (count == null) {
    counts.put(word, 1);
  } else {
    counts.put(word, count + 1);
  }
}
```

这很容易出错，并且不支持收集各种有用的统计信息。

Guava 提供的 `Multiset` 集合类型，一个元素可以出现多次，且元素顺序不重要：multiset `{a,a,b}` 与 `{a,b,a}` 相等。

有两种方式看待该问题：

- 一个没有顺序约束的 `ArrayList<E>`
- 类似 `Map<E, Integer>`，即元素和计数

Guava 的 `Multiset` API 结合了这两种方式：

- 当作为常规 `Collection` 使用，`Multiset` 的行为与无序 `ArrayList` 类似：
  - 调用 `add(E)` 添加指定元素
  - `iterator()` 对迭代每个元素的每次出现
  - `size()` 返回所有元素出现所有次数的加和
- 其它查询操作和性能特点则与 `Map<E, Integer>` 类似
  - `count(Object)` 返回指定元素出现的次数。对 `HashMultiset`，count 为 O(1) 操作，对 `TreeMultiset`，count 为 O(log n) 操作
  - `entrySet()` 返回 `Set<Multiset.Entry<E>>`，其行为与 `Map` 的 entrySet 类似
  - `elementSet()` 返回不同元素的 `Set<E>`，与 `Map` 的 `keySet()` 类似
  - `Multiset` 的内存消耗与不同元素的数量呈线性关系

`Multiset` 与 `Collection` 接口协议完全一致。`TreeMultiset` 和 `TreeSet` 一样，只是使用比较而非 `Object.equals` 判断相等性。`Multiset.addAll(Collection)` 会自动添加每个元素每次出现，比上面使用 `Map` 计数要方便。

| Method                                                       | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [`count(E)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Multiset.html#count-java.lang.Object-) | 返回指定元素出现的次数                                       |
| [`elementSet()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Multiset.html#elementSet--) | 返回 `Multiset<E>` 中不同元素的 `Set<E>` 视图                |
| [`entrySet()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Multiset.html#entrySet--) | 类似 `Map.entrySet()`, 返回一个 `Set<Multiset.Entry<E>>`, `Multiset.Entry<E>` 支持 `getElement()` 和 `getCount()` 操作 |
| [`add(E, int)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Multiset.html#add-java.lang.Object-int-) | 添加指定元素指定次数                                         |
| [`remove(E, int)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Multiset.html#remove-java.lang.Object-int--) | 删除指定元素指定次数                                         |
| [`setCount(E, int)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Multiset.html#setCount-E-int-) | 设置指定元素出现的次数                                       |
| `size()`                                                     | 返回所有元素出现的总次数                                     |

#### Multiset 不是 Map

`Multiset<E>` 不是 `Map<E, Integer>`，尽管这可能是 `Multiset` 实现的一部分。`Multiset` 是一个 `Collection` 类型，满足所有的相关接口。主要差异：

- `Multiset<E>` 只包含计数为正数的元素。计数为负数或 0 的元素认为不包含 multiset 中，也不会出现在 `elementSet()` 和 `entrySet()` 视图中
- `multiset.size()` 返回集合大小，是所有元素计数的总和。不同元素类型数使用 `elementSet().size()` 获得。因此，`add(E)` 会将 `multiset.size()` 增加 1
- `multiset.iterator()` 迭代每个元素的每次出现，因此迭代的长度等于 `multiset.size()`
- `Multiset<E>` 支持添加、删除元素，设置元素次数，`set(elem, 0)` 等效于删除元素的所有出现
- `multiset.count(elem)` 对 multiset 中不存在的元素返回 0

#### Multiset 实现

Guava 提供了 `Multiset` 的许多实现，大致对应于 JDK 的 map 实现。

| Map                 | Corresponding Multiset                                       | Supports `null` elements |
| ------------------- | ------------------------------------------------------------ | ------------------------ |
| `HashMap`           | [`HashMultiset`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/HashMultiset.html) | Yes                      |
| `TreeMap`           | [`TreeMultiset`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/TreeMultiset.html) | Yes                      |
| `LinkedHashMap`     | [`LinkedHashMultiset`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/LinkedHashMultiset.html) | Yes                      |
| `ConcurrentHashMap` | [`ConcurrentHashMultiset`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/ConcurrentHashMultiset.html) | No                       |
| `ImmutableMap`      | [`ImmutableMultiset`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/ImmutableMultiset.html) | No                       |

#### SortedMultiset

[`SortedMultiset`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/SortedMultiset.html)  相对 `Multiset` 支持有效获取指定范围的子集。例如，`latencies.subMultiset(0, BoundType.CLOSED, 100, BoundType.OPEN).size()` 获取延迟低于 100 毫秒的网站的点击次数，然后将其与 `latencies.size()` 对比确定总体比例。

`TreeMultiset` 实现 `SortedMultiset` 接口。

### Multimap

许多有经验的 Java 程序员都曾有实现 `Map<K, List<V>>` 或 `Map<K, Set<V>>`，并处理过该结构的尴尬问题。例如，`Map<K, Set<V>>` 是表示 unlabeled directed-graph 的典型方法。Guava 的 `Multimap` 框架可以轻松处理从 key 到多个 values 的映射。`Multimap` 是将一个 key 与多个值关联的通用方法。

从概念上将，有两种看待 Multimap 的方式，一种是从单个 key 到单个 value 的映射集合：

```
a -> 1
a -> 2
a -> 4
b -> 3
c -> 5
```

一种是从 unique-key 到集合的映射：

```
a -> [1, 2, 4]
b -> [3]
c -> [5]
```

一把来说，从第一个视角考虑 `Multimap` 更好，但它支持使用 `asMap()` 视图，返回 `Map<K, Collection<V>>`。

> [!NOTE]
>
> `Multimap` 不存在从 key 到空集合的映射，一个 key 要么映射到至少一个值，要么不存在与 `Multimap` 中。

一般不直接使用 `Multimap` 接口，而更常使用 `ListMultimap` 和 `SetMultimap`，它们分别将 key 映射到 `List` 和 `Set`。

#### 创建 Multimap

使用 `MultimapBuilder` 是创建 `Multimap` 的最直接方法，它支持配置表示 key 和 value 的方法。例如：

```java
// creates a ListMultimap with tree keys and array list values
ListMultimap<String, Integer> treeListMultimap =
    MultimapBuilder.treeKeys().arrayListValues().build();

// creates a SetMultimap with hash keys and enum set values
SetMultimap<Integer, MyEnum> hashEnumMultimap =
    MultimapBuilder.hashKeys().enumSetValues(MyEnum.class).build();
```

也可以直接使用实现类的 `create()` 方法，但更推荐 `MultimapBuilder`。

#### 修改 Multimap

[`Multimap.get(key)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Multimap.html#get-K-) 返回与指定 key 关联的值的视图，即使当前没有。对 `ListMultimap` 返回一个 `List`，对 `SetMultimap` 返回一个 `Set`。

对返回类型的修改会直接写入底层 `Multimap`。例如：

```java
Set<Person> aliceChildren = childrenMultimap.get(alice);
aliceChildren.clear();
aliceChildren.add(bob);
aliceChildren.add(carol);
```

会写入底层 multimap。

修改 multimap 更直接的方法：

| Signature                                                    | Description                                                  | Equivalent                                                   |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| [`put(K, V)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Multimap.html#put-K-V-) | 添加 key 到 value 的映射                                     | `multimap.get(key).add(value)`                               |
| [`putAll(K, Iterable)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Multimap.html#putAll-K-java.lang.Iterable-) | 依次添加从 key 到各个 value 的映射                           | `Iterables.addAll(multimap.get(key), values)`                |
| [`remove(K, V)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Multimap.html#remove-java.lang.Object-java.lang.Object-) | 删除一个 `key` 到 `value` 的映射，如果 multimap 发生变化，返回 `true` | `multimap.get(key).remove(value)`                            |
| [`removeAll(K)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Multimap.html#removeAll-java.lang.Object-) | 删除并返回与指定 key 关联的所有值。返回的集合可能可以修改，也可能不行，但是修改它肯定不影响 multimap | `multimap.get(key).clear()`                                  |
| [`replaceValues(K, Iterable)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Multimap.html#replaceValues-K-java.lang.Iterable-) | 清除与指定 `key` 关联的所有值，然后与 `values` 的所有值关联。返回之前与 `key` 关联的所有值 | `multimap.get(key).clear(); Iterables.addAll(multimap.get(key), values)` |

#### Multimap Views

`Multimap` 支持许多强大的视图：

- `asMap` 返回 `Multimap<K, V>` 的 `Map<K, Collection<V>>` 视图。返回的 map 支持 `remove`，但不支持 `put` 和 `putAll`。当你需要对不存在 key 返回 `null`，而不是一个新的可写入的空集合，就可以使用 `asMap().get(key)`。可以将 `asMap.get(key)` 转换为合适的集合类型，对 `SetMultimap` 为 `Set`，对 `ListMultimap` 为 `List`。
- `entries` 返回 `Multimap` 中所有条目的 `Collection<Map.Entry<K, V>>` 视图
- `keySet` 返回 `Multimap` 中不同 key 的集合 `Set`
- `keys` 将 `Multimap` 中的所有 keys 以 `Multiset` 返回，每个 key 出现的次数与其关联值的数目一样。可以从 `Multiset` 删除元素，但不能添加
- `values()` 将 `Multimap` 的所有值以 `Collection<V>` 返回

#### Multimap 不是 Map

`Multimap<K, V>` 不是 `Map<K, Collection<V>>`，尽管 `Multimap` 的实现可能采用了该 map。主要差别：

- `Multimap.get(key)` 始终返回一个非 null 集合。这不表示会消耗更多内容，返回的集合是一个视图，通过它可以添加与 key 的关联项
- 如果更喜欢 `Map` 对不存在的 key 映射返回 `null` 的行为，可以使用 `asMap()` 视图获得 `Map<K, Collection<V>>`。或者使用 static `Multimaps.asMap()` 方法从 `ListMultimap` 获得 `Map<K, List<V>>`，对 `SetMultimap` 和 `SortedSetMultimap` 也有类似方法。
- `Multimap.containsKey(key)` 只有在存在与 `key` 映射的元素时返回 true。如果 `k` 之前与多个值关联，后来这些关联从 multimap 移除，此时 `Multimap.containsKey(k)` 会返回 false
- `Multimap.entries()` 返回 `Multimap` 所有 key 的所有 entries。如果希望返回 key-collection entries，可以使用 `asMap().entrySet()`
- `Multimap.size()` 返回 multimap 包含 entries 的数目，不是不同 key 的数目。使用 `Multimap.keySet().size()` 获得不同 key 的数目

#### Multimap 实现

`Multimap` 提供了多种实现。注意，通常建议使用 `MultimapBuilder` 创建 `Multimap`。

| Implementation                                               | Keys behave like... | Values behave like.. |
| ------------------------------------------------------------ | ------------------- | -------------------- |
| [`ArrayListMultimap`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/ArrayListMultimap.html) | `HashMap`           | `ArrayList`          |
| [`HashMultimap`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/HashMultimap.html) | `HashMap`           | `HashSet`            |
| [`LinkedListMultimap`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/LinkedListMultimap.html) `*` | `LinkedHashMap``*`  | `LinkedList``*`      |
| [`LinkedHashMultimap`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/LinkedHashMultimap.html)`**` | `LinkedHashMap`     | `LinkedHashSet`      |
| [`TreeMultimap`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/TreeMultimap.html) | `TreeMap`           | `TreeSet`            |
| [`ImmutableListMultimap`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/ImmutableListMultimap.html) | `ImmutableMap`      | `ImmutableList`      |
| [`ImmutableSetMultimap`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/ImmutableSetMultimap.html) | `ImmutableMap`      | `ImmutableSet`       |

除了 immutable 实现，其它实现都支持 null keys 和 values。

- `*` `LinkedListMultimap.entries()` 保留迭代顺序
- `**` `LinkedHashMultimap` 保持 entry 的插入顺序，key 的插入顺序，以及与 key 关联的值的顺序

注意，并非所有 multimap 都通过 `Map<K, Collection<V>>` 实现，有些 `Multimap` 使用自定义哈希表来减少开销。

如果需要更多自定义功能，可以使用 [`Multimaps.newMultimap(Map, Supplier)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Multimaps.html#newMultimap-java.util.Map-com.google.common.base.Supplier-) 。

### BiMap

将 value 映射回 key 的传统方法是维护两个单独的 maps，并保持它们同步。这很容易出错，容易引起混乱。例如：

```java
Map<String, Integer> nameToId = Maps.newHashMap();
Map<Integer, String> idToName = Maps.newHashMap();

nameToId.put("Bob", 42);
idToName.put(42, "Bob");
// what happens if "Bob" or 42 are already present?
// weird bugs can arise if we forget to keep these in sync...
```

[`BiMap`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/BiMap.html) 是一个特殊的 `Map<K, V>`：

- 允许使用 `inverse()` 查看反向的 `BiMap<V, K>`
- 确保 values 是唯一的，使 `values()` 为 `Set`

如果将 key 映射到一个已经存在的 value，`BiMap.put(key, value)` 会抛出 `IllegalArgumentException`。如果希望删除之前已经存在的 value，可以使用 [`BiMap.forcePut(key, value)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/BiMap.html#forcePut-java.lang.Object-java.lang.Object-)。

```java
BiMap<String, Integer> userId = HashBiMap.create();
...

String userForId = userId.inverse().get(id);
```

#### BiMap 实现

| Key-Value Map Impl | Value-Key Map Impl | Corresponding `BiMap`                                        |
| ------------------ | ------------------ | ------------------------------------------------------------ |
| `HashMap`          | `HashMap`          | [`HashBiMap`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/HashBiMap.html) |
| `ImmutableMap`     | `ImmutableMap`     | [`ImmutableBiMap`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/ImmutableBiMap.html) |
| `EnumMap`          | `EnumMap`          | [`EnumBiMap`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/EnumBiMap.html) |
| `EnumMap`          | `HashMap`          | [`EnumHashBiMap`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/EnumHashBiMap.html) |

> [!NOTE]
>
> `BiMap` 的实用程序，如 `synchronizedBiMap` 在 `Maps` 中。

### Table

```java
Table<Vertex, Vertex, Double> weightedGraph = HashBasedTable.create();
weightedGraph.put(v1, v2, 4);
weightedGraph.put(v1, v3, 20);
weightedGraph.put(v2, v3, 5);

weightedGraph.row(v1); // returns a Map mapping v2 to 4, v3 to 20
weightedGraph.column(v3); // returns a Map mapping v1 to 20, v2 to 5
```

如果需要为多个 key 构建索引，最终会得到类似 `Map<FirstName, Map<LastName, Person>>` 的结构，这使用起来又丑又笨。Guava 提供的 `Table` 支持 row 和 column 的查询。`Table` 支持大量视图：

- [`rowMap()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Table.html#rowMap--)，返回 `Table<R, C, V>` 的 `Map<R, Map<C, V>>` 视图。类似的，[`rowKeySet()`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Table.html#rowKeySet--) 返回 `Set<R>`
- [`row(r)`](https://guava.dev/releases/snapshot/api/docs/com/google/common/collect/Table.html#row-R-) 返回一个 non-null `Map<C, V>`。

### RangeSet
2025-10-29⭐
***

`RangeSet` 描述一组不相连的非空范围（range）。当添加新的 range 到 `RangeSet`，相连的 ranges 被合并，空的 range 被忽略。例如：

```java
RangeSet<Integer> rangeSet = TreeRangeSet.create();
rangeSet.add(Range.closed(1, 10)); // {[1, 10]}
rangeSet.add(Range.closedOpen(11, 15)); // disconnected range: {[1, 10], [11, 15)}
rangeSet.add(Range.closedOpen(15, 20)); // connected range; {[1, 10], [11, 20)}
rangeSet.add(Range.openClosed(0, 0)); // empty range; {[1, 10], [11, 20)}
rangeSet.remove(Range.open(5, 10)); // splits [1, 10]; {[1, 5], [10, 10], [11, 20)}
```

> [!NOTE]
>
> 如果要合并类似 `Range.closed(1, 10)` 和 `Range.closedOpen(11, 15)` 的 ranges，需要首先使用 `Range.canonical(DiscreteDomain)` 对 ranges 进行预处理，例如使用 `DiscreteDomain.integers()`。
>
> GWT 和 JDK 1.5- 都不支持 `RangeSet`，因为 `RangeSet` 需要使用  JDK 1.6 的`NavigableMap` 的功能。

#### 创建 RangeSet

`RangeSet` 有两个实现：

- `TreeRangeSet`
- `ImmutableRangeSet`



- 创建 mutable 空 `RangeSet`

```java
TreeRangeSet<Integer> rangeSet = TreeRangeSet.create();
```

- 从 `Range` 集合创建

```java
List<Range<Integer>> numberList = Arrays.asList(Range.closed(0, 2));
TreeRangeSet<Integer> numberRangeSet = TreeRangeSet.create(numberList);
```

- 对 immutable `RangeSet`, 使用 `ImmutableRangeSet` 中的 builder

```java
ImmutableRangeSet<Integer> rangeSet = new ImmutableRangeSet.Builder<Integer>().add(Range.closed(0, 2)).build();
```

在创建 `ImmutableRangeSet` 时，不允许添加重叠的 ranges，否则抛出 `IllegalArgumentException`。

- 移出 ranges

```java
TreeRangeSet<Integer> rangeSet = TreeRangeSet.create();

rangeSet.add(Range.closed(0, 2));
rangeSet.add(Range.closed(3, 5));
rangeSet.add(Range.closed(6, 8));
rangeSet.add(Range.closed(9, 15)); // [[0..2], [3..5], [6..8], [9..15]]

rangeSet.remove(Range.closed(3, 5));
rangeSet.remove(Range.closed(7, 10)); // [[0..2], [6..7), (10..15]]

assertTrue(rangeSet.contains(1));
assertFalse(rangeSet.contains(9));
assertTrue(rangeSet.contains(12));
```

#### Views

`RangeSet` 支持许多视图，包括：

- `complement()`：`RangeSet` 的补集。`complement` 也是一个 `RangeSet`，同样包含不连续的非空 ranges
- `subRangeSet(Range<C>)`：返回 `RangeSet` 与指定 `Range` 的交集。是传统有序集合的 `headSet`, `subSet` 和 `tailSet` 的泛化版本
- `intersects(Range<C> otherRange)`：如果 `RangeSet` 与指定 `Range` 存在交集，返回 true。等价于调用 `subRangeSet(Range<C>)` 然后测试返回的 range是否为空
- `asRanges()`：将 `RangeSet` 转换为可迭代的 `Set<Range<C>>`
- `asSet(DiscreteDomain<C>)` (`ImmutableRangeSet` only)：将 `RangeSet<C>` 作为 `ImmutableSortedSet<C>`，查看范围内的元素而非 ranges。(如果 `DiscreteDomain` 和 `RangeSet` 的上边界或下边界均为 unbounded,则不支持该操作).

`complement()` 示例：

```java
RangeSet<Integer> rangeSet = TreeRangeSet.create();

rangeSet.add(Range.closed(0, 2));
rangeSet.add(Range.closed(3, 5));
rangeSet.add(Range.closed(6, 8)); // [[0..2], [3..5], [6..8]]

RangeSet<Integer> complementRangeSet = rangeSet.complement(); // [(-∞..0), (2..3), (5..6), (8..+∞)]

assertTrue(complementRangeSet.contains(-1000));
assertFalse(complementRangeSet.contains(2));
assertFalse(complementRangeSet.contains(3));
assertTrue(complementRangeSet.contains(1000));
```

`subRangeSet` 示例：

```java
RangeSet<Integer> rangeSet = TreeRangeSet.create();
rangeSet.add(Range.closed(0, 2));
rangeSet.add(Range.closed(3, 5));
rangeSet.add(Range.closed(6, 8)); // [[0..2], [3..5], [6..8]]

RangeSet<Integer> subRangeSet
        = rangeSet.subRangeSet(Range.closed(4, 14)); // [[4..5], [6..8]]

assertFalse(subRangeSet.contains(3));
assertFalse(subRangeSet.contains(14));
assertTrue(subRangeSet.contains(7));
```

`intersects` 示例：

```java
RangeSet<Integer> rangeSet = TreeRangeSet.create();

rangeSet.add(Range.closed(0, 2));
rangeSet.add(Range.closed(3, 10));
rangeSet.add(Range.closed(15, 18)); // [[0..2], [3..10], [15..18]]

assertTrue(rangeSet.intersects(Range.closed(4, 17)));
assertFalse(rangeSet.intersects(Range.closed(19, 200)));
```

#### Queries

`RangeSet` 支持许多查询操作，最常用的有：

- `contains(C)`：`RangeSet` 最基本的操作，查询 `RangeSet` 是否有一个 range 包含指定元素
- `rangeContaining(C)`：返回包含指定元素的 `Range`，如果没有则返回 `null`
- `encloses(Range<C>)`：测试 `RangeSet` 中是否有 `Range` 包含指定 range
- `span()`：返回包含 `RangeSet` 中每个范围的最小 `Range`。

`contains(C)` 示例：

```java
TreeRangeSet<Integer> rangeSet = TreeRangeSet.create();
rangeSet.add(Range.closed(0, 2));
rangeSet.add(Range.closed(3, 5));
rangeSet.add(Range.closed(6, 8)); // [[0..2], [3..5], [6..8]]

assertTrue(rangeSet.contains(1));
assertFalse(rangeSet.contains(9));
```

`span()` 示例：

```java
TreeRangeSet<Integer> rangeSet = TreeRangeSet.create();
rangeSet.add(Range.closed(0, 2));
rangeSet.add(Range.closed(3, 5));
rangeSet.add(Range.closed(6, 8)); // [[0..2], [3..5], [6..8]]

Range<Integer> experienceSpan = rangeSet.span();

assertEquals(0, experienceSpan.lowerEndpoint().intValue());
assertEquals(8, experienceSpan.upperEndpoint().intValue());
```



## RangeMap

`RangeMap` 定义不相交 range 到值得映射。和 `RangeSet` 不同, `RangeMap` 不会合并相邻的 range，即使是映射到相同的值。例如：

```java
RangeMap<Integer, String> rangeMap = TreeRangeMap.create();
rangeMap.put(Range.closed(1, 10), "foo"); // {[1, 10] => "foo"}
rangeMap.put(Range.open(3, 6), "bar"); // {[1, 3] => "foo", (3, 6) => "bar", [6, 10] => "foo"}
rangeMap.put(Range.open(10, 20), "foo"); // {[1, 3] => "foo", (3, 6) => "bar", [6, 10] => "foo", (10, 20) => "foo"}
rangeMap.remove(Range.closed(5, 11)); // {[1, 3] => "foo", (3, 5) => "bar", (11, 20) => "foo"}
```

`RangeMap` 提供了两个视图：
- `asMapOfRanges()`，即 `Map<Range<K>, V>` 视图，可用于迭代 `RangeMap`。
- `subRangeMap(Range<K>)` 以 `RangeMap` 的形式返回 `RangeMap` 与  `Range` 的交集。提供了传统 `headMap`, `subMap` 和 `tailMap` 的通用版本。

## 参考

- https://www.baeldung.com/guava-rangeset
- https://github.com/google/guava/wiki/NewCollectionTypesExplained#rangeset
