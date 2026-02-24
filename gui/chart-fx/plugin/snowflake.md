# Koch snowflake

2023-08-08, 10:06
****
## 简介

科赫雪花（Koch snowflake），也称为 Koch curve, Koch star, Koch island，是 一种分形曲线，是最早被描述的分形之一。该曲线在 1904 年由瑞典数学家 Helge von Koch 在论文 "On a Continuous Curve Without Tangents, Constructible from Elementary Geometry" 中提出。

科赫雪花可以通过迭代构建：

- 先画一个等边三角形
- 在上一个图形的每条边上增加一个更小的等边三角形
- 重复该过程

如下图所示：
![|200](Pasted%20image%2020230808095250.png)
在构建雪花的过程中，多边形面积收敛于原三角形的 $\frac{8}{5}$ 倍，多边形周长无限增加。

科赫雪花为连续曲线，无法绘制其中任意点的切线。与早期 Weierstrass 函数只有纯解析证明不同，科赫雪花函数可以用集合表示。

## 构建

科赫雪花可以从一个等边三角形开始构建，然后递归地修改每个线段：

1. 将线段等分为三段
2. 以步骤 1 的中间线段为底，向外绘制一个等边三角形
3. 从步骤 2 删除三角形的底

Helge von Koch 最初描述的科赫曲线是用原始三角形的三条边中的一条来构造，换句话说，三条科赫曲线构成科赫雪花。

## 参考

- [https://en.wikipedia.org/wiki/Koch_snowflake](https://en.wikipedia.org/wiki/Koch_snowflake)
- http://piziadas.com/en/2012/03/fractales-recursivos-curva-de-koch-java.html
