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

## 概述

CSS，即层叠样式表，用于描述GUI程序中UI元素的外观和样式的语言。CSS最初为网页设计的，实现网页的内容和样式的分离。

JavaFX 支持使用 CSS 定义JavaFX 程序的样式。即通过JavaFX类库或FXML定义UI元素，然后使用CSS定义样式。

CSS 通过规则指定UI元素的属性。基本语法：

- 一个规则（rule）包含选择器（selector）和一系列的键值对。
- 选择器是用于识别UI元素的字符串
- 键值对包含UI元素的属性名称和值，两个以冒号（`:`）分隔
- 属性之间以分号（`;`）分隔
- 所有的属性都放在大括号（`{}`）中。

例：

```css
.button {
    -fx-background-color: red;
    -fx-text-fill: white;
}
```

解释：

- `.button` 是选择器，表示该规则用于所有的按钮
- `-fx-background-color: red;` 指定背景色为红色
- `-fx-text-fill: white;` 指定文本颜色为白色

## 样式(style), 皮肤(skin) 和主题(theme)

CSS 规则称为样式（style），一系列CSS规则的集合称为样式表（style sheet）。样式、皮肤和主题，这三个概念很容易混淆。

**样式**，实现了外观和内容的分离，同时也促进了UI元素的分组。JavaFX 使用 JavaFX CSS 创建样式。  
**皮肤**，定义了应用程序的外观，它是特定于应用程序的样式的集合。换皮肤就是更改程序的外观。JavaFX 没有提供特定的机制以支持换肤，不过使用 JavaFX CSS 和 JavaFX API，可以轻松的为JavaFX程序提供换肤功能。  
**主题**，则是定义了OS系统所有UI元素的外观，JavaFX 对主题没有直接的支持。

## JavaFX CSS

`Scene` 包含一个 `ObservableList` 对象，保存有所有的样式表的URLs，通过 `getStylessheets()` 方法可以获得 `Scene` 该对象的引用。

### JavaFX CSS 命名约定

JavaFX 采用的命名约定和CSS样式类有所不同：

- 所有控件的选择器命名都是小写
- 对单个单词的控件，如 Button，CSS选择器命名为小写形式：button
- 对多个单词的控件，如 TextField，单词之间以短线分开：text-field
- 属性名以 `-fx-` 开头。例如，常规 CSS 样式表的的 `font-size`在JavaFX CSS 中变为 `-fx-font-size`
- 变量属性命名和变量命名规则相同。如 `textAlignment` 对应的样式表属性名为 `-fx-text-alignment`

### 添加样式表

可以为JavaFX 程序添加多个样式表，对 scene 或 parent对象都可以添加。

```java
// Add two style sheets, ss1.css and ss2.css to a scene
Scene scene = ...
scene.getStylesheets().addAll("resources/css/ss1.css", "resources/css/ss2.css");

// Add a style sheet, vbox.css, to a VBox (a Parent)
VBox root = new VBox();
root.getStylesheets().add("vbox.css");
```

## 默认样式表

JavaFX 的默认样式由默认样式表 Modena.css 定义，该样式表在 jfxrt.jar 文件中。

通过 `Application` 类的如下方式设置整个应用的默认样式：

- `public static void setUserAgentStylesheet(String url)`
- `public static String getUserAgentStylesheet()`

当前默认样式为 Modena，而在 JavaFX 8 之前，默认样式为 Caspian，将默认样式设置为 Caspian:

```java
Application.setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);
```

## 添加内联样式

添加样式的方式有两种，样式表或者内联样式。 `Node` 类的 style 属性包含节点的内联样式。可以通过 `setStyle(String inlineStyle)` 方法设置。

内联样式和样式表的区别在于，内联样式只作用于单个节点，而样式表，根据选择器，可以作用于多个节点。

## 样式优先级

JavaFX 指定样式的优先级如下，依次降低：

- 内联样式（最高优先级）
- Parent 样式表
- Scene 样式表
- 使用 JavaFX API 代码指定
- 默认样式

## CSS 属性继承

JavaFX 对 CSS 属性提供两种继承类型：

- CSS 属性类型继承
- CSS 属性值继承

对类型一，JavaFX 类中声明的所有 CSS 属性，被其子类继承。例如，`Node` 类声明的 `cursor` 属性，对应的CSS属性为 `-fx-cursor`。

因为 `Node` 类是所有 JavaFX 节点的基类，所以，所有节点类型都有 `-fx-cursor` CSS 属性。

对类型二，节点可以从其容器中继承 CSS 属性值。部分属性值可以直接继承，部分属性值需要显式指定。

通过设置属性值为 `inherit`，可以显式指定该 Node 的对应属性值从 parent 继承。例如：

```css
/* Parent Node (HBox)*/
-fx-cursor: hand;
-fx-border-color: blue;
-fx-border-width: 5px;

/* Child Node (OK Button)*/
-fx-border-color: red;
-fx-border-width: inherit;
```

说明：

- `-fx-cursor` CSS 属性在 `Node` 类中声明，被所有的节点默认继承。`HBox` 覆盖该默认值为 `HAND`。`HBox`容器的节点都继承该 `-fx-cursor` 属性值。
- 边框相关的 CSS 相关的属性默认不被继承。
- -fx-cursor, -fx-text-alignment, -fx-font 属性默认从 parent 继承。

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
