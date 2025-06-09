# JavaFX 对话框

2025-06-04⭐
@author Jiawei Mao
***
## 简介

JavaFX 提供了一套用户向用户发出警报、查询和通知的 dialog api。

- `Alert`：使用方便的预定义 dialog，主要用于发出警告信息
- `TextInputDialog`：提示用户输入文本
-  `ChoiceDialog`：提示用户从选项列表选择
- `Dialog` 和 `DialogPane`：自定义 dialog

类图如下：

![image-20250604155012555](./images/image-20250604155012555.png)

dialog 涉及两个术语：模态（modal）和阻塞（blocking）

- 当**模态（modal）**对话框在另一个窗口上面显示，会阻止用户点击对话框下面的窗口，直到用户关闭对话框
- **阻塞对话框**（blocking）会导致代码在显示对话框的那一行代码停止执行。对话框关闭后，代码从停止的地方继续执行。阻塞对话框可以看作一种同步对话框，使用起来更简单，开发者可以从对话框获取值并继续执行，而无需依赖 listener 或 callback

在 JavaFX 中，所有对话框默认都是模态的，不够可以使用 `initModality(Modality)` 将其设置为非模态。

阻塞对话框也是可以设置，用 `showAndWait()` 设置阻塞对话框；用 `show()` 设置非阻塞对话框。

## Alert

`Alert` 是 `Dialog` 的子类，用于弹出提示信息。`Alert` 提供了许多内置选项，包含不同的 icons 和默认按钮。

enum 类 `AlertType` 用于指定弹窗的类型，对应不同按钮和 icons，包括：

- `NONE`，无任何预设信息，除非需要自定义实现，否则避免使用
- `INFORMATION`，用于信息提示，包含一个 'information' 图像、标题以及 OK 按钮，点击按钮窗口关闭
- `WARNING`，用于警告，弹窗包含一个 'warning'图像、标题以及OK按钮；
- `CONFIRMATION`，用于确认，显示蓝色问号以及 "Cancel", "OK" 按钮
- `ERROR`，错误提示，显示一个 "X" image 和一个 "OK" 按钮

### 创建 Alert

创建 `Alert`，需要指定 `AlertType`。`Alert` 通过该类型，设置合适的 title, header, graphic以及 button 类型。

创建方式：

```java
Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to format your system?");
```

创建后一般要显示，`Alert` 为 modal & blocking 方式，即在完成该操作前阻止其他UI元素被操作，且代码执行被停止在当前。Alert 默认为 modal，修改方法：

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

### Alert 的使用方式

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

## ChoiceDialog

`ChoiceDialog` 用于向用户显示选项列表的对话框。用户最多可以从中选择一项。换言之，`ChoiceBox` 使用诸如 `ChoiceBox`, `ComboBox` 之类的控件让用户进行选择。随后，将选项返回给开发人员。

创建：

```java
List<String> colors = List.of("Red", "Yellow", "Green");
ChoiceDialog choiceDialog = new ChoiceDialog<>("Yellow", colors);
choiceDialog.showAndWait();
```



## TextInputDialog

## Dialog & DialogPane

