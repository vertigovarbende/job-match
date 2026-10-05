package com.deveyk.jobmatch.company.unit.domain.event;

import com.deveyk.jobmatch.company.domain.CompanyTargetType;
import com.deveyk.jobmatch.company.domain.event.CompanyMemberAddedEvent;
import com.deveyk.jobmatch.company.domain.event.CompanyMemberRemovedEvent;
import com.deveyk.jobmatch.company.domain.event.CompanyVerifiedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Company Event'leri - targetType Testleri")
class CompanyEventsTargetTypeTest {

    @Test
    @DisplayName("CompanyTargetType.COMPANY audit_log'a yazilan sabit \"COMPANY\" degerini tasir")
    void companyTargetType_hasStableWireValue() {

        assertThat(CompanyTargetType.COMPANY).isEqualTo("COMPANY");

    }

    @Test
    @DisplayName("CompanyVerifiedEvent targetType() olarak CompanyTargetType.COMPANY doner")
    void companyVerifiedEvent_returnsCompanyTargetType() {

        final CompanyVerifiedEvent event = new CompanyVerifiedEvent("actor-1", "1");

        assertThat(event.targetType()).isEqualTo(CompanyTargetType.COMPANY);

    }

    @Test
    @DisplayName("CompanyMemberAddedEvent targetType() olarak CompanyTargetType.COMPANY doner")
    void companyMemberAddedEvent_returnsCompanyTargetType() {

        final CompanyMemberAddedEvent event = new CompanyMemberAddedEvent("actor-1", "1", 2L);

        assertThat(event.targetType()).isEqualTo(CompanyTargetType.COMPANY);

    }

    @Test
    @DisplayName("CompanyMemberRemovedEvent targetType() olarak CompanyTargetType.COMPANY doner")
    void companyMemberRemovedEvent_returnsCompanyTargetType() {

        final CompanyMemberRemovedEvent event = new CompanyMemberRemovedEvent("actor-1", "1", 2L);

        assertThat(event.targetType()).isEqualTo(CompanyTargetType.COMPANY);

    }

}
