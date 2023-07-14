# Node Style

2023-07-13, 17:52
***

## applyCss

```java
public final void applyCss()
```

将样式应用于此 node 及其 children。通常不需要直接调用此方法，但可以与 `Parent.layout()` 结合使用，以便在下一个 pulse 之前调整 node 尺寸，或 Scene 不在 Stage 中。

如果 Node 的 Scene 不为 null，则不管 Node 的 CSS 状态是否干净，都会将 CSS 应用于此 node。CSS 样式从该 node 的最顶层 parent 开始应用，如果 Node 的 CSS 状态不干净，可能影响其它 nodes 的样式。

如果 Node 不在 Scene，此方法无效。Scene 不需要再 Stage 中。

通常调用顺序：

```java
parentNode.applyCss();
parentNode.layout();
```

下面调用 applyCss() 和 layout() ，从而在 Stage 显示前调整号 Button 的尺寸。如果注释掉 `applyCss()` 或 `layout()`，那么在 Stage 显示前，调用 `getWidth()` 和 `getHeight()` 都返回 0：

```java
@Override
public void start(Stage stage) throws Exception {

    Group root = new Group();
    Scene scene = new Scene(root);
    
    Button button = new Button("Hello World");
    root.getChildren().add(button);
    
    root.applyCss();
    root.layout();
    
    double width = button.getWidth();
    double height = button.getHeight();
    
    System.out.println(width + ", " + height);
    
    stage.setScene(scene);
    stage.show();
}
```

