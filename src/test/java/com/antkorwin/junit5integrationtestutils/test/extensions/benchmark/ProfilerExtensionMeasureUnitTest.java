package com.antkorwin.junit5integrationtestutils.test.extensions.benchmark;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/**
 * Created on 18.08.2018.
 *
 * @author Korovin Anatoliy
 */
@EnableTestProfiling
@MeasureUnit(unit = TimeUnit.NANOSECONDS)
class ProfilerExtensionMeasureUnitTest {

    @Test
    void first() throws InterruptedException {
        //Thread.sleep(10);
    }

    @Test
    void firstA() throws InterruptedException {
        //Thread.sleep(10);
    }

    @Test
    void firstB() throws InterruptedException {
        //Thread.sleep(10);
    }

    @Test
    void firstC() throws InterruptedException {
        //Thread.sleep(10);
    }
}
