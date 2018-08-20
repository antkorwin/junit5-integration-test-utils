package com.antkorwin.junit5integrationtestutils.test.extensions.benchmark;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 19.08.2018.
 *
 * Extension for make a decision which test-method a fastest in test suite.
 *
 * @author Korovin Anatoliy
 */
public class BenchmarkExtension implements BeforeAllCallback, AfterAllCallback {


    @Override
    public void afterAll(ExtensionContext context) throws Exception {

        // evaluate averages:
        Map<String, List<Long>> iterationResults = getResultOfEachIteration(context);
        iterationResults.forEach((method, result) -> {

            double average = result.stream()
                                   .mapToLong(l -> l)
                                   .average()
                                   .orElseThrow(() -> new AssertionError("not foud data for test iterations"));

            TestTiming timing = ProfilerExtension.getProfilerResult(context)
                                                 .get(method);
            timing.setAverage(average);
            timing.setDuration((long) average);

            System.out.println("\n Average: " + average + "\n");
        });

        // check fastest result:
        long expectedResult = getExpectedFasterResult(context);
        ProfilerExtension.getProfilerResult(context).forEach((method, timing) -> {
            if (timing.getDuration() < expectedResult) {
                String fastestName = getExpectedFasterMethodName(context);
                Assertions.fail("\n\nThe test method \"" + fastestName + "\" - is not fastest in this test suite.\n" +
                                "Timing of the method \"" + method + "\" (" + timing.getDuration() + " ms.) " +
                                " less than timing (" + expectedResult + " ms.) of the expected method \"" + fastestName + "\".\n");
            }
        });
    }


    @Override
    public void beforeAll(ExtensionContext context) throws Exception {

        Method[] methods = context.getTestClass()
                                  .map(Class::getDeclaredMethods)
                                  .orElseThrow(() -> new AssertionError("not found methods for test"));

        long count = Arrays.stream(methods)
                           .filter(m -> m.isAnnotationPresent(Fast.class))
                           .count();
        assertThat(count)
                .as("Expected one method with the annotation Fast")
                .isEqualTo(1);
    }


    private long getExpectedFasterResult(ExtensionContext context) {

        return Optional.ofNullable(ProfilerExtension.getProfilerResult(context))
                       .map(r -> r.get(getExpectedFasterMethodName(context)))
                       .map(TestTiming::getDuration)
                       .orElseThrow(() -> getNotFoundError(context));
    }

    private String getExpectedFasterMethodName(ExtensionContext context) {

        Method[] methods = context.getTestClass()
                                  .map(Class::getDeclaredMethods)
                                  .orElseThrow(() -> new AssertionError("not found methods for test"));

        Method method = Arrays.stream(methods)
                              .filter(m -> m.isAnnotationPresent(Fast.class))
                              .findFirst()
                              .orElseThrow(() -> new AssertionError(
                                      "Fast method not found, please annotate expected fastest method by annotation Fast"));

        return method.getName();
    }

    private AssertionError getNotFoundError(ExtensionContext context) {
        return new AssertionError("Not found a result for the expected method: " +
                                  getExpectedFasterMethodName(context));
    }

    private Map<String, List<Long>> getResultOfEachIteration(ExtensionContext context) {
        return (Map<String, List<Long>>) context.getRoot()
                                                .getStore(ProfilerExtension.NAMESPACE)
                                                .get(context.getRequiredTestClass()
                                                            .getName() + "_iterations");
    }

}
