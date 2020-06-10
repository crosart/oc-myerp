package com.dummy.myerp.consumer.dao.impl.db.dao;

import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    @DisplayName("La liste des journaux comptables de la DB doit contenir 4 lignes")
    public void gettingListJournalComptable_shouldReturn4() {
        assertEquals(4, dao.getListJournalComptable().size());
    }

    @Test
    @DisplayName("Doit retourner une séquence d'écriture comptable à partir d'une écriture comptable")
    public void gettingListSequenceEcritureComptable_shouldReturn4() throws NotFoundException {
        EcritureComptable vEC = new EcritureComptable();
        vEC.setJournal(new JournalComptable("AC", "Achat"));
        vEC.setDate(new GregorianCalendar(2016, Calendar.MARCH, 11).getTime());

        assertDoesNotThrow(() -> {
            dao.getSequenceJournalEcritureComptable(vEC);
        });
        assertEquals(40, dao.getSequenceJournalEcritureComptable(vEC).getDerniereValeur());
    }

    @Test
    @DisplayName("Doit retourner une Exception si la séquence n'existe pas")
    public void gettingListSequenceEcritureComptable_ifSequenceNotExists_shouldThroNotFoundException() throws NotFoundException {
        EcritureComptable vEC = new EcritureComptable();
        vEC.setJournal(new JournalComptable("AC", "Achat"));
        vEC.setDate(new GregorianCalendar(2020, Calendar.MARCH, 11).getTime());

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> { dao.getSequenceJournalEcritureComptable(vEC); }
        );
        assertNotNull(thrown.getMessage());
        assertTrue(thrown.getMessage().contains("SequenceEcritureComptable non trouvée"));
    }

    @Test
    @DisplayName("Doit insérer une nouvelle séquence d'écriture comptable dans la DB")
    public void givenJournalCodeAndYear_shouldCreateNewEntryInDatabase() {
        String vJournalCode = "AC";
        Integer vAnnee = 2020;

        assertDoesNotThrow(() -> {
            dao.insertSequenceEcritureComptable(vJournalCode, vAnnee);
        });
    }

    @Test
    @DisplayName("Doit mettre à jour une séquence d'écriture comptable dans la DB")
    public void givenSequenceEcritureComptable_shouldUpdateDerniereValeur() {
        SequenceEcritureComptable vSEC = new SequenceEcritureComptable();
        vSEC.setCodeJournal("AC");
        vSEC.setAnnee(2016);
        vSEC.setDerniereValeur(99);

        assertDoesNotThrow(() -> {
            dao.updateSequenceEcritureComptable(vSEC);
        });
    }

}
