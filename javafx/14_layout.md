
## TabPane

`TabPane`包含两部分：标题和内容。标题包含多个组成部分，如下图所示：

![TabPane](images/2019-06-05-17-40-06.png)

说明：

- "Headers region" 是标题全部区域
- Tabheaderbackground为标题背景
- Controlbuttons则是在分栏太多无法显示时，用于选择特定tab
- tab区域包含一个`Label`和一个closebutton，Label用于显示tab的标题和图标，closebutton为关闭tab的按钮

### 设置 Tab 大小

`TabPane` 将其区域分为两部分：

- 标题区
- 内容区

标题区显示 tab 的标题，内容区显示当前选择的 tab 的内容。

内容区的大小根据其内容自动计算大小，`TabPane` 包含如下属性用于设置标题区的尺寸：

- `tabMinHeight`
- `tabMaxHeight`
- `tabMinWidth`
- `tabMaxWidth`

默认宽度和高度的最小值为0，最大值为 `Double.MAX_VALUE`。而默认尺寸则是根据内容自动计算。如果你希望所有 tab 具有相同大小的标题区，可以将高度和宽度的最小值和最大值设置为相同值。如下所示：

```java
TabPane tabPane = new TabPane();
tabPane.setTabMinHeight(30);
tabPane.setTabMaxHeight(30);
tabPane.setTabMinWidth(100);
tabPane.setTabMaxWidth(100);
```
