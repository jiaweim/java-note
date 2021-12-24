# Data Binding

- [Data Binding](#data-binding)
  - [什么是 Property](#什么是-property)
  - [什么是 Binding](#什么是-binding)
  - [JavaFX 对 Properties 的支持](#javafx-对-properties-的支持)

2021-12-12, 20:54
***

## 什么是 Property

Java 类可以包含两种成员：字段和方法。字段表示对象的状态，一般声明为 `private`。public 存取方法，或者是 getters 和 setters，用于修改 private 字段。对全部或部分 private 字段有对应的 public getters 和 setters的类称为 Java bean。

property 即用于描述对象的字段。除了最简单的属性，还有 indexed, bound, constrained properties。indexed 属性为数组对象，通过索引访问。bound 属性在值改变时，会发送通知到所有的监听器。constrained 属性为特殊类型的 bound 属性，监听器可以否定特定的更改。

## 什么是 Binding

binding 有很多种，这儿特指 data binding。在GUI程序中，data binding 常用于数据模型和 UI 组件之间的数据同步。要确保 binding 的正确执行，在依赖关系发生变化时，就需要通知 binding。支持绑定的编程语言提供向依赖项注册 listener 的功能。当依赖项无效或发生改变时，通知所有注册的 listener。

binding 可以是 eager 或 lazy 类型。在 eager binding 中，在依赖项发生变化时，绑定变量立刻重新计算。在 lazy binding 中，在依赖项发生变化时，绑定变量不立刻计算，而是在下次读取绑定变量值时计算。从性能上来说，lazy binding 性能更好。

JavaBeans 支持数据绑定。通过 `PropertyChangeSupport`, `PropertyChangeListener` 实现。

下面通过一个实例说明 JavaBeans 如何实现数据绑定：

```java
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Employee
{
    // name 和 salary 都是 bound 属性
    private String name;
    private double salary;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public Employee()
    {
        this.name = "John Doe";
        this.salary = 1000.0;
    }

    public Employee(String name, double salary)
    {
        this.name = name;
        this.salary = salary;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public double getSalary()
    {
        return salary;
    }

    public void setSalary(double newSalary)
    {
        double oldSalary = this.salary;
        this.salary = newSalary;
        // 通知注册的监听器，值发生了改变
        pcs.firePropertyChange("salary", oldSalary, newSalary);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public String toString()
    {
        return "name = " + name + ", salary = " + salary;
    }
}
```

然后，就可以添加 listener，监听值的变化：

```java
import java.beans.PropertyChangeEvent;

public class EmployeeTest
{
    public static void main(String[] args)
    {
        final Employee e1 = new Employee("John Jacobs", 2000.0);

        // Compute the tax
        computeTax(e1.getSalary());
        // Add a property change listener to e1
        e1.addPropertyChangeListener(EmployeeTest::handlePropertyChange);

        // Change the salary
        e1.setSalary(3000.00);
        e1.setSalary(3000.00); // No change notification is sent.
        e1.setSalary(6000.00);
    }

    public static void handlePropertyChange(PropertyChangeEvent e)
    {
        String propertyName = e.getPropertyName();
        if ("salary".equals(propertyName)) {
            System.out.print("Salary has changed. ");
            System.out.print("Old:" + e.getOldValue());
            System.out.println(", New:" + e.getNewValue());
            computeTax((Double) e.getNewValue());
        }
    }

    public static void computeTax(double salary)
    {
        final double TAX_PERCENT = 20.0;
        double tax = salary * TAX_PERCENT / 100.0;
        System.out.println("Salary:" + salary + ", Tax:" + tax);
    }
}
```

输出如下：

```txt
Salary:2000.0, Tax:400.0
Salary has changed. Old:2000.0, New:3000.0
Salary:3000.0, Tax:600.0
Salary has changed. Old:3000.0, New:6000.0
Salary:6000.0, Tax:1200.0
```

虽然调用了三次 `setSalary`，但是只输出了两次变化，因为 `PropertyChangeSupport` 会检测值是否发生了变化，无法没有变化，不会发出 `PropertyChangeEvent`。

## JavaFX 对 Properties 的支持

所有的 JavaFX properties 都是 obsevable，即可以监听值的变化。

在JavaFX中，属性由特定的类表示，如 `IntegerProperty`, `DoubleProperty`, `StringProperty` 等，这些类都是抽象类，对应的实现有两种，如 `SimpleDoubleProperty` 和 `ReadOnlyDoubleWrapper`，其他类都有对应的实现。

下面是创建初始值为 100的 `IntegerProperty` 的示例：

```java
IntegerProperty counter = new SimpleIntegerProperty(100);
```

`Property` 类提供了两套 getter 和 setter 方法：

- `get()/set()`，属性值，对原始类型，如 `int`，`get` 和 `set` 都为 `int` 类型；
- `getValue()/setValue()`，用于对象类型，如对 `IntegerProperty` 返回 `Integer`。

对引用类型，如 `StringProperty`，上面两套方法都返回对象类型。

在 JavaFX 中使用只读属性比较有意思。`ReadOnlyXXXWrapper` 类包装了两种属性类型：只读和读/写。两个属性的值是同步的，其 `getReadOnlyProperty()` 方法返回只读对象类型 `ReadOnlyXXXProperty`。

例如，下面创建一个只读的 `Integer` 属性。其中 `idWrapper` 支持读/写，而 `id` 属性是只读的。当 `idWrapper` 的值发生变化，`id` 的值随之变化：

```java
ReadOnlyIntegerWrapper idWrapper = new ReadOnlyIntegerWrapper(100);
ReadOnlyIntegerProperty id = idWrapper.getReadOnlyProperty();

System.out.println("idWrapper:" + idWrapper.get());
System.out.println("id:" + id.get());

// Change the value
idWrapper.set(101);
System.out.println("idWrapper:" + idWrapper.get());
System.out.println("id:" + id.get());
```

```txt
idWrapper:100
id:100
idWrapper:101
id:101
```

`ReadOnlyXXXWrapper` 一般作为内部 `private` 字段，`ReadOnlyXXXProperty` 则作为方法返回值提供只读访问。

