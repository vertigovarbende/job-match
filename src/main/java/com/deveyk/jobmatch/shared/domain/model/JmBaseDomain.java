package com.deveyk.jobmatch.shared.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
public abstract class JmBaseDomain {

    protected String createdBy;
    protected String updatedBy;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    public abstract Long getId();

    @Override
    public final boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || this.getClass() != other.getClass()) return false;

        final Long id = this.getId();

        return id != null && id.equals(((JmBaseDomain) other).getId());
    }

    @Override
    public final int hashCode() {
        final Long id = this.getId();

        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

}
