package com.deveyk.jobmatch.candidate.unit.domain.model;

import com.deveyk.jobmatch.candidate.domain.ProficiencyLevel;
import com.deveyk.jobmatch.candidate.domain.model.CandidateLanguage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CandidateLanguage - Birim Testleri")
class CandidateLanguageTest {

    private static final Long CANDIDATE_ID = 1L;
    private static final Long LANGUAGE_ID = 10L;

    @Test
    @DisplayName("create() tum alanlari set eder, createdAt'i doldurur ve id'yi null birakir")
    void create_setsAllFields_andLeavesIdNull() {

        final CandidateLanguage candidateLanguage = CandidateLanguage.create(CANDIDATE_ID, LANGUAGE_ID, ProficiencyLevel.ADVANCED);

        assertThat(candidateLanguage.getId()).isNull();
        assertThat(candidateLanguage.getCandidateId()).isEqualTo(CANDIDATE_ID);
        assertThat(candidateLanguage.getLanguageId()).isEqualTo(LANGUAGE_ID);
        assertThat(candidateLanguage.getProficiencyLevel()).isEqualTo(ProficiencyLevel.ADVANCED);
        assertThat(candidateLanguage.getCreatedAt()).isNotNull();

    }

    @Test
    @DisplayName("create() proficiencyLevel null oldugunda NullPointerException firlatir")
    void create_throwsNullPointerException_whenProficiencyLevelIsNull() {

        assertThatThrownBy(() -> CandidateLanguage.create(CANDIDATE_ID, LANGUAGE_ID, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("proficiencyLevel");

    }

    @Test
    @DisplayName("updateProficiency() seviyeyi gunceller")
    void updateProficiency_updatesLevel() {

        final CandidateLanguage candidateLanguage = CandidateLanguage.create(CANDIDATE_ID, LANGUAGE_ID, ProficiencyLevel.BEGINNER);

        candidateLanguage.updateProficiency(ProficiencyLevel.NATIVE);

        assertThat(candidateLanguage.getProficiencyLevel()).isEqualTo(ProficiencyLevel.NATIVE);

    }

    @Test
    @DisplayName("updateProficiency() null oldugunda NullPointerException firlatir")
    void updateProficiency_throwsNullPointerException_whenLevelIsNull() {

        final CandidateLanguage candidateLanguage = CandidateLanguage.create(CANDIDATE_ID, LANGUAGE_ID, ProficiencyLevel.BEGINNER);

        assertThatThrownBy(() -> candidateLanguage.updateProficiency(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("proficiencyLevel");

    }

    @Test
    @DisplayName("isOwnedBy() candidateId eslestiginde true doner")
    void isOwnedBy_returnsTrue_whenCandidateIdMatches() {

        final CandidateLanguage candidateLanguage = CandidateLanguage.create(CANDIDATE_ID, LANGUAGE_ID, ProficiencyLevel.BEGINNER);

        assertThat(candidateLanguage.isOwnedBy(CANDIDATE_ID)).isTrue();

    }

    @Test
    @DisplayName("isOwnedBy() candidateId eslesmediginde false doner")
    void isOwnedBy_returnsFalse_whenCandidateIdDiffers() {

        final CandidateLanguage candidateLanguage = CandidateLanguage.create(CANDIDATE_ID, LANGUAGE_ID, ProficiencyLevel.BEGINNER);

        assertThat(candidateLanguage.isOwnedBy(99L)).isFalse();

    }

}
