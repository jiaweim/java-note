package mjw.java.concurrency;

import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 31 Mar 2025, 9:53 AM
 */
public class CompletableFutureTest {

    @Test
    void create() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "Hello, world!";
            }
        });
        future.thenAccept(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(s);
            }
        });

        CompletableFuture<String> completableFuture = new CompletableFuture<>();


    }

    public Future<String> calculateAsync() throws InterruptedException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            Thread.sleep(500);
            completableFuture.complete("Hello");
            return null;
        });

        return completableFuture;
    }

    @Test
    void completedFuture() {
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message");
        assertTrue(cf.isDone());
        assertEquals("message", cf.getNow(null));
    }

    @Test
    void runAsync() {
        CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
            assertTrue(Thread.currentThread().isDaemon());
            randomSleep();
        });
        assertFalse(cf.isDone());
        sleepEnough();
        assertTrue(cf.isDone());
    }

    @Test
    void theApply() {
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message")
                .thenApply(String::toUpperCase);
        assertEquals("MESSAGE", cf.getNow(null));
    }

    @Test
    void thenApplyAsync() {
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message")
                .thenApplyAsync(s -> {
                    assertTrue(Thread.currentThread().isDaemon());
                    randomSleep();
                    return s.toUpperCase();
                });
        assertNull(cf.getNow(null));
        assertEquals("MESSAGE", cf.join());
    }

    static ExecutorService executor = Executors.newFixedThreadPool(3,
            new ThreadFactory() {
                int count = 1;

                @Override
                public Thread newThread(Runnable runnable) {
                    return new Thread(runnable, "custom-executor-" + count++);
                }
            });

    @Test
    void thenApplyAsyncExecutor() {
        CompletableFuture<String> cf = CompletableFuture
                .completedFuture("message")
                .thenApplyAsync(s -> {
                    assertTrue(Thread.currentThread().getName()
                            .startsWith("custom-executor-"));
                    assertFalse(Thread.currentThread().isDaemon());
                    return s.toUpperCase();
                }, executor);
        assertNull(cf.getNow(null));
        assertEquals("MESSAGE", cf.join());
    }

    @Test
    void thenAccept() {
        StringBuilder sb = new StringBuilder();
        CompletableFuture.completedFuture("thenAccept message")
                .thenAccept(sb::append);
        assertFalse(sb.isEmpty());
    }

    @Test
    void thenAcceptAsync() {
        StringBuilder sb = new StringBuilder();
        CompletableFuture<Void> cf = CompletableFuture
                .completedFuture("thenAcceptAsync message")
                .thenAcceptAsync(sb::append);
        cf.join();
        assertFalse(sb.isEmpty());
    }

    static Random random = new Random();

    private static void randomSleep() {
        try {
            Thread.sleep(random.nextInt(1000));
        } catch (InterruptedException e) {
            // ...
        }
    }

    private static void sleepEnough() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // ...
        }
    }

    @Test
    void completeExceptionally() {
        CompletableFuture<String> cf = CompletableFuture
                .completedFuture("message")
                .thenApplyAsync(String::toUpperCase,
                        CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS));
        CompletableFuture<String> exceptionHandler = cf.handle(
                (s, error) -> error != null ? "message upon cancel" : "");
        cf.completeExceptionally(new RuntimeException("completed exceptionally"));
        assertTrue(cf.isCompletedExceptionally());
        try {
            cf.join();
            fail("should have thrown an exception");
        } catch (CompletionException e) {
            assertEquals("completed exceptionally", e.getCause().getMessage());
        }
        assertEquals("message upon cancel", exceptionHandler.join());
    }

    @Test
    void supplyAsync() {
        CompletableFuture.supplyAsync(() -> "Task Complete")
                .thenRun(() -> System.out.println("Next task starts now."));
    }

    @Test
    void thenCombine() {
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> 10);
        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> 20);
        future1.thenCombine(future2, Integer::sum)
                .thenAccept(i -> System.out.println("Combined Result:" + i));
    }

    @Test
    void thenCompose() {
        CompletableFuture<Integer> future =
                CompletableFuture.supplyAsync(() -> 5)
                        .thenCompose(i -> CompletableFuture.supplyAsync(() -> i * 2));
        future.thenAccept(i -> System.out.println("Final Result:" + i));

        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> 2);

        CompletableFuture<Integer> future2 = future1.thenApply(result -> result * 2);

        CompletableFuture<Integer> future3 = future1.thenCompose(
                result -> CompletableFuture.supplyAsync(() -> result * 2));
    }

    @Test
    void example() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(5000); // 模拟耗时任务
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Hello, world!";
        });
        future.thenAccept(System.out::println);
    }

    @Test
    void exceptionally(){
        CompletableFuture.supplyAsync(() -> {
            if(true){
                throw new ArithmeticException();
            }
            return 1;
        }).exceptionally(error -> {
            System.out.println("Exception occurred:"+ error.getMessage());
            return 0; // 默认值
        });
    }
}
