# record

2023-12-07, 15:30⭐
@author Jiawei Mao
****
## 简介

考虑如下 `Point` 类：

```java
class Point {
    private final double x;
    private final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {return x;}

    public double getY() {return y;}

    public String toString() {
        return "Point[x=%f, y =%f]".formatted(x, y);
    }
    // More methods ...
}
```

我们只是想表示平面上的一个点，创建类显得过于繁琐。JDK 14 引入了 record preview，在 JDK 16 中正式启用，用于简化这种数据类的创建。
## 使用 record

record 是一种特殊形式的类，其状态为 public immutable。以 `record` 定义相同功能的 `Point`:

```java
record Point(double x, double y) { }
```

JVM 会自动为 record 创建多个方法。包括：

- 两个字段

```java
private final double x; 
private final double y;
```

- 一个构造函数

```java
Point(double x, double y)
```

- 字段访问方法

```java
public double x() 
public double y()
```

调用方式：

```java
var p = new Point(3, 4); 
System.out.println(p.x() + " " + p.y());
```

另外，JVM 自动为 record 实现了三个方法：`toString`, `equals` 和 `hashCode`。

对这些方法，可以自定义实现覆盖默认实现（其实不推荐）：

```java
record Point(double x, double y) {
    public double x() {return y;} // BAD 
}
```

在 record 中也可以添加自定义方法：

```java
record Point(double x, double y) {
    public double distanceFromOrigin() {
        return Math.hypot(x, y);
    }
}
```

和其它类一样，record 也可以静态字段和方法：

```java
record Point(double x, double y) {

    public static Point ORIGIN = new Point(0, 0);

    public static double distance(Point p, Point q) {
        return Math.hypot(p.x - q.x, p.y - q.y);
    }

    public double distanceFromOrigin() {
        return Math.hypot(x, y);
    }
}
```

**但是**，在 record 中不能添加实例字段。例如：

```java
record Point(double x, double y) {
    private double r; // ERROR 
    ...
}
```

## Immutable

record 字段自动为 `final`，但是如果字段自身是 mutable 的，例如：

```java
record PointInTime(double x, double y, Date when) { }
```

依然可以修改 `when` 的值：

```java
var pt = new PointInTime(0, 0, new Date()); 
pt.when().setTime(0);
```

如果你希望 record 为 immutable，就不要使用 mutable 类型字段。

在并发应用中，record 更高效、安全。

## 构造函数

record 自动创建的构造函数称为**规范构造函数**（canonical constructor）。

可以额外自定义构造函数。在自定义构造函数中，第一句必须调用其它构造函数，所以最终必然重定向到规范构造函数：

```java
record Point(double x, double y) {
    public Point() {this(0, 0);}
}
```

该 record 由两个构造函数：一个规范构造函数，和一个自定义的无参构造函数，对应原点。

如果规范构造函数需要做额外初始化工作，可以覆盖默认实现：

```java
public record Range(int from, int to) {

    public Range(int from, int to) {
        if (from <= to) {
            this.from = from;
            this.to = to;
        } else {
            this.from = to;
            this.to = from;
        }
    }
}
```

不过，在实现规范构造函数时，可以省略参数，即：

```java
public record Range(int from, int to) {
    public Range {
        if (from > to) {
            int temp = from;
            from = to;
            to = temp;
        }
    }
}
```

紧凑形式的规范构造函数，其主体只是在将参数变量 `from` 和 `to` 赋值给实例字段 `this.from` 和 `this.to` 之前修改它们。不能在紧凑构造函数中修改实例字段。

