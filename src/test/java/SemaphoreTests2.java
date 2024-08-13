
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.lang3.concurrent.TimedSemaphore;

public class SemaphoreTests2 {

    class LoginQueueUsingSemaphore {

        private Semaphore semaphore;

        public LoginQueueUsingSemaphore(int slotLimit) {
            semaphore = new Semaphore(slotLimit);
        }

        boolean tryLogin(){
            try {
                return semaphore.tryAcquire(10, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }

        void logout() {
            semaphore.release();
        }

        int availableSlots() {
            return semaphore.availablePermits();
        }
    }

    class DelayQueueUsingTimedSemaphore {

        private TimedSemaphore semaphore;

        DelayQueueUsingTimedSemaphore(long period, int slotLimit) {
            semaphore = new TimedSemaphore(period, TimeUnit.SECONDS, slotLimit);
        }

        boolean tryAdd() {
            return semaphore.tryAcquire();
        }

        int availableSlots() {
            return semaphore.getAvailablePermits();
        }

    }

    class CounterUsingMutex {

        private Semaphore mutex;
        private int count;

        CounterUsingMutex() {
            mutex = new Semaphore(1);
            count = 0;
        }

        void increase() throws InterruptedException {
            mutex.acquire();
            this.count = this.count + 1;
            Thread.sleep(1000);
            mutex.release();

        }

        int getCount() {
            return this.count;
        }

        boolean hasQueuedThreads() {
            return mutex.hasQueuedThreads();
        }
    }



    @Test
    public void givenLoginQueue_whenReachLimit_thenBlocked() {
        int slots = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(slots);
        LoginQueueUsingSemaphore loginQueue = new LoginQueueUsingSemaphore(slots);
        IntStream.range(0, slots)
                .forEach(user -> executorService.execute(loginQueue::tryLogin));
        executorService.shutdown();

        assertEquals(0, loginQueue.availableSlots());
        assertFalse(loginQueue.tryLogin());
    }

    @Test
    public void givenLoginQueue_whenLogout_thenSlotsAvailable() {
        int slots = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(slots);
        LoginQueueUsingSemaphore loginQueue = new LoginQueueUsingSemaphore(slots);
        IntStream.range(0, 10)
                .forEach(user -> executorService.execute(loginQueue::tryLogin));
        executorService.shutdown();
        //
        assertEquals(0, loginQueue.availableSlots());
        loginQueue.logout();

        assertTrue(loginQueue.availableSlots() > 0);
        assertTrue(loginQueue.tryLogin());
    }


    @Test
    public void givenDelayQueue_whenReachLimit_thenBlocked() {
        int slots = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(slots);
        DelayQueueUsingTimedSemaphore delayQueue
                = new DelayQueueUsingTimedSemaphore(1, slots);

        IntStream.range(0, slots)
                .forEach(user -> executorService.execute(delayQueue::tryAdd));
        executorService.shutdown();

        assertEquals(0, delayQueue.availableSlots());
        assertFalse(delayQueue.tryAdd());
    }

    @Test
    public void givenDelayQueue_whenTimePass_thenSlotsAvailable() throws InterruptedException {
        int slots = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(slots);
        DelayQueueUsingTimedSemaphore delayQueue = new DelayQueueUsingTimedSemaphore(1, slots);
        IntStream.range(0, slots)
                .forEach(user -> executorService.execute(delayQueue::tryAdd));
        executorService.shutdown();

        assertEquals(0, delayQueue.availableSlots());
        Thread.sleep(1000);
        assertTrue(delayQueue.availableSlots() > 0);
        assertTrue(delayQueue.tryAdd());
    }

    @Test
    public void whenMutexAndMultipleThreads_thenBlocked()
            throws InterruptedException {
        int count = 5;
        ExecutorService executorService
                = Executors.newFixedThreadPool(count);
        CounterUsingMutex counter = new CounterUsingMutex();
        IntStream.range(0, count)
                .forEach(user -> executorService.execute(() -> {
                    try {
                        counter.increase();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }));
        executorService.shutdown();

        assertTrue(counter.hasQueuedThreads());
    }


    @Test
    public void givenMutexAndMultipleThreads_ThenDelay_thenCorrectCount()
            throws InterruptedException {
        int count = 5;
        ExecutorService executorService
                = Executors.newFixedThreadPool(count);
        CounterUsingMutex counter = new CounterUsingMutex();
        IntStream.range(0, count)
                .forEach(user -> executorService.execute(() -> {
                    try {
                        counter.increase();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }));
        executorService.shutdown();

        assertTrue(counter.hasQueuedThreads());
        Thread.sleep(5000);
        assertFalse(counter.hasQueuedThreads());
        assertEquals(count, counter.getCount());
    }

}
