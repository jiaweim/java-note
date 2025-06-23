package mjw.commons.math;

import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.fitting.GaussianCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 23 Jun 2025, 4:40 PM
 */
public class GaussianFitting {
    protected static final double[][] DATASET1 = new double[][]{
            {4.0254623, 531026.0},
            {4.02804905, 664002.0},
            {4.02934242, 787079.0},
            {4.03128248, 984167.0},
            {4.03386923, 1294546.0},
            {4.03580929, 1560230.0},
            {4.03839603, 1887233.0},
            {4.0396894, 2113240.0},
            {4.04162946, 2375211.0},
            {4.04421621, 2687152.0},
            {4.04550958, 2862644.0},
            {4.04744964, 3078898.0},
            {4.05003639, 3327238.0},
            {4.05132976, 3461228.0},
            {4.05326982, 3580526.0},
            {4.05585657, 3576946.0},
            {4.05779662, 3439750.0},
            {4.06038337, 3220296.0},
            {4.06167674, 3070073.0},
            {4.0636168, 2877648.0},
            {4.06620355, 2595848.0},
            {4.06749692, 2390157.0},
            {4.06943698, 2175960.0},
            {4.07202373, 1895104.0},
            {4.0733171, 1687576.0},
            {4.07525716, 1447024.0},
            {4.0778439, 1130879.0},
            {4.07978396, 904900.0},
            {4.08237071, 717104.0},
            {4.08366408, 620014.0}
    };

    private static WeightedObservedPoints createDataset(double[][] points) {
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = 0; i < points.length; i++) {
            obs.add(points[i][0], points[i][1]);
        }
        return obs;
    }

    @Test
    void testFit() {
        GaussianCurveFitter fitter = GaussianCurveFitter.create();
        double[] parameters = fitter.fit(createDataset(DATASET1).toList());
        assertEquals(3496978.1837704973, parameters[0], 1e-4);
        assertEquals(4.054933085999146, parameters[1], 1e-4);
        assertEquals(0.015039355620304326, parameters[2], 1e-4);
    }

    @Test
    public void testWithMaxIterations1() {
        final int maxIter = 20;
        final double[] init = {3.5e6, 4.2, 0.1};

        GaussianCurveFitter fitter = GaussianCurveFitter.create();
        double[] parameters = fitter
                .withMaxIterations(maxIter)
                .withStartPoint(init)
                .fit(createDataset(DATASET1).toList());

        assertEquals(3496978.1837704973, parameters[0], 1e-2);
        assertEquals(4.054933085999146, parameters[1], 1e-4);
        assertEquals(0.015039355620304326, parameters[2], 1e-4);
    }

    @Test
    public void testWithMaxIterations2() {
        final int maxIter = 1; // Too few iterations.
        final double[] init = {3.5e6, 4.2, 0.1};

        GaussianCurveFitter fitter = GaussianCurveFitter.create().withMaxIterations(maxIter).withStartPoint(init);
        assertThrows(TooManyIterationsException.class, () -> fitter.fit(createDataset(DATASET1).toList()));
    }
}
