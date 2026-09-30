package com.deveyk.jobmatch.candidate.unit.application.service;

import com.deveyk.jobmatch.candidate.application.port.in.command.AddExperienceCommand;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateExperienceCommand;
import com.deveyk.jobmatch.candidate.application.port.out.ExperienceRepository;
import com.deveyk.jobmatch.candidate.application.service.ExperienceService;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateResourceForbiddenException;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateResourceNotFoundException;
import com.deveyk.jobmatch.candidate.domain.model.Experience;
import com.deveyk.jobmatch.shared.domain.EmploymentType;
import com.deveyk.jobmatch.shared.domain.model.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExperienceService - Birim Testleri")
class ExperienceServiceTest {

    private static final Location LOCATION = new Location("Turkiye", "Istanbul");
    private static final LocalDate START_DATE = LocalDate.of(2018, 3, 1);
    private static final LocalDate END_DATE = LocalDate.of(2022, 12, 31);

    @Mock
    private ExperienceRepository experienceRepository;

    @InjectMocks
    private ExperienceService experienceService;

    @Test
    @DisplayName("addExperience() experience'i olusturup kaydeder")
    void addExperience_savesAndReturnsExperience() {

        when(this.experienceRepository.save(any(Experience.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final AddExperienceCommand command = AddExperienceCommand.builder()
                .candidateId(1L)
                .title("Backend Developer")
                .company("Acme A.S.")
                .location(LOCATION)
                .employmentType(EmploymentType.FULL_TIME)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .description("aciklama")
                .build();

        final Experience result = this.experienceService.addExperience(command);

        assertThat(result.getCandidateId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Backend Developer");
        assertThat(result.getId()).isNull();
        verify(this.experienceRepository).save(any(Experience.class));

    }

    @Test
    @DisplayName("updateExperience() candidate'e ait experience'i gunceller ve kaydeder")
    void updateExperience_updatesAndSavesExistingExperience_whenOwnedByCandidate() {

        final Experience existing = existingExperience(5L, 1L);
        when(this.experienceRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(this.experienceRepository.save(existing)).thenReturn(existing);

        final UpdateExperienceCommand command = UpdateExperienceCommand.builder()
                .candidateId(1L)
                .experienceId(5L)
                .title("Senior Backend Developer")
                .company("Globex GmbH")
                .location(LOCATION)
                .employmentType(EmploymentType.CONTRACT)
                .startDate(START_DATE)
                .endDate(null)
                .description("halen devam ediyor")
                .build();

        final Experience result = this.experienceService.updateExperience(command);

        assertThat(result.getTitle()).isEqualTo("Senior Backend Developer");
        assertThat(result.getCompany()).isEqualTo("Globex GmbH");
        assertThat(result.getEndDate()).isNull();
        verify(this.experienceRepository).save(existing);

    }

    @Test
    @DisplayName("updateExperience() experience bulunamadiginda CandidateResourceNotFoundException firlatir")
    void updateExperience_throwsCandidateResourceNotFoundException_whenExperienceDoesNotExist() {

        when(this.experienceRepository.findById(404L)).thenReturn(Optional.empty());

        final UpdateExperienceCommand command = UpdateExperienceCommand.builder()
                .candidateId(1L)
                .experienceId(404L)
                .title("Title")
                .company("Company")
                .location(LOCATION)
                .employmentType(EmploymentType.FULL_TIME)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .build();

        assertThatThrownBy(() -> this.experienceService.updateExperience(command))
                .isInstanceOf(CandidateResourceNotFoundException.class);

        verify(this.experienceRepository, never()).save(any());

    }

    @Test
    @DisplayName("updateExperience() experience baska candidate'e aitse CandidateResourceForbiddenException firlatir")
    void updateExperience_throwsCandidateResourceForbiddenException_whenNotOwnedByCandidate() {

        final Experience existing = existingExperience(5L, 1L);
        when(this.experienceRepository.findById(5L)).thenReturn(Optional.of(existing));

        final UpdateExperienceCommand command = UpdateExperienceCommand.builder()
                .candidateId(2L)
                .experienceId(5L)
                .title("Title")
                .company("Company")
                .location(LOCATION)
                .employmentType(EmploymentType.FULL_TIME)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .build();

        assertThatThrownBy(() -> this.experienceService.updateExperience(command))
                .isInstanceOf(CandidateResourceForbiddenException.class);

        verify(this.experienceRepository, never()).save(any());

    }

    @Test
    @DisplayName("deleteExperience() candidate'e ait experience'i siler")
    void deleteExperience_deletesExperience_whenOwnedByCandidate() {

        final Experience existing = existingExperience(5L, 1L);
        when(this.experienceRepository.findById(5L)).thenReturn(Optional.of(existing));

        this.experienceService.deleteExperience(1L, 5L);

        verify(this.experienceRepository).deleteById(5L);

    }

    @Test
    @DisplayName("deleteExperience() experience baska candidate'e aitse CandidateResourceForbiddenException firlatir ve silmez")
    void deleteExperience_throwsCandidateResourceForbiddenException_whenNotOwnedByCandidate() {

        final Experience existing = existingExperience(5L, 1L);
        when(this.experienceRepository.findById(5L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> this.experienceService.deleteExperience(2L, 5L))
                .isInstanceOf(CandidateResourceForbiddenException.class);

        verify(this.experienceRepository, never()).deleteById(anyLong());

    }

    @Test
    @DisplayName("deleteExperience() experience bulunamadiginda CandidateResourceNotFoundException firlatir")
    void deleteExperience_throwsCandidateResourceNotFoundException_whenExperienceDoesNotExist() {

        when(this.experienceRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.experienceService.deleteExperience(1L, 404L))
                .isInstanceOf(CandidateResourceNotFoundException.class);

        verify(this.experienceRepository, never()).deleteById(anyLong());

    }

    @Test
    @DisplayName("listExperiences() candidateId ile repository'ye oldugu gibi iletir")
    void listExperiences_delegatesToRepository() {

        final List<Experience> expected = List.of(existingExperience(5L, 1L));
        when(this.experienceRepository.findAllByCandidateId(1L)).thenReturn(expected);

        final List<Experience> result = this.experienceService.listExperiences(1L);

        assertThat(result).isSameAs(expected);
        verify(this.experienceRepository).findAllByCandidateId(1L);

    }

    private static Experience existingExperience(final Long id, final Long candidateId) {
        return Experience.builder()
                .id(id)
                .candidateId(candidateId)
                .title("Backend Developer")
                .company("Acme A.S.")
                .location(LOCATION)
                .employmentType(EmploymentType.FULL_TIME)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .createdAt(LocalDateTime.now())
                .build();
    }

}
