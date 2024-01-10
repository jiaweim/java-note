# JSplitPane

## 创建 JSplitPane

```java
public JSplitPane()
JSplitPane splitPane = new JSplitPane();

public JSplitPane(int newOrientation)
JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

public JSplitPane(int newOrientation, boolean newContinuousLayout)
JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);

public JSplitPane(int newOrientation, Component newLeftComponent,
    Component newRightComponent)
JComponent topComponent = new JButton("Top Button");
JComponent bottomComponent = new JButton("Bottom Button");
JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
    topComponent, bottomComponent);

public JSplitPane(int newOrientation, boolean newContinuousLayout,
    Component newLeftComponent, Component newRightComponent)
JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
    topComponent, bottomComponent);
```

方向 `newOrientation` 默认水平。

`continuousLayout` 指定拖动 divider 时 JSplitPane 的行为：

- false（默认），拖动时只重画分割线
- true，同时调整两边的组件大小，并重新绘制

如果使用无参构造函数，JSplitPane 大的初始组件为两个 JButton。