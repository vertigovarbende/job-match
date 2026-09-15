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
public final class CandidateSkill extends JmBaseDomain {

    private final Long id;
    private final Long candidateId;
    private final Long skillId;
    private ProficiencyLevel proficiencyLevel;

    public static CandidateSkill create(final Long candidateId, final Long skillId, final ProficiencyLevel proficiencyLevel) {

        Objects.requireNonNull(proficiencyLevel, "proficiencyLevel must not be null");

        return CandidateSkill.builder()
                .id(null)
                .candidateId(candidateId)
                .skillId(skillId)
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