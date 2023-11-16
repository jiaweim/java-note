# 接口优于抽象类


Java 有两种机制允许多个实现：接口和抽象类。在 Java 8（JLS 9.4.3）引入接口的默认方法（default）后，这两种机制都可以为实例方法提供实现。一个**主要区别**是：实现抽象类定义的类型，必须继承抽象类。由于 Java 只允许单一继承，极大限制了抽象类在类型定义中的应用。而一个类可以实现多个接口，没有该限制。

**现有类实现新接口很容易**。只需添加接口要求的方法，并在类声明中添加一个 `implements` 子句。但是，很难修改现有类来继承一个新的抽象类。这会破坏类层级结构。

**接口是定义混合类型（mixin）的理想选择**。简单类说，mixin 为类提供了额外的可选功能。例如，`Comparable` 是一个 mixin 接口，表示实现该接口的类型能互相比较。抽象类由于单继承性质，无法用于定义 mixin。

**接口可以用来构建非分层类型框架**。分层类型对于组织事物很好，但是应用受限。例如，假设有两个接口，一个表示歌手，一个表示作曲家：

```java
public interface Singer {
    AudioClip sing(Song s);
}

public interface Songwriter {
    Song compose(int chartPosition);
}
```

在现实生活中，歌手也可以是作曲家。由于我们使用接口而非抽象类来定义这些类型，所以一个类可以同时实现歌手和作曲家两个接口。例如，可以定义一个继承歌手和作曲家的第三个接口，并添加适合这个组合的新方法：

```java
public interface SingerSongwriter extends Singer, Songwriter {
    AudioClip strum();
    void actSensitive();
}
```

接口结合[包装范式](18_composition.md)可以实现安全、强大的功能增强。而抽象类强制继承，添加功能不方便。

当其他接口方法有明显的接口方法实现时，可以考虑向程序员提供默认形式的方法实现帮助。 有关此技术的示例，请参阅第 104 页的 removeIf 方法。如果提供默认方法，请确保使用@implSpec Javadoc 标记（条目 19）将它们文档说明为继承。

　　使用默认方法可以提供实现帮助多多少少是有些限制的。 尽管许多接口指定了 Object 类中方法（如 equals 和 hashCode）的行为，但不允许为它们提供默认方法。 此外，接口不允许包含实例属性或非公共静态成员（私有静态方法除外）。 最后，不能将默认方法添加到不受控制的接口中。

　　但是，你可以通过提供一个抽象的骨架实现类（abstract skeletal implementation class）来与接口一起使用，将接口和抽象类的优点结合起来。 接口定义了类型，可能提供了一些默认的方法，而骨架实现类在原始接口方法的顶层实现了剩余的非原始接口方法。 继承骨架实现需要大部分的工作来实现一个接口。 这就是模板方法设计模式[Gamma95]。

　　按照惯例，骨架实现类被称为 AbstractInterface，其中 Interface 是它们实现的接口的名称。 例如，集合框架（ Collections Framework）提供了一个框架实现以配合每个主要集合接口：AbstractCollection，AbstractSet，AbstractList 和 AbstractMap。 可以说，将它们称为 SkeletalCollection，SkeletalSet，SkeletalList 和 SkeletalMap 是有道理的，但是现在已经确立了抽象约定。 如果设计得当，骨架实现（无论是单独的抽象类还是仅由接口上的默认方法组成）可以使程序员非常容易地提供他们自己的接口实现。 例如，下面是一个静态工厂方法，在 AbstractList 的顶层包含一个完整的功能齐全的 List 实现：

```java
// Concrete implementation built atop skeletal implementation
static List<Integer> intArrayAsList(int[] a) {
    Objects.requireNonNull(a);
    // The diamond operator is only legal here in Java 9 and later
    // If you're using an earlier release, specify <Integer>
    return new AbstractList<>() {
        @Override 
        public Integer get(int i) {
            return a[i];  // Autoboxing ([Item 6](https://www.safaribooksonline.com/library/view/effective-java-third/9780134686097/ch2.xhtml#lev6))
        }
        @Override 
        public Integer set(int i, Integer val) {
            int oldVal = a[i];
            a[i] = val;     // Auto-unboxing
            return oldVal;  // Autoboxing
        }
        @Override 
        public int size() {
            return a.length;
        }
    };
}
```

当你考虑一个 List 实现为你做的所有事情时，这个例子是一个骨架实现的强大的演示。 顺便说一句，这个例子是一个适配器（Adapter）[Gamma95]，它允许一个 int 数组被看作 Integer 实例列表。 由于 int 值和整数实例（装箱和拆箱）之间的来回转换，其性能并不是非常好。 请注意，实现采用匿名类的形式（详见第 24 条）。

　　骨架实现类的优点在于，它们提供抽象类的所有实现的帮助，而不会强加抽象类作为类型定义时的严格约束。对于具有骨架实现类的接口的大多数实现者来说，继承这个类是显而易见的选择，但它不是必需的。如果一个类不能继承骨架的实现，这个类可以直接实现接口。该类仍然受益于接口本身的任何默认方法。此外，骨架实现类仍然可以协助接口的实现。实现接口的类可以将接口方法的调用转发给继承骨架实现的私有内部类的包含实例。这种被称为模拟多重继承的技术与条目 18 讨论的包装类模式密切相关。它提供了多重继承的许多好处，同时避免了缺陷。

　　编写一个骨架的实现是一个相对简单的过程，虽然有些乏味。 首先，研究接口，并确定哪些方法是基本的，其他方法可以根据它们来实现。 这些基本方法是你的骨架实现类中的抽象方法。 接下来，为所有可以直接在基本方法之上实现的方法提供接口中的默认方法，回想一下，你可能不会为诸如 Object 类中 equals 和 hashCode 等方法提供默认方法。 如果基本方法和默认方法涵盖了接口，那么就完成了，并且不需要骨架实现类。 否则，编写一个声明实现接口的类，并实现所有剩下的接口方法。 为了适合于该任务，此类可能包含任何的非公共属性和方法。

　　作为一个简单的例子，考虑一下 Map.Entry 接口。 显而易见的基本方法是 getKey，getValue 和（可选的）setValue。 接口指定了 equals 和 hashCode 的行为，并且在基本方面方面有一个 toString 的明显的实现。 由于不允许为 Object 类方法提供默认实现，因此所有实现均放置在骨架实现类中：

```java
// Skeletal implementation class
public abstract class AbstractMapEntry<K,V>
        implements Map.Entry<K,V> {
    // Entries in a modifiable map must override this method
    @Override public V setValue(V value) {
        throw new UnsupportedOperationException();
    }
    // Implements the general contract of Map.Entry.equals
    @Override 
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Map.Entry))
            return false;
        Map.Entry<?,?> e = (Map.Entry) o;
        return Objects.equals(e.getKey(),  getKey())
            && Objects.equals(e.getValue(), getValue());
    }
    // Implements the general contract of Map.Entry.hashCode
    @Override 
    public int hashCode() {
        return Objects.hashCode(getKey())
             ^ Objects.hashCode(getValue());
    }
    @Override 
    public String toString() {
        return getKey() + "=" + getValue();
    }
}
```

请注意，这个骨架实现不能在 Map.Entry 接口中实现，也不能作为子接口实现，因为默认方法不允许重写诸如 equals，hashCode 和 toString 等 Object 类方法。

　　由于骨架实现类是为了继承而设计的，所以你应该遵循条目 19 中的所有设计和文档说明。为了简洁起见，前面的例子中省略了文档注释，但是好的文档在骨架实现中是绝对必要的，无论它是否包含 一个接口或一个单独的抽象类的默认方法。

　　与骨架实现有稍许不同的是简单实现，以 AbstractMap.SimpleEntry 为例。 一个简单的实现就像一个骨架实现，它实现了一个接口，并且是为了继承而设计的，但是它的不同之处在于它不是抽象的：它是最简单的工作实现。 你可以按照情况使用它，也可以根据情况进行子类化。

　　总而言之，一个接口通常是定义允许多个实现的类型的最佳方式。 如果你导出一个重要的接口，应该强烈考虑提供一个骨架的实现类。 在可能的情况下，应该通过接口上的默认方法提供骨架实现，以便接口的所有实现者都可以使用它。 也就是说，对接口的限制通常要求骨架实现类采用抽象类的形式。