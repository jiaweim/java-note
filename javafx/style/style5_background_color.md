# CSS 背景颜色

2023-06-20, 17:45
****
`Node` （`Region` 或 `Control` ）的背景色使用三个属性指定：

- -fx-background-color
- -fx-background-radius
- -fx-background-insets

`-fx-background-color` 是以逗号分隔的颜色列表。颜色数量与要绘制的矩形数量一致。

`-fx-background-radius` 是逗号分隔的矩形的 4 个半径。每个矩形可以只指定一个值，如 `10`，4 个角的半径相同；或者空格分隔的 4 个值，如 `10 5 15 20`，对应左上、右上、右下和左下的半径。

`-fx-background-insets` 是逗号分隔的矩形四个边的 inset 值。可以为单个值；或空格分隔的 4 个值 `10 5 15 20` ，对应上、右、下、左。

下面创建一个 Pane，它是 Region 的子类，即为容器类：

```java
Pane pane = new Pane();
pane.setPrefSize(100, 100);
```

下面定义三种样式：

```css
.my-style-1 {
	-fx-background-color: gray;
	-fx-background-insets: 5;
	-fx-background-radius: 10;
}

.my-style-2 {
	-fx-background-color: gray;
	-fx-background-insets: 0;
	-fx-background-radius: 0;
}

.my-style-3 {
	-fx-background-color: gray;
	-fx-background-insets: 5 10 15 20;
	-fx-background-radius: 10 0 0 5;
}
```

![](Pasted%20image%2020230620171214.png)

解释：

- `insets` 应该可以理解为填充区域和边框的距离，即内边距。
- 三个样式都采用灰色填充
- 第一个矩形 4 个边的内边距均为 5px，半径都是 10px
- 第二个矩形 4 个边的内边距均为 0px，半径都是 0px
- 第三个矩形 4 个边的内边距都不同，5 10 15 20 顺时针递增，半径为 10 0 0 5

当半径为 0px，对应直角。

再来一个更特殊的样式：

```css
.my-style-4 {
	-fx-background-color: red, green, blue;
	-fx-background-insets: 5 5 5 5, 10 15 10 10, 15 20 15 15;
	-fx-background-radius: 5 5 5 5, 0 0 10 10, 0 20 5 10;
}
```

![](Pasted%20image%2020230620172015.png)

这个背景用了三种颜色，三种背景按先后依次填充红色、绿色、蓝色。

