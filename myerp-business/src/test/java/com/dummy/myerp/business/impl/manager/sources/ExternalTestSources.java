package com.dummy.myerp.business.impl.manager.sources;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.stream.Stream;

public class ExternalTestSources {

    static Stream<String> invalidLibelles() {
        return Stream.of("", RandomStringUtils.randomAlphabetic(201));
    }

}
