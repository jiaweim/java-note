package mjw.study.hamcrest.matcher;


import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class HamcrestObjectUnitTest {

    @Test
    public void testHasToString() {
        City city = new City("San Francisco", "CA");

        assertThat(city, hasToString("[Name: San Francisco, State: CA]"));
    }

    @Test
    public void testHasToStringEqualToIgnoringCase() {
        City city = new City("San Francisco", "CA");

        assertThat(city, hasToString(equalToIgnoringCase("[NAME: SAN FRANCISCO, STATE: CA]")));
    }

    @Test
    public void testHasToStringEmptyOrNullString() {
        City city = new City(null, null);

        assertThat(city, hasToString(emptyOrNullString()));
    }

    @Test
    public void testTypeCompatibleWith() {
        City city = new City("San Francisco", "CA");

        assertThat(city.getClass(), is(typeCompatibleWith(Location.class)));
    }

    @Test
    public void testTypeNotCompatibleWith() {
        City city = new City("San Francisco", "CA");

        assertThat(city.getClass(), is(not(typeCompatibleWith(String.class))));
    }

    @Test
    public void testTypeCompatibleWithObject() {
        City city = new City("San Francisco", "CA");

        assertThat(city.getClass(), is(typeCompatibleWith(Object.class)));
    }

}
