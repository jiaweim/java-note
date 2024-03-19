# 合并任务结果

2024-03-19
@author Jiawei Mao
## 简介

fork/join 框架可以执行返回结果的任务。这类任务通过扩展 RecursiveTask 类实现。该类扩展 ForkJoinTask 类，并实现 Future 接口。

在任务中，需要使用 Java  API 文档推荐的代码结构：

```java
if (problem size > size){
    tasks=Divide(task);
    execute(tasks);
    joinResults()
    return result;
} else {
    resolve problem;
    return result;
}
```

如果任务量大于 ref-size，就将问题拆分为更多 subtasks，并使用 fork/join 框架执行这些 subtasks。当 subtasks 执行完毕，task 获取所有 subtasks 生成的结果，合并结果，返回最终结果。

## 示例

在文档中查找单次，执行如下两类任务：

- document-task，在文档中搜索 word
- line-task，在部分文档（一行）中搜索 word

所有 tasks 返回对应文本 word 出现次数。

1. DocumentMock，生成文本矩阵，模拟文档

```java
import java.util.Random;

public class DocumentMock {

    private String[] words = {"the", "hello", "goodbye", "packt",
            "java", "thread", "pool", "random", "class", "main"};

    public String[][] generateDocument(int numLines, int numWords,
            String word) {
        int counter = 0;
        String[][] document = new String[numLines][numWords];
        Random random = new Random();
        // 随机填充
        for (int i = 0; i < numLines; i++) {
            for (int j = 0; j < numWords; j++) {
                int index = random.nextInt(words.length);
                document[i][j] = words[index];
                if (document[i][j].equals(word)) {
                    counter++;
                }
            }
        }
        System.out.println("DocumentMock: The word appears " + counter
                + " times in the document");
        return document;
    }
}
```

2. DocumentTask，任务类，计算 word 出现的次数

```java
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveTask;

public class DocumentTask extends RecursiveTask<Integer> {

    private String[][] document;
    private int start, end;
    private String word;

    public DocumentTask(String[][] document, int start, int end,
            String word) {
        this.document = document;
        this.start = start;
        this.end = end;
        this.word = word;
    }

    @Override
    protected Integer compute() {
        Integer result = null;
        // 若少于 10 行，直接处理
        if (end - start < 10) {
            result = processLines(document, start, end, word);
        } else {
            int mid = (start + end) / 2;
            DocumentTask task1 = new DocumentTask(document, start, mid, word);
            DocumentTask task2 = new DocumentTask(document, mid, end, word);
            invokeAll(task1, task2);
            // groupResults 用来合并两个任务的结果
            try {
                result = groupResults(task1.get(), task2.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private Integer processLines(String[][] document, int start,
            int end, String word) {
        // 对每行文本，单独创建一个 LineTask 进行处理
        List<LineTask> tasks = new ArrayList<>();
        for (int i = start; i < end; i++) {
            LineTask task = new LineTask(document[i], 0,
                    document[i].length, word);
            tasks.add(task);
        }
        // 执行所有 LineTask
        invokeAll(tasks);
        // 合并所有 LineTask 的结果
        int result = 0;
        for (LineTask task : tasks) {
            try {
                result = result + task.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private Integer groupResults(Integer number1, Integer number2) {
        Integer result;
        result = number1 + number2;
        return result;
    }
}
```

3. LineTask，处理单行文本

```java
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveTask;

public class LineTask extends RecursiveTask<Integer> {

    private String[] line;
    private int start, end;
    private String word;

    public LineTask(String[] line, int start, int end, String word) {
        this.line = line;
        this.start = start;
        this.end = end;
        this.word = word;
    }

    @Override
    protected Integer compute() {
        Integer result = null;
        // 如果小于 100 个单次，直接处理
        if (end - start < 100) {
            result = count(line, start, end, word);
        } else {
            // 将一行文本继续拆分
            int mid = (start + end) / 2;
            LineTask task1 = new LineTask(line, start, mid, word);
            LineTask task2 = new LineTask(line, mid, end, word);
            invokeAll(task1, task2);

            try {
                result = groupResults(task1.get(), task2.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private Integer count(String[] line, int start, int end,
            String word) {
        int counter;
        counter = 0;
        for (int i = start; i < end; i++) {
            if (line[i].equals(word)) {
                counter++;
            }
        }
        // 增加任务执行时间，模拟耗时任务
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return counter;
    }

    private Integer groupResults(Integer number1, Integer number2) {
        Integer result;
        result = number1 + number2;
        return result;
    }
}
```

4. main 类

```java
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class Main2 {

    public static void main(String[] args) {
        DocumentMock mock = new DocumentMock();
        // 生成文档，100 行，每行 1000 个单次
        String[][] document = mock.generateDocument(100, 1000, "the");
        // 创建任务
        DocumentTask task = new DocumentTask(document, 0, 100, "the");
        // 使用默认的 ForkJoinPool
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        commonPool.execute(task);
        // 查看任务进展，直到任务结束
        do {
            System.out.printf("******************************************\n");
            System.out.printf("Main: Active Threads: %d\n",
                    commonPool.getActiveThreadCount());
            System.out.printf("Main: Task Count: %d\n",
                    commonPool.getQueuedTaskCount());
            System.out.printf("Main: Steal Count: %d\n",
                    commonPool.getStealCount());
            System.out.printf("******************************************\n");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!task.isDone());
        // 关闭 pool
        commonPool.shutdown();
        // 等待任务结束
        try {
            commonPool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            System.out.printf("Main: The word appears %d in the document", task.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
```

```
DocumentMock: The word appears 10050 times in the document
******************************************
Main: Active Threads: 2
Main: Task Count: 3
Main: Steal Count: 0
******************************************
Main: The word appears 10050 in the document
```

上例实现了两个任务：

- DocumentTask：处理文档 start 到 end 的所有 lines
  - 如果 lines 小于 10，则对每行文本创建 LineTask，当它们执行完毕，将这些 task 的结果求和并返回结果。
  - 如果 lines 数不小于 10，则拆分任务为两份，当这些 subtasks 完成，合并结果。
- LineTask：该 task 负责处理一行文本 start 到 end 之间的部分
  - 如果 words 数小于 100，直接搜索，计算 word 出现次数
  - 如果 words 数 >=100，则拆分任务

在 Main 类中，使用默认的 ForkJoinPool，并执行 DocumentTask 任务。DocumentTask 需要处理 100 行、每行 1000 words 的文本。DocumentTask 将 task 拆分为 subtasks，subtasks 将每行文本创建一个 LineTask，而 LineTask 根据长度继续拆分。

使用 get() 获得 task 执行结果，该方法在 Future 中声明，由 RecursiveTask 实现。

