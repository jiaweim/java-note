package mjw.java.concurrency.executor;

import java.util.concurrent.FutureTask;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 18 Mar 2024, 11:18 AM
 */
public class ResultTask extends FutureTask<String> {

    private final String name;

    public ResultTask(ExecutableTask callable) {
        super(callable);
        this.name = callable.getName();
    }

    @Override
    protected void done() {
        if (isCancelled()) {
            System.out.printf("%s: Has been canceled\n", name);
        } else {
            System.out.printf("%s: Has finished\n", name);
        }
    }
}
