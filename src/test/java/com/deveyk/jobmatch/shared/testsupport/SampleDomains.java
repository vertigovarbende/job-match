package com.deveyk.jobmatch.shared.testsupport;

import com.deveyk.jobmatch.shared.domain.model.JmBaseDomain;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

public final class SampleDomains {

    private SampleDomains() {}

    @Getter
    @SuperBuilder
    public static final class SampleDomain extends JmBaseDomain {

        private final Long id;
        private final String name;

    }

    @Getter
    @SuperBuilder
    public static final class OtherSampleDomain extends JmBaseDomain {

        private final Long id;

    }

}
