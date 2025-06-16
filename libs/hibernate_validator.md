# Hibernate Validator

## 简介

数据验证是一项常见任务，贯穿所有应用层。通常，每一层都实现相同的验证逻辑，既耗时又容易出错。为了避免重复验证，开发人员通常将验证逻辑捆绑到 domain-model 中，导致 domain-model 中充斥着验证代码。

<img src="./images/application-layers.png" alt="application layers" style="zoom:67%;" />

Jakarta Validation 3.1.1 定义了用于实体和方法验证的 metadata-model 和 API。默认的 metadata 是 annotation，可以通过 XML 和覆盖和扩展 meta-data。该 API 不依赖于特定的应用层或编程模型，尤其不依赖于 Web 层和持久层，既适用于服务器应用开发人眼，也适用于 Swing 等 GUI 开发人员。

<img src="./images/application-layers2.png" alt="application layers2" style="zoom:67%;" />

Hibernate Validator 是 Jakarta Validation 的参考实现。

Hibernate Validator 9.0.1.Final 和Jakarta Validation 3.1.1 需要 Java 17+。

## 入门

添加依赖项：

```xml
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>9.0.1.Final</version>
</dependency>
```

## 声明和验证 bean 约束



### 内置约束

| Annotation | 说明 |
| ---------- | ---- |
| #NotNull   |      |
| @Min       |      |
|            |      |



## 参考

- https://hibernate.org/validator/