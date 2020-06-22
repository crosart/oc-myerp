package com.dummy.myerp.model.bean.comptabilite;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class JournalComptableTest {

    private JournalComptable vJC;

    @BeforeEach
    public void initJournalComptable() {
        vJC = new JournalComptable();
        vJC.setCode("AC");
        vJC.setLibelle("Achat");
    }

    @Nested
    @Tag("Validators")
    @DisplayName("Validators pour les attributs de JournalComptable")
    class checkValidators {

        private Validator validator;

        @BeforeEach
        public void validatorSetUp() {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            validator = factory.getValidator();
        }

        @Test
        @DisplayName("Un journal comptable sans code doit retourner une exception de validation")
        public void nullCodeShouldFailValidation() {
            // GIVEN
            vJC.setCode(null);

            // WHEN
            Set<ConstraintViolation<JournalComptable>> violations = validator.validate(vJC);

            // THEN
            assertFalse(violations.isEmpty());
        }

        @ParameterizedTest(name = "Le code \"{0}\" non valide doit retourner une exception (1-5 caractères)")
        @MethodSource("com.dummy.myerp.model.bean.comptabilite.sources.ExternalTestSources#invalidLibellesEmptyAnd6")
        @DisplayName("Un compte comptable avec code non valide doit retourner une exception de validation")
        public void nonValidCodeShouldFailValidation(String arg) {
            // GIVEN
            vJC.setCode(arg);

            // WHEN
            Set<ConstraintViolation<JournalComptable>> violations = validator.validate(vJC);

            // THEN
            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Un journal comptable sans libelle doit retourner une exception de validation")
        public void nullLibelleShouldFailValidation() {
            // GIVEN
            vJC.setLibelle(null);

            // WHEN
            Set<ConstraintViolation<JournalComptable>> violations = validator.validate(vJC);

            // THEN
            assertFalse(violations.isEmpty());
        }

        @ParameterizedTest(name = "Le libellé \"{0}\" non valide doit retourner une exception (1-150 caractères)")
        @MethodSource("com.dummy.myerp.model.bean.comptabilite.sources.ExternalTestSources#invalidLibellesEmptyAnd151")
        @DisplayName("Un compte comptable avec libellé non valide doit retourner une exception de validation")
        public void nonValidLibelleShouldFailValidation(String arg) {
            // GIVEN
            vJC.setLibelle(arg);

            // WHEN
            Set<ConstraintViolation<JournalComptable>> violations = validator.validate(vJC);

            // THEN
            assertFalse(violations.isEmpty());
        }
    }

    @Test
    @DisplayName("Doit retourner un JournalComptable à partir de son code et de la liste des journaux comptables")
    public void givenListCompteComptableAndNumero_thenReturnCompteComptable() {
        List<JournalComptable> listJC = new ArrayList<>(Arrays.asList(
                new JournalComptable("AC", "Achat"),
                new JournalComptable("VE", "Vente")
        ));
        String vCode = "AC";

        assertEquals(listJC.get(0), JournalComptable.getByCode(listJC, vCode));
    }

    @Test
    @DisplayName("Vérification de la validité du toString()")
    public void givenCompteComptable_thenBuildValidToString() {
        assertEquals("JournalComptable{code='AC', libelle='Achat'}", vJC.toString());
    }
}
