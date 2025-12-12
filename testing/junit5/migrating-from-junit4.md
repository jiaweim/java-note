# 从 JUnit 4 迁移到 JUnit 5

## 简介

虽然 Junit Jupiter 编程模型和扩展模型不支持 Junit 4 的功能，如 `Rules` 和 `Runners`，但迁移到 Junit Jupiter 的工作量没那么大。

Junit 通过 Junit Vintage 测试引擎提供温和的迁移路径，并允许基于 Junit 3 和 Junit 4 的测试执行。由于所有针对 Junit Jupiter 的类和注释都位于 `org.junit.jupiter` package 中，因此在类路径同时添加 Junit 4 和 Junit Jupiter 不会导致冲突。因此，可以安全地将现有 Junit 4 测试与 Junit Jupiter 测试逐步迁移。




## 迁移技巧
在迁移时需要注意以下方面：
- `@RunWith` 被 `@ExtendWith` 替代



## 参考

- https://docs.junit.org/current/user-guide/#migrating-from-junit4