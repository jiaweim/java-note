# 代码块（Code Blocks）

2026-08-05⭐
@author Jiawei Mao
***
## 简介

在 EC（Eclipse‑Collections） 中，代码块（code block）指只有一个抽象方法的函数式对象，常用作迭代方法的入参。它是一种抽象，用于定义遍历过程中每个集合元素的执行逻辑。代码块有助于将执行内容与实现方式解耦。

EC 的代码块大致等价于 Lambda 表达式。在 Java 8之前，能够实现类似 Lambda 功能的方案就是匿名内部类。

下面提供两段示例：分别使用 Java8+ 的 Lambda 以及匿名内部类去实现 `Predicate`（断言）。

示例 1：使用 Lambda 执行 select

```java
MutableList<Person> texans =
  this.people.select( each -> each.getAddress().getState().equals("TX"));
Verify.assertSize(1, texans);
```

示例 2：使用匿名内部类执行 select

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

两种实现方式的逻辑一致：若 `people` 列表中任意元素的 `state` 属性值等于 `"TX"`，`select` 方法就会把该元素存入结果列表 `texans`。

下面介绍最常用的代码块，以及接收这些代码块作为参数的 EC 方法。除下文描述的通用代码块类型外，EC 还提供一套原生类型代码块与对应的工厂类，例如 `IntFunction`、`IntPredicate`、`IntProcedure`。这类代码块供原生类型集合上的相关方法使用。

> [!NOTE]
>
> **形参（parameter）和实参（argument）**
>
> 形参一般指输入的形式定义，实参指传入的实际指。
>
> 为了便于解释，下面将迭代方法（如 `select()`）接收的输入表述为形参，这类形参可以是代码块（如 `Predicate` 实现）。将代码块接收的输入称为实参，即循环遍历过程中的每个元素。

## Predicate

**Predicate（断言）**是单入参代码块，对集合元素进行判定并返回布尔值。它也被称作过滤器，配合筛选类方法使用：`select`、`reject`、`detect`、`anySatisfy`、`allSatisfy` 以及 `count`。

| 接口         | 功能描述                                             | 参数       | 返回    | 适用方法                                                     |
| ------------ | ---------------------------------------------------- | ---------- | ------- | ------------------------------------------------------------ |
| `Predicate`  | 对集合中每个元素进行判定，返回 boolean 值            | 1 个 (T)   | boolean | **select**, **reject**, **detect**, **anySatisfy**, **allSatisfy**, **count** |
| `Predicate2` | 评估集合元素（第一个入参）；第二个入参由调用方法传入 | 2 个 (T,P) | boolean | **selectWith**, **rejectWith**, **detectWith**, **anySatisfyWith**, **allSatisfyWith**, **countWith** |

需要实现 `Predicate#accept` 方法，用来判断传入对象是否满足该 `Predicate` 的判定条件。下面展示在 `select` 方法中，以匿名内部类形式实现 `Predicate`：

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

`Predicates` 工具类可用于构建常用过滤器。`Predicates` 支持相等、不相等、小于、大于、小于等于、大于等于、包含、不包含、与、或，以及大量其他断言类型操作。

示例 4：使用 `Predicates` 工厂执行 select

```java
MutableList<Integer> myList =...
MutableList<Integer> selected1 = myList.select(Predicates.greaterThan(50));
```

### Predicate 工厂

| 工厂类                                                       | 说明                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| **[Predicates](https://www.eclipse.org/collections/javadoc/11.1.0/org/eclipse/collections/impl/block/factory/Predicates.html)** | 支持 equal, greaterThan, lessThan, in, notIn, and, or, instanceOf, null, notNull, anySatisfy, allSatisfy, etc. |
| **[Predicates2](https://www.eclipse.org/collections/javadoc/11.1.0/org/eclipse/collections/impl/block/factory/Predicates2.html)** | 配合后缀为 `With` 的集合方法使用，例如 **selectWith**        |
| **[StringPredicates](https://www.eclipse.org/collections/javadoc/11.1.0/org/eclipse/collections/impl/block/factory/StringPredicates.html)** | 支持 empty, notEmpty, contains, isAlpha, isNumeric, isBlank, startsWith, endsWith, matches, etc. |
| **[IntegerPredicates](https://www.eclipse.org/collections/javadoc/11.1.0/org/eclipse/collections/impl/block/factory/IntegerPredicates.html)** | 支持 isEven, isOdd, isPositive, isNegative, isZero.          |
| **[LongPredicates](https://www.eclipse.org/collections/javadoc/11.1.0/org/eclipse/collections/impl/block/factory/LongPredicates.html)** | 支持 isEven, isOdd, isPositive, isNegative, isZero.          |

## Function

Function 代码块最常见的用法：将集合的每一个元素当作入参交给函数逻辑。它借助取值方法（getter，对应 valueOf）提取元素属性，之后返回经过计算之后的值；倘若不存在运算逻辑，则直接返回该属性本身。

很多 Eclipse‑Collections 方法都接收 `Function` 代码块作为入参：

1. **collect**：为集合内每一个元素计算出新值，返回经过类型转换之后的全新同类型集合；
2. **groupBy**：为新建 `Multimap` 当中每一个子集合生成对应的键；
3. **flatCollect**：函数必须返回一个可迭代对象，后续所有迭代对象会被扁平化，合并成单个集合；
4. `Predicates` 工厂类的属性运算方法（例如 attributeLessThanOrEqualTo），依靠 Function 构造出布尔类型的 Predicate 判断对象。

| 接口名称           | 功能说明                                                     | 入参         | 返回值   | 调用该接口的方法                            |
| ------------------ | ------------------------------------------------------------ | ------------ | -------- | ------------------------------------------- |
| Function（转换器） | 接收集合元素作为代码块逻辑入参运算，返回处理之后的值         | 1 个 (T)     | 对象 (V) | collect、flatCollect、groupBy               |
| Function0          | 执行逻辑并返回结果（类似 Callable），用于延迟求值            | 无参数       | 对象 (V) | getIfAbsent、getIfAbsentPut、ifPresentApply |
| Function2          | 供 injectInto 类方法调用；首个参数为累加器，第二个参数为当前遍历的集合元素 | 2 个 (T,P)   | 对象 (V) | forEachEntry、injectInto、collectWith       |
| Function3          | 适配 injectIntoWith；第一个参数为累加值、第二个为遍历元素、第三个为外部传入参数；每一轮循环的运算结果会作为下一轮的首参数 | 3 个 (T,P,?) | 对象 (V) | injectIntoWith                              |

### 其他函数

1. **IfFunction**

   搭配判断器与函数，实现 if‑else 分支逻辑。

2. **CaseFunction**

   借助多个断言实现多条件、基于规则的值选择（建议在熟悉框架之后再使用）。

### 函数工厂

**Functions（通用转换函数工厂）**

内置获取对象类型、转为字符串、原值透传等工具方法：`getToClass`、`getToString`、`getPassThru`

**Functions0（无参函数工厂）**

用来生成各类集合实例、空值以及固定常量：

`newFastList`、`newHashBag`、`newUnifiedMap`、`newUnifiedSet`、`nullValue`、`value`

**Functions2（双参数函数工厂）**

`fromFunction`：由普通单参数 Function 包装生成双‑参数函数

**StringFunctions（字符串专用转换工厂）**

首字母、字符串长度、子串、获取首个字符、转为整型、转小写、转为对应原生基础类型、转大写、去除首尾空白；

对应 API：`firstLetter`、`length`、`subString`、`toFirstChar`、`toInteger`、`toLowerCase`、`toPrimitive [type]`、`toUpperCase`、`trim`

## Procedure

Procedure（执行过程）处理唯一传入的参数、执行相应逻辑，没有返回值。

Procedure 大多配合以 `forEach` 模式的遍历方法使用。

### 计数与计算

1. **CountProcedure**

   对目标对象执行断言判断，断言返回真值时计数器自增。

2. **CounterProcedure**

   包装任意代码块，统计该代码块的执行次数。

### 流程控制类

1. **ChainedProcedure**

   顺序串联多个待执行代码块；可装配 Procedure、Function、Function2。

2. **CaseProcedure**

   以对象形式实现分支语句。不同于普通 switch‑case 只比对单个数值，它由多组判断器‑执行代码构成；命中首个返回 true 的判断条件之后，便运行对应的执行块。

3. **IfProcedure**

   断言条件成立时执行指定代码块；条件不成立且配置了 else‑分支，则执行 elseProcedure。

4. **IfProcedureWith**

   功能等同于 IfProcedure，额外接收迭代方法传入的第二个参数。

5. **ObjectIntProcedure**

   携带对象与整型两个入参；整型参数一般代表集合当前元素的下标索引。

### 集合、映射修改专用过程块

1. **CollectionAddProcedure**

   触发代码块时，将元素添加至目标集合。

2. **CollectionRemoveProcedure**

   触发代码块时，从目标集合移除对应元素。

3. **MapPutProcedure**

   借助自定义函数计算元素对应的键，再把该元素存入指定 Map 的对应键位。

4. **MultimapPutProcedure**

   通过函数算出键值，将元素存放进可变多值映射容器 MutableMultimap。

### Procedure（执行过程）工厂类

1. **Procedures（单参数过程工厂）**

   拼接过程块、绑定参数、设置 case 默认分支、封装 ObjectIntProcedure、if‑else 分支、条件执行、控制台打印、多线程同步遍历等工具方法：`append, bind, caseDefault, fromObjectIntProcedure, ifElse, ifTrue, println, synchronizedEach`

2. **Procedures2（双参数过程工厂）**

   元素加入集合、由普通单参数 Procedure 封装生成双参数过程：`addToCollection, fromProcedure`