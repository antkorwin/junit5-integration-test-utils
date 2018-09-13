package com.antkorwin.junit5integrationtestutils.concurrency;

import com.antkorwin.commonutils.concurrent.NonAtomicInt;
import org.junit.jupiter.api.Assertions;
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
    private static final int THREADS = 32;

    @Test
    @DisabledIfEnvironmentVariable(named = "TRAVIS", matches = "true")
    void testConcurrentFailWithoutSync() {
        // Arrange
        NonAtomicInt value = new NonAtomicInt(0);
        // Act
        ConcurrentTestRunner.test()
                            .threads(THREADS)
                            .mode(ExecutionMode.TASK_EXECUTOR_MODE)
                            .iterations(ITERATIONS)
                            .run(value::increment);
        // Asserts
        assertThat(value.getValue()).isNotEqualTo(ITERATIONS);
    }

    @Test
    @DisabledIfEnvironmentVariable(named = "TRAVIS", matches = "true")
    void testConcurrentFail_toShowFailList() {
        // Arrange
        NonAtomicInt value = new NonAtomicInt(0);

        Error error = Assertions.assertThrows(Error.class, () -> {
            // Act
            ConcurrentTestRunner.test()
                                .iterations(ITERATIONS)
                                .run(() -> {
                                    int expected = value.getValue() + 1;
                                    value.increment();
                                    // Assert
                                    assertThat(value.getValue()).isEqualTo(expected);
                                });
        });
        // Assert
        assertThat(value.getValue()).isNotEqualTo(ITERATIONS);
        error.printStackTrace();
    }

    @Test
    void testConcurrentWithSync() {
        // Arrange
        NonAtomicInt value = new NonAtomicInt(0);
        // Act
        ConcurrentTestRunner.test()
                            .iterations(ITERATIONS)
                            .run(() -> {
                                synchronized (this) {
                                    value.increment();
                                }
                            });
        // Asserts
        assertThat(value.getValue()).isEqualTo(ITERATIONS);
    }

    @Test
    @Disabled("only to debug")
    void thr() {
        ConcurrentTestRunner.test()
                            .mode(ExecutionMode.TASK_EXECUTOR_MODE)
                            .threads(THREADS)
                            .iterations(ITERATIONS)
                            .run(() -> System.out.println(Thread.currentThread().getName()));
    }
}