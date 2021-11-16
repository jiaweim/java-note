# Look and feel

- [Look and feel](#look-and-feel)
  - [设置 LaF](#设置-laf)
  - [LaF 资源](#laf-资源)

## 设置 LaF

laf 设置应该放在程序的开头。使用 `UIManager.setLookAndFeel()` 设置 laf。

UIManager.getCrossPlatformLookAndFeelClassName(), 返回适用于所有平台的 laf。

UIManager.getSystemLookAndFeelClassName()，返回当前平台的laf。

Swing 的可插入外观的 API 支持 以下几种特性：

- 默认外观，即 Metal 外观，跨平台保持不变；
- 系统外观，基于底层操作系统的外观；
- 自定义外观。

## LaF 资源

- [FlatLaf](https://github.com/JFormDesigner/FlatLaf)

FlatLaf 是一个面向 Java Swing 的开源跨平台LNF。

扁平化设计（没有阴影或渐变），干净，简单，优雅。FlatLaf 带有 Light, Dark, IntelliJ 和 Darcula 主题，在 HiDPI 可缩放显示，支持 Java 8 或更高版本。

其主题颜色和按钮和 IntelliJ IDEA 2019.2+ 的 Darculla 和 IntelliJ 基本一样。

- [WebLaf](https://github.com/mgarin/weblaf)

WebLaf 是一个开源的 Swing lnf 库。

- [Radiance](https://github.com/kirill-grouchnikov/radiance)

需要 Java 9+。

原来比较流行的 substance 现在是 Radiance 的一部分。
