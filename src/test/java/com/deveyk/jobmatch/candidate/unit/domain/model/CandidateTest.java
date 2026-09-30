package com.deveyk.jobmatch.candidate.unit.domain.model;

import com.deveyk.jobmatch.candidate.domain.model.Candidate;
import com.deveyk.jobmatch.candidate.domain.model.WorkplacePreferences;
import com.deveyk.jobmatch.shared.domain.WorkplaceType;
import com.deveyk.jobmatch.shared.domain.model.Location;
import com.deveyk.jobmatch.shared.domain.model.Money;
import com.deveyk.jobmatch.shared.domain.model.SalaryRange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Candidate - Birim Testleri")
class CandidateTest {

    @Test
    @DisplayName("create() userId'yi set eder, createdAt'i doldurur ve id'yi null birakir")
    void create_setsUserIdAndCreatedAt_andLeavesIdNull() {

        final Candidate candidate = Candidate.create(1L);

        assertThat(candidate.getId()).isNull();
        assertThat(candidate.getUserId()).isEqualTo(1L);
        assertThat(candidate.getCreatedAt()).isNotNull();

    }

    @Test
    @DisplayName("create() userId null oldugunda NullPointerException firlatir")
    void create_throwsNullPointerException_whenUserIdIsNull() {

        assertThatThrownBy(() -> Candidate.create(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("userId");

    }

    @Test
    @DisplayName("updateProfile() tum alanlari gunceller")
    void updateProfile_updatesAllFields() {

        final Candidate candidate = Candidate.create(1L);
        final Location location = new Location("Turkiye", "Istanbul");
        final WorkplacePreferences preferences = new WorkplacePreferences(Set.of(WorkplaceType.REMOTE, WorkplaceType.HYBRID));
        final SalaryRange desiredSalary = new SalaryRange(new Money(BigDecimal.valueOf(40000), "USD"), new Money(BigDecimal.valueOf(60000), "USD"));

        candidate.updateProfile("Senior Backend Developer", "8 yil deneyim", location, preferences, desiredSalary);

        assertThat(candidate.getHeadline()).isEqualTo("Senior Backend Developer");
        assertThat(candidate.getSummary()).isEqualTo("8 yil deneyim");
        assertThat(candidate.getLocation()).isEqualTo(location);
        assertThat(candidate.getWorkplacePreferences()).isEqualTo(preferences);
        assertThat(candidate.getDesiredSalary()).isEqualTo(desiredSalary);

    }

    @Test
    @DisplayName("updateProfile() henuz validasyon icermedigi icin null degerleri kabul eder")
    void updateProfile_acceptsNullValues_becauseNoValidationExistsYet() {

        final Candidate candidate = Candidate.create(1L);

        candidate.updateProfile(null, null, null, null, null);

        assertThat(candidate.getHeadline()).isNull();
        assertThat(candidate.getSummary()).isNull();
        assertThat(candidate.getLocation()).isNull();
        assertThat(candidate.getWorkplacePreferences()).isNull();
        assertThat(candidate.getDesiredSalary()).isNull();

    }

    @Test
    @DisplayName("updateProfile() ikinci cagrida onceki degerlerin uzerine yazar")
    void updateProfile_overwritesPreviousValues() {

        final Candidate candidate = Candidate.create(1L);
        final Location firstLocation = new Location("Turkiye", "Istanbul");
        final Location secondLocation = new Location("Almanya", "Berlin");

        candidate.updateProfile("Backend Developer", "ilk ozet", firstLocation, null, null);
        candidate.updateProfile("Senior Backend Developer", "ikinci ozet", secondLocation, null, null);

        assertThat(candidate.getHeadline()).isEqualTo("Senior Backend Developer");
        assertThat(candidate.getSummary()).isEqualTo("ikinci ozet");
        assertThat(candidate.getLocation()).isEqualTo(secondLocation);

    }

    @Test
    @DisplayName("isOwnedBy() userId eslestiginde true doner")
    void isOwnedBy_returnsTrue_whenUserIdMatches() {

        final Candidate candidate = Candidate.create(1L);

        assertThat(candidate.isOwnedBy(1L)).isTrue();

    }

    @Test
    @DisplayName("isOwnedBy() userId eslesmediginde false doner")
    void isOwnedBy_returnsFalse_whenUserIdDiffers() {

        final Candidate candidate = Candidate.create(1L);

        assertThat(candidate.isOwnedBy(2L)).isFalse();

    }

}
