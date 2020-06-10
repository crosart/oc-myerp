package com.dummy.myerp.model.bean.comptabilite.sources;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.stream.Stream;

public class ExternalTestSources {

    static Stream<String> invalidLibelles() {
        return Stream.of("", RandomStringUtils.randomAlphabetic(201));
    }

}
