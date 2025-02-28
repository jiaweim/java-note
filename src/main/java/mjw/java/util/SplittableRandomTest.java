package mjw.java.util;

import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 09 Feb 2025, 3:39 PM
 */
public class SplittableRandomTest {
    @Test
    void test(){
        SplittableRandom random = new SplittableRandom();
        random.nextInt(10); // 0-9
    }
}
