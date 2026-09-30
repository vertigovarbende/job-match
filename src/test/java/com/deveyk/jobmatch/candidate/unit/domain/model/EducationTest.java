package com.deveyk.jobmatch.candidate.unit.domain.model;

import com.deveyk.jobmatch.candidate.domain.exception.CandidateFieldInvalidException;
import com.deveyk.jobmatch.candidate.domain.model.Education;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Education - Birim Testleri")
class EducationTest {

    private static final Long CANDIDATE_ID = 1L;
    private static final LocalDate START_DATE = LocalDate.of(2016, 9, 1);
    private static final LocalDate END_DATE = LocalDate.of(2020, 6, 30);

    @Test
    @DisplayName("create() tum alanlari set eder, createdAt'i doldurur ve id'yi null birakir")
    void create_setsAllFields_andLeavesIdNull() {

        final Education education = Education.create(CANDIDATE_ID, "Istanbul Teknik Universitesi",
                "Lisans", "Bilgisayar Muhendisligi", START_DATE, END_DATE, "Onur derecesiyle mezun");

        assertThat(education.getId()).isNull();
        assertThat(education.getCandidateId()).isEqualTo(CANDIDATE_ID);
        assertThat(education.getInstitution()).isEqualTo("Istanbul Teknik Universitesi");
        assertThat(education.getDegree()).isEqualTo("Lisans");
        assertThat(education.getFieldOfStudy()).isEqualTo("Bilgisayar Muhendisligi");
        assertThat(education.getStartDate()).isEqualTo(START_DATE);
        assertThat(education.getEndDate()).isEqualTo(END_DATE);
        assertThat(education.getDescription()).isEqualTo("Onur derecesiyle mezun");
        assertThat(education.getCreatedAt()).isNotNull();

    }

    @Test
    @DisplayName("create() endDate olmadan da olusturulabilir (devam eden egitim)")
    void create_allowsNullEndDate() {

        final Education education = education(null);

        assertThat(education.getEndDate()).isNull();

    }

    @Test
    @DisplayName("create() institution bos oldugunda CandidateFieldInvalidException firlatir")
    void create_throwsCandidateFieldInvalidException_whenInstitutionIsBlank() {

        assertThatThrownBy(() -> Education.create(CANDIDATE_ID, " ", "Lisans", "Bilgisayar Muhendisligi",
                START_DATE, END_DATE, null))
                .isInstanceOf(CandidateFieldInvalidException.class)
                .hasMessageContaining("institution");

    }

    @Test
    @DisplayName("create() startDate null oldugunda CandidateFieldInvalidException firlatir")
    void create_throwsCandidateFieldInvalidException_whenStartDateIsNull() {

        assertThatThrownBy(() -> Education.create(CANDIDATE_ID, "Istanbul Teknik Universitesi", "Lisans",
                "Bilgisayar Muhendisligi", null, END_DATE, null))
                .isInstanceOf(CandidateFieldInvalidException.class)
                .hasMessageContaining("startDate");

    }

    @Test
    @DisplayName("create() endDate startDate'den once oldugunda CandidateFieldInvalidException firlatir")
    void create_throwsCandidateFieldInvalidException_whenEndDateBeforeStartDate() {

        assertThatThrownBy(() -> Education.create(CANDIDATE_ID, "Istanbul Teknik Universitesi", "Lisans",
                "Bilgisayar Muhendisligi", START_DATE, START_DATE.minusDays(1), null))
                .isInstanceOf(CandidateFieldInvalidException.class)
                .hasMessageContaining("endDate");

    }

    @Test
    @DisplayName("update() tum alanlari gunceller, id ve candidateId degismez")
    void update_updatesAllFields_andLeavesIdAndCandidateIdUnchanged() {

        final Education education = education(END_DATE);
        final LocalDate newStartDate = LocalDate.of(2021, 9, 1);
        final LocalDate newEndDate = LocalDate.of(2023, 6, 30);

        education.update("Bogazici Universitesi", "Yuksek Lisans", "Yazilim Muhendisligi",
                newStartDate, newEndDate, "Tez: Dagitik sistemler");

        assertThat(education.getInstitution()).isEqualTo("Bogazici Universitesi");
        assertThat(education.getDegree()).isEqualTo("Yuksek Lisans");
        assertThat(education.getFieldOfStudy()).isEqualTo("Yazilim Muhendisligi");
        assertThat(education.getStartDate()).isEqualTo(newStartDate);
        assertThat(education.getEndDate()).isEqualTo(newEndDate);
        assertThat(education.getDescription()).isEqualTo("Tez: Dagitik sistemler");
        assertThat(education.getId()).isNull();
        assertThat(education.getCandidateId()).isEqualTo(CANDIDATE_ID);

    }

    @Test
    @DisplayName("update() institution bos oldugunda CandidateFieldInvalidException firlatir")
    void update_throwsCandidateFieldInvalidException_whenInstitutionIsBlank() {

        final Education education = education(END_DATE);

        assertThatThrownBy(() -> education.update(" ", "Lisans", "Bilgisayar Muhendisligi", START_DATE, END_DATE, null))
                .isInstanceOf(CandidateFieldInvalidException.class)
                .hasMessageContaining("institution");

    }

    @Test
    @DisplayName("isOwnedBy() candidateId eslestiginde true doner")
    void isOwnedBy_returnsTrue_whenCandidateIdMatches() {

        final Education education = education(END_DATE);

        assertThat(education.isOwnedBy(CANDIDATE_ID)).isTrue();

    }

    @Test
    @DisplayName("isOwnedBy() candidateId eslesmediginde false doner")
    void isOwnedBy_returnsFalse_whenCandidateIdDiffers() {

        final Education education = education(END_DATE);

        assertThat(education.isOwnedBy(99L)).isFalse();

    }

    @Test
    @DisplayName("isCurrent() endDate null oldugunda true doner (Ongoing arayuzu)")
    void isCurrent_returnsTrue_whenEndDateIsNull() {

        final Education education = education(null);

        assertThat(education.isCurrent()).isTrue();

    }

    @Test
    @DisplayName("isCurrent() endDate dolu oldugunda false doner (Ongoing arayuzu)")
    void isCurrent_returnsFalse_whenEndDateIsSet() {

        final Education education = education(END_DATE);

        assertThat(education.isCurrent()).isFalse();

    }

    private static Education education(final LocalDate endDate) {
        return Education.create(CANDIDATE_ID,
                "Istanbul Teknik Universitesi",
                "Lisans",
                "Bilgisayar Muhendisligi",
                START_DATE,
                endDate,
                "Aciklama"
        );
    }

}
