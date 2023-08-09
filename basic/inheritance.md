# 继承

2023-08-03, 09:21
modify: with Core Java 12th
2023-08-02, 17:16
****
## 参数量可变的方法

可以定义参数量可变的方法，这类方法称为 varargs 方法。

`printf` 就是这类方法，调用：

```java
System.out.printf("%d", n);
```

或者：

```java
System.out.printf("%d %s", n, "widgets");
```

虽然第一次调用有 2 个参数，第二次调用有 3 个参数，但是调用的是同一个方法。

`printf` 的定义如下：

```java
public class PrintStream{

    public PrintStream printf(String fmt, Object... args) { return format(fmt, args); }
}
```

这里的省略号 `...` 是 Java 代码的一部分 ，表示该方法可以接收任意数量的 `Object`。

`printf` 实际上接收两个参数：

- 格式化字符串 `fmt`
- `Object[]` 数组，即 `Object...` 等价于 `Object[]`

如果提供整数或其它基本类型，会自动装箱为对象类型。

编译器会自动将 `Object...` 转换为 `Object[]`， 并执行必要的装箱操作。然后扫描 `fmt` 字符串，将第 i 个格式符与 `args[i]` 匹配。

 简而言之，对 printf 的每次调用，编译器需要将参数打包成一个数组，并根据需要自动装箱：

```java
System.out.printf("%d %s", new Object[] { Integer.valueOf(n), "widgets" } );
```

可以自定义参数量可变的方法，可以指定任意类型的形参，包括基本类型。例如，计算最大值：

```java
public static double max(double... values){

    double largest = Double.NEGATIVE_INFINITY;
    for (double v : values) if (v > largest) largest = v;
    return largest;
}
```

可以按如下方式调用：

```java
double m = max(3.1, 40.4, -5);
```

编译器打包参数后将 `new double[] { 3.1, 40.4, -5 }` 传给 max 函数。

对参数可变的方法，可以将数组作为最后一个参数传入。例如：

```java
System.out.printf("%d %s", new Object[] { Integer.valueOf(1), "widgets" } );
```

因此，对最后一个参数为数组的现有函数，可以重新定义为 varargs 方法，不会破坏任何现有代码。
