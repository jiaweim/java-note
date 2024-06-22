package mjw.hipparchus;

import org.hipparchus.distribution.continuous.BetaDistribution;
import org.junit.jupiter.api.Test;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 15 May 2024, 2:10 PM
 */
public class BetaDistributionTest {

    @Test
    void test(){
        BetaDistribution distribution = new BetaDistribution(10,10);
        double density = distribution.density(0);
        System.out.println(density);
    }
}
