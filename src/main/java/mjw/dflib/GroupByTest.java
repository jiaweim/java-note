package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.GroupBy;
import org.dflib.Hasher;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 30 Sep 2024, 11:00 AM
 */
public class GroupByTest {
    @Test
    public void group_NullKeysIgnored() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                null, "a",
                1, "x");

        GroupBy gb = df.group(Hasher.of("a"));
        assertNotNull(gb);

        assertEquals(2, gb.size());
        assertEquals(new HashSet<>(asList(1, 2)), new HashSet<>(gb.getGroupKeys()));

        new DataFrameAsserts(gb.getGroup(1), "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 1, "z")
                .expectRow(2, 1, "x");

        new DataFrameAsserts(gb.getGroup(2), "a", "b")
                .expectHeight(1)
                .expectRow(0, 2, "y");
    }
}
