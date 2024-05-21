# 线性代数

## 简介

Hipparchus 的线性代数实现支持矩阵（稀疏和密集矩阵）和向量操作。提供基本数学运算和分解算法，可用于最小二乘求解线性模型。

## Real Matrices

`RealMatrix` 接口表示存储实数的矩阵。支持以下基本矩阵运算：

- 矩阵的加法、减法和乘法；
- 标量加法和乘法；
- 转置；
- Norm 和 Trace；
- 向量操作。

例如：

```java
// 工厂方法：创建 2 行 3 列矩阵
double[][] matrixData = {{1d, 2d, 3d}, {2d, 5d, 3d}};
RealMatrix m = MatrixUtils.createRealMatrix(matrixData);

// 构造函数：创建 3 行 2 列矩阵
double[][] matrixData2 = {{1d, 2d}, {2d, 5d}, {1d, 7d}};
RealMatrix n = new Array2DRowRealMatrix(matrixData2);

// 两种语法都会 copy 输入的 double[][]

// 矩阵乘法
RealMatrix p = m.multiply(n);
assertEquals(p.getRowDimension(), 2);
assertEquals(p.getColumnDimension(), 2);

// 使用 LU decomposition 计算逆矩阵
RealMatrix pInverse = new LUDecomposition(p).getSolver().getInverse();
```

`RealMatrix` 接口的实现三个：

- `Array2DRowRealMatrix` 和 `BlockRealMatrix` 用于密集矩阵，后者适合大型矩阵（>50）；
- `SparseRealMatrix` 用于稀疏矩阵。

![image-20240514191748285](./images/image-20240514191748285.png)

## Real vectors

`RealVector` 接口表示实数向量。支持以下矩阵运算：

- 矩阵的加法、减法和乘法；
- 标量加法和乘法；
- 转置；
- Norm 和 Trace；
- 向量操作。

`RealVectorFormat` 类以自定义文本格式处理向量的输入/输出。

## Solving linear systems



