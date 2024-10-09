package mjw.dflib;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Oct 2024, 3:46 PM
 */
public class SeriesPositionTest {
    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void locate(SeriesType type) {
        assertEquals(-1, type.createSeries(3, 4, 2).position(null));
        assertEquals(1, type.createSeries(3, 4, 2).position(4));
        assertEquals(-1, type.createSeries(3, 4, 2).position(5));
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void contains(SeriesType type) {
        assertFalse(type.createSeries(3, 4, 2).contains(null));
        assertTrue(type.createSeries(3, 4, 2).contains(4));
        assertFalse(type.createSeries(3, 4, 2).contains(5));
    }
}
