

# 让 Canvas Resizable

按如下覆盖对应方法。

```java
@Override
public boolean isResizable(){
    return true;
}

@Override
public double prefWidth(double height){
    return getWidth();
}

@Override
public double prefHeight(double width){
    return getHeight();
}
```

然后在构造函数中添加监听器：
```java
public ResizableCanvas(){
    widthProperty().addListener((observable, oldValue, newValue) -> draw());
    heightProperty().addListener((observable, oldValue, newValue) -> draw());
}
```

最后， Canvas 大小和对应的容器绑定：
```java
canvas.widthProperty().bind(root.widthProperty());
canvas.heightProperty().bind(root.heightProperty());
```

# 关于线段变模糊

避免线段边沿模糊的方法：
- 对奇数：采用 0.5 这样的位置比较好。
- 对偶数：采用整数比较好。
