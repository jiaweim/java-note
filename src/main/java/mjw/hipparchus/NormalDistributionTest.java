package mjw.hipparchus;

import org.hipparchus.distribution.continuous.NormalDistribution;
import org.junit.jupiter.api.Test;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 30 Apr 2024, 1:38 PM
 */
public class NormalDistributionTest {

    @Test
    void calcPValue() {
        NormalDistribution distribution = new NormalDistribution();
        double v = distribution.cumulativeProbability(-2.23);
        System.out.println(v);
    }

    @Test
    void calcTwoTailed() {
        NormalDistribution distribution = new NormalDistribution();
        double v = distribution.cumulativeProbability(-2.14);
        System.out.println(v);
    }

    @Test
    void zTest() {

    }
}
