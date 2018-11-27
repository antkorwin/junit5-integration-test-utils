package com.antkorwin.junit5integrationtestutils.test.extensions.benchmark;


import java.util.concurrent.TimeUnit;

/**
 * Created on 17.08.2018.
 *
 * @author Korovin Anatoliy
 */
@EnableTestBenchmark
@MeasureUnit(unit = TimeUnit.NANOSECONDS)
class BenchmarkExtensionMeasureUnitTest {

    @Fast
    @TestBenchmark(measurementIterations = 15, warmupIterations = 10)
    void testFast() {
    }

    @TestBenchmark(measurementIterations = 15, warmupIterations = 10)
    void testSlow() throws InterruptedException {
        Thread.sleep(1);
    }
}
