package com.antkorwin.junit5integrationtestutils.test.runners.stereotype;

import com.antkorwin.junit5integrationtestutils.TransactionalTestConfig;
import com.antkorwin.junit5integrationtestutils.test.runners.meta.annotation.PostgresDataTests;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.DataSetFormat;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.api.exporter.ExportDataSet;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.List;

/**
 * Created on 19.07.2018.
 *
 * TODO: replace on javadoc
 *
 * @author Korovin Anatoliy
 */
@PostgresDataTests
@Import(TransactionalTestConfig.class)
public class PostgresDataTest {


    @Autowired
    private TransactionalTestConfig.FooRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

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

    @Test
    @Sql("/stored_functions/test_func.sql")
    void testStoredFunc() {
        // Arrange
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("rnd");
        // Act
        query.execute();
        // Assert
        List resultList = query.getResultList();
        int rnd = (int) resultList.get(0);
        Assertions.assertThat(rnd).isEqualTo(123);
    }
}
