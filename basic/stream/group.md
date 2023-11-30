# 分组

- [分组](#分组)
  - [简介](#简介)
  - [下游 Collector](#下游-collector)
    - [指定 map 值类型](#指定-map-值类型)
    - [转换为数字](#转换为数字)
      - [counting](#counting)
      - [averaging](#averaging)
      - [summing](#summing)
    - [maxBy 和 minBy](#maxby-和-minby)
    - [collectingAndThen](#collectingandthen)
    - [mapping](#mapping)
    - [flatMapping](#flatmapping)
    - [summarizing](#summarizing)
    - [filtering](#filtering)
    - [teeing](#teeing)
  - [多级分组](#多级分组)
  - [partitioningBy](#partitioningby)

@author Jiawei Mao
***

## 简介

在 `toMap` 中展示了如何将相同 Key 的元素收集到 `Set`，该操作需要为每个元素创建 `Set`，然后进行 `Set` 合并，该过程乏味且低效。但是对具有相同特征的元素值进行分组是如此常见，因此 `Stream` 为该功能专门提供了 `groupingBy` 方法。

```java
groupingBy(classifier)
groupingBy(classifier, collector)
groupingBy(classifier, supplier, collector)
```

以及对应的并行版本：

```java
groupingByConcurrent(classifier)
groupingByConcurrent(classifier, collector)
groupingByConcurrent(classifier, supplier, collector)
```

参数说明：

- `classifier`：`Function` 类型，指定从元素到 key 的映射
- `collector`：`Collector` 类型，指定下游 collector，默认为 `toList`，即将相同分组元素放到 `List`
- `supplier`：`Supplier` 类型，指定 Map 的类型，默认为 HashMap

基本示例类和数据：

```java
record Person(int id, String name, double salary, Department department) { }

record Department(int id, String name) { }

List<Person> persons = List.of(
        new Person(1, "Alex", 100, new Department(1, "HR")),
        new Person(2, "Brian", 200, new Department(1, "HR")),
        new Person(3, "Charles", 900, new Department(2, "Finance")),
        new Person(4, "David", 200, new Department(2, "Finance")),
        new Person(5, "Edward", 200, new Department(2, "Finance")),
        new Person(6, "Frank", 800, new Department(3, "ADMIN")),
        new Person(7, "George", 900, new Department(3, "ADMIN")));
```

**示例：** 按 `Department` 对 `Person` 分组，返回 `Map<Department, List<Person>>`

```java
Map<Department, List<Person>> map = persons.stream()
        .collect(Collectors.groupingBy(Person::department));
System.out.println(map);
```

```
{
    Department[id=2, name=Finance]=[
        Person[id=3, name=Charles, salary=900.0, department=Department[id=2, name=Finance]], 
        Person[id=4, name=David, salary=200.0, department=Department[id=2, name=Finance]], 
        Person[id=5, name=Edward, salary=200.0, department=Department[id=2, name=Finance]]], 
    
    Department[id=3, name=ADMIN]=[
        Person[id=6, name=Frank, salary=800.0, department=Department[id=3, name=ADMIN]], 
        Person[id=7, name=George, salary=900.0, department=Department[id=3, name=ADMIN]]], 
    
    Department[id=1, name=HR]=[
        Person[id=1, name=Alex, salary=100.0, department=Department[id=1, name=HR]], 
        Person[id=2, name=Brian, salary=200.0, department=Department[id=1, name=HR]]]
}
```

## 下游 Collector

`groupingBy` 生成 map 的值为 `List` 类型。如果希望继续对 List 进行处理，可以提供下游 `collector`，即 `groupingBy` 的第二个参数。

### 指定 map 值类型

下游 collector 默认为 `toList()`，也可以用其它类型，如 `toSet()`，返回 map 的值类型就变为 Set：

```java
Map<Department, Set<Person>> map = persons.stream()
        .collect(Collectors.groupingBy(
                Person::department,
                Collectors.toSet()));
```

### 转换为数字

#### counting

`counting` 获得元素个数：

```java
Map<Department, Long> map = persons.stream()
        .collect(groupingBy(Person::department, counting()));
System.out.println(map);
```

```
{Department[id=2, name=Finance]=3, 
Department[id=3, name=ADMIN]=2, 
Department[id=1, name=HR]=2}
```

这里先按 `department` 进行分组，然后获得每个组的元素个数。

#### averaging

将下游 `collector` 指定为 `averagingDouble`, `averagingInt` 或 `averagingLong`，可以计算各个分组的均值。

```java
Map<Department, Double> map = persons.stream()
        .collect(Collectors.groupingBy(Person::department,
                Collectors.averagingDouble(Person::salary)));
```

这里按 `department` 分组后，计算各个分组的 `salary` 均值。

#### summing

同均值，只是下游 collector 使用 `summingDouble`, `summingInt` 或 `summingLong`： 

```java
Map<Department, Double> map = persons.stream()
        .collect(Collectors.groupingBy(Person::department,
                Collectors.summingDouble(Person::salary)));
```

### maxBy 和 minBy

`maxBy` 和 `minBy` 根据 `Comparator` 获得各个分组最大和最小元素。这两个 collector 都考虑到 Stream 可能为空，因此返回 `Optional` 类型。下面返回各个分组 salary 最高的 Person。

```java
Map<Department, Optional<Person>> map = persons.stream()
        .collect(Collectors.groupingBy(Person::department,
                Collectors.maxBy(Comparator.comparingDouble(Person::salary))));
```

### collectingAndThen

`collectingAndThen` 在执行 collect 后根据指定 `Function` 再执行一步操作。

例如，根据字符串首字母进行分组，收集到 `Set` 后，查看每个 `Set` 元素个数：

```java
Map<Character, Integer> stringCountsByStartingLetter =
        strings.collect(
                groupingBy(s -> s.charAt(0),
                        collectingAndThen(toSet(), Set::size)));
```

### mapping

`collectingAndThen` 是先收集元素，然后执行操作；`mapping` 相反，先执行操作，然后收集。

```java
Map<Character, Set<Integer>> stringLengthsByStartingLetter =
        strings.collect(
                groupingBy(s -> s.charAt(0),
                        mapping(String::length, toSet())));
```

这里，对字符串先按首字母分组，然后对每个分组，计算字符串长度，收集到 `Set`。

### flatMapping

功能类似 `mapping`，用于处理函数返回 stream 的情况，即处理返回多个元素的情况。


### summarizing

对返回 int, long 或 double 的 mapping 或 grouping 函数，可以为其生成一个统计对象。

```java
Map<String, IntSummaryStatistics> stateToCityPopulationSummary 
= cities.collect( 
   groupingBy(City::state, 
      summarizingInt(City::population)));
```

### filtering

`filtering` collector 对每个 group 进行过滤。例如：

```java
Map<String, Set<City>> largeCitiesByState 
   = cities.collect( 
      groupingBy(City::state, 
         filtering(c -> c.population() > 500000, 
            toSet()))); // States without large cities have empty sets
```

### teeing



## 多级分组

下游的 `collector` 如果继续为 `groupingBy`，就等于二次分组。例如：

```java
Map<Department, Map<Double, List<Person>>> map = persons.stream()
        .collect(Collectors.groupingBy(
                Person::department, 
                Collectors.groupingBy(Person::salary)));
```

这里先根据 `department` 进行分组，然后继续通过 `salary` 分组。


## partitioningBy

当用于分组的函数返回 boolean 类型，采用 `partitioningBy` 性能比 `groupBy` 更好。

