# JavaFX Bean

2023-06-27, 16:46
****
## 1. 简介

主要内容：如何在类中使用 JavaFX 属性。

下面创建一个 `Book` 类，在其中定义三个属性：`ISBN`, `title` 和 `price`。

## 2. read-write 属性

**定义 title 属性**

```java
public class Book {
    private StringProperty title = new SimpleStringProperty(this, "title", "Unknown");
}
```

**定义 getter 方法**

```java
public class Book {
    private StringProperty title = new SimpleStringProperty(this, "title", "Unknown");

    public final StringProperty titleProperty() {
        return title;
    }
}
```

**使用 title 属性**

```java
Book b = new Book();
b.titleProperty().set("Harnessing JavaFX 17.0");
String title = b.titleProperty().get();
```

**定义 getter 和 setter**

根据 JavaFX 设计模式，每个 JavaFX 属性应该提供与 JavaBeans 类似的 getter 和 setter。`title` 属性的 getter 和 setter 定义：

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

这里 `getTitle()` 和 `setTitle()` 方法内部使用 `title` 属性获取和设置 `title` 值。

```ad-tip
属性的 getter 和 setter 方法一般声明为 `final`。附加的 getter 和 setter 命名方法与 JavaBeans 一致，方便一些老的工具识别。
```

## 3. read-only 属性

**定义 ISBN read-only 属性**

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

- 使用 `ReadOnlyStringWrapper` 而非 `SimpleStringProperty`
- 没有 setter 方法，你可以定义一个，但必须为 `private`
- getter 方法与 read-write 属性一样
- `ISBNProperty()` 返回 `ReadOnlyStringProperty` 类型，而不是 `ReadOnlyStringWrapper` 类型。即从 wrapper 获得一个 read-only 版本

对 `Book` 用户，`ISBN` 是 read-only；而在 `Book` 内部可以修改 `ISBN` 值，并且修改结果会自动同步到 read-only 版本。

## 4. 完整示例

`Book` 定义了两个 read-write 属性，一个 read-only 属性。

```java
import javafx.beans.property.*;

public class Book {

    private StringProperty title = new SimpleStringProperty(this, "title", "Unknown");
    private DoubleProperty price = new SimpleDoubleProperty(this, "price", 0.0);
    private ReadOnlyStringWrapper ISBN = new ReadOnlyStringWrapper(this, "ISBN", "Unknown");

    public Book() {}

    public Book(String title, double price, String ISBN) {
        this.title.set(title);
        this.price.set(price);
        this.ISBN.set(ISBN);
    }

    public final String getTitle() {
        return title.get();
    }

    public final void setTitle(String title) {
        this.title.set(title);
    }

    public final StringProperty titleProperty() {
        return title;
    }

    public final double getPrice() {
        return price.get();
    }

    public final void setPrice(double price) {
        this.price.set(price);
    }

    public final DoubleProperty priceProperty() {
        return price;
    }

    public final String getISBN() {
        return ISBN.get();
    }

    public final ReadOnlyStringProperty ISBNProperty() {
        return ISBN.getReadOnlyProperty();
    }
}
```


测试：

- 创建 `Book`
- 打印 `Book` 信息
- 修改 `Book` 属性
- 打印 `Book` 信息

注意 `printDetails()` 方法的 `ReadOnlyProperty` 参数。所有属性类直接或间接地实现了 `ReadOnlyProperty` 接口。

```java
import javafx.beans.property.ReadOnlyProperty;

public class BookPropertyTest {

    public static void main(String[] args) {
        Book book = new Book("Harnessing JavaFX", 9.99, "0123456789");

        System.out.println("After creating the Book object...");

        // Print Property details
        printDetails(book.titleProperty());
        printDetails(book.priceProperty());
        printDetails(book.ISBNProperty());

        // Change the book's properties
        book.setTitle("Harnessing JavaFX 8.0");
        book.setPrice(9.49);

        System.out.println("\nAfter changing the Book properties...");

        // Print Property details
        printDetails(book.titleProperty());
        printDetails(book.priceProperty());
        printDetails(book.ISBNProperty());
    }

    public static void printDetails(ReadOnlyProperty<?> p) {
        String name = p.getName();
        Object value = p.getValue();
        Object bean = p.getBean();
        String beanClassName
                = (bean == null) ? "null" : bean.getClass().getSimpleName();
        String propClassName = p.getClass().getSimpleName();

        System.out.print(propClassName);
        System.out.print("[Name:" + name);
        System.out.print(", Bean Class:" + beanClassName);
        System.out.println(", Value:" + value + "]");
    }
}

```

```
After creating the Book object...
SimpleStringProperty[Name:title, Bean Class:Book, Value:Harnessing JavaFX]
SimpleDoubleProperty[Name:price, Bean Class:Book, Value:9.99]
ReadOnlyPropertyImpl[Name:ISBN, Bean Class:Book, Value:0123456789]

After changing the Book properties...
SimpleStringProperty[Name:title, Bean Class:Book, Value:Harnessing JavaFX 8.0]
SimpleDoubleProperty[Name:price, Bean Class:Book, Value:9.49]
ReadOnlyPropertyImpl[Name:ISBN, Bean Class:Book, Value:0123456789]
```

