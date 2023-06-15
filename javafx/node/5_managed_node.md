# 托管 Node

Node 类有一个 BooleanProperty 类型的 managed 属性。所有 node 默认为托管的（ managed），托管 node 的 layout 由其父容器管理。例如，Parent 在计算自身尺寸时，会考虑其所有子节点的 layoutBounds。Parent node 负责调整 resizable 类型托管 node 的大小，根据 layout 策略确定它们的位置。当托管 node 的 layoutBounds 发生变化，scne graph 的相关部分重新布局。

对非托管 node，由用户单独负责其大小和位置。即父容器不负责非托管 node 的 layout，非托管 node 的 layoutBounds 属性发生更改也不会触发 relayout。