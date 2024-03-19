package mjw.java.concurrency.forkjoin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveTask;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 19 Mar 2024, 3:38 PM
 */
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
