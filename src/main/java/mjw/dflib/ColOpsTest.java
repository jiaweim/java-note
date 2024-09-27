package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Printers;
import org.dflib.RowMapper;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.dflib.Exp.*;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 27 Sep 2024, 2:29 PM
 */
public class ColOpsTest {
    @Test
    void transformCol() {
        DataFrame df = DataFrame.foldByRow("first", "last", "middle").of(
                "Jerry", "Cosin", "M",
                "Joan", "O'Hara", null);
        Exp fmExp = concat(
                $str("first"),
                ifNull($str("middle").mapVal(s -> " " + s), ""));
        DataFrame df1 = df.cols("first_middle", "last")
                .select(fmExp, $str("last"));
        System.out.println(Printers.tabular.toString(df1));

        RowMapper mapper = (from, to) -> {
            String middle = from.get("middle") != null
                    ? " " + from.get("middle")
                    : "";
            to.set(0, from.get("first") + middle).set(1, from.get("last"));
        };

        DataFrame df2 = df
                .cols("first_middle", "last")
                .select(mapper);
        System.out.println(Printers.tabular.toString(df2));
    }

    @Test
    void merge() {
        DataFrame df = DataFrame.foldByRow("first", "last", "middle").of(
                "jerry", "cosin", "M",
                "Joan", "O'Hara", null);

        Function<String, Exp<String>> cleanup = col -> $str(col).mapVal(
                s -> s != null && !s.isEmpty()
                        ? Character.toUpperCase(s.charAt(0)) + s.substring(1)
                        : null); // 实现首字母大写

        DataFrame df1 = df
                .cols("last", "first", "full") // 要选择或生成的 cols
                .merge( // 不使用 select，而是用 merge 将 col 与原 DataFrame 合并
                        cleanup.apply("last"),
                        cleanup.apply("first"),
                        concat($str("first"), $val(" "), $str("last"))
                );
    }
}
