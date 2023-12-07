package mjw.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 05 Dec 2023, 1:39 PM
 */
public class GsonTest {

    @Test
    void primitives() {
        Gson gson = new Gson();
        assertEquals(gson.toJson(1), "1");
        assertEquals(gson.toJson("abcd"), "\"abcd\"");
        assertEquals(gson.toJson(Long.valueOf(10)), "10");
        assertEquals(gson.toJson(new int[]{1}), "[1]");

        assertEquals(gson.fromJson("1", int.class), 1);
        assertEquals(gson.fromJson("1", Integer.class), Integer.valueOf(1));
        assertEquals(gson.fromJson("1", Long.class), Long.valueOf(1));
        assertEquals(gson.fromJson("false", Boolean.class), Boolean.FALSE);
        assertEquals(gson.fromJson("\"abc\"", String.class), "abc");
        assertArrayEquals(gson.fromJson("[\"abc\"]", String[].class), new String[]{"abc"});
    }

    class BagOfPrimitives {

        private int value1 = 1;
        private String value2 = "abc";
        private transient int value3 = 3;

        BagOfPrimitives() {
            // no-args constructor
        }
    }

    @Test
    void object() {
        BagOfPrimitives obj = new BagOfPrimitives();
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        assertEquals(json, "{\"value1\":1,\"value2\":\"abc\"}");

        BagOfPrimitives obj2 = gson.fromJson(json, BagOfPrimitives.class);
    }

    @Test
    void array() {
        Gson gson = new Gson();
        int[] ints = {1, 2, 3, 4, 5};
        String[] strings = {"abc", "def", "ghi"};
        assertEquals(gson.toJson(ints), "[1,2,3,4,5]");
        assertEquals(gson.toJson(strings), "[\"abc\",\"def\",\"ghi\"]");
    }

    @Test
    void collect() {
        Gson gson = new Gson();
        Collection<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);

        String json = gson.toJson(ints);
        assertEquals(json, "[1,2,3,4,5]");

        TypeToken<Collection<Integer>> collectionType = new TypeToken<>() {
        };
        Collection<Integer> ints2 = gson.fromJson(json, collectionType);
        assertIterableEquals(ints2, ints);
    }

    @Test
    void map() {
        Gson gson = new Gson();
        Map<String, String> stringMap = new LinkedHashMap<>();
        stringMap.put("key", "value");
        stringMap.put(null, "null-entry");

        String json = gson.toJson(stringMap);
        // {"key":"value","null":"null-entry"}
        assertEquals(json, "{\"key\":\"value\",\"null\":\"null-entry\"}");

        Map<Integer, Integer> intMap = new LinkedHashMap<>();
        intMap.put(2, 4);
        intMap.put(3, 6);
        // {"2":4,"3":6}
        String json1 = gson.toJson(intMap);
        assertEquals(json1, "{\"2\":4,\"3\":6}");
    }

    @Test
    void mapDeserialization() {
        Gson gson = new Gson();
        TypeToken<Map<String, String>> mapType = new TypeToken<Map<String, String>>() {
        };
        String json = "{\"key\": \"value\"}";

        Map<String, String> stringMap = gson.fromJson(json, mapType);
        // ==> stringMap is {key=value}
    }

    class PersonName {

        String firstName;
        String lastName;

        PersonName(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        // ... equals and hashCode
    }

    @Test
    void map2() {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        Map<PersonName, Integer> complexMap = new LinkedHashMap<>();
        complexMap.put(new PersonName("John", "Doe"), 30);
        complexMap.put(new PersonName("Jane", "Doe"), 35);


        String json = gson.toJson(complexMap);
        // [[{"firstName":"John","lastName":"Doe"},30],[{"firstName":"Jane","lastName":"Doe"},35]]

        Map<String, String> stringMap = new LinkedHashMap<>();
        stringMap.put("key", "value");
        String json2 = gson.toJson(stringMap);
        // {"key":"value"}
    }

    static class Event {

        private String name;
        private String source;

        private Event(String name, String source) {
            this.name = name;
            this.source = source;
        }

        @Override
        public String toString() {
            return String.format("(name=%s, source=%s)", name, source);
        }
    }

    @Test
    void rawCollect() {
        Gson gson = new Gson();
        Collection collection = new ArrayList();
        collection.add("hello");
        collection.add(5);
        collection.add(new Event("GREETINGS", "guest"));
        String json = gson.toJson(collection);
        System.out.println("Using Gson.toJson() on a raw collection: " + json);
        JsonArray array = JsonParser.parseString(json).getAsJsonArray();
        String message = gson.fromJson(array.get(0), String.class);
        int number = gson.fromJson(array.get(1), int.class);
        Event event = gson.fromJson(array.get(2), Event.class);
        System.out.printf("Using Gson.fromJson() to get: %s, %d, %s", message, number, event);
    }


}
