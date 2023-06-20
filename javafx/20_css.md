# CSS

- [CSS](#css)
  - [参考](#参考)
  - [概述](#概述)
  - [样式(style), 皮肤(skin) 和主题(theme)](#样式style-皮肤skin-和主题theme)
  - [JavaFX CSS](#javafx-css)
    - [JavaFX CSS 命名约定](#javafx-css-命名约定)
    - [添加样式表](#添加样式表)
  - [默认样式表](#默认样式表)
  - [添加内联样式](#添加内联样式)
  - [样式优先级](#样式优先级)
  - [CSS 属性继承](#css-属性继承)
  - [选择器](#选择器)
    - [Class Selector](#class-selector)
    - [ID Selector](#id-selector)
    - [CSS 属性类型](#css-属性类型)
    - [URI](#uri)
    - [effect](#effect)
    - [font](#font)
    - [paint](#paint)

## 参考

- https://docs.oracle.com/javase/8/javafx/api/javafx/scene/doc-files/cssref.html
- https://www.w3.org/TR/css-backgrounds-3/#backgrounds
- https://openjfx.io/javadoc/11/javafx.graphics/javafx/scene/doc-files/cssref.html

**JavaFX CSS Reference Guide**  
https://openjfx.io/javadoc/11/javafx.graphics/javafx/scene/doc-files/cssref.html

## 选择器

JavaFX CSS 支持多种类型的选择器，class选择器，pseudo-class 选择器，ID选择器。

### Class Selector

定义位置： `Node` 的 `styleClass` 变量。

添加方式：

```java
HBox hb = new HBox();
hb.getStyleClass().addAll("hbox", "myhbox");
```

访问格式：

```css
.button{
    -fx-text-fill: blue;
}
```

### ID Selector

设置方法：

```java
Button b1 = new Button("Close");
b1.setId("closeBtn");
```

使用语法：

```css
#closeButton {
    -fx-text-fill: red;
}
```

### CSS 属性类型

JavaFX 支持如下类型:
| 类型       | 说明                                                                               |
| ---------- | ---------------------------------------------------------------------------------- |
| inherit    | 从 parent 继承属性值                                                               |
| boolean    | true,false                                                                         |
| string     | 单引号或双引号，包含引号需要转义，换行符（\A or \0000a）                           |
| number     | 整数或实数，以及单位，px(pixels), mm(millimeters), cm(centimeters), in(inches), pt | (points), pc(picas), em or ex. |
| angle      | 角度，单位有 deg(degrees), rad(radians), grad(gradients), turn(turns)              |
| point      | 空格分隔两个数，如 `0 0`                                                           |
| color-stop | 用于指定 linear 或 radical color gradients                                         |
| URI        | URI                                                                                |
| effect     | 特效                                                                               |
| font       | 字体                                                                               |
| paint      | 颜色                                                                               |

### URI

通过 `url(address)` 函数指定，其中相对地址是相对当前CSS文件的路径。

```css
.image-view {
    -fx-image: url("http://jdojo.com/myimage.png");
}
```

### effect

Drop shadow 和 inner shadow 特效可以通过 dropshadow() 和 innershadow() 两个CSS 函数指定：

```css
dropshadow(<blur-type>, <color>, <radius>, <spread>, <x-offset>, <y-offset>)
innershadow(<blur-type>, <color>, <radius>, <choke>, <x-offset>, <y-offset>)
```

`<blur-type>`： Gaussian, one-pass-box, three-pass-box, two-pass-box。

### font

字体包括四个属性：family, size, style, weight。指定CSS属性的方法有两种：

- 分别以四个CSS属性指定四个值：-fx-font-family, -fx-font-size, -fx-font-style, and -fx-font-weight.
- 以 `-fx-font` 指定所有四个属性
分别指定：

```css
.my-font-style {
    -fx-font-family: "serif";
    -fx-font-size: 20px;
    -fx-font-style: normal;
    -fx-font-weight: bolder;
}
```

同时指定：

```css
.my-font-style {
    -fx-font: italic bolder 20px "serif";
}
```

### paint

paint 类型用于指定颜色。指定方式有：

- linear-gradient() 函数
- radial-gradient() 函数
- 各种颜色值和函数

例：

```css
.my-style {
    -fx-fill: linear-gradient(from 0% 0% to 100% 0%, black 0%, red 100%);
    -fx-background-color: radial-gradient(radius 100%, black, red);
}
```

combo-box
extends combo-box-base
list-cell, ListCell instance used to show the selection in the button area of a non-editable ComboBox.
text-input, a TextField instance used to show the selection and allow input in the button area of an editable ComboBox.
