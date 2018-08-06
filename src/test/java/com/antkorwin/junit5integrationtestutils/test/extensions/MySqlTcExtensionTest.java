package com.antkorwin.junit5integrationtestutils.test.extensions;

import com.antkorwin.junit5integrationtestutils.TransactionalTestConfig;
import com.antkorwin.junit5integrationtestutils.test.runners.EnableDataTests;
import com.antkorwin.junit5integrationtestutils.test.runners.EnableRiderTests;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Commit;

/**
 * Created on 07.08.2018.
 *
 * @author Korovin Anatoliy
 */
@EnableDataTests
@EnableRiderTests
@ExtendWith(MySqlTcExtension.class)
@ExtendWith(TraceSqlExtension.class)
@Tag("mysql")
@Import(TransactionalTestConfig.class)
public class MySqlTcExtensionTest {

    @Autowired
    private TransactionalTestConfig.FooRepository repository;

    @Test
    @Commit
    @DataSet(cleanBefore = true, cleanAfter = true)
    @ExpectedDataSet(value = "/datasets/expected.json", ignoreCols = "ID")
    public void testCreate() throws Exception {

        repository.saveAndFlush(TransactionalTestConfig.Foo.builder()
                                                           .field("tru la la..")
                                                           .build());
    }
}
