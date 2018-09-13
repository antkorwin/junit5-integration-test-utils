package com.antkorwin.junit5integrationtestutils.concurrency.testrunner;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created on 13.09.2018.
 *
 * Total result of execution a TestRunner
 *
 * @author Korovin Anatoliy
 */
@Getter
@AllArgsConstructor
public class TestRunnerResult {
    /**
     * error list (if they happened)
     */
    List<Throwable> errors;

    /**
     * list of results
     */
    List<OneIterationTestResult> results;
}
