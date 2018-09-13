package com.antkorwin.junit5integrationtestutils.concurrency;

/**
 * Created on 13.09.2018.
 *
 * Factory to provide a different type of TestRunner
 * by the {@link ExecutionMode}
 *
 * @author Korovin Anatoliy
 */
public class TestRunnerFactory {

    public TestRunner get(ExecutionMode mode){
        switch (mode) {
            case PARALLEL_STREAM_MODE:
                return new ParallelStreamConcurrentTestRunner();
            case TASK_EXECUTOR_MODE:
                return new TaskExecutorConcurrentTestRunner();
            default:
                throw new RuntimeException("Not found TestRunner for ["+mode+"] execution mode");
        }
    }
}
