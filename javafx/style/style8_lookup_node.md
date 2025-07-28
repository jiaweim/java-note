# 查找 Node

2023-06-25, 10:39
****

可以使用选择器在 scene-graph 中查找节点。

`Scene` 和 `Node` 都包含一个 `lookup(String selector)` 方法，返回匹配选择器的第一个 node；如果没有匹配到任何 node，返回 null。两个类的 `lookup(String selector)` 略有不同，`Scene.lookup` 搜索整个 scene graph，`Node.lookup` 搜索调用该方法的节点及其子节点。

`Node` 类还有一个 `lookupAll(String selector)` 方法，返回所有匹配选择器的 nodes，包括调用该方法的节点。

- 使用 lookup 方法查找匹配 ID 选择器的节点

```java
Button b1 = new Button("Close");
b1.setId("closeBtn");
VBox root = new VBox();
root.setId("myvbox");
root.getChildren().addAll(b1);
Scene scene = new Scene(root, 200, 300);
...
Node n1 = scene.lookup("#closeBtn"); // n1 is the reference of b1
Node n2 = root.lookup("#closeBtn"); // n2 is the reference of b1
Node n3 = b1.lookup("#closeBtn"); // n3 is the reference of b1
Node n4 = root.lookup("#myvbox"); // n4 is the reference of root
Node n5 = b1.lookup("#myvbox"); // n5 is null
Set<Node> s = root.lookupAll("#closeBtn"); // s contains the reference of b1
```
