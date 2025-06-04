# Least squares

2025-02-10 ⭐⭐
@author Jiawei Mao

***
## 简介

最小二乘通过最小化特定形式的损失函数拟合参数模型。即找到合适的参数 $p_k$，使得损失函数最小化。损失函数定义：
$$
J=\sum_iw_i(o_i-f_i)^2
$$
其中，$w_i$ 为权重，$o_i$ 为观测值，$f_i=f_i(p)$ 为模型计算值。$r_i=o_i-f_i$ 为残差，用于量化观测值与模型计算的理论值之间的偏差。

目前有两个用于最小二乘的引擎：

- Gauss-Newton
- Levenberg-Marguardt

## LeastSquaresBuilder 和 LeastSquaresFactory

为了解决最小二乘拟合问题，用户需要提供：

- 模型 $f(p)$ 及其雅可比矩阵实现 $\frac{\partial f}{\partial p}$，最好的方法是实现 `MultivariateJacobianFunction`

- 观测值（目标）：$o$
- 参数初始值：$s$
- (可选)参数验证器，`ParameterValidator` 的实现
- (可选)样本权重，$w$，默认为 1.0
- 最大迭代次数
- 模型评估的最大次数，对 Levenberg-Marquardt 可能与迭代次数不同
- 收敛标准，[ConvergenceChecker](https://hipparchus.org/apidocs/org/hipparchus/optim/ConvergenceChecker.html) 实现

以上元素可以提供给  [LeastSquaresProblem](https://hipparchus.org/apidocs/org/hipparchus/optim/nonlinear/vector/leastsquares/LeastSquaresProblem.html) 接口的实现，但是一一设置很麻烦，因此 Hipparchus 提供了辅助类：

- `LeastSquaresBuilder` 
- `LeastSquaresFactory`。

`LeastSquaresBuilder` 适合逐步构建，即首先创建一个空的 builder，然后调用 `start`, `target`, `model` 等方法逐步配置，配置完成后，调用 `build` 创建 `LeastSquaresProblem`。

factory 适合于所有元素都一次，调用 `LeastSquaresFactory.create` 一次就能创建 `LeastSquaresProblem` 的场景。

## 模型函数

最小二乘引擎使用模型参数评估模型 $f(p)$。因此它是一个多元函数（取决于 $p_k$），并且是一个矢量（具有多个组分 $f_i$）。对每个观测值 $o_i$ 必须只有一个模型函数 $f_i$。为了确保问题定义良好，参数数量必须小于观测值数量。如果无法确保这一点，底层线性代数运算可能会遇到奇异矩阵，导致引擎抛出异常。

由于最小二乘引擎需要使用模型函数创建雅可比矩阵，因此模型函数值及其相对参数 $p_k$ 的导数都必须可用。有两种方法提供这些信息：

- 提供一个 `MultivariateVectorFunction` 用于计算组分值，以及一个 `MultivariateMatrixFunction` 计算组分相对于参数的导数（即雅可比矩阵）；
- 也可以提供一个 `MultivariateJacobianFunction` 同时计算组件值及其导数 

第一种方案适合计算量不大的模型，因为它允许使用更模块化的代码，每种类型的计算都使用一种方法。第二种方案适合计算量大的模型，一次性计算值和导数可以节省大量工作。

`MultivariateVectorFunction`, `MultivariateMatrixFunction` 和 `MultivariateJacobianFunction` 接口中 `value` 方法的 `point` 参数包含参数 $p_k$。其中值为模型分量 $\text{model}_i$ 的值，导数是模型分量相对参数的导数 $\frac{\partial\text{model}_i}{\partial p_k}$。

对如何计算模型值和导数没有任何要求。对复杂情况可以用`DerivativeStructure` 类辅助计算解析导数，但不强制要求，API 只要求导数为包含 double 值的雅可比矩阵。

另外，builder 和 factory 都提供 lazy 求值功能。该功能将对模型函数的调用推迟到引擎真正需要它们的时候，可以减少计算模型值也雅克比矩阵的次数。但是，只有模型函数本身分离时才行，对应上述第一种方案。在 builder 或 factory 中将 `lazyEvaluation` 设置为 true，同时将模型函数设置为 `MultivariateJacobianFunction` 会触发异常。



## 参数验证

在某些情况下，模型函数要求参数满足特定要求。例如，一个参数可能用于平方根，因此必须为正数；或者一个参数表示角度的正弦值，因此要在 -1 到 +1 之间；或者多个参数需要在单位圆内，因此它们的平方和必须小于 1。commons-math 提供的最小二乘求解器目前不支持对参数设置约束。有两种方法可以解决该问题。

这两种方式都通过设置 `ParameterValidator` 实现。如果设置了 `ParameterValidator`，那么输入值和雅可比模型函数的输入都采用 `ParameterValidator` 的输出值。

约束参数的一种方法是建立最小二乘求解器处理的参数与模型参数之间的连续映射。使用类似 `logit` 和 `sigmoid` 这样的映射函数可以将有限范围映射到无限实数。使用基于 `log` 和 `exp` 的映射函数 ，可以将半无限范围映射到无限实数。使用这样的映射，引擎处理的是无界参数，而数学模型看到的始终是正确范围的参数。不过在映射时，要小心处理导数和收敛状态。

约束参数的另一种方法是，当搜索点超出范围，直接将参数截断，不关心导数。只有解决方案位于 domain 内而非边界时该方案才有效。根据经验，只有当 domain 边界对应永远不会实现的不切实际的值（零距离、负质量等），才满足该条件。

## Tuning

builder 或 factory 创建 `LeastSquaresProblem` 需要为 solver 提供一些 tuning 参数。

最大迭代次数指引擎算法主循环次数，而最大评估次数指评估模型的次数。有些算法，如 Levenberg-Marquardt 有两个嵌套循环，迭代次数对应外层循环次数，而每个内循环都会进行一次评估。此时，评估次数大于迭代次数。其它算法，如 Gauss-Newton，只有一层循环，此时评估次数和循环次数相同。不管怎样，最大次数只是为了防止无限循环设置的保护措施，因此设置为多少并不重要，通常选择远大于预期次数的值，并将其赋值给 `maxIterations` 和 `maxEvaluations`。例如，如果最小二乘求解器通常在 50 次迭代内好到解决方案，则可以将最大值设置为 1000，以防止无限循环。如果最小二乘需要上百次评估，则将最大值设置为 10,000 甚至 1000,000  可能更保险，以避免在苛刻情况失败。对**这些最大数字进行精细调整通常毫无意义**，它们仅作为安全措施。

收敛性检查委托给 `optim` 包中的专用接口：`ConvergenceChecker`，参数化类型为用于最小二乘问题的特定 `Evaluation` 类或通用 `PointVectorValuePair` 类。每次检查收敛性时，都会提供最小二乘问题的前一次和当前评估，因此检查器可以比较它们以决定是否达到收敛要求。可用于最小二乘拟合的预定义收敛检查器实现有：

- `EvaluationRmsChecker`，仅使用归一化 cost（残差平方和的平方根，除以估计次数）
- `SimpleVectorValueChecker`，使用模型组件自身（非残差）
- `SimplePointChecker<PointVectorValuePair>`，使用参数

当然，也可以实现 `ConvergenceChecker` 接口自定义收敛性检查条件。

### SimpleVectorValueChecker

仅使用目标函数值实现 `ConvergenceChecker` 接口。

如果目标函数值之间的相对差异小于指定阈值，或者目标函数之间的绝对差异小于

## 优化引擎

使用 builder 或 factory 创建 `LeastSquaresProblem` 后，将其传递给优化引擎求解。有两个引擎专门用于最小二乘问题：Gauss-Newton 和 Levenberg-Marquardt。

为了提高可读性，建议使用 fluent 风格 API 构建和配置 optimizer。即先使用默认无参构造函数创建临时版本，然后使用 `withXxx` 方法进行配置。例如，设置 Levenberg-Marquardt，除了 cost-relative-tolerance 和 parameter-relative-tolerance，其它都采用默认值：

```java
LeastSquaresOptimizer optimizer = new LevenbergMarquardtOptimizer().
                                withCostRelativeTolerance(1.0e-12).
                                withParameterRelativeTolerance(1.0e-12);
```

再比如，设置 Gauss-Newton optimizer 并强制分解为 SVD（默认为 QR 分解）：

```java
LeastSquaresOptimizer optimizer = new GaussNewtonOptimizer().
    					withwithDecomposition(GaussNewtonOptimizer.Decomposition.QR);
```

## 求解

通过调用 optimizer 的 `optimize` 方法求解最小二乘问题 ：

```java
LeastSquaresOptimizer.Optimum optimum = optimizer.optimize(leastSquaresProblem);
```

`LeastSquaresOptimizer.Optimum` 类是一个专门的 `Evaluation`，提供检测评估次数和迭代次数的附加方法。主要方法从 `Evaluation` 继承，包含获取参数、cost、雅可比矩阵、RMS和协方差等的方法。

## 示例

下面演示如何找到已知半径的圆心，以便最好拟合观察到的 2D 点。这是 Junit 测试的一个简化版本，完整测试的半径和圆心都是拟合的，这里固定了半径。从 optimizer 角度看，参数是圆心位置 (x,y)，观察值是 2D 点到假定圆心的距离。

```java
final double radius = 70.0;
final Vector2D[] observedPoints = new Vector2D[]{
        new Vector2D(30.0, 68.0),
        new Vector2D(50.0, -6.0),
        new Vector2D(110.0, -20.0),
        new Vector2D(35.0, 15.0),
        new Vector2D(45.0, 97.0)
};
MultivariateJacobianFunction distancesToCurrentCenter = new MultivariateJacobianFunction() {
    @Override
    public Pair<RealVector, RealMatrix> value(RealVector point) {
        // 当前估计的圆心坐标
        Vector2D center = new Vector2D(point.getEntry(0), point.getEntry(1));

        RealVector value = new ArrayRealVector(observedPoints.length);
        RealMatrix jacobian = new Array2DRowRealMatrix(observedPoints.length, 2);
        for (int i = 0; i < observedPoints.length; i++) {
            Vector2D o = observedPoints[i];
            Vector2D diff = center.subtract(o); // 圆心到观测点的距离
            double modelI = diff.getNorm(); // 计算半径值
            value.setEntry(i, modelI);
            // 相对 p0=x 中心的导数
            jacobian.setEntry(i, 0, diff.getX() / modelI);
            // 相对 p1=y 中心的导数
            jacobian.setEntry(i, 1, diff.getY() / modelI);
        }
        return new Pair<>(value, jacobian);
    }
};

// 目标是使所有点位于距离中心指定半径内
double[] prescribedDistances = new double[observedPoints.length];
Arrays.fill(prescribedDistances, radius);

// 对最小二乘问题：建模半径应接近目标半径
LeastSquaresProblem problem = new LeastSquaresBuilder().
        start(new double[]{100.0, 50.0}).
        model(distancesToCurrentCenter).
        target(prescribedDistances).
        lazyEvaluation(false).
        maxEvaluations(1000).
        maxIterations(1000).
        build();
LeastSquaresOptimizer.Optimum optimum = new LevenbergMarquardtOptimizer().optimize(problem);
Vector2D fittedCenter = new Vector2D(optimum.getPoint().getEntry(0), optimum.getPoint().getEntry(1));
System.out.println("fitted center: " + fittedCenter.getX() + " " + fittedCenter.getY());
System.out.println("RMS: " + optimum.getRMS());
System.out.println("evaluations: " + optimum.getEvaluations());
System.out.println("iterations: " + optimum.getIterations());
```

总结：

1. 定义观测值

```java
final Vector2D[] points = new Vector2D[]{
        new Vector2D(30.0, 68.0),
        new Vector2D(50.0, -6.0),
        new Vector2D(110.0, -20.0),
        new Vector2D(35.0, 15.0),
        new Vector2D(45.0, 97.0)
};
```

2. 定义函数

定义 `MultivariateVectorFunction` 计算函数值，定义 `MultivariateMatrixFunction` 计算倒数；或者定义 `MultivariateJacobianFunction` 同时计算函数值和倒数：

```java
```



## 参考

- https://commons.apache.org/proper/commons-math/userguide/leastsquares.html
- https://hipparchus.org/hipparchus-optim/leastsquares.html