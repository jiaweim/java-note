# 随机数生成器

## Random

`Random` 类用于生成伪随机数，其周期为 $s^{48}$。该类使用 48-bit seed，使用线性同余公式进行处理，详情参考 Donald E. Knuth, The Art of Computer Programming, Volume 2, Third edition: Seminumerical Algorithms, Section 3.2.1.

用相同 seed 创建的两个 `Random` 实力，并且对每个实例方法调用顺序相同，则它们将生成相同的数字序列。为了保证该属性，为 `Random` 类指定了特定的算法；为了实现 Java 代码的绝对可移植性，Java 实现必须使用该算法。但是，`Random` 的子类可以使用其它算法，只要遵守所有方法的约定即可。

`Random` 类实现算法使用一个 `protected` 的工具方法，每次调用该方法最多可提供 32 个伪随机数 bits。

大多应用使用 `Math.random` 方法更简单。

`java.util.Random` 线程安全。但是在不同线程使用同一个 `java.util.Random` 实例可能遇到争用，从而导致性能不佳。在多线程中推荐使用 `java.uti.concurrent.ThreadLocalRandom`。

`java.util.Random` 的实例不是加密安全的。如果需要加密，可以使用 `java.security.SecureRandom` 来获取加密的伪随机数生成器，用于安全敏感型应用。

> [!NOTE]
>
> 周期：生成一系列值不会重复的长度。

## SplittableRandom

`SplittableRandom` 是一个均匀分布伪随机数生成器（周期$2^{64}$），适用于可能生成子任务的独立并行计算。`SplittableRandom` 支持生成 int, long, double 类型伪随机数，其用法与 `Random` 类类似，主要差异如下：

- 生成的一系列值通过了 DieHarder 测试，测试了随机数生成器的独立性和一致性。（最新测试版本 3.31.1）。虽然这些测试仅验证了部分范围和类型，但对其它类型或范围预计具有类似属性。周期为 $2^{64}$。
- `split` 方法构造并返回一个新的 `SplittableRandom` 实例，该实例与当前实例不共享可变状态。但是，两个对象生成的值大概率具有相同的统计属性，就好像单个线程使用单个 `SplittableRandom` 对象生成相同数量的值一样。
- `SplittableRandom` 的实例线程不安全。它们被设计为跨线程拆分，而不是共享。例如，使用伪随机数的 fork/join 计算可能包括类似 `new Subtask(aSplittableRandom. split()).fork()` 的构造。
- 该类包含生成伪随机数 stream 的方法，在 `stream.parallel()` 模式下使用采用上述技术。

`SplittableRandom` 的实例也不具备加密安全性。在安全敏感型应用中推荐使用 `java.security.SecureRandom`。

