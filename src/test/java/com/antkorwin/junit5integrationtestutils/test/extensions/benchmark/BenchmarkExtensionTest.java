package com.antkorwin.junit5integrationtestutils.test.extensions.benchmark;


/**
 * Created on 17.08.2018.
 *
 * @author Korovin Anatoliy
 */
@EnableTestBenchmark
class BenchmarkExtensionTest {

    @Fast
    @TestBenchmark(measurementIterations = 5, warmupIterations = 2)
    void testFast() throws InterruptedException {
        Thread.sleep(30);
    }

    @TestBenchmark(measurementIterations = 5, warmupIterations = 2)
    void testSlow() throws InterruptedException {
        Thread.sleep(100);
    }
}
