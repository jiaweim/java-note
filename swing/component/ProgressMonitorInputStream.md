# ProgressMonitorInputStream

## 简介

`ProgressMonitorInputStream` 是一个输入流，它使用 `ProgressMonitor` 展示读取进度。

如果读取时间较长，弹窗出现 `ProgressMonitor`，用户可以选择弹窗的 "Cancel" 中断读取，输入流抛出 `InterruptedIOException`。

## 创建 ProgressMonitorInputStream

和其它过滤流一样，ProgressMonitorInputStream 在已有流的基础上创建。另有两个参数用于配置 ProgressMonitor：

```java
public ProgressMonitorInputStream(
    Component parentComponent, 
    Object message, 
    InputStream inputStream)
```

与 JOptionPane 和 ProgressMonitor 一样，其 message 参数为 Object 类型，而非 String。

**示例：** 创建 ProgressMonitorInputStream

```java
FileInputStream fis = new FileInputStream(filename);
ProgressMonitorInputStream pmis = 
    new ProgressMonitorInputStream(parent, "Reading " + filename, fis);
```

## 使用 ProgressMonitorInputStream

与常规输入流使用方式一样，创建 ProgressMonitorInputStream 读取数据。如果读取不够快，弹窗 ProgressMonitor 显示进度。出现弹窗扣，用户可以点击 Cancel 按钮取消读取，抛出 `InterruptedIOException`，异常的 bytesTransferred 字段表示成功读取的字节数。

