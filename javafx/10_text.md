# 文本组件

- [文本组件](#%e6%96%87%e6%9c%ac%e7%bb%84%e4%bb%b6)
  - [概述](#%e6%a6%82%e8%bf%b0)
- [创建 Text](#%e5%88%9b%e5%bb%ba-text)
  - [Text Origin](#text-origin)
- [多行文本显示](#%e5%a4%9a%e8%a1%8c%e6%96%87%e6%9c%ac%e6%98%be%e7%a4%ba)
- [字体设置](#%e5%ad%97%e4%bd%93%e8%ae%be%e7%bd%ae)
  - [创建字体](#%e5%88%9b%e5%bb%ba%e5%ad%97%e4%bd%93)
  - [访问已安装字体](#%e8%ae%bf%e9%97%ae%e5%b7%b2%e5%ae%89%e8%a3%85%e5%ad%97%e4%bd%93)
  - [使用自定义字体](#%e4%bd%bf%e7%94%a8%e8%87%aa%e5%ae%9a%e4%b9%89%e5%ad%97%e4%bd%93)
- [设置文本 Fill 和 Stroke](#%e8%ae%be%e7%bd%ae%e6%96%87%e6%9c%ac-fill-%e5%92%8c-stroke)
- [文字装饰](#%e6%96%87%e5%ad%97%e8%a3%85%e9%a5%b0)
- [字体平滑](#%e5%ad%97%e4%bd%93%e5%b9%b3%e6%bb%91)
- [使用CSS](#%e4%bd%bf%e7%94%a8css)
- [TextField](#textfield)

## 概述

所有渲染文本相关的类都在 `javafx.scene.text` 包中，如 `Text`, `TextAlignment`, `Font`, `FontWeight`等。

`Text` 类继承自 `Shape`，所以 `Shape` 相关的属性都可以应用在 `Text` 上。

不同行通过 `\n` 来区分。

# 创建 Text
`Text` 包含如下几种构造函数：
```java
// Create an empty Text Node and later set its text
Text t1 = new Text();
t1.setText("Hello from the Text node!");

// Create a Text Node with initial text
Text t2 = new Text("Hello from the Text node!");

// Create a Text Node with initial text and position
Text t3 = new Text(50, 50, "Hello from the Text node!");
```

`Text` 的大小自动根据字体计算而来。

## Text Origin
除了 local 和 parent 坐标系，`Text` 还有一个额外的坐标系。`Text` 使用三个属性定义该坐标系：x, y, textOrigin.

x, y 定义了文本的原点坐标。`textOrigin` 为 `VPos` 类型，该 enum 的值有 `BASELINE`, `TOP`, `CENTER`, `BOTTOM`，默认值为 `BASELINE`。如下图所示，该图显示了 local 坐标系和文本坐标系。 

![](2019-06-05-16-34-48.png)

`textOrigin` 定义了文本坐标系的 x 轴相对文本高度的位置。
- `VPos.TOP` 文本的顶端和 `layoutY` 对齐
- `VPos.BASELINE` 文本的基线和 layoutY 对齐
- `VPos.BOTTOM` 文本的底端和 layouY 对齐
- `VPos.CENTER` 文本坐标系的 x-axis 和文本的中间段对齐

`Text` 类的 `baselineOffset` 属性，表示 text 的 baseline 和 top的距离，该值和字体的最大 ascent 值相等。

当需要将 `Text` 和其他 node 垂直对齐，需要该属性。如果要将 `Text` 垂直居中，需要设置 `textOrigin` 为 `VPos.TOP`。否则采用默认值 `VPos.BASELINE`，文本会明显高于中间位置。


# 多行文本显示
`Text` 可以显示多行文本：
- 碰到换行符 '\n' 会自行换行
- `Text` 包含一个 `wrappingWidth` 属性，默认值为0.0。该值为像素值，而非字符数。当该值大于0，则每行文本以指定像素值换行。
- `lineSpacing` 指定两行之间的距离，默认为0.0
- `textAlignment` 指定文本水平对齐方式。最宽的文本定义外框的宽度，该值对单行文本无效。`TextAlignment` enum 包含如下几个选项.

TextAlignment
- `LEFT`, 左对齐，默认值
- `RIGHT`, 右对齐
- `CENTER`, 居中
- `JUSTIFY`，两端对齐

# 字体设置
`Text`的 `font` 属性定义文本字体。默认字体来自 `System` 字体，`Regular` 风格，字体大小取决于系统及其桌面设置。

字体包括 _family_ 和 _family name_，font family 也称为 _typeface_，两个都可以以字体称谓。字体定义了字符的字形。

## 创建字体
`Font` 类提供了两个构造函数：
- Font(double size)，以系统字体创建指定大小的 `Font` 对象
- Font(String name, double size)，以指定full name和大小创建 `Font` 对象，如果没有找到 full name 对应的字体，使用默认的系统字体。

因为字体的 full name 很难记住，所以 `Font` 类提供了许多工厂方法方便字体的创建：
- font(double size)
- font(String family)
- font(String family, double size)
- font(String family, FontPosture posture, double size)
- font(String family, FontWeight weight, double size)
- font(String family, FontWeight weight, FontPosture posture, double size)

`FontWeight` 指定字体的粗细，可用值有：`THIN`, `EXTRA_LIGHT`, `LIGHT`, `NORMAL`, `MEDIUM`, `SEMI_BOLD`, `BOLD`, `EXTRA_BOLD`, `BLACK`。

`FontPosture` 是否斜体，可用值有：`REGULAR` 和 `ITALIC`。

使用 `Font` 的 `getDefault()` 静态方法可获得系统的默认字体。

## 访问已安装字体
下面是 `Font` 类中访问已安装字体的静态方法：
- `List<String> getFamilies()`
- `List<String> getFontNames()`
- `List<String> getFontNames(String family)`

例：输出已安装字体的 family name
```java
// Print the family names of all installed fonts
for(String familyName: Font.getFamilies()) {
    System.out.println(familyName);
}
```

例：输出已安装字体的 full name
```java
// Print the full names of all installed fonts
for(String fullName: Font.getFontNames()) {
    System.out.println(fullName);
}
```

例：输出指定 family name 字体的 full name
```java
// Print the full names of “Times New Roman” family
for(String fullName: Font.getFontNames("Times New Roman")) {
    System.out.println(fullName);
}
```

## 使用自定义字体
`Font` 包含如下两个静态方法：
- loadFont(InputStream in, double size)
- loadFont(String urlStr, double size)

使用这两个方法，可以载入本地或网络上的字体。

# 设置文本 Fill 和 Stroke
`Text` 继承自 `Shape`，所以有 fill 和 stroke。`Text`的默认 stroke 为 null，默认 fill 为 `Color.BLACK`。

# 文字装饰
`Text` 类包含两个 boolean 属性值，用于添加文字装饰：
- strikethrough, 删除线
- underline, 下划线

两个属性默认为 false。设置方法很简单，如下：
```java
Text t1 = new Text("It uses the \nunderline decoaration.");
t1.setUnderline(true);

Text t2 = new Text("It uses the \nstrikethrough decoration.");
t2.setStrikethrough(true);
```

# 字体平滑
`Text` 的 `fontSmoothingType` 属性，可用于添加灰度或LCD字体平滑。

`FontSmoothingType` enum: `GRAY`, `LCD`。默认为 `GRAY`。

# 使用CSS
`Text` 没有默认的CSS个性化类名，除了`Shape`支持的CSS属性，`Text`支持如下的CSS属性：
- `-fx-font`
- `-fx-font-smoothing-type`
- `-fx-text-origin`
- `-fx-text-alignment`
- `-fx-strikethrought`
- `-fx-underline`

# TextField
用于输入单行文本。对多行文本，使用 `TextArea` 方法。

文本中的换行符和 tab 字符被移除。

TextField 提供了一个默认的 Context Menu.

|属性|功能|
|---|---|
|alignment|文本对齐方式，默认值 CENTER_LEFT|
|onAction|ActionEvent handler, 用于处理输入事件|
|prefColmnCount|宽度。默认值 12，宽度 1 可以显示一个大写的 W|
|text|存储显示的文本|

方法：
|方法|功能|
|---|---|
|setPrefColumnCount||
|getText()|获得文本|
|setText(String newText)|设置文本|
