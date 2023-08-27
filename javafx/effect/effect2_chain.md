# 特效串联

2023-08-11, 10:10
@author Jiawei Mao
## 简介

有些特效可以与其它特效一起串联使用。第一个特效的输出成为下一个特效的输入，以此类推。如下图所示：

@import "images/2023-08-11-10-04-14.png" {width="500px" title=""}

支持串联的特效类包含一个 `input` 属性，指定在它之前应用的特效。

如果 input 为 null，则将特效应用于当前 node，而不是前一个特效的输出。input 默认为 null。

**示例：** 在 Text 上创建 2 个特效序列

```java{.line-numbers}
// Effect Chain: Text >> Reflection >> Shadow
DropShadow dsEffect = new DropShadow();
dsEffect.setInput(new Reflection());
Text t1 = new Text("Reflection and Shadow");
t1.setEffect(dsEffect);

// Effect Chain: Text >> Shadow >> Reflection
Reflection reflection = new Reflection();
reflection.setInput(new DropShadow());
Text t2 = new Text("Shadow and Reflection");
t2.setEffect(reflection);
```

![](images/2023-08-11-10-07-41.png)

- 左：按 Reflection, Shadow 的顺序应用特效，所以其镜像没有阴影
- 右：按 Shadow, Reflection 的顺序应用特效，所以其镜像有阴影
