- [IO](#io)
  - [CSV](#csv)
    - [EDGE_LIST](#edgelist)
    - [ADJACENCY_LIST](#adjacencylist)
    - [MATRIX](#matrix)
  - [DIMACS](#dimacs)
    - [SHORTEST_PATH](#shortestpath)
    - [MAX_CLIQUE](#maxclique)
    - [COLORING](#coloring)
  - [DOT](#dot)
    - [Undirected graph](#undirected-graph)
    - [Directed graphs](#directed-graphs)
    - [Attributes](#attributes)
    - [注释](#%e6%b3%a8%e9%87%8a)
  - [GML](#gml)
  - [graph6](#graph6)
  - [GraphML](#graphml)

# IO
## CSV

### EDGE_LIST

每行包含一个 edge，例如：
```
a,b
b,c
```
表示包含两个 edges 的 graph: a->b and b->c.

### ADJACENCY_LIST

每行包含一个 vertex 及其邻接表。第一个为vertex，余下为其 neighbors。
例如：
```
a,b
b,c,d
c,a,c,d
```
表示具有如下 edges 的 graph: a->b, b->c, b->d, c->a, c->c, c->d.

也可以混合 `EDGE_LIST` 和 `ADJACENCY_LIST` 样式，如下：
```
a,b
b,a
d,a
c,a,b
b,d,a
```

表示具有如下 edges 的 graph: a->b, b->a, d->a, c->a, c->b, b->d, b->a. 相同边出现多次表示 multi-graph。

如果设置了 `CSVFormat.Parameter.EDGE_WEIGHTS` 还可以使用权重 edge。此时在 target vertex 后面要加上权重值，如下所示：
```
a,b,2.0
b,a,3.0
d,a,2.0
c,a,1.5,b,2.5
b,d,3.3,a,5.5
```

### MATRIX 
用邻接矩阵表示 graph。每一行表示一个 vertex。如下：
```
0,1,0,1,0
1,0,0,0,0
0,0,1,0,0
0,1,0,1,0
0,0,0,0,0
```
表示具有五个 vertices 1,2,3,4,5 并包含如下 edges 的 graph: 1->2, 1->4, 2->1, 3->3, 4->2, 4->4.

如若设置了 `CSVFormat.Parameter.MATRIX_FORMAT_ZERO_WHEN_NO_EDGE`，则 0 被忽略，结果如下：
```
,1,,1,
1,,,,
,,1,,
,1,,1,
,,,,
```

设置权重值 `CSVFormat.Parameter.EDGE_WEIGHTS`，上面对应的权重版本如下：
```
,1.0,,1.0,
1.0,,,,
,,1.0,,
,1.0,,1.0,
,,,,
```

如果额外设置了 `CSVFormat.Parameter.MATRIX_FORMAT_ZERO_WHEN_NO_EDGE` 则整数 0 表示对应的 edge 不存在，double 0 表示 edge 存在，但权重为 0.

如果设置了参数 `CSVFormat.Parameter.MATRIX_FORMAT_NODEID`，则同事输出 node 的identifiers。如下所示：
```
,a,b,c,d,e
a,,1,,1,
b,1,,,,
c,,,1,,
d,,1,,1,
e,,,,,
```
在上例中，第一行包含 node 的 identifiers，下面的每一行包含对应 vertex。当使用 node identifiers时，打乱行之间的顺序也没问题：
```
,a,b,c,d,e
c,,,1,,
b,1,,,,
e,,,,,
d,,1,,1,
a,,1,,1,
```
对应的 graph 为: a->b, a->d, b->a, c->c, d->b, d->d.

## DIMACS

### SHORTEST_PATH
第 9th DIMACS implementation challenge 使用的格式。
结构如下：
```
 c <comments>
 p sp <number of nodes> <number of edges>
 a <edge source 1> <edge target 1>
 a <edge source 2> <edge target 2>
 a <edge source 3> <edge target 3>
 a <edge source 4> <edge target 4>    
```

权重版本如下：
```
a <edge source 1> <edge target 1> <edge_weight> 
```

### MAX_CLIQUE
第 2nd DIMACS implementation challenge 使用的格式。格式如下：
```
 c <comments>
 p edge <number of nodes> <number of edges>
 e <edge source 1> <edge target 1>
 e <edge source 2> <edge target 2>
 e <edge source 3> <edge target 3>
 e <edge source 4> <edge target 4>
```

权重版本如下：
```
e <edge source 1> <edge target 1> <edge_weight>
```

### COLORING
同 MAX_CLIQUE，只是第一行用 `col` 代替了 `edge`。

## DOT
[DOT](http://en.wikipedia.org/wiki/DOT_language) 是一种 graph description language。

### Undirected graph
定义格式如下：
```
 // The graph name and the semicolons are optional
 graph graphname {
     a -- b -- c;
     b -- d;
 }
```
以关键字 `graph` 开始，在大括号内定义 nodes，用连字符（--）定义nodes 之间的连接。

### Directed graphs
类似于 undirected graph，不过关键字为 `digraph`，用箭头替代连字符：
```
 digraph graphname {
     a -> b -> c;
     b -> d;
 }
```

### Attributes
在 graphs, nodes 和 edges 中可以插入任意属性。这些属性可用于设置 color, shape, line styles等。如下所示：
```
 graph graphname {
     // This attribute applies to the graph itself
     size="1,1";
     // The label attribute can be used to change the label of a node
     a [label="Foo"];
     // Here, the node shape is changed.
     b [shape=box];
     // These edges both have different line properties
     a -- b -- c [color=blue];
     b -- d [style=dotted];
     // [style=invis] hides a node.
}
```

### 注释
类似于 C 语言的注释。

## GML
GML (Graph Modeling Language) 用于支持网络数据的文本文件格式。`Graphlet`, `Pajet`, `yEd`, `LEDA` 和 `NetworkX` 等支持该格式。

实例：
```
graph
[
  node
  [
   id A
  ]
  node
  [
   id B
  ]
  node
  [
   id C
  ]
   edge
  [
   source B
   target A
  ]
  edge
  [
   source C
   target A
  ]
]
```

带标签示例：
```
graph
[
  node
  [
   id A
   label "Node A"
  ]
  node
  [
   id B
   label "Node B"
  ]
  node
  [
   id C
   label "Node C"
  ]
   edge
  [
   source B
   target A
   label "Edge B to A"
  ]
  edge
  [
   source C
   target A
   label "Edge C to A"
  ]
]
```

## graph6
graph6 或 sparse6 格式用于保存 undirected graphs的一种紧凑的文本格式，只使用可打印的 ASCII 字符。每一行为一个 graph。graph6 适合于小于的 graphs，或者很大的 dense graphs。

sparse6 更适合于大的 sparse graphs。一般保存 graph6 graphs 的文件后缀为 'g6'，而 sparse6 graphs 文件后缀为 's6'。sparse6 支持 loops 和 multiple edges，graph6 不支持。

graph6 占用空间的大小只取决于 vertex 的数目，即不管 edges 有多少，占用空间相同，所以特别适合于 dense graphs。

sparse6 占用空间大小则取决于 edges 的数目，适合于 sparse graph。

## GraphML
GraphML 是一个基于 XML 格式的 graphs 表达格式。基本上支持所有的 graph 结构。

示例：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<graphml xmlns="http://graphml.graphdrawing.org/xmlns"  
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd">
  <graph id="G" edgedefault="undirected">
    <node id="n0"/>
    <node id="n1"/>
    <edge id="e1" source="n0" target="n1"/>
  </graph>
</graphml>
```