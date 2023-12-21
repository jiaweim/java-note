# Observable 集合

- [Observable 集合](#observable-集合)
  - [1. 什么是 Observable 集合](#1-什么是-observable-集合)
  - [2. ObservableList](#2-observablelist)
    - [2.1. 创建 ObservableList](#21-创建-observablelist)
    - [2.2. 监听 ObservableList 的 Invalidation](#22-监听-observablelist-的-invalidation)
    - [2.3. 监听 ObservableList 的 Change 事件](#23-监听-observablelist-的-change-事件)
      - [2.3.1. ListChangeListener.Change 类](#231-listchangelistenerchange-类)
      - [2.3.2. Update 事件](#232-update-事件)
      - [2.3.3. 完整示例](#233-完整示例)
    - [2.4 FilteredList](#24-filteredlist)
  - [3. ObservableSet](#3-observableset)
  - [4. ObservableMap](#4-observablemap)

2023-08-15, 09:36
modify: 样式
2023-07-04, 15:22
****
## 1. 什么是 Observable 集合

JavaFX `Observable` 集合对 Java 集合进行扩展，支持三种可观察内容变化的集合：

- `ObservableList`
- `ObservableSet`
- `ObservableMap`

这三个接口分别继承 `java.util` 包中的 `List`, `Set` 和 `Map` 接口，同时继承 `javafx.collections.Observable` 接口。主要类图如下所示：

![](images/2023-07-03-09-02-24.png){width="600px"}

JavaFX `Observable` 集合具有两个额外功能：

- 支持 Invalidation 通知
- 支持 Change 通知

`javafx.collections.FXCollections` 是 JavaFX 集合工具类，包含许多 `static` 方法，用于创建这些集合类。

## 2. ObservableList

`ObservableList` 接口的类图如下：

@import "images/2023-07-03-09-26-53.png" {width="600px" title=""}

添加 `ListChangeListener` 监听 `ObservableList` 的变化，当 `ObservableList` 发生变化，会自动调用 `ListChangeListener` 的 `onChanged()` 方法。

!!! note
    `javafx.collections.transformation` 中包含 `FilteredList` 和 `SortedList` 类：
      - `FilteredList` 也是 `ObservableList`，根据指定 `Predicate` 过滤内容
      - `SortedList` 对内容进行排序

### 2.1. 创建 ObservableList

使用 `FXCollections` 中的工厂方法创建  `ObservableList`：

```java
<E> ObservableList<E> emptyObservableList()
<E> ObservableList<E> observableArrayList()
<E> ObservableList<E> observableArrayList(Collection<? extends E> col)
<E> ObservableList<E> observableArrayList(E... items)
<E> ObservableList<E> observableList(List<E> list)
<E> ObservableList<E> observableArrayList(Callback<E, Observable[]> extractor)
<E> ObservableList<E> observableList(List<E> list, Callback<E, Observable[]> extractor)
```

`emptyObservableList()` 创建一个空的 unmodifiable `ObservableList`。该方法一般用来提供空 list 参数。

**示例：** 创建空的 `String` 类型 `ObservableList`

```java
ObservableList<String> emptyList = FXCollections.emptyObservableList();
```

`observableArrayList()` 创建一个由 `ArrayList` 支持的 `ObservableList`。其它变体指定初始元素。

最后两个方法可以监听元素的更新。

**示例：** 演示创建 `ObservableList`

```java{.line-numbers}
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ObservableListTest {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        // 创建 ObservableList：指定初始值
        ObservableList<String> list = FXCollections.observableArrayList("one", "two");
        System.out.println("After creating list: " + list);

        // 添加元素
        list.addAll("three", "four");
        System.out.println("After adding elements: " + list);

        // 此时有 4 个元素，删除中间两个
        list.remove(1, 3);
        System.out.println("After removing elements: " + list);

        // 只保留 "one"
        list.retainAll("one");
        System.out.println("After retaining \"one\": " + list);

        // 再创建一个 ObservableList
        ObservableList<String> list2 =
                FXCollections.<String>observableArrayList("1", "2", "3");

        // 设置 list 的值为 list2
        list.setAll(list2);
        System.out.println("After setting list2 to list: " + list);

        // 创建另一个 list
        ObservableList<String> list3 =
                FXCollections.<String>observableArrayList("ten", "twenty", "thirty");

        // 将 list2 和 list3 串起来
        ObservableList<String> list4 = FXCollections.concat(list2, list3);
        System.out.println("list2 is " + list2);
        System.out.println("list3 is " + list3);
        System.out.println("After concatenating list2 and list3:" + list4);
    }
}
```

```
After creating list: [one, two]
After adding elements: [one, two, three, four]
After removing elements: [one, four]
After retaining "one": [one]
After setting list2 to list: [1, 2, 3]
list2 is [1, 2, 3]
list3 is [ten, twenty, thirty]
After concatenating list2 and list3:[1, 2, 3, ten, twenty, thirty]
```

### 2.2. 监听 ObservableList 的 Invalidation

`ObservableList` 支持 `InvalidationListener`。

```java{.line-numbers}
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ListInvalidationTest {

    public static void main(String[] args) {
        // 创建 list
        ObservableList<String> list =
                FXCollections.observableArrayList("one", "two");
		
        // 添加 InvalidationListener
        list.addListener(ListInvalidationTest::invalidated);

        System.out.println("Before adding three.");
        list.add("three"); // 触发一次 invalid
        System.out.println("After adding three.");

        System.out.println("Before adding four and five.");
        list.addAll("four", "five"); // 触发一次 invalid
        System.out.println("After adding four and five.");

        System.out.println("Before replacing one with one.");
        list.set(0, "one"); // 触发一次 invalid
        System.out.println("After replacing one with one.");
    }

    public static void invalidated(Observable list) {
        System.out.println("List is invalid.");
    }
}
```

```
Before adding three.
List is invalid.
After adding three.
Before adding four and five.
List is invalid.
After adding four and five.
Before replacing one with one.
List is invalid.
After replacing one with one.
```

!!! tip
    `InvalidationListener` 对所有改变 `ObservableList` 的操作，都触发一次 Invalid 事件。    


### 2.3. 监听 ObservableList 的 Change 事件

列表的元素可以重排、更新、替换、添加和删除，这些都属于 Change 事件。使用 `ObservableList.addListener()` 添加 `ListChangeListener` 来监听这些事件。例如：

```java
ObservableList<String> list = FXCollections.observableArrayList();

list.addListener(new ListChangeListener<String>() {
    @Override
    public void onChanged(ListChangeListener.Change<? extends String> change) {
        System.out.println("List has changed.");
    }
});
```

**示例：** 演示 `ListChangeListener`，添加 listener 后，操作 list 4 次，listener 每次都收到通知。

```java{.line-numbers}
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class SimpleListChangeTest {

    public static void main(String[] args) {
        // 创建 ObservableList
        ObservableList<String> list = FXCollections.observableArrayList();

        // 添加 ListChangeListener
        list.addListener(SimpleListChangeTest::onChanged);

        // 下面 4 次操作，触发 4 次事件
        list.add("one");
        list.add("two");
        FXCollections.sort(list);
        list.clear();
    }
	
    public static void onChanged(ListChangeListener.Change<? extends String> change) {
        System.out.println("List has changed");
    }
}
```

```
List has changed
List has changed
List has changed
List has changed
```

#### 2.3.1. ListChangeListener.Change 类

传递给 `onChanged()` 的 `ListChangeListener.Change` 类包含对 list 所作修改的详细信息。下表是 `ListChangeListener.Change` 类的相关方法：

| 方法                               | 分类                                    |
| ---------------------------------- | --------------------------------------- |
| `ObservableList<E> getList()`      | General                                 |
| `boolean next()`                   | Cursor movement                         |
| `void reset()`                     | Cursor movement                         |
| `boolean wasAdded()`               | 修改：添加元素                          |
| `boolean wasRemoved()`             | 修改：删除元素                          |
| `boolean wasReplaced()`            | 修改：替换元素                          |
| `boolean wasPermutated()`          | 修改：重新排序                          |
| `boolean wasUpdated()`             | 修改：更新                              |
| `int getFrom()`                    | 起始索引（inclusive）                   |
| `int getTo()`                      | 结束索引（exclusive）                   |
| `int getAddedSize()`               | 添加元素的个数                          |
| `List<E> getAddedSubList()`        | 添加元素的 sublist                      |
| `List<E> getRemoved()`             | 包含被删除或替换的元素的 immutable list |
| `int getRemovedSize()`             | 返回元素的个数                          |
| `int getPermutation(int oldIndex)` | 重排后元素新的索引                      |

`getList()` 返回更改前的 list。`ListChangeListener.Change` 可能包含多个部分的更改。例如：

```java
ObservableList<String> list = FXCollections.observableArrayList();

// 假设这里添加 listener

list.addAll("one", "two", "three");
list.removeAll("one", "three");
```

在该代码中，`ListChangeListener` 将收到两次通知：一次因为调用 `addAll()`，一次因为调用 `removeAll()`。调用 `removeAll()` 删除第 1,3 两个元素，因此 `Change` 包含这两次删除的信息：

- 第一次删除 "one"，索引为 0，删除后 list 只包含 2 个元素
- 第二次删除 "three"，索引为 1

`Change` 包含一个 cursor，指向特定修改，便于迭代查看修改项。调用 `onChanged()` 时，cursor 指向第一个更改前面（即 `cursor=-1`），调用 `next()` 移动到下一个修改项。当 cursor 移动到有效修改项，`next()` 返回 `true`，否则返回 `false`。

`reset()` 重置 cursor 为 -1。`next() `一般在 while 循环中使用，例如：

```java
ObservableList<String> list = FXCollections.observableArrayList();
...
// 添加 ListChangeListener
list.addListener(new ListChangeListener<String>() {
    @Override
    public void onChanged(ListChangeListener.Change<? extends String> change) {
        while(change.next()) {
            // 处理当前 change
        }
    }
});
```

**替换操作**可以看作删除元素后，在原位置再插入一个元素，所以如果 `wasReplaced()` 返回 `true`，`wasRemoved()` 和 `wasAdded()` 都返回 `true`。

`wasPermutated()` 表示是否发生重排操作。

```ad-note
add, remove, replace, permutate, update 这五种修改事件，permutate 和 update 具有排他性，余下三种可能同时发生。
```

可以在 `onChanged() `方法中添加如下代码处理所有类型的 change 事件：

```java
public void onChanged(ListChangeListener.Change change) {
    while (change.next()) {
        if (change.wasPermutated()) {
            // Handle permutations
        } else if (change.wasUpdated()) {
            // Handle updates
        } else if (change.wasReplaced()) {
            // Handle replacements
        } else {
            if (change.wasRemoved()) {
                // Handle removals
            }
            else if (change.wasAdded()) {
                // Handle additions
            }
        }
    }
}
```

`getFrom()` 和 `getTo()` 返回受影响的索引范围：

- `wasPermutated()` 为 `true` 时，返回重排元素的范围
- `wasUpdated()` 为 `true` 时，返回更新元素的索引范围
- `wasAdded()` 为 `true` 时，返回添加元素的索引范围
- `wasRemoved()` 为 `true` 且 `wasAdded()` 为 false 时，getFrom() 和 getTo() 的值相同，为删除元素的索引

#### 2.3.2. Update 事件

`FXCollections` 有如下两个创建 `ObservableList` 的工厂方法：

```java
<E> ObservableList<E> observableArrayList(Callback<E, Observable[]> extractor)
<E> ObservableList<E> observableList(List<E> list, Callback<E, Observable[]> extractor)
```

如果要监听 update 事件，需要使用以上两个方法创建 `ObservableList`。`javafx.util` 包中的 `Callback<P,R>` 接口定义如下：

```java
public interface Callback<P,R> {
    R call(P param)
}
```

工厂方法中的参数 `Callback<E, Observable[]> extractor`，参数 `E` 是 list 的元素类型，第二个为 `Observable` 数组。

- 从 `ObservableList` 添加或删除元素，不管是否使用 extractor 都会触发事件。
- 但是，如果 `ObservableList` 中的元素是 `Observable` 属性、或者是对 `Observable` 属性的引用，那么只有在构造 `ObservableList` 时指定 extractor，返回对 `Observable` 属性数组，才能在这些元素发生变化时触发事件。

示例：演示 extractor 功能

```java{.line-numbers}
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;


public class ListExtractorDemo {

    public static void main(String[] args) {
        ObservableList<IntegerProperty> listWithoutExtractor =
                FXCollections.observableArrayList();

        ObservableList<IntegerProperty> listWithExtractor =
                FXCollections.observableArrayList(p -> new Observable[]{p});

        listWithoutExtractor.addListener(createListener("listWithoutExtractor"));
        listWithExtractor.addListener(createListener("listWithExtractor"));

        IntegerProperty p1 = new SimpleIntegerProperty(1);
        IntegerProperty p2 = new SimpleIntegerProperty(2);

        // add 或 remove 元素，两个 list 都触发 change 事件
        listWithoutExtractor.addAll(p1, p2);
        listWithExtractor.addAll(p1, p2);

        // 此时只有配备 extractor 的 list 才能触发 change 事件
        p2.set(3);
    }

    private static ListChangeListener<IntegerProperty> createListener(String listId) {
        return (Change<? extends IntegerProperty> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    System.out.println(listId + " added: " + c.getAddedSubList());
                }
                if (c.wasRemoved()) {
                    System.out.println(listId + " removed: " + c.getRemoved());
                }
                if (c.wasUpdated()) {
                    System.out.println(listId + " updated");
                }
            }
        };
    }
}
```

```
listWithoutExtractor added: [IntegerProperty [value: 1], IntegerProperty [value: 2]]
listWithExtractor added: [IntegerProperty [value: 1], IntegerProperty [value: 2]]
listWithExtractor updated
```

#### 2.3.3. 完整示例

```java{.line-numbers}
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Person implements Comparable<Person> {

    private StringProperty firstName = new SimpleStringProperty();
    private StringProperty lastName = new SimpleStringProperty();

    public Person() {
        this.setFirstName("Unknown");
        this.setLastName("Unknown");
    }

    public Person(String firstName, String lastName) {
        this.setFirstName(firstName);
        this.setLastName(lastName);
    }

    public final String getFirstName() {
        return firstName.get();
    }

    public final void setFirstName(String newFirstName) {
        firstName.set(newFirstName);
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public final String getLastName() {
        return lastName.get();
    }

    public final void setLastName(String newLastName) {
        lastName.set(newLastName);
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    @Override
    public int compareTo(Person p) {
        // Assume that the first and last names are always not null
        int diff = this.getFirstName().compareTo(p.getFirstName());
        if (diff == 0) {
            diff = this.getLastName().compareTo(p.getLastName());
        }

        return diff;
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName();
    }
}
```

```java{.line-numbers}
import javafx.collections.ListChangeListener;
import java.util.List;

public class PersonListChangeListener implements ListChangeListener<Person> {

    @Override
    public void onChanged(ListChangeListener.Change<? extends Person> change) {
        while (change.next()) {
            if (change.wasPermutated()) {
                handlePermutated(change);
            } else if (change.wasUpdated()) {
                handleUpdated(change);
            } else if (change.wasReplaced()) { 
                // replace 同时触发 add 和 remove，所以应先处理该事件
                handleReplaced(change);
            } else {
                if (change.wasRemoved()) {
                    handleRemoved(change);
                } else if (change.wasAdded()) {
                    handleAdded(change);
                }
            }
        }
    }

    public void handlePermutated(ListChangeListener.Change<? extends Person> change) {
        System.out.println("Change Type: Permutated");
        System.out.println("Permutated Range: " + getRangeText(change));
        int start = change.getFrom();
        int end = change.getTo();
        for (int oldIndex = start; oldIndex < end; oldIndex++) {
            int newIndex = change.getPermutation(oldIndex);
            System.out.println("index[" + oldIndex + "] moved to " +
                    "index[" + newIndex + "]");
        }
    }

    public void handleUpdated(ListChangeListener.Change<? extends Person> change) {
        System.out.println("Change Type: Updated");
        System.out.println("Updated Range : " + getRangeText(change));
        System.out.println("Updated elements are: " +
                change.getList().subList(change.getFrom(), change.getTo()));
    }

    public void handleReplaced(ListChangeListener.Change<? extends Person> change) {
        System.out.println("Change Type: Replaced");

        // A "replace" is the same as a “remove” followed with an "add"
        handleRemoved(change);
        handleAdded(change);
    }

    public void handleRemoved(ListChangeListener.Change<? extends Person> change) {
        System.out.println("Change Type: Removed");

        int removedSize = change.getRemovedSize();
        List<? extends Person> subList = change.getRemoved();

        System.out.println("Removed Size: " + removedSize);
        System.out.println("Removed Range: " + getRangeText(change));
        System.out.println("Removed List: " + subList);
    }

    public void handleAdded(ListChangeListener.Change<? extends Person> change) {
        System.out.println("Change Type: Added");

        int addedSize = change.getAddedSize();
        List<? extends Person> subList = change.getAddedSubList();

        System.out.println("Added Size: " + addedSize);
        System.out.println("Added Range: " + getRangeText(change));
        System.out.println("Added List: " + subList);
    }

    public String getRangeText(ListChangeListener.Change<? extends Person> change) {
        return "[" + change.getFrom() + ", " + change.getTo() + "]";
    }
}
```

```java{.line-numbers}
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

public class ListChangeTest {

    public static void main(String[] args) {
        Callback<Person, Observable[]> cb =
                (Person p) -> new Observable[]{
                        p.firstNameProperty(),
                        p.lastNameProperty()
                };

        // 创建包含 extractor 的 list
        ObservableList<Person> list = FXCollections.observableArrayList(cb);

        // 添加自定义的 listener
        list.addListener(new PersonListChangeListener());

        Person p1 = new Person("Li", "Na");
        System.out.println("Before adding " + p1 + ": " + list);
        list.add(p1); // 触发 add 事件
        System.out.println("After adding " + p1 + ": " + list);

        Person p2 = new Person("Vivi", "Gin");
        Person p3 = new Person("Li", "He");
        System.out.println("\nBefore adding " + p2 + " and " + p3 + ": " + list);
        list.addAll(p2, p3); // 再次触发 add 事件
        System.out.println("After adding " + p2 + " and " + p3 + ": " + list);

        System.out.println("\nBefore sorting the list:" + list);
        FXCollections.sort(list); // 排序，触发 permutate 事件
        System.out.println("After sorting the list:" + list);

        System.out.println("\nBefore updating " + p1 + ": " + list);
        p1.setLastName("Smith"); // 设置属性值，设置 extractor 后可以监听该事件
        System.out.println("After updating " + p1 + ": " + list);

        Person p = list.get(0);
        Person p4 = new Person("Simon", "Ng");
        System.out.println("\nBefore replacing " + p +
                " with " + p4 + ": " + list);
        list.set(0, p4); // 触发 replace 事件
        System.out.println("After replacing " + p + " with " + p4 + ": " + list);

        System.out.println("\nBefore setAll(): " + list);
        Person p5 = new Person("Lia", "Li");
        Person p6 = new Person("Liz", "Na");
        Person p7 = new Person("Li", "Ho");
        list.setAll(p5, p6, p7); // 触发 replace 事件
        System.out.println("After setAll(): " + list);

        System.out.println("\nBefore removeAll(): " + list);
        list.removeAll(p5, p7); // Leave p6 in the list
        System.out.println("After removeAll(): " + list);
    }
}
```

```
Before adding Li Na: []
Change Type: Added
Added Size: 1
Added Range: [0, 1]
Added List: [Li Na]
After adding Li Na: [Li Na]

Before adding Vivi Gin and Li He: [Li Na]
Change Type: Added
Added Size: 2
Added Range: [1, 3]
Added List: [Vivi Gin, Li He]
After adding Vivi Gin and Li He: [Li Na, Vivi Gin, Li He]

Before sorting the list:[Li Na, Vivi Gin, Li He]
Change Type: Permutated
Permutated Range: [0, 3]
index[0] moved to index[1]
index[1] moved to index[2]
index[2] moved to index[0]
After sorting the list:[Li He, Li Na, Vivi Gin]

Before updating Li Na: [Li He, Li Na, Vivi Gin]
Change Type: Updated
Updated Range : [1, 2]
Updated elements are: [Li Smith]
After updating Li Smith: [Li He, Li Smith, Vivi Gin]

Before replacing Li He with Simon Ng: [Li He, Li Smith, Vivi Gin]
Change Type: Replaced
Change Type: Removed
Removed Size: 1
Removed Range: [0, 1]
Removed List: [Li He]
Change Type: Added
Added Size: 1
Added Range: [0, 1]
Added List: [Simon Ng]
After replacing Li He with Simon Ng: [Simon Ng, Li Smith, Vivi Gin]

Before setAll(): [Simon Ng, Li Smith, Vivi Gin]
Change Type: Replaced
Change Type: Removed
Removed Size: 3
Removed Range: [0, 3]
Removed List: [Simon Ng, Li Smith, Vivi Gin]
Change Type: Added
Added Size: 3
Added Range: [0, 3]
Added List: [Lia Li, Liz Na, Li Ho]
After setAll(): [Lia Li, Liz Na, Li Ho]

Before removeAll(): [Lia Li, Liz Na, Li Ho]
Change Type: Removed
Removed Size: 1
Removed Range: [0, 0]
Removed List: [Lia Li]
Change Type: Removed
Removed Size: 1
Removed Range: [1, 1]
Removed List: [Li Ho]
After removeAll(): [Liz Na]
```

### 2.4 FilteredList

`FilteredList` 实现了 `ObservableList`，根据 `Predicate` 过滤列表元素。对 ObservableList 的更改都会同步到 `FilteredList`。

https://courses.bekwam.net/public_tutorials/bkcourse_filterlistapp.html

## 3. ObservableSet

除了继承 `Set` 的所有方法，`ObservableSet` 的类图如下所示：

@import "images/2023-07-04-10-45-43.png" {width="600px" title=""}

`ObservableSet` 支持 `InvalidationListener` 和 `SetChangeListener`。

`FXCollections` 提供了三个工厂方法创建  `ObservableSet`：

```java
<E> ObservableSet<E> observableSet(E... elements)
<E> ObservableSet<E> observableSet(Set<E> set)
<E> ObservableSet<E> emptyObservableSet()
```

`ObservableSet` 的使用与 `ObservableList` 差别不大，且更简单。

**示例：** 创建 `ObservableSet`

```java{.line-numbers}
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.HashSet;
import java.util.Set;

public class ObservableSetTest {

    public static void main(String[] args) {
        // 创建 ObservableSet：指定初始元素
        ObservableSet<String> s1 = FXCollections.observableSet("one", "two", "three");
        System.out.println("s1: " + s1);

        // Create a Set, and not an ObservableSet
        Set<String> s2 = new HashSet<String>();
        s2.add("one");
        s2.add("two");
        System.out.println("s2: " + s2);

        // 创建 ObservableSet：用 Set 指定初始元素
        ObservableSet<String> s3 = FXCollections.observableSet(s2);
        s3.add("three");
        System.out.println("s3: " + s3);
    }
}

```

```
s1: [two, three, one]
s2: [one, two]
s3: [one, two, three]
```

**示例：** `ObservableSet` 的 `SetChangeListener` 演示

```java{.line-numbers}
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

import java.util.HashSet;
import java.util.Set;

public class SetChangeTest {

    public static void main(String[] args) {
        // 创建 ObservableSet
        ObservableSet<String> set = FXCollections.observableSet("one", "two");

        // 添加 SetChangeListener
        set.addListener(SetChangeTest::onChanged);

        set.add("three"); // 触发 add

        // 因为集合中已有 one，所以 add 失败，不会触发 add 事件
        set.add("one");

        // 创建 set
        Set<String> s = new HashSet<>();
        s.add("four");
        s.add("five");

        // 添加 s 中的所有元素
        set.addAll(s); // 触发两次 add 事件

        set.remove("one"); // 触发 remove 事件
        set.clear();       // 触发 4 次 remove 事件
    }

    public static void onChanged(SetChangeListener.Change<? extends String> change) {
        if (change.wasAdded()) {
            System.out.print("Added: " + change.getElementAdded());
        } else if (change.wasRemoved()) {
            System.out.print("Removed: " + change.getElementRemoved());
        }

        System.out.println(", Set after the change: " + change.getSet());
    }
}
```

```
Added: three, Set after the change: [two, three, one]
Added: four, Set after the change: [two, three, four, one]
Added: five, Set after the change: [two, three, five, four, one]
Removed: one, Set after the change: [two, three, five, four]
Removed: two, Set after the change: [three, five, four]
Removed: three, Set after the change: [five, four]
Removed: five, Set after the change: [four]
Removed: four, Set after the change: []
```

**示例：** `ObservableSet` 的 `InvalidationListener` 演示

```java{.line-numbers}
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

public class SetInvalidationTest {

    public static void main(String[] args) {
        // 创建 ObservableSet
        ObservableSet<String> set = FXCollections.observableSet("one", "two");

        // 添加 InvalidationListener
        set.addListener(SetInvalidationTest::invalidated);

        System.out.println("Before adding three.");
        set.add("three"); // add 触发失效
        System.out.println("After adding three.");

        System.out.println("\nBefore adding four.");
        set.add("four"); // 再次 add，依然触发失效
        System.out.println("After adding four.");

        System.out.println("\nBefore adding one.");
        set.add("one"); // 已有元素，不触发
        System.out.println("After adding one.");

        System.out.println("\nBefore removing one.");
        set.remove("one"); // remove 触发失效
        System.out.println("After removing one.");

        System.out.println("\nBefore removing 123.");
        set.remove("123"); // 非已有元素，不会触发失效
        System.out.println("After removing 123.");
    }

    public static void invalidated(Observable set) {
        System.out.println("Set is invalid.");
    }
}
```

```
Before adding three.
Set is invalid.
After adding three.

Before adding four.
Set is invalid.
After adding four.

Before adding one.
After adding one.

Before removing one.
Set is invalid.
After removing one.

Before removing 123.
After removing 123.
```

## 4. ObservableMap

除了 `java.util.Map` 接口的相关方法，`ObservableMap` 的类图如下：

![](Pasted%20image%2020230704110944.png)
`FXCollections` 提供了如下创建 `ObservableMap` 的方法：

```java
<K,V> ObservableMap<K, V> observableHashMap()
<K,V> ObservableMap<K, V> observableMap(Map<K, V> map)
<K,V> ObservableMap<K, V> emptyObservableMap()
```

**示例：** 创建 `ObservableMap`

```java{.line-numbers}
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.HashMap;
import java.util.Map;

public class ObservableMapTest {

    public static void main(String[] args) {
        ObservableMap<String, Integer> map1 = FXCollections.observableHashMap();

        map1.put("one", 1);
        map1.put("two", 2);
        System.out.println("Map 1: " + map1);

        Map<String, Integer> backingMap = new HashMap<>();
        backingMap.put("ten", 10);
        backingMap.put("twenty", 20);

        ObservableMap<String, Integer> map2 = FXCollections.observableMap(backingMap);
        System.out.println("Map 2: " + map2);
    }
}
```

```
Map 1: {one=1, two=2}
Map 2: {ten=10, twenty=20}
```

**示例：** `MapChangeListener` 演示

```java{.line-numbers}
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class MapChangeTest {

    public static void main(String[] args) {
        ObservableMap<String, Integer> map = FXCollections.observableHashMap();

        // 添加 MapChangeListener
        map.addListener(MapChangeTest::onChanged);

        System.out.println("Before adding (\"one\", 1)");
        map.put("one", 1); // 触发 add
        System.out.println("After adding (\"one\", 1)");

        System.out.println("\nBefore adding (\"two\", 2)");
        map.put("two", 2); // 触发 add
        System.out.println("After adding (\"two\", 2)");

        System.out.println("\nBefore adding (\"one\", 3)");
        // 会删除 ("one", 1) 并添加 ("one", 3)
        map.put("one", 3); // 触发 remove 和 add
        System.out.println("After adding (\"one\", 3)");

        System.out.println("\nBefore calling clear()");
        map.clear(); // 触发多次 remove
        System.out.println("After calling clear()");
    }

    public static void onChanged(
            MapChangeListener.Change<? extends String, ? extends Integer> change) {
        if (change.wasRemoved()) {
            System.out.println("Removed (" + change.getKey() + ", " +
                    change.getValueRemoved() + ")");
        }

        if (change.wasAdded()) {
            System.out.println("Added (" + change.getKey() + ", " +
                    change.getValueAdded() + ")");
        }
    }
}
```

```
Before adding ("one", 1)
Added (one, 1)
After adding ("one", 1)

Before adding ("two", 2)
Added (two, 2)
After adding ("two", 2)

Before adding ("one", 3)
Removed (one, 1)
Added (one, 3)
After adding ("one", 3)

Before calling clear()
Removed (one, 3)
Removed (two, 2)
After calling clear()
```

**示例：** `InvalidationLisener` 演示

```java{.line-numbers}
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class MapInvalidationTest {

    public static void main(String[] args) {
        ObservableMap<String, Integer> map = FXCollections.observableHashMap();

        //添加 InvalidationListener
        map.addListener(MapInvalidationTest::invalidated);

        System.out.println("Before adding (\"one\", 1)");
        map.put("one", 1); // 触发 invalid
        System.out.println("After adding (\"one\", 1)");

        System.out.println("\nBefore adding (\"two\", 2)");
        map.put("two", 2); // 再次触发 invalid
        System.out.println("After adding (\"two\", 2)");

        System.out.println("\nBefore adding (\"one\", 1)");
        // 添加已有 key,value 不会触发
        map.put("one", 1);
        System.out.println("After adding (\"one\", 1)");

        System.out.println("\nBefore adding (\"one\", 100)");
        // 添加已有 key，不同 value 触发 invalid
        map.put("one", 100);
        System.out.println("After adding (\"one\", 100)");

        System.out.println("\nBefore calling clear()");
        map.clear(); // 有多少元素，触发多少次 invalid
        System.out.println("After calling clear()");
    }

    public static void invalidated(Observable map) {
        System.out.println("Map is invalid.");
    }
}
```

```
Before adding ("one", 1)
Map is invalid.
After adding ("one", 1)

Before adding ("two", 2)
Map is invalid.
After adding ("two", 2)

Before adding ("one", 1)
After adding ("one", 1)

Before adding ("one", 100)
Map is invalid.
After adding ("one", 100)

Before calling clear()
Map is invalid.
Map is invalid.
After calling clear()
```
