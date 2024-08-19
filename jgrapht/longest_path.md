# 最长路径

## 简介

给定一个加权有向无环图（Weighted Directed Acyclic Graph, W-DAG）以及 source vertex `s`，找到它与 graph 中其它 vertex 的最长路径。

graph 的最长路径不像最短路径那样简单，因为最长路径问题没有最优结构性质。对一般的 graph，最长路径问题属于 NP-hard。但是对 DAG，最长路径问题有线性时间解。其思想类似 DAG 中最短路径的线性时间解。