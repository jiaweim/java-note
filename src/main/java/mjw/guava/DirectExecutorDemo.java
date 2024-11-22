package mjw.guava;

import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 11 Nov 2024, 11:09 AM
 */
public class DirectExecutorDemo {
    public static void main(String[] args) {
        Executor executor = MoreExecutors.directExecutor();
        AtomicBoolean executed = new AtomicBoolean(false);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                executed.set(true);
            }
        });
        assert executed.get();
    }
}
