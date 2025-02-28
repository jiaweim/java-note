package mjw.hipparchus.optim;


import org.hipparchus.optim.SimpleVectorValueChecker;
import org.hipparchus.optim.nonlinear.vector.leastsquares.LeastSquaresBuilder;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 10 Feb 2025, 2:33 PM
 */
public class LevenbergMarquardtOptimizerTest {

    public static final double TOl = 1e-10;

    public LeastSquaresBuilder base() {
        return new LeastSquaresBuilder()
                .checkerPair(new SimpleVectorValueChecker(1e-6, 1e-6))
                .maxEvaluations(100)
                .maxIterations(getMaxIterations());
    }


    public int getMaxIterations() {
        return 25;
    }
}
