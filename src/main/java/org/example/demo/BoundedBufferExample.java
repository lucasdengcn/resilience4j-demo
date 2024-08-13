package org.example.demo;

public class BoundedBufferExample {

    public static void main(String[] args) {
        // 创建一个容量为10的BoundedBuffer实例
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(10);

        // 生产者线程
        Thread producer = new Thread(() -> {
            for (int i = 0; i < 20; i++) { // 尝试放入20个元素，但缓冲区只能容纳10个
                try {
                    buffer.put(i);
                    System.out.println("Produced: " + i);
                    Thread.sleep((int) (Math.random() * 1000)); // 模拟生产时间
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // 保留中断状态
                }
            }
        });

        // 消费者线程
        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 20; i++) { // 尝试消费20个元素
                try {
                    int consumed = buffer.take();
                    System.out.println("Consumed: " + consumed);
                    Thread.sleep((int) (Math.random() * 1000)); // 模拟消费时间
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // 保留中断状态
                }
            }
        });

        // 启动线程
        producer.start();
        consumer.start();

        // 等待线程完成（在实际应用中，可能需要更复杂的同步机制）
        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 保留中断状态
        }
    }

    // BoundedBuffer类与之前示例中相同
    // ...
}
