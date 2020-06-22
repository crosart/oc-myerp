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

public class CompteComptableTest {

    private CompteComptable vCC;

    @BeforeEach
    public void initCompteComptable() {
        vCC = new CompteComptable();
        vCC.setNumero(401);
        vCC.setLibelle("Fournisseurs");
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
        @DisplayName("Un compte comptable sans numéro doit retourner une exception de validation")
        public void nullNumeroShouldFailValidation() {
            // GIVEN
            vCC.setNumero(null);

            // WHEN
            Set<ConstraintViolation<CompteComptable>> violations = validator.validate(vCC);

            // THEN
            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Un compte comptable sans libellé doit retourner une exception de validation")
        public void nullLibelleShouldFailValidation() {
            // GIVEN
            vCC.setLibelle(null);

            // WHEN
            Set<ConstraintViolation<CompteComptable>> violations = validator.validate(vCC);

            // THEN
            assertFalse(violations.isEmpty());
        }

        @ParameterizedTest(name = "Le libellé \"{0}\" non valide doit retourner une exception (1-150 caractères)")
        @MethodSource("com.dummy.myerp.model.bean.comptabilite.sources.ExternalTestSources#invalidLibellesEmptyAnd151")
        @DisplayName("Un compte comptable avec libellé non valide doit retourner une exception de validation")
        public void nonValidLibelleShouldFailValidation(String arg) {
            // GIVEN
            vCC.setLibelle(arg);

            // WHEN
            Set<ConstraintViolation<CompteComptable>> violations = validator.validate(vCC);

            // THEN
            assertFalse(violations.isEmpty());
        }
    }

    @Test
    @DisplayName("Doit retourner un CompteComptable à partir de son numéro et de la liste des comptes comptables")
    public void givenListCompteComptableAndNumero_thenReturnCompteComptable() {
        List<CompteComptable> listCC = new ArrayList<>(Arrays.asList(
                new CompteComptable(401, "Fournisseurs"),
                new CompteComptable(411, "Clients")
        ));
        Integer vNumero = 401;

        assertEquals(listCC.get(0), CompteComptable.getByNumero(listCC, vNumero));
    }

    @Test
    @DisplayName("Vérification de la validité du toString()")
    public void givenCompteComptable_thenBuildValidToString() {
        assertEquals("CompteComptable{numero=401, libelle='Fournisseurs'}", vCC.toString());
    }
}
