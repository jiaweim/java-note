# TableView CSS

2023-08-03, 21:12
****
## 简介

TableView 的 column-header, cells, placeholder 等都可以使用 CSS 定义样式。将 CSS 应用于 TableView 比较复杂。

- TableView 的 CSS 样式类名默认为 table-view
- 单元格的 CSS 样式类名默认为 table-cell
- row 的 CSS 样式类名默认为 table-row-cell
- column 标题的 CSS 样式类名默认为 column-header

```css
/* Set the font for the cells */
.table-row-cell {
    -fx-font-size: 10pt;
    -fx-font-family: Arial;
}
/* Set the font size and text color for column headers */
.table-view .column-header .label{
    -fx-font-size: 10pt;
    -fx-text-fill: blue;
}
```

TableView 支持的 CSS pseudo-classes:

- cell-selection：启用 cell-level selection 时应用
- row-selection：启用 row-level selection 时应用
- constrained-resize：当 column-resize-policy 为 CONSTRAINED_RESIZE_POLICY 时应用

TableView 默认隔行高亮显示。下面删除隔行高亮功能，将所有 row 的背景设置白色：

```css
.table-row-cell {
    -fx-background-color: white;
}

.table-row-cell .table-cell {
    -fx-border-width: 0.25px;
    -fx-border-color: transparent gray gray transparent;
}
```

对多余的空间，TableView 显示空 rows 来填充。下面移除这些空 rows (其实只是让这些 rows 不可见)：

```css
.table-row-cell:empty {
    -fx-background-color: transparent;
}
.table-row-cell:empty .table-cell {
    -fx-border-width: 0px;
}
```

TableView 包含多个子结构：

- column-resize-line
- column-overlay
- placeholder
- column-header-background

column-resize-line 为 Region 类型，当用户 resize column 时显示。

column-overlay 也是 Region 类型，在移动的 column 上面显示一个涂层。

placeholder 为 StackPane 类型，当 TableView 不包含 column 或 data 时显示。例如：

```css
/* Make the text in the placeholder red and bold */
.table-view .placeholder .label {
    -fx-text-fill: red;
    -fx-font-weight: bold;
}
```

column-header-background 也是 StackPane 类型，它是  column header 下面的区域。column-header-background 包含多个子结构：

- filler，Region 类型，标题区域 TableView 右边 edge 和 最右边 column 中间的区域
- show-hide-columns-button，StackPane 类型，显示菜单按钮的区域
