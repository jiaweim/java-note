# 数据处理

## Column

tablesaw 为处理表格而设计，而表格由 column 组成。我们经常需要处理单个 column，因此 tablesaw 提供了许多相关工具。

column 就是包含数据的命名向量。其中可能有缺失值，缺失值处理是数据分析很重要的一部分。

支持的 column 类型包括：

- `BooleanColumn`
- `StringColumn`，用于 Categorical 类型
- `TextColumn`，单纯字符串
- `NumberColumn`，数值类型接口
  - `ShortColumn`, 用于较小的整数值
  - ` IntColumn`，整数
  - `LongColumn`，较大整数
  - `FloatColumn`，单精度浮点数
  - `DoubleColumn`，双精度浮点数
- `DateColumn`，本地日期，即不包含时区的日期，例如  April 10, 2018
- `DateTimeColumn`，本地