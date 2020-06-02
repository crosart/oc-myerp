package com.dummy.myerp.business.impl.manager;

import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.consumer.dao.impl.db.dao.ComptabiliteDaoImpl;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@Tag("ComptabiliteManagerImplTests")
@ExtendWith(MockitoExtension.class)
public class ComptabiliteManagerImplTest {

    @Nested
    @Tag("checkEcritureComptableUnit")
    @DisplayName("Respect des règles de gestion unitaires par une écriture comptable")
    class checkEcritureComptableUnitTests {

        private ComptabiliteManagerImpl manager;

        @BeforeEach
        public void initComptabiliteManagerImpl() {
            manager = new ComptabiliteManagerImpl();
        }

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

    @Nested
    @Tag("addReference")
    @DisplayName("Création et ajout d'une référence pour une écriture comptable")
    class addReferenceTests {

        private ComptabiliteManagerImpl manager = new ComptabiliteManagerImpl();
        private DaoProxy daoProxyMock = mock(DaoProxy.class, Mockito.RETURNS_DEEP_STUBS);

        @BeforeEach
        public void initAbstractBusinessManager() {
            AbstractBusinessManager.configure(null, daoProxyMock, TransactionManager.getInstance());
        }

        @Test
        @DisplayName("Doit retourner une référence valide")
        public void givenEcritureComptable_thenBuiltReferenceShouldBeValid() throws FunctionalException, NotFoundException {

            // GIVEN
            EcritureComptable vEcritureComptable = new EcritureComptable();
            vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
            vEcritureComptable.setDate(new GregorianCalendar(2020, Calendar.MARCH, 11).getTime());
            vEcritureComptable.setLibelle("Libelle");
            vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                    "Libelle", new BigDecimal(123),
                    null));
            vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                    "Libelle", new BigDecimal(123),
                    null));

            SequenceEcritureComptable vSequenceEcritureComptable = new SequenceEcritureComptable();
            vSequenceEcritureComptable.setCodeJournal("AC");
            vSequenceEcritureComptable.setAnnee(2020);
            vSequenceEcritureComptable.setDerniereValeur(3);

            when(daoProxyMock.getComptabiliteDao().getSequenceJournalEcritureComptable(vEcritureComptable))
                    .thenReturn(vSequenceEcritureComptable);

            // WHEN
            manager.addReference(vEcritureComptable);

            // THEN
            assertEquals("AC-2020/00003", vEcritureComptable.getReference());
        }

        @Test
        @DisplayName("Doit rejeter la référence si la séquence est au MAX")
        public void givenEcritureComptable_whenSequenceMax_ThenThrowFunctionalException() throws NotFoundException {

            // GIVEN
            EcritureComptable vEcritureComptable = new EcritureComptable();
            vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
            vEcritureComptable.setDate(new GregorianCalendar(2020, Calendar.MARCH, 11).getTime());
            vEcritureComptable.setLibelle("Libelle");
            vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                    "Libelle", new BigDecimal(123),
                    null));
            vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                    "Libelle", new BigDecimal(123),
                    null));

            SequenceEcritureComptable vSequenceEcritureComptable = new SequenceEcritureComptable();
            vSequenceEcritureComptable.setCodeJournal("AC");
            vSequenceEcritureComptable.setAnnee(2020);
            vSequenceEcritureComptable.setDerniereValeur(99999);

            when(daoProxyMock.getComptabiliteDao().getSequenceJournalEcritureComptable(vEcritureComptable))
                    .thenReturn(vSequenceEcritureComptable);

            // WHEN
            // ...

            // THEN
            assertThrows(FunctionalException.class, () -> {
                manager.addReference(vEcritureComptable);
            });

        }

        @Test
        @DisplayName("Doit incrémenter la séquence après création de la référence")
        public void givenEcritureComptable_whenReferenceBuilt_ThenIncrementDerniereValeur() throws FunctionalException, NotFoundException {

            // GIVEN
            EcritureComptable vEcritureComptable = new EcritureComptable();
            vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
            vEcritureComptable.setDate(new GregorianCalendar(2020, Calendar.MARCH, 11).getTime());
            vEcritureComptable.setLibelle("Libelle");
            vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                    "Libelle", new BigDecimal(123),
                    null));
            vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                    "Libelle", new BigDecimal(123),
                    null));

            SequenceEcritureComptable vSequenceEcritureComptable = new SequenceEcritureComptable();
            vSequenceEcritureComptable.setCodeJournal("AC");
            vSequenceEcritureComptable.setAnnee(2020);
            vSequenceEcritureComptable.setDerniereValeur(3);

            when(daoProxyMock.getComptabiliteDao().getSequenceJournalEcritureComptable(vEcritureComptable))
                    .thenReturn(vSequenceEcritureComptable);

            // WHEN
            manager.addReference(vEcritureComptable);

            // THEN
            assertEquals(4, vSequenceEcritureComptable.getDerniereValeur());
        }
    }

}
