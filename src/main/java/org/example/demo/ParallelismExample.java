package org.example.demo;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParallelismExample {

    public static void main(String[] args){
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int parallelism = ForkJoinPool.commonPool().getParallelism();
        int commonPoolParallelism = ForkJoinPool.getCommonPoolParallelism();
        //
        System.out.println("Available processors: " + availableProcessors);
        System.out.println("Parallelism: " + parallelism);
        System.out.println("CommonPool Common Parallelism: " + commonPoolParallelism);
        //
        var start = System.nanoTime();
        var futures = IntStream.range(0, 50)
                .mapToObj(i -> CompletableFuture.runAsync(ParallelismExample::block))
                .toList();

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        var duration = Duration.ofNanos(System.nanoTime() - start).toSeconds();
        System.out.println("Processed in " + duration + " seconds");
    }

    private static void block(){
        try {
            Thread.sleep(1000);
            System.out.println("Processed in thread: " + Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
