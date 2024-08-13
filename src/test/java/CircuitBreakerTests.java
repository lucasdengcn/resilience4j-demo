import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.event.*;
import io.github.resilience4j.core.EventConsumer;
import org.example.demo.BackendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CircuitBreakerTests {

    private BackendService service;
    private Function<String, Integer> decorated;
    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setup() {
        //
        service = new BackendService();
        //
        System.setProperty("circuitbreaker.enabled", "true");
        //
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .failureRateThreshold(20)
                .slidingWindowSize(5)
                .build();
        //
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        circuitBreaker = registry.circuitBreaker("breaker");
        //
        decorated = CircuitBreaker
                .decorateFunction(circuitBreaker, service::doSomething);
        //
        circuitBreaker.getEventPublisher().onSuccess(new EventConsumer<CircuitBreakerOnSuccessEvent>() {
            @Override
            public void consumeEvent(CircuitBreakerOnSuccessEvent event) {
                System.out.println("success: " + circuitBreaker.getMetrics().getNumberOfSuccessfulCalls());
            }
        }).onError(new EventConsumer<CircuitBreakerOnErrorEvent>() {
            @Override
            public void consumeEvent(CircuitBreakerOnErrorEvent event) {
                System.out.println("error: " + circuitBreaker.getMetrics().getNumberOfFailedCalls());
            }
        }).onCallNotPermitted(new EventConsumer<CircuitBreakerOnCallNotPermittedEvent>() {
            @Override
            public void consumeEvent(CircuitBreakerOnCallNotPermittedEvent event) {
                System.out.println("call not permitted: " + circuitBreaker.getMetrics().getFailureRate());
            }
        }).onFailureRateExceeded(new EventConsumer<CircuitBreakerOnFailureRateExceededEvent>() {
            @Override
            public void consumeEvent(CircuitBreakerOnFailureRateExceededEvent event) {
                System.out.println("failure rate exceeded: " + circuitBreaker.getMetrics().getFailureRate());
            }
        }).onStateTransition(new EventConsumer<CircuitBreakerOnStateTransitionEvent>() {
            @Override
            public void consumeEvent(CircuitBreakerOnStateTransitionEvent event) {
                System.out.println("state trans: " + circuitBreaker.getState().name());
            }
        }).onIgnoredError(new EventConsumer<CircuitBreakerOnIgnoredErrorEvent>() {
            @Override
            public void consumeEvent(CircuitBreakerOnIgnoredErrorEvent event) {
                System.out.println("ignored error: " + circuitBreaker.getState().allowPublish);
            }
        });
    }

    @Test
    public void testCircuitBreaker() {
        when(service.doSomething(anyString())).thenThrow(new RuntimeException());
        for (int i = 0; i < 10; i++) {
            try {
                decorated.apply(i + "");
            } catch (Exception ignore) {}
        }
        verify(service, times(5)).doSomething(anyString());
        //
    }

}
