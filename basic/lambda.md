# Lambda 表达式

## 语法

Lambda 表达式有两种形式，下面的一般形式：

- 以圆括号包围的参数列表后面
- 后面是箭头符号 `->`
- 表达式

```java
(param1, param2, ...) -> expression;
(param1, param2, ...) -> { /* code statements */ };
```

与 Java 方法类似，括号内为**形参**：

- 只有一个参数时，括号可选
- 没有参数时，需要空括号

**箭头符号**分隔参数列表和表达式主体。

**表达式主体**只包含一条语句时：

- 大括号可选
- 隐式求值返回给调用者

如果方法需要返回类型，而代码有大括号，则必须用 return 语句返回值。

**实例：** 两个等价的语句，一个使用 return，一个采用隐式 return

```java
// explicit return of result
Function<Double, Double> func = x -> { return x * x; }

// evaluates & implicitly returns result
Function<Double, Double> func = x -> x * x;

double y = func(2.0); // x = 4.0
```

**参数类型** 也是可选的。编译器会根据上下文推断参数类型。上例中，函数接口 `java.util.function. Function<T, U>` 将类型 `T` 转换为类型 `U`。上例中，没有指定参数 `x` 的类型为 `Double`。因为 Java 8+ 编译器能够根据 functional 接口定义推断类型。

可以将 lambda 表达式传递给方法。如 JavaFX Button.setOnAction() 方法。编译器可以通过匹配方法的参数类型和返回值类型来确定 lambda 参数。有时为了可读性，可以显式指定参数类型，此时参数列表必须在括号中：

```java
btn.setOnAction( (ActionEvent event) -> System.out.println("Hello World"));
```

!!! tip
    方法内部的单行语句可以省略分号。

简洁形式：

- 单个参数省略括号
- 单条语句省略大括号
- 单条语句省略分号

```java
btn.setOnAction( event -> System.out.println("Hello World") );
```

下面三条 lambda 语句等价：

```java{.line-numbers}
btn.setOnAction( (ActionEvent event) -> {System.out.println(event); } );
btn.setOnAction( (event) -> System.out.println(event) );
btn.setOnAction( event -> System.out.println(event) );
```

## 方法引用

上面展示了三种 lambda 语句样式。还有第四种，即方法引用。

对只有一个参数或一个返回值的方法，不显式指定参数或返回值也不会出现误解，所以 Java 8 新增了方法引用。

**方法引用** 是一种语法糖，以更简洁的语法调用方法。

**示例：** 使用方法引用的 lambda 表达式

- `System.out` 和 `println` 之间的 `::` 称为 *scope operator*，通过名称引用方法
- `println` 没有括号，当 lambda 表达式只有一个参数，该参数会隐式传递给只接受一个参数的方法 `println`

```java
btn.setOnAction(System.out::println);
```

更多方法引用类型可参考 [Java Tutorials](https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html)。

## 变量捕获


