package com.dummy.myerp.testbusiness.business;

import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

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

    @Test
    @DisplayName("La référence de l'écriture comptable doit être unique")
    public void givenEcritureComptable_shouldThrowFunctionalExceptionIfReferenceExists() throws FunctionalException {
        // GIVEN
        EcritureComptable vEC = new EcritureComptable();
        vEC.setJournal(new JournalComptable("AC", "Achat"));
        vEC.setDate(new GregorianCalendar(2016, Calendar.MARCH, 11).getTime());
        vEC.setLibelle("Libelle");
        vEC.setReference("AC-2016/00001");
        vEC.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEC.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));

        // WHEN
        // ...

        // THEN
        FunctionalException thrown = assertThrows(
                FunctionalException.class,
                () -> { getBusinessProxy().getComptabiliteManager().checkEcritureComptable(vEC); }
        );
        assertNotNull(thrown.getMessage());
        assertTrue(thrown.getMessage().contains("Une autre écriture comptable existe déjà avec la même référence."));
    }



}

