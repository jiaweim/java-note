# 回归



## 简单回归

`SimpleRegression` 提供普通最小二乘回归（ordinary least square regression），只有一个自变量：
$$
y=mx+b
$$
可以获得截距和斜率的标准差，以及方差分析、r 平方和 Pearson's r 统计量。

观测值 $(x,y)$ 可以一次一个地添加到模型，也可以提供二维数组。观测值不存在在内存，因此不限制添加到模型的观测值数量。

**注意事项：**

- 当观测值少于 2 个，或者 x 值没有变化（所有 x 值相同），所有统计数据都返回 `NaN`。拟合二元回归模型至少需要 2 个具有不同 x 的观测值；
- 统计量的 getter 方法总是返回当前数据集的计算值，即可以一边添加数据，一边获得更新后的统计量，不需要创建新实例。没有更新所有统计量的方法，每个 getter 方法都会执行必要的计算以返回请求的统计量；
- 将 `false` 传递给 `SimpleRegression(boolean)` 表示不要截距项，此时 `hasIntercept` 返回 false，拟合模型时也不考虑截距，`getIntercept()` 返回 0；
- `SimpleRegression` 类不是线程安全的。

**实现：**

- 随着观测值被添加到模型，x 值加和、y 值加和、xy 加和以及 x 和 y 与各自平均值的差值平方和使用 “Algorithms for Computing the Sample Variance: Analysis and Recommendations”, Chan, T.F., Golub, G.H., and LeVeque, R.J. 1983, American Statistician, vol. 37, pp. 242-247 中的公式进行更新，Weisberg, S. “Applied Linear Regression”. 2nd Ed. 1985 中引用了该公式。所有统计量都根据这些加和计算而来
- 统计推断（置信区间、参数显著性水平）基于假设：模型中的观测值来自二元正态分布

### 一次添加一个数据拟合模型

1. 实例化回归实例，添加数据点

```java
regression = new SimpleRegression();
regression.addData(1d, 2d);
// 此时只有一个观测值
// 所有回归统计量返回 NaN

regression.addData(3d, 3d);
// 此时有两个观测值
// 可以计算斜率和截距，但是推断统计量依然返回 NaN

regression.addData(3d, 3d);
// 现在可以计算所有统计量
```

2. 基于目前添加的数据计算统计量

```java
// 回归线截距
System.out.println(regression.getIntercept());
// 回归线斜率
System.out.println(regression.getSlope());
// 斜率标准差
System.out.println(regression.getSlopeStdErr());
```

3. 使用回归模型预测新 x 值的 y 值

```java
// 显示 x = 1.5 时的预测 y 值
System.out.println(regression.predict(1.5d)
```

可以继续添加数据，随后的 getXXX 会包含新添加的数据。

### 使用二维 double 数组拟合模型


实例化 `SimpleRegression` 并加载数据集：

```java
double[][] data = { { 1, 3 }, {2, 5 }, {3, 7 }, {4, 14 }, {5, 11 }};
SimpleRegression regression = new SimpleRegression();
regression.addData(data);
```

根据数据拟合回归模型：

```java
// 显示回归线截距
System.out.println(regression.getIntercept());
// 显示回归线斜率
System.out.println(regression.getSlope());
// 显示斜率的标准差
System.out.println(regression.getSlopeStdErr());
```

可以继续添加更多数据，甚至可以添加另一个 `double[][]` 数组，随后的 getXXX 将包含额外添加的数据。

### 禁用常数项

实例化 `SimpleRegression` 并加载数据集：

```java
double[][] data = { { 1, 3 }, {2, 5 }, {3, 7 }, {4, 14 }, {5, 11 }};
// 参数为 false，表示截距为 0
SimpleRegression regression = new SimpleRegression(false);
regression.addData(data);
```

根据数据拟合回归模型：

```java
// 回归线截距，因为约束了常数项，所以返回 0
System.out.println(regression.getIntercept());

// 回归线斜率
System.out.println(regression.getSlope());

// 斜率标准差
System.out.println(regression.getSlopeStdErr());

// 返回 Double.NaN, 截距保持为 0
System.out.println(regression.getInterceptStdErr() );
```

约束常数项要谨慎。

## 多元线性回归

`OLSMultipleLinearRegression`, `GLSMultipleLinearRegression` 和 `MillerUpdatingRegression` 实现最小二乘回归拟合，线性模型如下：
$$
Y = X\times b+\mu
$$

其中 Y 是 n 维回归因变量（**regressand**），X 是 `[n,k]` 矩阵，其中 k 个 columns 值称为自变量（**regressor**）。b 是 k 维的回归参数，$\mu$ 是 n 维误差项或残差项（**error terms**, or **residuals**）。

- `OLSMultipleLinearRegression` 提供普通最小二乘（Ordinary Least Square, OLS） 实现；
- `GLSMultipleLinearRegression` 提供广义最小二乘实现；
- `MillerUpdatingRegression` 提供 OLS 的流式实现。

相关算法和公式可参考 Javadoc。

`OLSMultipleLinearRegression` 模型的数据可以是单个 double[] 数组，由数据 row 拼接成的一维数组，每个 Y 后跟着对应的 X 值；也可以使用 `double[][]`，每一个 row 对应一个 `double[]`。

`GLSMultipleLinearRegression` 模型还需要一个代表误差项的协方差矩阵 `double[][]`。参考 `AbstractMultipleLinearRegression#newSampleData(double[],int,int)`, `OLSMultipleLinearRegression#newSampleData(double[], double[][])` 和`GLSMultipleLinearRegression#newSampleData(double[],double[][],double[][])`。

`MillerUpdatingRegression` 模型实现 `UpdatingMultipleLinearRegression` 接口，它提供了类似 `OLSMultipleLinearRegression` 添加数据的方法。与 `StorelessUnivariateStatistics` 相似，`UpdatingMultipleLinearRegression` 不存储观测数据，因此不限制数据量。

**使用注意事项：**

- 在调用 `newSample`, `newX`, `newY` 或 `newCovariance` 方法时会验证数据，当输入数组维度不匹配，或没有足够的数据来估计模型时，抛出 `IllegalArgumentException`；
- 回归模型默认包含截距。所以，上面的矩阵 X 包含一个初始为 1 的 row。提供给 `newX` 或 `newSample` 的 X 数据不应该包含这一列，数据加载方法会自动创建。要取消截距项，可以将 `noIntercept` 属性设置为 `true`；
- 多元线性回归类都不是线程安全。

下面是一些示例。

### OLS 回归

初始化 OLS 回归对象，加载数据集：

```java
OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
double[] y = new double[]{11.0, 12.0, 13.0, 14.0, 15.0, 16.0};
double[][] x = new double[6][];
x[0] = new double[]{0, 0, 0, 0, 0};
x[1] = new double[]{2.0, 0, 0, 0, 0};
x[2] = new double[]{0, 3.0, 0, 0, 0};
x[3] = new double[]{0, 0, 4.0, 0, 0};
x[4] = new double[]{0, 0, 0, 5.0, 0};
x[5] = new double[]{0, 0, 0, 0, 6.0};          
regression.newSampleData(y, x);
```

获取回归参数：

```java
double[] beta = regression.estimateRegressionParameters();       
double[] residuals = regression.estimateResiduals();
double[][] parametersVariance = regression.estimateRegressionParametersVariance();
double regressandVariance = regression.estimateRegressandVariance();
double rSquared = regression.calculateRSquared();
double sigma = regression.estimateRegressionStandardError();
```

### GLS 回归

初始化 GLS 回归对象，加载数据集：

```java
GLSMultipleLinearRegression regression = new GLSMultipleLinearRegression();
double[] y = new double[]{11.0, 12.0, 13.0, 14.0, 15.0, 16.0};
double[][] x = new double[6][];
x[0] = new double[]{0, 0, 0, 0, 0};
x[1] = new double[]{2.0, 0, 0, 0, 0};
x[2] = new double[]{0, 3.0, 0, 0, 0};
x[3] = new double[]{0, 0, 4.0, 0, 0};
x[4] = new double[]{0, 0, 0, 5.0, 0};
x[5] = new double[]{0, 0, 0, 0, 6.0};          
double[][] omega = new double[6][];
omega[0] = new double[]{1.1, 0, 0, 0, 0, 0};
omega[1] = new double[]{0, 2.2, 0, 0, 0, 0};
omega[2] = new double[]{0, 0, 3.3, 0, 0, 0};
omega[3] = new double[]{0, 0, 0, 4.4, 0, 0};
omega[4] = new double[]{0, 0, 0, 0, 5.5, 0};
omega[5] = new double[]{0, 0, 0, 0, 0, 6.6};
regression.newSampleData(y, x, omega); 
```

### 流式回归

初始化流式 OLS 回归对象，加载数据集：

```java
// 创建包含 3 个自变量和 1 个截距项的流式回归对象
MillerUpdatingRegression regression = new MillerUpdatingRegression(3, true); 

// 添加一个观测值
double[] x = {1.0, 1.0, 1.0};
double y = {1.0};
instance.addObservation(x, y);

// 一次添加两个观测值
double[][] xMult = {{2.0, 4.0, 5.0}, {1.4, 2.4, 2.1}};
double[] yMult = {2.0, 8.0};
instance.addObservations(xMult, yMult);

// 添加更多观测值，都不保存在内存
```

获取回归参数：

```java
RegressionResults result = regression.regress();
double[] parameters =  result.getParameterEstimates();
```

由于该模型包含截距，因此 `parameters[0]` 为截距的估计值。`parameters[1]`, `[2]` 和`[3]` 为三个自变量的回归系数估计自豪。

估计参数的标准差（顺序同上）：

```java
double[] stdErrs = result.getStdErrorOfEstimates();
```

R-squares, SSE, MSE:

```java
double Rsquare = result.getRSquared();
double MSE = result.getMeanSquareError();
double SSE = result.getErrorSumSquares();
```

参数 1 和 2 的协方差：

```java
double cov = result.getCovarianceOfParameters(1, 2) 
```

## 参考

- 