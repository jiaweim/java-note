# oj! Algorithms

## 简介

ojAlgo 是一个开源的 Java 库，用于数学计算、线性代数和优化问题。它是进行数据科学、机器学习和科学计算的理想选择。

零依赖、功能丰富且性能卓越：

- **极致性能**：ojAlgo 是目前可用的最快的纯 Java 线性代数库。这一结论得到了最新的 Java Matrix Benchmark 结果的支持——这是一个由第三方独立进行的基准测试（非 ojAlgo 相关团队编写）。
- **优化工具**：提供包括线性规划（LP）、二次规划（QP）和混合整数规划（MIP）求解器在内的数学编程工具——同样为纯 Java 实现，零依赖。
- **灵活的数组类**：提供一系列“数组（array）”类，支持稀疏或密集存储，且容量不受限制。它们可用作一维、二维或 N 维数组，并支持多种数据类型，包括复数、有理数和四元数。此外，数组的内存可以分配在堆外（off-heap）或直接存储在文件中。ojAlgo 的线性代数模块正是构建在这些数组之上，因此速度极快且高效。
- **数据科学工具集**：提供不断扩充的数据科学实用工具，包括人工神经网络（ANN）、聚类算法，以及用于数据读取、写入和处理的相关工具。
- **其他实用功能**：包含时间序列分析、随机数生成、随机过程以及描述性统计等多种功能。

### 设计理念

- **专为 JVM 编写的代码**：不调用任何原生库（如 C 或 Fortran）。ojAlgo 本质上是一个 Java 库，但 Android、Kotlin、Scala、Groovy 和 Clojure 开发者也都在成功地使用它。
- **零依赖**：编译或执行代码除了需要 JDK 之外，不需要任何其他依赖。
- **具备高可扩展性的代码**：ojAlgo 之所以快，是因为它的资源利用率极高（对机器硬件感知），这也正是它能够轻松实现性能扩展（scaling up）的基础。
- **不断演进的 API**：ojAlgo 是一个成熟的代码库，但仍在持续演进。改进 ojAlgo 通常伴随着 API 的迭代——因此，未来的版本中任何内容都有可能发生变更。

## ojAlgo 基础

ojAlgo 的线性代数模块是其最大亮点，也是其它功能模块的基础。它的使用门槛极低，上手容易。

首先，介绍 ogAlgo 的底层模块，以及它的各种数组、向量和矩阵类。

这些结构在 `org.ojalgo.structure` 包中，特别是其中的 `Structure1D` 接口。该接口定义如下：

```java
package org.ojalgo.structure;

import static org.ojalgo.function.constant.PrimitiveMath.ZERO;

import java.util.ArrayList;
import java.util.List;

/**
 * A (fixed size) 1-dimensional data structure.
 *
 * @author apete
 */
@FunctionalInterface
public interface Structure1D {

    /**
     * The total number of elements in this structure.
     * <p>
     * You only need to implement this method if the structure can contain more than Integer.MAX_VALUE
     * elements.
     */
    default long count() {
        return this.size();
    }

    /**
     * The total number of elements in this structure.
     */
    int size();

}
```

核心方法为 `count()`，表示数据结构中包含的元素总数，作用类似 `java.util.Collection` 中的 `size()`，但返回 `long` 类型，这是因为 ojAlgo 中的某些数据结构支持容纳如此庞大数量的元素。





## 参考

- https://github.com/optimatika/ojAlgo
- https://ojalgo.org/
