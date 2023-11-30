package mjw.java.util;

import com.google.common.base.Stopwatch;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 28 Nov 2023, 10:01 AM
 */
public class PrimeUtil {

    public static boolean isPrime(int number) {
        if (number < 2)
            return false;

        for (int i = 2; Math.sqrt(number) >= i; i++) {
            if (number % i == 0)
                return false;
        }

        return true;
    }

    @Test
    void findPrimse() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        long count1 = IntStream.range(1, 100_0000)
                .filter(PrimeUtil::isPrime).count();
        System.out.println(count1);
        System.out.println(stopwatch.elapsed(TimeUnit.MILLISECONDS));
        stopwatch.reset();
        long count2 = IntStream.range(1, 100_0000)
                .parallel()
                .filter(PrimeUtil::isPrime).count();
        System.out.println(count2);
        System.out.println(stopwatch.elapsed(TimeUnit.MILLISECONDS));

    }
}
