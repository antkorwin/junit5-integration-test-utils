package com.antkorwin.junit5integrationtestutils.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.awaitility.Awaitility;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created on 13.09.2018.
 *
 * TestRunner implementation based on the {@link ThreadPoolTaskExecutor}.
 * Provides an ability to set a thread count for execute test case in concurrent mode.
 *
 * @author Korovin Anatoliy
 */
public class TaskExecutorConcurrentTestRunner implements TestRunner {

    @Override
    public TestRunnerResult run(CallableVoid testCase, TestRunnerSettings settings) {

        List<Throwable> errors = initEmptyErrorList();
        List<Future<OneIterationTestResult>> futureList = new ArrayList<>();
        ThreadPoolTaskExecutor executor = initThreadPoolExecutor(settings.getThreadCount());

        for (int i = 0; i < settings.getIterationCount(); i++) {
            futureList.add(executor.submit(() -> executeOneIterationResult(testCase, errors)));
        }

        Awaitility.await()
                  .atMost(10, TimeUnit.SECONDS)
                  .until(() -> futureList.stream().allMatch(Future::isDone));

        List<OneIterationTestResult> results = futureList.stream()
                                                         .map(f -> {
                                                           try {
                                                               return f.get();
                                                           }
                                                           catch (Exception e) {
                                                               e.printStackTrace();
                                                               return OneIterationTestResult.FAIL;
                                                           }
                                                       })
                                                         .collect(Collectors.toList());

        return new TestRunnerResult(errors, results);
    }


    private ThreadPoolTaskExecutor initThreadPoolExecutor(int threadCount) {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(threadCount);
        executor.setCorePoolSize(threadCount);
        executor.setThreadNamePrefix("Thread-");
        executor.initialize();
        return executor;
    }
}
