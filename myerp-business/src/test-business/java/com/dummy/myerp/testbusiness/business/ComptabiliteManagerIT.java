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
    @DisplayName("Ne doit rien faire si aucune écriture n'a la même référence")
    public void givenEcritureComptable_ifReferenceNotExists_shouldDoNothing() {
        // GIVEN
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
        // WHEN
        // ...

        // THEN
        assertDoesNotThrow(() -> {
            getBusinessProxy().getComptabiliteManager().checkEcritureComptable(vEC);
        });
    }

    @Test
    @DisplayName("Doit rejeter l'écriture si la référence existe déjà alors que c'est une nouvelle écriture (ID = null)")
    public void givenEcritureComptable_ifReferenceAlreadyExistsAndEcritureIsNew_shouldThrowFunctionalException() throws FunctionalException {
        // GIVEN
        EcritureComptable vEC = new EcritureComptable();
        vEC.setJournal(new JournalComptable("AC", "Achat"));
        vEC.setDate(new GregorianCalendar(2016, Calendar.MARCH, 11).getTime());
        vEC.setLibelle("Libelle");
        vEC.setReference("AC-2016/00001");
        vEC.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                "Test", new BigDecimal(123),
                null));
        vEC.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                "Test", null,
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

    @Test
    @DisplayName("Doit rejeter l'écriture si la référence existe déjà mais que l'ID de l'écriture diffère de celle existante (ID = null)")
    public void givenEcritureComptable_ifReferenceAlreadyExistsAndEcritureIdDiffers_shouldThrowFunctionalException() {
        // GIVEN
        EcritureComptable vEC = new EcritureComptable();
        vEC.setId(2);
        vEC.setJournal(new JournalComptable("AC", "Achat"));
        vEC.setDate(new GregorianCalendar(2016, Calendar.MARCH, 11).getTime());
        vEC.setLibelle("Libelle");
        vEC.setReference("AC-2016/00001");
        vEC.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                "Test", new BigDecimal(123),
                null));
        vEC.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                "Test", null,
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

    @Test
    @DisplayName("Doit insérer une nouvelle écriture comptable dans la DB")
    public void givenEcritureComptable_shouldCreateNewEntryInDatabase() throws FunctionalException {
        // GIVEN
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

        // WHEN
        // ...

        // THEN
        assertDoesNotThrow(() -> {
            getBusinessProxy().getComptabiliteManager().insertEcritureComptable(vEC);
        });
    }

    @Test
    @DisplayName("Doit mettre à jour une écriture comptable dans la DB")
    public void givenEcritureComptable_shouldUpdateEntryInDatabase() {
        // GIVEN
        EcritureComptable vEC = new EcritureComptable();
        vEC.setId(-1);
        vEC.setJournal(new JournalComptable("VE", "Vente")); // Updated Value
        vEC.setDate(new GregorianCalendar(2016, Calendar.JANUARY,1).getTime());
        vEC.setLibelle("Cartouche d'imprimante UPDATED");
        vEC.setReference("VE-2016/00001");

        // WHEN
        // ...

        // THEN
        assertDoesNotThrow(() -> {
            getBusinessProxy().getComptabiliteManager().updateEcritureComptable(vEC);
        });
    }

    @Test
    @DisplayName("Doit mettre à jour une écriture comptable dans la DB")
    public void givenEcritureComptableId_shoulddeleteEntryInDatabase() {
        // GIVEN
        Integer vECID = -1;

        // WHEN
        // ...

        // THEN
        assertDoesNotThrow(() -> {
            getBusinessProxy().getComptabiliteManager().deleteEcritureComptable(vECID);
        });
    }

}

