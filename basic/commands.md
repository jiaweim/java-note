# Java 命令

2025-08-01
@author Jiawei Mao
***

## 内存设置

JVM 存储数据的内存分为两部分：

- 堆（heap），存储 `new` 对象和数组等
- 堆栈，存储代码，常量

heap 还分为新生代、老生代，垃圾回收只作用于 heap，对非 heap 区域无效。

### heap 调整命令

| 命令                       | 功能                                                         |
| -------------------------- | ------------------------------------------------------------ |
| `-Xms`value                | JVM heap 分配的初始内存大小，一般为系统内存的 1/64           |
| `-Xmx`value                | JVM heap 可分配内存的上限，一般为系统内存的 1/4              |
| `-XX:NewSize=`size         | 新生代初始内存大小（需要小于 `-Xms`）                        |
| `-XX:MaxNewSize=`size      | 新生代内存上限（需要小于 `-Xmx`）                            |
| `-XX:MinHeapFreeRatio`=min | 垃圾回收后可用空间的最小百分比                               |
| `-XX:MaxHeapFreeRatio`=max | 垃圾回收后可用空间的最大百分比                               |
| `-XX:InitialRAMPercentage` | 按照 RAM 的百分比设置 heap 的初始大小，如果同时指定 `-Xms`，JVM 会忽略该设置 |
| `-XX:MinRAMPercentage`     |                                                              |
| `-XX:NewRatio`=ratio       |                                                              |
| `-XX:+AggressiveHeap`      |                                                              |

`-Xms` 和 `-Xmx` 定义 heap-size 的最小和最大值，如果 `-Xms` 和 `-Xmx` 的参数值相等，则 heap-size 大小固定，这样设置的优点是在 GC 后不需要重新计算 heap 大小而浪费资源。

JVM 会在每次垃圾回收（GC）时增大或缩小 heap-size，将可用空间与存活对象的比例保持在一定范围，该范围由 `-XX:MinHeapFreeRatio=` 和 `-XX:MaxHeapFreeRatio=` 按百分比设置。设置合适的空闲空间比例可以避免不必要的空闲空间的扩大或缩小，在不显著影响性能的前提下释放未使用的内存。总大小由 `-Xms` 和 `-Xmx` 控制。

例如，设置 `-XX:MinHeapFreeRatio=40` 和 `-XX:MaxHeapFreeRatio=70`，那么当可用空间的百分比低于 40% 时，新生代将扩大，当可用空间百分比超过 70% 时，新生代将缩小。

`-XX:InitialRAMPercentage` 按百分比设置初始 heap 大小，例如：

```sh
-XX:InitialRAMPercentage=50.0
```

对 RAM 为 1GB 的服务器，对应初始 heap-size 约为 500 MB。

> [!IMPORTANT]
>
> 当同时设置 `-Xms` 和 `-XX:InitialRAMPercentage`，JVM 会忽略 `-XX:InitialRAMPercentage` 设置。

`-XX:MinRAMPercentage` 和 `-XX:MaxRAMPercentage` 都是这是 heap 的**最大内存大小**。

- 当物理机内存小于 250 MB，那么`-XX:MinRAMPercentage` 配置 heap 的最大内存，例如，假设物理机的内存为 100 MB，那么 `-XX:MinRAMPercentage=50` 表示最大 heap 大小为 50 MB
- 当物理机内存大于 250 MB，那么 `-XX:MaxRAMPercentage` 配置 heap 的最大内存

**示例**：较小的宿主机，内存 100 MB，MaxRAMPercentage 设置为 25， MinRAMPercentage 设置为 50。

由于 100 MB 小于 250 MB，所以 MinRAMPercentage 生效。此时最大 heap-size 为 100 MB 的 50%，约 50 MB。

**示例**：较大宿主机，内存 1GB，MaxRAMPercentage 设置为 25， MinRAMPercentage 设置为 50，此时最大 heap-size 为 1GB 的 25%，即约为 250 MB。

> [!IMPORTANT]
>
> 如果设置 `-Xmx`，MinRAMPercentage  和 MaxRAMPercentage  设置无效。

### 非 heap 区设置

| 命令              | 功能               |
| ----------------- | ------------------ |
| `-XX:PermSize`    | 非堆区初始内存大小 |
| `-XX:MaxPermSize` | 非堆区内存上限     |

## 运行命令

- **运行 jar**

```sh
java -jar xxx.jar
```

- **指定主类**

如果 jar 里没有 manifest，则需要指定主类，语法：

```sh
--class-path classpath
-classpath classpath
-cp classpath
```

指定搜索类文件位置，`classpath` 是由分号（;）分隔的目录，JAR文件和ZIP文件列表。

例如指定主类运行：

```cmd
java -cp xxx.jar xxx.com.mainClass
```

这里 `-cp xxx.jar` 表示把 xxx.jar 加入到 classpath，这样 class loader 就会在其中查找匹配的类。



## 参考

- [Oracle docs](https://docs.oracle.com/javase/10/tools/java.htm#JSWOR624)
- https://help.aliyun.com/zh/sae/serverless-app-engine-classic/use-cases/best-practices-for-jvm-heap-size-configuration
- https://docs.oracle.com/en/java/javase/21/docs/specs/man/java.html
