package mjw.java.concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 02 Apr 2025, 1:44 PM
 */
public class Flow<T> extends CompletableFuture<T> {

    private static final Executor executor =
            Executors.newFixedThreadPool(10);

    public Flow() {}

    @Override
    public Executor defaultExecutor() {
        return executor;
    }

    @Override
    public <U> Flow<U> newIncompleteFuture() {
        return new Flow<U>();
    }
}
