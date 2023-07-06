# 设置 Cursor

2023-07-06, 11:34
****
## 1. 简介

`javafx.scene.Cursor` 类表示鼠标光标。Cursor 类中定义了许多常量，如 HAND, CLOSED_HAND, DEFAULT, TEXT, NONE, WAIT 等，代表不同的鼠标光标类型。

## 2. 设置光标

将 Scene 的光标设置为 WAIT：

```java
Scene scene;
...
scene.setCursor(Cursor.WAIT);
```

## 3. 自定义光标

Cursor 类包含如下 static 方法：

```java
public static Cursor cursor(final String identifier)
```

如果 `identifier` 是标准光标名称，则返回对应的标准光标；否则将 `identifier` 视为 bitmap 的 URL，以该 bitmap 创建新的光标。例如，下面以 mycur.png 创建光标：

```java
// 以 bitmap 自定义光标
URL url = getClass().getClassLoader().getResource("mycur.png");
Cursor myCur = Cursor.cursor(url.toExternalForm());
scene.setCursor(myCur);

// 使用名称获取标准光标
Cursor waitCur = Cursor.cursor("WAIT")
scene.setCursor(waitCur);
```

