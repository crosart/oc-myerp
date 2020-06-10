package com.dummy.myerp.consumer.dao.impl.db.dao;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/com/dummy/myerp/consumer/dao/impl/db/dao/bootstrapContext.xml")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/com/dummy/myerp/consumer/dao/impl/db/dao/dropDB.sql")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/com/dummy/myerp/consumer/dao/impl/db/dao/populateDB.sql")
public class ComptabiliteDaoIT {

    private static ComptabiliteDaoImpl dao;

    @BeforeAll
    public static void testSetupBeforeAll() {
        dao = ComptabiliteDaoImpl.getInstance();
    }

    @Test
    @DisplayName("La liste des comptes comptables de la DB doit contenir 7 lignes")
    public void gettingListCompteComptable_shouldReturn7() {
        assertEquals(7, dao.getListCompteComptable().size());
    }



}
