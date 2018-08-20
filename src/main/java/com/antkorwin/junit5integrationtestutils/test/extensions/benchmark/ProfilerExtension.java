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

    /**
     * Namespace for the storage of test results
     */
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace
            .create("com", "antkorwin", "profiler");

    /**
     * return a result of profiling
     * for a current test class in the context
     */
    public static Map<String, TestTiming> getProfilerResult(ExtensionContext context) {
        return getProfilerResult(context, context.getRequiredTestClass().getName());
    }

    /**
     * return a result of profiling
     * for the testClassName
     */
    public static Map<String, TestTiming> getProfilerResult(ExtensionContext context, String testClassName) {
        ExtensionContext.Store store = context.getRoot().getStore(NAMESPACE);
        return (Map<String, TestTiming>) store.get(testClassName);
    }

    /**
     * return a timing of the profiling result (by method name)
     * for current test class in the context.
     */
    public static TestTiming getTestTiming(ExtensionContext context, String testMethodName) {
        return getProfilerResult(context).get(testMethodName);
    }

    /**
     * return a timing of the profiling result
     * by test class name
     * and test method name from this class.
     */
    public static TestTiming getTestTiming(ExtensionContext context, String testClassName, String testMethodName) {
        return getProfilerResult(context, testClassName).get(testMethodName);
    }

    /**
     * print profiling results in the console
     */
    public static void printProfilerResult(Map<String, TestTiming> results) {
        System.out.println("\nResult of profiling: ");
        results.forEach((method, timing) ->
                                System.out.println(String.format("-> %s : %d ms.",
                                                                 method,
                                                                 timing.getDuration())));
        System.out.println();
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {

        Map<String, TestTiming> map = getOrCreateProfilerResults(context);

        String testMethodName = context.getRequiredTestMethod().getName();
        map.put(testMethodName, new TestTiming(System.currentTimeMillis()));
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {

        long endTime = System.currentTimeMillis();

        String testMethodName = context.getRequiredTestMethod().getName();
        TestTiming testTiming = getTestTiming(context, testMethodName);
        testTiming.setDuration(endTime - testTiming.getStartTime());
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {

        Map<String, TestTiming> results = getProfilerResult(context);
        printProfilerResult(results);
    }

    private Map<String, TestTiming> getOrCreateProfilerResults(ExtensionContext context) {
        ExtensionContext.Store store = getStore(context);
        return (Map<String, TestTiming>) store.getOrComputeIfAbsent(context.getRequiredTestClass().getName(),
                                                                    k -> new HashMap());
    }

    @NotNull
    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getRoot().getStore(NAMESPACE);
    }
}
