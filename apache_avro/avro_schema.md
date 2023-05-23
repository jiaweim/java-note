# Avro Schema 规范

- [Avro Schema 规范](#avro-schema-规范)
  - [Schema 声明](#schema-声明)
  - [基本类型](#基本类型)
  - [复杂类型](#复杂类型)
    - [Unions](#unions)
  - [参考](#参考)

***

## Schema 声明

Schema 在 JSON 中表示为：

- JSON string，命名已定义类型
- JSON object，形式如下

```json
{"type": "typeName" ...attributes...}
```

其中 `typeName` 为基础类型或派生类型。Schema 中未定义的属性允许作为 metadata，但不能影响序列化数据的格式。

## 基本类型

基本类型包括：

- null: no value
- boolean: a binary value
- int: 32-bit signed integer
- long: 64-bit signed integer
- float: single precision (32-bit) IEEE 754 floating-point number
- double: double precision (64-bit) IEEE 754 floating-point number
- bytes: sequence of 8-bit unsigned bytes
- string: unicode character sequence

基本类型没有指定属性。

## 复杂类型

Avro 支持 6 种复杂类型： records, enums, arrays, maps, unions and fixed。

### Unions

Unions 用 JSON 数组表示。例如，`["null", "string"]` 定义了一个 schema，可以是 null 或 string。




## 参考

- [Specification](https://avro.apache.org/docs/1.11.1/specification/)
