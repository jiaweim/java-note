package mjw.reactor;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 19 Mar 2025, 11:03 AM
 */
public class PublishOnDemo {
    public static void main(String[] args) throws InterruptedException {
        Disposable subscribe = Flux.just(1, 2, 3, 4, 5)
                .publishOn(
                        Schedulers.newParallel("parallelScheduler")
                )
                .map(i -> {
                    System.out.format("map(%d) - %s\n",
                            i, Thread.currentThread().getName()
                    );
                    return i * 10;
                })
                .publishOn(
                        Schedulers.newSingle("singleScheduler")
                )
                .flatMap(i -> {
                    System.out.format("flatMap(%d) - %s\n",
                            i, Thread.currentThread().getName()
                    );
                    return Mono.just(i * 10);
                })
                .subscribe(i -> System.out.format("subscribe(%d) - %s\n",
                        i, Thread.currentThread().getName())
                );
        Thread.sleep(1000);
        subscribe.dispose();
    }
}
