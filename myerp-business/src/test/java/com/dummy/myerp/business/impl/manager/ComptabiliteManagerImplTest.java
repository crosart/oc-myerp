package com.dummy.myerp.business.impl.manager;

import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@Tag("ComptabiliteManagerImplTests")
@ExtendWith(MockitoExtension.class)
public class ComptabiliteManagerImplTest {

    private EcritureComptable vEC;

    @BeforeEach
    public void initEcritureComptable() {
        vEC = new EcritureComptable();
        vEC.setJournal(new JournalComptable("AC", "Achat"));
        vEC.setDate(new GregorianCalendar(2020, Calendar.MARCH, 11).getTime());
        vEC.setLibelle("Libelle");
        vEC.setReference("AC-2020/00001");
        vEC.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEC.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));
    }

    @Nested
    @Tag("checkEcritureComptable")
    @DisplayName("Vérifie la validité de l'écriture comptable")
    class checkEcritureComptableTests {

        @Spy
        private ComptabiliteManagerImpl manager;

        @Test
        @DisplayName("Vérifier les règles de validité de l'écriture comptable")
        public void givenValidNonAlreadyExistingEcritureComptable_shouldDoNothing() throws FunctionalException {
            // GIVEN
            // ...

            // WHEN
            doNothing().when(manager).checkEcritureComptableUnit(vEC);
            doNothing().when(manager).checkEcritureComptableContext(vEC);

            // THEN
            assertDoesNotThrow(() -> {
                manager.checkEcritureComptable(vEC);
            });
        }

        @Test
        @DisplayName("Doit Rejeter une écriture comptable déjà existante")
        public void whenEcritureComptableAlreadyExists_shouldThrowFunctionalException() throws FunctionalException {
            // GIVEN
            // ...

            // WHEN
            doNothing().when(manager).checkEcritureComptableUnit(vEC);
            doThrow(FunctionalException.class).when(manager).checkEcritureComptableContext(vEC);

            // THEN
            assertThrows(FunctionalException.class, () -> {
                manager.checkEcritureComptable(vEC);
            });
        }

        @Test
        @DisplayName("Doit rejeter une écriture comptable non valide")
        public void whenEcritureComptableNotValid_shouldThrowFunctionalException() throws FunctionalException {
            // GIVEN
            // ...

            // WHEN
            doThrow(FunctionalException.class).when(manager).checkEcritureComptableUnit(vEC);
            lenient().doNothing().when(manager).checkEcritureComptableContext(vEC);

            // THEN
            assertThrows(FunctionalException.class, () -> {
                manager.checkEcritureComptable(vEC);
            });
        }

    }

    @Nested
    @Tag("checkEcritureComptableUnit")
    @DisplayName("Respect des règles de gestion pour une écriture comptable")
    class checkEcritureComptableUnitTests {

        private ComptabiliteManagerImpl manager;

        @BeforeEach
        public void initComptabiliteManagerImpl() {
            manager = new ComptabiliteManagerImpl();
        }


        @Test
        @DisplayName("Une écriture qui ne respecte pas une règle de validation doit retourner une exception")
        public void nonValidEcritureComptableShouldThrowFunctionalException() {
            //GIVEN
            vEC.setJournal(null);

            FunctionalException thrown = assertThrows(
                    FunctionalException.class,
                    () -> { manager.checkEcritureComptableUnit(vEC); }
            );
            assertNotNull(thrown.getCause());
            assertNotNull(thrown.getCause().getMessage());
            assertTrue(thrown.getCause().getMessage().contains("L'écriture comptable ne respecte pas les contraintes de validation"));
        }

        @Test
        @DisplayName("Vérifier la validité unitaire d'une écriture comptable correcte")
        public void givenEcritureComptable_thenCheckEcritureComptableUnitPasses() throws Exception {
            // GIVEN
            // ...

            // WHEN
            // ...

            // THEN
            assertDoesNotThrow(() -> {
                manager.checkEcritureComptableUnit(vEC);
            });
        }

        @Test
        @DisplayName("Doit rejeter une écriture comptable non équilibrée (RG2)")
        public void checkEcritureComptableUnit_shouldThrowFunctionalException_forAnUnequalTotalDebitAndTotalCredit() {
            // GIVEN
            vEC.getListLigneEcriture().clear();
            vEC.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                    null, new BigDecimal(123),
                    null));
            vEC.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                    null, null,
                    new BigDecimal(1234)));

            // WHEN
            // ...

            // THEN
            FunctionalException thrown = assertThrows(
                    FunctionalException.class,
                    () -> { manager.checkEcritureComptableUnit(vEC); }
            );
            assertNotNull(thrown.getMessage());
            assertTrue(thrown.getMessage().contains("L'écriture comptable n'est pas équilibrée."));

        }

        @Test
        @DisplayName("Doit rejeter une écriture comptable équilibrée mais sans crédit (RG3)")
        public void checkEcritureComptableUnit_shouldThrowFunctionalException_whenBalancedEcritureComptableContainsNoDebit() {
            // GIVEN
            vEC.getListLigneEcriture().clear();
            vEC.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                    null, new BigDecimal(123),
                    null));
            vEC.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                    null, new BigDecimal(-123),
                    null));

            // WHEN
            // ...

            // THEN
            FunctionalException thrown = assertThrows(
                    FunctionalException.class,
                    () -> { manager.checkEcritureComptableUnit(vEC); }
            );
            assertNotNull(thrown.getMessage());
            assertTrue(thrown.getMessage().contains("L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit."));
        }

        @Test
        @DisplayName("Doit rejeter une écriture comptable équilibrée mais sans débit (RG3)")
        public void checkEcritureComptableUnit_shouldThrowFunctionalException_whenBalancedEcritureComptableContainsNoCredit() {
            // GIVEN
            vEC.getListLigneEcriture().clear();
            vEC.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                    null, null,
                    new BigDecimal(123)));
            vEC.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                    null, null,
                    new BigDecimal(-123)));

            // WHEN
            // ...

            // THEN
            FunctionalException thrown = assertThrows(
                    FunctionalException.class,
                    () -> { manager.checkEcritureComptableUnit(vEC); }
            );
            assertNotNull(thrown.getMessage());
            assertTrue(thrown.getMessage().contains("L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit."));
        }

        @ParameterizedTest(name = "La référence \"{0}\" ne correspond pas aux informations de l'écriture comptable")
        @ValueSource(strings = {"BR-2020/00001", "AC-2019/00001"})
        @DisplayName("Une référence non valide doit retourner une exception de validation")
        public void checkEcritureComptableUnit_shouldThrowFunctionalException_whenReferenceTokensNotMatchingWithEcritureComptable(String arg) {
            // GIVEN
            vEC.setReference(arg);

            // WHEN
            // ...

            // THEN
            FunctionalException thrown = assertThrows(
                    FunctionalException.class,
                    () -> { manager.checkEcritureComptableUnit(vEC); }
            );
            assertNotNull(thrown.getMessage());
            assertTrue(thrown.getMessage().contains("(RG5)"));
        }


    }

    @Nested
    @Tag("addReference")
    @DisplayName("Création et ajout d'une référence pour une écriture comptable")
    class addReferenceTests {

        private ComptabiliteManagerImpl manager = new ComptabiliteManagerImpl();
        private DaoProxy daoProxyMock = mock(DaoProxy.class, RETURNS_DEEP_STUBS);

        @BeforeEach
        public void initAbstractBusinessManager() {
            AbstractBusinessManager.configure(null, daoProxyMock, TransactionManager.getInstance());
        }

        @Test
        @DisplayName("Doit retourner une référence valide")
        public void givenEcritureComptable_thenBuiltReferenceShouldBeValid() throws FunctionalException, NotFoundException {
            // GIVEN
            vEC.setReference(null);

            SequenceEcritureComptable vSEC = new SequenceEcritureComptable();
            vSEC.setCodeJournal("AC");
            vSEC.setAnnee(2020);
            vSEC.setDerniereValeur(3);

            when(daoProxyMock.getComptabiliteDao().getSequenceJournalEcritureComptable(vEC))
                    .thenReturn(vSEC);

            // WHEN
            manager.addReference(vEC);

            // THEN
            assertEquals("AC-2020/00003", vEC.getReference());
            verify(daoProxyMock.getComptabiliteDao()).updateSequenceEcritureComptable(any());
        }

        @Test
        @DisplayName("Doit rejeter la référence si la séquence est au MAX")
        public void givenEcritureComptable_whenSequenceMax_ThenThrowFunctionalException() throws NotFoundException {
            vEC.setReference(null);

            SequenceEcritureComptable vSEC = new SequenceEcritureComptable();
            vSEC.setCodeJournal("AC");
            vSEC.setAnnee(2020);
            vSEC.setDerniereValeur(99999);

            when(daoProxyMock.getComptabiliteDao().getSequenceJournalEcritureComptable(vEC))
                    .thenReturn(vSEC);

            // WHEN
            // ...

            // THEN
            FunctionalException thrown = assertThrows(
                    FunctionalException.class,
                    () -> { manager.addReference(vEC); }
            );
            assertNotNull(thrown.getMessage());
            assertTrue(thrown.getMessage().contains("Ce journal ne peut pas contenir plus d'écritures pour cette année"));
        }

        @Test
        @DisplayName("Doit incrémenter la séquence après création de la référence")
        public void givenEcritureComptable_whenReferenceBuilt_ThenIncrementDerniereValeur() throws FunctionalException, NotFoundException {
            // GIVEN
            vEC.setReference(null);

            SequenceEcritureComptable vSEC = new SequenceEcritureComptable();
            vSEC.setCodeJournal("AC");
            vSEC.setAnnee(2020);
            vSEC.setDerniereValeur(3);

            when(daoProxyMock.getComptabiliteDao().getSequenceJournalEcritureComptable(vEC))
                    .thenReturn(vSEC);

            // WHEN
            manager.addReference(vEC);

            // THEN
            assertEquals(4, vSEC.getDerniereValeur());
        }

        @Test
        @DisplayName("Doit retourner une référence à 1 conforme si la séquence n'existe pas")
        public void givenEcritureComptable_whenSequenceNonExists_ThenBuildInitialReference() throws FunctionalException, NotFoundException {

            // GIVEN
            vEC.setReference(null);

            when(daoProxyMock.getComptabiliteDao().getSequenceJournalEcritureComptable(vEC))
                    .thenThrow(NotFoundException.class);

            // WHEN
            manager.addReference(vEC);

            // THEN
            assertEquals("AC-2020/00001", vEC.getReference());
        }
    }

}
