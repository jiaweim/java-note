# java.util.regex.Matcher

常量	等价的表达式
Pattern.CANON_EQ	None
Pattern.CASE_INSENSITIVE	(?i)
Pattern.COMMENTS	(?x)
Pattern.MULTILINE	(?m)
Pattern.DOTALL	(?s)
Pattern.LITERAL	None
Pattern.UNICODE_CASE	(?u)
Pattern.UNIX_LINES	(?d)


## 查找

|方法|说明|
|---|---|
|`boolean lookingAt()`|匹配字符串的前面，不要求完整匹配|
|`boolean find()`|尝试查找下一个匹配的子字符串，适合在循环中使用|
|`find(int start)`|重置 `Matcher`，并从指定 `index` 开始尝试匹配下一个子字符串|
|`matches()`|尝试匹配整个字符串|

### 遍历所有匹配



## 替换

方法	说明
Matcher appendReplacement(StringBuffer sb, String replacement)	实现non-terminal append-and-replace步骤。
StringBuffer appendTail(StringBuffer sb)	实现terminal append-and-replace操作。
String replaceAll(String replacement)	替换所有匹配的子字符串
String replaceFirst(String replacement)	替换匹配的第一个子字符串
static String quoteReplacement(String s)	
