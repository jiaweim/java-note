# 简介
手动编写 layout 十分麻烦，如果对布局细节不感兴趣，可以使用`GroupLayout` 结合使用GUI builder(如WindowBuilder)。如果想手动编写layout，又不想用 `GroupLayout`，则推荐使用 `GridBagLayout`，灵活而强大。

布局管理器的种类

|种类|说明|
|---|---|
|BorderLayout|将容器分为东南西北中5个部分，每个区域可以容纳一个组件|
|FlowLayout|按先后顺序从左到右排列，一行排满再换行。每一行的组件都是居中排列|
|GridLayout|将布局空间划分为若干行和列的网络区域，组件放在小区域内|
|GridBagLayout|通过网格进行划分，一个组件可以占据一个或多个网格|
|CardLayout|将容器中的每个组件当作一个卡片，一次仅有一个卡片可见|
|BoxLayout|在水平或垂直方向安排多个组件|
|SpringLayout|通过定义组件边沿的关系来实现布局|
|GroupLayout|指定在一个窗体上组件彼此的关系，如位置关系或对齐关系|

# AbsoluteLayout
Absolute Layout或者Null Layout，以坐标x,y定制组件的绝对位置。

设置方法
- 选择Absolute Layout，拖放到 `JFrame` 或 `JPanel` 中。
- 界面上会实时显示当前的位置和组件大小。

