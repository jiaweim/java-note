# Vertex 和 Edge 类型

## 1. equals 和 hashCode

默认 graph 实现使用 vertex 和 edge 作为 keys，因为 vertex 和 edge 要求：

- 对 `equals` 和 `hashCode`，必须遵守 `java.lang.Object` 中的定义；
- 如果重写 `equals` 和 `hashCode`，必须同时重写；
- `hashCode` 的实现在对象的整个生命周期中，其值应该保持不变。

## 2. 匿名 Vertices

只图 graph 结构感兴趣的应用（如图论研究）需要尽可能减少 vertices 占用的内存，此时使用 `java.lang.Object` 作为 vertex 类型最合适。而 vertex 的 `hashCode` 作为其识别符。

## 3. Vertex 作为 Key

Vertex 常作为 key 值，此时 `String`, `Integer` 等类型适合作为 vertex 类型。在这种情况下，需要注意保持 Vertex 值的唯一性，如对 `String` 类型的 vertex，`String` 除了作为标签，还需要作为识别符。

## 4. Vertex 的属性

在一些复杂的应用中，每个 vertex 可能包含多个 non-key 属性。此时需要一个类来存储属性值，根据需要有多种实现方式。

### No Keys

如果 vertex 的所有属性都是 non-key，那么这些属性可以作为字段实现。以分子结构为例，其 vertex 是原子：

```java
class AtomVertex{
    Element element;  // using enum of the periodic table
    int formalCharge; // for bookkeeping purposes
    ... other atomic properties ...
}
```

此时**不应该**重写 `equals` 和 `hashCode`，因为可能有很多具有完全相同属性的不同原子。

### All keys

如果所有属性都是 key 的一部分，实现起来也简单。例如，假设要实现一个软件包管理器，每个 vertex 对应一个软件包，edge 表示依赖关系，可以定义：

```java
class SoftwarePackageVertex{
    
    final String orgName;
    final String packageName;
    final String packageVersion;
    
    ... constructor etc ...
    
    public String toString() {
        return orgName + "-" + packageName + "-" + packageVersion;
    }
    
    public int hashCode() {
        return toString().hashCode();
    }
    
    public boolean equals(Object o) {
        return (o instanceof SoftwarePackageVertex) && (toString().equals(o.toString()));
    }
}
```

此时需要重写 `equals` 和 `hashCode`。上面字段都声明为 `final`，因为 vertices 被用作 hash 值，因此在构造后不可以改变。

### Key subset

如果部分属性作为 keys，就稍微复杂一点。继续上例，假设需要向 `SoftwarePackageVertex` 添加 `releaseDate` 字段以跟踪包的发布时间。此时是否将 `releaseDate` 添加到 hashCode/equals 实现就需要取舍：

- 将 `releaseDate` 合并到 `hashCode`/`equals` 实现显然不是一个好的选择。例如，如果需要通过识别符引用 vertex，但是不知道其发布日期，就难以找到该 vertex。
- 如果不将 `releaseDate` 合并到 `hashCode`/`equals`，就可能出现两个具有不同 `releaseDate` 的 vertex 相等的情况。

## 5. Vertices as Pointers

处理上述情况的一种更灵活的方法是通过 vertex 引用外部对象，而是直接包含数据。对上例，`SoftwarePackageVertex` 依然定义为不含 `releaseDate` 的对象。其它信息通过一个单独的 `SoftwarePackageVersion` 类定义，从而使 graph 的表示更清晰，不过增加的查找成本：

```java
class SoftwarePackageVertex {
    final SoftwarePackageVersion version;
    
    public String toString() {
        return version.keyString();
    }
    
    public int hashCode() {
        return toString().hashCode();
    }
    
    public boolean equals(Object o) {
        return (o instanceof SoftwarePackageVertex) && (toString().equals(o.toString()));
    }
}

class SoftwarePackageVersion {
    final String orgName;
    final String packageName;
    final String packageVersion;
    Date releaseDate;
    
    public String keyString() {
        return orgName + "-" + packageName + "-" + packageVersion;
    }
}
```

此时，依然存在 `releaseDate` 是否作为 key 的问题，只是该问题从 graph 中分开了。

## 6. 匿名 edge

最简单的 edge 除了表示连接性，没有额外信息。通常可以使用 JGraphT 提供的 `DefaultEdge` 来实现。

> [!NOTE]
>
> JGraphT 通过侵入式技术优化基于 `DefaultEdge` 的 graph，连接性信息直接保存在 edge 中，而不是 graph 中。

因此，如果需要将相同的 edge 添加到两个不同的 graphs，那么这两个 graphs 必须具有相同的 vertex 连接性，否则就会出错。

## 7. Weighted edge

`DefaultWeightedEdge` 提供了加权 edge 的默认实现。其限制和 `DefaultEdge`，相同实例用在不同 graph 中会出问题。



## 8. Edges as Key Values

## 9. Edges with Attributes

对包含多个属性的 edge，所需考虑的问题和 vertex 一样。

## 参考

- https://jgrapht.org/guide/VertexAndEdgeTypes

