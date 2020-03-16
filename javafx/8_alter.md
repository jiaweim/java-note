# Alert

- [Alert](#alert)
  - [简介](#%e7%ae%80%e4%bb%8b)
  - [AlertType](#alerttype)
  - [创建 Alert](#%e5%88%9b%e5%bb%ba-alert)
  - [Alert 的使用方式](#alert-%e7%9a%84%e4%bd%bf%e7%94%a8%e6%96%b9%e5%bc%8f)

## 简介

`Alert` 是 `Dialog` 的子类，用于弹出提示信息。JavaFX 内置多种类型的 `Alert`。
而如果要弹窗输入信息，可以用 `TextInputDialog`或 `ChoiceDialog`。

## AlertType

enum 类 `AlertType` 用于指定弹窗的类型，包括：

- `NONE`，无任何预设信息；
- `INFORMATION`，用于信息提示，弹窗包含一个 'information' 图像、标题以及 OK 按钮，点击按钮窗口关闭；
- `WARNING`，用于警告，弹窗包含一个 'warning'图像、标题以及OK按钮；
- `CONFIRMATION`，用于确认
- `ERROR`，错误提示

## 创建 Alert

创建 Alert，需要指定 `AlertType`。Alert 通过该类型，设置合适的 title, header, graphic以及 button 类型。

创建方式：

```java
Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to format your system?");
```

创建后一般要显示，Alert 为 modal & blocking 方式，即在完成该操作前阻止其他UI元素被操作，且代码执行被停止在当前。Alert 默认为 modal，修改方法：

```java
Dialog.initModality(javafx.stage.Modality);
```

是否 blocking，则从选择的显示方式决定：

```java
Dialog.showAndWait();
```

或：

```java
Dialog.show();
```

## Alert 的使用方式

1. 传统方法

```java
Optional<ButtonType> result = alert.showAndWait();
 if (result.isPresent() && result.get() == ButtonType.OK) {
     formatSystem();
 }
```

2. 传统+Optional 方法

```java
alert.showAndWait().ifPresent(response -> {
     if (response == ButtonType.OK) {
         formatSystem();
     }
 });
```

3. lambda 方法

```java
alert.showAndWait()
      .filter(response -> response == ButtonType.OK)
      .ifPresent(response -> formatSystem());
```

三种方法无有优劣，看个人习惯。
