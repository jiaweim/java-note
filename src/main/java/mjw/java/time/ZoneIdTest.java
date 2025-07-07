package mjw.java.time;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 07 Jul 2025, 12:41 PM
 */
public class ZoneIdTest {

    @Test
    void testSystem() {
        ZoneId id1 = ZoneId.systemDefault();
        System.out.println(id1);

        ZoneId id2 = TimeZone.getDefault().toZoneId();
        assertEquals(id1, id2);
    }

    @Test
    void testOf() {
        ZoneId zoneId = ZoneId.of("Europe/Berlin");
        System.out.println(zoneId);
        LocalDateTime now = LocalDateTime.now();
        ZoneOffset offset = zoneId.getRules().getOffset(now);
        System.out.println(offset);
    }

}
