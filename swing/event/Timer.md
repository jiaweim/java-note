# Timer

2024-04-08⭐
@author Jiawei Mao
***

## 简介

除了 `EventQueue` 提供的 `invokeAndWait()` 和 `invokeLater()` 方法，还可以使用 `Timer` 类创建要在 EDT 上执行的操作。`Timer` 支持延迟和周期性执行。

## 创建 Timer

`javax.swing.Timer` 只有一个构造函数：

```java
public Timer(int delay, ActionListener listener)
```

## 使用 Timer

`Timer` 方法：

- `start()` 启动
- `stop()` 停止
- `restart()` 重启

调用 `start()` 后 `ActionListener` 在指定延迟时间后执行，如果系统繁忙，延迟可能更长，但不会更短。

**示例：** 定义一个 ActionListener，它打印一条消息。Timer 没半秒调用一次该 listener

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimerSample {

    public static void main(String[] args) {
        Runnable runner = new Runnable() {
            @Override
            public void run() {
                ActionListener actionListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Hello World Timer");
                    }
                };
                Timer timer = new Timer(500, actionListener);
                timer.start();
            }
        };
        EventQueue.invokeLater(runner);
    }
}
```

## Timer 属性

|属性|类型|权限|
|---|---|---|
|actionListeners|ActionListener[]| Read-only|
|coalesce |boolean| Read-write|
|delay| int| Read-write|
|initialDelay| int| Read-write|
|repeats |boolean| Read-write|
|running |boolean| Read-only|

其中 4 个属性用于自定义 `Timer` 的行为，`running` 表示 `Timer` 是否在运行，`actionListeners` 返回 action-listeners。

`delay` 属性和构造函数的参数一样。如果在 `Timer` 运行时修改 `delay`，新的 `delay` 不会立即生效，而是等上一个 `delay` 结束。

除了周期性的 `delay`，还可以指定一个启动前延迟 `initialDelay`，如下所示：

```
|------------|-----|----------|-----|----------|
initialDelay  delay   running   delay  running
```

`initialDelay` 默认和 `delay` 相同。

`repeats` 属性默认为 true，即 `Timer` 重复运行，当为 `false` 时，`Timer` 只运行一次，只运行 `ActionListener` 一次。要多次运行需要调用 restart()。非 `repeats` `Timer` 适合在一次性通知。

`coalesce` 属性允许在系统繁忙时丢掉还未处理的事件。`coalesce` 默认为 `true`。即，如果 `Timer` 每 500 毫秒运行一次，但系统卡住了，在整整 2 秒都没有响应，再次响应时，`Timer` 只需要发送一条消息，超时的消息直接丢掉。如果设置为 `false`，则 `Timer` 需要发送 4 条消息。

除了以上属性，还可以开启日志：

```java
Timer.setLogTimers(true);
```

!!! tip
    `java.util.Timer` 的工作方式与 `javax.swing.Timer` 类似，只是不在 EDT 线程运行。