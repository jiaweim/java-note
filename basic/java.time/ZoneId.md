# ZoneId

## 简介

`ZoneId` 是 Java8 的新时区类。

`ZoneId` 指定 `Instant` 和 `LocalDateTime` 之间转换的规则，实际规则由 `ZoneRules` 定义。`ZoneId` 只是获取规则的 ID。之所以这么设计，是因为规则经常改变，但是 ID 很稳定。

有两种类型的 ID：

- 固定偏差：相对 UTC/Greenwich 的具体精确偏移量
- 地理位置：用于定位 UTC/Greenwich 偏移量的规则

