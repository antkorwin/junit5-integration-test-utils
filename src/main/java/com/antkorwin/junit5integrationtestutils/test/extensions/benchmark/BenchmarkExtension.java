package com.antkorwin.junit5integrationtestutils.test.extensions.benchmark;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import static com.antkorwin.junit5integrationtestutils.test.extensions.benchmark.ProfilerExtension.NAMESPACE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 18.08.2018.
 * <p>
 * Runs a test suite and asserts that the execution time
 * of each test does not better than the time of the fastest method
 * which set by the {@link EnableTestBenchmark} annotation
 *
 * @author Korovin Anatoliy
 */
public class BenchmarkExtension implements AfterAllCallback, BeforeAllCallback {


    private static final String WRONG_CONFIGURATION_MESSAGE = "wrong configuration of the Benchmark class";

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        validateTestSuite(context);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {

        ExtensionContext.Store store = context.getStore(NAMESPACE);
        long expectedResult = getExpectedFasterResult(context);

        ProfilerExtension.getProfilerResult(store).forEach((method, timing) -> {
            if (timing.getDuration() < expectedResult) {
                String fastestName = getExpectedFasterMethodName(context);
                Assertions.fail("Test method [" + fastestName + "] - is not fastest in this test suite");
            }
        });
    }

    private void validateTestSuite(ExtensionContext context) {
        Boolean annotationExist = context.getElement()
                                         .map(e -> e.isAnnotationPresent(EnableTestBenchmark.class))
                                         .orElseThrow(() -> new AssertionError(WRONG_CONFIGURATION_MESSAGE));

        assertThat(annotationExist).as("not found EnableTestBenchmark annotation")
                                   .isTrue();

        assertThat(getFasterMethodsExist(context)).as("Test method [%s] is not found in this test suite.\n",
                                                      getExpectedFasterMethodName(context))
                                                  .isTrue();
    }

    private boolean getFasterMethodsExist(ExtensionContext context) {

        String fastestTestName = getExpectedFasterMethodName(context);

        Method[] methods = context.getTestClass()
                                  .map(Class::getDeclaredMethods)
                                  .orElseThrow(() -> new AssertionError("not found methods for test"));

        return Arrays.stream(methods)
                     .filter(m -> m.getName().equals(fastestTestName))
                     .anyMatch(m -> m.isAnnotationPresent(Test.class));
    }

    private long getExpectedFasterResult(ExtensionContext context) {

        return Optional.ofNullable(ProfilerExtension.getProfilerResult(context.getStore(NAMESPACE)))
                       .map(r -> r.get(getExpectedFasterMethodName(context)))
                       .map(ProfilerExtension.TestTiming::getDuration)
                       .orElseThrow(() -> getNotFoundError(context));
    }

    private String getExpectedFasterMethodName(ExtensionContext context) {
        return context.getElement()
                      .map(e -> e.getAnnotation(EnableTestBenchmark.class))
                      .map(EnableTestBenchmark::fastest)
                      .orElseThrow(() -> new AssertionError(WRONG_CONFIGURATION_MESSAGE));
    }

    private AssertionError getNotFoundError(ExtensionContext context) {
        return new AssertionError("Not found a result for the expected method: " +
                                  getExpectedFasterMethodName(context));
    }
}
