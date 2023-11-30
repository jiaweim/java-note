# Reduce

- [Reduce](#reduce)
  - [简介](#简介)
  - [Stream.reduce](#streamreduce)
  - [combiner](#combiner)
  - [identity](#identity)
  - [accumulator](#accumulator)

@author Jiawei Mao
***

## 简介

JDK 包含许多终端操作，如 avarage, sum, min, max, count 等，组合 Stream 的内容返回一个值。这类操作称为 reduction 操作。JDK 还包含返回集合而非单个值的 reduction 操作。许多 reduction 操作执行特定任务，如计算均值、分组。而 `reduce` 和 `collect` 提供通用 reduction 操作。

示例类：

```java
import java.time.LocalDate;
import java.time.chrono.IsoChronology;
import java.util.ArrayList;
import java.util.List;

public class Person {

    public enum Sex {
        MALE, FEMALE
    }

    String name;
    LocalDate birthday;
    Sex gender;
    String emailAddress;

    Person(String nameArg, LocalDate birthdayArg,
            Sex genderArg, String emailArg) {
        name = nameArg;
        birthday = birthdayArg;
        gender = genderArg;
        emailAddress = emailArg;
    }

    public int getAge() {
        return birthday.until(IsoChronology.INSTANCE.dateNow()).getYears();
    }

    public void printPerson() {
        System.out.println(name + ", " + this.getAge());
    }

    public Sex getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public static int compareByAge(Person a, Person b) {
        return a.birthday.compareTo(b.birthday);
    }

    public static List<Person> createRoster() {

        List<Person> roster = new ArrayList<>();
        roster.add(new Person(
                "Fred",
                IsoChronology.INSTANCE.date(1980, 6, 20),
                Person.Sex.MALE,
                "fred@example.com"));
        roster.add(new Person(
                "Jane",
                IsoChronology.INSTANCE.date(1990, 7, 15),
                Person.Sex.FEMALE, "jane@example.com"));
        roster.add(new Person(
                "George",
                IsoChronology.INSTANCE.date(1991, 8, 13),
                Person.Sex.MALE, "george@example.com"));
        roster.add(new Person(
                "Bob",
                IsoChronology.INSTANCE.date(2000, 9, 12),
                Person.Sex.MALE, "bob@example.com"));

        return roster;
    }
}
```

## Stream.reduce

`Stream.reduce` 是一个通用 reduction 操作。

**示例：** 以 `sum` 专用操作计算年龄总和

```java
List<Person> roster = Person.createRoster();
int totalAge = roster
        .stream()
        .mapToInt(Person::getAge)
        .sum();
```

以 `reduce` 实现该功能：

```java
Integer totalAgeReduce = roster
        .stream()
        .map(Person::getAge)
        .reduce(0, (a, b) -> a + b);
```

这里 reduce 操作有两个参数：

- `identity`：reduction 操作的初始值，也是 Stream 为空时的默认值。对上例，`identity` 值为 0，即年龄加和初始值为 0，如果 `roster` 为空，年龄加和默认为 0.
- `accumulator`：accumulator 为 `BinaryOperator` 类型，它有两个参数，一个为部分结果（这里为已处理整数加和），一个为下一个元素。返回新的部分结果。上例以 lambda 表达式实现 `BinaryOperator`

```java
(a, b) -> a + b)
```

`reduce` 方法有三个版本，可能的参数有三个：

- `identity`, 收集器的初始值或默认值；
- `accumulator`, 指定收集策略的函数；
- `combiner`，将 `accumulator` 的结果进行合并的函数。

## combiner

`combiner` 为 `BinaryOperator` 类型，即接收两个相同类型的元素，生成一个新的 `Optional` 类型。

例如：

```java
OptionalInt reduced =
  IntStream.range(1, 4).reduce((a, b) -> a + b);
```

或者更复杂的示例：

```java
public class Point {

    private double x;
    private double y;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}

public static List<Point> generatePointList(int size) {

    List<Point> ret = new ArrayList<>();
    Random randomGenerator = new Random();
    for (int i = 0; i < size; i++) {
        Point point = new Point();
        point.setX(randomGenerator.nextDouble());
        point.setY(randomGenerator.nextDouble());
        ret.add(point);
    }
    return ret;
}
```

```java
List<Point> points = PointGenerator.generatePointList(10000);
Optional<Point> point = points.parallelStream().reduce((p1, p2) -> {
    Point p = new Point();
    p.setX(p1.getX() + p2.getX());
    p.setY(p1.getY() + p2.getY());
    return p;
});
```

这里 `reduce` 返回 `Optional<Point>`。

## identity

`identity` 指定初始值或默认值。

根据初始值和指定的函数，根据流生成一个值：
```java
List<Integer> integers = Arrays.asList(1, 1, 1);
Integer reduced = integers.stream().reduce(23, (a, b) -> a + b);
```

## accumulator

如果所需类型与 Stream 元素类型不一样，需要 accumulator 进行类型转换。`accumulator` 为 `BiFunction` 类型。等价于：

```java
U result = identity; 
for (T element : this stream)      
    result = accumulator.apply(result, element)  
return result;
```