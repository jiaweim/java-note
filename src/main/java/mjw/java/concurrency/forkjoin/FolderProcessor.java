package mjw.java.concurrency.forkjoin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountedCompleter;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 19 Mar 2024, 5:01 PM
 */
public class FolderProcessor extends CountedCompleter<List<String>> {

    private String path;
    private String extension;
    // 当前 task 的 subtasks
    private List<FolderProcessor> tasks = new ArrayList<>();
    // 当前 task 的结果
    private List<String> resultList = new ArrayList<>();

    protected FolderProcessor(CountedCompleter<?> completer,
            String path, String extension) {
        super(completer);
        this.path = path;
        this.extension = extension;
    }

    public FolderProcessor(String path, String extension) {
        this.path = path;
        this.extension = extension;
    }

    @Override
    public void compute() {
        File file = new File(path);
        File[] content = file.listFiles();
        if (content != null) {
            for (File value : content) {
                // 如果是子目录
                if (value.isDirectory()) {
                    FolderProcessor task = new FolderProcessor(this,
                            value.getAbsolutePath(), extension);
                    task.fork(); // 异步执行
                    // pending-task +1
                    addToPendingCount(1);
                    tasks.add(task);
                } else {
                    if (checkFile(value.getName())) {
                        resultList.add(value.getAbsolutePath());
                    }
                }
            }
            if (tasks.size() > 50) {
                System.out.printf("%s: %d tasks ran.\n",
                        file.getAbsolutePath(), tasks.size());
            }

        }
        tryComplete();
    }

    @Override
    public List<String> getRawResult() {
        return resultList;
    }

    // 当所有 subtasks 完成，触发下面的方法
    @Override
    public void onCompletion(CountedCompleter<?> completer) {
        for (FolderProcessor childTask : tasks) {
            resultList.addAll(childTask.getRawResult());
        }
    }

    private boolean checkFile(String name) {
        return name.endsWith(extension);
    }
}
