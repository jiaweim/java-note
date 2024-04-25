package mjw.hipparchus;

import org.hipparchus.stat.correlation.PearsonsCorrelation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CorrelationTest {

    void data() {
        double[] xs = new double[]{
                78.4, 75.0, 153.7, 218.7, 176.1, 113.4, 77.3, 94.5, 89.7, 199.9,
                89.5, 125.1, 139.7, 231.3, 72.5, 52.1, 93.3, 155.2, 193.2, 55.0,
                84.8, 81.2, 50.7, 137.2, 177.0, 150.4, 48.2, 212.1, 182.7, 153.0
        };
        double[] ys = new double[]{
                25.138, 24.950, 26.819, 36.487, 39.906, 21.559, 23.384, 19.650, 32.130, 31.173,
                28.477, 31.577, 37.236, 45.720, 21.405, 28.575, 24.246, 34.440, 37.820, 18.784,
                23.644, 27.768, 29.030, 27.999, 41.546, 42.525, 15.879, 33.462, 41.878, 30.641
        };

    }

    @Test
    void Correlation() {
        double[] xs = new double[]{
                1.8, 1.3, 2.4, 1.5, 3.9, 2.1, 0.9, 1.4, 3, 4.6
        };
        double[] ys = new double[]{
                604.4, 434.2, 544, 370.4, 742.3, 340.5, 232, 262.3, 441.9, 1157.7
        };
        double r = new PearsonsCorrelation().correlation(xs, ys);
        assertEquals(r, 0.874, 0.001);
        System.out.println(r);
    }

    public static void main(String[] args) {
        double[] xs = new double[]{
                1.8,
                1.3,
                2.4,
                1.5,
                3.9,
                2.1,
                0.9,
                1.4,
                3,
                4.6
        };
        double[] ys = new double[]{
                604.4,
                434.2,
                544,
                370.4,
                742.3,
                340.5,
                232,
                262.3,
                441.9,
                1157.7
        };
        double r = new PearsonsCorrelation().correlation(xs, ys);
        System.out.println(r);
    }
}
