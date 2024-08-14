package org.example.demo;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class CyclicBarrierExample {
    public static void main(String[] args) throws InterruptedException {
        // 假设有10个员工
        int numberOfEmployees = 10;
        // 创建一个CyclicBarrier，等待10个线程
        CyclicBarrier cyclicBarrier = new CyclicBarrier(numberOfEmployees, () -> {
            // 当所有员工都到齐后（即所有线程都调用了await()），执行此操作
            System.out.println("所有员工已到齐，可以开始吃饭了！");
        });

        // 创建并启动10个线程，每个线程代表一个员工
        for (int i = 1; i <= numberOfEmployees; i++) {
            new Thread(() -> {
                try {
                    // 模拟员工到达餐厅所需的时间（随机时间）
                    TimeUnit.SECONDS.sleep((long) (Math.random() * 5));

                    // 员工到达餐厅后，调用await()等待其他员工
                    cyclicBarrier.await();

                    // 当所有员工都到齐后，执行后续操作（在这个例子中只是打印一条消息）
                    System.out.println(Thread.currentThread().getName() + " 开始吃饭！");
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}