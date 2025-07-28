#  JavaFX CSS Color

## Named Colors

## Looked-up Colors

## RGB Colors

RGB 颜色模型用于数值颜色。它有多种不同的形式：

```css
#<digit><digit><digit>
#<digit><digit><digit><digit><digit><digit>
rgb( <integer> , <integer> , <integer> )
rgb( <integer> %, <integer>% , <integer>% )
rgba( <integer> , <integer> , <integer> , <number> )
rgba( <integer>% , <integer>% , <integer> %, <number> )
```

**示例**：为 label 指定颜色，效果相同

```css
.label { -fx-text-fill: #f00 } /* #rgb */
.label { -fx-text-fill: #ff0000 } /* #rrggbb */
.label { -fx-text-fill: rgb(255,0,0) }
.label { -fx-text-fill: rgb(100%, 0%, 0%) }
.label { -fx-text-fill: rgba(255,0,0,1) }
```

