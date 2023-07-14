# HiddenSidesPane

在中间显示一个 node，四个边可以设置初始隐藏的 4 个 nodes。将鼠标移到 edges 附近，隐藏的 nodes 显示。

隐藏的 node 以 prefWidth 或 prefHeight 显示，并带有一个简短的划入动画。光标移开，node 再次隐藏。也可以将隐藏的 node 固定，使其保持可见。

使用方式：

```java
HiddenSidesPane pane = new HiddenSidesPane();  
pane.setContent(new TableView());  
pane.setRight(new ListView());
```
