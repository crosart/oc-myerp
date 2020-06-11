package com.dummy.myerp.model.bean.comptabilite.sources;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.stream.Stream;

public class ExternalTestSources {

    static Stream<String> invalidLibelles201() {
        return Stream.of(RandomStringUtils.randomAlphabetic(201));
    }

    static Stream<String> invalidLibellesEmptyAnd201() {
        return Stream.of("", RandomStringUtils.randomAlphabetic(201));
    }

    static Stream<String> invalidLibellesEmptyAnd151() {
        return Stream.of("", RandomStringUtils.randomAlphabetic(151));
    }

    static Stream<String> invalidLibellesEmptyAnd6() {
        return Stream.of("", RandomStringUtils.randomAlphabetic(6));
    }

}
