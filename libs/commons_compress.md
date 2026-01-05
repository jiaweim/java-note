# Apache Commons Compress

## 简介

Apache Commons Compress 库支持 ar, cpio, Unix dump, tar, zip, gzip, XZ, Pack200, bzip2, 7z, arj, LZMA, snappy, DEFLATE, lz4, Brotli, Zstandard, DEFLATE64 和 Z 文件.

各个组件代码来源：

- bzip2, tar 和 zip 来自 Avalon 的 Excalibur，但对 Apache 而言来自 Ant。tar package 最初来自 Tim Endres 的 public domain package. bzip2 package 基于 Keiron Liddle 和 Julian Seward 的 libbzip2，迁移过程：Ant -> Avalon-Excalibur -> Commons-IO -> Commons-Compress
- cpio package 由 Michael Kuss 和 jRPM 项目贡献
- pack200 代码来自已退役项目 Apache Harmony

## 归档和压缩

Commons Compress 将压缩单个数据流的所有格式称为**压缩格式**；而将多个文件收集到一个文件的格式称为**归档格式**。

- 支持的压缩格式包括： gzip, bzip2, XZ, LZMA, Pack200, DEFLATE, Brotli, DEFLATE64, ZStandard 和 Z
- 支持的归档格式包括：7z, ar, arj, cpio, dump, tar 和 zip
- Pack200 是一个特例，它只能压缩 JAR 文件

对  arj, dump, Brotli, DEFLATE64 和 Z，commons-compress 目前只支持 read 功能。

arj 格式只支持读取未压缩的归档格式；对 7z 支持读取多种压缩和加密算法的压缩包，但输出归档格式时不支持加密。

## 参考

- https://commons.apache.org/proper/commons-compress/
