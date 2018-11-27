package com.antkorwin.junit5integrationtestutils.test.extensions.benchmark;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;

/**
 * Created on 19.08.2018.
 *
 * @author Korovin Anatoliy
 */
@EnableTestBenchmark
class WarmUpTest {

    @Fast
    @TestBenchmark(warmupIterations = 10, measurementIterations = 20)
    void fastStream() {
        // Act
        int sum = IntStream.range(0, 100_000)
                           .boxed()
                           .mapToInt(i -> i)
                           .sum();
        // Asserts
        Assertions.assertEquals(sum, 704982704);
    }

    @TestBenchmark(warmupIterations = 10, measurementIterations = 20)
    void slowStream() {
        // Act
        int sum = IntStream.range(0, 100_000)
                           .boxed()
                           .map(i -> i * 3)
                           .map(i -> i / 3)
                           .map(i -> i * 2)
                           .map(i -> i / 2)
                           .mapToInt(i -> i)
                           .sum();
        // Asserts
        Assertions.assertEquals(sum, 704982704);
    }
}
