package com.antkorwin.junit5integrationtestutils.concurrency;

import com.antkorwin.junit5integrationtestutils.concurrency.strategy.TestRunnerFactory;
import com.antkorwin.junit5integrationtestutils.concurrency.testrunner.OneIterationTestResult;
import com.antkorwin.junit5integrationtestutils.concurrency.testrunner.TestRunnerResult;
import com.antkorwin.junit5integrationtestutils.concurrency.testrunner.TestRunnerSettings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;

import static com.antkorwin.junit5integrationtestutils.concurrency.ConcurrentTestRunner.DefaultSettings.DEFAULT_ITERATIONS;
import static com.antkorwin.junit5integrationtestutils.concurrency.ConcurrentTestRunner.DefaultSettings.DEFAULT_MODE;
import static com.antkorwin.junit5integrationtestutils.concurrency.ConcurrentTestRunner.DefaultSettings.DEFAULT_THREADS;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created on 15.08.2018.
 *
 * The tool for running a test case in concurrency mode.
 *
 * You can use:
 * - an implementation based on the parallel stream
 * or
 * - an implementation based on the {@link org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor}
 *
 * For {@link org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor} you can set
 * - an available thread number, this implementation provide a more flexible configuration of behavior.
 *
 * Both of these implementation provide a configuration to set a number of iterations.
 * The test case method will be called this number of times,
 * and after that check all results, if any iteration throws an error than
 * {@link ConcurrentTestRunner} will throw the {@link AssertionError}.
 *
 * @author Korovin Anatoliy
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConcurrentTestRunner {

    static class DefaultSettings {
        static final int DEFAULT_ITERATIONS = 10;
        static final int DEFAULT_THREADS = 4;
        static final ExecutionMode DEFAULT_MODE = ExecutionMode.PARALLEL_STREAM_MODE;
    }

    private TestRunnerFactory testRunnerFactory;
    private TestRunnerSettings settings;
    private ExecutionMode mode;

    private ConcurrentTestRunner(TestRunnerFactory factory) {
        this.testRunnerFactory = factory;
        this.settings = new TestRunnerSettings(DEFAULT_ITERATIONS, DEFAULT_THREADS);
        this.mode = DEFAULT_MODE;
    }

    /**
     * factory method
     *
     * @return ConcurrentTestRunner
     */
    public static ConcurrentTestRunner test() {
        return new ConcurrentTestRunner(new TestRunnerFactory());
    }

    /**
     * Set iteration counter
     */
    public ConcurrentTestRunner iterations(int iterations) {
        this.settings.setIterationCount(iterations);
        return this;
    }

    /**
     * Set threads count for TASK_EXECUTOR_MODE
     *
     * @param threadsCount count of threads for ThreadPoolTaskExecutor
     */
    public ConcurrentTestRunner threads(int threadsCount) {
        this.settings.setThreadCount(threadsCount);
        return this;
    }

    /**
     * Set the mode of concurrently running
     *
     * @param mode ExecutionMode (by stream().parallel() or by ThreadPoolTaskExecutor)
     */
    public ConcurrentTestRunner mode(ExecutionMode mode) {
        this.mode = mode;
        return this;
    }

    /**
     * Run the test case method with selected execution mode
     *
     * @param testCase method that need to run concurrently
     */
    public void run(CallableVoid testCase) {
        // Arrange
        TestRunnerSettings settings = TestRunnerSettings.builder()
                                                        .iterationCount(this.settings.getIterationCount())
                                                        .threadCount(this.settings.getThreadCount())
                                                        .build();
        // Act
        TestRunnerResult result = testRunnerFactory.get(this.mode)
                                                   .run(testCase, settings);
        // Assert
        Assertions.assertAll(() -> assertThat(result.getErrors()).isEmpty(),
                             () -> assertThat(result.getResults()).containsOnly(OneIterationTestResult.OK));
    }
}
