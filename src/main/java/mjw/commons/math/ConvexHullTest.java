package mjw.commons.math;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.euclidean.twod.hull.ConvexHull2D;
import org.apache.commons.math3.geometry.euclidean.twod.hull.ConvexHullGenerator2D;
import org.apache.commons.math3.geometry.euclidean.twod.hull.MonotoneChain;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 24 Jun 2025, 5:04 PM
 */
public class ConvexHullTest {

    public ConvexHullGenerator2D createMonotoneChain(boolean includeColinearPoints) {
        return new MonotoneChain(includeColinearPoints);
    }

    private ConvexHullGenerator2D monotoneChain;
    private RandomGenerator random;

    @BeforeEach
    public void setup() {
        monotoneChain = new MonotoneChain(false);
        random = new MersenneTwister(10);
    }

    @Test
    void testEmpty() {
        ConvexHull2D hull = monotoneChain.generate(Collections.emptyList());
        assertEquals(0, hull.getVertices().length);
        assertEquals(0, hull.getLineSegments().length);
    }

    @Test
    void testOnePoint() {
        List<Vector2D> points = createRandomPoints(1);
        ConvexHull2D hull = monotoneChain.generate(points);
        assertEquals(1, hull.getVertices().length);
        assertEquals(0, hull.getLineSegments().length);
    }

    @Test
    void testTwoPoints() {
        List<Vector2D> points = createRandomPoints(2);
        ConvexHull2D hull = monotoneChain.generate(points);
        assertEquals(2, hull.getVertices().length);
        assertEquals(1, hull.getLineSegments().length);
    }

    @Test
    void testIdentical() {
        List<Vector2D> points = new ArrayList<>();
        points.add(new Vector2D(1, 1));
        points.add(new Vector2D(1, 1));
        points.add(new Vector2D(1, 1));
        points.add(new Vector2D(1, 1));

        // identical points are merged according specified tolerance
        ConvexHull2D hull = monotoneChain.generate(points);
        assertEquals(1, hull.getVertices().length);
    }

    @Test
    void testHull() {

        for (int i = 0; i < 100; i++) {
            // random size
            int size = (int) FastMath.floor(random.nextDouble() * 96.0 + 4.0);
            List<Vector2D> points = createRandomPoints(size);
            ConvexHull2D hull = monotoneChain.generate(points);

        }
    }


    @Test
    void testMonotoneChain() {
        ConvexHullGenerator2D generator = createMonotoneChain(false);
        RandomGenerator randomGenerator = new MersenneTwister(10);


    }

    protected final List<Vector2D> createRandomPoints(int size) {
        List<Vector2D> points = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            points.add(new Vector2D(random.nextDouble() * 2.0 - 1.0, random.nextDouble() * 2.0 - 1.0));
        }
        return points;
    }

}
