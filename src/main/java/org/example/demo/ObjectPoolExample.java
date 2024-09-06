package org.example.demo;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ObjectPoolExample {

    private ConcurrentLinkedQueue<Tick> pool;
    private volatile boolean empty = false;

    public ObjectPoolExample(int poolSize) {
        pool = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < poolSize; i++) {
            pool.add(new Tick());
        }
    }

    public Tick borrowTick() {
        Tick tick = pool.poll(); // Retrieves and removes the head of the queue
        if (tick == null) {
            System.out.println("pool is Empty, means size is too small or leak happens, try to increasing size next time.");
            tick = new Tick();
            // pool might expand rapidly. so returnTick properly is Key action.
        }
        return tick;
    }

    public void returnTick(Tick tick) {
        if (tick != null) {
            tick.reset();
            pool.offer(tick); // Adds the Tick to the end of the queue
        }
    }

    public boolean isEmpty(){
        return pool.isEmpty();
    }

    public int size(){
        return pool.size();
    }

    public static void main(String[] args) {
        ObjectPoolExample objectPool = new ObjectPoolExample(5);

        // Borrow and return Ticks
        Tick tick = objectPool.borrowTick();
        System.out.println("Tick 1 borrowed. size=" + objectPool.size());

        Tick tick1 = objectPool.borrowTick();
        System.out.println("Tick 2 borrowed. size=" + objectPool.size());

        objectPool.returnTick(tick);
        System.out.println("Tick 1 returned. size=" + objectPool.size());

        Tick tick2 = objectPool.borrowTick();
        System.out.println("Tick 3 borrowed. size=" + objectPool.size());
    }
    
}
