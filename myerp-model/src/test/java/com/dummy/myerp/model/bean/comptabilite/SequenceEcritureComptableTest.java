package com.dummy.myerp.model.bean.comptabilite;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SequenceEcritureComptableTest {

    @Test
    @DisplayName("Vérification de la validité du toString()")
    public void givenSequenceEcritureComptable_thenBuildValidToString() {
        SequenceEcritureComptable vSEC = new SequenceEcritureComptable("AC", 2016, 40);

        assertEquals("SequenceEcritureComptable{codeJournal='AC', annee=2016, derniereValeur=40}", vSEC.toString());
    }

}
