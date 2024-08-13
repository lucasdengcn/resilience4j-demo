package org.example.demo;

import java.util.concurrent.atomic.AtomicInteger;

public class BackendService {

    AtomicInteger count = new AtomicInteger(0);

    public Integer doSomething(String name){
        int i = count.incrementAndGet();
        System.out.println("do:" + i + "/" + name);
        return i;
    }

}
