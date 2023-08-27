package mjw.study.hamcrest.matcher;


import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;

public class HamcrestCoreMatchersUnitTest {

    @Test
    public void testIsForMatch() {

        // GIVEN
        String testString = "hamcrest core";

        // ASSERT
        assertThat(testString, is("hamcrest core"));
        assertThat(testString, is(equalTo("hamcrest core")));
    }

    @Test
    void testEqualTo() {
        String actualString = "equalTo match";
        List<String> actualList = Lists.newArrayList("equalTo", "match");

        assertThat(actualString, is(equalTo("equalTo match")));
        assertThat(actualList, is(equalTo(Lists.newArrayList("equalTo", "match"))));
    }

    @Test
    public void testDifferentStaticTypeUsingEqualToObject() {

        // GIVEN
        Object original = 100;

        // ASSERT
        assertThat(original, equalToObject(100));
    }

    @Test
    public void testInstanceOf() {

        assertThat("hamcrest", is(instanceOf(String.class)));
    }

    @Test
    public void testIsA() {

        assertThat("hamcrest core", isA(String.class));
    }

    @Test
    public void givenTestInput_WhenUsingEqualToMatcherForEquality() {

        // GIVEN
        String actualString = "Hamcrest Core";
        List<String> actualList = Lists.newArrayList("hamcrest", "core");

        // ASSERT
        assertThat(actualString, is(equalTo("Hamcrest Core")));
        assertThat(actualList, is(equalTo(Lists.newArrayList("hamcrest", "core"))));
    }

    @Test
    public void testNot() {

        String testString = "hamcrest";

        assertThat(testString, not("hamcrest core"));
        assertThat(testString, is(not(equalTo("hamcrest core"))));
        assertThat(testString, is(not(instanceOf(Integer.class))));
    }

    @Test
    public void testNullValue() {

        // GIVEN
        Integer nullObject = null;

        // ASSERT
        assertThat(nullObject, is(nullValue()));
        assertThat(nullObject, is(nullValue(Integer.class)));
    }

    @Test
    public void testNotNull() {

        // GIVEN
        Integer testNumber = 123;

        // ASSERT
        assertThat(testNumber, is(notNullValue()));
        assertThat(testNumber, is(notNullValue(Integer.class)));
    }

    @Test
    public void givenString_WhenStartsWith_ThenCorrect() {

        // GIVEN
        String testString = "hamcrest core";

        // ASSERT
        assertThat(testString, startsWith("hamcrest"));
    }

    @Test
    public void giveString_WhenStartsWithIgnoringCase_ThenCorrect() {

        // GIVEN
        String testString = "hamcrest core";

        // ASSERT
        assertThat(testString, startsWithIgnoringCase("HAMCREST"));
    }

    @Test
    public void givenString_WhenEndsWith_ThenCorrect() {

        // GIVEN
        String testString = "hamcrest core";

        // ASSERT
        assertThat(testString, endsWith("core"));
    }

    @Test
    public void givenString_WhenEndsWithIgnoringCase_ThenCorrect() {

        // GIVEN
        String testString = "hamcrest core";

        // ASSERT
        assertThat(testString, endsWithIgnoringCase("CORE"));
    }

    @Test
    public void givenString_WhenContainsString_ThenCorrect() {

        // GIVEN
        String testString = "hamcrest core";

        // ASSERT
        assertThat(testString, containsString("co"));
    }

    @Test
    public void givenString_WhenContainsStringIgnoringCase_ThenCorrect() {

        // GIVEN
        String testString = "hamcrest core";

        // ASSERT
        assertThat(testString, containsStringIgnoringCase("CO"));
    }

    @Test
    public void testHasItem() {

        List<String> list = Lists.newArrayList("java", "spring", "baeldung");

        assertThat(list, hasItem("java"));
        assertThat(list, hasItem(isA(String.class)));
    }

    @Test
    public void testHasItems() {

        List<String> list = Lists.newArrayList("java", "spring", "baeldung");

        assertThat(list, hasItems("java", "baeldung"));
        assertThat(list, hasItems(isA(String.class), endsWith("ing")));
    }

    @Test
    public void testAny() {

        assertThat("hamcrest", is(any(String.class)));
        assertThat("hamcrest", is(any(Object.class)));
    }

    @Test
    public void testAllOf() {

        String testString = "Hamcrest Core";

        assertThat(testString, allOf(startsWith("Ham"), endsWith("ore"), containsString("Core")));
    }

    @Test
    public void testAnyOf() {

        String testString = "Hamcrest Core";

        assertThat(testString, anyOf(startsWith("Ham"), containsString("baeldung")));
    }

    @Test
    public void testBoth() {

        String testString = "Hamcrest Core Matchers";

        assertThat(testString, both(startsWith("Ham")).and(containsString("Core")));
    }

    @Test
    public void testEither() {

        String testString = "Hamcrest Core Matchers";

        assertThat(testString, either(startsWith("Bael")).or(containsString("Core")));
    }

    @Test
    public void givenTestInput_WhenUsingEveryItemForMatchInCollection() {

        // GIVEN
        List<String> testItems = Lists.newArrayList("Common", "Core", "Combinable");

        // ASSERT
        assertThat(testItems, everyItem(startsWith("Co")));
    }

    @Test
    public void testSameInstance() {

        String string1 = "hamcrest";
        String string2 = string1;

        assertThat(string1, is(sameInstance(string2)));
    }

    @Test
    public void givenTwoTestInputs_WhenUsingTheInstanceForMatch() {
        // GIVEN
        String string1 = "hamcrest";
        String string2 = string1;

        // ASSERT
        assertThat(string1, is(theInstance(string2)));
    }

}

