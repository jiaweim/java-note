# Java 数据类型

- [Java 数据类型](#java-数据类型)
  - [简介](#简介)
  - [简单数据类型](#简单数据类型)
    - [整数类型](#整数类型)
  - [内存优化](#内存优化)
  - [类型转换](#类型转换)
    - [float 转换 byte[]](#float-转换-byte)

2020-04-28, 09:56
***

## 简介

Java 是强类型语言，每个变量和表达式都有类型，每种类型都是严格定义的。

所有的数值传递，不管是直接还是通过方法调用经参数传过去的，都要先进行类型相容性检查。

## 简单数据类型

java 定义了 8 个简单数据类型：字节型（byte），短整型（short），整型（int），长整型（long），浮点型（float），双精度（double），布尔型（boolean）。

### 整数类型

java 定义了 4 种整数类型：byte, short, int, long。

整数类型的长度不应该被理解为它占用的存储空间，而应该是该类变量和表达式的行为。事实上，为了提高性能，至少字节型和短整型的存储是32位。

## 内存优化

降低程序的内存占用，同时让 CPU 等待的数据量降低，在降低内存占用的同时提供程序性能。

Java 对象除了数据本身，还有额外的元数据，例外数组对象的长度，用于表示对象类型的 `java.lang.Class`，这些元数据一般放在对象开头，称为对象头（_object header_）。每个对象至少占用 16 字节，其中 12 字节是 header。由于所有 Java 对象以8字节为基本单位，所以一个 int + byte 对象，占用的字节不是 17 (12+4+1)，而是24字节。

对非常大的或复杂的对象，header 的大小无关紧要。对小的对象，header 的大小就很显著了，例如：

```java
new byte[1];
```

对启用 `XX:+UseCompressedOops` 的32G 系统，每个 `Object` 引用占用 4 字节。否则 `Object` 引用占用 8 字节。

所有基本数据类型占用对应的字节大小：
|类型|内存占用 (byte)|
|---|---|
|byte, boolean|1|
|short, char|2|
|int, float|4|
|long, double|8|

数值的包装类型、数组和字符串的内存占用有所不同。

对数组类型，占用内存大小为 $12+len(array)\times len(element)$

## 类型转换

### float 转换 byte[]

在需要将 `float[]` 转换成内存数据，将 `float` 转换为 `byte[]` 有两种方式。

方法一，使用 `ByteBuffer`:

```java
import java.nio.ByteBuffer;
import java.util.ArrayList;

float buffer = 0f;
ByteBuffer bbuf = ByteBuffer.allocate(4);
bbuf.putFloat(buffer);
byte[] bBuffer = bbuf.array();
bBuffer=this.dataValueRollback(bBuffer);

//数值反传
private byte[] dataValueRollback(byte[] data) {
    ArrayList<Byte> al = new ArrayList<Byte>();
    for (int i = data.length - 1; i >= 0; i--) {
        al.add(data[i]);
    }
    byte[] buffer = new byte[al.size()];
    for (int i = 0; i <= buffer.length - 1; i++) {
        buffer[i] = al.get(i);
    }
    return buffer;
}
```

方法二，先用 `Float.floatToIntBits(f)` 转换成 `int`，再通过如下方法转换成 `byte[]`:

```java
/**
* 将int类型的数据转换为byte数组 原理：将int数据中的四个byte取出，分别存储
 *
 * @param n  int数据
* @return 生成的byte数组
 */
public static byte[] intToBytes2(int n) {
    byte[] b = new byte[4];
    for (int i = 0; i < 4; i++) {
        b[i] = (byte) (n >> (24 - i * 8));
    }
    return b;
}
/**
 * 将byte数组转换为int数据
 *
 * @param b 字节数组
 * @return 生成的int数据
 */
public static int byteToInt2(byte[] b) {
    return (((int) b[0]) << 24) + (((int) b[1]) << 16)
            + (((int) b[2]) << 8) + b[3];
}  
```
