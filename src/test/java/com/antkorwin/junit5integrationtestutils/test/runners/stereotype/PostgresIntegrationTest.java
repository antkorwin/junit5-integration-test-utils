package com.antkorwin.junit5integrationtestutils.test.runners.stereotype;

import com.antkorwin.junit5integrationtestutils.TransactionalTestConfig;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created on 19.07.2018.
 *
 * TODO: replace on javadoc
 *
 * @author Korovin Anatoliy
 */
@PostgresIntegrationTests
@Import(TransactionalTestConfig.class)
public class PostgresIntegrationTest {


    @Autowired
    private TransactionalTestConfig.FooRepository repository;

    @Test
    @Commit
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DataSet(cleanBefore = true, cleanAfter = true, transactional = true)
    @ExpectedDataSet(value = "/datasets/expected.json", ignoreCols = "ID")
    public void testCreate() throws Exception {
        // Arrange
        // Act
        repository.saveAndFlush(TransactionalTestConfig.Foo.builder()
                                                           .field("tru la la..")
                                                           .build());
        // Assert
    }
}
