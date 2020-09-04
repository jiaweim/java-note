# TilePane

- [TilePane](#tilepane)
  - [简介](#简介)
  - [创建 TilePane](#创建-tilepane)

## 简介

`TilePane` 将其包含的组件放在大小一致的网格中。和 `FlowPane` 很小，不过 `FlowPane` 的不过列的宽度和不同行的高度可以不一样，而 `TilePane` 所有高度和宽度一致。

`TilePane` 的方向有水平和垂直两个选项，默认水平。

## 创建 TilePane

```java
// Create an empty horizontal TilePane with 0px spacing
TilePane tpane1 = new TilePane();
// Create an empty vertical TilePane with 0px spacing
TilePane tpane2 = new TilePane(Orientation.VERTICAL);
// Create an empty horizontal TilePane with 5px horizontal
// and 10px vertical spacing
TilePane tpane3 = new TilePane(5, 10);
// Create an empty vertical TilePane with 5px horizontal
// and 10px vertical spacing
TilePane tpane4 = new TilePane(Orientation.VERTICAL, 5, 10);
// Create a horizontal TilePane with two Buttons and 0px spacing
TilePane tpane5 = new TilePane(new Button("Button 1"), new Button("Button 2"));
```
