package mjw.study.concurrency;

import java.util.Date;
import java.util.Deque;
import java.util.concurrent.TimeUnit;

/**
 * Runnable class that generates and event every second
 */
public class WriterTask implements Runnable {

    /**
     * Data structure to stores the events
     */
    Deque<Event> deque;

    /**
     * Constructor of the class
     *
     * @param deque data structure that stores the event
     */
    public WriterTask(Deque<Event> deque) {
        this.deque = deque;
    }

    @Override
    public void run() {

        // 输出 100 个 events
        for (int i = 1; i < 100; i++) {
            // 创建并初始化 event 对象
            Event event = new Event();
            event.setDate(new Date());
            event.setEvent(String.format("The thread %s has generated the event %d event",
                    Thread.currentThread().getId(), i));

            // 添加到 queue
            deque.addFirst(event);
            try {
                // sleep 一秒
                Thread.sleep(500);
//                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
