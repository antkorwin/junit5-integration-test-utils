package com.antkorwin.junit5integrationtestutils.test.extensions.benchmark;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Created on 18.08.2018.
 *
 * @author Korovin Anatoliy
 */
@EnableTestProfiling
class ProfilerExtensionTest {

    @Test
    void first() throws InterruptedException {
        Thread.sleep(100);
    }

    @Test
    void second() {

    }
}
