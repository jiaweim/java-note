package mjw.reactor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 20 Mar 2025, 5:21 PM
 */
public class SubscribeThread {
    public static void main(String[] args) throws InterruptedException {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5)
                .map(i -> {
                    System.out.format("map(%d) - %s\n",
                            i, Thread.currentThread().getName());
                    return i * 10;
                })
                .flatMap(i -> {
                    System.out.format("flatMap(%d) - %s\n",
                            i, Thread.currentThread().getName());
                    return Mono.just(i * 10);
                });

        Thread myThread = new Thread(() ->
                integerFlux.subscribe(i ->
                        System.out.format("subscribe(%d) - %s\n",
                                i, Thread.currentThread().getName())
                )
        );

        myThread.start();
        myThread.join(); // 让程序等待该线程结束
    }
}
