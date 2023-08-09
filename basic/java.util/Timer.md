# Timer

## 简介

`java.util.Timer` 和 `java.util.TimerTask` 用于在后台线程调度任务：

- `TimerTask` 实现了 `Runnable` 接口，是要执行的任务
- `Timer` 是调度器

## 一次性任务

### 延迟执行

`Timer.schedule(TimerTask task, long delay)` 延迟执行指定任务，例如：

```java
TimerTask task = new TimerTask() {
    public void run() {
        System.out.println("Task performed on: " + new Date() + "\n" +
                "Thread's name: " + Thread.currentThread().getName());
    }
};
Timer timer = new Timer("Timer");

long delay = 1000L;
timer.schedule(task, delay);
```

```
Task performed on: Wed Aug 02 13:59:18 CST 2023
Thread's name: Timer
```

### 指定 Date 和 Time 执行

`Timer.schedule(TimerTask task, Date time)` 在指定 Date 执行任务。

假设你有一个遗留数据库，需要将其数据迁移到新数据库中，可以创建一个 `DatabaseMigrationTask` 类来完成该任务：

```java
import java.util.List;
import java.util.TimerTask;

public class DatabaseMigrationTask extends TimerTask {

    private List<String> oldDatabase;
    private List<String> newDatabase;

    public DatabaseMigrationTask(List<String> oldDatabase, List<String> newDatabase) {
        this.oldDatabase = oldDatabase;
        this.newDatabase = newDatabase;
    }

    @Override
    public void run() {
        newDatabase.addAll(oldDatabase);
    }
}
```

简单起见，这里直接用 `List` 表示两个数据库；而数据迁移就是把第一个 `List` 的数据加到第二个 `List`。

用 `schedule()` 方法在指定时间执行迁移：

```java
List<String> oldDatabase = Arrays.asList("Harrison Ford", "Carrie Fisher", "Mark Hamill");  
List<String> newDatabase = new ArrayList<>();  
  
LocalDateTime twoSecondsLater = LocalDateTime.now().plusSeconds(2);  
Date twoSecondsLaterAsDate = 
                    Date.from(twoSecondsLater.atZone(ZoneId.systemDefault()).toInstant());  
  
new Timer().schedule(new DatabaseMigrationTask(oldDatabase, newDatabase), twoSecondsLaterAsDate);
```

## 重复任务

