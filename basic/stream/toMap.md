# 收集为 Map

- [收集为 Map](#收集为-map)
  - [简介](#简介)
  - [Key 冲突](#key-冲突)
  - [合并相同 key](#合并相同-key)
  - [设置 Map 类型](#设置-map-类型)
  - [ConcurrentMap](#concurrentmap)
  - [SortedMap](#sortedmap)

2023-11-22, 20:58
@author Jiawei Mao
****

## 简介

假设有一个 `Book` 类：

```java
class Book {

    private String name;
    private int year;
    private String isbn;

    public Book(String name, int year, String isbn) {
        this.name = name;
        this.year = year;
        this.isbn = isbn;
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }

    public String getIsbn() {
        return isbn;
    }

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", year=" + year +
                ", isbn='" + isbn + '\'' +
                '}';
    }
}
```

现在有一个 `Stream<Book>`，想将其元素收集到 `Map`，key 为 ISBN，value 为 name:

```java
List<Book> bookList = new ArrayList<>();
bookList.add(new Book("The Fellowship of the Ring", 1954, "0395489318"));
bookList.add(new Book("The Two Towers", 1954, "0345339711"));
bookList.add(new Book("The Return of the King", 1955, "0618129111"));

Map<String, String> map = bookList.stream()
        .collect(Collectors.toMap(Book::getIsbn, Book::getName));
```

`Collectors.toMap` 方法有两个参数，均为 `Function` 类型，分别用于生成 `Map` 的 key 和 value。

如果直接将 `Book` 作为 `Map` 的 value，第二个参数可以使用 `Function.identity()`：

```java
Map<String, Book> map = bookList.stream()
        .collect(Collectors.toMap(Book::getIsbn, Function.identity()));
```

## Key 冲突

如果有多个元素的 key 相同，`Collectors.toMap` 会抛出 `IllegalStateException`。

此时可以使用 `toMap` 的第三个可选参数 `BinaryOperator<U> mergeFunction` 来解决。

`mergeFunction` 包含两个参数，第一个为流中已有元素，第二个为与已有 Key 冲突的元素，我们只需要选择保留哪个值。对 Book 中，按 Year 第二个和第一个冲突：

```java
Map<Integer, Book> map = bookList.stream()
        .collect(Collectors.toMap(Book::getYear,
                Function.identity(),
                (exiting, newValue) -> exiting));
System.out.println(map);
```

```
{1954=Book{name='The Fellowship of the Ring', year=1954, isbn='0395489318'}, 1955=Book{name='The Return of the King', year=1955, isbn='0618129111'}}
```

## 合并相同 key

如果希望将相同 year 的 `Book` 都收集起来，此时需要返回 `Map<Integer, Set<Book>>`。处理方式：

```java
bookList.stream().collect(Collectors.toMap(
        Book::getYear,
        Collections::singleton,
        (a, b) -> {
            Set<Book> union = new HashSet<>(a);
            union.addAll(b);
            return union;
        }
));
```

这里，第二个参数不再是 `Function.identity()`，而是一个 Set<String> 类型，即将每个 `Book` 元素都映射为包含单个元素的 `Set`，在第三个参数解决冲突时，将两个 Set 进行合并，就达到目的。

## 设置 Map 类型

`toMap()` 默认返回 `HashMap`。如果希望返回其它 Map 类型，如 `TreeMap`，可以将其构造函数作为 `toMap` 的第四个参数：

```java
bookList.stream()
        .collect(Collectors.toMap(
                Book::getIsbn,
                Function.identity(),
                (existing, newValue) -> {
                    throw new IllegalStateException();
                },
                TreeMap::new));;
```

此时必须提供第三个参数，这里采用了默认选择，即有 Key 冲突时抛出异常。

## ConcurrentMap

!!! note
    对每个 `toMap` 方法，都有一个对应的 `toConcurrentMap` 方法生成并发 map。在并行集合处理过程中，使用单个并行 map。因为共享 map 并合并 map 更有效。不过此时元素不再按顺序收集，不过这通常没影响。

```java
bookList.stream()
        .collect(Collectors.toConcurrentMap(
                book -> book.getIsbn(),
                Function.identity()));
```

## SortedMap

`TreeMap` 为 SortedMap 类型，所以只需要提供 TreeMap 的构造函数即可：

```java
bookList.stream()
        .collect(Collectors.toMap(
                Book::getName,
                Function.identity(),
                (o1, o2) -> o1,
                TreeMap::new));
```
