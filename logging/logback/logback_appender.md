# Appender

## 自定义 Appender

通过继承 `AppenderBase` 很容易自定义 appender。它支持 filters, status messages 等大多数 appender 的通用功能。派生类只需要实现一个方法，即 `append(Object eventObject)`。

下面将列出的 `CountingConsoleAppender` 将一定数量的日志事件发送到控制台。当事件数达到限制，关闭日志。它使用 `PatternLayoutEncoder` 格式化事件，并接受一个名为 `limit` 的参数。

```java

```