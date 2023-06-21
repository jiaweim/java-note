# Border

2023-06-21, 10:37
****
## 1. 简介

node (Region 和 Control) 可以通过 CSS 指定多个边框。边框使用以下 5 个属性定义：

- -fx-border-color
- -fx-border-width
- -fx-border-radius
- -fx-border-insets
- -fx-border-style

每个属性值为逗号分隔的列表。列表中的每一项又可以包含空格分隔的多个值。

## 2. Border Color

`-fx-border-color` 包含的颜色个数对应 border 数。

- 绘制红色 border

```css
-fx-border-color: red;
```

- 指定 4 种颜色：red, green, blue, aqua，对应上、右、下、左

```css
-fx-border-color: red green blue aqua;
```

需要注意，这没有逗号，即只有一项，所以只有一个 border，只是每个边的颜色不同。


- 指定 2 个 border

```css
-fx-border-color: red green blue aqua, tan;
```

第一个 border 四个边的颜色不同；第二个 border 只有一种颜色 tan。

```ad-tip
对不是矩形的 node，只用第一个 border 颜色绘制整个边框。
```

## 3. Border Width

使用 `-fx-border-width` 指定边框宽度。四个边可以指定不同宽度，单位默认为 px。

- 指定 2px 宽的红色边框

```css
-fx-border-color: red;
-fx-border-width: 2;
```

- 指定三个边框

```css
-fx-border-color: red green blue black, tan, aqua;
-fx-border-width: 2 1 2 2, 2 2 2 1, 3;
```

前面两个边框，四个边不一样宽；第三个边框四个边宽度都是 3px。

## 4. Border Radii

使用 `-fx-border-radius` 指定边框四个角的半径。可以使用一个值，指定四个角相同半径；也可以使用空格分开的 4 个值，对应左上、右上、右下、左下四个角度。

半径单位默认为 px。

- 红色、2px 宽、四个角的半径均为 5px

```css
-fx-border-color: red;
-fx-border-width: 2;
-fx-border-radius: 5;
```

- 3 个边框

```css
-fx-border-color: red green blue black, tan, aqua;
-fx-border-width: 2 1 2 2, 2 2 2 1, 3;
-fx-border-radius: 5 2 0 2, 0 2 0 1, 0;
```

最后一个边框四个角的半径为 0px。

## 5. Border Insets

inset 是边框到 node 的距离。除了 inset，边框的最终位置还受 -fx-border-width 和 -fx-border-style 影响。

`-fx-border-insets` 也支持两种指定方式：指定单个值表示四个边 inset 相同；指定四个值对应上、右、下、左四个边的 inset。单位默认为 px。

- 红色、2px 宽、5px 半径、20px insets 的边框

```css
-fx-border-color: red;
-fx-border-width: 2;
-fx-border-radius: 5;
-fx-border-insets: 20;
```

- 三个边框

每个边框的四个边的 insets 相同，分别为 10px, 20px, 30px。

```css
-fx-border-color: red green blue black, tan, aqua;
-fx-border-width: 2 1 2 2, 2 2 2 1, 3;
-fx-border-radius: 5 2 0 2, 0 2 0 1, 0;
-fx-border-insets: 10, 20, 30;
```

## 6. Border Styles

`-fx-border-style` 定义边框样式。指定语法：

```css
-fx-border-style: <dash-style> [phase <number>] [<stroke-type>] 
	[line-join <line-join-value>] [line-cap <line-cap-value>]
```

- `<dash-style>`: `none`, `solid`, `dotted`, `dashed`, `segments(<number>, <number>...)`
- `<stroke-type>`: centered, inside, or outside
- `<line-join-value>`: `miter <number>`, bevel, or round
- `<line-cap-value>`: square, butt, or round.


最简单的边框样式可以只指定 `<dash-style>`：

```css
-fx-border-style: solid;
```

### 6.1. 虚线

`segments()` 函数用于指定虚线：

```css
-fx-border-style: segments(dash-length, gap-length, dash-length, ...);
```

segments 的第一个参数为 dash 长度、第二个为 gap 长度，以此类推；到最后一个参数，又从头开始重复。


例如：10px dash, 5px gap 虚线

```css
-fx-border-style: segments(10px, 5px);
```
 
可以给 segments 函数传入任意数量的 dash 和 gap，不过最好是偶数个。如果不是偶数，比如 `segments(20px, 10px, 5px)`，segments 会将参数重复拼接成偶数，等价于 `segments(20px, 10px, 5px, 20px, 10px, 5px)`。

`phase` 参数仅在使用 segments() 时有效，指定虚线开始前的 offset。例如：

```css
-fx-border-style: segments(20px, 5px) phase 10.0;
```

segment 长度为 25px (20px+5px)，phase 值为 10.0，表示虚线从第一个 segment 的 10px 开始，所以第一个 dash 长度只剩下 10px。

phase 默认为 0.0.

### 6.2. stroke type

`<stroke-type>` 有三个选项：centered, inside, outside。指定边框相对 inset 的位置。

假设 region 长宽均为 200px，矩形上方的 inset 为 10px，边框宽度为 4px：

- 如果 `<stroke-type>` 为 centered，顶部边框会占据顶部 8px 到 12px 的位置
- 如果 `<stroke-type>` 为 inside，顶部边框会占据顶部 10px 到 14px 的位置
- 如果 `<stroke-type>` 为 outside，顶部边框会占据顶部 6px 到 10px 的位置

### 6.3. line join

line-join 参数指定边框两个边的连接方式。可用值包括：

- miter
- bevel
- round

如果指定为 miter，还需要指定 miter limit 值。如果 miter limit 小于 miter length，则换成 bevel。

![](images/2019-06-05-16-14-57.png)

如图所示，miter length 是从相交的内点和外点的距离，为上图中的 A。miter length 以相对边框宽度定义。

miter limit 指定线段相交外边缘延伸的距离。例如，如果 miter length 为 5，miter limit 为 4，此时 miter limit 小于 miter length，采用 BEVEL 样式，对应上图中的 B；如果 miter limit 超过 5，则采用完整延申，即 A 的样式。

例如，将 miter limit 设置为 30：

```css
-fx-border-style: solid line-join miter 30;
```

### 6.4. line cap

`line-cap` 指定线段末端的样式，可选样式: 

| line-cap | 说明                                                 |
| ------------- | ---------------------------------------------------- |
| `butt`        | 不添加装饰，默认                                     |
| `round`       | 在线段末端添加圆弧，圆弧半径为 stroke width 的一半。 |
| `square`      | 线段向外延伸，延伸长度为 `strokeWidth` 的一半。      |

![](images/2019-06-05-16-14-01.png)

从左到右，分别为 `butt`, `round`, `square`。

例如，指定为 `round`：

```css
-fx-border-style: solid line-join bevel 30 line-cap round;
```

### 6.5. 边框样式示例

定义 100px 宽 50px 高的 Pane，应用如下样式：

```css
.my-style-1 {
    -fx-border-color: black;
    -fx-border-width: 5;
    -fx-border-radius: 0;
    -fx-border-insets: 0;
    -fx-border-style: solid line-join bevel line-cap square;
}

.my-style-2 {
    -fx-border-color: red, black;
    -fx-border-width: 5, 5;
    -fx-border-radius: 0, 0;
    -fx-border-insets: 0, 5;
    -fx-border-style: solid inside, dotted outside;
}

.my-style-3 {
    -fx-border-color: black, black;
    -fx-border-width: 1, 1;
    -fx-border-radius: 0, 0;
    -fx-border-insets: 0, 5;
    -fx-border-style: solid centered, solid centered;
}

.my-style-4 {
    -fx-border-color: red black red black;
    -fx-border-width: 5;
    -fx-border-radius: 0;
    -fx-border-insets: 0;
    -fx-border-style: solid line-join bevel line-cap round;
}
```

![](images/Pasted%20image%2020230621102318.png)

| 边框 | 颜色                | 宽度 | 角半径 | 内半径 | 样式                                  |
| ---- | ------------------- | ---- | ------ | ------ | ------------------------------------- |
| 1 号 | black               | 5    | 0      | 0      | solid line-join bevel line-cap square |
| 2 号 | red, black          | 5,5  | 0,0    | 0,5    | solid inside, dotted outside          |
| 3 号 | black, black        | 1,1  | 0,0    | 0,5    | solid centered, solid centered        |
| 4 号 | red black red black | 5    | 0      | 0      | solid line-join bevel line-cap round  |

其中 2 号包含两个重叠边框：

- 一个是红色实线，solid inside，其位置为 0px 到 5px
- 一个是黑色虚线，inset 为 5，dotted outside，其位置为也是 0px 到 5px

所以这两个边框重叠。

边框根据指定的顺序绘制，所以先绘制红色实线边框，然后绘制黑色虚线边框，所以才能看到黑色虚线边框。

```ad-tip
`Region` 可以通过 CSS 指定背景图片和边框图片。
```
