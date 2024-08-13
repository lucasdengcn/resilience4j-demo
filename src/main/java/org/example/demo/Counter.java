package org.example.demo;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.random.RandomGenerator;

public class Counter {
    private final Random random = Random.from(RandomGenerator.getDefault());
    //
    private int count = 0;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final java.util.concurrent.locks.Lock readLock = lock.readLock();
    private final java.util.concurrent.locks.Lock writeLock = lock.writeLock();

    private void sleep(){
        int nextInt = random.nextInt(1, 5);
        try {
            TimeUnit.SECONDS.sleep(nextInt);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void increment() {
        sleep();
        writeLock.lock(); // 获取写锁
        try {
            count++; // 修改数据
            System.out.println(Thread.currentThread().getName() + ": Incremented by a thread, result=" + count);
        } finally {
            writeLock.unlock(); // 释放写锁
        }
    }

    public int getCount() {
        sleep();
        readLock.lock(); // 获取读锁
        try {
            return count; // 读取数据
        } finally {
            readLock.unlock(); // 释放读锁
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final Counter counter = new Counter();

        // 创建多个线程来读取和修改计数器
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                counter.increment();
            }, "write-thread-"+i).start();

            new Thread(() -> {
                int c = counter.getCount();
                System.out.println(Thread.currentThread().getName() + ": Read count: " + c);
            }, "read-thread" + i).start();
        }

        // 等待一段时间以查看结果
        Thread.sleep(60*1000);
    }
}
