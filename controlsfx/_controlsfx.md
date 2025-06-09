# ControlsFX

## Glyph font

ControlsFX 支持 font-packs，如 Font Awesome 和 IcoMoon。

不仅可以作为 button 的 graphics，还可以在其它 API 中使用。例如，使用 `@ActionProxy` API：

```java
@ActionProxy(text=”Action Text”, image=”font>FontAwesome:STAR”)
```

![GlyphFont](./images/glyphFont.png)

`org.controlsfx.glyphfont.FontAwesome` 定义 [Font Awesome](https://fontawesome.com/) 字体。

要在 JavaFX 中使用 `FontAwesome` 或任何其它字体，首先要获得 `FontAwesome` glyph-font。具体操作：

```java
GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
```

这段代码之所以能工作，是因为 `GlyphFontRegistry` 类在运行时会动态查找 glyph-fonts。

加载完字体后，就可以直接创建 `FontAwesome.Glyph` node 并将其放在界面中。例如：

```java
new Button("", fontAwesome.create(\uf013).fontColor(Color.RED));
```

当然，前提是你知道 `\uf013` 映射到 'gear' icon，这在使用时不是很直观。所以 controlsfx 提供了一种更直观的方法：

```java
new Button("", fontAwesome.create(FontAwesome.Glyph.GEAR));
```

或者：

```java
new Button("", fontAwesome.create("GEAR"));
```

也可以不显式创建 `GlyphFont`，直接使用 `org.controlsfx.glyphfont.Glyph` 构造函数，也可以实现相同效果：

```java
new Button("", new Glyph("FontAwesome","GEAR");
```

也可以在 FXML 中使用上述 `Glyph`。

## PopOver

`PopOver` 用于显示弹窗。弹窗具有非常轻量级的外观（没有窗口装饰）和一个指向 owner 的箭头，如下图所示：

![](images/2023-08-09-16-24-50.png)

由于弹窗的性质，当用户拖动它时，弹窗随着父窗口移动。

可以将 PopOver 从 owner node 拖离开，它不再显示箭头，而是显示标题和关闭图标。



## 参考

- https://controlsfx.github.io/

