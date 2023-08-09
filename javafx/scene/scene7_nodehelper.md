# NodeHelper

```java
public static void markDirty(Node node, DirtyBits dirtyBit)
```

当 NodeHelper.markDirty 被调用，整个 scene/subscene 在下一个 pulse 被重绘。尽管调用该方法的 node 知道哪些 nodes 变脏了，需要重绘。