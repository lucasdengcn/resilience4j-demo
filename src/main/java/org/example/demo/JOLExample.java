package org.example.demo;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JOLExample {

    /**
     * # Object alignment: 8 bytes
     * #                       ref, bool, byte, char, shrt,  int,  flt,  lng,  dbl
     * # Field sizes:            4,    1,    1,    2,    2,    4,    4,    8,    8
     * # Array element sizes:    4,    1,    1,    2,    2,    4,    4,    8,    8
     * # Array base offsets:    16,   16,   16,   16,   16,   16,   16,   16,   16
     *
     * @param args
     */
    public static void main(String[] args) {
        // Print VM details
        System.out.println("JVM: " + VM.current().details());
        // Print the memory layout of the object
        ClassLayout classLayout = ClassLayout.parseInstance(new Tick());
        System.out.println(classLayout.toPrintable());
        //
        classLayout = ClassLayout.parseInstance(new BigDecimal(1.0));
        System.out.println(classLayout.toPrintable());
        //
        classLayout = ClassLayout.parseInstance(1L);
        System.out.println(classLayout.toPrintable());
        //
        classLayout = ClassLayout.parseInstance(1);
        System.out.println(classLayout.toPrintable());
    }

}
