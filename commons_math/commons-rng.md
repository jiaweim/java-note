# Apache Commons RNG

## 简介

Commons RNG 提供为随机数生成器。

目标是提供一个简单易用的 API，并在代码和 javadoc 中提供基本原理解释。其代码在 `org.apache.commons.math3.random` 包的基础上进行改进，并切断与 `java.util.Random` 的联系。

该工具包分为多个模块：

- Client-API (Java 8+)

提供用户接口。

- Core (Java 8+)

包含多个伪随机数生成器的实现。该模块中的代码旨在 commons-rng 内部使用，用户代码不应该直接访问。随着 Java 模块化的出现，该库的未来版本可能会强制通过 `RandomSource` 工厂类访问。

- Simple (Java 8+)

提供创建 commons-rng-core 模块中生成器的工厂方法。

- Sampling (Java 8+)

根据指定概率分布生成数字序列；从几何形状中抽样坐标；从集合中抽样等。相当于 commons-rng-client-api 的使用示例。

- **Examples**：commons-rng 还提供了各种示例程序，用于演示各种用法。这些示例不是库的一部分，可以在 github 源码仓库查看。主要包括：
  - examples-jhm: JMH 基准测试，使用 JMH 基准框架评测生成器的相对性能。
  - examples-stress: 压力测试，实现一个调用外部工具的 wrapper ，通过将生成器的输出提交给一系列压力测试来评估生成器的质量。
  - examples-sampling：概率密度，生成概率密度图
  - examples-jpms: JPMS 集成（java 11+)，展示如何使用各个模块
  - examples-quadrature: 使用 quasi-Montecarlo 积分估算 π 值

## 使用概览

javadoc 提供了各个方法的详细说明。

- 随机数生成器通过 `RandomSource` 中定义的工厂方法实例化

```java
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();
```

- 生成器可以从特定 java 类型指定范围随机选择值

```java
boolean isOn = rng.nextBoolean(); // "true" or "false".
```

```java
int n = rng.nextInt();         // Integer.MIN_VALUE <= n <= Integer.MAX_VALUE.
int m = rng.nextInt(max);      // 0 <= m < max.
int l = rng.nextInt(min, max); // min <= l < max.
```

```java
long n = rng.nextLong();         // Long.MIN_VALUE <= n <= Long.MAX_VALUE.
long m = rng.nextLong(max);      // 0 <= m < max.
long l = rng.nextLong(min, max); // min <= l < max.
```

```java
float x = rng.nextFloat();         // 0 <= x < 1.
float y = rng.nextFloat(max);      // 0 <= y < max.
float z = rng.nextFloat(min, max); // min <= z < max.
```

```java
double x = rng.nextDouble();         // 0 <= x < 1.
double y = rng.nextDouble(max);      // 0 <= y < max.
double z = rng.nextDouble(min, max); // min <= z < max.
```

- 生成器可以用随机数填充指定 byte 数组

```java
byte[] a = new byte[47];
// The elements of "a" are replaced with random values from the interval [-128, 127].
rng.nextBytes(a);
```

```java
byte[] a = new byte[47];
// Replace 3 elements of the array (at indices 15, 16 and 17) with random values.
rng.nextBytes(a, 15, 3);
```

- 生成器可以返回原始类型流

```java
IntStream s1 = rng.ints();         // [Integer.MIN_VALUE, Integer.MAX_VALUE]
IntStream s2 = rng.ints(max);      // [0, max)
IntStream s3 = rng.ints(min, max); // [min, max)
```

```java
LongStream s1 = rng.longs();         // [Long.MIN_VALUE, Long.MAX_VALUE]
LongStream s2 = rng.longs(max);      // [0, max)
LongStream s3 = rng.longs(min, max); // [min, max)
```

```java
DoubleStream s1 = rng.doubles();         // [0, 1)
DoubleStream s2 = rng.doubles(max);      // [0, max)
DoubleStream s3 = rng.doubles(min, max); // [min, max)
```

可以限制流的大小：

```java
// Roll a die 1000 times
int[] rolls = rng.ints(1000, 1, 7).toArray();
```

生成流的方法默认通过重复调用相关的 `next` 方法实现，可能有性能开销。可以使用 sampling 模块的实例创建高效的流，sampler 可以在构造时提前计算系数，加快计算速度。

- `UniformRandomProvider` 接口为除 `nextLong` 之外的所有生成方法提供了默认实现，新生成器必须只提供 64-bit 随机源

```java
UniformRandomProvider rng = new SecureRandom()::nextLong;
```

core 模块提供了 32-bit 和 64-bit 随机源的抽象类，以及接口中没有的其它功能。

- 为了生成可重复序列，在实例化生成器时必须提供 seed

```java
UniformRandomProvider rng = RandomSource.SPLIT_MIX_64.create(5776);
```

如果没有 seed，则隐式生成随机 seed。

`RandomSource` 提供了生成各种类型 seed 的方法。

```java
int seed = RandomSource.createInt();

long seed = RandomSource.createLong();

int[] seed = RandomSource.createIntArray(128); // Length of returned array is 128.

long[] seed = RandomSource.createLongArray(128); // Length of returned array is 128.
```

- 以下任何类型都可以作为 seed 参数传递给 `create` 方法

  - `int` or `Integer`

  - `long` or `Long`

  - `int[]`

  - `long[]`

  - `byte[]`

```java
UniformRandomProvider rng = RandomSource.ISAAC.create(5776);

UniformRandomProvider rng = RandomSource.ISAAC.create(new int[] { 6, 7, 7, 5, 6, 1, 0, 2 });

UniformRandomProvider rng = RandomSource.ISAAC.create(new long[] { 0x638a3fd83bc0e851L, 0x9730fd12c75ae247L });
```

需要注意的是，在初始化时底层算法：

- 没有完全使用所有 seed 的内容
- 使用给定 seed 作为输入填充其内部状态（避免过于统一的初始状态）

这两种情况的输出都不是标准的，但在不同版本之间不变。

每个 RNG 实现都有一个native-seed，当传递给 `create` 的参数不是 native-type 时，会自动转换。转换会保留信息内容，但不固定（不同版本可能使用不同的转换方式）。

因此，为了保证在不同版本库中生成序列的可重复性，用户应使用本地 seeds：

```java
long seed = 9246234616L;
if (!RandomSource.TWO_CMRES.isNativeSeed(seed)) {
    throw new IllegalArgumentException("Seed is not native");
}
```

对每个可用实现，native-seed 类型在 javadoc 中有说明。

- 当对随机源实现参数化，自定义参数在 seed 后给出

```java
int seed = 96912062;
int first = 7; // Subcycle identifier.
int second = 4; // Subcycle identifier.
UniformRandomProvider rng = RandomSource.TWO_CMRES_SELECT.create(seed, first, second);
```

- generator 的当前状态可以保存并恢复

```java
import org.apache.commons.rng.RestorableUniformRandomProvider;
import org.apache.commons.rng.RandomProviderState;

RestorableUniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();
RandomProviderState state = rng.saveState();
double x = rng.nextDouble();
rng.restoreState(state);
double y = rng.nextDouble(); // x == y.
```

- 从 `create` 方法返回的 `UniformRandomProvider` 没有实现 `java.io.Serializable` 接口

但是，如果已知 random-source 通信 channel 的两端，自定义序列化很容易。

```java
import org.apache.commons.rng.RestorableUniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.rng.core.RandomProviderDefaultState;

RandomSource source = RandomSource.KISS; // Known source identifier.

RestorableUniformRandomProvider rngOrig = source.create(); // Original RNG instance.

// Save and serialize state.
RandomProviderState stateOrig = rngOrig.saveState(rngOrig);
ByteArrayOutputStream bos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(bos);
oos.writeObject(((RandomProviderDefaultState) stateOrig).getState());

// Deserialize state.
ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bis);
RandomProviderState stateNew = new RandomProviderDefaultState((byte[]) ois.readObject());

RestorableUniformRandomProvider rngNew = source.create(); // New RNG instance from the same "source".

// Restore original state on the new instance.
rngNew.restoreState(stateNew);
```

- `JumpableUniformRandomProvider` 接口允许创建 generator 的  copy，并在单个 jump 中将 generator 的状态推进很多。这可用于创建一组 generators，这些 generators 生成的序列在 jump 长度内不会重叠，可用于并行计算

```java
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.JumpableUniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import java.util.concurrent.ForkJoinPool;

RandomSource source = RandomSource.XO_RO_SHI_RO_128_SS; // Known to be jumpable.

JumpableUniformRandomProvider jumpable = (JumpableUniformRandomProvider) source.create();

// For use in parallel
int streamSize = 10;
jumpable.jumps(streamSize).forEach(rng -> {
    ForkJoinPool.commonPool().execute(() -> {
        // Task using the rng
    });
});
```

注意，这里 RNG 流的串行的。每个 RNG 在可能耗时任务中使用，，这些任务使用 executor-service 执行。

在上例中，已知源实现了 `JumpableUniformRandomProvider` 接口。但并非所有 generators 实现该接口，可以使用 `isJumpable()` 和 `isLongJumpable()` 来确定 `RandomSource` 是否可以跳转。

```java
import org.apache.commons.rng.simple.RandomSource;

public void initialise(RandomSource source) {
    if (!source.isJumpable()) {
        throw new IllegalArgumentException("Require a jumpable random source");
    }
    // ...
}
```

- `SplittableUniformRandomProvider` 接口允许 generator 拆分为两个对象（原实例和新实例），每个对象都实现相同接口，并且可以无限递归拆分。该接口可用于分支数量未知的并行计算。这些 generators 提供对并行流的支持。需要注意的是，新建的 generator 生成的序列不可避免会与现有 generator 有关联。支持该接口的 generator 都在最大限度地减少实例之间的相关性。由并行流递归拆分的 generators 对其输出序列的碰撞具有鲁棒性

```java
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.SplittableUniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

RandomSource source = RandomSource.L64_X128_MIX; // Known to be splittable.

SplittableUniformRandomProvider splittable = (SplittableUniformRandomProvider) source.create();

// For use in parallel
int streamSize = 10;
jumpable.splits(streamSize).parallel().forEach(rng -> {
    // Task using the rng
});
```

注意，这里 RNG 流是并行的，每个 RNG 都用于可能耗时任务中，如果包含的流支持多线程，则该任务可以与其它任务并行。

在上例中，已知 source 实现了 `SplittableUniformRandomProvider` 接口。并非所有 generators 支持该功能，可以使用 `RandomSource` 的 `isSplittable()` 方法判断是否可拆分：

```java
import org.apache.commons.rng.simple.RandomSource;

public void initialise(RandomSource source) {
    if (!source.isSplittable()) {
        throw new IllegalArgumentException("Require a splittable random source");
    }
    // ...
}
```

- 生成各种分布的随机值

```java
import org.apache.commons.rng.sampling.distribution.ContinuousSampler;
import org.apache.commons.rng.sampling.distribution.GaussianSampler;
import org.apache.commons.rng.sampling.distribution.ZigguratSampler;

ContinuousSampler sampler = GaussianSampler.of(ZigguratSampler.NormalizedGaussian.of(RandomSource.ISAAC.create()),
                                               45.6, 2.3);
double random = sampler.sample();
```

```java
import org.apache.commons.rng.sampling.distribution.DiscreteSampler;
import org.apache.commons.rng.sampling.distribution.RejectionInversionZipfSampler;

DiscreteSampler sampler = RejectionInversionZipfSampler.of(RandomSource.ISAAC.create(),
                                                           5, 1.2);
int random = sampler.sample();
```

- 基础类型 `int`, `long` 和 `double` 提供了 Sampler 接口。`samples` 方法使用 Java 8 Stream API 创建样本流

```java
import org.apache.commons.rng.sampling.distribution.PoissonSampler;
import org.apache.commons.rng.simple.RandomSource;

double mean = 15.5;
int streamSize = 100;
int[] counts = PoissonSampler.of(RandomSource.L64_X128_MIX.create(), mean)
                             .samples(streamSize)
                             .toArray();
```

```java
import org.apache.commons.rng.sampling.distribution.ZigguratSampler;
import org.apache.commons.rng.simple.RandomSource;

// Lower-truncated Normal distribution samples
double low = -1.23;
double[] samples = ZigguratSampler.NormalizedGaussian.of(RandomSource.L64_X128_MIX.create())
                                                     .samples()
                                                     .filter(x -> x > low)
                                                     .limit(100)
                                                     .toArray();
```

- 

## library 布局

client 的 API 由 `org.apache.commons.rng` 中定义的类和接口组成：

- `UniformRandomProvider` 接口提供指定范围均匀分布的一系列随机序列
- `RestorableUniformRandomProvider` 和 `RandomProviderState` 提供 "save/restore" API
- `JumpableUniformRandomProvider` 和 `LongJumpableUniformRandomProvider` 为并行计算提供 copy-jump API，适用于已知并行任务数的情况
- `SplittableUniformRandomProvider` 为并行计算提供 split API，适用于并行任务数未知的任务，例如使用 JDK 并行流执行任务

用于实例化 generators 的 API 在 `org.apache.commons.rng.simple` 包中。`RandomSource` enum 指定使用哪种算法来生成随机值序列。





## 参考

- https://commons.apache.org/proper/commons-rng/index.html
- https://github.com/apache/commons-rng