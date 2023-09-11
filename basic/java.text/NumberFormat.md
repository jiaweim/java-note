# NumberFormat

## 简介

`java.text.NumberFormat` 是所有数字格式化相关类的基类，用于将数字转换为格式化字符串。`NumberFormat` 还提供了一些用来确定不同地区数字格式和名称的方法。`NumberFormat` 可用于格式化和解析任何地区的数字。

## 使用

- 若要格式化当前 `Locale` 的数字，可使用工厂方法

```java
String myString = NumberFormat.getInstance().format(myNumber);
```

- 如果要格式化多个数字，那么先获取 `NumberFormat` 再多次调用更高效，这样系统就不必多次获取 Locale 信息

```java
NumberFormat nf = NumberFormat.getInstance();
for (int i = 0; i < myNumber.length; ++i) {
    output.println(nf.format(myNumber[i]) + "; ");
}
```

- 若要格式化不同 Locale 下的数字，可在调用 `getInstance` 时指定 Locale

```java
NumberFormat nf = NumberFormat.getInstance(Locale.CHINA); 
```

- 还可以使用 NumberFormat 解析数字

```java
 myNumber = nf.parse(myString);
```

## 方法

|方法|说明|
|---|---|
|getInstance|格式化数字|
|getNumberInstance|格式化数字|
|getIntegerInstance|格式化整数|
|getCurrencyInstance|格式化货币数字|
|getPercentInstance|百分比格式|
|setMinimumFractionDigits|小数点|	
|setParseIntegerOnly|配置解析|
|setDecimalSeparatorAlwaysShown |配置格式化|


`setParseIntegerOnly` 配置解析：

- 如果为 true，则 "3456.78" -> 3456（并保留索引 6 后面的解析位置）
- 如果为 false，则 "3456.78" -> 3456.78（并保留索引 8 后面的解析位置）

此方法与格式化无关。如果希望在小数点后面没有数值的情况下不显示小数点，则使用 `setDecimalSeparatorAlwaysShown`。 

`setDecimalSeparatorAlwaysShown` 只影响小数点后没有数值的情况，例如模式 "#,##0.##"，如果为 true，则 3456.00 -> "3,456."，如果为 false，则 3456.00 -> "3456"。此方法与解析无关。如果希望解析在小数点处停止，则使用 setParseIntegerOnly

要更细致的控制数值的格式，直接使用 `DecimalFormat`、


## 参考

- https://docs.oracle.com/javase/8/docs/api/java/text/NumberFormat.html