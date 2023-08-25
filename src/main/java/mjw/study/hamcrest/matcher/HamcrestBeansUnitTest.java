package mjw.study.hamcrest.matcher;

import org.junit.jupiter.api.Test;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.beans.PropertyUtil.getPropertyDescriptor;
import static org.hamcrest.beans.PropertyUtil.propertyDescriptorsFor;

public class HamcrestBeansUnitTest {

    @Test
    public void testHasProperty() {
        City city = new City("San Francisco", "CA");

        assertThat(city, hasProperty("name"));
    }

    @Test
    public void testNotHasProperty() {
        City city = new City("San Francisco", "CA");

        assertThat(city, not(hasProperty("country")));
    }

    @Test
    public void testPropertyWithValue() {
        City city = new City("San Francisco", "CA");

        assertThat(city, hasProperty("name", equalTo("San Francisco")));
    }

    @Test
    public void testHasPropertyWithValueEqualToIgnoringCase() {
        City city = new City("San Francisco", "CA");

        assertThat(city, hasProperty("state", equalToIgnoringCase("ca")));
    }

    @Test
    public void testSamePropertyValuesAs() {
        City city = new City("San Francisco", "CA");
        City city2 = new City("San Francisco", "CA");

        assertThat(city, samePropertyValuesAs(city2));
    }

    @Test
    public void testNotSamePropertyValuesAs() {
        City city = new City("San Francisco", "CA");
        City city2 = new City("Los Angeles", "CA");

        assertThat(city, not(samePropertyValuesAs(city2)));
    }

    @Test
    public void testGetPropertyDescriptor() {
        City city = new City("San Francisco", "CA");
        PropertyDescriptor descriptor = getPropertyDescriptor("state", city);

        assertThat(descriptor
                .getReadMethod()
                .getName(), is(equalTo("getState")));
    }

    @Test
    public void testGetPropertyDescriptorsFor() {
        City city = new City("San Francisco", "CA");
        PropertyDescriptor[] descriptors = propertyDescriptorsFor(city, Object.class);
        List<String> getters = Arrays
                .stream(descriptors)
                .map(x -> x.getReadMethod().getName())
                .collect(toList());

        assertThat(getters, containsInAnyOrder("getName", "getState"));
    }

}