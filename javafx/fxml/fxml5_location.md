# 指定 Location

2023-07-12, 17:34
****
以 @ 开头的 attribute 值表示位置：

- 如果 @ 后跟着 /，则是相对 CLASSPATH 的路径
- 如果 @ 后没有 /，则是相对 FXML 文件的路径

例如，下面的 URL 是相对 FXML 文件的路径：

```xml
<ImageView>
    <Image url="@resources/picture/ksharan.jpg"/>
</ImageView>
```

而下面的 URL 是相对 CLASSPATH 的路径：

```xml
<ImageView>
    <Image url="@/resources/picture/ksharan.jpg"/>
</ImageView>
```

如果希望使用 @ 符号本身，则用 \ 进行转义，如 `\@not-a-location`。
