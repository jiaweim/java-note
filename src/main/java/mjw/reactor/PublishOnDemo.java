package mjw.reactor;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 19 Mar 2025, 11:03 AM
 */
public class PublishOnDemo {
    public static void main(String[] args) {
        Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);

        final Flux<String> flux = Flux
                .range(1, 2)
                .map(i -> 10 + i)
                .publishOn(s)
                .map(i -> "value " + i).log();

        new Thread(() -> flux.subscribe(System.out::println)).start();
    }
}
