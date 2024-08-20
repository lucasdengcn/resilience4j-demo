package org.example.demo;

import java.util.concurrent.locks.StampedLock;

public class StampedLockExample {

    private final StampedLock lock = new StampedLock();
    private long count = 0;

    // 安全的增加计数器
    public void increment() {
        long stamp = lock.writeLock(); // 获取写锁
        try {
            count++; // 修改共享资源
            System.out.println(stamp + " >> incr count: " + count);
        } finally {
            lock.unlock(stamp); // 释放写锁
        }
    }

    // 安全的读取计数器
    public long getCount() {
        System.out.println("try to get read lock...");
        long stamp = lock.tryOptimisticRead(); // 尝试获取乐观读锁
        long result;
        do {
            result = count; // 读取共享资源
            System.out.println( stamp + ">> to get count: " + result);
        } while (!lock.validate(stamp) && stamp > 0); // 检查在读取过程中是否有写操作发生
        //
        System.out.println(stamp +" >> get count: " + result);
        //
        // 如果validate返回false，说明有写操作发生，我们可以选择重试乐观读
        // 或者转换为悲观读锁，这里为了简单起见，直接返回结果（注意：这可能不是最新的值）
        // 或者，更健壮的做法是使用悲观读锁：
        /*
        stamp = lock.readLock(); // 转换为悲观读锁
        try {
            result = count; // 再次读取共享资源
        } finally {
            lock.unlockRead(stamp); // 释放悲观读锁
        }
        */

        return result;
    }

    public static void main(String[] args) throws InterruptedException {
        StampedLockExample counter = new StampedLockExample();

        // 模拟写操作
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                counter.increment();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                }
            }
        });

        // 模拟读操作
        Thread reader = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                long count = counter.getCount();
                // 这里只是简单地打印，实际使用时可能需要对count进行进一步处理
                System.out.println(Thread.currentThread().getName() + " read: " + count);
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                }
            }
        });

        System.out.println("Start Threads");
        writer.start();
        reader.start();

        System.out.println("Wait Threads");
        writer.join(); // 等待写线程完成
        // 注意：通常我们不会等待读线程完成，因为读线程可能会持续运行以响应新的读取请求
        // 但在这个例子中，我们为了看到结果而等待它
        reader.join();
    }

}
