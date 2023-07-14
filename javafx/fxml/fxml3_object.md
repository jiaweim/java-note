# 在 FXML 中创建对象

2023-07-12, 17:09
****
## 1. 简介

FXML 用于创建 object graph。不同类创建对象的方式不同，如使用构造函数、valueOf() 方法、factory 方法等。FXML 支持这些创建对象的方式。

## 2. 无参构造函数

在 FXML 中使用无参构造函数创建对象很容易。如果元素名称为类名，同时该类有无参构造函数，FXML 通过该元素名创建对象。例如，VBox 具有无参构造函数，下面创建 VBox 对象：

```xml
<VBox>
    ...
</VBox>
```

## 3. static valueOf() 方法

一些 immutable 类提供 valueOf() 方法创建对象。如果 valueOf() 为 static 方法，则接受单个 String 参数，返回一个对象。

通过该方法，可以使用 `fx:value` attribute 创建对象。假设有一个 `Xxx` 类，包含一个 static `valueOf(String s)` 方法。Java 代码：

```java
Xxx x = Xxx.valueOf("a value");
```

在 FXML 定义:

```xml
<Xxx fx:value="a value"/>
```


例如，创建 Long 和 String 对象：

```xml
<Long fx:value="100"/>
<String fx:value="Hello"/>
```

`String` 还包含一个无参构造函数，创建空字符串。因此，如果需要使用空字符串，依然可以使用无参构造函数：

```xml
<!-- Will create a String object with "" as the content -->
<String/>
```

在 FXML 中使用上述内容，需要导入对应类，如 Long 和 String。

还有一点需要注意，使用 `fx:value` attribute 创建对象的类型不是元素类型而是 valueOf() 返回对象类型。例如：

```java
public static Zzz valueOf(String arg);
```

那么：

```xml
<Yyy fx:value="hello"/>
```

`fx:value` 创建的对象类型为 Zzz，而不是 Yyy。

## 4. Factory 方法

如果一个类包含创建对象的无参静态方法，就可以在 FXML 中用 `fx:factory` attribute 创建对象。例如，使用 `LocalDate` 的 `now()` 工厂方法创建 `LocalDate`：

```xml
<?import java.time.LocalDate?>
<LocalDate fx:factory="now"/>
```

`FXCollections` 类包含许多创建集合的静态方法，在 FXML 中可以用它们创建 JavaFX 集合。例如创建 `ObservableList<String>`，并添加四个值：

```xml
<?import java.lang.String?>
<?import javafx.collections.FXCollections?>

<FXCollections fx:factory="observableArrayList">
    <String fx:value="Apple"/>
    <String fx:value="Banana"/>
    <String fx:value="Grape"/>
    <String fx:value="Orange"/>
</FXCollections>
```

**示例：** 使用 `fx:factory` 创建 `ObservableList`

使用 `fx:factory` attribute 创建 `ObservableList`，并用其初始化 `ComboBox` 的 `items` 属性，随后设置 `ComboBox` 的初始值：

```xml
<?import javafx.scene.layout.VBox?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.control.Button?>
<?import javafx.scene.control.ComboBox?>
<?import java.lang.String?>
<?import javafx.collections.FXCollections?>

<VBox xmlns:fx="http://javafx.com/fxml">
    <Label text="List of Fruits"/>
    <ComboBox>
        <items>
            <FXCollections fx:factory="observableArrayList">
                <String fx:value="Apple"/>
                <String fx:value="Banana"/>
                <String fx:value="Grape"/>
                <String fx:value="Orange"/>
            </FXCollections>
        </items>
        <value>
            <String fx:value="Orange"/>
        </value>
    </ComboBox>
</VBox>
```

下面使用 ControlsFX 的 `TextFields`的 `createClearableTextField` 方法创建一个可清除的 `TextField`:

```xml
<TextFields fx:factory="createClearableTextField"/>
```

## 5. Builder

当使用以上方法都无法创建对象时，FXMLLoader 将查找可以创建该对象的 builder。builder 实现 `javafx.util.Builder` 接口：

```java
public interface Builder<T> {
    public T build();
}
```

FXMLLoader 使用其它方法无法创建类的对象时，会调用 `BuilderFactory` 的 getBuilder() 查找对应类的 Builder 实现。BuilderFactory 接口定义如下：

```java
package javafx.util;

@FunctionalInterface
public interface BuilderFactory {

    public Builder<?> getBuilder(Class<?> type);
}
```

FXMLLoader 使用 `JavaFXBuilderFactory` 作为 BuilderFactory 的默认实现。

FXMLLoader 支持两类 Builder：

- 如果 Builder 实现 Map 接口，则使用 put() 方法将 object properties 传递给 Builder。
- 如果 Builder 没有实现 Map 接口，Builder 必须按照 JavaBean 规则提供 getter 和 setter 方法

### 5.1. Builder 的 JavaBean 实现

以下面的 Item 类为例：

```java
public class Item {

    private Long id;
    private String name;

    public Item(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "id=" + id + ", name=" + name;
    }
}
```

Item 没有无参构造函数，也没有  valueOf() 方法，更没有 factory 方法。所以 FXML 默认无法创建 Item 对象。

Item 有两个属性，id 和 name。

下面的 FXML 文件创建了一个 ArrayList，其中包含 3 个 Item 对象。如果使用 FXMLLoader 加载该文件，由于无法实例化 Item，会报错：

```xml
<!-- items.fxml -->
<?import com.jdojo.fxml.Item?>
<?import java.util.ArrayList?>

<ArrayList>
    <Item name="Kishori" id="100"/>
    <Item name="Ellen" id="200"/>
    <Item name="Kannan" id="300"/>
</ArrayList>
```

所以，决定创建一个 Builder 来构建 Item 对象。ItemBuilder 类如下：

```java
import javafx.util.Builder;

public class ItemBuilder implements Builder<Item> {

    private Long id;
    private String name;
	
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public Item build() {
        return new Item(id, name);
    }
}
```

`ItemBuilder` 声明了 id 和 name 两个实例变量。FXMLLoader 遇到相关 properties 时，会调用对应的 setter 方法。setters 将解析值保存在实例变量中。最后调用 build() 获得 Item 对象。

接下来还要实现与 Item 对应的 BuilderFactory。如下：

```java
import javafx.fxml.JavaFXBuilderFactory;
import javafx.util.Builder;
import javafx.util.BuilderFactory;

public class ItemBuilderFactory implements BuilderFactory {

    private final JavaFXBuilderFactory fxFactory = new JavaFXBuilderFactory();

    @Override
    public Builder<?> getBuilder(Class<?> type) {
        // You supply a Builder only for Item type
        if (type == Item.class) {
            return new ItemBuilder();
        }

        // Let the default Builder do the magic
        return fxFactory.getBuilder(type);
    }
}
```

### 5.2. Builder 的 Map 实现

Builder 通过扩展 AbstractMap 实现 Map 接口：

```java
import javafx.util.Builder;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

public class ItemBuilderMap extends AbstractMap<String, Object> implements Builder<Item> {

    private String name;
    private Long id;

    @Override
    public Object put(String key, Object value) {
        if ("name".equals(key)) {
            this.name = (String) value;
        } else if ("id".equals(key)) {
            this.id = Long.valueOf((String) value);
        } else {
            throw new IllegalArgumentException("Unknown Item property: " + key);
        }

        return null;
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Item build() {
        return new Item(id, name);
    }
}
```

对应 BuilderFactory 实现：

```java
import javafx.fxml.JavaFXBuilderFactory;
import javafx.util.Builder;
import javafx.util.BuilderFactory;

public class ItemBuilderFactoryMap implements BuilderFactory {

    private final JavaFXBuilderFactory fxFactory = new JavaFXBuilderFactory();

    @Override
    public Builder<?> getBuilder(Class<?> type) {
        if (type == Item.class) {
            return new ItemBuilderMap();
        }
        return fxFactory.getBuilder(type);
    }
}
```

### 5.3. Builder 测试

```java
import javafx.fxml.FXMLLoader;
import javafx.util.BuilderFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class BuilderTest {

    public static void main(String[] args) throws IOException {
        // Use the Builder with property getter and setter
        loadItems(new ItemBuilderFactory());

        // Use the Builder with Map
        loadItems(new ItemBuilderFactoryMap());
    }

    public static void loadItems(BuilderFactory builderFactory) throws IOException {
        URL fxmlUrl = BuilderTest.class.getResource("/fxml/items.fxml");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(fxmlUrl);
        loader.setBuilderFactory(builderFactory);
        ArrayList items = loader.<ArrayList>load();
        System.out.println("List:" + items);
    }
}
```

```
List:[id=100, name=Kishori, id=200, name=Ellen, id=300, name=Kannan]
List:[id=100, name=Kishori, id=200, name=Ellen, id=300, name=Kannan]
```

```ad-tip
FXMLLoader.setBuilderFactory() 替换了默认的 BuilderFactory。
```
