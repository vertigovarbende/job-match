package com.deveyk.jobmatch.candidate.unit.domain.model;

import com.deveyk.jobmatch.candidate.domain.ProficiencyLevel;
import com.deveyk.jobmatch.candidate.domain.model.CandidateSkill;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CandidateSkill - Birim Testleri")
class CandidateSkillTest {

    private static final Long CANDIDATE_ID = 1L;
    private static final Long SKILL_ID = 20L;

    @Test
    @DisplayName("create() tum alanlari set eder, createdAt'i doldurur ve id'yi null birakir")
    void create_setsAllFields_andLeavesIdNull() {

        final CandidateSkill candidateSkill = CandidateSkill.create(CANDIDATE_ID, SKILL_ID, ProficiencyLevel.EXPERT);

        assertThat(candidateSkill.getId()).isNull();
        assertThat(candidateSkill.getCandidateId()).isEqualTo(CANDIDATE_ID);
        assertThat(candidateSkill.getSkillId()).isEqualTo(SKILL_ID);
        assertThat(candidateSkill.getProficiencyLevel()).isEqualTo(ProficiencyLevel.EXPERT);
        assertThat(candidateSkill.getCreatedAt()).isNotNull();

    }

    @Test
    @DisplayName("create() proficiencyLevel null oldugunda NullPointerException firlatir")
    void create_throwsNullPointerException_whenProficiencyLevelIsNull() {

        assertThatThrownBy(() -> CandidateSkill.create(CANDIDATE_ID, SKILL_ID, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("proficiencyLevel");

    }

    @Test
    @DisplayName("updateProficiency() seviyeyi gunceller")
    void updateProficiency_updatesLevel() {

        final CandidateSkill candidateSkill = CandidateSkill.create(CANDIDATE_ID, SKILL_ID, ProficiencyLevel.BEGINNER);

        candidateSkill.updateProficiency(ProficiencyLevel.EXPERT);

        assertThat(candidateSkill.getProficiencyLevel()).isEqualTo(ProficiencyLevel.EXPERT);

    }

    @Test
    @DisplayName("updateProficiency() null oldugunda NullPointerException firlatir")
    void updateProficiency_throwsNullPointerException_whenLevelIsNull() {

        final CandidateSkill candidateSkill = CandidateSkill.create(CANDIDATE_ID, SKILL_ID, ProficiencyLevel.BEGINNER);

        assertThatThrownBy(() -> candidateSkill.updateProficiency(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("proficiencyLevel");

    }

    @Test
    @DisplayName("isOwnedBy() candidateId eslestiginde true doner")
    void isOwnedBy_returnsTrue_whenCandidateIdMatches() {

        final CandidateSkill candidateSkill = CandidateSkill.create(CANDIDATE_ID, SKILL_ID, ProficiencyLevel.BEGINNER);

        assertThat(candidateSkill.isOwnedBy(CANDIDATE_ID)).isTrue();

    }

    @Test
    @DisplayName("isOwnedBy() candidateId eslesmediginde false doner")
    void isOwnedBy_returnsFalse_whenCandidateIdDiffers() {

        final CandidateSkill candidateSkill = CandidateSkill.create(CANDIDATE_ID, SKILL_ID, ProficiencyLevel.BEGINNER);

        assertThat(candidateSkill.isOwnedBy(99L)).isFalse();

    }

}
