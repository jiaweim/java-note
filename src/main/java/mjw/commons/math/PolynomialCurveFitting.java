package mjw.commons.math;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.SimpleCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 23 Jun 2025, 5:20 PM
 */
public class PolynomialCurveFitting {
    @Test
    void test() {
        final RealDistribution rng = new UniformRealDistribution(-100, 100);
        rng.reseedRandomGenerator(64925784252L);

        final double[] coeff = {12.9, -3.4, 2.1}; // 12.9 - 3.4 x + 2.1 x^2
        final PolynomialFunction f = new PolynomialFunction(coeff);

        // Collect data from a known polynomial.
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = 0; i < 100; i++) {
            final double x = rng.sample();
            obs.add(x, f.value(x));
        }

        // Start fit from initial guesses that are far from the optimal values.
        final PolynomialCurveFitter fitter
                = PolynomialCurveFitter.create(0).withStartPoint(new double[]{-1e-20, 3e15, -5e25});
        final double[] best = fitter.fit(obs.toList());

        assertArrayEquals(coeff, best, 1E-12);
    }

    @Test
    public void testPolynomialFit() {
        final Random randomizer = new Random(53882150042L);
        final RealDistribution rng = new UniformRealDistribution(-100, 100);
        rng.reseedRandomGenerator(64925784252L);

        final double[] coeff = {12.9, -3.4, 2.1}; // 12.9 - 3.4 x + 2.1 x^2
        final PolynomialFunction f = new PolynomialFunction(coeff);

        // Collect data from a known polynomial.
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = 0; i < 100; i++) {
            final double x = rng.sample();
            obs.add(x, f.value(x) + 0.1 * randomizer.nextGaussian());
        }

        final ParametricUnivariateFunction function = new PolynomialFunction.Parametric();
        // Start fit from initial guesses that are far from the optimal values.
        final SimpleCurveFitter fitter
                = SimpleCurveFitter.create(function,
                new double[]{-1e20, 3e15, -5e25});
        final double[] best = fitter.fit(obs.toList());

        assertArrayEquals(coeff, best, 2e-2);
    }
}
