# 对实例控制，enum 优于 readResolve

## 简介

在 T3 单例模式中，给出了如下的单例类。该类通过限制对构造函数的访问，确保只创建一个实例：

```java
public class Elvis {
	public static final Elvis INSTANCE = new Elvis();
	private Elvis() { ... }
	public void leaveTheBuilding() { ... }
}
```

