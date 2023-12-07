package mjw.java.time;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 04 Dec 2023, 5:23 PM
 */
public class InstantDemo {

    @Test
    void now(){
        Instant min = Instant.MIN;
        System.out.println(min);
    }

    @Test
    void between(){
        Instant start = Instant.now();
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        long millis = timeElapsed.toMillis();

    }
}


