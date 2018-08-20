package com.antkorwin.junit5integrationtestutils.test.extensions.benchmark;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

/**
 * Created on 19.08.2018.
 *
 * A single result of measuring the duration of the test method.
 *
 * @author Korovin Anatoliy
 */
@Getter
@Setter
public class TestTiming {

    private long startTime;
    private long duration;
    private double average;
    private List<Long> results = new ArrayList<>();

    public TestTiming(long startTime) {
        this.startTime = startTime;
    }
}
