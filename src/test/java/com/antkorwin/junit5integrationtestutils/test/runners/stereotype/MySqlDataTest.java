package com.antkorwin.junit5integrationtestutils.test.runners.stereotype;

import com.antkorwin.junit5integrationtestutils.TransactionalTestConfig;
import com.antkorwin.junit5integrationtestutils.test.runners.meta.annotation.MySqlDataTests;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.DataSetFormat;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.api.exporter.ExportDataSet;
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
@MySqlDataTests
@Import(TransactionalTestConfig.class)
public class MySqlDataTest {

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

    @Test
    @Commit
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @ExportDataSet(outputName = "target/dataset/export.json", format = DataSetFormat.JSON)
    public void generate() throws Exception {

        repository.save(TransactionalTestConfig.Foo.builder()
                                                   .field("tru la la..")
                                                   .build());
    }
}
