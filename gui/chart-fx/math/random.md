# TRandom

2023-08-07, 21:20
****
## 简介

TRandom 是用一个基本的随机数生成器类（周期=$10^9$）。这是一个非常简单的生成器（linear congruential），存在缺陷，因此不应该用作任何统计研究。

TRandom 类图如下：

![|300](Pasted%20image%2020230807203613.png)

推荐使用 TRandom1, TRandom3 类：

- TRandom3 基于 "Mersenne Twister generator"，随机特性好（周期=$10^{6000}$），速度快，因此**推荐**
- TRandom1 基于 RANLUX 算法，已在数学上证明其随机性，周期约为 $10^{171}$，但是速度较慢
- TRandom2 基于 Tausworthe generator of L'Ecuye，速度快，仅使用 3 个单词 (32 位) 作为状态，周期为 $10^{26}$ 

提供了如下基本分布：

- Exp
- Integer
- Gaus
- Rndm
- Uniform
- Landau
- Poisson
- Binomial


