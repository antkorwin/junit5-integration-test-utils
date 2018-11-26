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
 * TODO: replace on javadoc
 *
 * @author Korovin Anatoliy
 */
public abstract class BaseMeasureTestExtension implements AfterAllCallback, BeforeAllCallback {

    private long before;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        before = getCurrentTime();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {

        TestTiming timing = ProfilerExtension.getTestTiming(context, getTestMethodName());
        long profilerResult = (long) timing.getDuration();
        assertTimeout(profilerResult);

        long doubleCheckResult = evaluateTimeout();
        assertTimeout(doubleCheckResult);
    }

    private long evaluateTimeout() {
        return getCurrentTime() - before;
    }

    private void assertTimeout(long duration) {
        long delta = Math.abs(duration - getTimeout());
        System.out.println(String.format("result = %d ms, delta = %d", duration, delta));
        assertThat(delta).isLessThanOrEqualTo(getExpectedThreshold());
    }

    abstract long getExpectedThreshold();
    abstract long getTimeout();
    abstract String getTestMethodName();
    abstract long getCurrentTime();
}
