package com.dummy.myerp.testbusiness.business;

import org.junit.jupiter.api.Test;

public class TestIT extends BusinessTestCase {

    @Test
    public void classTest() {
        getBusinessProxy().getComptabiliteManager().getListJournalComptable();
    }

}
