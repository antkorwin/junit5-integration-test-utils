package com.antkorwin.junit5integrationtestutils.test.extensions;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 17.08.2018.
 *
 * TODO: replace on javadoc
 *
 * @author Korovin Anatoliy
 */
public class ProfilerExtension implements AfterAllCallback, BeforeEachCallback, AfterEachCallback {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create("antkorwin");
    private static final String TEST_METHOD_NAMES = "TEST_METHODS";

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {

        ExtensionContext.Store store = getParentStore(context);
        String testMethodName = context.getRequiredTestMethod().getName();

        List<String> testMethods = (List<String>) store.getOrComputeIfAbsent(TEST_METHOD_NAMES, k -> new ArrayList<>());
        testMethods.add(testMethodName);

        store.put(testMethodName, System.currentTimeMillis());
    }

    @NotNull
    private ExtensionContext.Store getParentStore(ExtensionContext context) {
        return context.getParent()
                      .map(c -> c.getStore(NAMESPACE))
                      .orElseThrow(() -> new RuntimeException("wrong test context hierarchy for benchmark test"));
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {

        ExtensionContext.Store store = getParentStore(context);
        String testMethodName = context.getRequiredTestMethod().getName();

        long before = (long) store.get(testMethodName);
        long duration = System.currentTimeMillis() - before;

        store.put(testMethodName, duration);
        System.out.println(testMethodName + " : " + duration + " ms.");
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {

        String expectedFaster = context.getElement()
                                       .filter(e -> e.isAnnotationPresent(Benchmark.class))
                                       .map(e -> e.getAnnotation(Benchmark.class))
                                       .map(Benchmark::fastest)
                                       .orElseThrow(() -> new RuntimeException("wrong configuration of the Benchmark class"));

        long expectedResult = (long) context.getStore(NAMESPACE)
                                            .get(expectedFaster);

        List<String> allTestMethods = (List<String>) context.getStore(NAMESPACE).get(TEST_METHOD_NAMES);

        allTestMethods.forEach(method -> {
            long value = (long) context.getStore(NAMESPACE)
                                       .get(method);
            if (value < expectedResult) {
                Assertions.fail("not fastest");
            }
        });
    }
}
