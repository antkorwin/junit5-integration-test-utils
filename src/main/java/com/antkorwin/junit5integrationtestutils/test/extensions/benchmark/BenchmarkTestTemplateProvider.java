package com.antkorwin.junit5integrationtestutils.test.extensions.benchmark;

import org.junit.jupiter.api.extension.*;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 19.08.2018.
 *
 * Template provider for make a sequence of warm-up iteration
 * and sequence of iteration that influence on the test result.
 *
 * @author Korovin Anatoliy
 */
public class BenchmarkTestTemplateProvider implements TestTemplateInvocationContextProvider {


    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {

        boolean annotationPresent = context.getRequiredTestMethod().isAnnotationPresent(TestBenchmark.class);

        assertThat(annotationPresent)
                .as("not found TestBenchmark annotation for this test method.")
                .isTrue();

        return annotationPresent;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {

        TestBenchmark annotation = context.getRequiredTestMethod().getAnnotation(TestBenchmark.class);

        ArrayList<TestTemplateInvocationContext> invocations = new ArrayList<>();

        IntStream.range(0, annotation.warmupIterations())
                 .boxed()
                 .forEach(i -> invocations.add(invocationContext("warmup")));

        IntStream.range(0, annotation.measurementIterations())
                 .boxed()
                 .forEach(i -> invocations.add(invocationContext("_" +
                                                                 context.getRequiredTestMethod().getName())));

        return invocations.stream();
    }


    private TestTemplateInvocationContext invocationContext(String parameter) {


        return new TestTemplateInvocationContext() {

            @Override
            public String getDisplayName(int invocationIndex) {
                return parameter;
            }

            @Override
            public List<Extension> getAdditionalExtensions() {

                return Arrays.asList(

                        (AfterEachCallback) context -> {

                            if (context.getDisplayName().equals("warmup")) return;

                            TestTiming timing =
                                    ProfilerExtension.getTestTiming(context,
                                                                    context.getRequiredTestMethod().getName());

                            Map<String, List<Long>> results =
                                    (Map<String, List<Long>>) context.getRoot()
                                                                     .getStore(ProfilerExtension.NAMESPACE)
                                                                     .getOrComputeIfAbsent(
                                                                             context.getRequiredTestClass()
                                                                                    .getName() + "_iterations",
                                                                             name -> new HashMap<String, List<Long>>());

                            List<Long> longs = results.computeIfAbsent(context.getRequiredTestMethod().getName(),
                                                                       k -> new ArrayList<>());

                            longs.add(timing.getDuration());
                        },

                        new ProfilerExtension());
            }
        };
    }
}
