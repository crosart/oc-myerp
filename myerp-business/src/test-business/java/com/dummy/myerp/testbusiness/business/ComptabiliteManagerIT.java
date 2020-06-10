package com.dummy.myerp.testbusiness.business;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/com/dummy/myerp/testbusiness/business/bootstrapContext.xml")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/com/dummy/myerp/testbusiness/business/dropDB.sql")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/com/dummy/myerp/testbusiness/business/populateDB.sql")
public class ComptabiliteManagerIT extends BusinessTestCase {

    @Test
    @DisplayName("La liste des comptes comptables de la DB doit contenir 7 lignes")
    public void gettingListCompteComptable_shouldReturn7() {
        assertEquals(7, getBusinessProxy().getComptabiliteManager().getListCompteComptable().size());
    }

    @Test
    @DisplayName("La liste des journaux comptables de la DB doit contenir 4 lignes")
    public void  gettingListJournalComptable_shouldReturn4() {
        assertEquals(4, getBusinessProxy().getComptabiliteManager().getListJournalComptable().size());
    }

    @Test
    @DisplayName("La liste des écritures comptables de la DB doit contenir 5 lignes")
    public void gettingListEcritureComptable_shouldReturn5() {
        assertEquals(5, getBusinessProxy().getComptabiliteManager().getListEcritureComptable().size());
    }





}

