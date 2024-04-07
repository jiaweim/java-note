# Listener API

2023-12-27, 23:13⭐
****
## 简介

下表内容：

- 第一列为 listener 接口的名称
- 第二列为 adapter 名称
- 第三列列出 listener 接口包含的方法，以及传递给方法的事件对象类型

对 adapter 的讨论，可以参考[事件处理的基本规则](rules.md)。

listener, adapter 和 event-type 的名称前缀一般相同。

|Listener 接口|Adapter 类|Listener 方法|
|---|---|---|
|ActionListener|none|actionPerformed(ActionEvent)|
|AncestorListener|none|ancestorAdded(AncestorEvent)<br/>ancestorMoved(AncestorEvent)<br/>ancestorRemoved(AncestorEvent)|
|CaretListener|none|caretUpdate(CaretEvent)|
|CellEditorListener|none|editingStopped(ChangeEvent)<br/>editingCanceled(ChangeEvent)|
|ChangeListener|none|stateChanged(ChangeEvent)|
|ComponentListener|ComponentAdapter|componentHidden(ComponentEvent)<br/>componentMoved(ComponentEvent)<br/>componentResized(ComponentEvent)<br/>componentShown(ComponentEvent)|
|ContainerListener|ContainerAdapter|componentAdded(ContainerEvent)<br/>componentRemoved(ContainerEvent)|
|DocumentListener|none|changedUpdate(DocumentEvent)<br/>insertUpdate(DocumentEvent)<br/>removeUpdate(DocumentEvent)|
|ExceptionListener|none|exceptionThrown(Exception)|
|FocusListener|FocusAdapter|focusGained(FocusEvent)<br/>focusLost(FocusEvent)|
|HierarchyBoundsListener|HierarchyBoundsAdapter|ancestorMoved(HierarchyEvent)<br/>ancestorResized(HierarchyEvent)|
|HierarchyListener|none|hierarchyChanged(HierarchyEvent)|
|HyperlinkListener|none|hyperlinkUpdate(HyperlinkEvent)|
|InputMethodListener|none|caretPositionChanged(InputMethodEvent)<br/>inputMethodTextChanged(InputMethodEvent)|
|InternalFrameListener|InternalFrameAdapter|internalFrameActivated(InternalFrameEvent)<br/>internalFrameClosed(InternalFrameEvent)<br/>internalFrameClosing(InternalFrameEvent)<br/>internalFrameDeactivated(InternalFrameEvent)<br/>internalFrameDeiconified(InternalFrameEvent)<br/>internalFrameIconified(InternalFrameEvent)<br/>internalFrameOpened(InternalFrameEvent)|
|ItemListener|none|itemStateChanged(ItemEvent)|
|KeyListener|KeyAdapter|keyPressed(KeyEvent)<br/>keyReleased(KeyEvent)<br/>keyTyped(KeyEvent)|
|ListDataListener|none|contentsChanged(ListDataEvent)<br/>intervalAdded(ListDataEvent)<br/>intervalRemoved(ListDataEvent)|
|ListSelectionListener|none|valueChanged(ListSelectionEvent)|
|MenuDragMouseListener|none|menuDragMouseDragged(MenuDragMouseEvent)<br/>menuDragMouseEntered(MenuDragMouseEvent)<br/>menuDragMouseExited(MenuDragMouseEvent)<br/>menuDragMouseReleased(MenuDragMouseEvent)|
|MenuKeyListener|none|menuKeyPressed(MenuKeyEvent)<br/>menuKeyReleased(MenuKeyEvent)<br/>menuKeyTyped(MenuKeyEvent)|
|MenuListener|none|menuCanceled(MenuEvent)<br/>menuDeselected(MenuEvent)<br/>menuSelected(MenuEvent)|
|MouseInputListener (extends MouseListener and MouseMotionListener|MouseInputAdapter<br/>MouseAdapter|mouseClicked(MouseEvent)<br/>mouseEntered(MouseEvent)<br/>mouseExited(MouseEvent)<br/>mousePressed(MouseEvent)<br/>mouseReleased(MouseEvent)<br/>mouseDragged(MouseEvent)<br/>mouseMoved(MouseEvent)<br/>MouseAdapter(MouseEvent)|
|MouseListener|MouseAdapter, MouseInputAdapter|mouseClicked(MouseEvent)<br/>mouseEntered(MouseEvent)<br/>mouseExited(MouseEvent)<br/>mousePressed(MouseEvent)<br/>mouseReleased(MouseEvent)|
|MouseMotionListener|MouseMotionAdapter, MouseInputAdapter|mouseDragged(MouseEvent)<br/>mouseMoved(MouseEvent)|
|MouseWheelListener|MouseAdapter|mouseWheelMoved(MouseWheelEvent)<br/>MouseAdapter<MouseEvent>|
|PopupMenuListener|none|popupMenuCanceled(PopupMenuEvent)<br/>popupMenuWillBecomeInvisible(PopupMenuEvent)<br/>popupMenuWillBecomeVisible(PopupMenuEvent)|
|PropertyChangeListener|none|propertyChange(PropertyChangeEvent)|
|TableColumnModelListener|none|columnAdded(TableColumnModelEvent)<br/>columnMoved(TableColumnModelEvent)<br/>columnRemoved(TableColumnModelEvent)<br/>columnMarginChanged(ChangeEvent)<br/>columnSelectionChanged(ListSelectionEvent)|
|TableModelListener|none|tableChanged(TableModelEvent)|
|TreeExpansionListener|none|treeCollapsed(TreeExpansionEvent)<br/>treeExpanded(TreeExpansionEvent)|
|TreeModelListener|none|treeNodesChanged(TreeModelEvent)<br/>treeNodesInserted(TreeModelEvent)<br/>treeNodesRemoved(TreeModelEvent)<br/>treeStructureChanged(TreeModelEvent)|
|TreeSelectionListener|none|valueChanged(TreeSelectionEvent)|
|TreeWillExpandListener|none|treeWillCollapse(TreeExpansionEvent)<br/>treeWillExpand(TreeExpansionEvent)|
|UndoableEditListener|none|undoableEditHappened(UndoableEditEvent)|
|VetoableChangeListener|none|vetoableChange(PropertyChangeEvent)|
|WindowFocusListener|WindowAdapter|windowGainedFocus(WindowEvent)<br/>windowLostFocus(WindowEvent)|
|WindowListener|WindowAdapter|windowActivated(WindowEvent)<br/>windowClosed(WindowEvent)<br/>windowClosing(WindowEvent)<br/>windowDeactivated(WindowEvent)<br/>windowDeiconified(WindowEvent)<br/>windowIconified(WindowEvent)<br/>windowOpened(WindowEvent)|
|WindowStateListener|WindowAdapter|windowStateChanged(WindowEvent)|



## 参考

- https://docs.oracle.com/javase/tutorial/uiswing/events/api.html