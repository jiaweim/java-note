# PasswordField

2023-07-24, 20:18
****
`PasswordField` 是一个文本输入控件。它继承自 TextField，除了会遮挡文本，其工作原理与 TextField 基本相同。

PasswordField 不显示实际输入的字符，对输入的每个字符显示一个 echo 字符。默认 echo 字符为 `.`。如下所示：

![|400](Pasted%20image%2020230724201328.png)

`PasswordField` 类只提供了一个无参构造函数。可以通过 setText() 和 getText() 设置和查询实际的文本。一般来说，不需要设置 password text，而是由用户输入：

```java
// Create a PasswordField
PasswordField passwordFld = new PasswordField();
...
// Get the password text
String passStr = passwordFld.getText();
```

PasswordField 覆盖了 TextInputControl 的 cut() 和 copy() 方法，使它们成为无效操作。即不能将 PasswordField 的 text 转移到剪切板。

PasswordField 的默认 CSS 样式类名为 password-field。PasswordField 具有 TextField 所有样式属性，没有添加额外样式属性。
