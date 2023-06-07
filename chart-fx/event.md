# 事件处理

chart-fx 为事件处理定义了两个接口：

- `EventSource`
- `EventListener`

其中 `Dataset` 和 `Axis` 实现了 `EventSource` 接口，即可以监听数据和坐标轴的变化。

相关事件如下：

![[Pasted image 20230605165014.png|500]]

## EventSource

`Dataset` 和 `Axis` 实现了 `EventSource` 接口，所以可以监听数据和坐标轴的变化。

### autoNotification

```java
AtomicBoolean autoNotification()
```

一般来说，如果数据集中的数据发生变化，数据集应当通知注册的 Invalidation Listener。Chart 通常使用 dataset 注册 invalidation listener，当数据发生更改时更新 chart。

将 `autoNotification` 设置为 false，意味着禁用自动更新 chart 行为。例如，在一个数据采集循环中，数据集多次被更新，而 chart 只需要在周期结束时更新。

例如：

```java
dataSet2.autoNotification().set(false); // 禁用自动更新 
for (int n = 0; n < N_SAMPLES; n++) {  
    final double y2 = Math.sin(Math.toRadians(10.0 * n));  
    dataSet2.add(n, y2);
}  
dataSet2.autoNotification().set(true); // 启用自动更新 
// 手动触发 `UpdatedDataEvent` 事件
dataSet2.invokeListener(new UpdatedDataEvent(dataSet2 /* pointer to update source */, "manual update event"));
```

