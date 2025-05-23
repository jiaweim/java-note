# Layout 概述

2023-07-06, 16:37
@author Jiawei Mao
****
## 1. 简介

在 scene graph 排列 nodes 的方法有两种：

- 静态布局，调整 Window 大小时，节点的位置和大小不变
- 动态布局，调整 Window 大小时，会动态调整 scene graph 中的所有 nodes 的大小和位置

两种方式各有优缺点，静态布局可以让你完全控制所有 `Node` 的位置，动态布局更为复杂，需要做更多的事情，但是更灵活。

布局面板（layout pane）或容器（container），是可以包含其它 nodes 的 node，负责在需要时布置其子节点。

不同类型的 layout pane 有不同的 layout policy，主要执行两项操作：

- 计算子节点在 `Parent` 容器中的位置
- 计算子节点在 `Parent` 容器中的尺寸

每个 node 有三个尺寸：preferred size, minimum size, maximum size。大部分容器尽量将子节点设置为 preferred size，不过实际尺寸受多种因素影响：

- 窗口大小
- 容器的布局策略
- 节点的缩放策略

## 2. Layout Pane 类

`Parent` 是所有容器的超类，类图如下：

<img src="images/Pasted%20image%2020230706155615.png" style="zoom:50%;" />

说明：

- `Group` 可用来对一组 nodes 同时应用特效和转换
- 所有容器都是 `Parent` 的子类，在 `javafx.scene.layout` 包中，但并非所有继承 `Parent` 的类都是容器。如 `Control` 及其子类在 `javafx.scene.control` 包中，用来支持高度可定制的控件
- 一个 node 一次只能在一个容器。如果 node 在容器1，又将其添加到容器 2，那么 node 会自动从容器 1 移除。容器支持嵌套
- 容器通过布局策略布局其子节点，如果某个子节点不希望容器管理，可以设置 `Node.managed` 属性为 false

`Parent` 如下方法用于获取容器包含的子节点：

```java
protected ObservableList<Node> getChildren()
public ObservableList<Node> getChildrenUnmodifiable()
protected <E extends Node> List<E> getManagedChildren()
```

- `getChildren()`

返回包含子节点的 modifiable `ObservableList`。这是容器类常用的方法，使用返回的 ObservableList 为容器添加子节点。

```ad-note
`getChildren()` 的访问权限是 `protected`。如果 `Parent` 子类不想成为容器，保持为 `protected`，如 `Control` 的子类 `Button`, `TextField` 等；容器类则覆盖该方法并调整为 `public`，如 `Group`, `Pane` 等。
```

- `getChildrenUnmodifiable()`

返回包含子节点的 read-only ObservableList。

- `getManagedChildren()` 也是 protected

一般在容器内部使用，查询托管的所有子节点。用于自定义容器。

下表对容器类进行简要介绍

| 容器类     | 说明                                   |
| ---------- | -------------------------------------- |
| Group      | 将特效和变换应用其所有子节点           |
| Pane       | 绝对布局                               |
| HBox       | 单行排列                               |
| VHox       | 单列排列                               |
| FlowPane   | 以行或列排列，自动换行                 |
| BorderPane | 以上、下、左、右、中 5 个区域布局      |
| StackPane  | back-to-front 堆栈布局子节点           |
| TilePane   | 以大小均匀的网格布局子节点             |
| GridPane   | 以大小可变的网格布局子节点             |
| AnchorPane | 将子节点的边缘铆钉到 layout 区域的边缘 |
| TextFlow   | `Text` 专用布局                        |

## 3. 添加子节点

为容器添加子节点的方式有两种：

- 构造时添加
- 构造后添加

```java
// 创建两个按钮
Button okBtn = new Button("OK");
Button cancelBtn = new Button("Cancel");

// 构造 HBox 时添加子节点
HBox hBox1 = new HBox(okBtn, cancelBtn);

// 构造 HBox 时添加子节点，同时指定 hspace
double hSpacing = 20;
HBox hBox2 = new HBox(hSpacing, okBtn, cancelBtn);

// 构造 HBox 后添加子节点
HBox hBox3 = new HBox();
hBox3.getChildren().addAll(okBtn, cancelBtn);
```

构造后添加有两个方法：

| 方法                     | 说明           |
| ------------------------ | -------------- |
| `getChildren().add()`    | 添加一个子节点 |
| `getChildren().addAll()` | 添加多个子节点 |
