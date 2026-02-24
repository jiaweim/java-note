# Node 数据

每个 Node 维护了一个包含用户自定义属性的 observable map。可以用它来存储任何信息。

假设你有一个 `TextField` 用于操作用户名，可以将最初从数据库中检索到的人名存储为 TextField 的属性。稍后可以使用该属性重置用户名，或生成 UPDATE 语句来更新数据库中的用户名。

属性还可以用来存储帮助文本。当 node 获取焦点时，可以读取并显示帮助信息，以帮助用户理解其使用方法。

Node 的 getProperties() 方法 ObservableMap<Object, Object>，通过该返回值可以添加或删除属性。例如：

```java
TextField nameField = new TextField();
...
ObservableMap<Object, Object> props = nameField.getProperties();
props.put("originalData", "Advik");
```

下面从 nameField 读取 "originalData" 属性值：

```java
ObservableMap<Object, Object> props = nameField.getProperties();
if (props.containsKey("originalData")) {
	String originalData = (String)props.get("originalData");
} else {
	// originalData property is not set yet
}
```

Node 有两个用于自定义属性的便捷方法：`setUserData(Object value)` 和 `getUserData()`。`setUserData()` 使用相同的 `ObservableMap` 存储数据。Node 类使用一个内部 Object 作为 key 存储值：

```java
nameField.setUserData("Saved"); // Set the user data
...
String userData = (String)nameField.getUserData(); // Get the user data
```

`Node` 的 `hasProperties()` 可用来检查 Node 是否包含自定义属性。

