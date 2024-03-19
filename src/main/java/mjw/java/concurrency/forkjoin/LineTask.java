package mjw.java.concurrency.forkjoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveTask;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 19 Mar 2024, 3:42 PM
 */
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
