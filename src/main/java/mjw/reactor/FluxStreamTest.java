package mjw.reactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FluxStreamTest {

    @Test
    void withStream() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        List<Integer> doubleEvenNumbers = numbers.stream()
                .filter(n -> n % 2 == 0)
                .map(n -> n * n)
                .toList();
        assertEquals(List.of(4, 16), doubleEvenNumbers);
    }

    @Test
    void withFlux() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        Flux<Integer> fluxLine = Flux.fromIterable(numbers)
                .filter(n -> n % 2 == 0)
                .map(n -> n * n);
        StepVerifier.create(fluxLine)
                .expectNext(4, 16);
    }
}
