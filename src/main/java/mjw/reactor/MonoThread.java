package mjw.reactor;

import reactor.core.publisher.Mono;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 19 Mar 2025, 9:34 AM
 */
public class MonoThread {
    public static void main(String[] args) throws InterruptedException {
        final Mono<String> mono = Mono.just("hello ");
        Thread t = new Thread(() -> mono
                .map(msg -> msg + "thread ")
                .subscribe(v ->
                        System.out.println(v + Thread.currentThread().getName()))
        );
        t.start();
        t.join();
    }
}
