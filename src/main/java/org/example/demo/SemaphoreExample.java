package org.example.demo;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.random.RandomGenerator;

public class SemaphoreExample {
    // 创建一个Semaphore实例，许可数量为3
    private static final Semaphore semaphore = new Semaphore(3);
    private final Random random = Random.from(RandomGenerator.getDefault());

    public static void main(String[] args) {
        // 创建并启动10个线程，每个线程都会尝试获取信号量的许可并执行任务
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    // 请求许可
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + " 获取许可, 正在执行");
                    // 模拟任务执行
                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName() + " 执行完毕, 释放许可");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    // 释放许可
                    semaphore.release();
                }
            }).start();
        }
    }
}
