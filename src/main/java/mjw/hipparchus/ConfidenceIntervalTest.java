package mjw.hipparchus;

import org.hipparchus.distribution.continuous.NormalDistribution;
import org.hipparchus.stat.StatUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 23 Apr 2024, 4:04 PM
 */
public class ConfidenceIntervalTest {

    @Test
    void test() {

        NormalDistribution normalDistribution = new NormalDistribution();
        double v = normalDistribution.cumulativeProbability(2.575);
        double probability = normalDistribution.probability(-2.575, 2.575);
        System.out.println(probability);

        NormalDistribution distribution = new NormalDistribution();
        double significance = 0.80;

        double criticalValue = distribution.inverseCumulativeProbability((1 - significance) / 2);
        System.out.println(criticalValue);
    }

    @Test
    void mean() {
        double[] data = new double[]{
                19, 18, 18, 15, 21, 21, 23, 20,
                21, 19, 16, 19, 22, 15, 19, 24,
                20, 24, 20, 17, 18, 17, 19, 20,
                20, 20, 22, 24, 22, 23, 23, 21,
                22, 20, 17, 21, 16, 18, 18, 25
        };
        double mean = StatUtils.mean(data);
        assertEquals(mean, 19.9, 0.1);
    }

    @Test
    void criticalValue() {
        NormalDistribution distribution = new NormalDistribution();
        double criticalValue = distribution.inverseCumulativeProbability(0.5);
        assertEquals(criticalValue, 0.0);
    }


}
