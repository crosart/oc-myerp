package com.dummy.myerp.model.bean.comptabilite;

import java.math.BigDecimal;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

    @Test
    @DisplayName("Vérification de l'équilibre d'une écriture comptable")
    public void givenEcritureComptable_thenIsEquilibree() {
        EcritureComptable vEC = new EcritureComptable();

        vEC.setLibelle("Equilibrée");
        vEC.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
        vEC.getListLigneEcriture().add(this.createLigne(1, "100.50", "33"));
        vEC.getListLigneEcriture().add(this.createLigne(2, null, "301"));
        vEC.getListLigneEcriture().add(this.createLigne(2, "40", "7"));
        assertTrue(vEC.isEquilibree());
        // TODO séparer tests
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

}
