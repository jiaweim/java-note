package mjw.study.jackson.objectmapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class JavaReadWriteJsonExampleUnitTest {

    final String EXAMPLE_JSON = "{ \"color\" : \"Black\", \"type\" : \"BMW\" }";


    @Test
    public void whenWriteJavaToJson_thanCorrect() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Car car = new Car("yellow", "renault");
        String carAsString = objectMapper.writeValueAsString(car);

        assertThat(carAsString, containsString("yellow"));
        assertThat(carAsString, containsString("renault"));
    }

    @Test
    public void testWriteToFile(@TempDir Path folder) throws Exception {
        File resultFile = folder.resolve("car.json").toFile();

        ObjectMapper objectMapper = new ObjectMapper();
        Car car = new Car("yellow", "renault");
        objectMapper.writeValue(resultFile, car);

        Car fromFile = objectMapper.readValue(resultFile, Car.class);
        assertThat(car.getType(), is(fromFile.getType()));
        assertThat(car.getColor(), is(fromFile.getColor()));
    }

    @Test
    public void whenReadJsonToJava_thanCorrect() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Car car = objectMapper.readValue(EXAMPLE_JSON, Car.class);

        assertThat(car, is(notNullValue()));
        assertThat(car.getColor(), is("Black"));
    }

    @Test
    public void testReadTree() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(EXAMPLE_JSON);
        assertThat(jsonNode, is(notNullValue()));
        assertThat(jsonNode.get("color").asText(), is("Black"));
    }

    @Test
    void testReadJsonToList() throws Exception {
        String LOCAL_JSON = "[{ \"color\" : \"Black\", \"type\" : \"BMW\" }, { \"color\" : \"Red\", \"type\" : \"BMW\" }]";
        ObjectMapper objectMapper = new ObjectMapper();
        List<Car> listCar = objectMapper.readValue(LOCAL_JSON, new TypeReference<List<Car>>() {

        });
        for (final Car car : listCar) {
            assertThat(car, notNullValue());
            assertThat(car.getType(), equalTo("BMW"));
        }
    }

    @Test
    public void testReadJsonToMap() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Map<String, Object> map = objectMapper.readValue(EXAMPLE_JSON, new TypeReference<Map<String, Object>>() {
        });
        assertThat(map, is(notNullValue()));
        for (final String key : map.keySet()) {
            assertThat(key, is(notNullValue()));
        }
    }

    @Test
    public void testReadValueFromFile() throws Exception {
        File resource = new File("src/main/resources/json_car.json");

        ObjectMapper objectMapper = new ObjectMapper();
        Car fromFile = objectMapper.readValue(resource, Car.class);

        assertThat("BMW", is(fromFile.getType()));
        assertThat("Black", is(fromFile.getColor()));
    }

    @Test
    public void testReadFromUrl() throws Exception {
        URL resource = new URL("file:src/main/resources/json_car.json");

        ObjectMapper objectMapper = new ObjectMapper();
        Car fromFile = objectMapper.readValue(resource, Car.class);

        assertThat("BMW", is(fromFile.getType()));
        assertThat("Black", is(fromFile.getColor()));
    }
}
