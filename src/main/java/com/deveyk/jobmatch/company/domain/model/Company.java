package com.deveyk.jobmatch.company.domain.model;

import com.deveyk.jobmatch.company.domain.CompanySize;
import com.deveyk.jobmatch.company.domain.exception.CompanyFieldInvalidException;
import com.deveyk.jobmatch.shared.domain.model.JmBaseDomain;
import com.deveyk.jobmatch.shared.domain.model.Location;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public final class Company extends JmBaseDomain {

    private final Long id;
    private String name;
    private String description;
    private String industry;
    private String website;
    private CompanySize size;
    private Location headquarters;
    private boolean verified;

    public static Company create(final String name) {

        if (name == null || name.isBlank()) {
            throw new CompanyFieldInvalidException("name");
        }

        return Company.builder()
                .id(null)
                .name(name)
                .verified(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void updateProfile(final String name, final String description, final String industry, final String website, final CompanySize size, final Location headquarters) {

        if (name == null || name.isBlank()) {
            throw new CompanyFieldInvalidException("name");
        }

        this.name = name;
        this.description = description;
        this.industry = industry;
        this.website = website;
        this.size = size;
        this.headquarters = headquarters;

    }

    public boolean verify() {
        if (this.verified) {
            return false;
        }

        this.verified = true;
        return true;
    }

}
