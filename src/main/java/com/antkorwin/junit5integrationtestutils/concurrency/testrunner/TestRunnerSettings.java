package com.antkorwin.junit5integrationtestutils.concurrency.testrunner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created on 13.09.2018.
 *
 * @author Korovin Anatoliy
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class TestRunnerSettings {
    /**
     * count of iterations to run test-case
     */
    private int iterationCount;

    /**
     * count of available threads
     */
    private int threadCount;
}
