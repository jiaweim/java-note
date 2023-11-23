package mjw.jackson.objectmapper;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SerializationDeserializationFeatureUnitTest {

    final String EXAMPLE_JSON = "{ \"color\" : \"Black\", \"type\" : \"BMW\" }";
    final String JSON_ARRAY = "[{ \"color\" : \"Black\", \"type\" : \"BMW\" }, { \"color\" : \"Red\", \"type\" : \"BMW\" }]";

    @Test
    public void testUnkownProperties() throws Exception {
        String JSON_CAR = "{ \"color\" : \"Black\", \"type\" : \"Fiat\", \"year\" : \"1970\" }";

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Car car = objectMapper.readValue(JSON_CAR, Car.class);

        JsonNode jsonNodeRoot = objectMapper.readTree(JSON_CAR);
        JsonNode jsonNodeYear = jsonNodeRoot.get("year");

        assertNotNull(car);
        assertThat(car.getColor(), equalTo("Black"));
        assertThat(jsonNodeYear.asText(), is("1970"));
    }

    @Test
    public void testCustomSerializerDeserializer() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        SimpleModule serializerModule = new SimpleModule("CustomSerializer",
                new Version(1, 0, 0, null, null, null));
        serializerModule.addSerializer(Car.class, new CustomCarSerializer());
        mapper.registerModule(serializerModule);

        Car car = new Car("yellow", "renault");
        String carJson = mapper.writeValueAsString(car);
        assertThat(carJson, containsString("renault"));
        assertThat(carJson, containsString("model"));

        final SimpleModule deserializerModule = new SimpleModule("CustomCarDeserializer", new Version(1, 0, 0, null, null, null));
        deserializerModule.addDeserializer(Car.class, new CustomCarDeserializer());
        mapper.registerModule(deserializerModule);
        final Car carResult = mapper.readValue(EXAMPLE_JSON, Car.class);
        assertNotNull(carResult);
        assertThat(carResult.getColor(), equalTo("Black"));
    }

    @Test
    public void testDateFormatSet() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Car car = new Car("yellow", "renault");
        Request request = new Request();
        request.setCar(car);
        request.setDatePurchased(new Date());

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
        objectMapper.setDateFormat(df);
        String carAsString = objectMapper.writeValueAsString(request);

        assertNotNull(carAsString);
        assertThat(carAsString, containsString("datePurchased"));
    }

    @Test
    public void whenUseJavaArrayForJsonArrayTrue_thanJsonReadAsArray() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
        final Car[] cars = objectMapper.readValue(JSON_ARRAY, Car[].class);
        for (final Car car : cars) {
            assertNotNull(car);
            assertThat(car.getType(), equalTo("BMW"));
        }
    }
}
