# CSS 属性

2023-06-20, 11:42
****
## 1. CSS 属性继承

JavaFX 为 CSS 属性提供了两种类型的继承：

- CSS 属性类型继承
- CSS 属性值继承

对**类型继承**，JavaFX 类中声明的所有 CSS 属性，被其子类继承。例如，`Node` 类声明的 `cursor` 属性，对应的 CSS 属性为 `-fx-cursor`。因为 `Node` 是所有 JavaFX 控件的基类，所以所有控件类型都有 `-fx-cursor` CSS 属性。

对**值继承**，`Node` 可以从 parent 继承 CSS 属性值，这里 parent 不是父类，而是 `Scene` graph 中 `Node` 的容器。Node 的部分属性值默认从 parent 继承，而有些属性需要显式指定想要从 parent 继承。

将属性值为 `inherit`，即表示从 parent 继承属性值。例如，为 HBox 添加两个按钮，O看和 Cancel。下面为 HBox 和 OK 按钮设置 CSS 属性，没有为 Cancel 设置：

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

- `-fx-cursor` CSS 属性在 `Node` 中声明，所有控件默认继承。
- `HBox` 覆盖 `-fx-cursor` 默认值为 `HAND`，`HBox` 容器中的 `Node` 继承该 `-fx-cursor` 属性值，即 OK 和 Cancel 按钮的 `-fx-cursor` 为 `HAND`，当将鼠标放到这两个按钮上，鼠标指针变成手形状。

边框相关的 CSS 属性默认不被继承：

- HBox 设置 5px 宽的蓝色边框；
- OK 按钮设置红色变量，其 `-fx-border-width` 值为 `inherit`，表示从 HBox 继承，也是 5px.

代码如下：

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class CSSInheritance extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button okBtn = new Button("OK");
        Button cancelBtn = new Button("Cancel");

        HBox root = new HBox(10); // 10px spacing
        root.getChildren().addAll(okBtn, cancelBtn);

        // Set styles for the OK button and its parent HBox
        root.setStyle("-fx-cursor: hand;-fx-border-color: blue;-fx-border-width: 5px;");
        okBtn.setStyle("-fx-border-color: red;-fx-border-width: inherit;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("CSS Inheritance");
        stage.show();
    }
}
```

![[Pasted image 20230619193026.png|150]]

```ad-tip
`-fx-cursor`, `-fx-text-alignment` 和 `-fx-font` 默认继承。
```

## 2. CSS 属性类型

Java 以及 JavaFX 中所有值都有类型，样式中指定的 CSS 属性值同样有类型。不同类型的值语法不同。JavaFX 支持以下类型：

- inherit
- boolean
- string
- number, integer
- size
- length
- percentage
- angle
- duration
- point
- color-stop
- uri
- effect
- font
- paint
- color

```ad-note
CSS 类型与 Java 类型无关，它们只能在 CSS 样式表或内联样式中指定属性值。JavaFX 负责解析这些类型，并转换为合适的 JavaFX 类型。
```

### 2.1. inherit

`inherit` 表示从父容器继承 CSS 属性值。

### 2.2. boolean

boolean 类型值

- `true` 或 `false`
- 也可以指定为字符串："true" 或 "false"

例如，将 `TextField` 的 `-fx-display-caret` CSS 属性设置为 `false`:

```css
.text-field {
	-fx-display-caret: false;
}
```

### 2.3. string

string 类型可以用单引号或双引号括起来。字符串内部引号需转义：

- 双引号转义为` \"` 或 `\22`
- 单引号转义为 `\'` 或 `\27`

例如，用字符串设置 skin 和字体，skin 放在双引号中，font family 放在单引号中：

```css
.my-control {
	-fx-skin: "com.jdojo.MySkin";
	-fx-font: normal bold 20px 'serif';
}
```

```ad-tip
字符串值不能直接包含换行符，可以用转义表示，如 `\A` 或 `\00000a`
```

### 2.4. number 和 integer

数字可以为整数或实数，使用十进制格式。例如，将 opacity 设置为 0.60:

```css
.my-style {
	-fx-opacity: 0.60;
}
```

CSS 数值类型的属性值后面可以指定单位。长度单位包括 px, mm, cm, in, pt, pc, em 或 ex。其中 size 还可以指定为百分比，此时百分号 % 和前面数字之间不能有空格：

```css
.my-style {
	-fx-font-size: 12px;
	-fx-background-radius: 0.5em;
	-fx-border-width: 5%;
}
```

### 2.5. size

以 length 或 percentage 为单位的数字。

### 2.6. length 和 percentage

length 是数字加单位 px, mm, cm, in, pt, pc, em, ex。

percentage 是数字加百分号 %。

### 2.7. angle

angle 也是数字加单位定义。角度的单位包括：

- deg (角度)
- rad (弧度)
- grad (梯度)
- turn (turns)

例如：

```css
.my-style {
	-fx-rotate: 45deg;
}
```

### 2.8. duration

duration 是数字加上时间单位：

- “s” (second)
- “ms” (millisecond)

或者为 "indefinite"。

### 2.9. point

用于指定 x, y  坐标。可以使用空格分隔的数字指定，例如：`0 0`, `100 0`, `90 67`，或者用百分比形式，如 `2% 2%`。下面定义从点 (0, 0) 到 (100, 0) 的线性梯度颜色：

```css
.my-style {
	-fx-background-color: linear-gradient(from 0 0 to 100 0, repeat,
		red, blue);
}
```

### 2.10. color-stop

`color-stop` 用于在线性或径向颜色梯度中指定特定距离处的颜色。

`color-stop`  包含以空格分隔的颜色和距离。其中距离可以是 length 或 percentage。

`color-stop` 示例：`white 0%`, `yellow 50%` 以及 `yellow 100px`。

### 2.11. URI

URI 使用 `url(<address>)` 函数指定。如果 `<address>` 为相对值，则从 CSS 文件所在位置开始解析。

```css
.image-view {
	-fx-image: url("http://jdojo.com/myimage.png");
}
```

### 2.12. effect

drop shadow 和 inner shadow 特效可以使用 CSS 函数 `dropshadow()` 和 `innershadow()` CSS 指定：

```css
dropshadow(<blur-type>, <color>, <radius>, <spread>, <x-offset>, <y-offset>)
innershadow(<blur-type>, <color>, <radius>, <choke>, <x-offset>, <y-offset>)
```

说明：

- `<blur-type>`: `Gaussian`, `one-pass-box`, `three-pass-box`, `two-pass-box`
- `<color>` 指定阴影颜色
- `<radius>` 指定阴影模糊半径，从 0.0 到 127.0
- `spread/choke` 在 0.0 到 1.0 之间
- `<x-offset>, <y-offset>` 指定阴影在 x 和 y 方向的偏移（px）

示例：

```css
.drop-shadow-1 {
	-fx-effect: dropshadow(gaussian, gray, 10, 0.6, 10, 10);
}
.drop-shadow-2 {
	-fx-effect: dropshadow(one-pass-box, gray, 10, 0.6, 10, 10);
}
.inner-shadow-1 {
	-fx-effect: innershadow(gaussian, gray, 10, 0.6, 10, 10);
}
```

### 2.13. font

字体包括四个属性：family, size, style, weight。指定 font CSS 属性的方式有两种：

- 分别指定 4 个 CSS 属性：`-fx-font-family`, `-fx-font-size`, `-fx-font-style`, `-fx-font-weight`
- 使用 `-fx-font` 指定所有四个属性

font family 是字符串值，可以是系统上实际可用的子图，如 "Arial", "Times"，也可以是通用 family 名称，例如 "serif", "sans-serif", "monospace"。

font size 的单位可以是 px, em, pt, in, cm。如果没指定单位，默认为 px。

font style 的可选值：normal, italic, oblique。

font weight 的可选值：normal, bold, bolder, lighter, 100, 200, 300, 400, 500, 600, 700,
800, or 900.

- 分别设置 font 的 4 个属性

```css
.my-font-style {
	-fx-font-family: "serif";
	-fx-font-size: 20px;
	-fx-font-style: normal;
	-fx-font-weight: bolder;
}
```

- 使用 `-fx-font` 一次设置 font 属性

```css
-fx-font: <font-style> <font-weight> <font-size> <font-family>;
```

例如：

```css
.my-font-style {
	-fx-font: italic bolder 20px "serif";
}
```

### 2.14. paint 和 color

`paint` 类型指定颜色，如形状的填充颜色、按钮的背景颜色。指定颜色的方式有三类：

- `linear-gradient()` 函数
- `radial-gradient()` 函数
- 各种颜色值或颜色函数

`linear-gradient()` 和 `radial-gradient()`  的字符串格式可以参考 [线性梯度颜色](../color/color3_linear_gradient.md) 和 [径向梯度颜色](../color/color4_radial_gradient.md)。

例如：

```css
.my-style {
    -fx-fill: linear-gradient(from 0% 0% to 100% 0%, black 0%, red 100%);
    -fx-background-color: radial-gradient(radius 100%, black, red);
}
```

指定纯色的方式就多了：

- 颜色名称
- Using looked-up colors
- `rgb()` 和 `rgba()` 函数
- RGB hex 表示
- `hsb()` 和 `hsba()` 函数
- `derive()` 和 `ladder()` 函数


1. 使用预定义颜色的名称，如 red, blue, green, aqua 等

```css
.my-style {
    -fx-background-color: red;
}
```

2. 查找颜色

可以先在 Node 或其它父容器预定义颜色属性，然后通过名称引用其值。例如：

```css
.root {
    my-color: black;
}

.my-style {
    -fx-fill: my-color;
}
```

3. rgb() 和 rgba() 函数

```css
.my-style-1 {
    -fx-fill: rgb(0, 0, 255);
}

.my-style-2 {
    -fx-fill: rgba(0, 0, 255, 0.5);
}
```

4. 十六进制格式

十六进制支持 `#rrggbb` 和 `#rgb` 两种格式。

```css
.my-style-1 {
    -fx-fill: #0000ff;
}
.my-style-2 {
    -fx-fill: #0bc;
}
```

5. `hsb(hue, saturation, brightness)` 和 `hsba(hue, saturation, brightness, alpha)`

```css
.my-style-1 {
    -fx-fill: hsb(200, 70%, 40%);
}

.my-style-2 {
    -fx-fill: hsba(200, 70%, 40%, 0.30);
}
```

6. `derive()` 和 `ladder()` 函数

从其它颜色计算新的颜色。

derive 函数包含两个参数：

```java
derive(color, brightness)
```

生成指定颜色的更亮或更暗的版本。亮度范围 -100% 到 100%：

- `-100%` 全黑
- 0% 亮度不变
- 100% 全白

例如，生成加深 20% 的红色：

```css
.my-style {
    -fx-fill: derive(red, -20%);
}
```

`ladder()` 包含一个 color 以及多个 color-stop:

```java
ladder(color, color-stop-1, color-stop-2, ...)
```

可以将 ladder() 看作以 color-stops 创建渐变色，然后使用指定颜色的亮度，得到最终颜色。如果指定颜色亮度为 x%，则返回渐变色 %x 位置的颜色。例如，如果亮度为 0%，则返回渐变色 0.0 位置的颜色；如果亮度为 40%，则返回渐变色 0.4 位置的颜色。

```css
.root {
    my-base-text-color: red;
}

.my-style {
    -fx-text-fill: ladder(my-base-text-color, white 29%, black 30%);
}
```

ladder() 会根据 my-base-text-color 的亮度返回白色或黑色。如果亮度低于 29%，则返回白色；否则返回黑色。

使用该技术，可以动态更改 JavaFX 程序的颜色。默认样式表 modena.css 定义了一些基础颜色，然后使用 derive() 和 ladder() 派生出其它不同亮度的颜色。

在 root 类中定义基础颜色，便于在整个应用范围更改颜色。
