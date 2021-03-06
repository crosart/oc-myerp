package com.dummy.myerp.model.bean.comptabilite;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static java.sql.Date.valueOf;
import static org.junit.jupiter.api.Assertions.*;


public class EcritureComptableTest {

    private LigneEcritureComptable createLigne(Integer pCompteComptableNumero, String pDebit, String pCredit) {
        BigDecimal vDebit = pDebit == null ? null : new BigDecimal(pDebit);
        BigDecimal vCredit = pCredit == null ? null : new BigDecimal(pCredit);
        String vLibelle = ObjectUtils.defaultIfNull(vDebit, BigDecimal.ZERO)
                                     .subtract(ObjectUtils.defaultIfNull(vCredit, BigDecimal.ZERO)).toPlainString();
        LigneEcritureComptable vRetour = new LigneEcritureComptable(new CompteComptable(pCompteComptableNumero),
                                                                    vLibelle,
                                                                    vDebit, vCredit);
        return vRetour;
    }

    private EcritureComptable vEC;

    @BeforeEach
    public void initEcritureComptable() {
        vEC = new EcritureComptable();
        vEC.setId(1);
        vEC.setJournal(new JournalComptable("AC", "Achat"));
        vEC.setDate(valueOf(LocalDate.of(2020, 03, 11)));
        vEC.setReference("AC-2020/00001");
        vEC.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                "Test", new BigDecimal(123),
                null));
        vEC.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                "Test", null,
                new BigDecimal(123)));
        vEC.setLibelle("Libelle");
    }

    @Nested
    @Tag("Validators")
    @DisplayName("Validators pour les attributs d'EcritureComptable")
    class checkValidators {

        private Validator validator;

        @BeforeEach
        public void validatorSetUp() {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            validator = factory.getValidator();
        }

        @Test
        @DisplayName("Une écriture comptable sans journal doit retourner une exception de validation")
        public void nullJournalShouldFailValidation() {
            // GIVEN
            vEC.setJournal(null);

            // WHEN
            Set<ConstraintViolation<EcritureComptable>> violations = validator.validate(vEC);

            // THEN
            assertFalse(violations.isEmpty());
        }

        @ParameterizedTest(name = "La référence {0} non valide doit retourner une exception")
        @ValueSource(strings = {"A-2020/00001", "44-2020/00001", "AC-19/00001", "AC-XXXX/00001", "AC-2020/0001", "AC-2020/XXXXX"})
        @DisplayName("Une référence non valide doit retourner une exception de validation")
        public void nonValidReferenceShouldFailValidation(String arg) {
            // GIVEN
            vEC.setReference(arg);

            // WHEN
            Set<ConstraintViolation<EcritureComptable>> violations = validator.validate(vEC);

            // THEN
            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Une écriture comptable sans date doit retourner une exception de validation")
        public void nullDateShouldFailValidation() {
            // GIVEN
            vEC.setDate(null);

            // WHEN
            Set<ConstraintViolation<EcritureComptable>> violations = validator.validate(vEC);

            // THEN
            assertFalse(violations.isEmpty());
        }

        @ParameterizedTest(name = "Le libellé \"{0}\" non valide doit retourner une exception (1-200 caractères)")
        @MethodSource("com.dummy.myerp.model.bean.comptabilite.sources.ExternalTestSources#invalidLibellesEmptyAnd201")
        @DisplayName("Une écriture comptable avec libellé non valide doit retourner une exception de validation")
        public void nonValidLibelleShouldFailValidation(String arg) {
            // GIVEN
            vEC.setLibelle(arg);

            // WHEN
            Set<ConstraintViolation<EcritureComptable>> violations = validator.validate(vEC);

            // THEN
            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Une écriture avec moins de deux lignes d'écriture doit retourner une exception de validation")
        public void lessThanTwoLigneEcritureShouldFailValidation() {
            // GIVEN
            vEC.getListLigneEcriture().clear();
            vEC.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                    null, new BigDecimal(123),
                    null));

            // WHEN
            Set<ConstraintViolation<EcritureComptable>> violations = validator.validate(vEC);

            // THEN
            assertFalse(violations.isEmpty());
        }
    }

    @Test
    @DisplayName("Vérification de l'équilibre d'une écriture comptable")
    public void givenValidEcritureComptable_thenIsEquilibree() {
        EcritureComptable vEC = new EcritureComptable();

        vEC.setLibelle("Equilibrée");
        vEC.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
        vEC.getListLigneEcriture().add(this.createLigne(1, "100.50", "33"));
        vEC.getListLigneEcriture().add(this.createLigne(2, null, "301"));
        vEC.getListLigneEcriture().add(this.createLigne(2, "40", "7"));
        assertTrue(vEC.isEquilibree());
    }

    @Test
    @DisplayName("Vérification de l'équilibre d'une écriture comptable")
    public void givenNotValidEcritureComptable_thenIsNotEquilibree() {
        EcritureComptable vEC = new EcritureComptable();

        vEC.getListLigneEcriture().clear();
        vEC.setLibelle("Non équilibrée");
        vEC.getListLigneEcriture().add(this.createLigne(1, "10", null));
        vEC.getListLigneEcriture().add(this.createLigne(1, "20", "1"));
        vEC.getListLigneEcriture().add(this.createLigne(2, null, "30"));
        vEC.getListLigneEcriture().add(this.createLigne(2, "1", "2"));
        assertFalse(vEC.isEquilibree());
    }

    @Test
    @DisplayName("Vérification du total de débits et crédits d'une écriture comptable")
    public void givenEcritureComptable_thenTotalsShouldBeEqualToSums() {
        EcritureComptable vEC = new EcritureComptable();

        vEC.setLibelle("Equilibrée");
        vEC.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
        vEC.getListLigneEcriture().add(this.createLigne(1, "100.50", "30"));
        vEC.getListLigneEcriture().add(this.createLigne(2, null, "300"));
        vEC.getListLigneEcriture().add(this.createLigne(2, "40", "7"));
        assertEquals(new BigDecimal("341.00"), vEC.getTotalDebit());
        assertEquals(new BigDecimal("337.00"), vEC.getTotalCredit());
    }

    @Test
    @DisplayName("Vérification de la validité du toString()")
    public void givenEcritureComptable_thenBuildValidToString() {
        assertEquals("EcritureComptable{id=1, journal=JournalComptable{code='AC', libelle='Achat'}, reference='AC-2020/00001', date=2020-03-11, libelle='Libelle', totalDebit=123.00, totalCredit=123.00, listLigneEcriture=" +
                "[\nLigneEcritureComptable{compteComptable=CompteComptable{numero=401, libelle='null'}, libelle='Test', debit=123, credit=null}" +
                "\nLigneEcritureComptable{compteComptable=CompteComptable{numero=411, libelle='null'}, libelle='Test', debit=null, credit=123}\n" +
                "]}", vEC.toString());
    }

}
