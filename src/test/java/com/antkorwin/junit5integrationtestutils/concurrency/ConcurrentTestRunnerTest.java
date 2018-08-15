package com.antkorwin.junit5integrationtestutils.concurrency;

import com.antkorwin.commonutils.concurrent.NonAtomicInt;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 15.08.2018.
 *
 * @author Korovin Anatoliy
 */
class ConcurrentTestRunnerTest {

    private static final int ITERATIONS = 100000;

    @Test
    @DisabledIfEnvironmentVariable(named = "CI", matches = "travis")
    void testConcurrentFailWithoutSync() {
        // Arrange
        NonAtomicInt value = new NonAtomicInt(0);

        // Act
        ConcurrentTestRunner.test()
                            .iterations(100000)
                            .run(value::increment);
        // Asserts
        assertThat(value.getValue()).isNotEqualTo(ITERATIONS);
    }

    @Test
    @Disabled("case of the fail test to presentation a log with errors")
    void testConcurrentFail() {
        // Arrange
        NonAtomicInt value = new NonAtomicInt(0);

        // Act
        ConcurrentTestRunner.test()
                            .iterations(100000)
                            .run(()->{
                                int expected = value.getValue()+1;
                                value.increment();
                                assertThat(value.getValue()).isEqualTo(expected);
                            });
    }

    @Test
    void testConcurrentWithSync() {
        // Arrange
        NonAtomicInt value = new NonAtomicInt(0);

        // Act
        ConcurrentTestRunner.test()
                            .iterations(100000)
                            .run(() -> {
                                synchronized (this) {
                                    value.increment();
                                }
                            });
        // Asserts
        assertThat(value.getValue()).isEqualTo(ITERATIONS);
    }
}