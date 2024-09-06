package org.example.demo;

import org.openjdk.jol.info.GraphLayout;

import java.util.concurrent.ConcurrentLinkedQueue;

public class LinkedQueueSizeMeasurement {

    /**
     *
     *      COUNT       AVG       SUM   DESCRIPTION
     *     200000        24   4800000   java.lang.Long
     *     500000        40  20000000   java.math.BigDecimal
     *          1        24        24   java.util.concurrent.ConcurrentLinkedQueue
     *     100001        24   2400024   java.util.concurrent.ConcurrentLinkedQueue$Node
     *     100000        40   4000000   org.example.demo.Tick
     *     900002            31200048   (total) ~ 32MB
     *
     * @param args
     */
    public static void main(String[] args) {
        // Create an instance of an object for analysis
        ConcurrentLinkedQueue<Tick> linkedQueue = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < 100000; i++) {
            linkedQueue.add(new Tick());
        }

        System.out.println("Size of the LinkedQueue including elements: ");
        System.out.println(GraphLayout.parseInstance(linkedQueue).toFootprint());

        // Measure the size of individual elements
        // System.out.println("Size of individual elements: ");
        // linkedQueue.forEach(element -> System.out.println(GraphLayout.parseInstance(element).toFootprint()));
    }
}
