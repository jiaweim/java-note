package mjw.study.concurrency;

import java.lang.Thread.UncaughtExceptionHandler;

// 处理 Thread 中的 unchecked 异常
public class ExceptionHandler implements UncaughtExceptionHandler {

    /**
     * @param t 抛出异常的 Thread
     * @param e 被抛出的异常
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.printf("An exception has been captured\n");
        System.out.printf("Thread: %s\n", t.getId());
        System.out.printf("Exception: %s: %s\n", e.getClass().getName(), e.getMessage());
        System.out.printf("Stack Trace: \n");
        e.printStackTrace(System.out);
        System.out.printf("Thread status: %s\n", t.getState());
    }

}
