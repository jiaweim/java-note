# 泛型

## 简介

泛型类和泛型方法包含参数类型。在泛型出现之前，程序员只能用 Object 来编写可以处理多种类型的代码，这既麻烦又不安全。泛型让程序员能够详细描述变量和方法的类型。

泛型在 Java 5 引入，为了向后兼容，Java 泛型也有诸多限制。

泛型让编写的代码可以被许多不同类型的对象重用。例如，ArrayList 就是一个泛型类，可以存储各种类型的对象。

### 类型参数优点

在 Java 5 之前，泛型编程通过继承实现，`ArrayList` 维护一个 `Object` 数组：

```java
public class ArrayList // before generic classes
{
   private Object[] elementData;
   . . .
   public Object get(int i) { . . . }
   public void add(Object o) { . . . }
}
```

这个方法有两个问题。

1. 在查询时，需要转换类型

```java
ArrayList files = new ArrayList(); . . .
String filename = (String)files.get(0);
```

2. 没有类型检查，可以添加任何类型值

```java
files.add(new File(". . ."));
```

该调用可以便于并运行，但是将查询结果转换为 String 会报错。

泛型通过类型参数解决该问题。新版的 ArrayList 有一个类型参数，表示元素类型：

```java
var files = new ArrayList<String>();
```

这样代码可读性更好，马上可以看出该 ArrayList 包含 String 对象。

如果声明变量时指定类型而不是用 var，则可以忽略构造函数中的 type 参数（diamond）：

```java
ArrayList<String> files = new ArrayList<>();
```

从变量类型可以推断处类型。

Java 9 扩展了 diamond 语法，例如，可以在匿名子类中使用 diamond 语法：

```java
ArrayList<String> passwords = new ArrayList<>() // diamond OK in Java 9
   {
      public String get(int n) { return super.get(n).replaceAll(".", "*"); }
   };
```

编译器会利用类型信息，调用 get() 时不再需要转换类型：

```java
String filename = files.get(0);
```

编译器还会检查添加元素的类型是否正确，例如：

```java
files.add(new File(". . .")); // can only add String objects to an ArrayList<String>
```

无法编译，编译器在运行时会抛出错误。

总而言之，类型参数时程序更易读，更安全。

## 定义一个简单的泛型类

泛型类包含一个或多个类型变量。以 Pair 类为例，下面时一个泛型 Pair 类：

```java
public class Pair<T>
{
   private T first;
   private T second;  
   
   public Pair() { first = null; second = null; }
   public Pair(T first, T second) { this.first = first; this.second = second; }  
   
   public T getFirst() { return first; }
   public T getSecond() { return second; }  
   
   public void setFirst(T newValue) { first = newValue; }
   public void setSecond(T newValue) { second = newValue; }
}
```

Pair 类包含一个类型变量 `T`，在类名后 `<>` 中。泛型类可以有多个类型变量。例如，可以将 Pair 的第一个和第二个字段定义为不同类型：

```java
public class Pair<T, U> { . . . }
```

**类型变量**在类定义中，用于指定方法返回类型、字段和局部变量类型。例如：

```java
private T first; // uses the type variable
```

```ad-note
类型变量一般用大写，且简短。Java 库使用类型变量 E 表示集合元素类型，K 和 V 表示 key 和 value 类型；T, U 和 S 表示任何类型。
```

将类型变量替换为具体类型实例化泛型类，例如：

```java
Pair<String>
```

泛型类可以看作普通类的工厂。

## 泛型方法

使用类型参数定义方法：

```java
class ArrayAlg
{
   public static <T> T getMiddle(T... a)
   {
      return a[a.length / 2];
   }
}
```

该方法在普通类中定义，而不是泛型类。不过，从 `<>` 和类型变量可以看出这是一个泛型方法。

**类型变量** 在修饰符 `public static` 后，返回类型之前定义。

在普通类和泛型类中都可以定义泛型方法。

在调用泛型方法前，可以将实际类型用 `<>` 括起来放在方法名前：

```java
String middle = ArrayAlg.<String>getMiddle("John", "Q.", "Public");
```

在大多情况下，可以省略方法调用中的 类型参数。编译器有足够的信息来推断你想要的类型。它通过参数类型与泛型类型 `T...` 的匹配可以推导出 `T` 肯定是 String。因此，上面的语句可以简写为：

```java
String middle = ArrayAlg.getMiddle("John", "Q.", "Public");
```

大多时候泛型的类型推断都很丝滑。但是编译器偶尔也会出错。例如：

```java
double middle = ArrayAlg.getMiddle(3.14, 1729, 0);
```

编译器会报错，不同版本的编译器错误消息可能不同。简而言之，编译器自动将参数封装为 1 个 `Double` 和 2 个 `Integer` 对象。然后尝试寻找 `Double` 和 `Integer` 的共同超类型。编译器会找到两个超类型：`Number` 和 `Comparable`。补救方法是将所有参数都写成 `double` 值。

## 类型变量的边界

有时需要对泛型类或泛型方法的类型变量进行限制。例如，计算数组的最小值：

```java
class ArrayAlg
{
   public static <T> T min(T[] a) // almost correct
   {
      if (a == null || a.length == 0) return null;
      T smallest = a[0];
      for (int i = 1; i < a.length; i++)
         if (smallest.compareTo(a[i]) > 0) smallest = a[i];
      return smallest;
   }
}
```

但是有个问题，`min()` 方法中变量 smallest 的类型是 T，这意味着它可以是任意类型。那么我们如何确定 T 所属的类有 `compareTo` 方法？解决方案是将 T 限制为实现 `Comparable` 接口的类。即为类型变量 T 指定一个边界：

```java
public static <T extends Comparable> T min(T[] a) . . .
```

实际上，`Comparable` 接口本身也是泛型类。此时，忽略该问题和编译器生成的警告。

现在，就只能使用实现 `Comparable` 接口的类的数组调用泛型方法 `min`，如 `String`, `LocalDate` 等。用 `Rectangle` 数组调用 `min` 会导致编译错误，因为 `Rectangle` 没有实现 `Comparable` 接口。

你可能好奇，这里为什么用 extends 而不是 `implements`，毕竟 `Comparable` 是接口：

```java
<T extends BoundingType>
```

该语句表示 `T` 是边界类型的子类型，`T` 和边界类型都可以是类或接口。所以采用 extends 更合理。

类型变量或通配符 `*` 可以有多个边界。例如：

```java
T extends Comparable & Serializable
```

边界类型由 `&` 分隔，因为逗号已经用来分隔类型变量。

和 Java 继承一样，可以有任意多个接口超类型，但最多只能有一个类边界，且类边界必须放在第一个位置。

**示例：** 定义泛型方法 `minmax`，计算 `Pair` 的最小值和最大值，`返回 Pair<T>`

```java
import java.time.*;
public class PairTest2
{
   public static void main(String[] args)
   {
      LocalDate[] birthdays =
         {
            LocalDate.of(1906, 12, 9), // G. Hopper
            LocalDate.of(1815, 12, 10), // A. Lovelace
            LocalDate.of(1903, 12, 3), // J. von Neumann
            LocalDate.of(1910, 6, 22), // K. Zuse
         };
      Pair<LocalDate> mm = ArrayAlg.minmax(birthdays);
      System.out.println("min = " + mm.getFirst());
      System.out.println("max = " + mm.getSecond());
   }
}

class ArrayAlg
{
   /**
      Gets the minimum and maximum of an array of objects of type T.
      @param a an array of objects of type T
      @return a pair with the min and max values, or null if a is null or empty
   */
   public static <T extends Comparable> Pair<T> minmax(T[] a)
   {
      if (a == null || a.length == 0) return null;
      T min = a[0];
      T max = a[0];
      for (int i = 1; i < a.length; i++)
      {
         if (min.compareTo(a[i]) > 0) min = a[i];
         if (max.compareTo(a[i]) < 0) max = a[i];
      }
      return new Pair<>(min, max);
   }
}
```

## 泛型代码和虚拟机

虚拟机没有泛型对象，所有对象都属于普通类。编译器在编译时会移除类型参数。

### 类型擦除

在定义泛型类型时，虚拟机会自动提供对应的 raw 类型。raw 类型名称相对泛型去掉了类型参数，即类型变量被擦除，替换为 `Object` 或边界类型。

例如，`Pair<T>` 的 raw 类型为：

```java
public class Pair{

    private Object first;
    private Object second;
    
    public Pair(Object first, Object second){
        this.first = first;
        this.second = second;
    }
    
    public Object getFirst() { return first; }
    public Object getSecond() { return second; }
    public void setFirst(Object newValue) { first = newValue; }
    public void setSecond(Object newValue) { second = newValue; }
}
```

因为 `T` 类型变量没有边界，所以直接替换为 `Object`。得到一个普通类，和添加泛型之前的 Java 类一样。

你在程序中可能使用不同类型的 Pair，如 `Pair<String>` 和 `Pair<LocalDate>`，擦除类型后，他们都变成 raw `Pair` 类型。

如果为类型变量指定了边界类型，例如：

```java
public class Interval<T extends Comparable & Serializable> implements Serializable
{
   private T lower;
   private T upper;
   . . .
   public Interval(T first, T second)
   {
      if (first.compareTo(second) <= 0) { lower = first; upper = second; }
      else { lower = second; upper = first; }
   }
}
```

编译器将类型变量替换为边界类型，所以 Interval 的 raw 类型为：

```java
public class Interval implements Serializable
{
   private Comparable lower;
   private Comparable upper;
   . . .
   public Interval(Comparable first, Comparable second) { . . . }
}
```

### 泛型表达式类型转换

程序中对泛型方法的调用，返回类型被擦除后，编译器会插入强制转换。例如：

```java
Pair<Employee> buddies = . . .;
Employee buddy = buddies.getFirst()
```

擦除类型后，`getFirst` 返回 `Object`，编译器自动将其强制转换为 Employee。所以，编译器将该方法调用转换为两条虚拟机指令：

- 调用 raw 方法 Pair.getFirst
- 将返回的 `Object` 转换为 Employee 类型

在访问泛型字段时也存在强制类型转换。假设 Pair 的 `first` and `second` 字段是 public。则下面的表达式也存在强制转换：

```java
Employee buddy = buddies.first;
```

### 泛型方法类型转换

泛型方法也有类型擦除。例如：

```java
public static <T extends Comparable> T min(T[] a)
```

类型擦除后变为：

```java
public static Comparable min(Comparable[] a)
```

此时，类型参数 T 被擦除，只保留边界类型 Comparable。

泛型方法类型擦除会带来一些复杂性。例如：

```java
class DateInterval extends Pair<LocalDate>
{
   public void setSecond(LocalDate second)
   {
      if (second.compareTo(getFirst()) >= 0)
      super.setSecond(second);
   }
   . . .
}
```

DateInterval 包含一对 `LocalDate` 对象，重写 setSecond() 方法确保第二个值不会大于第一个值。擦除类型后为：

```java
class DateInterval extends Pair // after erasure
{
   public void setSecond(LocalDate second) { . . . }
   . . .
}
```

有趣的是，还有另一个继承自 Pair 的 setSecond() 方法：

```java
public void setSecond(Object second)
```

其参数类型为 Object，而不是 LocalDate。

考虑下面问题：

```java
DateInterval interval = new DateInterval(. . .);
Pair<LocalDate> pair = interval; // OK--assignment to superclass
pair.setSecond(aDate);
```

我们希望对 `setSecond` 的调用是多态的，会根据需要调用合适的方法。上面 `pair` 指向 `DateInterval` 对象，pair.setSecond(aDate) 应该等价于 DateInterval.setSecond。问题是类型擦除干扰了多态性。为了解决该问题，编译器在 `DateInterval` 类中生成了一个桥接方法：

```java
public void setSecond(Object second) { setSecond((LocalDate) second); }
```

要了解为什么这么做，可以考虑语句的执行过程：

```java
pair.setSecond(aDate)
```

变量 `pair` 声明类型为 `Pair<LocalDate>`，该类型只有一个方法 `setSecond`，即 `setSecond(Object)`。虚拟机在 pair 引用的对象上调用该方法。而 `pair` 引用对象的类型为 `DateInterval`，所以调用 `DateInterval.setSecond(Object)` 方法。该方法是由编译器生成的桥接方法，其内部调用我们想要的 `DateInterval.setSecond(LocalDate)`。

桥接方法可以变得更奇怪。假设 `DateInterval` 也重写了 `getSecond` 方法：

```java
class DateInterval extends Pair<LocalDate>
{
   public LocalDate getSecond() { return (LocalDate) super.getSecond(); }
   . . .
}
```

`DateInterval` 中会有两个 `getSecond` 方法：

```java
LocalDate getSecond() // defined in DateInterval
Object getSecond() // overrides the method defined in Pair to call the first method
```

我们不能在同一个类中定义形参相同的两个方法（这里两个方法都没有形参）。但是，虚拟机同时使用参数类型和返回值类型指定方法，因此，虚拟机可以生成只有返回类型不同的两个方法的字节码。

总之，对 Java 泛型：

- 在虚拟机中没有泛型，只有普通类和方法
- 所有类型参数被替换为边界类型
- 编译器生成桥接方法以保留泛型的多态性
- 编译器在需要时会插入类型转换，以保证类型安全

### 调用遗留代码

在设计 Java 泛型时，一个主要目标时允许泛型和遗留代码之间的互操作性。下面以 Swing 的 JSlider 类为例，其 ticks 可以用包含 text 和 image 的 label 进行自定义。设置 label 的方法：

```java
void setLabelTable(Dictionary table)
```

`Dictionary` 将 Integer 映射为 Label。在 Java 5 之前，该类被实现为 Object 的 map。Java 5 将 Dictionary 变为泛型类，但 JSlider 没有更新，这里没有类型参数的 Dictionary 为 raw 类型。所以就有兼容性问题。

在填充 Dictionary 时，可以用泛型：

```java
Dictionary<Integer, Component> labelTable = new Hashtable<>();
labelTable.put(0, new JLabel(new ImageIcon("nine.gif")));
labelTable.put(20, new JLabel(new ImageIcon("ten.gif")));
. . .
```

将 `Dictionary<Integer, Component>`  传递给 setLabelTable，编译器会发出警告：

```java
slider.setLabelTable(labelTable); // warning
```

编译器无法确定 setLabelTable 会对 Dictionary 指定什么操作。setLabelTable 可能将所有 key 替换为 String，从而破坏了 key 为 Integer 类型的要求，后续操作可能导致错误的强制转换。

现在考虑相反情况，即从遗留类 JSlider 中获得 raw 类型对象。可以将其赋值给泛型变量，也会出现警告：

```java
Dictionary<Integer, Component> labelTable = slider.getLabelTable(); // warning
```

这是可以的，只要确保 labelTable 的 key 为 `Integer` 类型，value 为 `Component` 类型。但是在自定义 JSlider 中可能出现其它类型的 Dictionary。不过，情况不会比泛型出现之前更差，最多就是程序抛出异常。

确定这些警告没问题后，可以使用注释使这些警告消失。例如:

**注释局部变量**

```java
@SuppressWarnings("unchecked")
Dictionary<Integer, Components> labelTable = slider.getLabelTable(); // no warning
```

**注释整个方法**

```java
@SuppressWarnings("unchecked")
public void configureSlider() { . . . }
```

该注释会关闭方法中所有代码的检查。
## 泛型限制

下面介绍 Java 泛型的一些限制，这些限制大多是由类型擦除导致。

### 类型参数不能用基本类型

基本类型不能用作类型参数。因此不能用 `Pair<double>`，只能用 `Pair<Double>`。

这由类型擦除导致，擦除类型后，`Pair` 字段类型为 `Object`，`Object` 不能保存 `double` 值。

### 运行时类型查询只对原始类型有效

虚拟机中对象总是为特定的非泛型类型。因此，所有类型查询都只能用于原始类型。例如，不支持对泛型检查：

```java
if (a instanceof Pair<String>) // ERROR
```

只能检查 `a` 是否为 `Pair` 类型。下面也不行：

```java
if (a instanceof Pair<T>) // ERROR
```

所以强制转换会发出警告：

```java
Pair<String> p = (Pair<String>) a; // warning--can only test that a is a Pair
```

为了提醒这种风险，查询泛型类型（instanceof）编译器会报错，强制转换编译器会发出警告。

同理，`getClass` 总是返回 raw 类型。例如：

```java
Pair<String> stringPair = . . .;
Pair<Employee> employeePair = . . .;
if (stringPair.getClass() == employeePair.getClass()) // they are equal
```

上面的比较结果为 true，因为两个 getClass() 都返回 Pair.class。

### 不能创建参数化类型数组

不能实例化参数化类型数组，例如：

```java
var table = new Pair<String>[10]; // ERROR
```

这是为什么呢？类型擦除后，table 类型为 `Pair[]`，可以转换为 `Object[]`：

```java
Object[] objarray = table;
```

数组会记住其元素类型。当存入元素的类型不对，会抛出 `ArrayStoreException`：

```java
objarray[0] = "Hello"; // ERROR--component type is Pair
```

但是类型擦除使这种机制对泛型无效。例如：

```java
objarray[0] = new Pair<Employee>();
```

赋值为 Pair 类型，类型擦除后没问题，所以能通过数组存储检查，但仍然会导致类型错误。为了避免该问题，Java 干脆不允许使用参数类型的数组。

N.B. 不允许创建这类数组，但可以声明变量类型为 `Pair<String>[]`，但是后面不能用 `new Pair<String>[10]` 进行初始化。

```ad-tip
如果需要参数化集合类型，可以使用 ArrayList，使用 `ArrayList<Pair<String>>` 是安全且有效的。
```

### varargs 警告

将泛型类型传递给[参数量可变的方法](./inheritance.md#参数量可变的方法)。例如：

```java
public static <T> void addAll(Collection<T> coll, T... ts){
    for (T t : ts) coll.add(t);
}
```

形参 `ts` 实际上是一个数组。考虑下面问题：

```java
Collection<Pair<String>> table = . . .;
Pair<String> pair1 = . . .;
Pair<String> pair2 = . . .;
addAll(table, pair1, pair2);
```

为了调用该方法，JVM 必须使用 varargs 参数创建一个 `Pair<String>` 数组，然而不允许参数化类型数组。不过该规则已经放款了，只是得到警告，不会报错。

有两种方法可以抑制警告。可以给调用 addAll 的方法添加 @SuppressWarnings("unchecked") 注释。也可以直接给 addAll 方法加上 @SafeVarargs 注释：

```java
@SafeVarargs
public static <T> void addAll(Collection<T> coll, T... ts)
```

然后就能用泛型类型调用该方法，也不会发出警告。对任何只读取泛型参数数组的方法，都可以安全地使用该注释。

@SafeVarargs 注释只能用于 static, final 或 private (Java 9) 构造函数和方法。子类覆盖方法使注释无效，即该注释无法继承效果。

使用 @SafeVarargs 还可以打破无法创建泛型数组的限制，例如：

```java
@SafeVarargs static <E> E[] array(E... array) { return array; }
```

然后可以调用：

```java
Pair<String>[] table = array(pair1, pair2);
```

这看起来很方便，但有一个隐患。例如：

```java
Object[] objarray = table;
objarray[0] = new Pair<Employee>();
```

不会抛出 `ArrayStoreException`，因为编译器只检查擦除类型，但使用 `table[0]` 时会出错。
