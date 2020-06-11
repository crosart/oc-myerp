package com.dummy.myerp.model.bean.comptabilite;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class LigneEcritureComptableTest {

    private LigneEcritureComptable vLEC;

    @BeforeEach
    public void initLigneEcritureComptable() {
        vLEC = new LigneEcritureComptable();
        vLEC.setCompteComptable(new CompteComptable(401, "Fournisseurs"));
        vLEC.setLibelle("Libellé");
    }

    @Nested
    @Tag("Validators")
    @DisplayName("Validators pour les attributs de CompteComptable")
    class checkValidators {

        private Validator validator;

        @BeforeEach
        public void validatorSetUp() {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            validator = factory.getValidator();
        }

        @Test
        @DisplayName("Une ligne d'écriture sans compte comptable doit retourner une exception de validation")
        public void nullCompteComptableShouldFailValidation() {
            // GIVEN
            vLEC.setCompteComptable(null);

            // WHEN
            Set<ConstraintViolation<LigneEcritureComptable>> violations = validator.validate(vLEC);

            // THEN
            assertFalse(violations.isEmpty());
        }

        @ParameterizedTest(name = "Le libellé \"{0}\" non valide doit retourner une exception (200 caractères maximum)")
        @MethodSource("com.dummy.myerp.model.bean.comptabilite.sources.ExternalTestSources#invalidLibelles201")
        @DisplayName("Une ligne d'écriture avec libellé non valide doit retourner une exception de validation")
        public void nonValidLibelleShouldFailValidation(String arg) {
            // GIVEN
            vLEC.setLibelle(arg);

            // WHEN
            Set<ConstraintViolation<LigneEcritureComptable>> violations = validator.validate(vLEC);

            // THEN
            assertFalse(violations.isEmpty());
        }

        @ParameterizedTest(name = "Le montant \"{0}\" non valide au débit doit retourner une exception (13 Chiffres et 2 Décimales au maximum")
        @ValueSource(strings = {"12345678901234", "1.123"})
        @DisplayName("Une ligne d'écriture avec montant au débit non valide doit retourner une exception de validation")
        public void nonValidMontantDebitShouldFailValidation(String arg) {
            // GIVEN
            vLEC.setDebit(new BigDecimal(arg));

            // WHEN
            Set<ConstraintViolation<LigneEcritureComptable>> violations = validator.validate(vLEC);

            // THEN
            assertFalse(violations.isEmpty());
        }

        @ParameterizedTest(name = "Le montant \"{0}\" non valide au crédit doit retourner une exception (13 Chiffres et 2 Décimales au maximum")
        @ValueSource(strings = {"12345678901234", "1.123"})
        @DisplayName("Une ligne d'écriture avec montant au crédit non valide doit retourner une exception de validation")
        public void nonValidMontantCreditShouldFailValidation(String arg) {
            // GIVEN
            vLEC.setDebit(new BigDecimal(arg));

            // WHEN
            Set<ConstraintViolation<LigneEcritureComptable>> violations = validator.validate(vLEC);

            // THEN
            assertFalse(violations.isEmpty());
        }
    }

    @Test
    @DisplayName("Vérification de la validité du toString()")
    public void givenLigneEcritureComptable_thenBuildValidToString() {
        vLEC.setDebit(new BigDecimal("123"));
        assertEquals("LigneEcritureComptable{compteComptable=CompteComptable{numero=401, libelle='Fournisseurs'}, " +
                "libelle='Libellé', debit=123, credit=null}", vLEC.toString());
    }

}
