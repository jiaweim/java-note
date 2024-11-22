package mjw.guava;

import com.google.common.util.concurrent.SettableFuture;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 11 Nov 2024, 2:23 PM
 */
public class FutureCallbackTest {

    @Test
    void testSameThreadSuccess(){
        SettableFuture<String> f = SettableFuture.create();

    }

}
