package com.antkorwin.junit5integrationtestutils.test.extensions.benchmark;


import org.junit.jupiter.api.Test;

/**
 * Created on 17.08.2018.
 *
 * @author Korovin Anatoliy
 */
@EnableTestBenchmark(fastest = "testFast")
class BenchmarkExtensionTest {

    @Test
    void testFast() throws InterruptedException {
        Thread.sleep(30);
    }

    @Test
    void testSlow() throws InterruptedException {
        Thread.sleep(100);
    }
}
