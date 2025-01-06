# Data Stream

2024-12-21 ⭐
@author Jiawei Mao
***

## 简介

data stream 支持基础类型 `boolean`, `char`, `byte`, `short`, `int`, `long`, `float`, `double` 以及 `String` 的二进制 I/O。所有数据流实现 `java.io.DataInput` 或 `java.io.DataOutput` 接口。下面重点介绍这些接口使用最广泛的实现：`DataInputStream` 和 `DataOutputStream`。

`DataStreams` 示例通过写入一组数据，然后再次读入来演示数据流。每条数据由发票上的三个值组成：

| Order in record | Data type | Data description | Output Method                  | Input Method                 | Sample Value     |
| --------------- | --------- | ---------------- | ------------------------------ | ---------------------------- | ---------------- |
| 1               | `double`  | Item price       | `DataOutputStream.writeDouble` | `DataInputStream.readDouble` | `19.99`          |
| 2               | `int`     | Unit count       | `DataOutputStream.writeInt`    | `DataInputStream.readInt`    | `12`             |
| 3               | `String`  | Item description | `DataOutputStream.writeUTF`    | `DataInputStream.readUTF`    | `"Java T-Shirt"` |

下面解释一下 `DataStreams` 中的关键代码。首先，程序定义了一些常量，其中包含数据文件的名称和将写入的数据：

```java
static final String dataFile = "invoicedata";

static final double[] prices = { 19.99, 9.99, 15.99, 3.99, 4.99 };
static final int[] units = { 12, 8, 13, 29, 50 };
static final String[] descs = {
    "Java T-shirt",
    "Java Mug",
    "Duke Juggling Dolls",
    "Java Pin",
    "Java Key Chain"
};
```

`DataStreams` 打开一个输出流。由于 `DataOutputStream` 只能作为现有字节流对象的包装器创建，因此 `DataStreams` 提供了一个缓冲文件输出字节流。

```java
out = new DataOutputStream(new BufferedOutputStream(
              new FileOutputStream(dataFile)));
```

`DataStreams` 输出 records 并关闭输出流：

```java
for (int i = 0; i < prices.length; i ++) {
    out.writeDouble(prices[i]);
    out.writeInt(units[i]);
    out.writeUTF(descs[i]);
}
```

`writeUTF` 以 UTF-8 编码输出字符串。这是一种变宽字符编码，对常见的西文字符只需要一个字节。

然后，`DataStreams` 再次读回数据。首先，必须提供一个输入流。与 `DataOutputStream` 一样，`DataInputStream` 必须构造为字节流的包装器。

```java
in = new DataInputStream(new
            BufferedInputStream(new FileInputStream(dataFile)));

double price;
int unit;
String desc;
double total = 0.0;
```

然后就可以读取每个 record：

```java
try {
    while (true) {
        price = in.readDouble();
        unit = in.readInt();
        desc = in.readUTF();
        System.out.format("You ordered %d" + " units of %s at $%.2f%n",
            unit, desc, price);
        total += unit * price;
    }
} catch (EOFException e) {
}
```

这里，`DataStreams` 通过捕获 `EOFException` 来检测文件结束条件，而不是测试无效的返回值。`DataInput` 方法都使用 `EOFException` 而不是返回值。

还有注意，`DataStreams` 中每个专用写入都与对应的读取完全匹配。

另外，`DataStreams` 使用了一种非常糟糕的编程技术：它使用 `double` 表示货币值。一般来说，浮点数不适合表示精确值，尤其不适合表示小数，因为常用数，如 0.1，没有二进制表示。

货币值应使用 `java.math.BigDecimal`。遗憾的是，`BigDecimal` 是对象类型，因此不能用于对象流。不过，`BigDecimal` 可以用于对象流。

## DataStreams 示例

```java
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.EOFException;

public class DataStreams {
    static final String dataFile = "invoicedata";

    static final double[] prices = {19.99, 9.99, 15.99, 3.99, 4.99};
    static final int[] units = {12, 8, 13, 29, 50};
    static final String[] descs = {"Java T-shirt",
            "Java Mug",
            "Duke Juggling Dolls",
            "Java Pin",
            "Java Key Chain"};

    public static void main(String[] args) throws IOException {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new
                    BufferedOutputStream(new FileOutputStream(dataFile)));

            for (int i = 0; i < prices.length; i++) {
                out.writeDouble(prices[i]);
                out.writeInt(units[i]);
                out.writeUTF(descs[i]);
            }
        } finally {
            out.close();
        }

        DataInputStream in = null;
        double total = 0.0;
        try {
            in = new DataInputStream(new
                    BufferedInputStream(new FileInputStream(dataFile)));

            double price;
            int unit;
            String desc;

            try {
                while (true) {
                    price = in.readDouble();
                    unit = in.readInt();
                    desc = in.readUTF();
                    System.out.format("You ordered %d units of %s at $%.2f%n",
                            unit, desc, price);
                    total += unit * price;
                }
            } catch (EOFException e) {
            }
            System.out.format("For a TOTAL of: $%.2f%n", total);
        } finally {
            in.close();
        }
    }
}
```



## 参考

- https://docs.oracle.com/javase/tutorial/essential/io/datastreams.html