# 属性和绑定概述

2023-06-26, 22:03
****
## 1. 什么是属性

Java 类可以包含字段和方法两种类成员。字段表示对象的状态，一般声明为 `private`，然后提供 `public` 的 getter 和 setter 方法：

- 对部分或所有字段提供 public getter 和 setter 的 Java 类称为 Java bean
- getter 和 setter 定义了 bean 的**属性**（property）
- Java bean 可以通过属性定义其状态和行为

Java bean 是可观察的（observable），支持属性变更通知。当 Java bean 的 `public` 属性发生变化，会向注册的监听器发送通知。本质上，Java bean 定义了可重用组件，这些组件甚至可以通过构建工具来创建 Java 应用。

属性包含 read-only, write-only 以及 read-write 类型：read-only 属性只有 getter 方法，write-only 属性只有 setter 方法。

Java IDE 等构建工具通过内省（introspection）获取 bean 的属性列表。

JavaBeans API 在 `java.beans` 包中提供了创建和使用 Java beans 的功能及命名约定。下面是一个具有 `name` 属性的 `Person` bean：

```java
public class Person {
    private String name;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
```

按照**约定**，getter 和 setter 方法名称是在属性名称前添加 *get* 或 *set* 前缀：

- getter 方法没有参数，返回类型与字段相同
- setter 方法的参数与字段类型相同，返回 `void`

以编程的方式操作 `Person` bean 的 `name` 属性：

```java
Person p = new Person();
p.setName("John Jacobs");
String name = p.getName();
```

有些面向对象编程语言，如 C#，提供了第三种类成员，称为属性（property）。`Person` 的 C# 实现：

```csharp
public class Person {
    private string name;

    public string Name {
        get { return name; }
        set { name = value; }
    }
}
```

对应的 `Name` 属性操作：

```csharp
Person p = new Person();
p.Name = "John Jacobs";
string name = p.Name;
```

如果只需要访问和返回字段值，C# 还有一种更紧凑的定义：

```csharp
public class Person {
    public string Name { get; set; }
}
```

```ad-tip
属性（*property*）定义了对象的 `public` 状态，支持读写，是可观察的（observable），支持变更通知。
```

## 2. 什么是数据绑定

**数据绑定**（*data binding*）定义程序中数据元素（通常是变量）之间的关系，保持它们同步。GUI 程序通常使用数据绑定同步数据模型元素和相应的 UI 元素。

假设 x, y, z 是数值变量：

```java
x = y + z;
```

该语句定义了 x, y, z 之间的数据绑定。执行时，x 的值与 y, z 的加和同步。

绑定具有**时效性**，执行该语句之前和之后，x 的值不一定是 y 和 z 的加和。

有时候，希望绑定能持续一段时间。例如：

```java
soldPrice = listPrice - discounts + taxes;
```

此时，希望绑定永远有效，这样当 `listPrice`、`discounts` 或 `taxes` 发生变化时，都能正确计算 `soldPrice`。其中，`listPrice`、`discounts` 和 `taxes` 称为**依赖项**，`soldPrice` 与这些依赖性绑定。

为了使绑定正确工作，必须在依赖项发生变化时通知绑定。当依赖项无效或发生变化，所有 listeners 收到通知。绑定收到通知后，将自己与其依赖项同步。

绑定分为**即时绑定**（*eager binding*）和**延迟绑定**（*lazy binding*）：

- 即时绑定，绑定变量在其依赖项发生变化时立即重新计算
- 延迟绑定，依赖项发生变化时不重新计算，而是在下一次读取时重新计算

延迟绑定比即时绑定性能更好。

绑定还可以分为**单向绑定**和**双向绑定**：

- 单向绑定，依赖项的变化单向传递到绑定变量
- 双向绑定，绑定变量和依赖项保持同步

双向绑定只能在两个变量之间定义，例如，x=y 或 y=x 为双向绑定，使 x 和 y 的值相同。

在 GUI 应用程序中，GUI 控件显示的数据与底层数据模型同步，就可以使用双向绑定实现。

## 3. JavaBeans 绑定

Java 很早就支持 bean 属性绑定。

**示例：** `Employee` 包含两个属性，`name` 和 `salary`

```java
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Employee {

    private String name;
    private double salary;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public Employee() {
        this.name = "John Doe";
        this.salary = 1000.0;
    }

    public Employee(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double newSalary) {
        double oldSalary = this.salary;
        this.salary = newSalary;
        // 通知注册的 listeners salary 发生变化
        pcs.firePropertyChange("salary", oldSalary, newSalary);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public String toString() {
        return "name = " + name + ", salary = " + salary;
    }
}
```

`Employee` 的两个属性都是 read-write 类型。`salary` 是绑定属性，当 `salary` 发生变化，其 setter 方法会发出属性变化通知。对 `salary` 变更感兴趣的对象可以调用 `addPropertyChangeListener()` 和 `removePropertyChangeListener()` 注册或注销 listener。

`PropertyChangeSupport` 类是 JavaBeans 的一部分，用于辅助注册和注销 listener，并发出属性变更通知。对 `salary` 改变感兴趣的类需要向 `Employee` bean 注册 listener，在收到变更通知时采取必要操作。

**示例：** 展示为 `Employee` bean 注册 `salary` 注册更改通知。

```java
import java.beans.PropertyChangeEvent;

public class EmployeeTest {

    public static void main(String[] args) {
        final Employee e1 = new Employee("John Jacobs", 2000.0);

        // 计算 tax
        computeTax(e1.getSalary());
        // 为 e1 添加一个属性更改 listener
        e1.addPropertyChangeListener(EmployeeTest::handlePropertyChange);

        // 修改 salary
        e1.setSalary(3000.00);
        e1.setSalary(3000.00); // No change notification is sent.
        e1.setSalary(6000.00);
    }

    public static void handlePropertyChange(PropertyChangeEvent e) {
        String propertyName = e.getPropertyName();
        if ("salary".equals(propertyName)) {
            System.out.print("Salary has changed. ");
            System.out.print("Old:" + e.getOldValue());
            System.out.println(", New:" + e.getNewValue());
            computeTax((Double) e.getNewValue());
        }
    }

    public static void computeTax(double salary) {
        final double TAX_PERCENT = 20.0;
        double tax = salary * TAX_PERCENT / 100.0;
        System.out.println("Salary:" + salary + ", Tax:" + tax);
    }
}
```

```
Salary:2000.0, Tax:400.0
Salary has changed. Old:2000.0, New:3000.0
Salary:3000.0, Tax:600.0
Salary has changed. Old:3000.0, New:6000.0
Salary:6000.0, Tax:1200.0
```

从输出可以发现，调用了三次 `setSalary()`，但只触发了两次 `salary` 变更通知。这是因为第二次调用 `setSalary()` 使用的 `salary` 值与第一次调用 `setSalary() `的值相同，而 `PropertyChangeSupport` 能够检测到这一点。
