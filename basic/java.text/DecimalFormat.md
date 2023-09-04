# DecimalFormat

## 简介

`DecimalFormat` 是 `NumberFormat` 的子类，用于格式化十进制数。该类可用于格式化及解析任意 `Locale` 中的数。且支持不同类型，包括整数(123)、fixed-point 数(123.4)、科学计数(1.23E4)、百分数(12%)、金额($123)。所有这些内容都可以本地化。

要获得具体语言环境的 `NumberFormat` (包括默认 Locale)，可调用 `NumberFormat` 的工厂方法，如 `getInstance()`。通常不直接调用 `DecimalFormat` 的构造函数，因为 `NumberFormat` 的工厂方法返回的 `NumberFormat` 的子类可能不是 `DecimalFormat`。如果需要自定义格式对象，可执行：

```java
NumberFormat format = NumberFormat.getInstance(loc);
if(format instanceof DecimalFormat){
	((DecimalFormat)format).setDecimalSeparatorAlwaysShown(true);
}
```

`DecimalFormat` 包含模式（pattern）和一组符号。pattern 可使用 `applyPattern()` 直接设置或使用 AP 方法间接设置。符号存储在 `DecimalFormatSymbols` 对象中。当使用 `NumberFormat` 工厂方法，会从本地化的 `ResourceBundles` 中获取 pattern 和 symbols。

## Pattern 语法

`DecimalFormat` 的语法如下：

|内容|说明|
|---|---|
|Pattern|PositivePattern [; NegativePattern]|
|PositivePattern|[Prefix] Number [Suffix]|
|NegativePattern|[Prefix] Number [Suffix]|
|Prefix|除 \uFFFE, \uFFFF 和特殊字符外的任意 Unicode 字符，直接显示|
|Suffix|除 \uFFFE, \uFFFF 和特殊字符外的任何 Unicode 字符|
|Number|- Integer [Exponent]<br>- Integer . Fraction [Exponent]|
|Integer|- MinimumInteger<br>- `#`<br>- `# Integer`<br>- `# , Integer`|
|MinimumInteger|- 0<br>- 0 MinimumInteger<br>- 0 , MinimumInteger|
|Fraction|[MinimumFraction] [OptionalFraction]|
|MinimumFraction|0 [MinimumFraction]|
|OptionalFraction|`# [OptionalFraction]`|
Exponent|E MinimumExponent|
|MinimumExponent|0 [MinimumExponent]|

!!! warning
	`[]` 表示可选。

`DecimalFormat` 模式说明

- `DecimalFormat` 模式包含正数和负数子模式，例如 "#,##0.00; (#,##0.00)"
- 每个子模式有前缀、数字和后缀三部分：`[Prefix] Number [Suffix]`
- 负数子模式可选；如果不存在，则在正数模式前加减号前缀作为负数模式( '-' U+002D HYPHEN-MINUS)，所以 "0.00" 等价于"0.00;-0.00"
- 如果显式指定负数模式，则它仅用于指定负数的前缀和后缀，其它内容如数字位数、最小位数等都与正数模式相同，所以 `"#,##0.0#;(#)"` 与 `"#,##0.0#;(#,##0.0#)"` 等价
- 用于无穷大、数字、千位分隔符、小数分隔符等的前缀、后缀和各种符号可设置为任意值，并且能在格式化期间正确显示。但是，注意不要让符号和字符串发生冲突，否则解析是不可靠的。例如，为了让 `DecimalFormat.parse()` 能够区分正数和负数，正数和负数前缀或后缀必须是不同的，如果它们相同，则 `DecimalFormat` 的行为就如同未指定负数模式一样。另一个示例是小数分隔符和千位分隔符应该是不同的字符，否则无法解析。 
- group 分隔符通常用于千位，但是在某些国家/地区中它用于分隔万位。分组大小是分组字符之间的固定数字位数，例如 100,000,000 是 3，而 1,0000,0000 则是 4。如果使用具有多个分组字符的模式，则最后一个分隔符和整数结尾之间的间隔才是使用的分组大小。所以 "#,##,###,####" == "######,####" == "##,####,####"。 

### 特殊模式字符

模式中的大多字符按字面解释；在解析期间对其进行匹配，在格式化期间不经改变直接输出。而特殊字符则代表了其它字符、字符串或字符类。如果要将其作为字面量出现在前缀或后缀中，那么除非另行说明，否则必须对其加引号。 

下表列出的字符用在非本地化的模式中。已本地化的模式使用 `DecimalFormatSymbols` 中的相应字符，这些字符已失去其特殊状态。例外的是货币符号和引号，它们不被本地化。 

|符号|位置|本地化？|含义|
|---|---|---|---|  
|0|Number|Yes|数字，不足以 0 补齐|
|#|Number|Yes|数字，不足则不显示|
|.|Number|Yes|小数分隔符，或货币小数分隔符|
|-|Number|Yes|减号|
|,|Number|Yes|group 分隔符|  
|E|Number|Yes|分隔科学计数法中的尾数和指数。在前缀或后缀中无需加引号|
|;|分隔子模式|Yes|分隔正数和负数子模式|
|%|前缀或后缀|Yes|乘以 100 并显示为百分数|
|`&#92;u2030`|前缀或后缀|Yes|乘以 1000 并显示为千分数 |
|`&#164; ( &#92;u00A4)`|前缀或后缀|No|货币记号，由货币符号替换。连续两个则用国际货币符号替换。如果出现在某个模式中，则使用货币 group 分隔符，而非常规 group 分隔符|
|'|前缀或后缀|No|用于在前缀或或后缀中为特殊字符加引号，例如 "'#'#" 将 123 格式化为 "#123"。要创建单引号本身，请连续使用两个单引号："# o''clock"|  

### 科学记数

科学计数法中的数表示为一个尾数和一个 10 的幂的乘积，例如 1234 可表示为 1.234 x 10^3。尾数的范围通常是 1.0 <= x < 10.0，但非必需。

目前只能通过 pattern 设置 `DecimalFormat` 格式化和解析科学记数，目前没有创建科学记数的工厂方法。

- 小数点后的 `#` 数目，指定小数点的位数
- E 后面的数，指定最小的指数位数，没有最大位数
- 使用本地减号来格式化负指数，不使用模式中的前缀和后缀。这就允许存在诸如 `"0.###E0 m/s"` 等此类的模式
- 整数位数
  - 如果整数最大 digit 数大于其最小 digit 数且大于 1，则强制指数为整数最大 digit 的倍数，且最小整数 digits 为 1；常用于工程表示，其中指数是 3 的倍数。例如 "##0.#####E0" 格式化 12345 为 "12.345E3，格式化 123456 为 "123.456E3"；这里整数最大 digit 数为 3，所以指数必须为 3 的倍数
  - 否则，通过调整指数得到最小整数 digit 数。例如，"00.###E0 格式化 0.00123 为  "12.3E-4"；这里整数 digit 数为 2，通过调整指数实现
- 尾数中的有效位数是最小整数和最大小数位数的和，不受最大整数位数的影响。例如，使用 "##0.##E0" 格式化 12345 得到 "12.3E3"。要显示所有位数，请将有效位数计数设置为零。有效位数不会影响解析
- 指数模式可能不包含分组分隔符。 
 
### Rounding

`DecimalFormat` 提供 `RoundingMode` 中定义的舍入模式进行格式化。默认情况下，它使用 `RoundingMode.HALF_EVEN`。 

### Digits

对于格式化，`DecimalFormat` 使用 `DecimalFormatSymbols` 中定义的本地化的 0-9 这 10 个连续字符作为 digits。对于解析，可识别 `Character.digit` 所定义的digits 和 Unicode 定义的十进制 digits。 

### 特殊值

NaN 被格式化为一个字符串，通常具有单个字符 `\uFFFD`。此字符串由 `DecimalFormatSymbols` 对象所确定。这是唯一不使用前缀和后缀的值。 

Infinity 被格式化为一个字符串，通常具有单个字符 `\u221E`，具有正数或负数的前缀和后缀。infinity 字符串由 `DecimalFormatSymbols` 对象所确定。 

将负零（"-0"）解析为 

- 如果 isParseBigDecimal() 为 true，则为 BigDecimal(0)， 
- 如果 isParseBigDecimal() 为 false 并且 isParseIntegerOnly() 为 true，则为 Long(0)
- 如果 isParseBigDecimal() 和 isParseIntegerOnly() 均为 false，则为 Double(-0.0)。 


## 示例

|值|pattern|输出|
|---|---|---|
|123456.789|###,###.###|123,456.789|
|123456.789|###.##|123456.79|
|123.78|000000.000|000123.780|
|12345.67|$###,###.###|$12,345.67|
|12345.67|\u00A5###,###.###|￥12,345.67|
|1231423.3823|#.##|1231423.38|
|	|0000000000.000000|0001231423.382300|
|	|-#,###.##|-1,231,423.382|
|	|0.00KG|1231423.38KG|
|	|#000.00KG|1231423.38KG|
|	|0.00%|123142338.23%|
|	|#.##E000|1.23E006|
|1234|0.###E0|1.234E3|
|12345|##0.#####E0|12.345E3|
