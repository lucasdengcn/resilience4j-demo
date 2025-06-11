package org.example.demo;

import java.time.LocalDateTime;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Tick tick0 = new Tick();
        tick0.setAmount(10);
        System.out.println("0: " + tick0);
        //
        final Tick tick1 = tick0;
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("10: " + tick1 + ", " + tick1.getAmount());
                tick1.setAmount(100); // amount is not certain.
                System.out.println(LocalDateTime.now() + " >> 11: " + tick1 + ", " + tick1.getAmount());
            }
        });
        final Tick tick2 = tick0;
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("20: " + tick2 + ", " + tick2.getAmount());
                tick2.setAmount(200);// amount is not certain.
                System.out.println(LocalDateTime.now() +  " >> 21: " + tick2 + ", " + tick2.getAmount());
            }
        });
        //
        thread1.start();
        thread2.start();
    }
}