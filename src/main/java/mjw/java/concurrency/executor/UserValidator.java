package mjw.java.concurrency.executor;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 11 Mar 2024, 5:05 PM
 */
public class UserValidator {

    private final String name;

    public UserValidator(String name) {
        this.name = name;
    }

    public boolean validate(String name, String password) {
        Random random = new Random();

        try {
            // 随机 sleep 一下，模拟验证过程
            long duration = (long) (Math.random() * 10);
            System.out.printf("Validator %s: Validating a user during %d seconds\n",
                    this.name, duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            return false;
        }
        return random.nextBoolean();
    }

    public String getName() {
        return name;
    }
}
