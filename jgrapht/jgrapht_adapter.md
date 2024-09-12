# 适配器

## Guava Graph Adapter

如果已经用 `com.google.common.graph` 表示 graph，使用 `org.jgrapht.graph.guava` 中 adapter 类可以将其转换为 jgrapht 的 graph 对象，该对象与 Guava graph 自动同步，没有额外的内存开销。因此，可以在 Guava 的 graph 上运行 JGraphT 算法，或者调用 JGraphT 的 IO 工具。

例如，创建如下 Guava graph:

```java
MutableGraph<String> guava = GraphBuilder.undirected().build();
guava.addNode("ul");
guava.addNode("um");
guava.addNode("ur");
guava.addNode("ml");
guava.addNode("mm");
guava.addNode("mr");
guava.addNode("ll");
guava.addNode("lm");
guava.addNode("lr");
guava.putEdge("ul", "um");
guava.putEdge("um", "ur");
guava.putEdge("ml", "mm");
guava.putEdge("mm", "mr");
guava.putEdge("ll", "lm");
guava.putEdge("lm", "lr");
guava.putEdge("ul", "ml");
guava.putEdge("ml", "ll");
guava.putEdge("um", "mm");
guava.putEdge("mm", "lm");
guava.putEdge("ur", "mr");
guava.putEdge("mr", "lr");
```

该 graph 没有任何与 edge 相关的信息，因此可以使用 JGraphT 的 `MutableGraphAdapter`：

```java
Graph<String, EndpointPair<String>> jgrapht = new MutableGraphAdapter<>(guava);
```

如果希望找到该 graph 的 minimum vertex cover，可以使用 JGraphT 提供的算法：

```java
VertexCoverAlgorithm<String> alg = new RecursiveExactVCImpl<>(jgrapht);
VertexCoverAlgorithm.VertexCover<String> cover = alg.getVertexCover();
Set<String> expectedCover = Set.of("um", "ml", "mr", "lm");
assertEquals(expectedCover, cover);
```

## JGraphX Adapter

JGraphT 提供了 JGraphX 的适配器，以支持 JGraphX 可视化。只需要将 JGraphT graph 用 `org.jgrapht.ext.JGraphXAdapter` 进行包装。例如：

