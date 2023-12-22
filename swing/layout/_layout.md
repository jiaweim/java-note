# 布局管理器

- [布局管理器](#布局管理器)
  - [简介](#简介)
  - [种类](#种类)

2023-12-22, 00:51
****

## 简介

布局管理器，能够对顶层容器中的组件按照指定规则进行排列。

手动编写 layout 十分麻烦，如果对布局细节不感兴趣，可以使用 `GroupLayout` 结合 GUI builder，如WindowBuilder，NetBeans IDE，Intellij IDEA 等。如果想手动编写 layout，又不想用 `GroupLayout`，则推荐使用 `GridBagLayout`，其灵活性仅次于 `GroupLayout`。

## 种类

Swing 提供的布局管理器种类如下：

|种类|说明|
|---|---|
|`BorderLayout`|将容器分为东南西北中 5 个区域，每个区域可以容纳一个组件|
|`FlowLayout`|按先后顺序从左到右排列，一行排满换行。每一行的组件都是居中排列|
|`GridLayout`|将布局空间划分为若干行和列的网络区域，组件放在网格中|
|`GridBagLayout`|通过网格进行划分，一个组件可以占据一个或多个网格|
|`CardLayout`|将容器中的每个组件当作一个卡片，一次仅有一个卡片可见|
|`BoxLayout`|在水平或垂直方向排序组件|
|`SpringLayout`|通过定义组件边沿的关系来实现布局|
|`GroupLayout`|指定在一个窗体上组件彼此的关系，如位置关系或对齐关系|