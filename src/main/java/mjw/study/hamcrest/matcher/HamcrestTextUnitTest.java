package mjw.study.hamcrest.matcher;


import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringContains.containsStringIgnoringCase;
import static org.hamcrest.core.StringEndsWith.endsWithIgnoringCase;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.hamcrest.core.StringStartsWith.startsWithIgnoringCase;
import static org.hamcrest.text.IsBlankString.blankOrNullString;
import static org.hamcrest.text.IsBlankString.blankString;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;
import static org.hamcrest.text.IsEmptyString.emptyString;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.hamcrest.text.StringContainsInOrder.stringContainsInOrder;

public class HamcrestTextUnitTest {

    @Test
    public final void testEqual() {
        String first = "hello";
        String second = "Hello";

        assertThat(first, equalToIgnoringCase(second));
    }

    @Test
    public final void testEqualWithWhiteSpace() {
        String first = "hello";
        String second = "   hello   ";

        assertThat(first, equalToCompressingWhiteSpace(second));
    }

    @Test
    public final void testStringIsBlank() {
        String first = "  ";
        String second = null;

        assertThat(first, is(blankString()));
        assertThat(first, is(blankOrNullString()));
        assertThat(second, is(blankOrNullString()));
    }

    @Test
    public final void testStringIsEmpty() {
        String first = "";
        String second = null;

        assertThat(first, is(emptyString()));
        assertThat(first, is(emptyOrNullString()));
        assertThat(second, is(emptyOrNullString()));
    }

    @Test
    public final void testStringMatchPattern() {
        String first = "hello";

        assertThat(first, matchesPattern("[a-z]+"));
    }

    @Test
    public final void testContains() {
        String first = "hello";

        assertThat(first, containsString("lo"));
        assertThat(first, containsStringIgnoringCase("EL"));
    }

    @Test
    public final void testContainsInOrder() {
        String first = "hello";

        assertThat(first, stringContainsInOrder("e", "l", "o"));
    }

    @Test
    public final void testStartsWith() {
        String first = "hello";

        assertThat(first, startsWith("he"));
        assertThat(first, startsWithIgnoringCase("HEL"));
    }

    @Test
    public final void testEndsWith() {
        String first = "hello";

        assertThat(first, endsWith("lo"));
        assertThat(first, endsWithIgnoringCase("LO"));
    }

}
