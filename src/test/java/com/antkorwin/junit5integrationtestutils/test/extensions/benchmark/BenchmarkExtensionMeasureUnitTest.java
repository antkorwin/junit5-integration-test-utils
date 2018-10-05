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
    @TestBenchmark(measurementIterations = 5, warmupIterations = 2)
    void testFast() {
    }

    @TestBenchmark(measurementIterations = 5, warmupIterations = 2)
    void testSlow() throws InterruptedException {
        Thread.sleep(1);
    }
}
