# 统计示例

## 排列组合

- 从 n 个元素中取 r ($1\le r\le n$) 个的不同**排列数**

$$
P^n_r=n(n-1)(n-2)\cdots(n-r+1)=\frac{n!}{r!}
$$

```java
long count = CombinatoricsUtils.factorial(4) / CombinatoricsUtils.factorial(2);
assertEquals(count, 12);
```

> 由于 long 限制，阶乘 `factorial` 要求参数不大于 20.

- 从 n 个元素中取 r ($1\le r\le n$) 个的不同**组合数**

$$
\binom{n}{r}=\frac{P^n_r}{r!}=\frac{n!}{r!(n-r)!}
$$

例如，从 4 个取

```java
long count = CombinatoricsUtils.binomialCoefficient(4, 2);
assertEquals(count, 6);
```

## 抽样分布

- 计算 20%, 25%, 50% 分位数

样本值：

```
122 126 133 140 145 145 149 150 157
162 166 175 177 177 183 188 199 212
```

计算：

```java

```

## 线性回归

###  一元线性回归

