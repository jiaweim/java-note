# t-test

## 理论

当两个总体方差 $\sigma_1^2$ 和 $\sigma_2^2$ 未知，而 $n_1$ 和/或 $n_2<30$，其平均值的差值不再服从正态分布，而是 t 分布，因此用 t 检验。

t 值的计算公式为：
$$
t=\frac{(\overline{x}_1-\overline{x}_2)-(\mu_1-\mu_2)}{s_{\overline{x}_1-\overline{x}_2}}
$$
 在假设 $H_0$: $\mu_1=\mu_2=\mu$ 的情况下，t 值的计算公式为：
$$
t=\frac{(\overline{x}_1-\overline{x}_2)}{s_{\overline{x}_1-\overline{x}_2}}
$$
在两个样本平均值的比较中，根据试验设计不同，又分为两类情况，即：

- 成组数据平均值比较的 t 检验
- 成对数据平均数比较的 t 检验

二者都是检验两个样本平均值 $\overline{x}_1$ 和 $\overline{x}_2$ 所属总体平均数 $\mu_1$ 和 $\mu_2$ 是否相等的检验方法。这两类检验经常被用于比较生物学研究中不同处理效应的差异显著性。

## 成组数据平均数比较的 t 检验

成组数据（pooled data）是两个样本的各个变量从各自总体中抽取，两个抽样样本彼此独立。这样，无论两个样本容量是否相同，所得数据皆为成组数据。两组数据以组平均数进行比较，来检验其差异的显著性。

不管两个总体方差 $\sigma_1^2$ 和 $\sigma_2^2$ 已知或未知，当两个样本**均为大样本**时，采用 $\mu$ 检验法检验两组平均值的差异显著性。

当总体方差  $\sigma_1^2$ 和 $\sigma_2^2$ 未知，且两个样本为小样本时（$n_1<30, n_2 < 30$），用 t 检验，可以分为以下三种情况。

### 1. 方差相同

要求方差 $\sigma_1^2=\sigma_2^2=\sigma^2$。

首先，用样本方差 $s_1^2$ 和 $s_2^2$ 进行加权计算平均数差值的方差 $s_e^2$，作为对 $\sigma^2$ 的估计，计算公式为：
$$
s_e^2=\frac{s_1^2(n_1-1)+s_2^2(n_2-1)}{(n_1-1)(n_2-1)}
$$
其次，计算两样本平均数差值的标准误为：
$$
s_{\overline{x}_1-\overline{x}_2}=\sqrt{\frac{s_e^2}{n_1}+\frac{s_e^2}{n_2}}
$$
当 $n_1=n_2=n$ 时，上式变为：
$$
s_{\overline{x}_1-\overline{x}_2}=\sqrt{\frac{2s_e^2}{n}}
$$
此时，t 服从自由度为 $df=(n_1-1)+(n_2-1)=n_1+n_2-2$ 的 t 分布。

**例 1** 用高蛋白和低蛋白两种饲料饲养 1 月龄大白鼠，在 3 个月时，测定两组大白鼠的增重量（g），两组数据分别为：

高蛋白组：134, 146, 106, 119, 124, 161, 107, 83, 113, 129, 97, 123;

低蛋白组：70, 118, 101, 85, 107, 132, 94.

试问两种饲料饲养的大白鼠增重是否有差别？

本题 $\sigma_1^2$ 和 $\sigma_2^2$ 未知，经 F 检验 $\sigma_1^2=\sigma_2^2$，且两样本均为小样本，用 t 检验；由因事先不知道两种饲料饲养的大白鼠增重孰高孰低，故用双尾检验。

1) 假设 $H_0$: $\mu_1=\mu_2$，即两种饲料饲养的大白鼠增重没有差别；$H_A$: $\mu_1\ne\mu_2$。
2) 取显著性水平 $\alpha=0.05$。

计算 t 统计量：

```java
TTest tTest = new TTest();
double[] sample1 = new double[]{134, 146, 106, 119, 124, 161, 107, 83, 113, 129, 97, 123};
double[] sample2 = new double[]{70, 118, 101, 85, 107, 132, 94};
double v = tTest.homoscedasticT(sample1, sample2); // 1.9156630398579795
```

自由度 $df=12+7-2=17$，查表得到 $t_{0.05}=2.110$，由于 $|t|<t_{0.05}$，$P>0.05$，故接受 $H_0$，认为两种饲料饲养的大白鼠的增重没有显著差别。

也可以直接计算 p-value：

```java
TTest tTest = new TTest();
double[] sample1 = new double[]{134, 146, 106, 119, 124, 161, 107, 83, 113, 129, 97, 123};
double[] sample2 = new double[]{70, 118, 101, 85, 107, 132, 94};
double pValue = tTest.homoscedasticTTest(sample1, sample2);
System.out.println(pValue); // 0.07238315570866749
```

p-value 大于 0.05，也说明没有显著性差异。

### 2. 方差不同，样本数相同

通过 F 检验判断方差是否相同。

应用条件：$\sigma_1^2\ne\sigma_2^2$，但 $n_1=n_2=n$.

这种情况仍可以用 t 检验，其计算与总体方差相等的情况一样，只是 t 值服从自由度为 $df=n-1$ 的 t 分布。

**例 2** 两个小麦品种千粒重 (g) 的调查结果如下所示。

品种甲：50, 47, 42, 43, 39, 51, 43, 38, 44, 37;

品种乙：36, 38, 37, 38, 36, 39, 37, 35, 33, 37.

试检验两个品种的千粒重有无显著差异。

经 F 检验，得知两个品种千粒重的方差有显著不同，即 $\sigma_1^2\ne\sigma_2^2$；$n_1=n_2=10$，为小样本，用 t 检验。

又因事先不知道甲、乙两个品种千粒重孰高孰低，故使用双尾检验。

1. 假设：$H_0$: $\mu_1=\mu_2$，两个品种千粒重没有显著差异；$H_A$: $\mu_1\ne\mu_2$。
2. 取显著性水平 $\alpha=0.01$。

计算 t 值：

```java
double[] sample1 = new double[]{50, 47, 42, 43, 39, 51, 43, 38, 44, 37};
double[] sample2 = new double[]{36, 38, 37, 38, 36, 39, 37, 35, 33, 37};
TTest tTest = new TTest();
double tValue = tTest.t(sample1, sample2);
System.out.println(tValue); // 4.228036028677357
```

$df=10-1=9$，查表 $t_{0.01}=3.250$。由于 $|t|>t_{0.01}$，故在 0.01 显著性水平上否定 $H_0$，接受 $H_A$，认为两个品种千粒重有显著差异。因 $\overline{x}_1>\overline{x}_2$，故品种甲的千粒重显著高于品种乙。

计算 p-value 方式：

```java
double[] sample1 = new double[]{50, 47, 42, 43, 39, 51, 43, 38, 44, 37};
double[] sample2 = new double[]{36, 38, 37, 38, 36, 39, 37, 35, 33, 37};
TTest tTest = new TTest();
System.out.println(tTest.tTest(sample1, sample2));// 0.0013449647191229048
```

p-Value 小于 0.01，结论同上。

### 3. 方差不同，样本数不同

应用条件：$\sigma_1^2\ne\sigma_2^2$, $n_1\ne n_2$.

此时统计量 $\frac{\overline{x}_1-\overline{x}_2}{s_{\overline{x}_1-\overline{x}_2}}$ 不再服从 t 分布，因此只能进行近似的 t 检验。由于 $\sigma_1^2\ne \sigma_2^2$， 所以计算两个样本平均差值的标准误时不能使用加权方差，需要用两个样本方差 $s_1^2$ 和 $s_2^2$ 分别估计总体方差 $\sigma_1^2$ 和 $\sigma_2^2$，即：
$$
s_{\overline{x}_1-\overline{x}_2}=\sqrt{\frac{s_1^2}{n_1}+\frac{s_2^2}{n_2}}
$$
进行 t 检验时，需要计算 R 和 $df'$：
$$
R=\frac{s_{\overline{x}_1}^2}{s_{\overline{x}_1}^2+s_{\overline{x}_2}^2}
$$

$$
df'=\frac{1}{\frac{R^2}{n_1-1}+\frac{(1-R)^2}{n_2-1}}
$$

$$
t_{df'}=\frac{\overline{x}_1-\overline{x}_2}{s_{\overline{x}_1-\overline{x}_2}}
$$

其中 $t_{df'}$ 近似服从自由度为 $df'$ 的 t 分布。

**例 3** 测定冬小麦“东方红 3 号”的蛋白质含量（$）10 次，得 

## Hipparchus 实现

支持：

- one-sample 和 two-sample
- one-sided 和 two-sided
- paired 和 unpaired (for two-sample tests)
- Homoscedastic (等方差假设) 和 heteroscedastic（异方差）(for two-sample tests)
- 固定显著性水平或 p-values

检验统计量可用于所有检验。包含 "Test" 的方法执行检验，其它方法都返回 t 统计量。

在 "Test" 方法中，`double-` 方法返回 p-value；`boolean-` 方法执行固定显著性水平检验。

显著性水平通常在 0 到 0.5 之间，例如 95% 水平的检验使用 `alpha=0.05`。

检验的输入可以为 `double[]` 数组或 `StatisticalSummary` 实例。

