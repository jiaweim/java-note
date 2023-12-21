# 格式化和解析

## 简介

`DateTimeFormatter` 提供了三种格式化器用来打印日期和时间：

- 预定义标准格式

|Formatter|说明|示例|
|---|---|---|
|`BASIC_ISO_DATE`|Year, month, day, zone offset 不带分隔符|19690716-0500|
|`ISO_LOCAL_DATE`, <br>`ISO_LOCAL_TIME`, <br>`ISO_LOCAL_DATE_TIME`|分隔符 -, :, T|1969-07-16, <br> 09:32:00, <br>1969-07-16T09:32:00|
|ISO_OFFSET_DATE, <br>ISO_OFFSET_TIME, <br>ISO_OFFSET_DATE_TIME|类似 ISO_LOCAL_XXX, 但包含 zone offset|

1969-07-16-05:00, 
09:32:00-05:00, 
1969-07-
16T09:32:00-
05:00

ISO_ZONED_D
ATE_TIME

With zone offset and zone 
ID

1969-07-
16T09:32:00-
05:00[America/Ne
w_York]

ISO_INSTANT

In UTC, denoted by the Z 
zone ID

1969-07-
16T14:32:00Z

ISO_DATE, 
ISO_TIME, 
ISO_DATE_TI
ME

Like 
ISO_OFFSET_DATE, 
ISO_OFFSET_TIME, 
and 
ISO_ZONED_DATE_TI
ME, but the zone 
information is optional

1969-07-16-05:00, 
09:32:00-05:00, 
1969-07-
16T09:32:00-
05:00[America/Ne
w_York]

ISO_ORDINAL
_DATE

The year and day of year, 
for LocalDate

1969-197

ISO_WEEK_DA
TE

The year, week, and day of 
week, for LocalDate

1969-W29-3
RFC_1123_DA
TE_TIME

The standard for email 
timestamps, codified in 
RFC 822 and updated to 
four digits for the year in 
RFC 1123

Wed, 16 Jul 1969 
09:32:00 -0500
