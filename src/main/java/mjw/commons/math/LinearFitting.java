package mjw.commons.math;

import org.apache.commons.math3.stat.regression.RegressionResults;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 23 Jun 2025, 10:37 AM
 */
public class LinearFitting {

    public static double[][] generateData() {
        List<double[]> data = new ArrayList<>();
        for (double x = 0; x <= 10; x += 0.1) {
            double y = 1.5 * x + 0.5; // y=1.5*x+0.5
            y += Math.random() * 2; // add noise
            data.add(new double[]{x, y});
        }
        return data.toArray(double[][]::new);
    }

    public static void fit(double[][] data) {
        SimpleRegression regression = new SimpleRegression();
        regression.addData(data);
        RegressionResults results = regression.regress();

        double b = results.getParameterEstimate(0);


    }

    /*
     * NIST "Norris" refernce data set from
     * http://www.itl.nist.gov/div898/strd/lls/data/LINKS/DATA/Norris.dat
     * Strangely, order is {y,x}
     */
    private double[][] data = {{0.1, 0.2}, {338.8, 337.4}, {118.1, 118.2},
            {888.0, 884.6}, {9.2, 10.1}, {228.1, 226.5}, {668.5, 666.3}, {998.5, 996.3},
            {449.1, 448.6}, {778.9, 777.0}, {559.2, 558.2}, {0.3, 0.4}, {0.1, 0.6}, {778.1, 775.5},
            {668.8, 666.9}, {339.3, 338.0}, {448.9, 447.5}, {10.8, 11.6}, {557.7, 556.0},
            {228.3, 228.1}, {998.0, 995.8}, {888.8, 887.6}, {119.6, 120.2}, {0.3, 0.3},
            {0.6, 0.3}, {557.6, 556.8}, {339.3, 339.1}, {888.0, 887.2}, {998.5, 999.0},
            {778.9, 779.0}, {10.2, 11.1}, {117.6, 118.3}, {228.9, 229.2}, {668.4, 669.1},
            {449.2, 448.9}, {0.2, 0.5}
    };

    @Test
    void testNorris() {
        assertEquals(36, data.length);
        SimpleRegression regression = new SimpleRegression();
        for (double[] datum : data) {
            regression.addData(datum[1], datum[0]);
        }
        assertEquals(1.00211681802045, regression.getSlope(), 1E-11, "slope");
        assertEquals(0.429796848199937E-03, regression.getSlopeStdErr(), 1E-11, "slope std err");
        assertEquals(36, regression.getN(), "number of observations");
        assertEquals(-0.262323073774029, regression.getIntercept(), 1E-11, "intercept");
        assertEquals(0.232818234301152, regression.getInterceptStdErr(), 1E-11, "std err intercept");
        assertEquals(0.999993745883712, regression.getRSquare(), 1E-11, "r-square");
        assertEquals(4255954.13232369, regression.getRegressionSumSquares(), 1E-8, "SSR");
        assertEquals(0.782864662630069, regression.getMeanSquareError(), 1E-9, "MSE");
        assertEquals(26.6173985294224, regression.getSumSquaredErrors(), 1E-8, "SSE");
    }

    public static void main(String[] args) {
        double[][] doubles = generateData();
        for (double[] aDouble : doubles) {
            System.out.println(aDouble[0] + "\t" + aDouble[1]);
        }

    }
}
