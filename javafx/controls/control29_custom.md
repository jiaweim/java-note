# 自定义控件

## 简介

当现有控件无法满足需求，就需要自定义实现新的控件。

JavaFX 提供了多种创建自定义控件的方法：

- 使用 CSS 重新定义控件样式
- 组合已有控件
- 扩展已有控件
- 使用 `Control`+Skin 类
- 使用 `Region` 类
- 使用 `Canvas` 类

## 使用 CSS 重新定义控件样式

例如，我们想将 `CheckBox` 的样式更改为 MaterialDesign 类型。

因为我们只想重新设计 `CheckBox`，所以需要查看 CheckBox 当前 CSS 样式。为此，需要查看 modena.css 文件，该文件包含 JavaFX 所有控件的默认 CSS 样式。在 openjfx GitHub 可以找到 [modena.css](https://raw.githubusercontent.com/openjdk/jfx/master/modules/javafx.controls/src/main/resources/com/sun/javafx/scene/control/skin/modena/modena.css) 文件。

在 modena.css 之前是 caspian.css 文件，两个文件结构不同：

- modena.css 进行了很多优化，但并不是为了增加可读性
- caspian.css 中的样式按照控件进行分组，因此很容易找到 CheckBox 的所有样式
- modena.css 中 CheckBox 的样式分散在不同位置

所以需要从 modena.css 文件中将 CheckBox 的相关样式收集起来，复制到一个单独的 css 文件，例如 restyled.css。该文件类似于：

```css
.check-box > .box {}
    -fx-background-color: -fx-shadow-highlight-color, -fx-outer-border, -fx-inner-border, -fx-body-color;
    -fx-background-insets: 0 0 -1 0, 0, 1, 2;
    -fx-background-radius: 3px, 3px, 2px, 1px;
    -fx-padding: 0.333333em 0.666667em 0.333333em 0.666667em;
    -fx-text-fill: -fx-text-base-color;
    -fx-alignment: CENTER;
    -fx-content-display: LEFT;
}
.check-box:hover > .box {
    -fx-color: -fx-hover-base;
}
.check-box:armed .box {
    -fx-color: -fx-pressed-base;
}
.check-box:focused > .box {
    -fx-background-color: -fx-focus-color, -fx-inner-border, -fx-body-color, -fx-faint-focus-color, -fx-body-color;
    -fx-background-insets: -0.2, 1, 2, -1.4, 2.6;
    -fx-background-radius: 3, 2, 1, 4, 1;
}
.check-box:disabled {
    -fx-opacity: 0.4;
}
.check-box:show-mnemonics > .mnemonic-underline {
    -fx-stroke: -fx-text-base-color;
}
.check-box:selected > .box > .mark,
.check-box:indeterminate  > .box > .mark {
    -fx-background-color: -fx-mark-highlight-color, -fx-mark-color;
    -fx-background-insets: 1 0 -1 0, 0;
}
.check-box {
    -fx-label-padding: 0.0em 0.0em 0.0em 0.416667em; /* 0 0 0 5 */
    -fx-text-fill: -fx-text-background-color;
}
.check-box > .box {
    -fx-background-radius: 3, 2, 1;
    -fx-padding: 0.166667em 0.166667em 0.25em 0.25em; /* 2 2 3 3 */
}
.check-box > .box > .mark {
    -fx-background-color: null;
    -fx-padding: 0.416667em 0.416667em 0.5em 0.5em; /* 5 5 6 6 */
    -fx-shape: "M-0.25,6.083c0.843-0.758,4.583,4.833,5.75,4.833S14.5-1.5,15.917-0.917c1.292,0.532-8.75,17.083-10.5,17.083C3,16.167-1.083,6.833-0.25,6.083z";
}
.check-box:indeterminate > .box {
    -fx-padding: 0;
}
.check-box:indeterminate  > .box > .mark {
    -fx-shape: "M0,0H10V2H0Z";
    -fx-scale-shape: false;
    -fx-padding: 0.666667em;
}
```

总的来说，JavaFX CSS 与 Web CSS 非常相似，除了基于 CSS 2.1  设计，所有属性包含 `-fx-` 前缀，支持遍历。

下面实现 [MaterialDesign](https://material.io/develop/web/components/input-controls/checkboxes#checkboxes "MaterialDesign") 样式的 `CheckBox`，需要对现有 CSS 样式进行一些修改：

![](Pasted%20image%2020230727124128.png)

- 取消渐变，使 UI 更扁平化
- 不同的 checkmark
- 勾选时填充背景
- 不同 focus indicator

取消渐变很容易，只需要定义一些颜色来代替渐变。为此，在 restyled.css 文件中定义如下遍历：

```css
.check-box {
    -material-design-color: #3f51b5;
    -material-design-color-transparent-12: #3f51b51f;
    -material-design-color-transparent-24: #3f51b53e;
    -material-design-color-transparent-40: #3f51b566;
    ...
}
```

这样就能在 CSS 文件的 `.check-box` 中使用 `-material-design-color`。

### 修改 checkmark

checkmark 是使用 `-fx-shape` 定义的 SVGPath。modena.css 中的定义：

```css
.check-box > .box > .mark {
    -fx-background-color: null;
    -fx-padding: 0.416667em 0.416667em 0.5em 0.5em; /* 5 5 6 6 */
    -fx-shape: "M-0.25,6.083c0.843-0.758,4.583,4.833,5.75,4.833S14.5-1.5,15.917-0.917c1.292,0.532-8.75,17.083-10.5,17.083C3,16.167-1.083,6.833-0.25,6.083z";
}
```

上面的 `-fx-shape` 定义了如下形状

![|40](Pasted%20image%2020230727142313.png)
现在自定义一个 SVGPath，替换原有的 -fx-shape:

```css
.check-box > .box > .mark {
    -fx-background-color: null;
    -fx-padding: 0.45em;
    -fx-scale-x: 1.1;
    -fx-scale-y: 0.8;
    -fx-shape: "M9.998,13.946L22.473,1.457L26.035,5.016L10.012,21.055L0.618,11.688L4.178,8.127L9.998,13.946Z";
}
```

![|40](Pasted%20image%2020230727142830.png)
上面还调整了 scale-x 和 scale-y 属性使 checkmark 和 CheckBox 更匹配。

### 修改 box

下面要取消 CheckBox 的渐变背景吗，即取消 modena.css 中定义的线性渐变，将背景设置为透明，并设置一个边框：

```css
.check-box > .box {
    -fx-background-color: transparent;
    -fx-background-insets: 0;
    -fx-border-color: #0000008a;
    -fx-border-width: 2px;
    -fx-border-radius: 2px;
    -fx-padding: 0.083333em; /* 1px */
    -fx-text-fill: -fx-text-base-color;
    -fx-alignment: CENTER;
    -fx-content-display: LEFT;
}
```

- 当勾选复选框时，填充 box

```css
.check-box:selected > .box {
    -fx-background-color: -material-design-color;
    -fx-background-radius: 2px;
    -fx-background-insets: 0;
    -fx-border-color: transparent;
}
```

这里简单地填充背景，并将 border 设置为透明。

### CheckBox 状态

接下来就是 CheckBox 在  hover, focused 和 selected 状态的样式。原始 MaterialDesign CheckBox 在 CheckBox 周围画一个圆。

使用 CSS 很容易实现该样式，在 focused 状态添加圆的 CSS：

```css
.check-box:focused > .box {
    -fx-background-color: #6161613e, transparent;
    -fx-background-insets: -14, 0;
    -fx-background-radius: 1024;
}
```

这个状态圆比 CheckBox 要大一点。为了实现该效果，这里定义了两个 background colors `-fx-background-color: #6161613e, transparent;`

## 扩展已有控件

## 使用 Canvas

为了理解 Canvas 及其优点，可以参考 [JavaFX 的渲染机制](../scene/scene2_rendering_mode.md)。

在 Canvas 上的


## 参考

- https://foojay.io/today/custom-controls-in-javafx-part-i/