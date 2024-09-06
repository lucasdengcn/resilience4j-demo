package org.example.demo;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JOLExample {

    public static void main(String[] args) {
        // Print VM details
        System.out.println("JVM: " + VM.current().details());
        // Print the memory layout of the object
        ClassLayout classLayout = ClassLayout.parseInstance(new Tick());
        System.out.println(classLayout.toPrintable());
        //
        classLayout = ClassLayout.parseInstance(new BigDecimal(1.0));
        System.out.println(classLayout.toPrintable());
    }

}
