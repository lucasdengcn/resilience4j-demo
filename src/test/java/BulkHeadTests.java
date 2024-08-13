import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.bulkhead.event.BulkheadOnCallFinishedEvent;
import io.github.resilience4j.bulkhead.event.BulkheadOnCallPermittedEvent;
import io.github.resilience4j.bulkhead.event.BulkheadOnCallRejectedEvent;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.core.EventConsumer;
import net.bytebuddy.utility.RandomString;
import org.example.demo.BackendService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class BulkHeadTests {

    private Bulkhead bulkhead;
    private BackendService service;
    private Function<String, Integer> decorated;

    // run on each test
    @BeforeEach
    void init(){
        //
        service = new BackendService();
        // Create a custom configuration for a Bulkhead
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(5)
                .maxWaitDuration(Duration.ofMillis(500))
                .build();
        // Create a BulkheadRegistry with a custom global configuration
        BulkheadRegistry registry = BulkheadRegistry.of(config);
        // Get or create a Bulkhead from the registry -
        // bulkhead will be backed by the default config
        bulkhead = registry.bulkhead("bulkhead");
        //
        decorated = Bulkhead
                .decorateFunction(bulkhead, service::doSomething);
        //
        bulkhead.getEventPublisher().onCallFinished(new EventConsumer<BulkheadOnCallFinishedEvent>() {
            @Override
            public void consumeEvent(BulkheadOnCallFinishedEvent event) {
                System.out.println("call finished: " + bulkhead.getMetrics().getAvailableConcurrentCalls());
            }
        }).onCallPermitted(new EventConsumer<BulkheadOnCallPermittedEvent>() {
            @Override
            public void consumeEvent(BulkheadOnCallPermittedEvent event) {
                System.out.println("call permitted: " + bulkhead.getMetrics().getAvailableConcurrentCalls());
            }
        }).onCallRejected(new EventConsumer<BulkheadOnCallRejectedEvent>() {
            @Override
            public void consumeEvent(BulkheadOnCallRejectedEvent event) {
                System.out.println("call rejected: " + bulkhead.getMetrics().getAvailableConcurrentCalls());
            }
        });
        //
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                boolean permission = bulkhead.tryAcquirePermission();
                System.out.println("bulkhead permission: " + permission);
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
    }

    // test bulk head
    @Test
    void testBulkhead() throws InterruptedException {
        Future<?> taskInProgress = callAndBlock(decorated);
        try {
            Assertions.assertFalse(bulkhead.tryAcquirePermission());
        } finally {
            taskInProgress.cancel(true);
        }
   }

    @Test
    public void testBulkhead2() {
        //when(service.doSomething(anyString())).thenThrow(new RuntimeException());
        List<Callable<Object>> callables = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            callables.add(new Callable() {
                @Override
                public Object call() throws Exception {
                    try {
                        Integer result = decorated.apply("S" + finalI + "");
                        System.out.println(" :called: " + result);
                        Thread.sleep(1000);
                    } catch (Exception ignore) {
                        System.err.println(" error call: " + finalI);
                    }
                    return null;
                }
            });
        }
        // create an executorService
        ExecutorService executor = Executors.newFixedThreadPool(10);
        try {
            executor.invokeAll(callables);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //verify(service, times(5)).doSomething(anyString());
        // wait exector to finish
        // Assertions.assertFalse(bulkhead.tryAcquirePermission());
        try {
            Thread.sleep(60 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Future<?> callAndBlock(Function<String, Integer> decoratedService) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        when(service.doSomething(anyString())).thenAnswer(invocation -> {
            latch.countDown();
            Thread.currentThread().join();
            return null;
        });

        ForkJoinTask<?> result = ForkJoinPool.commonPool().submit(() -> {
            try {
                decoratedService.apply(RandomString.make(10));
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                bulkhead.onComplete();
            }
        });

        latch.await();
        return result;
    }

}
