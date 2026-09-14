package com.deveyk.jobmatch.shared.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode
public abstract class JmBaseDomain {

    protected String createdBy;
    protected String updatedBy;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

}
