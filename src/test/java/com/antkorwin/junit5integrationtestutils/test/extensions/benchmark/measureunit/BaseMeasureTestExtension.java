package com.antkorwin.junit5integrationtestutils.test.extensions.benchmark.measureunit;

import java.util.concurrent.TimeUnit;

import com.antkorwin.junit5integrationtestutils.test.extensions.benchmark.ProfilerExtension;
import com.antkorwin.junit5integrationtestutils.test.extensions.benchmark.TestTiming;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 26.11.2018.
 *
 * abstract base Extension to write a test of MeasureUnit
 *
 * @author Korovin Anatoliy
 */
public abstract class BaseMeasureTestExtension implements AfterAllCallback, BeforeAllCallback {

    private long before;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        before = getCurrentTime();
    }

    /**
     * At first:
     * check that ProfilerExtension store in the context a timing
     * with a duration which far from getTimeout no more than getExpectedThreshold.
     *
     * Second:
     * evaluate time of the test execution and check that this time is far from
     * the measured by profiler no more than getExpectedThreshold.
     */
    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        // check the range of a duration which measured by profiler:
        TestTiming timing = ProfilerExtension.getTestTiming(context, getTestMethodName());
        long profilerResult = (long) timing.getDuration();
        assertThatMeasureInRange(profilerResult, getTimeout(), getExpectedThreshold());

        // compare with time measured by this extension:
        long doubleCheckResult = evaluateTimeout();
        assertThatMeasureInRange(profilerResult, doubleCheckResult, getExpectedThreshold());
    }

    private long evaluateTimeout() {
        return getCurrentTime() - before;
    }

    private void assertThatMeasureInRange(long firstMeasure, long secondMeasure, long threshold) {
        long delta = Math.abs(firstMeasure - secondMeasure);
        System.out.println(String.format("firstMeasure = %d, secondMeasure = %d, delta = %d, threshold =%d",
                                         firstMeasure, secondMeasure, delta, threshold));
        assertThat(delta).isLessThanOrEqualTo(threshold);
    }

    abstract long getExpectedThreshold();

    abstract long getTimeout();

    abstract String getTestMethodName();

    abstract long getCurrentTime();
}
