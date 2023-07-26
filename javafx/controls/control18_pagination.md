# Pagination

2023-07-25, 10:06
****
## 1. 简介

Pagination 分页显示内容，如搜索结果。Pagination 控件如下所示：

![|200](Pasted%20image%2020230725085947.png)
Pagination 控件包含页数。每个页面有个索引，从 0 开始。

Pagination 控件分为两个区域：

- 内容 area
- 导航 area

内容 area 显示当前页内容。导航 area 用于从一个页面跳转到另一个页面。Pagination 的分区如下所示：

![|400](Pasted%20image%2020230725090234.png)
Page indicator 默认使用 tool-tip 显示页码，可以通过 CSS 属性禁用。

## 2. 创建 Pagination

Pagination 提供了多个构造函数。

- 默认构造函数指定的页数不确定，选择页索引 0

```java
// Indeterminate page count and first page selected
Pagination pagination1 = new Pagination();
```

当页数不确定，page indicator label 显示 "x/..."，其中 x 为当前页面 index+1。

- 构造时指定页数

```java
// 5 as the page count and first page selected
Pagination pagination2 = new Pagination(5);
```

- 构造时指定页数和选择页

```java
// 5 as the page count and second page selected (page index starts at 0)
Pagination pagination3 = new Pagination(5, 1);
```

Pagination 声明了一个 INDETERMINATE 常量，用于指定不确定页数：

```java
// Indeterminate page count and second page selected
Pagination pagination4 = new Pagination(Pagination.INDETERMINATE, 1);
```

## 3. 属性

Pagination 声明了如下属性：

- currentPageIndex
- maxPageIndicatorCount
- pageCount
- pageFactory

`currentPageIndex` 属性为整数类型，表示当前显示页面的索引：

- 默认为 0
- 可以通过构造函数或 setCurrentPageIndex() 设置
- 设置值小于 0 时取 0
- 设置大于页数-1 时取 页数-1
- 为 currentPageIndex 添加 ChangeListener 监听选择页

maxPageIndicatorCount 属性为整数类型，表示 page indicator 显示的最大页面数：

- 默认为 10
- 当设置值过大，会自动减小以保证控件能够显示
- 通过 setMaxPageIndicatorCount() 方法设置

pageCount 属性为整数类型，表示 Pagination 的页数：

- 值不能小于 1
- 默认 indeterminate，即不确定
- 可以通过构造函数或 setPageCount() 方法设置

pageFactory 为 `Callback<Integer,Node>` 类型，用于生成页面：

- 当需要显示指定页面，Pagination 将页面索引传入 Callback 返回 Node
- Pagination 将返回的 Node 作为当前页显示

设置 Pagination 的 pageFactory：

```java
// Create a Pagination with an indeterminate page count
Pagination pagination = new Pagination();

// Create a page factory that returns a Label
Callback<Integer, Node> factory = 
        pageIndex -> new Label("Content for page " + (pageIndex + 1));

// Set the page factory
pagination.setPageFactory(factory);
```

```ad-note
pageIndex 不存在时 pageFactory 的 call() 方法应返回 null。call() 返回 null 时当前页面不变。
```

**示例：** Pagination

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PaginationTest extends Application {

    private static final int PAGE_COUNT = 5;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Pagination pagination = new Pagination(PAGE_COUNT);

        // Set the page factory
        pagination.setPageFactory(this::getPage);

        VBox root = new VBox(pagination);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Pagination Controls");
        stage.show();
    }

    public Label getPage(int pageIndex) {
        Label content = null;

        if (pageIndex >= 0 && pageIndex < PAGE_COUNT) {
            content = new Label("Content for page " + (pageIndex + 1));
        }
        return content;
    }
}
```

![|350](Pasted%20image%2020230725093400.png)

page indicator 可能为数字或圆形按钮。默认为数字按钮。

Pagination 类包含一个 String 常量 `STYLE_CLASS_BULLET`，可用于设置为圆形按钮。

例如，将 Pagination 的 page indicator 设置为圆形按钮：

```java
Pagination pagination = new Pagination(5);

// Use bullet page indicators
pagination.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
```

![|250](Pasted%20image%2020230725094219.png)

## 4. CSS

Pagination 的 CSS 样式类名为 pagination。

Pagination 添加了多个 CSS 属性：

- -fx-max-page-indicator-count
- -fx-arrows-visible
- -fx-tooltip-visible
- -fx-page-information-visible
- -fx-page-information-alignment

`-fx-max-page-indicator-count` 属性指定 page indicators 显示的最大页面数，默认为 10.

`-fx-arrows-visible` 属性指定 previous 和 next page 按钮是否可见，默认 true。

`-fx-tooltip-visible` 属性指定鼠标悬停在 page indicator 上时是否显示 tool-tip，默认 true。

`-fx-page-information-visible` 属性指定是否显示选择页 label，默认 true。

`-fx-page-information-alignment` 属性指定所选页面 Label 相对 page indicators 的位置，可选值包括：top, right, bottom, left，默认 bottom，即在页面下方显示 page indicator。

Pagination 包含两个 StackPane 类型的子结构：

- page
- pagination-control

`page` 子结构表示 content area。

pagination-control 子结构表示导航 area，包含如下子结构：

- left-arrow-button
- right-arrow-Button
- bullet-button
- number-button
- page-information

left-arrow-button 和 right-arrow-button 为 Button 类型，表示 previous 和 next page 按钮。

- left-arrow-button 还有一个 left-arrow 子结构，为 StackPane 类型，表示 previous page button 的箭头
- right-arrow-button 包含一个 right-arrow 子结构，为 StackPane 类型，表示 next page button 的箭头

bullet-button 和 number-button 为 ToggleButton 类型，表示 page indicators。

page-information 子结构为 Label 类型，包含选择页信息。

pagination-control 包含一个 HBox 类型的 control-box 子结构，该子结构包含 previous, next page buttons 和 page indicators。

**示例：** Pagination CSS

使选择页的 label 不可见，将 page background 设置为 lightgray，为包含 previous, next 和 page indicator buttons 的 HBox 添加 border。Pagination 的 CSS 详情可参考 modena.css

```css
.pagination {
    -fx-page-information-visible: false;
}

.pagination > .page {
    -fx-background-color: lightgray;
}

.pagination > .pagination-control > .control-box {
    -fx-padding: 2;
    -fx-border-style: dashed;
    -fx-border-width: 1;
    -fx-border-radius: 5;
    -fx-border-color: blue;
}
```