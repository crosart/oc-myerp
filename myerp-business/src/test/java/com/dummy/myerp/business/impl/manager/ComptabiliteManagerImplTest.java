package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.util.Date;

import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("ComptabiliteManagerImplTests")
public class ComptabiliteManagerImplTest {

    private ComptabiliteManagerImpl manager;

    @BeforeEach
    public void initComptabiliteManagerImpl() {
        manager = new ComptabiliteManagerImpl();
    }

    @Nested
    @Tag("checkEcritureComptableUnit")
    @DisplayName("Respect des règles de gestion unitaires par une écriture comptable")
    class checkEcritureComptableUnitTests {
        @Test
        @DisplayName("Vérifier une écriture comptable correcte")
        public void givenEcritureComptable_thenCheckEcritureComptableUnitPasses() throws Exception {
            // GIVEN
            EcritureComptable vEcritureComptable;
            vEcritureComptable = new EcritureComptable();
            vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
            vEcritureComptable.setDate(new Date());
            vEcritureComptable.setLibelle("Libelle");
            vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                    null, new BigDecimal(123),
                    null));
            vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                    null, null,
                    new BigDecimal(123)));

            // WHEN
            manager.checkEcritureComptableUnit(vEcritureComptable);

            // THEN
            // ...
        }

        @Test
        @DisplayName("Rejeter une écriture comptable vide")
        public void checkEcritureComptableUnit_shouldThrowFunctionalException_forAnEmptyEcritureComptable() {
            // GIVEN
            EcritureComptable vEcritureComptable = new EcritureComptable();

            // WHEN
            // ...

            // THEN
            assertThrows(FunctionalException.class, () -> {
                manager.checkEcritureComptableUnit(vEcritureComptable);
            });
        }

        @Test
        @DisplayName("Doit rejeter une écriture comptable non équilibrée (RG2)")
        public void checkEcritureComptableUnit_shouldThrowFunctionalException_forAnUnequalTotalDebitAndTotalCredit() {
            // GIVEN
            EcritureComptable vEcritureComptable;
            vEcritureComptable = new EcritureComptable();
            vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
            vEcritureComptable.setDate(new Date());
            vEcritureComptable.setLibelle("Libelle");
            vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                    null, new BigDecimal(123),
                    null));
            vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                    null, null,
                    new BigDecimal(1234)));

            // WHEN
            // ...

            // THEN
            assertThrows(FunctionalException.class, () -> {
                manager.checkEcritureComptableUnit(vEcritureComptable);
            });
        }

        @Test
        @DisplayName("Doit rejeter une écriture comptable sans ligne en crédit (RG3)")
        public void checkEcritureComptableUnit_shouldThrowFunctionalException_whenEcritureComptableContainsNoCredit() {
            // GIVEN
            EcritureComptable vEcritureComptable;
            vEcritureComptable = new EcritureComptable();
            vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
            vEcritureComptable.setDate(new Date());
            vEcritureComptable.setLibelle("Libelle");
            vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                    null, new BigDecimal(123),
                    null));
            vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                    null, new BigDecimal(123),
                    null));

            // WHEN
            // ...

            // THEN
            assertThrows(FunctionalException.class, () -> {
                manager.checkEcritureComptableUnit(vEcritureComptable);
            });
        }
    }

    @Test
    @Tag("addReference")
    @DisplayName("Doit mettre à jour la référence d'une écriture comptable")
    public void checkAddReference_shouldUpdateReference_whenGivenEcritureComptable() throws Exception {
        // GIVEN
        EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                "Libelle", new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                "Libelle", new BigDecimal(123),
                null));

        // WHEN
        String vReference = manager.addReference(vEcritureComptable);

        // THEN
        assertThat(vReference).isEqualTo("AC-2020/00004");
    }

}
