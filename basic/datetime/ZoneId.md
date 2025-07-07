# ZoneId

2025-07-07
@author Jiawei Mao
***
## 简介

`ZoneId` 是 Java 8 引入的时区类，取代以前的 `TimeZone`，它有两个子类：

- `ZoneRegion`
- `ZoneOffset`

`ZoneId` 用于表示 `Instant` 和 `LocalDateTime` 之间转换的规则。 ID 有两种类型：

- 固定 offset - 相对 UTC/Greenwich 的固定 offset，所有本地 date-time 都使用相同的 offset
- 地理区域 - 按特定规则查找与 UTC/Greenwich 的 offset 的区域

大多数固定 offset 使用 `ZoneOffset` 类表示。对任意 `ZoneId` 对象调用 `normalized()` 方法，都会将 offset 转换为 `ZoneOffset` 类型。

`ZoneRules` 类描述 offset 变化规则。该类提供从 ID 到规则的映射，之所以采用该方法，是因为规则由政府定义，并且经常变动，但是 ID 是稳定的。

该设计还有其他影响，例如，序列化 `ZoneId` 会只保存 id，但是序列化规则会保存整个数据集。同样，比较两个 `ZoneId` 只比较 DI，而比较两个规则则比较整个数据集。

### Time-zone IDs

ID 在系统中是唯一的，有三种类型的 ID。

1. 最简单的 ID 类型是基于 `ZoneOffset` 的 ID。它由 ‘Z’ 以及 '+' 或 '-' 开头
2. 第二种 ID 带有前缀 ID。例如 "GMT+2" 或  "UTF+01:00"。可识别的前缀包括 "UTC", "GMT" 和 "UT"。offset 作为后缀，在创建会被规范化。这类 ID 在调用 `normalized()` 后可以转换为 `ZoneOffset`
3. 第三种 ID 基于区域。基于区域的 ID至少包含两个字符，并且不能以 "UTC", "GMT", "UT", "+" 和 "-" 开头。基于区域的 ID 由配置定义，具体参考 `ZoneRulesProvider`。配置主要提供从 ID 到底层 `ZoneRules` 的映射。

时区规则由政府指定，并且经常变化。由许多组织监控时区变化并进行整理。Java 默认采用 IANA 时区数据库（TZDB）。其它相关组织还有 IATA 和微软。

每个组织都有各自的区域 ID 格式。TZDB 采用 'Europe/London' 和 'America/New_York' 这类 ID。TZDB ID 优先于其它组织的 ID 格式。

对 TZDB 以外的组织提供的 ID，建议包含组织名称，以避免冲突。例如，IATA 航空公司时区区域 ID 通常与三个字母的机场代码相同。但是，乌得勒支机场的代码是 "UTC"，这就存在冲突。因此，TZDB 以外的区域 ID 建议格式为 "group~region"。因此，如果定义了 IATA 数据，则乌得勒支机场的 ID 为 "IATA~UTC"。

### 序列化

`ZoneId` 类可以序列化。`ZoneOffset` 子类使用专门格式存储与 UTC/Greenwich 的 offset。

对 ID 未知的 jre，`ZoneId` 也可以被反序列化。例如，如果服务器端的 jre 已使用新的区域 `ID` 更新，但客户端 jre 尚未更新，此时 `ZoneId` 可以反序列化，并且可以使用 `getId`, `equals`, `hashCode`, `toString`, `getDisplayName` 和 `normalized` 进行查询，但是针对 `getRules` 的调用会抛出 `ZoneRulesException`。该方法旨在允许在时区信息不完整的 jre 中加载和查询 `ZonedDateTime`，但不能修改。 

`ZoneId` 是一个基于值的类，应避免使用相等性 `==` 操作，应使用 `equals()` 进行比较。

## ZoneOffset

该类定义相对 Greenwich/UTC 的时区 offset，例如 "+02:00"。

**时区 offset**: 某个时区相对 Greenwich/UTC 时区之间的时间差。通常为固定的时间差。

世界各地有不同的时区 offset。`ZoneId` 类中定义了 offset 随地点和时间变化的规则。

例如，巴黎冬季比 Greenwich/UTC 早 1h，夏季比 Greenwich/UTC 早 2h。巴黎的 `ZoneId` 实例将引用两个 `ZoneOffset` 实例，一个为冬季的 "+01:00"，一个为夏季的 "+02:00"。

2008 年，世界各地的时区 offset 从 “-12:00” 扩展到 "+14:00"。为了防止该扩展导致的问题，同样提供验证功能，offset 范围限制为 `-18:00` 到 `18:00`。

该类专门为与 ISO 日历系统配合使用而设计。小时、分钟和秒字段与标准 ISO 定义一致。

## 示例

1. 获取系统时区

```java
ZoneId id1 = ZoneId.systemDefault();
System.out.println(id1);

ZoneId id2 = TimeZone.getDefault().toZoneId();
assertEquals(id1, id2);
// Asia/Shanghai
```

2. 获取柏林时区（基于区域的 ID）

```java
ZoneId zoneId = ZoneId.of("Europe/Berlin");
```

## ZonedDateTime

`ZonedDateTime` 表示 ISO-8601 日历系统中带时区的 immutable 日期时间。`ZonedDateTime` 包含三个对象的信息：`LocalDateTime`, `ZoneId` 和 `ZoneOffset`。



## 参考

- https://docs.oracle.com/javase/8/docs/api/java/time/ZoneId.html