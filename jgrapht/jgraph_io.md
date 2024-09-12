# Graph 序列化和 IO

## 简介

JGraphT 提供的默认 graph 实现支持序列化，不过要求提供的 vertex 和 edge 为可序列化类型。

序列化是将 graph 存储为二进制数据的一种便捷方法，但是这种格式不是人类可读，并且不同 JGraphT 版本的序列化也不一定兼容。

为了解决该问题，JGraphT 提供了 `org.jgrapht.io` 模块用于将 graph 导出为各种标准格式。

以 HelloJGraphT 为例，将 graph 导出为 GraphViz.dot 格式：

```java
DOTExporter<URI, DefaultEdge> exporter =
    new DOTExporter<>(v -> v.getHost().replace('.', '_'));
exporter.setVertexAttributeProvider((v) -> {
    Map<String, Attribute> map = new LinkedHashMap<>();
    map.put("label", DefaultAttribute.createAttribute(v.toString()));
    return map;
});
Writer writer = new StringWriter();
exporter.exportGraph(hrefGraph, writer);
System.out.println(writer.toString());
```

