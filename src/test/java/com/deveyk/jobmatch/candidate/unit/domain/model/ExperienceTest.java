package com.deveyk.jobmatch.candidate.unit.domain.model;

import com.deveyk.jobmatch.candidate.domain.exception.CandidateFieldInvalidException;
import com.deveyk.jobmatch.candidate.domain.model.Experience;
import com.deveyk.jobmatch.shared.domain.EmploymentType;
import com.deveyk.jobmatch.shared.domain.model.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Experience - Birim Testleri")
class ExperienceTest {

    private static final Long CANDIDATE_ID = 1L;
    private static final Location LOCATION = new Location("Turkiye", "Istanbul");
    private static final LocalDate START_DATE = LocalDate.of(2018, 3, 1);
    private static final LocalDate END_DATE = LocalDate.of(2022, 12, 31);

    @Test
    @DisplayName("create() tum alanlari set eder, createdAt'i doldurur ve id'yi null birakir")
    void create_setsAllFields_andLeavesIdNull() {

        final Experience experience = Experience.create(CANDIDATE_ID, "Backend Developer", "Acme A.S.",
                LOCATION, EmploymentType.FULL_TIME, START_DATE, END_DATE, "Mikroservis mimarisi ile calisildi");

        assertThat(experience.getId()).isNull();
        assertThat(experience.getCandidateId()).isEqualTo(CANDIDATE_ID);
        assertThat(experience.getTitle()).isEqualTo("Backend Developer");
        assertThat(experience.getCompany()).isEqualTo("Acme A.S.");
        assertThat(experience.getLocation()).isEqualTo(LOCATION);
        assertThat(experience.getEmploymentType()).isEqualTo(EmploymentType.FULL_TIME);
        assertThat(experience.getStartDate()).isEqualTo(START_DATE);
        assertThat(experience.getEndDate()).isEqualTo(END_DATE);
        assertThat(experience.getDescription()).isEqualTo("Mikroservis mimarisi ile calisildi");
        assertThat(experience.getCreatedAt()).isNotNull();

    }

    @Test
    @DisplayName("create() endDate olmadan da olusturulabilir (halen calisiyor)")
    void create_allowsNullEndDate() {

        final Experience experience = experience(null);

        assertThat(experience.getEndDate()).isNull();

    }

    @Test
    @DisplayName("create() title bos oldugunda CandidateFieldInvalidException firlatir")
    void create_throwsCandidateFieldInvalidException_whenTitleIsBlank() {

        assertThatThrownBy(() -> Experience.create(CANDIDATE_ID, " ", "Acme A.S.", LOCATION,
                EmploymentType.FULL_TIME, START_DATE, END_DATE, null))
                .isInstanceOf(CandidateFieldInvalidException.class)
                .hasMessageContaining("title");

    }

    @Test
    @DisplayName("create() company bos oldugunda CandidateFieldInvalidException firlatir")
    void create_throwsCandidateFieldInvalidException_whenCompanyIsBlank() {

        assertThatThrownBy(() -> Experience.create(CANDIDATE_ID, "Backend Developer", " ", LOCATION,
                EmploymentType.FULL_TIME, START_DATE, END_DATE, null))
                .isInstanceOf(CandidateFieldInvalidException.class)
                .hasMessageContaining("company");

    }

    @Test
    @DisplayName("create() startDate null oldugunda CandidateFieldInvalidException firlatir")
    void create_throwsCandidateFieldInvalidException_whenStartDateIsNull() {

        assertThatThrownBy(() -> Experience.create(CANDIDATE_ID, "Backend Developer", "Acme A.S.", LOCATION,
                EmploymentType.FULL_TIME, null, END_DATE, null))
                .isInstanceOf(CandidateFieldInvalidException.class)
                .hasMessageContaining("startDate");

    }

    @Test
    @DisplayName("create() endDate startDate'den once oldugunda CandidateFieldInvalidException firlatir")
    void create_throwsCandidateFieldInvalidException_whenEndDateBeforeStartDate() {

        assertThatThrownBy(() -> Experience.create(CANDIDATE_ID, "Backend Developer", "Acme A.S.", LOCATION,
                EmploymentType.FULL_TIME, START_DATE, START_DATE.minusDays(1), null))
                .isInstanceOf(CandidateFieldInvalidException.class)
                .hasMessageContaining("endDate");

    }

    @Test
    @DisplayName("update() tum alanlari gunceller, id ve candidateId degismez")
    void update_updatesAllFields_andLeavesIdAndCandidateIdUnchanged() {

        final Experience experience = experience(END_DATE);
        final Location newLocation = new Location("Almanya", "Berlin");
        final LocalDate newStartDate = LocalDate.of(2023, 1, 15);

        experience.update("Senior Backend Developer", "Globex GmbH", newLocation, EmploymentType.CONTRACT,
                newStartDate, null, "Halen devam ediyor");

        assertThat(experience.getTitle()).isEqualTo("Senior Backend Developer");
        assertThat(experience.getCompany()).isEqualTo("Globex GmbH");
        assertThat(experience.getLocation()).isEqualTo(newLocation);
        assertThat(experience.getEmploymentType()).isEqualTo(EmploymentType.CONTRACT);
        assertThat(experience.getStartDate()).isEqualTo(newStartDate);
        assertThat(experience.getEndDate()).isNull();
        assertThat(experience.getDescription()).isEqualTo("Halen devam ediyor");
        assertThat(experience.getId()).isNull();
        assertThat(experience.getCandidateId()).isEqualTo(CANDIDATE_ID);

    }

    @Test
    @DisplayName("update() title bos oldugunda CandidateFieldInvalidException firlatir")
    void update_throwsCandidateFieldInvalidException_whenTitleIsBlank() {

        final Experience experience = experience(END_DATE);

        assertThatThrownBy(() -> experience.update(" ", "Acme A.S.", LOCATION, EmploymentType.FULL_TIME,
                START_DATE, END_DATE, null))
                .isInstanceOf(CandidateFieldInvalidException.class)
                .hasMessageContaining("title");

    }

    @Test
    @DisplayName("isOwnedBy() candidateId eslestiginde true doner")
    void isOwnedBy_returnsTrue_whenCandidateIdMatches() {

        final Experience experience = experience(END_DATE);

        assertThat(experience.isOwnedBy(CANDIDATE_ID)).isTrue();

    }

    @Test
    @DisplayName("isOwnedBy() candidateId eslesmediginde false doner")
    void isOwnedBy_returnsFalse_whenCandidateIdDiffers() {

        final Experience experience = experience(END_DATE);

        assertThat(experience.isOwnedBy(99L)).isFalse();

    }

    @Test
    @DisplayName("isCurrent() endDate null oldugunda true doner (Ongoing arayuzu)")
    void isCurrent_returnsTrue_whenEndDateIsNull() {

        final Experience experience = experience(null);

        assertThat(experience.isCurrent()).isTrue();

    }

    @Test
    @DisplayName("isCurrent() endDate dolu oldugunda false doner (Ongoing arayuzu)")
    void isCurrent_returnsFalse_whenEndDateIsSet() {

        final Experience experience = experience(END_DATE);

        assertThat(experience.isCurrent()).isFalse();

    }

    private static Experience experience(final LocalDate endDate) {
        return Experience.create(CANDIDATE_ID,
                "Backend Developer",
                "Acme A.S.",
                LOCATION,
                EmploymentType.FULL_TIME,
                START_DATE,
                endDate,
                "Aciklama"
        );
    }

}
