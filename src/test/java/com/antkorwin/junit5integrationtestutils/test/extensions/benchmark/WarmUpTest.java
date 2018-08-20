package com.antkorwin.junit5integrationtestutils.test.extensions.benchmark;

import java.util.stream.IntStream;

/**
 * Created on 19.08.2018.
 *
 * @author Korovin Anatoliy
 */
@EnableTestBenchmark
class WarmUpTest {

    @Fast
    @TestBenchmark(warmupIterations = 3, measurementIterations = 10)
    void name() throws InterruptedException {
        // Arrange
        // Act
        IntStream.range(0, 100)
                 .boxed()
                 .forEach(System.out::print);
        // Asserts
    }


    @TestBenchmark(warmupIterations = 3, measurementIterations = 10)
    void nameSlow() throws InterruptedException {
        // Arrange
        // Act
        IntStream.range(0, 1000)
                 .boxed()
                 .forEach(System.out::print);
        // Asserts
    }
}
