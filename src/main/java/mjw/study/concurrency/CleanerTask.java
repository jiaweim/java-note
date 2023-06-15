package mjw.study.concurrency;

import java.util.Date;
import java.util.Deque;

/**
 * 查看 deque 中的 events，删除 10 秒前的 events
 */
public class CleanerTask extends Thread {

    // 保存 events
    private Deque<Event> deque;

    public CleanerTask(Deque<Event> deque) {
        this.deque = deque;
        // 将其设置为 daemon 线程
        setDaemon(true);
    }

    @Override
    public void run() {
        // daemon 线程常包含无限循环
        while (true) {
            Date date = new Date();
            clean(date);
        }
    }

    /**
     * 查看 deque 中的事件，删除 10 秒前的 events
     */
    private void clean(Date date) {
        long difference;
        boolean delete;
        if (deque.size() == 0) {
            return;
        }

        delete = false;
        do {
            Event e = deque.getLast();
            difference = date.getTime() - e.getDate().getTime();
            if (difference > 10000) {
                System.out.printf("Cleaner: %s\n", e.getEvent());
                deque.removeLast();
                delete = true;
            }
        } while (difference > 10000);

        if (delete) {
            System.out.printf("Cleaner: Size of the queue: %d\n", deque.size());
        }
    }
}
