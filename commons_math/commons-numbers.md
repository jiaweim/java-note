# Apache Commons Numbers

## 简介

Apache Commons Numbers 提供数字类型和工具。该库分为许多模块，包括：

- angles: commons-numbers-angle 角度相关的工具
- arrays: commons-numbers-arrays 数组工具
- combinatorics: commons-numbers-combinatorics 组合工具，如阶乘和二项式系数
- complex numbers: commons-numbers-complex 用于处理复数
- core arithmetic: commons-numbers-core 基本工具
- fields: commons-numbers-field
- fractions: commons-numbers-fraction 处理分数，如有理数
- gamma functions: commons-numbers-gamma Gamma 函数
- primes: commons-numbers-primes 质数
- quaternions: commons-numbers-quaternion
- root finding: commons-numbers-rootfinder

## Core

commons-numbers-core 提供一个通用功能。

`Addition` 和 `Multiplication` 提供算术加法和乘法运行的通用类型定义。`NativeOperators` 对它们进行扩展，并添加了高性能运算符。这些接口由 Field 模块使用。

`ArithmeticUtils`  提供算术功能，如整数的指数，unsigned 除、余、最大公约数、最小公倍数等。这些函数被 `Faction` 模块使用。

- `Norm` 类提供范数实现

这些实现使用扩展精度的方法提高结果的总体准确性，代价是提高了一些计算量。欧几里得实现通过缩放避免可能的溢出。

```java
double x = Norm.EUCLIDEAN.of(3, -4);                              // 5
double y = Norm.MANHATTAN.of(3, -4, 5);                           // 12
double z = Norm.MAXIMUM.of(new double[]{3, -4, 5, -6, -7, -8});   // 8

double big = Double.MAX_VALUE * 0.5;
double length = Norm.EUCLIDEAN.of(big, big, big);
// length == Math.sqrt(0.5 * 0.5 * 3) * Double.MAX_VALUE
```

- `Sum` 提供准确的浮点数加和和线性组合

`Sum` 类使用技术减少标准浮点运算的舍入错误，提高结果的整体准确性，代价时增加了计算次数。

`Sum` 可以将数字和数字的乘积相加，如果结果是有限的，则返回高精度值，否则返回标准的 IEEE754 错误。

```java
double sum1 = Sum.create().add(1)
                          .addProduct(3, 4)
                          .getAsDouble();
double sum2 = Sum.of(1).addProduct(3, 4)
                       .getAsDouble();
double sum3 = Sum.ofProducts(new double[] {3, 4}, new double[] {5, 6})
                 .getAsDouble();
// sum1 == sum2 == 13.0
// sum3 == 3 * 5 + 4 * 6

Sum.of(1, 2, Double.NaN).getAsDouble()                 // NaN
Sum.of(1, 2, Double.NEGATIVE_INFINITY).getAsDouble()   // -inf
```

`Sum` 实现提供高达 106 bit 的浮点有效性。但是，该有效值被分成两个 `double` 值，将第二个 `double` 作为指数，可以分隔超过 $2^{53}$ 的浮点数：

```java
double x1 = 1e100 + 1 - 2 - 1e100;
double x2 = Sum.of(1e100, 1, -2, -1e100).getAsDouble();
// x1 == 0.0
// x2 == -1.0
```

`Sum` 支持减法操作：

```java
double x1 = 1e100 + 1 - 2 - 1e100;
Sum s1 = Sum.of(1e100, 1);
Sum s2 = Sum.of(2, 1e100);
double x2 = s1.subtract(s2).getAsDouble();
// x1 == 0.0
// x2 == -1.0
```

### Precision

`Precision` 类提供浮点数的相对、绝对和 unit-least-precision (ULP) 比较。相对 $\epsilon$ 比较：
$$
\frac{|a-b|}{\max(|a|,|b|)}\le\epsilon
$$
使用 ULP 参数进行比较，指定两个数字之间允许的距离。如果 `a` 和 `b` 之间的距离小于 `(ulp-1)`。对 binary 相等性测试，`ulp=0`。

> [!NOTE]
>
> [ULP](https://www.intel.com/content/www/us/en/docs/programmable/683242/current/unit-in-the-last-place.html ) 两个连续浮点数之间的距离，为 $2^{-23}$，近似为 $1.192093e^{-7}$。IEEE 舍入最大误差为 0.5 ULP，

```java
// Default allows no numbers between
Precision.equals(1000.0, 1000.0)                              // true
Precision.equals(1000.0, 1000.0 + Math.ulp(1000.0))           // true
Precision.equals(1000.0, 1000.0 + 2 * Math.ulp(1000.0))       // false

// Absolute - tolerance is floating-point
Precision.equals(1000.0, 1001.0)                          // false
Precision.equals(1000.0, 1001.0, 1.0)                    // true
Precision.equals(1000.0, 1000.0 + Math.ulp(1000.0), 0.0) //true(but not binary equal)

// ULP - tolerance is integer
Precision.equals(1000.0, 1001.0)                              // false
Precision.equals(1000.0, 1001.0, 1)                           // false
Precision.equals(1000.0, 1000.0 + 2 * Math.ulp(1000.0), 1)    // false
Precision.equals(1000.0, 1000.0 + 2 * Math.ulp(1000.0), 2)    // true  (n - 1) numbers between
Precision.equals(1000.0, 1000.0 + 3 * Math.ulp(1000.0), 2)    // false

// Relative
Precision.equalsWithRelativeTolerance(1000.0, 1001.0, 1e-6)   // false
Precision.equalsWithRelativeTolerance(1000.0, 1001.0, 1e-3)   // true
```



## Combinatorics

commons-numbers-combinatorics 提供组合相关工具，如阶乘和二项式系数。

- 二项式系数 $\binom{n}{k}$ 可以计算为 `long`, `double` 或自然对数

```java
long   bc1 = BinomialCoefficient.value(63, 33);
double bc2 = BinomialCoefficientDouble.value(1029, 514);
double bc3 = LogBinomialCoefficient.value(152635712, 125636);
// bc1 == 7219428434016265740
// bc2 ~ 1.429820686498904e308
// bc3 ~ 1017897.199659759
```

- 阶乘 $n!$ 同样可以计算为 `long`, `double` 或自然对数

```java
long   f1 = Factorial.value(15);
double f2 = Factorial.doubleValue(170);
double f3 = LogFactorial.create().value(Integer.MAX_VALUE);
// f1 == 1307674368000
// f2 == 7.257415615307999e306
// f3 == 4.3996705655378525e10
```

> [!NOTE]
>
> 不管是计算二项系数还是阶乘，如果超出类型范围，对 `long` 抛出 `ArithmeticException`，对 `double` 返回 `Double.POSITIVE_INFINITY`。自然对数则没有问题。

- 创建 `LogFactorial` 可以设置缓存，在创建时会计算设置数以下的所有阶乘。

当可能需要重复计算一系列值的阶乘时，可以使用该功能。

```java
LogFactorial lf = LogFactorial.create().withCache(50);
```

缓存外的值使用 Gamma 模块中的 `LogGamma.value(n+1.0)` 计算，这样可以避免重复创建 `LogFactorial` 实例。

- 二项式系数 $\binom{n}{k}$ 定义的组合可以使用 `Combinations` 类迭代

```java
Combinations.of(4, 2).iterator().forEachRemaining(c -> System.out.println(Arrays.toString(c)));
```

```
[0, 1]
[0, 2]
[1, 2]
[0, 3]
[1, 3]
[2, 3]
```

- `Combinations` 迭代组合的顺序基于输入数组相反顺序的自然排序。选择合适的 `Combination` 可以将该顺序应用于任意数组，例如

```java
List<int[]> list = Arrays.asList(new int[][] {
    {3, 4, 5},
    {3, 1, 5},
    {3, 2, 5},
    {4, 2, 4},
});
list.sort(Combinations.of(6, 3).comparator());
list.forEach(c -> System.out.println(Arrays.toString(c)));
```

`list` 没有数组长度为 3，最大值为 5，所以采用了 $\binom{6}{3}$ 组合。输出：

```
[4, 2, 4]
[3, 1, 5]
[3, 2, 5]
[3, 4, 5]
```

- `Stirling`


## 参考

- https://commons.apache.org/proper/commons-numbers/index.html