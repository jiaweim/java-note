package mjw.reactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 25 Mar 2025, 9:28 AM
 */
public class ReactorSnippets {

    private static List<String> words = Arrays.asList(
            "the",
            "quick",
            "brown",
            "fox",
            "jumped",
            "over",
            "the",
            "lazy",
            "dog"
    );

    @Test
    void simpleCreation() {
        Flux<String> fewWords = Flux.just("Hello", "World");
        Flux<String> manyWord = Flux.fromIterable(words);

        fewWords.subscribe(System.out::println);
        System.out.println();
        manyWord.subscribe(System.out::println);
    }

    @Test
    void findingMissingLetter() {
        Flux<String> manyLetters = Flux.fromIterable(words)
                .flatMap(s -> Flux.fromArray(s.split("")))
                .distinct()
                .sort()
                .zipWith(Flux.range(1, Integer.MAX_VALUE),
                        (s, count) -> String.format("%2d. %s", count, s));
        manyLetters.subscribe(System.out::println);
    }

    @Test
    void restoringMissingLetter() {
        Mono<String> missing = Mono.just("s");
        Flux<String> allLetters = Flux.fromIterable(words)
                .flatMap(s -> Flux.fromArray(s.split("")))
                .concatWith(missing)
                .distinct()
                .sort()
                .zipWith(Flux.range(1, Integer.MAX_VALUE)
                        , (s, count) -> String.format("%2d. %s", count, s));
        allLetters.subscribe(System.out::println);
    }

    @Test
    void shortCircuit() {
        Flux<String> helloPauseWorld = Mono.just("Hello")
                .concatWith(Mono.just("world")
                        .delaySubscription(Duration.ofMillis(500)));
        helloPauseWorld.subscribe(System.out::println);
    }

    @Test
    void blocks() {
        Flux<String> helloPauseWorld = Mono.just("Hello")
                .concatWith(Mono.just("world")
                        .delaySubscription(Duration.ofMillis(500)));
        helloPauseWorld.toStream()
                .forEach(System.out::println);
    }

    @Test
    void firstEmitting() {
        Mono<String> a = Mono.just("oops I'm late")
                .delaySubscription(Duration.ofMillis(450));
        Flux<String> b = Flux.just("let's get", "the party", "started")
                .delayElements(Duration.ofMillis(400));
        Flux.firstWithValue(a, b)
                .toIterable()
                .forEach(System.out::println);
    }


    public Flux<String> alphabet5(char from) {
        return Flux.range((int) from, 5)
                .map(i -> "" + (char) i.intValue());
    }

    public Mono<String> withDelay(String value, int delaySeconds) {
        return Mono.just(value)
                .delaySubscription(Duration.ofSeconds(delaySeconds));
    }

    @Test
    void testAlphabet5LimitsToZ() {
        StepVerifier.create(alphabet5('x'))
                .expectNext("x", "y", "z")
                .verifyComplete();
    }

    @Test
    void testAlphabet5LastItemIsAlphabeticalChar() {
        StepVerifier.create(alphabet5('x'))
                .consumeNextWith(s -> assertTrue(s.matches("[a-z]"), "first is alphabetic"))
                .consumeNextWith(s -> assertTrue(s.matches("[a-z]"), "second is alphabetic"))
                .consumeNextWith(s -> assertTrue(s.matches("[a-z]"), "third is alphabetic"))
                .consumeNextWith(s -> assertTrue(s.matches("[a-z]"), "fourth is alphabetic"))
                .verifyComplete();

    }

    @Test
    void testWithDelay() {
        Duration duration = StepVerifier.withVirtualTime(() -> withDelay("foo", 30))
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(10))
                .expectNoEvent(Duration.ofSeconds(10))
                .thenAwait(Duration.ofSeconds(10))
                .expectNext("foo")
                .verifyComplete();
        System.out.println(duration.toMillis() + "ms");

    }
}
