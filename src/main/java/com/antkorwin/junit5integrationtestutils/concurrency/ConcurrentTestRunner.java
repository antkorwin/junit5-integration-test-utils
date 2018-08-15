package com.antkorwin.junit5integrationtestutils.concurrency;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 15.08.2018.
 *
 * The tool for running a test case in the parallel stream execution
 *
 * @author Korovin Anatoliy
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConcurrentTestRunner {

    private int iterationCount = 10;

    private List<Throwable> errors = new ArrayList<>();

    /**
     * factory method
     * @return ConcurrentTestRunner
     */
    public static ConcurrentTestRunner test() {
        return new ConcurrentTestRunner();
    }

    /**
     * Set iteration counter
     */
    public ConcurrentTestRunner iterations(int iterations) {
        this.iterationCount = iterations;
        return this;
    }

    /**
     * execute test method in concurrent mode
     */
    public void run(CallableVoid singleTestBody) {
        // Arrange
        List<ConcurrentTestResult> results =
                IntStream.range(0, getIterationCount())
                         .boxed()
                         .parallel()
                         .map(i -> {
                             try {
                                 // Act
                                 singleTestBody.call();
                                 return ConcurrentTestResult.OK;
                             } catch (Throwable t) {
                                 t.printStackTrace();
                                 errors.add(t);
                                 return ConcurrentTestResult.FAIL;
                             }
                         })
                         .collect(Collectors.toList());
        // Assert
        Assertions.assertAll(() -> assertThat(errors).isEmpty(),
                             () -> assertThat(results).containsOnly(ConcurrentTestResult.OK));
    }

    private enum ConcurrentTestResult {
        OK,
        FAIL
    }
}
