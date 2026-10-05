package com.deveyk.jobmatch.company.domain.model;

import com.deveyk.jobmatch.shared.domain.model.JmBaseDomain;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
public final class CompanyMembership extends JmBaseDomain {

    private final Long id;
    private final Long companyId;
    private final Long userId;

    public static CompanyMembership create(final Long companyId, final Long userId) {

        return CompanyMembership.builder()
                .id(null)
                .companyId(companyId)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build();
    }

}
