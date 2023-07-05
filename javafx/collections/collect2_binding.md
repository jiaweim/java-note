# JavaFX 集合的属性和绑定

2023-07-04, 19:56
****
## 1. 简介

`ObservableList`, `ObservableSet` 和 `ObservableMap` 集合都有对应的 `Property` 类型，支持高级和底层绑定 API。

## 2. ObservableList 属性和绑定

下面是 `ListProperty` 的类图。`ListProperty` 实现了 `ObservableValue` 和 `ObservableList` 接口。

![](Pasted%20image%2020230704154836.png)
使用 `SimpleListProperty` 类的构造函数创建 `ListProperty`：

```java
SimpleListProperty()
SimpleListProperty(ObservableList<E> initialValue)
SimpleListProperty(Object bean, String name)
SimpleListProperty(Object bean, String name, ObservableList<E> initialValue)
```

使用 `ListProperty` 的一个常见错误是没有在构造函数中传入 `ObservableList`。`ListProperty` 内部使用 `ObservableList` 实现相关功能。例如：

```java
ListProperty<String> lp = new SimpleListProperty<String>();  
// 内部没有 ObservableList，抛出异常
lp.add("Hello");
```

```
Exception in thread "main" java.lang.UnsupportedOperationException
	at java.base/java.util.AbstractList.add(AbstractList.java:153)
	at java.base/java.util.AbstractList.add(AbstractList.java:111)
	at javafx.base/javafx.beans.binding.ListExpression.add(ListExpression.java:256)
```

**示例：** 创建和初始化 `ListProperty`

```java
ObservableList<String> list1 = FXCollections.observableArrayList();
ListProperty<String> lp1 = new SimpleListProperty<String>(list1);
lp1.add("Hello");

ListProperty<String> lp2 = new SimpleListProperty<String>();
lp2.set(FXCollections.observableArrayList());
lp2.add("Hello");
```

### 2.1. 监听 ListProperty 事件

`ListProperty` 支持三种类型的 listeners：

- `InvalidationListener`
- `ChangeListener`
- `ListChangeListener`

当 `ListProperty` 中封装的 `ObservableList` 发生变化，或 `ObservableList` 的内容发生变化，会触发这三种 listeners。

`ChangeListener` 说明：

- 当 `ObservableList` 的内容发生变化，`ChangeListener` 的 `changed()` 收到的 old 和 new 是对相同 `ObservableList` 的引用
- 当封装的 `ObservableList` 替换为另一个 `ObservableList`，则 `changed()` 收到的 old 和 new ObservableList 不同

**示例：** 处理 `ListProperty` 的这三类 listeners

```java
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ListPropertyTest {

    public static void main(String[] args) {
        // 创建 ListProperty
        ListProperty<String> lp =
                new SimpleListProperty<>(FXCollections.observableArrayList());

        // 添加三种 listeners
        lp.addListener(ListPropertyTest::invalidated);
        lp.addListener(ListPropertyTest::changed);
        lp.addListener(ListPropertyTest::onChanged);

        System.out.println("Before addAll()");
        lp.addAll("one", "two", "three");
        System.out.println("After addAll()");

        System.out.println("\nBefore set()");

        // 替换封装的 ObservableList
        lp.set(FXCollections.observableArrayList("two", "three"));
        System.out.println("After set()");

        System.out.println("\nBefore remove()");
        lp.remove("two");
        System.out.println("After remove()");
    }

    // An invalidation listener
    public static void invalidated(Observable list) {
        System.out.println("List property is invalid.");
    }

    // A change listener
    public static void changed(
                    ObservableValue<? extends ObservableList<String>> observable,
                    ObservableList<String> oldList,
                    ObservableList<String> newList) {
        System.out.print("List Property has changed.");
        System.out.print(" Old List: " + oldList);
        System.out.println(", New List: " + newList);
    }

    // A list change listener
    public static void onChanged(ListChangeListener.Change<? extends String> change) {
        while (change.next()) {
            String action = change.wasPermutated() ? "Permutated" : change.wasUpdated()
                    ? "Updated" : change.wasRemoved() && change.wasAdded() ? "Replaced"
                    : change.wasRemoved() ? "Removed" : "Added";

            System.out.print("Action taken on the list: " + action);
            System.out.print(". Removed: " + change.getRemoved());
            System.out.println(", Added: " + change.getAddedSubList());
        }
    }
}
```

```
Before addAll()
List property is invalid.
List Property has changed. Old List: [one, two, three], New List: [one, two, three]
Action taken on the list: Added. Removed: [], Added: [one, two, three]
After addAll()

Before set()
List property is invalid.
List Property has changed. Old List: [one, two, three], New List: [two, three]
Action taken on the list: Replaced. Removed: [one, two, three], Added: [two, three]
After set()

Before remove()
List property is invalid.
List Property has changed. Old List: [three], New List: [three]
Action taken on the list: Removed. Removed: [two], Added: []
After remove()
```

```ad-tip
`ListProperty` 采用 `ListChangeListener` 监听内容变化，与 `ObservableList` 完全相同，所以这方面的详细内容可以参考上一节。
```

### 2.2. 绑定 ListProperty 的 size 和 empty 属性

`ListProperty` 继承的 `ListExpression` 包含两个 `public` 属性：

```java
ReadOnlyIntegerProperty sizeProperty()
ReadOnlyBooleanProperty emptyProperty()
```

它们在 GUI 中非常有用。例如，GUI 应用中可能采用 `ListProperty` 存储信息，将这两个信息与 `Label` 的 `text` 属性绑定，这样在 `ListProperty` 变化时，`Label` 通过绑定可以自动更新。

**示例：** size 和 empty 属性的使用

```java
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

public class ListBindingTest {

    public static void main(String[] args) {
        ListProperty<String> lp = new SimpleListProperty<>(FXCollections.observableArrayList());

        //将 ListProperty 的 size 和 empty 属性与 StringProperty 绑定
        //以生成 ListProperty 的描述信息
        StringProperty initStr = new SimpleStringProperty("Size: ");
        StringProperty desc = new SimpleStringProperty();
        desc.bind(initStr.concat(lp.sizeProperty())
                .concat(", Empty: ")
                .concat(lp.emptyProperty())
                .concat(", List: ")
                .concat(lp.asString()));

        System.out.println("Before addAll(): " + desc.get());
        lp.addAll("John", "Jacobs");
        System.out.println("After addAll(): " + desc.get());
    }
}
```

```
Before addAll(): Size: 0, Empty: true, List: []
After addAll(): Size: 2, Empty: false, List: [John, Jacobs]
```

## 3. List 属性和内容绑定

`ListProperty` 的高级绑定 API 在 `ListExpression` 和 `Bindings` 类中。底层 API 通过继承 `ListBinding` 实现。`ListProperty` 支持两类绑定：

- 绑定封装的 `ObservableList` 引用
- 绑定封装的 `ObservableList` 的内容

`bind()` 和 `bindBidirectional()` 创建绑定引用。

**示例：** `ObservableList` 引用绑定

```java
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class BindingListReference {

    public static void main(String[] args) {
        ListProperty<String> lp1 =
                new SimpleListProperty<>(FXCollections.observableArrayList());
        ListProperty<String> lp2 =
                new SimpleListProperty<>(FXCollections.observableArrayList());

        lp1.bind(lp2);

        print("Before addAll():", lp1, lp2);
        lp1.addAll("One", "Two");
        print("After addAll():", lp1, lp2);

        // 修改 ObservableList 引用
        lp2.set(FXCollections.observableArrayList("1", "2"));
        print("After lp2.set():", lp1, lp2);

        // Cannot do the following as lp1 is a bound property
        // lp1.set(FXCollections.observableArrayList("1", "2"));
        // 解绑 lp1
        lp1.unbind();
        print("After unbind():", lp1, lp2);

        // lp1 和 lp2 双向绑定
        lp1.bindBidirectional(lp2);
        print("After bindBidirectional():", lp1, lp2);

        lp1.set(FXCollections.observableArrayList("X", "Y"));
        print("After lp1.set():", lp1, lp2);
    }

    public static void print(String msg, ListProperty<String> lp1, ListProperty<String> lp2) {
        System.out.println(msg);
        System.out.println("lp1: " + lp1.get() + ", lp2: " + lp2.get() +
                ", lp1.get() == lp2.get(): " + (lp1.get() == lp2.get()));
        System.out.println("---------------------------");
    }
}
```

```
Before addAll():
lp1: [], lp2: [], lp1.get() == lp2.get(): true
---------------------------
After addAll():
lp1: [One, Two], lp2: [One, Two], lp1.get() == lp2.get(): true
---------------------------
After lp2.set():
lp1: [1, 2], lp2: [1, 2], lp1.get() == lp2.get(): true
---------------------------
After unbind():
lp1: [1, 2], lp2: [1, 2], lp1.get() == lp2.get(): true
---------------------------
After bindBidirectional():
lp1: [1, 2], lp2: [1, 2], lp1.get() == lp2.get(): true
---------------------------
After lp1.set():
lp1: [X, Y], lp2: [X, Y], lp1.get() == lp2.get(): true
---------------------------
```

`bindContent()` 和 `bindContentBidirectional()` 将 `ListProperty` 中封装的 `ObservableList` 的内容与另一个 `ObservableList` 绑定。`unbindContent()` 和 `unbindContentBidirectional()` 分别用于解绑。`Bindings` 类也有对应的方法。

**示例：** `ListProperty` 内容绑定

```java
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class BindingListContent {

    public static void main(String[] args) {
        ListProperty<String> lp1 =
                new SimpleListProperty<>(FXCollections.observableArrayList());
        ListProperty<String> lp2 =
                new SimpleListProperty<>(FXCollections.observableArrayList());

        // 将 lp1 的内容与 lp2 的内容绑定
        lp1.bindContent(lp2);

        // 此时可以修改 lp1 的内容。但是不推荐，因为会打破内容绑定
        // 使用 lp1 的内容不再与 lp2 的内容同步
        // lp1.addAll("X", "Y");
        
        print("Before lp2.addAll():", lp1, lp2);
        lp2.addAll("1", "2");
        print("After lp2.addAll():", lp1, lp2);

        lp1.unbindContent(lp2);
        print("After lp1.unbindContent(lp2):", lp1, lp2);

        // lp1 和 lp2 的内容双向绑定
        lp1.bindContentBidirectional(lp2);

        print("Before lp1.addAll():", lp1, lp2);
        lp1.addAll("3", "4");
        print("After lp1.addAll():", lp1, lp2);

        print("Before lp2.addAll():", lp1, lp2);
        lp2.addAll("5", "6");
        print("After lp2.addAll():", lp1, lp2);
    }

    public static void print(String msg, ListProperty<String> lp1, ListProperty<String> lp2) {
        System.out.println(msg + " lp1: " + lp1.get() + ", lp2: " + lp2.get());
    }
}

```

```
Before lp2.addAll(): lp1: [], lp2: []
After lp2.addAll(): lp1: [1, 2], lp2: [1, 2]
After lp1.unbindContent(lp2): lp1: [1, 2], lp2: [1, 2]
Before lp1.addAll(): lp1: [1, 2], lp2: [1, 2]
After lp1.addAll(): lp1: [1, 2, 3, 4], lp2: [1, 2, 3, 4]
Before lp2.addAll(): lp1: [1, 2, 3, 4], lp2: [1, 2, 3, 4]
After lp2.addAll(): lp1: [1, 2, 3, 4, 5, 6], lp2: [1, 2, 3, 4, 5, 6]
```

## 4. List 元素绑定

可以绑定 `ListProperty` 封装的 `ObservableList` 中的元素：

```java
ObjectBinding<E> valueAt(int index)
ObjectBinding<E> valueAt(ObservableIntegerValue index)
```

第一个与 list 中指定索引处元素绑定，返回 `ObjectBinding`。第二个参数为 `ObservableIntegerValue`，索引可以变化。

当索引超过 list 范围，返回的 `ObjectBinding` 封装内容为 `null`。

```java
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class BindingToListElements {
    public static void main(String[] args) {
        ListProperty<String> lp =
                new SimpleListProperty<>(FXCollections.observableArrayList());

        // 与 list 的最后一个元素绑定
        ObjectBinding<String> last = lp.valueAt(lp.sizeProperty().subtract(1));
        System.out.println("List:" + lp.get() + ", Last Value: " + last.get());

        lp.add("John");
        System.out.println("List:" + lp.get() + ", Last Value: " + last.get());

        lp.addAll("Donna", "Geshan");
        System.out.println("List:" + lp.get() + ", Last Value: " + last.get());

        lp.remove("Geshan");
        System.out.println("List:" + lp.get() + ", Last Value: " + last.get());

        lp.clear();
        System.out.println("List:" + lp.get() + ", Last Value: " + last.get());
    }
}
```

```
List:[], Last Value: null
List:[John], Last Value: John
List:[John, Donna, Geshan], Last Value: Geshan
List:[John, Donna], Last Value: Donna
List:[], Last Value: null
```

## 5. ObservableSet 属性和绑定

`SetProperty` 封装 `ObservableSet`。`SetProperty` 的使用与 `ListProperty` 类似。要点：

- `SetExpression` 和 `Bindings` 包含 `SetProperty` 的高级绑定 API，继承 `SetBinding` 自定义绑定
- `SetProperty` 公开了 `size` 和 `empty` 属性
- `SetProperty` 支持引用绑定和内容绑定
- `SetProperty` 支持三种通知：Invalidation, Change 和 SetChange
- 与 `ListProperty` 不同的是，`SetProperty` 是无序的，其元素没有索引，因此不支持与特定元素绑定，即没有 `valueAt()` 这类方法

`SetProperty` 构造函数：

```java
SimpleSetProperty()
SimpleSetProperty(ObservableSet<E> initialValue)
SimpleSetProperty(Object bean, String name)
SimpleSetProperty(Object bean, String name, ObservableSet<E> initialValue)
```

**示例：** 创建 `SetProperty` 并添加元素

```java
SetProperty<String> sp = new SimpleSetProperty<String>(FXCollections.observableSet());
sp.add("two");

// 获取封装的 ObservableSet
ObservableSet<String> set = sp.get();
```

**示例：** `SetProperty` 绑定

```java
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

public class SetBindingTest {

    public static void main(String[] args) {
        SetProperty<String> sp1 =
                new SimpleSetProperty<>(FXCollections.observableSet());

        // 绑定 SetProperty 的 size 和 empty 属性
        StringProperty initStr = new SimpleStringProperty("Size: ");
        StringProperty desc = new SimpleStringProperty();
        desc.bind(initStr
                .concat(sp1.sizeProperty())
                .concat(", Empty: ")
                .concat(sp1.emptyProperty())
                .concat(", Set: ")
                .concat(sp1.asString())
        );

        System.out.println("Before sp1.add(): " + desc.get());
        sp1.add("John");
        sp1.add("Jacobs");
        System.out.println("After sp1.add(): " + desc.get());

        SetProperty<String> sp2 =
                new SimpleSetProperty<>(FXCollections.observableSet());

        // sp1 与 sp2 内容绑定
        sp1.bindContent(sp2);
        System.out.println("Called sp1.bindContent(sp2)...");

        // 此时可以修改 sp1，但是打破了内容绑定，使得 sp1 与 sp2 内容不同步
        // 所以不推荐
        // sp1.add("X");

        print("Before sp2.add():", sp1, sp2);
        sp2.add("1");
        print("After sp2.add():", sp1, sp2);

        sp1.unbindContent(sp2);
        print("After sp1.unbindContent(sp2):", sp1, sp2);

        // sp1 与 sp2 内容双向绑定
        sp1.bindContentBidirectional(sp2);

        print("Before sp2.add():", sp1, sp2);
        sp2.add("2");
        print("After sp2.add():", sp1, sp2);
    }

    public static void print(String msg, SetProperty<String> sp1, 
                            SetProperty<String> sp2) {
        System.out.println(msg + " sp1: " + sp1.get() + ", sp2: " + sp2.get());
    }
}

```

```
Before sp1.add(): Size: 0, Empty: true, Set: []
After sp1.add(): Size: 2, Empty: false, Set: [John, Jacobs]
Called sp1.bindContent(sp2)...
Before sp2.add(): sp1: [], sp2: []
After sp2.add(): sp1: [1], sp2: [1]
After sp1.unbindContent(sp2): sp1: [1], sp2: [1]
Before sp2.add(): sp1: [1], sp2: [1]
After sp2.add(): sp1: [1, 2], sp2: [1, 2]
```

## 6. ObservableMap 属性和绑定

`MapProperty` 封装 `ObservableMap`。`MapProperty` 的使用与 `ListProperty` 类似。`MapProperty` 要点：

- `MapExpression` 和 `Bindings` 类包含 `MapProperty` 高级绑定 API，继承 `MapBinding` 类自定义底层绑定
- `MapProperty` 包含 `size` 和 `empty` 属性
- `MapProperty` 包含引用和内容两种绑定
- `MapProperty` 支持三种 notifications：Invalidation, change 和 map change
- `MapProperty` 的 `valueAt()` 方法支持绑定特定 key 的 value

`SimpleMapProperty` 构造函数：

```java
SimpleMapProperty()
SimpleMapProperty(Object bean, String name)
SimpleMapProperty(Object bean, String name, ObservableMap<K,V> initialValue)
SimpleMapProperty(ObservableMap<K,V> initialValue)
```

**示例：** 创建 `MapProperty`，并添加值

```java
MapProperty<String, Double> mp = 
        new SimpleMapProperty<String, Double>(FXCollections.observableHashMap());

// Add two entries to the wrapped ObservableMap
mp.put("Ken", 8190.20);
mp.put("Jim", 8990.90);

// Get the wrapped map from the mp property
ObservableMap<String, Double> map = mp.get();
```

**示例：** `ObservableMap` 属性和绑定

```java
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

public class MapBindingTest {

    public static void main(String[] args) {
        MapProperty<String, Double> mp1 =
                new SimpleMapProperty<>(FXCollections.observableHashMap());
		
        // Create an object binding to bind mp1 to the value of the key "Ken"
        // 创建与 "Ken" 映射值 value 的绑定
        ObjectBinding<Double> kenSalary = mp1.valueAt("Ken");
        System.out.println("Ken Salary: " + kenSalary.get()); // 此时为 null

        // 绑定 size 和 empty 属性
        StringProperty initStr = new SimpleStringProperty("Size: ");
        StringProperty desc = new SimpleStringProperty();
        desc.bind(initStr.concat(mp1.sizeProperty())
                .concat(", Empty: ")
                .concat(mp1.emptyProperty())
                .concat(", Map: ")
                .concat(mp1.asString())
                .concat(", Ken Salary: ")
                .concat(kenSalary));

        System.out.println("Before mp1.put(): " + desc.get());
        mp1.put("Ken", 7890.90);
        mp1.put("Jim", 9800.80);
        mp1.put("Lee", 6000.20);
        System.out.println("After mp1.put(): " + desc.get());

        // 创建一个新的 MapProperty
        MapProperty<String, Double> mp2 =
                new SimpleMapProperty<>(FXCollections.observableHashMap());

        // map1 和 map2 内容绑定
        mp1.bindContent(mp2);
        System.out.println("Called mp1.bindContent(mp2)...");

        /* At this point, you can change the content of mp1. However,
         * that will defeat the purpose of content binding, because the
         * content of mp1 is no longer in sync with the content of mp2.
         * Do not do this:
         * mp1.put("k1", 8989.90);
         */
        System.out.println("Before mp2.put(): " + desc.get());
        mp2.put("Ken", 7500.90);
        mp2.put("Cindy", 7800.20);
        System.out.println("After mp2.put(): " + desc.get());
    }
}
```

```
Ken Salary: null
Before mp1.put(): Size: 0, Empty: true, Map: {}, Ken Salary: null
After mp1.put(): Size: 3, Empty: false, Map: {Ken=7890.9, Lee=6000.2, Jim=9800.8}, Ken Salary: 7890.9
Called mp1.bindContent(mp2)...
Before mp2.put(): Size: 0, Empty: true, Map: {}, Ken Salary: null
After mp2.put(): Size: 2, Empty: false, Map: {Ken=7500.9, Cindy=7800.2}, Ken Salary: 7500.9
```
