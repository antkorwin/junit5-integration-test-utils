package com.antkorwin.junit5integrationtestutils.concurrency.strategy;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.antkorwin.junit5integrationtestutils.concurrency.CallableVoid;
import com.antkorwin.junit5integrationtestutils.concurrency.testrunner.OneIterationTestResult;
import com.antkorwin.junit5integrationtestutils.concurrency.testrunner.TestRunner;
import com.antkorwin.junit5integrationtestutils.concurrency.testrunner.TestRunnerResult;
import com.antkorwin.junit5integrationtestutils.concurrency.testrunner.TestRunnerSettings;

/**
 * Created on 13.09.2018.
 *
 * TestRunner implementation based on the parallel stream
 *
 * @author Korovin Anatoliy
 */
public class ParallelStreamConcurrentTestRunner implements TestRunner {

    @SuppressWarnings("Duplicates")
    @Override
    public TestRunnerResult run(CallableVoid testCase, TestRunnerSettings settings) {
        // Arrange
        List<Throwable> errors = initEmptyErrorList();

        List<OneIterationTestResult> results =
                IntStream.range(0, settings.getIterationCount())
                         .boxed()
                         .parallel()
                         .map(i -> executeOneIterationResult(testCase, errors))
                         .collect(Collectors.toList());

        return new TestRunnerResult(errors, results);
    }
}
