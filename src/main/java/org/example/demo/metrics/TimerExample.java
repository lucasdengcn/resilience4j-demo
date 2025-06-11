package org.example.demo.metrics;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class TimerExample {

    public static void main(String[] args){
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        //
        Timer timer = Timer
                .builder("test.timer")
                .publishPercentiles(0.3, 0.5, 0.95)
                .publishPercentileHistogram()
                .register(registry);
        //
        timer.record(2, TimeUnit.SECONDS);
        timer.record(2, TimeUnit.SECONDS);
        timer.record(3, TimeUnit.SECONDS);
        timer.record(4, TimeUnit.SECONDS);
        timer.record(8, TimeUnit.SECONDS);
        timer.record(13, TimeUnit.SECONDS);
        //
        System.out.println("Count=" + timer.count());
        System.out.println("Mean=" + timer.mean(TimeUnit.SECONDS));
        System.out.println("Max=" + timer.max(TimeUnit.SECONDS));
        //
        Map<Double, Double> actualMicrometer = new TreeMap<>();
        ValueAtPercentile[] percentiles = timer.takeSnapshot().percentileValues();
        for (ValueAtPercentile percentile : percentiles) {
            actualMicrometer.put(percentile.percentile(), percentile.value(TimeUnit.MILLISECONDS));
            System.out.println(percentile);
        }
        //

    }

}
