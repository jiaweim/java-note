# DecimalFormat

## 简介

`DecimalFormat` 是 `NumberFormat` 的子类，用于格式化十进制数。该类可用于格式化及解析任意 `Locale` 中的数。且支持不同类型，包括整数(123)、fixed-point 数(123.4)、科学计数(1.23E4)、百分数(12%)、金额($123)。所有这些内容都可以本地化。

要获得具体语言环境的 `NumberFormat` (包括默认 Locale)，可调用 `NumberFormat` 的工厂方法，如 `getInstance()`。通常不直接调用 `DecimalFormat` 的构造函数，因为 `NumberFormat` `的工厂方法返回的NumberFormat` 的子类可能不是 `DecimalFormat`。如果需要自定义格式对象，可执行：

```java
NumberFormat format = NumberFormat.getInstance(loc);
if(format instanceof DecimalFormat){
	((DecimalFormat)format).setDecimalSeparatorAlwaysShown(true);
}
```

`DecimalFormat` 包含模式和一组符号。可直接使用 `applyPattern()` 或间接使用 API 来设置模式。符号存储在 `DecimalFormatSymbols` 对象中。使用 `NumberFormat` 工厂方法可以从本地化的 `ResourceBundles` 中获取pattern 和 symbols。

## Pattern 语法

|项目|语法|
|---|---|
|Pattern|subpattern[;subpattern]|
subpattern	[Prefix] Number [Suffix]
Prefix	任意 Unicode 字符 (\uFFFE, \uFFFF 除外)，作为前缀直接显示
Number	 Integer [Exponent]
	 Integer . Fraction [Exponent]
Integer	MinimumInteger
	#
	# Integer
	# , Integer
MinimumInteger	 0
	 0 MinimumInteger
	 0 , MinimumInteger
Fraction	[MinimumFraction] [OptionalFraction]
MinimumFraction	0 [MinimumFraction]
OptionalFraction	# [OptionalFraction]
Exponent	 E MinimumExponent
MinimumExponent	0 [MinimumExponent]


## 示例
