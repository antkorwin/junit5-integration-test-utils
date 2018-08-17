package com.antkorwin.junit5integrationtestutils.test.extensions;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Created on 17.08.2018.
 *
 * TODO: replace on javadoc
 *
 * @author Korovin Anatoliy
 */
@ExtendWith(ProfilerExtension.class)
@Benchmark(fastest = "testFast")
class ProfilerExtensionTest {

    @Test
    void testFast() throws InterruptedException {
        // Arrange
        // Act
        Thread.sleep(30);
        // Assert
    }

    @Test
    void testSlow() throws InterruptedException {
        // Arrange
        // Act
        Thread.sleep(100);
        // Assert
    }
}
