# Apache Commons Codec

2024-09-30
@author Jiawei Mao
***

## 简介

Apache Commons Codec 提供常用编码器和解码器的实现，包括 Base64、Hex、Phonetic 和 URLs 等。

## Binary Encoders

| Encoders          | 说明                             |
| ----------------- | -------------------------------- |
| Base32            | Base32 编码和解码                |
| Base32InputStream | Base32 编码和解码（流式）        |
| Base64            | Base64 编码和解码                |
| Base64InputStream | Base64 编码和解码（流式）        |
| BinaryCodec       | 字节数组和 "0", "1" 字符串的转换 |
| Hex               | 十六进制字符串转换               |

### Base64

`Base64` 类提供 Base64 编码和解码。

构造函数参数：

- URL-safe mode: 默认关闭
- Line length: 默认 76
- Line separator: 默认 "\r\n"

URL-safe 参数应用于编码操作。解码自动处理两种模式。

由于该类直接操作字节流，而不是字符流，因此它是硬编码的，仅编码/解码小写 127  ASCII （ISO-8859-1. Windows-1252, UTF-8 等）。

该类线程安全。

可以通过 Base64.Builder 配置该类实例：

```java
 Base64 base64 = Base64.builder()
   .setDecodingPolicy(DecodingPolicy.LENIENT) // 默认为 lenient, null resets to default
   .setEncodeTable(customEncodeTable)         // 默认为 built in, null resets to default
   .setLineLength(0)                          // 默认 none
   .setLineSeparator('\r', '\n')              // 默认 CR LF, null resets to default
   .setPadding('=')                           // 默认 =
   .setUrlSafe(false)                         // 默认 false
   .get()
```

## Digest Encoders

> [!WARNING]
>
> 下面有些函数不适合加密，或者加密不安全。

| Encoders | 说明 |
| -------- | ---- |
| Blake3   |      |
|          |      |
|          |      |



## 参考

- https://commons.apache.org/proper/commons-codec/