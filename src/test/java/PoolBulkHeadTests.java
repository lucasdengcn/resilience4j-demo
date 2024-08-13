import io.github.resilience4j.bulkhead.*;
import io.github.resilience4j.bulkhead.event.BulkheadOnCallFinishedEvent;
import io.github.resilience4j.bulkhead.event.BulkheadOnCallPermittedEvent;
import io.github.resilience4j.bulkhead.event.BulkheadOnCallRejectedEvent;
import io.github.resilience4j.core.EventConsumer;
import net.bytebuddy.utility.RandomString;
import org.example.demo.BackendService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class PoolBulkHeadTests {

    private ThreadPoolBulkhead bulkhead;
    private BackendService service;
    private Supplier<CompletionStage<Integer>> decorated;

    class ApiCall implements Callable<Integer> {
        private int id;
        public ApiCall(int id){
            this.id = id;
        }

        @Override
        public Integer call() throws Exception {
            return id;
        }

    }

    // run on each test
    @BeforeEach
    void init(){
        //
        service = new BackendService();
        // Create a custom configuration for a Bulkhead
        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
                .maxThreadPoolSize(3)
                .coreThreadPoolSize(2)
                .queueCapacity(5)
                .build();
        // Create a BulkheadRegistry with a custom global configuration
        ThreadPoolBulkheadRegistry registry = ThreadPoolBulkheadRegistry.of(config);
        // Get or create a Bulkhead from the registry -
        // bulkhead will be backed by the default config
        bulkhead = registry.bulkhead("bulkhead-pool");
        //
        decorated = ThreadPoolBulkhead.decorateCallable(bulkhead, new ApiCall(0));
        //
        bulkhead.getEventPublisher().onCallFinished(new EventConsumer<BulkheadOnCallFinishedEvent>() {
            @Override
            public void consumeEvent(BulkheadOnCallFinishedEvent event) {
                int permission = bulkhead.getMetrics().getRemainingQueueCapacity();
                System.out.println("call finished: " + bulkhead.getMetrics().getAvailableThreadCount() + " qc=" + permission);
            }
        }).onCallPermitted(new EventConsumer<BulkheadOnCallPermittedEvent>() {
            @Override
            public void consumeEvent(BulkheadOnCallPermittedEvent event) {
                int permission = bulkhead.getMetrics().getRemainingQueueCapacity();
                System.out.println("call permitted: " + bulkhead.getMetrics().getAvailableThreadCount() + " qc=" + permission);
            }
        }).onCallRejected(new EventConsumer<BulkheadOnCallRejectedEvent>() {
            @Override
            public void consumeEvent(BulkheadOnCallRejectedEvent event) {
                int permission = bulkhead.getMetrics().getRemainingQueueCapacity();
                System.out.println("call rejected: " + bulkhead.getMetrics().getAvailableThreadCount() + " qc=" + permission);
            }
        });
        //
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                int permission = bulkhead.getMetrics().getRemainingQueueCapacity();
                int threads = bulkhead.getMetrics().getAvailableThreadCount();
                System.out.println("bulkhead queue remaining: " + permission + ", threads=" + threads);
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testBulkhead() {
        //when(service.doSomething(anyString())).thenThrow(new RuntimeException());
        List<Callable<Object>> callables = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) {
            int finalI = i;
            callables.add(new Callable() {
                @Override
                public Object call() throws Exception {
                    try {
                        decorated.get().thenApply(new Function<Integer, Object>() {
                            @Override
                            public Object apply(Integer integer) {
                                System.out.println(" :called: " + finalI);
                                return finalI;
                            }
                        });
                        // Thread.sleep(2000);
                        Thread.currentThread().join();
                    } catch (Exception ignore) {
                        // Bulkhead 'bulkhead-pool' is full and does not permit further calls
                        System.err.println(" error call: " + finalI + ignore.getClass().getName()  + " , " + ignore.getMessage());
                        System.out.println("qc = " + bulkhead.getMetrics().getRemainingQueueCapacity() + " tc = " +bulkhead.getMetrics().getAvailableThreadCount());
                    }
                    return null;
                }
            });
        }
        // create an executorService
        ExecutorService executor = Executors.newFixedThreadPool(20);
        try {
            executor.invokeAll(callables);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //verify(service, times(5)).doSomething(anyString());
        // wait exector to finish
        // Assertions.assertFalse(bulkhead.tryAcquirePermission());
        try {
            Thread.sleep(20 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
