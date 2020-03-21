
# 简介
applet是一种java程序，一般运行在支持Java的Web浏览器内。运行applet所需的大多数图形支持能力都内置于浏览器中，不需要额外创建框架。

目前 applet 已被废弃。

# applet viewer
applet 程序由嵌入在Web页中的 applet HTML 标识符来运行的。当我们使用支持Java的Web浏览器，或JDK所附带的appletviewer来浏览该Web页时，就可以看到applet的运行结果。

作为appletviewer的参数使用的最小HTML文件：
```html
<title>Applet Title</title>
<hr>
    <applet code="applet_name.class" width=width height=height>
    </applet>
<hr> 
```

在applet程序结构中，并不需要main()语句，因为applet程序的运行是由浏览器来控制的。
