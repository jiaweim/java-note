package mjw.java.time;

import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 19 Dec 2023, 00:01
 */
public class LocalDateTest {

    @Test
    void createTest() {
        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.of(1903, 6, 14);
        birthday = LocalDate.of(1903, Month.JUNE, 14);
    }

    @Test
    void plus() {
        LocalDate programmersDay = LocalDate.of(2014, 1, 1)
                .plusDays(255);
    }

    @Test
    void testPlus() {
        LocalDate localDate = LocalDate.of(2016, 1, 31).plusMonths(1);
        System.out.println(localDate);
    }

    @Test
    void testMinus() {
        LocalDate localDate = LocalDate.of(2016, 3, 31).minusMonths(1);
        System.out.println(localDate);
        localDate.getDayOfWeek();
    }

    @Test
    void testStream() {
        LocalDate start = LocalDate.of(2000, 1, 1);
        LocalDate endExclusive = LocalDate.now();
        Stream<LocalDate> allDays = start.datesUntil(endExclusive);
        Stream<LocalDate> firstDaysInMonth = start.datesUntil(endExclusive, Period.ofMonths(1));
    }

    @Test
    void testLocalDate() {
        LocalDate today = LocalDate.now();
        System.out.println("today:" + today);

        LocalDate birthday = LocalDate.of(1903, 6, 14);
        birthday = LocalDate.of(1903, Month.JUNE, 14);
        System.out.println("birthday:" + birthday);

        LocalDate programmersDay = LocalDate.of(2018, 1, 1).plusDays(255);
        System.out.println("programmers day:" + programmersDay);

        LocalDate independenceDay = LocalDate.of(2018, Month.JULY, 4);
        LocalDate christmas = LocalDate.of(2018, Month.DECEMBER, 25);

        System.out.println("Until christmas: " + independenceDay.until(christmas));
        System.out.println("Until christmas: " + independenceDay.until(christmas, ChronoUnit.DAYS));

        System.out.println(LocalDate.of(2016, 1, 31).plusMonths(1));
        System.out.println(LocalDate.of(2016, 3, 31).minusMonths(1));

        DayOfWeek startOfLastMillennium = LocalDate.of(1900, 1, 1).getDayOfWeek();
        System.out.println("startOfLastMillennium: " + startOfLastMillennium);
        System.out.println(startOfLastMillennium.getValue());
        System.out.println(DayOfWeek.SATURDAY.plus(3));

        LocalDate start = LocalDate.of(2000, 1, 1);
        LocalDate endExclusive = LocalDate.now();
        Stream<LocalDate> firstDaysInMonth = start.datesUntil(endExclusive, Period.ofMonths(1));
        System.out.println("firstDaysInMonth:" + firstDaysInMonth.toList());
    }

    @Test
    void adjust() {
        ZonedDateTime ambiguous = ZonedDateTime.of(
                LocalDate.of(2013, 10, 27), // End of daylight savings time
                LocalTime.of(2, 30),
                ZoneId.of("Europe/Berlin"));
        // 2013-10-27T02:30+02:00[Europe/Berlin]
        ZonedDateTime anHourLater = ambiguous.plusHours(1);
        // 2013-10-27T02:30+01:00[Europe/Berlin]
        System.out.println(ambiguous);
        System.out.println(anHourLater);
    }
}
