# 最优化

## 概述

optimization 包提供优化目标函数或损失函数的算法。这个包分成几个子包，用于不同类型的函数和算法：

- univariate 包处理**一元标量函数**（univariate scalar function）；
- linear 包处理具有线性约束的**多元向量线性函数**（multivariate vector linear function）；
- direct 使用直接搜索方法（即不使用导数）处理**多元标量函数**（multivariate scalar function）；
- general 使用导数处理多元标量函数或向量函数；
- fitting 包使用一元实函数处理曲线拟合。

顶层 optimization  包为 sub-packages 的优化算法提供公共接口。主接口定义 optimizers 和收敛检查器。该包及其子包提供的算法优化的函数是 analysis 包中定义的函数的子集，即实值函数和向量值函数。这些函数被称为目标函数（objective function）。当目标是最小化函数值，则通常称为损失函数（cost function），不过这里统一用目标函数这个命名。

目标类型，即最小化和最大化，由枚举类 `GoalType` 定义，只有两个值：`MAXIMIZE` 和`MINIMIZE`。

**优化器（optimizer）**是通过改变输入变量来最小化或最大化目标函数的算法。由 4 个接口定义 optimizer 的共同行为，每个接口对应一个支持的目标函数类型：

- univariate 包处理一元标量函数；
- linear 包处理具有线性约束的多元向量线性函数；
- direct 包使用直接搜索方法处理多元标量函数；
- general 包使用导数处理多元标量或向量函数；
- fitting 包使用一元实值函数处理曲线拟合。

尽管只支持四种类型的 optimizers，但使用 `LeastSquaresConverter` 辅助类可以将不可微的多元向量函数转换为不可微的多元实函数，转换后的函数可以使用任何实现 `MultivariateOptimizer` 接口的类优化。

对支持的四种类型的所有 optimizers，都有一个特殊实现，它包装了一个经典 optimizer 实现，以提供多启动特性。**多启动 feature** 以不同的起点多次调用底层 optimizer，并返回找到的最佳或所有优化结果（optimum）。这是查找全局极值防止陷入局部极值的经典方法。

## 一元函数

`UnivariateOptimizer` 用于查找一元实值函数 `f` 的最小值。

这类算法的用法与 analysis 包中查找 root 的算法类似。主要区别在于，将 `solve` 方法替换为 `optimize` 方法。

## 线性

这个包提供 George Dantzig 的 simplex 算法，用于解决线性等式或不等式约束的线性优化问题。

## Direct 方法

直接搜索方法只使用损失函数，它们不需要导数，也不试图计算导数的近似值。根据 Margaret H. Wright 1996 年的一篇论文（Direct Search Methods: Once Scorned, Now Respectable），当无法计算导数（有噪音，不可预测的不连续）或难以计算（复杂、计算成本）时，使用直接搜索方法更合适。在第一个情况，需要的不是最优解，而是一个不太差的点。在后一种情况，需要最优解，但不能在合理时间内找到。对两种情况，直接搜索都很有用。

基于 Simplex 的直接搜索方法通过更新 simplex 的 vertices (n 维空间的 n+1 个点的集合)，比较不同点的损失函数值。

其实例可以在单启动或多启动模式下构建。多启动减小陷入局部最优的可能，也可以用来验证算法的收敛性。在多启动模式，`minimizes` 返回所有起点得到的最小值，而 `etMinima` 返回每个起点得到的最小值。

`direct` 包提供了四个 solvers：

- univariate 处理一元标量函数；
- linear 包处理多元向量线性函数



## 参考

- https://hipparchus.org/hipparchus-optim/old.html