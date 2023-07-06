# Focus Owner

2023-07-06, 11:50
****
`Scene` 中只能有一个 Node 持有焦点。`Scene` 的 focusOwner 属性保存持有焦点的 Node 类。

`focusOwner` 是 read-only 属性。如果需要 Scene 中特定 node 持有焦点，调用 `Node.requestFocus()` 即可。

Scene.getFocusOwner() 返回 scene 中持有焦点的节点。如果没有任何 node 持有焦点，返回 null。

需要注意，focusOwner 和 haveFocus 是两码事。每个 Scene 可以有一个 focusOwner。例如，如果你打开了两个窗口，有两个 Scene，因此可以有两个 focusOwner。然后，一次只能有一个 haveFocus，活动窗口的某个 node 持有焦点。

要确定 focusOwner 是否持有焦点，可以用 Node.focused 属性检查。

下面是使用 focusOwner 的典型逻辑：

```java
Scene scene;
...
Node focusOwnerNode = scene.getFocusOwner();
if (focusOwnerNode == null) {
    // scene 中没有 focusOwner
} else if (focusOwnerNode.isFocused()) {
    // focusOwner 持有 focus
} else {
    // focusOwner 没有 focus
}
```

