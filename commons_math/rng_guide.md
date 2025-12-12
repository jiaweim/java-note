# 随机数生成器

## 定义

**随机数生成器**（Random number generator, RNG）生成一个有界范围内的整数，每种整数出现的可能相同。

**伪随机数生成器**（Pseudorandom number generator, PRNG）通过数学扩展其输入的算法来生成数字的随机数生成器。

- 是否有信息安全需求（如密码）
  - 是：使用加密 RNG（cryptographic）



## 加密 RNG

加密 RNG 只在生成不仅“看着随机”，而且猜测成本很高的数字。具有如下需求的应用应该使用加密 RNG：

- 生成用于信息安全的随机数
- 