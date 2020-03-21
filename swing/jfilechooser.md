# 使用

## 构造函数

```java
JFileChooser(File currentDirectory); // 指向某个文件
JFileChooser(String currentDirectoryPath) // 指向某个路径
```
## 过滤器
```java
FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG&GIF Images","jpg","gif");
dlg.setFileFilter(filter);
```

## 设置选择模式
```java
dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
dlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
dlg.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
```

## 对话框模式
```java
int result = dlg.showOpenDislog(this);
int result = dlg.showSaveDialog(this);
```

## 返回值
```java
static int APPROVE_OPTION;//选择确认后返回该值
static int CALCEL_OPTION;//选择calcel后返回该值
static int ERROR_OPTION;//发生错误后返回该值。
```