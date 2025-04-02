package mjw.java.concurrency;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    void exceptionally() {
        CompletableFuture.supplyAsync(() -> {
            if (true) {
                throw new ArithmeticException();
            }
            return 1;
        }).exceptionally(error -> {
            System.out.println("Exception occurred:" + error.getMessage());
            return 0; // 默认值
        });
    }

    @Test
    void allOf() {
        CompletableFuture<String> future1 = CompletableFuture
                .supplyAsync(() -> "Hello");
        CompletableFuture<String> future2 = CompletableFuture
                .supplyAsync(() -> "Beautiful");
        CompletableFuture<String> future3 = CompletableFuture
                .supplyAsync(() -> "World");
        String collect = Stream.of(future1, future2, future3)
                .map(CompletableFuture::join)
                .collect(Collectors.joining(" "));
        assertEquals("Hello Beautiful World", collect);

    }

    private static ExecutorService executorService = Executors.newCachedThreadPool();

    private static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void date(String str) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current = dateFormat.format(now);
        System.out.println(str + ": " + current);
    }

    @Test
    void test1() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture
                .supplyAsync(() -> {
                    sleep(1000);
                    return "Hello, world!";
                }, executor);

        String s = future.get();
        System.out.println(s);
    }

    @Test
    void test2() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            sleep(1000);
            int i = 1 / 0;
            return "Hello, world!";
        });
        future.whenComplete((s, throwable) -> System.out.println(s));
        future.exceptionally(throwable -> {
            System.out.println("failed: " + throwable.getMessage());
            return "Error";
        });
        future.join();
    }

    @Test
    public void testFuture() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        Future<String> future = executorService.submit(() -> {
            Thread.sleep(2000);
            return "hello";
        });
        System.out.println(future.get());
        System.out.println("end");
    }

    @Test
    public void testCountDownLatch() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        CountDownLatch downLatch = new CountDownLatch(2);

        long startTime = System.currentTimeMillis();
        Future<String> userFuture = executorService.submit(() -> {
            //模拟查询商品耗时500毫秒
            Thread.sleep(500);
            downLatch.countDown();
            return "用户A";
        });

        Future<String> goodsFuture = executorService.submit(() -> {
            //模拟查询商品耗时500毫秒
            Thread.sleep(400);
            downLatch.countDown();
            return "商品A";
        });

        downLatch.await();
        //模拟主程序耗时时间
        Thread.sleep(600);
        System.out.println("获取用户信息:" + userFuture.get());
        System.out.println("获取商品信息:" + goodsFuture.get());
        System.out.println("总共用时" + (System.currentTimeMillis() - startTime) + "ms");
    }

    @Test
    public void testCompletableInfo() throws InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis();

        //调用用户服务获取用户基本信息
        CompletableFuture<String> userFuture = CompletableFuture.supplyAsync(() ->
                //模拟查询商品耗时500毫秒
        {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "用户A";
        });

        //调用商品服务获取商品基本信息
        CompletableFuture<String> goodsFuture = CompletableFuture.supplyAsync(() ->
                //模拟查询商品耗时500毫秒
        {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "商品A";
        });

        System.out.println("获取用户信息:" + userFuture.get());
        System.out.println("获取商品信息:" + goodsFuture.get());

        //模拟主程序耗时时间
        Thread.sleep(600);
        System.out.println("总共用时" + (System.currentTimeMillis() - startTime) + "ms");
    }

    @Test
    public void testCompletableGet() throws InterruptedException, ExecutionException {

        CompletableFuture<String> cp1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "商品A";
        });

        // getNow方法测试
        System.out.println(cp1.getNow("商品B")); // 输出 "商品B"

        //join方法测试
        CompletableFuture<Integer> cp2 = CompletableFuture.supplyAsync((() -> 1 / 0)); // 抛出 CompletionException
        System.out.println(cp2.join());
        System.out.println("-----------------------------------------------------");
        //get方法测试
        CompletableFuture<Integer> cp3 = CompletableFuture.supplyAsync((() -> 1 / 0));
        System.out.println(cp3.get());
    }

    @Test
    void testGetNow() {
        CompletableFuture<String> cp1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "商品A";
        });

        // getNow方法测试
        System.out.println(cp1.getNow("商品B")); // 输出 "商品B"
    }

    @Test
    void testJoin() {
        CompletableFuture<Integer> cp2 = CompletableFuture.supplyAsync((() -> 1 / 0)); // 抛出 CompletionException
        System.out.println(cp2.join());
    }

    @Test
    void testGet() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> cp3 = CompletableFuture.supplyAsync((() -> 1 / 0));
        System.out.println(cp3.get());
    }

    @Test
    public void testCompletableThenRunAsync() throws InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis();

        CompletableFuture<Void> cp1 = CompletableFuture.runAsync(() -> {
            try {
                //执行任务A
                Thread.sleep(600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        CompletableFuture<Void> cp2 = cp1.thenRun(() -> {
            try {
                //执行任务B
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // get方法测试
        System.out.println(cp2.get());
        //模拟主程序耗时时间
        Thread.sleep(600);
        System.out.println("总共用时" + (System.currentTimeMillis() - startTime) + "ms");
    }

    @Test
    public void testCompletableThenAccept() throws ExecutionException, InterruptedException {
//        CompletableFuture<String> cp1 = CompletableFuture.supplyAsync(() -> "dev");
//        CompletableFuture<Void> cp2 = cp1.thenAccept((a) -> {
//            System.out.println("上一个任务的返回结果为: " + a);
//        });
//
//        cp2.get();

        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> "Hello, World!")
                .thenAccept(s -> System.out.println("Result:" + s));
        future.join();

    }

    @Test
    public void testCompletableThenApply() throws ExecutionException, InterruptedException {
        CompletableFuture<String> cp1 = CompletableFuture
                .supplyAsync(() -> "dev")
                .thenApply((a) -> {
                    if (Objects.equals(a, "dev")) {
                        return "dev";
                    }
                    return "prod";
                });

        System.out.println("当前环境为:" + cp1.get());
    }

    @Test
    public void testCompletableWhenComplete() throws ExecutionException, InterruptedException {
        CompletableFuture<Double> future = CompletableFuture.supplyAsync(() -> {
            if (Math.random() < 0.5) {
                throw new RuntimeException("出错了");
            }
            System.out.println("正常结束");
            return 0.11;

        }).whenComplete((aDouble, throwable) -> {
            if (aDouble == null) {
                System.out.println("whenComplete aDouble is null");
            } else {
                System.out.println("whenComplete aDouble is " + aDouble);
            }
            if (throwable == null) {
                System.out.println("whenComplete throwable is null");
            } else {
                System.out.println("whenComplete throwable is " + throwable.getMessage());
            }
        });
        System.out.println("最终返回的结果 = " + future.get());
    }

    @Test
    public void testCompletableThenCombine() throws ExecutionException, InterruptedException {
        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        //开启异步任务1
        CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
            System.out.println("异步任务1，当前线程是：" + Thread.currentThread().threadId());
            int result = 1 + 1;
            System.out.println("异步任务1结束");
            return result;
        }, executorService);

        //开启异步任务2
        CompletableFuture<Integer> task2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("异步任务2，当前线程是：" + Thread.currentThread().threadId());
            int result = 1 + 1;
            System.out.println("异步任务2结束");
            return result;
        }, executorService);

        //任务组合
        CompletableFuture<Integer> task3 = task.thenCombineAsync(task2, (f1, f2) -> {
            System.out.println("执行任务3，当前线程是：" + Thread.currentThread().threadId());
            System.out.println("任务1返回值：" + f1);
            System.out.println("任务2返回值：" + f2);
            return f1 + f2;
        }, executorService);

        Integer res = task3.get();
        System.out.println("最终结果：" + res);
    }

    @Test
    public void testCompletableEitherAsync() {
        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        //开启异步任务1
        CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
            System.out.println("异步任务1，当前线程是：" + Thread.currentThread().threadId());

            int result = 1 + 1;
            System.out.println("异步任务1结束");
            return result;
        }, executorService);

        //开启异步任务2
        CompletableFuture<Integer> task2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("异步任务2，当前线程是：" + Thread.currentThread().threadId());
            int result = 1 + 2;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("异步任务2结束");
            return result;
        }, executorService);

        //任务组合
        task.acceptEitherAsync(task2, (res) -> {
            System.out.println("执行任务3，当前线程是：" + Thread.currentThread().threadId());
            System.out.println("上一个任务的结果为：" + res);
        }, executorService);
    }

    @Test
    public void testCompletableAallOf() throws ExecutionException, InterruptedException {
        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        //开启异步任务1
        CompletableFuture<Integer> task1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("异步任务1，当前线程是：" + Thread.currentThread().threadId());
            int result = 1 + 1;
            System.out.println("异步任务1结束");
            return result;
        }, executorService);

        //开启异步任务2
        CompletableFuture<Integer> task2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("异步任务2，当前线程是：" + Thread.currentThread().threadId());
            int result = 1 + 2;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("异步任务2结束");
            return result;
        }, executorService);

        //开启异步任务3
        CompletableFuture<Integer> task3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("异步任务3，当前线程是：" + Thread.currentThread().threadId());
            int result = 1 + 3;
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("异步任务3结束");
            return result;
        }, executorService);

        //任务组合
        CompletableFuture<Void> allOf = CompletableFuture.allOf(task1, task2, task3);

        //等待所有任务完成
        allOf.get();
        //获取任务的返回结果
        System.out.println("task1结果为：" + task1.get());
        System.out.println("task2结果为：" + task2.get());
        System.out.println("task3结果为：" + task3.get());
    }

    @Test
    public void testCompletableAnyOf() throws ExecutionException, InterruptedException {
        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        //开启异步任务1
        CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
            int result = 1 + 1;
            return result;
        }, executorService);

        //开启异步任务2
        CompletableFuture<Integer> task2 = CompletableFuture.supplyAsync(() -> {
            int result = 1 + 2;
            return result;
        }, executorService);

        //开启异步任务3
        CompletableFuture<Integer> task3 = CompletableFuture.supplyAsync(() -> {
            int result = 1 + 3;
            return result;
        }, executorService);

        //任务组合
        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(task, task2, task3);
        //只要有一个有任务完成
        Object o = anyOf.get();
        System.out.println("完成的任务的结果：" + o);
    }

    @Test
    public void testWhenCompleteExceptionally() throws InterruptedException {
        CompletableFuture<Double> future = CompletableFuture.supplyAsync(() -> {
            if (1 == 1) {
                throw new RuntimeException("出错了");
            }
            System.out.println("a");
            return 0.11;
        });
        Thread.sleep(1000);
        //如果不加 get()方法这一行，看不到异常信息
        //future.get();
    }

    @Test
    void exceptionallyMultiple() {
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            // Some long-running operation
            return 10;
        });

        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            int result = 10 / 0; // Causes an ArithmeticException
            return result;
        });

        CompletableFuture<Integer> future3 = CompletableFuture.supplyAsync(() -> {
            // Some long-running operation
            return 20;
        });

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(future1, future2, future3);

        allFutures.exceptionally(ex -> {
            System.out.println("Exception occurred: " + ex.getMessage());
            return null; // Default value to return if there's an exception
        }).thenRun(() -> {
            // All futures completed
            int result1 = future1.join();
            int result2 = future2.join();
            int result3 = future3.join();
            System.out.println(result1 + ", " + result2 + ", " + result3);
        });
    }

    @Test
    void thenApply() {

        ExecutorService e = Executors.newSingleThreadExecutor(r -> new Thread(r, "dead-pool"));
        CompletableFuture<Integer> f = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "task-1");
            return 42;
        }, e);

        CompletableFuture<String> f2 = f.thenApply(i -> {
            System.out.println(Thread.currentThread().getName() + "task-2");
            return i.toString();
        });

    }

    @Test
    void thenApply2() {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> "Hello")
                .thenApply(String::length);
        Integer result = future.join();
        assertEquals(5, result);
    }


    @Test
    void cancelExample() {
        CompletableFuture<String> cf = CompletableFuture
                .completedFuture("message")
                .thenApplyAsync(String::toUpperCase,
                        CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS));
        CompletableFuture<String> cf2 = cf.exceptionally(throwable -> "canceled message");
        assertTrue(cf.cancel(true));
        assertTrue(cf.isCompletedExceptionally());
        assertEquals("canceled message", cf2.join());
    }
}
