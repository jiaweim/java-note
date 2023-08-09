# 对象 IO 流和序列化

## 简介

如果需要存储相同类型的数据，使用定长 record 格式是个不错的选择。然而，在面向对象编程中，创建的对象很少都是同一种了诶行。例如，创建 Employee record 数组，可能包含的对象是其子类。

实现一种数据格式存储这种多态集合是可以的，但是不需要。Java 支持一种通用机制，称为对象序列化（object serialization），可以将任何对象写入输出流，然后再读取。

## 保存和加载可序列化对象

- 使用 `ObjectOutputStream` 保存对象

```java
var out = new ObjectOutputStream(new FileOutputStream("employee.dat"));
```

然后调用 ObjectOutputStream 的 `writeObject` 方法保存对象：

```java
var harry = new Employee("Harry Hacker", 50000, 1989, 10, 1);
var boss = new Manager("Carl Cracker", 80000, 1987, 12, 15);
out.writeObject(harry);
out.writeObject(boss);
```

- 使用 `ObjectInputStream` 读取对象

```java
var in = new ObjectInputStream(new FileInputStream("employee.dat"));
```

然后调用 ObjectInputStream 的 `readObject` 方法读取对象，与保存时的顺序一致：

```java
var e1 = (Employee) in.readObject();
var e2 = (Employee) in.readObject();
```

- 实现 `Serializable` 接口

对任何需要保存到 ObjectOutputStream，然后从 ObjectInputStream 恢复的类，都必须实现 Serializable 接口：

```java
class Employee implements Serializable { . . . }
```

`Serializable` 接口没有声明方法，所以不需要对类做其他修改。

对象流方法总结：

- writeObject/readObject 用于读写对象
- writeInt/readInt 用于读写基本类型 int
- riteDouble/readDouble 用于读写基本类型 double

在幕后，ObjectOutputStream 查看对象的所有字段并保存它们的内容。例如，在写入 `Employee` 对象时，会将其  name, date 和 salary 字段写入输出流。

然而，有一个重要情况需要考虑，当一个对象被多个 对象作为状态的一部分共享时，会发生什么？

为了说明该问题，我们稍微修改一下 `Manager` 类。假设每个经理有一个秘书：

```java
class Manager extends Employee
{
   private Employee secretary;
   . . .
}
```

现在每个 `Manager` 对象都包含一个表示秘书的 `Employee` 对象引用。当然，两个经理可以共用一个秘书，如下所示：

```java
var harry = new Employee("Harry Hacker", . . .);

var carl = new Manager("Carl Cracker", . . .);
carl.setSecretary(harry);

var tony = new Manager("Tony Tester", . . .);
tony.setSecretary(harry);
```

![|500](f0091-01.jpg)
保存这样一个对象网络有一定调整。首先，我们不能保存和恢复 secretary 的内存地址，因为重新加载对象时，它占用的内存地址与最初大概率不同。

因此，每个对象都用序列号（*serial number*）来识别，这也是**对象序列化**名称的来源。其算法为：

1. 每个对象引用都分配一个序列化（如下图所示）
2. 第一个遇到对象引用时，将对象数据保存到输出流
3. 遇到之前保存过对象，就通过序列化引用

![|500](f0092-01.jpg)
在回读对象时，这个过程反过来了：

1. 在 `ObjectInputStream` 中第一次遇到的对象，使用流数据初始化它，并记住其序列号
2. 当遇到序列号引用时，检索序列号对应的对象

```ad-caution
对象流包含序列化对象的类名、超类名和字段名。对于内部类，部分名称是由编译器合成的，不同编译器的命名约定可能不同。如果出现这种情况，反序列化就会失败。因此，序列化内部类时需要谨慎。
```

**示例：** 保存并重新加载由 `Employee` 和 `Manager` 对象组成的网络。

```java
import java.io.*;

class ObjectStreamTest{

    public static void main(String[] args) throws IOException, ClassNotFoundException{
    
        var harry = new Employee("Harry Hacker", 50000, 1989, 10, 1);
        
        var carl = new Manager("Carl Cracker", 80000, 1987, 12, 15);
        carl.setSecretary(harry);
        
        var tony = new Manager("Tony Tester", 40000, 1990, 3, 15);
        tony.setSecretary(harry);
        
        var staff = new Employee[3];
        
        staff[0] = carl;
        staff[1] = harry;
        staff[2] = tony;
        
        // save all employee records to the file employee.ser
        try (var out = new ObjectOutputStream(new FileOutputStream("employee.ser")))
        {
           out.writeObject(staff);
        }
        
        try (var in = new ObjectInputStream(new FileInputStream("employee.ser")))
        {
           // retrieve all records into a new array
        
           var newStaff = (Employee[]) in.readObject();
        
           // raise secretary's salary
           newStaff[1].raiseSalary(10);
        
           // print the newly read employee records
           for (Employee e : newStaff)
              System.out.println(e);
        }
    }
}
```

## 对象序列化文件格式

对象序列化以特定文件格式保存对象数据。

当然，使用 writeObject/readObject 方法不需要知道文件中对象的确切字节序列。但是，研究数据格式对深入了解对象序列化过程非常有帮助。

每个文件以 2 个字节的魔数（magic number）开始：

```
AC ED
```

后面是对象序列化格式的版本号，目前是：

```
00 05
```

> 这里用十六进制数表示字节

再后面是对象序列，按照它们保存的顺序排列。

String 对象保存为：

- 74
- 2 个字节（表示长度）
- 字符（modified UTF-8 格式）

例如，字符串 "Harry" 保存为：

```
74 00 05 Harry
```

保存对象时，必须同时保存该对象的类。类的保存格式：

- `72`    
- 2-byte length of class name
- Class name
- 8-byte fingerprint
- 1-byte flag
- 2-byte count of instance field descriptors
- Instance field descriptors
- `78` (end marker)
- Superclass descriptor (`70` if none)

#TODO 

## 修改默认序列化机制

有些字段不应该被序列化，例如，只对 native 方法有意义的文件句柄和窗口句柄。另外，为了提高效率，对象会存储缓存值，这些内容也没必要序列化。

Java 有一种防止字段被序列化的简单机制，用关键字 `transient` 标记即可。在序列化对象时，会自动跳过 transient 字段。

序列化机制为默认序列化提供了自定义功能。例如，可序列化类可以使用标签定义方法：

```java
@Serial private void readObject(ObjectInputStream in) 
    throws IOException, ClassNotFoundException;
@Serial private void writeObject(ObjectOutputStream out)
    throws IOException;
```

这样就不再自动序列化实例的字段，而是调用这些方法序列化。

N.B. @Serial 注释的方法不属于接口，因此不能使用 `@Override` 注释让编译器检查方法声明。@Serial 注释启用对序列化方法声明的检查。在 Java 17 之前，javac 不支持这种检查。

下面是一个典型示例。java.awt.geom 包中的一些类，比如 Point2D.Double 不可序列化。现在，假设你想序列化一个 `LabeledPoint` 类，该类包含一个 `String` 和一个 Point2D.Double。首先，需要将 Point2D.Double 标记为 `transient` 以避免 NotSerializableException。

```java
public class LabeledPoint implements Serializable
{
   private String label;
   private transient Point2D.Double point;
   . . .
}
```

在 `writeObject` 方法，首先调用 `defaultWriteObject` 方法写入对象描述符和 `String` 字段。这是 `ObjectOutputStream` 类的一个特殊方法


如果 Serializable 对象的父类不能序列化，它必须有一个可访问的无参构造函数。例如：

```java
class Person // Not serializable
class Employee extends Person implements Serializable
```

当 Employee 被反序列化时，从对象输入流读取其字段，而 Person 实例的字段通过 Person 的构造函数设置。


