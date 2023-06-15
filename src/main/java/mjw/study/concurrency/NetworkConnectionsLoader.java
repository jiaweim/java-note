package mjw.study.concurrency;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NetworkConnectionsLoader implements Runnable {

    @Override
    public void run() {
        // 开始运行
        System.out.printf("Begining network connections loading: %s\n", new Date());
        try {
            // sleep 6 秒，模拟初始化所需时间
            TimeUnit.SECONDS.sleep(6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 运行结束
        System.out.printf("Network connections loading has finished: %s\n", new Date());
    }
}
