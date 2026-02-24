# PseudoClass

2023-08-07, 15:36
****
## 1. 简介

PseudoClass 类表示伪状态类。

要在 JavaFX 中引入 pseudo-class，只需要在 pseudo-class 状态发生变化时调用 `Node.pseudoClassStateChanged()` 方法。`pseudoClassStateChanged` 方法一般在 javafx.beans.property 属性类的 `invalidated()` 方法中调用。

如果一个 node 有默认的 pseudo-class 状态，则应该在构造函数中调用 `pseudoClassStateChanged` 来设置初始状态。

## 2. 示例

将 xyzzy 设置为 pseudo-class：

```java
public boolean isMagic() {
    return magic.get();
}

public BooleanProperty magicProperty() {
    return magic;
}

public BooleanProperty magic = new BooleanPropertyBase(false) {

     @Override protected void invalidated() {
        pseudoClassStateChanged(MAGIC_PSEUDO_CLASS. get());
    }

     @Override public Object getBean() {
        return MyControl.this;
    }

     @Override public String getName() {
        return "magic";
    }
}

private static final PseudoClass MAGIC_PSEUDO_CLASS = 
                                    PseudoClass.getPseudoClass("xyzzy");
```

