package mjw.commons.rng;

import org.apache.commons.rng.RestorableUniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Test;

/**
 *
 *
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 08 Sep 2025, 5:36 PM
 */
public class RandomTest {
    @Test
    void create(){
        RestorableUniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();

    }
}
