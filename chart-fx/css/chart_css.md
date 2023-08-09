Chart 继承 Parent，可以通过如下方式设置样式表：

```java
chart.getStylesheets()
     .setAll(Objects.requireNonNull(CssStylingSample.class.getResource(newVal), 
     "could not load css file: " + newVal).toExternalForm());
```

清除样式表：

```java
chart.getStylesheets().clear();
```