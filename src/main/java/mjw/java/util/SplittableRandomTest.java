package mjw.java.util;

import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 09 Feb 2025, 3:39 PM
 */
public class SplittableRandomTest {
    @Test
    void test() {
        SplittableRandom random = new SplittableRandom();
        random.nextInt(10); // 0-9
    }

    @Test
    void random() {
        Random random = new Random();
        int randomInt = random.nextInt();
        random.nextInt(10, 20);
    }

    @Test
    void threadLocal(){
        int min = 10;
        int max = 20;
        int val = ThreadLocalRandom.current().nextInt(min, max);

        int anInt = ThreadLocalRandom.current().nextInt();
    }
}
