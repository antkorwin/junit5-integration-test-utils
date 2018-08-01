package com.antkorwin.junit5integrationtestutils.test.runners;

import com.antkorwin.junit5integrationtestutils.TransactionalTestConfig;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
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
 * Created on 17.07.2018.
 * <p>
 * TODO: replace on javadoc
 *
 * @author Korovin Anatoliy
 */
@EnableIntegrationTests
@EnableRiderTests
@EnablePostgresTestContainers
@Import(TransactionalTestConfig.class)
public class PostgresTcIntegrationTest {

    @Autowired
    private TransactionalTestConfig.FooRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

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
