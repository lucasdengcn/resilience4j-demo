package org.example.demo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountDownLatchExample {
    public static void main(String[] args) throws InterruptedException {
        int taskCount = 5; // 假设有5个任务需要完成
        CountDownLatch latch = new CountDownLatch(taskCount);

        ExecutorService executor = Executors.newFixedThreadPool(taskCount);

        for (int i = 0; i < taskCount; i++) {
            int taskId = i;
            executor.submit(() -> {
                System.out.println("任务 " + taskId + " 正在执行");
                // 模拟任务执行时间
                try {
                    Thread.sleep((long) (Math.random() * 1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("任务 " + taskId + " 完成");
                latch.countDown(); // 任务完成后，计数器减一
            });
        }

        // 等待所有任务完成
        latch.await();

        System.out.println("所有任务完成，继续执行后续操作");

        executor.shutdown();
    }
}
