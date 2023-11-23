package mjw.hamcrest.matcher;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class HamcrestExamplesUnitTest {

    @Test
    public final void testSingleElement() {
        final List<String> collection = Lists.newArrayList("ab", "cd", "ef");
        assertThat(collection, hasItem("cd"));
        assertThat(collection, not(hasItem("zz")));
    }

    @Test
    public final void testMultipleElements() {
        final List<String> collection = Lists.newArrayList("ab", "cd", "ef");
        assertThat(collection, hasItems("ef", "cd"));
    }

    @Test
    public final void testContains() {
        final List<String> collection = Lists.newArrayList("ab", "cd", "ef");
        assertThat(collection, contains("ab", "cd", "ef"));
    }

    @Test
    public final void testContainsInAnyOrder() {
        final List<String> collection = Lists.newArrayList("ab", "cd", "ef");
        assertThat(collection, containsInAnyOrder("cd", "ab", "ef"));
    }

    @Test
    public final void testCollectionIsEmpty() {
        final List<String> collection = Lists.newArrayList();
        assertThat(collection, is(empty()));
    }

    @Test
    public final void testIterableIsEmpty() {
        final Iterable<String> collection = Lists.newArrayList();
        assertThat(collection, is(emptyIterable()));
    }

    @Test
    public final void givenCollectionIsNotEmpty_whenChecking_thenNotEmpty() {
        final List<String> collection = Lists.newArrayList("a");
        assertThat(collection, not(empty()));
    }

    @Test
    public final void testEmptyMap() {
        final Map<String, String> collection = Maps.newHashMap();
        assertThat(collection, equalTo(Collections.EMPTY_MAP));
    }

    @Test
    public final void testEmptyArray() {
        final String[] array = new String[]{"ab"};
        assertThat(array, not(emptyArray()));
    }

    @Test
    public final void testCollectionSize() {
        final List<String> collection = Lists.newArrayList("ab", "cd", "ef");
        assertThat(collection, hasSize(3));
    }

    @Test
    public final void testIterableSize() {
        final Iterable<String> collection = Lists.newArrayList("ab", "cd", "ef");
        assertThat(collection, iterableWithSize(3));
    }

    @Test
    public final void testEachItem() {
        final List<Integer> collection = Lists.newArrayList(15, 20, 25, 30);
        assertThat(collection, everyItem(greaterThan(10)));
    }

    @Test
    void testHasKey() {
        Map<String, String> myMap = new HashMap<>();
        myMap.put("bar", "a");
        assertThat(myMap, hasKey("bar"));
    }

}
