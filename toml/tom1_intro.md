# TOML 快速入门

## 宗旨

TOML 旨在成为一个语义明显且易于阅读的最小化配置文件格式。
TOML 被设计成可以无歧义地映射为哈希表。
TOML 应该能很容易地被解析成各种语言中的数据结构。

## 规格

- TOML 是大小写敏感的。
- TOML 文件必须是合法的 UTF-8 编码的 Unicode 文档。
- 空白是指制表符（0x09）或空格（0x20）。
- 换行是指 LF（0x0A）或 CRLF（0x0D 0x0A）。

## 注释

井字符将该行余下的部分标记为注释，除非它在字符串中。

```toml
# 这是一个全行注释
key = "value"  # 这是一个行末注释
another = "# 这不是一个注释"
```

除制表符以外的控制字符（U+0000 至 U+0008，U+000A 至 U+001F，U+007F）不允许出现在注释中。

## 键值对

TOML 文档最基本的构成区块是键值对。

- 键名在等号的左边而值在右边。
- 键名和键值周围的空白会被忽略。
- 键、等号和值必须在同一行（不过有些值可以跨多行）。

```toml
key = "value"
```

值必须是下述类型之一。

- 字符串
- 整数
- 浮点数
- 布尔值
- 坐标日期时刻
- 各地日期时刻
- 各地日期
- 各地时刻
- 数组
- 内联表

不指定值是非法的。

```toml
key = # 非法
```

键值对后必须换行（或结束文件）。
（例外见内联表）

```toml
first = "Tom" last = "Preston-Werner" # 非法
```

## 键名

键名可以是裸露的，引号引起来的，或点分隔的。

**裸键**只能包含 ASCII 字母，ASCII 数字，下划线和短横线（`A-Za-z0-9_-`）。
注意裸键允许仅由纯 ASCII 数字构成，例如 `1234`，但总是被解释为字符串。

```toml
key = "value"
bare_key = "value"
bare-key = "value"
1234 = "value"
```

引号键遵循与基本字符串或字面量字符串相同的规则并允许你使用更为广泛的键名。
除非明显必要，使用裸键方为最佳实践。

```toml
"127.0.0.1" = "value"
"character encoding" = "value"
"ʎǝʞ" = "value"
'key2' = "value"
'quoted "value"' = "value"
```

裸键不能为空，但空引号键是允许的（虽然不建议如此）。

```toml
= "no key name"  # 非法
"" = "blank"     # 合法但不鼓励
'' = 'blank'     # 合法但不鼓励
```

点分隔键是一系列通过点相连的裸键或引号键。
这允许了你将相近属性放在一起：

```toml
name = "Orange"
physical.color = "orange"
physical.shape = "round"
site."google.com" = true
```

等价于 JSON 的如下结构：

```json
{
  "name": "Orange",
  "physical": {
    "color": "orange",
    "shape": "round"
  },
  "site": {
    "google.com": true
  }
}
```

有关点分隔键定义表的详细信息，请参阅后文表一节。

点分隔符周围的空白会被忽略。
不过，最佳实践是不要使用任何不必要的空白。

```toml
fruit.name = "banana"     # 这是最佳实践
fruit. color = "yellow"    # 等同于 fruit.color
fruit . flavor = "banana"   # 等同于 fruit.flavor
```

缩进被作为空白对待而被忽略。

多次定义同一个键是非法的。

```toml
# 不要这样做
name = "Tom"
name = "Pradyun"
```

注意裸键和引号键是等价的：

```toml
# 这是不可行的
spelling = "favorite"
"spelling" = "favourite"
```

只要一个键还没有被直接定义过，你就仍可以对它和它下属的键名赋值。

```toml
# 这使“fruit”键作为表存在。
fruit.apple.smooth = true

# 所以接下来你可以像中这样对“fruit”表添加内容：
fruit.orange = 2
```

```toml
# 以下是非法的

# 这将 fruit.apple 的值定义为一个整数。
fruit.apple = 1

# 但接下来这将 fruit.apple 像表一样对待了。
# 整数不能变成表。
fruit.apple.smooth = true
```

不鼓励跳跃式地定义点分隔键。

```toml
# 合法但不鼓励

apple.type = "水果"
orange.type = "水果"

apple.skin = "薄"
orange.skin = "厚"

apple.color = "红"
orange.color = "橙"
```

```toml
# 建议

apple.type = "水果"
apple.skin = "薄"
apple.color = "红"

orange.type = "水果"
orange.skin = "厚"
orange.color = "红
```

由于裸键可以仅由 ASCII 整数组成，所以可能写出看起来像浮点数、但实际上是两部分的点分隔键。

除非你有充分的理由（基本不太会），否则不要这样做。

```toml
3.14159 = "派"
```

上面的 TOML 对应以下 JSON。

```json
{ "3": { "14159": "派" } }
```

## 字符串

## 数组

数组是内含值的方括号。

- 空白会被忽略。
- 子元素由逗号分隔。
- 数组可以包含与键值对所允许的相同数据类型的值。
- 可以混合不同类型的值。

```toml
integers = [ 1, 2, 3 ]
colors = [ "红", "黄", "绿" ]
nested_array_of_ints = [ [ 1, 2 ], [3, 4, 5] ]
nested_mixed_array = [ [ 1, 2 ], ["a", "b", "c"] ]
string_array = [ "所有的", '字符串', """是相同的""", '''类型''' ]

# 允许混合类型的数组
numbers = [ 0.1, 0.2, 0.5, 1, 2, 5 ]
contributors = [
  "Foo Bar <foo@example.com>",
  { name = "Baz Qux", email = "bazqux@example.com", url = "https://example.com/bazqux" }
]
```

- 数组可以跨行。
- 数组的最后一个值后面可以有终逗号（也称为尾逗号）。
- 值、逗号、结束括号前可以存在任意数量的换行和注释。
- 数组值和逗号之间的缩进被作为空白对待而被忽略。

```toml
integers2 = [
  1, 2, 3
]

integers3 = [
  1,
  2, # 这是可以的
]
```

## 表

表（也被称为哈希表或字典）是键值对的集合。

- 它们由表头定义，连同方括号作为单独的行出现。
- 看得出表头不同于数组，因为数组只有值

```toml
[table]
```

在它下方，直至下一个表头或文件结束，都是这个表的键值对。
表不保证保持键值对的指定顺序。

```toml
[table-1]
key1 = "some string"
key2 = 123

[table-2]
key1 = "another string"
key2 = 456
```

表名的规则与键名相同（见前文键名定义）。

```toml
[dog."tater.man"]
type.name = "pug"
```

等价于 JSON 的如下结构：

```json
{ "dog": { "tater.man": { "type": { "name": "pug" } } } }
```

键名周围的空格会被忽略。
然而，最佳实践还是不要有任何多余的空白。

```toml
[a.b.c]            # 这是最佳实践
[ d.e.f ]          # 等同于 [d.e.f]
[ g .  h  . i ]    # 等同于 [g.h.i]
[ j . "ʞ" . 'l' ]  # 等同于 [j."ʞ".'l']
```

缩进被作为空白对待而被忽略。

你不必层层完整地写出你不想写的所有途径的父表。
TOML 知道该怎么办。

```toml
# [x] 你
# [x.y] 不
# [x.y.z] 需要这些
[x.y.z.w] # 来让这生效

[x] # 后置父表定义是可以的
```

空表是允许的，只要里面没有键值对就行了。

类似于键名，你不能重复定义一个表。这样做是非法的。

```toml
# 不要这样做

[fruit]
apple = "红"

[fruit]
orange = "橙"
```

```toml
# 也不要这样做

[fruit]
apple = "红"

[fruit.apple]
texture = "光滑"
```

不鼓励无序地定义表。

```toml
# 有效但不鼓励
[fruit.apple]
[animal]
[fruit.orange]
```

```toml
# 推荐
[fruit.apple]
[fruit.orange]
[animal]
```

顶层表，又被称为根表，于文档开始处开始并在第一个表头（或文件结束处）前结束。
不同于其它表，它没有名字且无法后置。

```toml
# 顶层表开始。
name = "Fido"
breed = "pug"

# 顶层表结束。
[owner]
name = "Regina Dogman"
member_since = 1999-08-04
```

点分隔键为最后一个键名前的每个键名创建并定义一个表，倘若这些表尚未被创建的话。

```toml
fruit.apple.color = "red"
# 定义一个名为 fruit 的表
# 定义一个名为 fruit.apple 的表

fruit.apple.taste.sweet = true
# 定义一个名为 fruit.apple.taste 的表
# fruit 和 fruit.apple 已经创建过了
```




## 表数组

最后一个还没讲到的语法允许你写表数组。

这可以通过把表名写在双方括号里的表头来表示。

表头的第一例定义了这个数组及其首个表元素，而后续的每个则在该数组中创建并定义一个新的表元素。

这些表按出现顺序插入该数组。



## 文件扩展名

TOML 文件应当使用 `.toml` 扩展名。