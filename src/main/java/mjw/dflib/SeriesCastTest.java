package mjw.dflib;

import org.dflib.Series;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Oct 2024, 2:33 PM
 */
public class SeriesCastTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void unsafeCastAs(SeriesType type) {
        Series<?> s = type.createSeries("s1", "s2");
        assertDoesNotThrow(() -> s.unsafeCastAs(String.class));
        assertDoesNotThrow(() -> s.unsafeCastAs(Integer.class));
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void castAs(SeriesType type) {
        Series<?> s = type.createSeries("s1", "s2");
        assertDoesNotThrow(() -> s.castAs(String.class));
        assertThrows(ClassCastException.class, () -> s.castAs(Integer.class));
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void castAs_Upcast(SeriesType type) {
        Series<?> s = type.createSeries(1, 2);
        assertDoesNotThrow(() -> s.castAs(Integer.class));
        assertDoesNotThrow(() -> s.castAs(Number.class));
        assertThrows(ClassCastException.class, () -> s.castAs(Long.class));
    }
}
