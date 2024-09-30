package org.example.demo;

import com.google.common.base.Function;
import com.google.common.util.concurrent.*;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class ListenableFutureExample {

    static ExecutorService execService = new ThreadPoolExecutor(5, 50,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>());
    //
    static ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(execService);

    public static void example001(){
        ListenableFuture<Integer> asyncTask = listeningExecutorService.submit(() -> {
            // long-running task
            TimeUnit.MILLISECONDS.sleep(500);
            return 5;
        });
        asyncTask.addListener(new Runnable() {
            @Override
            public void run() {
                System.out.println(LocalDateTime.now() + " >> example001: asyncTask callback");
            }
        }, execService);
        //
    }


    public static void example002(){
        ListenableFuture<Integer> asyncTask = listeningExecutorService.submit(() -> {
            // long-running task
            TimeUnit.MILLISECONDS.sleep(500);
            return 5;
        });
        //
        Futures.addCallback(asyncTask, new FutureCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                // do on success
                System.out.println(LocalDateTime.now() + " >> example002: onSuccess." + result);
            }

            @Override
            public void onFailure(Throwable t) {
                // do on failure
                t.printStackTrace();
            }
        }, execService);
    }

    // old api
    public static FutureTask<String> fetchConfigTask(String configKey) {
        return new FutureTask<>(() -> {
            int nextInt = new Random().nextInt(500, 1000);
            TimeUnit.MILLISECONDS.sleep(nextInt);
            return String.format("example003: %s.%d", configKey, nextInt);
        });
    }

    // new api
    public static ListenableFutureTask<String> fetchConfigListenableTask(String configKey) {
        ListenableFutureTask<String> futureTask = ListenableFutureTask.create(() -> {
            int nextInt = new Random().nextInt(500, 1000);
            TimeUnit.MILLISECONDS.sleep(nextInt);
            if (nextInt % 3 == 0){
                throw new RuntimeException("Error: " + configKey + ", " + nextInt);
            }
            return String.format( LocalDateTime.now() + " >> %s.%d", configKey, nextInt);
        });
        listeningExecutorService.submit(futureTask);
        return futureTask;
    }

    public static ListenableFutureTask<String> fetchConfigListenableTaskThrowable(String configKey) {
        ListenableFutureTask<String> futureTask = ListenableFutureTask.create(() -> {
            int nextInt = new Random().nextInt(500, 1000);
            TimeUnit.MILLISECONDS.sleep(nextInt);
            throw new RuntimeException("Error: " + configKey + ", " + nextInt);
        });
        listeningExecutorService.submit(futureTask);
        return futureTask;
    }

    public static void exampleWithNormalList(){
        // Fan-In
        ListenableFutureTask<String> task1 = fetchConfigListenableTask("config.0");
        // lExecService.submit(task1);
        //
        ListenableFutureTask<String> task2 = fetchConfigListenableTaskThrowable("config.1");
        // lExecService.submit(task2);
        //
        ListenableFutureTask<String> task3 = fetchConfigListenableTask("config.2");
        // lExecService.submit(task3);
        //
        // Collect result of above tasks
        // if any of them failed, then onFailure will be called
        ListenableFuture<List<String>> configsTask = Futures.allAsList(task1, task2, task3);
        Futures.addCallback(configsTask, new FutureCallback<List<String>>() {
            @Override
            public void onSuccess(@Nullable List<String> configResults) {
                // do on all futures success
                assert configResults != null;
                System.out.println("exampleWithNormalList ======");
                configResults.forEach(System.out::println);
            }

            @Override
            public void onFailure(Throwable t) {
                // handle on at least one failure
                System.err.println("Error: exampleWithNormalList ======");
                t.printStackTrace();
            }
        }, execService);
    }

    public static void exampleWithSuccessfulList(){
        // Fan-In
        ListenableFutureTask<String> task1 = fetchConfigListenableTask("config.30");
        // lExecService.submit(task1);
        //
        ListenableFutureTask<String> task2 = fetchConfigListenableTaskThrowable("config.31");
        // lExecService.submit(task2);
        //
        ListenableFutureTask<String> task3 = fetchConfigListenableTask("config.32");
        // lExecService.submit(task3);
        //
        // Collect result of above tasks
        // if any of them failed, then onFailure will be called
        ListenableFuture<List<String>> configsTask = Futures.successfulAsList(task1, task2, task3);
        Futures.addCallback(configsTask, new FutureCallback<List<String>>() {
            @Override
            public void onSuccess(@Nullable List<String> configResults) {
                // do on all futures success
                assert configResults != null;
                System.out.println("exampleWithSuccessfulList ======");
                configResults.forEach(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        if (null == s){
                            System.out.println("Error on : " + s);
                        } else {
                            System.out.println(s);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                // handle on at least one failure
                System.err.println("Error: exampleWithSuccessfulList ======");
                t.printStackTrace();
            }
        }, execService);
    }

    public static void exampleWithCombiner(){
        ListenableFuture<String> cartIdTask = fetchConfigListenableTask("combiner.40");
        ListenableFuture<String> customerNameTask = fetchConfigListenableTask("combiner.41");
        ListenableFuture<String> cartItemsTask = fetchConfigListenableTask("combiner.42");

        // combiner
        ListenableFuture<String> cartInfoTask = Futures.whenAllSucceed(cartIdTask, customerNameTask, cartItemsTask)
                .call(() -> {
                    String cartId = Futures.getDone(cartIdTask);
                    String customerName = Futures.getDone(customerNameTask);
                    String cartItems = Futures.getDone(cartItemsTask);
                    return cartId + " \n " + customerName + " \n " + cartItems;
                }, listeningExecutorService);
        // callback
        Futures.addCallback(cartInfoTask, new FutureCallback<String>() {
            @Override
            public void onSuccess(@Nullable String result) {
                //handle on all success and combination success
                System.out.println("exampleWithCombiner ======");
                System.out.println(result);
            }

            @Override
            public void onFailure(Throwable t) {
                //handle on either task fail or combination failed
                System.err.println("Error exampleWithCombiner ======");
                t.printStackTrace();
            }
        }, listeningExecutorService);
    }

    public static void exampleWithTransformation(){
        ListenableFuture<String> cartItemsTask = fetchConfigListenableTask("transform 100 to 1000");
        Function<String, String> itemTransformFuncToUpperCase = s -> {
            return s.toUpperCase();
        };
        ListenableFuture<String> transformTask = Futures.transform(cartItemsTask, itemTransformFuncToUpperCase, listeningExecutorService);
        //
        // callback
        Futures.addCallback(transformTask, new FutureCallback<String>() {
            @Override
            public void onSuccess(@Nullable String result) {
                //handle on all success and combination success
                System.out.println("exampleWithTransformation ======");
                System.out.println(result);
            }

            @Override
            public void onFailure(Throwable t) {
                //handle on either task fail or combination failed
                System.err.println("Error exampleWithTransformation ======");
                t.printStackTrace();
            }
        }, listeningExecutorService);
    }

    public static void exampleWithFutureChain(){
        ListenableFuture<String> cartItemsTask = fetchConfigListenableTask("chain 100");
        //
        AsyncFunction<String, String> chainFunc = previousResult -> {
            ListenableFuture<String> finalResult = fetchConfigListenableTask(previousResult + " | Upper");
            TimeUnit.MILLISECONDS.sleep(500); // some long running task
            return finalResult;
        };
        ListenableFuture<String> chainTask = Futures.transformAsync(cartItemsTask, chainFunc, listeningExecutorService);
        // callback
        Futures.addCallback(chainTask, new FutureCallback<String>() {
            @Override
            public void onSuccess(@Nullable String result) {
                //handle on all success and combination success
                System.out.println("exampleWithFutureChain ======");
                System.out.println(result);
            }

            @Override
            public void onFailure(Throwable t) {
                //handle on either task fail or combination failed
                System.err.println("Error exampleWithFutureChain ======");
                t.printStackTrace();
            }
        }, listeningExecutorService);
    }

    // write a main function here
    public static void main(String[] args) throws Exception {
        //
        example001();
        //
        example002();
        //
        exampleWithNormalList();
        //
        exampleWithSuccessfulList();
        //
        exampleWithCombiner();
        //
        exampleWithTransformation();
        //
        exampleWithFutureChain();
    }
}
