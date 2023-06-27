# JavaFX Bean

## 简介

主要内容：如何在类中使用 JavaFX 属性。

下面创建一个 Book 类，在其中定义三个属性：ISBN, title 和 price。

## read-write 属性

- 定义 title 属性

```java
public class Book {
    private StringProperty title = new SimpleStringProperty(this, "title", "Unknown");
}
```

- 为 title 属性定义 getter 方法

```java
public class Book {
    private StringProperty title = new SimpleStringProperty(this, "title", "Unknown");

    public final StringProperty titleProperty() {
        return title;
    }
}
```

- 使用 title 属性

```java
Book b = new Book();
b.titleProperty().set("Harnessing JavaFX 17.0");
String title = b.titleProperty().get();
```

- 定义 getter 和 setter

根据 JavaFX 设计模式，每个 JavaFX 属性应该提供与 JavaBeans 类似的 getter 和 setter。title 属性的 getter 和 setter 定义：

```java
public class Book {
    private StringProperty title = new SimpleStringProperty(this, "title", "Unknown");
    
    public final StringProperty titleProperty() {
        return title;
    }

    public final String getTitle() {
        return title.get();
    }

    public final void setTitle(String title) {
        this.title.set(title);
    }
}
```

这里 getTitle() 和 setTitle() 方法内部使用 title 属性获取和设置 title 值。

```ad-tip
属性的 getter 和 setter 方法一般声明为 final。附加的 getter 和 setter 命名方法与 JavaBeans 一致，方便一些老的工具识别。
```

## read-only 属性

将 Book 的 ISBN 定义为 read-only 属性：

```java
public class Book {
    private ReadOnlyStringWrapper ISBN = new ReadOnlyStringWrapper(this, "ISBN", "Unknown");

    public final String getISBN() {
        return ISBN.get();
    }

    public final ReadOnlyStringProperty ISBNProperty() {
        return ISBN.getReadOnlyProperty();
    }
    // More code goes here...
}
```

要点：

- 使用 `ReadOnlyStringWrapper` 而不是 SimpleStringProperty
- 