# 收集为 Map

- [收集为 Map](#收集为-map)
  - [简介](#简介)
  - [相同 key](#相同-key)
  - [合并相同 key](#合并相同-key)
  - [设置 Map 类型](#设置-map-类型)
  - [示例](#示例)

2023-11-22, 20:58
@author Jiawei Mao
****

## 简介

假设你有一个 `Stream<Person>`，想将其元素收集到 `Map`，便于后续通过 ID 查找 `Person`。`Collectors.toMap` 方法有两个参数，用于生成 Map 的 key 和 value。例如：

```java
public record Person(int id, String name) {} 
. . . 
Map<Integer, String> idToName = 
        people.collect(Collectors.toMap(Person::id, Person::name));
```

如果直接将元素作为 Map 的 value，第二个参数可以使用 `Function.identity()`：

```java
Map<Integer, Person> idToPerson = people.collect( 
    Collectors.toMap(Person::id, Function.identity()));
```

## 相同 key

如果有多个元素具有相同 keys，该 collector 会抛出 `IllegalStateException`。可以提供一个函数作为第三个参数，来覆盖该默认行为。例如：

```java
Stream<Locale> locales = Stream.of(Locale.getAvailableLocales()); 
Map<String, String> languageNames = locales.collect( 
   Collectors.toMap( 
      Locale::getDisplayLanguage, 
      loc -> loc.getDisplayLanguage(loc), 
      (existingValue, newValue) -> existingValue));
```

第三个参数，对现有值和新值，采用现有值。

## 合并相同 key

现在，假设你想知道某个国家使用的所有语言。此时需要返回 `Map<String, Set<String>>`。对每个语言，先保存为一个 set，当出现新的语言，将其合并为一个新的 set：

```java
Map<String, Set<String>> countryLanguageSets = locales.collect(
        Collectors.toMap(
                Locale::getDisplayCountry,
                l -> Collections.singleton(l.getDisplayLanguage()),
                (a, b) -> { // Union of a and b 
                    var union = new HashSet<String>(a);
                    union.addAll(b);
                    return union;
                }));
```

## 设置 Map 类型

例如，如果希望返回 `TreeMap`，可以将其构造函数作为 toMap 的第四个参数：

```java
Map<Integer, Person> idToPerson = people.collect(
        Collectors.toMap(
                Person::id,
                Function.identity(),
                (existingValue, newValue) -> {throw new IllegalStateException();},
                TreeMap::new));
```

!!! note
    对每个 toMap 方法，都有一个对应的 `toConcurrentMap` 方法生成并发 map。在并行集合处理过程中，使用单个并行 map。因为共享 map 并合并 map 更有效。不过此时元素不再按顺序收集，不过这通常没影响。

## 示例

```java
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamTest {

    public record Person(int id, String name) {}

    public static Stream<Person> people() {
        return Stream.of(new Person(1001, "Peter"),
                new Person(1002, "Paul"),
                new Person(1003, "Mary"));
    }

    public static void main(String[] args) throws IOException {
        Map<Integer, String> idToName = people().collect(
                Collectors.toMap(Person::id, Person::name)); // 自选字段为 value
        System.out.println("idToName: " + idToName);

        Map<Integer, Person> idToPerson = people().collect(
                Collectors.toMap(Person::id, Function.identity())); // Stream 元素为 value
        System.out.println("idToPerson: " + idToPerson.getClass().getName() + idToPerson);

        idToPerson = people().collect(
                Collectors.toMap(Person::id, Function.identity(),
                        (existingValue, newValue) -> {
                            throw new IllegalStateException();
                        },
                        TreeMap::new)); // 返回 TreeMap 类型
        System.out.println("idToPerson: " + idToPerson.getClass().getName() + idToPerson);

        Stream<Locale> locales = Stream.of(Locale.getAvailableLocales());
        // 重复 key，选择保留已有元素
        Map<String, String> languageNames = locales.collect(
                Collectors.toMap(
                        Locale::getDisplayLanguage,
                        l -> l.getDisplayLanguage(l),
                        (existingValue, newValue) -> existingValue)); 
        System.out.println("languageNames: " + languageNames);

        locales = Stream.of(Locale.getAvailableLocales());
        Map<String, Set<String>> countryLanguageSets = locales.collect(
                Collectors.toMap(
                        Locale::getDisplayCountry,
                        l -> Set.of(l.getDisplayLanguage()),
                        (a, b) ->
                        { // union of a and b
                            Set<String> union = new HashSet<>(a);
                            union.addAll(b);
                            return union;
                        })); // 重复 key，选择合并元素
        System.out.println("countryLanguageSets: " + countryLanguageSets);
    }
}
```