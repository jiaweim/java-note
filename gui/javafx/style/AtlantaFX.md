# AtlantaFX

## 简介

AtlantaFX 是一个现代 JavFX CSS 主题集合，并提供额外控件：

- 受各种 Web 组件框架的启发的扁平化界面
- 首选 CSS，支持现有 JavaFX 控件
- 同时支持浅色和深色主题
- 可定制
- 用模块化 SASS 编写
- 为现代 GUI 开发的额外控件

## 入门

添加依赖项：

```xml
<dependency>
    <groupId>io.github.mkpaz</groupId>
    <artifactId>atlantafx-base</artifactId>
    <version>2.0.0</version>
</dependency>
```

设置主题：

```java
public class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // find more themes in 'atlantafx.base.theme' package
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        // the rest of the code ...
    }
}
```



## 参考

- https://mkpaz.github.io/atlantafx/
- https://github.com/mkpaz/atlantafx