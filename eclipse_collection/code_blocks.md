# 代码块（Code Blocks）

## 简介

在 EC 中，代码块（code block）指单抽象方法对象，出作为参数传入迭代方法。它是一种抽象，代表迭代过程中针对每个元素执行的运算逻辑。代码块有助于将执行内容与方式解耦。

EC 中的代码块大致等同于 Lambda 表达式。在 Java 8 之前的版本中，与 Lambda 最接近的实现形式为匿名内部类。

下面两个示例实现 `Predicate`：一个使用 Java 8+ lambda 表达式，另一个使用匿名内部类。

示例 1：使用 Lambda 执行 select 筛选操作

```java
MutableList<Person> texans =
  this.people.select( each -> each.getAddress().getState().equals("TX"));
Verify.assertSize(1, texans);
```

示例 2：使用匿名内部类执行 select 筛选操作

```java
MutableList<Person> texans = this.people.select(
  new Predicate<Person>()
  {
    public boolean accept(Person each)
    {
      return each.getAddress().getState().equals("TX");
    }
  });
Verify.assertSize(1, texans);
```

两段示例实现的逻辑一致：若 `people` 列表中任意元素的 `state` 属性值等于 `"TX"`，`select` 方法就会把该元素纳入结果列表 `texans`。

下面介绍最常用的代码块，以及接收这些代码块作为参数的 Eclipse Collections 方法。除这里描述的通用代码块类型外，Eclipse Collections 还提供一套基础类型专用代码块及其配套工厂，例如 `IntFunction`、`IntPredicate`、`IntProcedure`。这类代码块供基础类型集合上的相关方法使用。

> [!NOTE]
>
> **形参（parameter）和实参（argument）**
>
> 形参一般指输入的形式定义，实参指传入的实际指。
>
> 为了便于解释，下面将迭代方法（如 `select()`）接收的输入表述为形参，这类形参可以是代码块（如 `Predicate` 实现）。将代码块接收的输入称为实参，即循环遍历过程中的每个元素。

## Predicate

**Predicate（断言）**是单实参代码块，对元素进行判定并返回布尔值。它也被称作过滤器，配合筛选类方法使用：`select`、`reject`、`detect`、`anySatisfy`、`allSatisfy` 以及 `count`。

|              | 描述                                               | 参数      | 返回    | 方法                                                         |
| ------------ | -------------------------------------------------- | --------- | ------- | ------------------------------------------------------------ |
| `Predicate`  | 对集合中每个元素进行判定，并返回 boolean 值        | One (T)   | boolean | **select**, **reject**, **detect**, **anySatisfy**, **allSatisfy**, **count** |
| `Predicate2` | 集合中每个元素作为第一个参数，由调用者传入外部参数 | Two (T,P) | boolean | **selectWith**, **rejectWith**, **detectWith**, **anySatisfyWith**, **allSatisfyWith**, **countWith** |

实现 `Predicate#accept` 方法，用以判断传入该方法的对象是否满足此 `Predicate` 的判定条件。下面展示在 `select` 方法中，以匿名内部类形式实现 `Predicate` 的示例：

示例 3：以匿名内部类形式实现 Predicate

```java
MutableList<Integer> greaterThanFifty =
    list.select(new Predicate<Integer>()
    {
        public boolean accept(Integer each)
        {
            return each > 50;
        }
    });
```

`Predicates` 工具类可用于构建常用筛选条件。`Predicates` 支持相等、不相等、小于、大于、小于等于、大于等于、包含、不包含、与、或，以及大量其他断言类型操作。

示例 4：借助 Predicates 工厂执行 select 筛选

```java
MutableList<Integer> myList =...
MutableList<Integer> selected1 = myList.select(Predicates.greaterThan(50));
```

## Predicate 工厂

| 工厂类                                                       | 说明                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| **[Predicates](https://www.eclipse.org/collections/javadoc/11.1.0/org/eclipse/collections/impl/block/factory/Predicates.html)** | 支持 equal, greaterThan, lessThan, in, notIn, and, or, instanceOf, null, notNull, anySatisfy, allSatisfy, etc. |
| **[Predicates2](https://www.eclipse.org/collections/javadoc/11.1.0/org/eclipse/collections/impl/block/factory/Predicates2.html)** | 配合后缀为 `With` 的一系列方法使用，例如 **selectWith**      |
| **[StringPredicates](https://www.eclipse.org/collections/javadoc/11.1.0/org/eclipse/collections/impl/block/factory/StringPredicates.html)** | 支持 empty, notEmpty, contains, isAlpha, isNumeric, isBlank, startsWith, endsWith, matches, etc. |
| **[IntegerPredicates](https://www.eclipse.org/collections/javadoc/11.1.0/org/eclipse/collections/impl/block/factory/IntegerPredicates.html)** | 支持 isEven, isOdd, isPositive, isNegative, isZero.          |
| **[LongPredicates](https://www.eclipse.org/collections/javadoc/11.1.0/org/eclipse/collections/impl/block/factory/LongPredicates.html)** | 支持 isEven, isOdd, isPositive, isNegative, isZero.          |

## Function