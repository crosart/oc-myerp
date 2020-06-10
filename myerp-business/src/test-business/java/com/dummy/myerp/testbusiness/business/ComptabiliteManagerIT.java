package com.dummy.myerp.testbusiness.business;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComptabiliteManagerIT extends BusinessTestCase {

    @Test
    @DisplayName("La liste des comptes comptables de la DB doit contenir 7 lignes")
    public void gettingListCompteComptable_shouldReturn7() {
        assertEquals(7, getBusinessProxy().getComptabiliteManager().getListCompteComptable().size());
    }

    @Test
    public void  gettingListJournalComptable_shouldReturn4() {
        assertEquals(4, getBusinessProxy().getComptabiliteManager().getListJournalComptable().size());
    }

    @Test
    public void gettingListEcritureComptable_shouldReturn5() {
        assertEquals(5, getBusinessProxy().getComptabiliteManager().getListEcritureComptable().size());
    }

}
