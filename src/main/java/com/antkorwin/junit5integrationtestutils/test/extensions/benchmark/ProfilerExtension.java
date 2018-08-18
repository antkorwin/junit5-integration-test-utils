package com.antkorwin.junit5integrationtestutils.test.extensions.benchmark;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 17.08.2018.
 * <p>
 * Profiler extension
 * - show results in a console
 * - append results of profiling in the junit5 context storage
 *
 * @author Korovin Anatoliy
 */
public class ProfilerExtension implements AfterAllCallback, BeforeEachCallback, AfterEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace
            .create("com", "antkorwin", "profiler");

    public static final String PROFILER_RESULTS_VAR_NAME = "profilerResultsMap";

    public static Map<String, TestTiming> getProfilerResult(ExtensionContext.Store store) {
        return (Map<String, TestTiming>) store.get(PROFILER_RESULTS_VAR_NAME);
    }

    public static TestTiming getTestTiming(ExtensionContext.Store store, String testMethodName) {
        return getProfilerResult(store).get(testMethodName);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {

        String testMethodName = context.getRequiredTestMethod().getName();
        ExtensionContext.Store store = getParentStore(context);

        Map<String, TestTiming> map = getOrCreateProfilerResults(store);

        map.put(testMethodName,
                new TestTiming(System.currentTimeMillis()));
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {

        long endTime = System.currentTimeMillis();

        ExtensionContext.Store store = getParentStore(context);
        String testMethodName = context.getRequiredTestMethod().getName();

        TestTiming testTiming = getTestTiming(store, testMethodName);
        testTiming.setDuration(endTime - testTiming.getStartTime());
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {

        ExtensionContext.Store store = context.getStore(NAMESPACE);
        Map<String, TestTiming> results = getProfilerResult(store);
        printProfilerResult(results);
    }

    private void printProfilerResult(Map<String, TestTiming> results) {
        System.out.println("\nResult of profiling: ");
        results.forEach((method, timing) ->
                                System.out.println(String.format("-> %s : %d ms.",
                                                                 method,
                                                                 timing.duration)));
        System.out.println();
    }

    private Map<String, TestTiming> getOrCreateProfilerResults(ExtensionContext.Store store) {
        return (Map<String, TestTiming>) store.getOrComputeIfAbsent(PROFILER_RESULTS_VAR_NAME,
                                                                    k -> new HashMap());
    }

    @NotNull
    private ExtensionContext.Store getParentStore(ExtensionContext context) {
        return context.getParent()
                      .map(c -> c.getStore(NAMESPACE))
                      .orElseThrow(() -> new RuntimeException("wrong test context hierarchy for benchmark test"));
    }

    @Getter
    @Setter
    class TestTiming {
        private long startTime;
        private long duration;

        public TestTiming(long startTime) {
            this.startTime = startTime;
        }
    }
}
