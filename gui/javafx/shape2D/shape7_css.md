# Shape CSS

2023-07-14, 10:40
****
所有 shapes 默认没有 style class name。如果需要为 shapes 应用 CSS 样式，需要为其设置 style class names。所有 Shapes 使用如下 CSS 属性：

- -fx-fill
- -fx-smooth
- -fx-stroke
- -fx-stroke-type
- -fx-stroke-dash-array
- -fx-stroke-dash-offset
- -fx-stroke-line-cap
- -fx-stroke-line-join
- -fx-stroke-miter-limit
- -fx-stroke-width

这些 CSS 属性与 Shape 类一一对应，前面均已讨论。Rectangle 支持 2 个额外的 CSS 属性用于指定 圆角：

- -fx-arc-height
- -fx-arc-width

下面创建 Rectangle 并为其设置 style class name:

```java
Rectangle r1 = new Rectangle(200, 50);
r1.getStyleClass().add("rectangle");
```

**示例：** 为 Rectangle 应用 CSS 样式

```css
.rectangle {
    -fx-fill: lightgray;
    -fx-stroke: black;
    -fx-stroke-width: 4;
    -fx-stroke-dash-array: 15 5 5 10;
    -fx-stroke-dash-offset: 20;
    -fx-stroke-line-cap: round;
    -fx-stroke-line-join: bevel;
}
```

![|200](Pasted%20image%2020230714103925.png)

