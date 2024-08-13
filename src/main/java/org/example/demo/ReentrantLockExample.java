package org.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.random.RandomGenerator;

public class ReentrantLockExample {

    private final ReentrantLock lock = new ReentrantLock();
    private final Random random = Random.from(RandomGenerator.getDefault());

    public void criticalSection() {
        //
        float v = random.nextFloat(1.0f, 5.0f);
        try {
            TimeUnit.MILLISECONDS.sleep((long) (v * 1000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 尝试获取锁
        lock.lock();
        try {
            // 临界区代码，需要同步执行
            System.out.println("临界区代码执行中，线程：" + Thread.currentThread().getName());
            // 模拟耗时操作
            v = random.nextFloat(1.0f, 5.0f);
            TimeUnit.MILLISECONDS.sleep((long) (v * 1000));
        } catch (InterruptedException e) {
            // 如果在等待锁的过程中被中断，则捕获异常并处理
            Thread.currentThread().interrupt(); // 保持中断状态
            System.out.println("线程在等待锁的过程中被中断：" + Thread.currentThread().getName());
        } finally {
            // 释放锁
            lock.unlock();
        }
    }

    public void tryLockExample() {
        //
        float v = random.nextFloat(1.0f, 4.0f);
        try {
            TimeUnit.MILLISECONDS.sleep((long) (v * 1000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 尝试非阻塞获取锁
        if (lock.tryLock()) {
            try {
                // 临界区代码，如果获取到锁则执行
                System.out.println("尝试非阻塞获取锁成功，线程：" + Thread.currentThread().getName());
                // 模拟耗时操作
                v = random.nextFloat(1.0f, 4.0f);
                TimeUnit.MILLISECONDS.sleep((long) (v * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                // 释放锁
                lock.unlock();
            }
        } else {
            // 如果没有获取到锁，则执行其他操作
            System.out.println("尝试非阻塞获取锁失败，线程：" + Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) {
        ReentrantLockExample example = new ReentrantLockExample();
        //
        List<Thread> threadList = new ArrayList<>(10);
        // 创建并启动多个线程来模拟并发访问
        for (int i = 0; i < 5; i++) {
            threadList.add(new Thread(() -> example.criticalSection(), "Thread-" + i)); //.start();
        }

        // 演示tryLock的使用
        for (int i = 0; i < 5; i++) {
            threadList.add(new Thread(() -> example.tryLockExample(), "TryLock-Thread-" + i)); //.start();
        }
        //
        threadList.forEach((t) -> t.start());
        //

    }
}