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

## 分布

### 指数分布

指数分布的概率密度函数：

$$
f(x)=\begin{cases}
    \lambda e^{-\lambda x}, &\text{当 } x>0 \text{ 时}\\
    0, &\text{当 } x\le 0 \text{ 时}
\end{cases}
$$

指数分布的分布函数：

$$
\begin{aligned}
F(x)&=\int_{-\infty}^x f(t)dt\\
    &=\begin{cases}
        0, &\text{当} x\le 0 \text{时}\\
        1-e^{-\lambda x}, &\text{当} x>0 \text{时}
    \end{cases}
\end{aligned}
$$

其中 $\lambda^{-1}$ 就是均值。

指数分布最常见的一个场合是**寿命分布**。指数分布描述了**无老化**时的寿命分布，但“无老化”是不可能的，因此只是一种近似。对一些寿命长的元件，在初期老化现象较小，此时指数分布比较确切地描述其寿命分布。

比如人的寿命，一般在 50 或 60 之前，生理上老化的因素是次要的，若排除意外情况，人的寿命分布在这个阶段也接近指数分布。

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

