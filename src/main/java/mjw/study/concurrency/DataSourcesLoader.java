package mjw.study.concurrency;

import java.util.Date;
import java.util.concurrent.TimeUnit;


public class DataSourcesLoader implements Runnable {

    @Override
    public void run() {

        System.out.printf("Beginning data sources loading: %s\n", new Date());
        try {
            // sleep 4 秒，模拟初始化时间
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Data sources loading has finished: %s\n", new Date());
    }
}
