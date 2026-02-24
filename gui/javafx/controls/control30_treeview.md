# TreeView


## 简介

`TreeView` 以树状结构显示分层数据。树的每项数据以 `TreeItem` 类表示。

- `TreeItem` 分为 branch 和 leaf 两种类型。branch-node 有 `expanded` 和 `collapsed` 两种状态
- `TreeItem` 使用 `TreeCell` 渲染数据
- `TreeCell` 默认不可编辑，可以使用 `cellFactory` 自定义

`TreeView` 是一个虚拟化控件，它只创建当前高度能显示的 `TreeCell`。从而能够使用 TreeView 查看大量数据，而不需要使用太多内存。

`TreeView` 的第一个 `TreeItem` 没有父节点，称为 root-node。root-node 默认可见。调用 `TreeView.setShowRoot(false)` 可以隐藏 root-node。






## TreeItem

`TreeItem` 实现单个节点的模型，为 TreeView 等控件提供层次结构的值。模型的实现可以使值在需要时加载到内存。

该模型支持注册监听器，当 item 数目、位置或值发生变化，触发监听器。

!!! note
    `TreeItem` 不是 `Node` 类型，不回触发可视化相关事件。这类事件需要通过 `TreeCell` 注册。

**示例：** 在内存中创建 `TreeItem`

```java
 TreeItem<String> root = new TreeItem<String>("Root Node");
 root.setExpanded(true);
 root.getChildren().addAll(
     new TreeItem<String>("Item 1"),
     new TreeItem<String>("Item 2"),
     new TreeItem<String>("Item 3")
 );
 TreeView<String> treeView = new TreeView<String>(root);
```

这种方法适合简单的树结构，或者当数据不多时。在树结构很大时，可以选择**按需创建** `TreeItem`。

**示例：** 文件系统浏览器

```java{.line-numbers}
private TreeView buildFileSystemBrowser() {
      TreeItem<File> root = createNode(new File("/"));
      return new TreeView<File>(root);
}

// 使用 TreeItem 表示文件。This method creates a TreeItem to represent the given File. It does this
// by overriding the TreeItem.getChildren() and TreeItem.isLeaf() methods 
// anonymously, but this could be better abstracted by creating a 
// 'FileTreeItem' subclass of TreeItem. However, this is left as an exercise
// for the reader.
private TreeItem<File> createNode(final File f) {
    return new TreeItem<File>(f) {
        // We cache whether the File is a leaf or not. A File is a leaf if
        // it is not a directory and does not have any files contained within
        // it. We cache this as isLeaf() is called often, and doing the 
        // actual check on File is expensive.
        private boolean isLeaf;

        // We do the children and leaf testing only once, and then set these
        // booleans to false so that we do not check again during this
        // run. A more complete implementation may need to handle more 
        // dynamic file system situations (such as where a folder has files
        // added after the TreeView is shown). Again, this is left as an
        // exercise for the reader.
        private boolean isFirstTimeChildren = true;
        private boolean isFirstTimeLeaf = true;
        
        @Override public ObservableList<TreeItem<File>> getChildren() {
            if (isFirstTimeChildren) {
                isFirstTimeChildren = false;

                // First getChildren() call, so we actually go off and 
                // determine the children of the File contained in this TreeItem.
                super.getChildren().setAll(buildChildren(this));
            }
            return super.getChildren();
        }

        @Override public boolean isLeaf() {
            if (isFirstTimeLeaf) {
                isFirstTimeLeaf = false;
                File f = (File) getValue();
                isLeaf = f.isFile();
            }

            return isLeaf;
        }

        private ObservableList<TreeItem<File>> buildChildren(TreeItem<File> TreeItem) {
            File f = TreeItem.getValue();
            if (f != null && f.isDirectory()) {
                File[] files = f.listFiles();
                if (files != null) {
                    ObservableList<TreeItem<File>> children = FXCollections.observableArrayList();

                    for (File childFile : files) {
                        children.add(createNode(childFile));
                    }

                    return children;
                }
            }

            return FXCollections.emptyObservableList();
        }
    };
}
``````


## 创建 TreeView

通过使用 TreeItem 的相应构造函数或调用 `setGraphic` 可以设置 icon（推荐 16x16）。



## 参考

- https://docs.oracle.com/javafx/2/ui_controls/tree-view.htm
- https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TreeView.html
