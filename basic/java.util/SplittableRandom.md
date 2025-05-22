# 随机数生成器

2025-05-22
@author Jiawei Mao
***
## Random

`Random` 类用于生成伪随机数，其周期为 $2^{48}$。该类使用 48-bit seed，使用线性同余公式进行处理，详情参考 Donald E. Knuth, The Art of Computer Programming, Volume 2, Third edition: Seminumerical Algorithms, Section 3.2.1.

用相同 seed 创建的两个 `Random` 实例，并且对每个实例方法调用顺序相同，则它们将生成相同的数字序列。为了保证该属性，为 `Random` 类指定了特定的算法；为了实现 Java 代码的绝对可移植性，Java 实现必须使用该算法。但是，`Random` 的子类可以使用其它算法，只要遵守所有方法的约定即可。

`Random` 类实现算法使用一个 `protected` 的工具方法，每次调用该方法最多可提供 32 个伪随机数 bits。

大多应用使用 `Math.random` 方法更简单。

`java.util.Random` 线程安全。但是在不同线程使用同一个 `java.util.Random` 实例可能遇到争用，从而导致性能不佳。在多线程中推荐使用 `java.uti.concurrent.ThreadLocalRandom`。

`java.util.Random` 的实例不是加密安全的。如果需要加密，可以使用 `java.security.SecureRandom` 来获取加密的伪随机数生成器，用于安全敏感型应用。

> [!NOTE]
>
> 周期：生成一系列值不会重复的长度。

例如，`nextInt()` 方法随机生成一个 int 值，所有 $2^{32}$ 个整数以相同概率生成：

```java
Random random = new Random();
int randomInt = random.nextInt();
```

`nextInt(int origin, int bound)` 随机生成一个 `origin` 到 `bound` (exclusive)之间的整数。如果 `bound` 是 2 的指数，那么限制操作只是一个简单的掩码操作，否则就是重复调用 `nextInt()` 操作，直到返回值在指定范围。

## ThreadLocalRandom

`ThreadLocalRandom` 是 Java 1.7 引入的一个更高效的随机数生成器，为 `Random` 的子类。主要用于如下场景：

- 需要一个中等质量非加密的随机数生成器
- 不需要特定算法或 seed，`ThreadLocalRandom` 不能设置 seed
- 不同线程使用的生成器互相独立

另外：

- `ThreadLocalRandom` 不适合加密需求
- `ThreadLocalRandom` 不适合分治算法，即同一个任务在大量线程执行，或者需要大量随机数的地方
- `ThreadLocalRandom` 不能设置 seed

尽管有这些局限性，`ThreadLocalRandom` 相比 `Random` 还是更有优势：生成的随机数质量更高，通过了许多统计测试；周期更大 $2^{64}$；速度快几倍。

### 使用 ThreadLocalRandom

`ThreadLocalRandom` 的用法和 `Random` 基本一样，可以直接替换 `Random`，只是不需要创建实例，而是 `ThreadLocalRandom.current()` 静态方法。

```java
// 全范围随机 int
int anInt = ThreadLocalRandom.current().nextInt();

// 指定范围随机 int
int min = 10;
int max = 20;
int val = ThreadLocalRandom.current().nextInt(min, max);
```

### ThreadLocalRandom 算法

`ThreadLocalRandom` 使用 **SplitMin** 算法生成随机数。SplitMin 采用一个简单的 seed 序列，对序列的每个数字应用 mixing 函数（其实就是一种 hash）。该 hash 函数的输出就是下一个随机数。我们可以把 seed 序列想象成简单的计数序列（1, 2, 3...），mixing 函数将 seed 序列转换为随机数字序列。

**ThreadLocalRandom mixing 函数**

mixing 函数与 hash 函数基本相同：

- **avalanching (雪崩)**：如果输入序列的两个数字只相差一个 bit，那么我们希望该微小差异在输出中变为 ~50% 的差异
- 均匀分布：输出应在可能值上均匀分布

`ThreadLocalRandom` 使用的 SplitMix 函数是从 MurmurHash3 的 64-bit 版本变化而来：

```java
private static long mix64(long z) {
    z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
    z = (z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L;
    return z ^ (z >>> 33);
}
```

**seed 序列**

原则上我们可以从  `mix64()` 获得一个随机数生成器：

- 从一个 64-bit seed 开始
- 在当前 seed 上调用 `mix64()` 获得输出，即为随机数
- seed+1

每次将 seed 增加一个奇数，可以确保不漏掉所有 $2^{64}$ 个值：换言之，生成器的周期为 $2^{64}$。如果每次将 seed+2，则可能跳过一般的值。不过在实践中，我们可以尝试选择一种有助于提供额外随机性的增量。

选择的增量称为 gamma。对 `ThreadLocalRandom`，所有线程使用相同 gamma 值。所选值称为 golden-gamma：0x9e3779b97f4a7c15L，它是黄金比例的 64-bit 表示。

> [!TIP]
>
> 不同线程执行不同任务，适合采用 `ThreadLocalRandom`，基本可以作为默认随机数生成器使用。

## SplittableRandom

模拟、蒙特卡洛搜索等需要在每个试验中生成大量随机数，`ThreadLocalRandom` 的 $2^{64}$ 周期成为一个限制。另外，我们还希望并行化此类算法。

通常，我们可以转向一个周期更长的随机数生成器，例如，Numerical Recipes 的作者提出了一个周期为 $2^{191}$ 的组合生成器。但是，周期更长意味着每生成一个数后需要**更新更多状态**。如果多个现场共享一个实例，则必须以线程安全的方式更新。

另外，模拟等算法通常是 divide and conquer 类型，将自身分解为许多子任务。单个子任务，如在 search-tree 的一个 node 运行模拟，只需消耗相对较少的随机数：$2^{64}$ 周期足以满足这些 node 的需求。但是，如果我们采用同一个生成器的多个实例，那么最终会得到同一序列的不同部分。如果实例足够多，或生成的随机数足够多，那么部分随机数序列可能会重合，该现场称为 seed-collision。

理想情况下，我们希望每个 node 能够使用周期为 $2^{64}$ 的生成器，同时这些子任务的生成器的合并效果与更大周期的单个生成器没多大差别，即不同子任务的随机数序列不会碰撞。

`SplittableRandom` 就是为这种并行计算设计的，在 java 8 引入。该生成器从一个 base-gen 开始，启动分治法，在分裂子任务时，从 base-gen 实例生成 sub-gen。每个 sub-gen 只需要更新 64 位状态，可以更有效地生成数字。每个 sub-gen 从自身唯一序列获得随机数，合并效果与周期 $2^{127}$ 的单个随机数生成器近似。

### SplittableRandom 原理

要理解 `SplittableRandom` 的原理，首先要明白 `ThreadLocalRandom` 的原理。`ThreadLocalRandom` 从一个 seed 序列开始，然后应用 hash 函数生成一系列随机数，然后每次迭代 seed 增加固定的 gamma 值。 

`SplittableRandom` 每次 split 时采用不同的 gamma 值。

### SplittableRandom 的使用

`SplittableRandom` 是一个均匀分布伪随机数生成器（周期 $2^{64}$），为并行计算而设计。`SplittableRandom` 支持生成 int, long, double 类型伪随机数，其用法与 `Random` 类似，主要差异如下：

- 生成的一系列值通过了 DieHarder 测试，测试了随机数生成器的独立性和一致性。（最新测试版本 3.31.1）。虽然这些测试仅验证了部分范围和类型，但对其它类型或范围预计具有类似属性。周期为 $2^{64}$。
- `split` 方法构造并返回一个新的 `SplittableRandom` 实例，该实例与当前实例不共享可变状态。但是，两个对象生成的值大概率具有相同的统计属性，就好像单个线程使用单个 `SplittableRandom` 对象生成相同数量的值一样。
- `SplittableRandom` 的实例线程不安全。它们被设计为跨线程拆分，而不是共享。例如，使用伪随机数的 fork/join 计算可能包括类似 `new Subtask(aSplittableRandom.split()).fork()` 的构造。
- 该类包含生成伪随机数 stream 的方法，在 `stream.parallel()` 模式下采用上述技术。

`SplittableRandom` 的实例也不具备加密安全性。在安全敏感型应用中推荐使用 `java.security.SecureRandom`。

## 参考

- https://www.javamex.com/tutorials/random_numbers/SplittableRandom.shtml
