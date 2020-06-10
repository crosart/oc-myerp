package com.dummy.myerp.consumer.dao.impl.db.dao;

import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.jupiter.api.BeforeAll;
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
        assertDoesNotThrow(() -> {
            dao.getListCompteComptable();
        });
        assertEquals(7, dao.getListCompteComptable().size());
    }

    @Test
    @DisplayName("La liste des journaux comptables de la DB doit contenir 4 lignes")
    public void gettingListJournalComptable_shouldReturn4() {
        assertDoesNotThrow(() -> {
            dao.getListJournalComptable();
        });
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

    @Test
    @DisplayName("La liste des écritures comptables de la DB doit contenir 5 lignes")
    public void gettingListEcritureComptable_shouldReturn5() {
        assertEquals(5, dao.getListEcritureComptable().size());
    }

    @Test
    @DisplayName("Doit retourner l'écriture comptable de référence \"BQ-2016/00003\" à partir de l'ID \"-3\"")
    public void whenGivenId_ifIdExists_shouldReturnEcritureComptable() throws NotFoundException {
        Integer vId = -3;

        assertDoesNotThrow(() -> {
            dao.getEcritureComptable(vId);
        });
        assertEquals("BQ-2016/00003", dao.getEcritureComptable(vId).getReference());
    }

    @Test
    @DisplayName("Doit retourner une Exception si l'écriture comptable d'ID \"1\" n'existe pas")
    public void whenGivenId_ifIdNotExists_shouldThrowNotFoundException() throws NotFoundException {
        Integer vID = 1;

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> { dao.getEcritureComptable(vID); }
        );
        assertNotNull(thrown.getMessage());
        assertTrue(thrown.getMessage().contains("EcritureComptable non trouvée"));
    }

    @Test
    @DisplayName("Doit retourner l'écriture comptable de libellé \"Paiement Facture F110001\" à partir de la référence \"BQ-2016/00003\"")
    public void whenGivenReference_ifReferenceExists_shouldReturnEcritureComptable() throws NotFoundException {
        String vReference = "BQ-2016/00003";

        assertDoesNotThrow(() -> {
            dao.getEcritureComptableByRef(vReference);
        });
        assertEquals("Paiement Facture F110001", dao.getEcritureComptableByRef(vReference).getLibelle());
    }

    @Test
    @DisplayName("Doit retourner une Exception si l'écriture comptable de référence \"BQ-2020/00003\" n'existe pas")
    public void whenGivenReference_ifReferenceNotExists_shouldThrowNotFoundException() throws NotFoundException {
        String vReference = "BQ-2020/00003";

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> { dao.getEcritureComptableByRef(vReference); }
        );
        assertNotNull(thrown.getMessage());
        assertTrue(thrown.getMessage().contains("EcritureComptable non trouvée"));
    }

    @Test
    @DisplayName("Doit insérer une nouvelle écriture comptable dans la DB")
    public void givenEcritureComptable_shouldCreateNewEntryInDatabase() {
        EcritureComptable vEC = new EcritureComptable();
        vEC.setJournal(new JournalComptable("AC", "Achat"));
        vEC.setDate(new GregorianCalendar(2020, Calendar.MARCH, 11).getTime());
        vEC.setLibelle("Libelle");
        vEC.setReference("AC-2020/00001");
        vEC.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                "Test", new BigDecimal(123),
                null));
        vEC.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                "Test", null,
                new BigDecimal(123)));

        assertDoesNotThrow(() -> {
            dao.insertEcritureComptable(vEC);
        });
    }

    @Test
    @DisplayName("Doit mettre à jour une écriture comptable dans la DB")
    public void givenEcritureComptable_shouldUpdateEcritureComptableData() {
        EcritureComptable vEC = new EcritureComptable();
        vEC.setId(-3);
        vEC.setJournal(new JournalComptable("AC", "Achat"));
        vEC.setDate(new GregorianCalendar(2020, Calendar.MARCH, 11).getTime());
        vEC.setLibelle("Libelle");
        vEC.setReference("AC-2020/00001");
        vEC.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                "Test", new BigDecimal(123),
                null));
        vEC.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                "Test", null,
                new BigDecimal(123)));

        assertDoesNotThrow(() -> {
            dao.updateEcritureComptable(vEC);
        });
    }

    @Test
    @DisplayName("Doit supprimer une écriture comptable de la DB")
    public void givenId_shouldDeleteEcritureComptable() {
        Integer vId = -3;

        assertDoesNotThrow(() -> {
            dao.deleteEcritureComptable(vId);
        });
    }

}
