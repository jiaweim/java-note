# CaretListener

- [CaretListener](#caretlistener)
  - [简介](#简介)
  - [参考](#参考)

***

## 简介

文本组件中移动插入符号（caret，指示插入点的光标）或选择发生变化，发生 caret-event。例如，text-component 中插入或删除文本时可以触发 caret-event。`JTextComponent` 及其子类可以使用 `addCaretListener` 添加 caret-listener。

> **NOTE:** 检测 caret 变化的另一种方法是之间监听 caret 本身。不过 caret 触发的是 change-event。



## 参考

- https://docs.oracle.com/javase/tutorial/uiswing/events/caretlistener.html