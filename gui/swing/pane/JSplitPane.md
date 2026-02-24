# JSplitPane

## 简介

与 `Box` 类似，`JSplitPane` 允许在一行或一列显示组件，但 `Box` 可以包含任何数量的组件，而 `JSplitPane` 则只能显示两个组件。不过 `JSplitPane` 组件的大小可调，并由可移动的 divider 分割。用户可以通过拖动 divider 来调整所含组件的大小。如下图所示：

<img src="./images/image-20241217155458358.png" alt="image-20241217155458358" style="zoom:50%;" />

## 创建 JSplitPane

`JSplitPane` 提供了 5 个构造函数，可以设置所含组件和方向。

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

`continuousLayout` 指定拖动 divider 时 `JSplitPane` 的行为：

- false（默认），拖动时只重画分割线
- true，同时调整两边的组件大小，并重新绘制

> [!NOTE]
>
> 如果方向为 `JSplitPane.VERTICAL_SPLIT`，可以将 top 组件作为左侧组件。

如果使用无参构造函数，`JSplitPane` 的初始组件为两个 `JButton`。

## JSplitPane 属性

**设置方向**

除了在构造函数中初始化方向，还可以将 `orientation` 属性修改为 `JSplitPane.VERTICAL_SPLIT` 或
`JSplitPane.HORIZONTAL_SPLIT` 修改方向。

不推荐在运行时动态修改方向。不过，如果是可视化开发工具，则可以在创建 `JSplitPane` 后明确设置 `orientation` 属性。

**修改组件**

`JSplitPane` 不同位置的组件对应 4 个可读写属性：`bottomComponent`, `leftComponent`, `rightComponent`, `topComponent`。实际上，这 4 个属性代表两个组件：left 和 top 是同一个组件，right 和 bottom 是同一个组件，根据 `JSplitPane` 的方向设置属性。例如：

```java
JComponent topButton = new JButton("Left");
JComponent bottomButton = new JButton("Right");
JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
splitPane.setTopComponent(topButton);
splitPane.setBottomComponent(bottomButton);
```

**移动 divider**

divider 初始位置是顶部或组件组件首选大小下方或右侧。随时调用 `JSplitPane` 的 `resetToPreferredSizes()` 可以将 divider 重置为该位置。

使用 `setDividerLocation(newLocation)` 以编程方式设置 divider 的位置：

- 若设为 `int` 值，则表示距离 top 或 left 的绝对距离
- 若为 0-1 的 `double` 指，则表示距离 top 或 left 的百分比，设置值超出 0-1 抛出异常

如果要设置 divider 位置，则必须要等待组件响应，这意味着 `JSplitPane` 必须可见。最直接的方法是为 `JSplitPane` 添加 `HierarchyListener`，并观察 `HierarchyEvent` 是否为 `SHOWING_CHANGED`。以下代码演示该功能，将 divider 位置更改为 75%：

```java
HierarchyListener hierarchyListener = new HierarchyListener() {
  public void hierarchyChanged(HierarchyEvent e) {
    long flags = e.getChangeFlags();
    if ((flags & HierarchyEvent.SHOWING_CHANGED) ==
         HierarchyEvent.SHOWING_CHANGED) {
      splitPane.setDividerLocation(.75);
    }
  }
};
splitPane.addHierarchyListener(hierarchyListener);
```

另外，虽然可以使用 `double` 设置 `dividerLocation` 属性，但 getter 方法只能得到绝对位置的 int 值。

### 调整组件大小

`JSplitPane` 尊重所含组件的最小尺寸，因此组件大小的调整存在限制。

> [!NOTE]
>
> 通过编程方式设置 divider 位置没有该限制。

如果组件的最小尺寸对 `JSplitPane` 来说太大，则建议更改组件的最小尺寸。对 Swing 组件，调用 `JComponent.setMinimumSize()` 即可。

还有一种方法可以让一个组件比另一个组件占用更多空间：将 `JSplitPane` 的 `oneTouchExpandable` 属性设置为 `true`。当该属性为 `true`，divider 旁边有一个图标，点击可以折叠两个组件中的一个，让另一个组件完全占据整个空间。如下图：

<img src="./images/image-20241217162923933.png" alt="image-20241217162923933" style="zoom:50%;" />

再次点击图标可以让组件返回之前的位置。点击 divider 图标以外的地方将会定位 divider，使折叠的组件处于其首选大小。

`lastDividerLocation` 属性存储 divider 上一个位置。`JSpliePane` 使用该属性来撤销 one-touch 操作。

> [!CAUTION]
>
> 对那些根据容器大小或初始大小确定最小尺寸的组件容易出问题，将它们放在 `JSplitPane` 中可能需要手动设置组件的最小或首选尺寸。在 JSplitPane 使用最常导致问题的组件包括 JTextArea 和 JScrollPane。

### 调整 JSplitPane 尺寸

如果 `JSplitPane` 中存在额外可用空间，而其所含组件的首选尺寸用不到该空间，则根据 `resizeWeight` 属性来分配该空间。

`resizeWeight` 的初始值为 0，表示 right 或 bottom 组件获得额外空间；设置位 1 表示 left 或 top 组件获得额外空间；设置为 0.5 表示两个组件平均分配该空间。

## 监听 JSplitPane 属性变化

`JSplitPane` 定义了以下常量来帮助监听绑定属性的变化：

- `CONTINUOUS_LAYOUT_PROPERTY`
- `DIVIDER_LOCATION_PROPERTY`
- `DIVIDER_SIZE_PROPERTY`
- `LAST_DIVIDER_LOCATION_PROPERTY`
- `ONE_TOUCH_EXPANDABLE_PROPERTY`
- `ORIENTATION_PROPERTY`
- `RESIZE_WEIGHT_PROPERTY`

监听用户移动 divider 的一种方法是监听 `lastDividerLocation` 属性。下例将 `PropertyChangeListener` 添加到 `JSplitPane`，显示当前 divider 位置、当前 last-location 以及上一个 last-location。

```java
import com.beust.jcommander.JCommander;
import mjw.swing.j2d.OvalComponent;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PropertySplit {
    public static void main(String[] args) {
        Runnable runner = () -> {
            JFrame frame = new JFrame("Property Split");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            splitPane.setContinuousLayout(true);
            splitPane.setOneTouchExpandable(true);

            JComponent topComponent = new OvalComponent();
            splitPane.setTopComponent(topComponent);

            JComponent bottomComponent = new OvalComponent();
            splitPane.setBottomComponent(bottomComponent);

            PropertyChangeListener propertyChangeListener = changeEvent -> {
                JSplitPane sourceSplitPane = (JSplitPane) changeEvent.getSource();
                String propertyName = changeEvent.getPropertyName();
                if (propertyName.equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)) {
                    int current = sourceSplitPane.getDividerLocation();
                    System.out.println("Current: " + current);
                    Integer last = (Integer) changeEvent.getNewValue();
                    System.out.println("Last: " + last);
                    Integer priorLast = (Integer) changeEvent.getOldValue();
                    System.out.println("Prior last: " + priorLast);
                }
            };

            splitPane.addPropertyChangeListener(propertyChangeListener);

            frame.add(splitPane, BorderLayout.CENTER);
            frame.setSize(300, 150);
            frame.setVisible(true);
        };

        EventQueue.invokeLater(runner);
    }
}
```

<img src="./images/image-20241217165103357.png" alt="image-20241217165103357" style="zoom:50%;" />

