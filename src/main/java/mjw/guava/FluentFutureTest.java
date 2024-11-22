package mjw.guava;

import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.SettableFuture;
import org.hipparchus.distribution.discrete.GeometricDistribution;
import org.hipparchus.distribution.discrete.PoissonDistribution;
import org.junit.jupiter.api.Test;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 11 Nov 2024, 2:38 PM
 */
public class FluentFutureTest {

    @Test
    void testFromFluentFuture() {
        FluentFuture<String> f = FluentFuture.from(SettableFuture.<String>create());

    }

    @Test
    void test() {
        double p = 15;
        PoissonDistribution dis = new PoissonDistribution(p);
        System.out.println(1 - dis.cumulativeProbability(15));
    }
}
