# ConcurrentLinkedDeque

## 简介

ConcurrentLinkedDeque 实现 non-blocking 双向队列。

## 示例

- 创建 `AddTask` 类

```java
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 添加 10000 个元素到 ConcurrentLinkedDeque
 */
public class AddTask implements Runnable {

    /**
     * List to add the elements
     */
    private final ConcurrentLinkedDeque<String> list;

    /**
     * Constructor of the class
     *
     * @param list List to add the elements
     */
    public AddTask(ConcurrentLinkedDeque<String> list) {
        this.list = list;
    }

    /**
     * Main method of the class. Add 10000 elements to the list using the add()
     * method that adds the element at the end of the list
     */
    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        for (int i = 0; i < 10000; i++) {
            list.add(name + ": Element " + i);
        }
    }

}
```