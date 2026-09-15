package com.deveyk.jobmatch.candidate.domain.model;

import com.deveyk.jobmatch.candidate.domain.ProficiencyLevel;
import com.deveyk.jobmatch.shared.domain.model.JmBaseDomain;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public final class CandidateLanguage extends JmBaseDomain {

    private final Long id;
    private final Long candidateId;
    private final Long languageId;
    private ProficiencyLevel proficiencyLevel;

    public static CandidateLanguage create(final Long candidateId, final Long languageId, final ProficiencyLevel proficiencyLevel) {

        Objects.requireNonNull(proficiencyLevel, "proficiencyLevel must not be null");

        return CandidateLanguage.builder()
                .id(null)
                .candidateId(candidateId)
                .languageId(languageId)
                .proficiencyLevel(proficiencyLevel)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void updateProficiency(final ProficiencyLevel proficiencyLevel) {
        Objects.requireNonNull(proficiencyLevel, "proficiencyLevel must not be null");
        this.proficiencyLevel = proficiencyLevel;
    }

    public boolean isOwnedBy(final Long candidateId) {
        return this.candidateId.equals(candidateId);
    }

}