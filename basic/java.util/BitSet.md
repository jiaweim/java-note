# BitSet

## 简介

构造一个 bit 数组，设 bit-vector 的第一个位置对应数字 1，第二个位置对应数字 2，第 N 个位置对应数字 N。在读取一个数字时，将该数字对应位置的值设置为 1，这样可以节省内存。主要用于海量数据分析。

该方案也有缺点，必然如果读入 2 个数据，一个是 1，一个是 100,0000，那个 bit-vector 的长度取决于最大数字。所以 bit-vector 适用于数据长度均衡的情况。

bit-vector 实现函数：

- Set(index)：对于读入的数据，将对应的bit位置为1
- Clear(index)：对于删除的数据，将对应的bit位置为0
- Get(index)：读取某一位数据，结果是1则证明存在，为0则证明不存在
- Size()：返回容器的长度。
- Count(置位元素个数)：返回所有被置为1的位的个数

bit-vector 在 java 中有一个对应实现 `BitSet`。

例如，包含 1024 个元素的 `boolean[]` 数组：

```java
boolean[] bits = new boolean[1024];
System.out.println(ClassLayout.parseInstance(bits).toPrintable());
```

相对 bit 数组，`boolean[]` 数组消耗 8 倍的内存占用。

