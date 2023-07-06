# JavaFX Binding

2023-06-28, 09:36
****
## 1. 简介

在 JavaFX 中，binding 是一个求值表达式，由一个或多个 observable 类型的依赖项组成。binding 会观察依赖项的变化，并根据需要重新计算值。

JavaFX 对所有 binding 采用**延迟计算**策略：

- 刚定义的 binding 以及依赖项发生变化的 binding 被标记为失效
- 调用 `get()` 或 `getValue(`) 请求值时，重新计算 binding 值，binding 重新生效

JavaFX 的所有 property 类都支持 binding。

## 2. 创建绑定

**示例：** 两个整数 x 和 y 的加和

```java
x + y
```

表达式 $x+y$ 表示一个 binding，依赖项为 $x$ 和 $y$。将该 binding 命名为 `sum`：

```java
sum = x + y
```

在 JavaFX 中实现上述逻辑。首先创建 `x` 和 `y` 两个 `IntegerProperty` 变量：

```java
IntegerProperty x = new SimpleIntegerProperty(100);
IntegerProperty y = new SimpleIntegerProperty(200);
```

然后，创建 `x` 和 `y` 的 binding `sum`：

```java
NumberBinding sum = x.add(y);
```

binding 的 `isValid()` 方法用于判断 binding 是否有效，有效时返回 `true`，失效时返回 `false`。

`NumberBinding` 的 `intValue()`, `longValue()`, `floatValue()` 和 `doubleValue()` 返回对应类型的 binding 值。

完整示例：

```java
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class BindingTest {

    public static void main(String[] args) {
        IntegerProperty x = new SimpleIntegerProperty(100);
        IntegerProperty y = new SimpleIntegerProperty(200);

        // 创建绑定: sum = x + y
        NumberBinding sum = x.add(y); // 此时 binding 无效

        System.out.println("After creating sum");
        System.out.println("sum.isValid(): " + sum.isValid());

        // 计算值，binding 生效
        int value = sum.intValue();

        System.out.println("\nAfter requesting value");
        System.out.println("sum.isValid(): " + sum.isValid());
        System.out.println("sum = " + value);

        // 修改依赖项 x 的值，binding 失效
        x.set(250);
    
        System.out.println("\nAfter changing x");
        System.out.println("sum.isValid(): " + sum.isValid());

        // 请求 sum 值
        value = sum.intValue();

        System.out.println("\nAfter requesting value");
        System.out.println("sum.isValid(): " + sum.isValid());
        System.out.println("sum = " + value);
    }
}
```

```
After creating sum
sum.isValid(): false

After requesting value
sum.isValid(): true
sum = 300

After changing x
sum.isValid(): false

After requesting value
sum.isValid(): true
sum = 450
```

```ad-summary
两个 `Property` 绑定，生成 `Binding` 对象。
```

## 3. Property 绑定

binding 在内部会给它的所有依赖项添加失效监听器。当它的任何依赖项失效，binding 将自身标记为失效。当然，binding 失效并不意味着它的值发生变化，只表示在下一次请求其值时，需要重新计算 binding 值。

可以将 property 与 binding 绑定。binding 是一个自动与其依赖项同步的表达式，根据该定义，绑定的 property 的值基于 binding 表达式，当 binding 的依赖项发生变化时自动同步 property 值。假设有 `x`, `y`, `z` 三个属性：

```java
IntegerProperty x = new SimpleIntegerProperty(10);
IntegerProperty y = new SimpleIntegerProperty(20);
IntegerProperty z = new SimpleIntegerProperty(60);
```

使用 `Property` 的 `bind()` 方法将属性 `z` 与表达式 $x+y$ 绑定：

```java
z.bind(x.add(y));
```

绑定后，`x` 或 `y` 的值发生变化时，属性 `z` 就会失效。在下次请求 `z` 的值时，会重新计算表达式 `x.add(y)` 以获得 `z` 的值。

使用 `Property` 的 `unbind()` 方法解除绑定：

```java
z.unbind();
```

完整示例：

```java
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class BoundProperty {
    public static void main(String[] args) {
    
        IntegerProperty x = new SimpleIntegerProperty(10);
        IntegerProperty y = new SimpleIntegerProperty(20);
        IntegerProperty z = new SimpleIntegerProperty(60);
        z.bind(x.add(y));
        System.out.println("After binding z: Bound = " + z.isBound() +
                ", z = " + z.get());

        // Change x and y
        x.set(15);
        y.set(19);
        System.out.println("After changing x and y: Bound = " + z.isBound() +
                ", z = " + z.get());
        // Unbind z
        z.unbind();

        // 解绑后，修改 x 和 y 不影响 z
        x.set(100);
        y.set(200);
        System.out.println("After unbinding z: Bound = " + z.isBound() +
                ", z = " + z.get());
    }
}
```

```
After binding z: Bound = true, z = 30
After changing x and y: Bound = true, z = 34
After unbinding z: Bound = false, z = 34
```

```ad-summary
将 `Property` 与 `Property` 表达式生成的 `Binding` 对象绑定。
```

## 4. 单向绑定

绑定具有方向性，即变化传播的方向。JavaFX 支持单向（unidirectional binding）和双向（bidirectional binding）绑定：

- 单向绑定：变化从依赖项向属性传播
- 双向绑定：变化可以从依赖项向属性传播，也可以从属性向依赖项传播

`Property` 的 `bind()` 在 property 和 `ObservableValue` 之间创建单向绑定。`bindBidirectional()` 在 `Property` 和另一个同类型的 `Property` 之间创建双向绑定。

假设 `x`, `y`, `z` 都是 `IntegerProperty`，定义如下 binding：

```java
z = x + y
```

对这类绑定，JavaFX 只能定义单向绑定：

```java
z.bind(x.add(y)
```

单向绑定限制一：`Property` 与 `Binding` 绑定后，不能直接设置 `Property` 值，它的值只能通过绑定自动计算。只有解除绑定，才能直接修改其值。例如：

```java
IntegerProperty x = new SimpleIntegerProperty(10);
IntegerProperty y = new SimpleIntegerProperty(20);
IntegerProperty z = new SimpleIntegerProperty(60);
z.bind(x.add(y));

z.set(7878); // 抛出 RuntimeException
```

先解绑，再直接修改 `z` 的值：

```java
z.unbind(); // Unbind z first
z.set(7878); // OK
```

单向绑定限制二：一个属性一次只能有一个单向绑定。假设 `x`, `y`, `z`, `a`, `b` 都是 `IntegerProperty` 实例：

```java
z = x + y
z = a + b
```

如果 `x`, `y`, `a`, `b` 是 4 个不同的属性，`z` 的两个绑定不能同时发生，否则可能出现冲突。

对已有单向绑定的属性重新绑定，会自动解绑上一个绑定。例如：

```java
IntegerProperty x = new SimpleIntegerProperty(1);
IntegerProperty y = new SimpleIntegerProperty(2);
IntegerProperty a = new SimpleIntegerProperty(3);
IntegerProperty b = new SimpleIntegerProperty(4);
IntegerProperty z = new SimpleIntegerProperty(0);

z.bind(x.add(y));
System.out.println("z = " + z.get());

z.bind(a.add(b)); // 会自动解除上一个绑定
System.out.println("z = " + z.get());
```

```
z = 3
z = 7
```

## 5. 双向绑定

双向绑定只能在相同类型的两个属性之间创建。即只有 $x=y$ 或 $y=x$ 两种形式，且 `x` 和 `y` 类型相同。

一个属性可以有多个双向绑定；双向绑定属性可以独立修改值，修改会传递到所有绑定的属性中。例如：

```java
x = y
x = z
```

`x`, `y`, `z` 的值总是同步。即绑定后，它们的值总是相同。

也可以按如下方式绑定：

```java
x = z
z = y
```

那么，这两种绑定方式是否相同？答案是否定的。设 $x=1$, $y=2$, $z=3$，使用下面的绑定方式：

```java
x = y
x = z
```

第一个绑定 $x=y$，使得 $x$ 等于 $y$ ，所以 $x=y=2$；第二个绑定 $x=z$，使得 $x$ 等于 $z$，即 $x=z=3$。由于 $x$ 已经与 $y$ 双向绑定，所以 $x$ 的新值 3 传递给 $y$，最后 $x=y=z=3$。

**示例：** 双向绑定

```java
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class BidirectionalBinding {

    public static void main(String[] args) {
        IntegerProperty x = new SimpleIntegerProperty(1);
        IntegerProperty y = new SimpleIntegerProperty(2);
        IntegerProperty z = new SimpleIntegerProperty(3);

        System.out.println("Before binding:");
        System.out.println("x=" + x.get() + ", y=" + y.get() + ", z=" + z.get());

        x.bindBidirectional(y); // x=y
        System.out.println("After binding-1:");
        System.out.println("x=" + x.get() + ", y=" + y.get() + ", z=" + z.get());

        x.bindBidirectional(z); // x=z
        System.out.println("After binding-2:");
        System.out.println("x=" + x.get() + ", y=" + y.get() + ", z=" + z.get());

        System.out.println("After changing z:");
        z.set(19);
        System.out.println("x=" + x.get() + ", y=" + y.get() + ", z=" + z.get());

        // 移除绑定
        x.unbindBidirectional(y);
        x.unbindBidirectional(z);
        System.out.println("After unbinding and changing them separately:");
        x.set(100);
        y.set(200);
        z.set(300);
        System.out.println("x=" + x.get() + ", y=" + y.get() + ", z=" + z.get());
    }
}
```

```
Before binding:
x=1, y=2, z=3
After binding-1:
x=2, y=2, z=3
After binding-2:
x=3, y=3, z=3
After changing z:
x=19, y=19, z=19
After unbinding and changing them separately:
x=100, y=200, z=300
```

与单向绑定不同，在创建双向绑定时，不会删除之前的绑定。必须使用 `unbindBidirectional()` 移除绑定：

```java
// Create bidirectional bindings
x.bindBidirectional(y);
x.bindBidirectional(z);

// Remove bidirectional bindings
x.unbindBidirectional(y);
x.unbindBidirectional(z);
```