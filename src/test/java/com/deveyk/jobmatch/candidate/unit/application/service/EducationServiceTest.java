package com.deveyk.jobmatch.candidate.unit.application.service;

import com.deveyk.jobmatch.candidate.application.port.in.command.AddEducationCommand;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateEducationCommand;
import com.deveyk.jobmatch.candidate.application.port.out.EducationRepository;
import com.deveyk.jobmatch.candidate.application.service.EducationService;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateResourceForbiddenException;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateResourceNotFoundException;
import com.deveyk.jobmatch.candidate.domain.model.Education;
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
@DisplayName("EducationService - Birim Testleri")
class EducationServiceTest {

    private static final LocalDate START_DATE = LocalDate.of(2016, 9, 1);
    private static final LocalDate END_DATE = LocalDate.of(2020, 6, 30);

    @Mock
    private EducationRepository educationRepository;

    @InjectMocks
    private EducationService educationService;

    @Test
    @DisplayName("addEducation() education'i olusturup kaydeder")
    void addEducation_savesAndReturnsEducation() {

        when(this.educationRepository.save(any(Education.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final AddEducationCommand command = AddEducationCommand.builder()
                .candidateId(1L)
                .institution("Istanbul Teknik Universitesi")
                .degree("Lisans")
                .fieldOfStudy("Bilgisayar Muhendisligi")
                .startDate(START_DATE)
                .endDate(END_DATE)
                .description("aciklama")
                .build();

        final Education result = this.educationService.addEducation(command);

        assertThat(result.getCandidateId()).isEqualTo(1L);
        assertThat(result.getInstitution()).isEqualTo("Istanbul Teknik Universitesi");
        assertThat(result.getId()).isNull();
        verify(this.educationRepository).save(any(Education.class));

    }

    @Test
    @DisplayName("updateEducation() candidate'e ait education'i gunceller ve kaydeder")
    void updateEducation_updatesAndSavesExistingEducation_whenOwnedByCandidate() {

        final Education existing = existingEducation(5L, 1L);
        when(this.educationRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(this.educationRepository.save(existing)).thenReturn(existing);

        final UpdateEducationCommand command = UpdateEducationCommand.builder()
                .candidateId(1L)
                .educationId(5L)
                .institution("Bogazici Universitesi")
                .degree("Yuksek Lisans")
                .fieldOfStudy("Yazilim Muhendisligi")
                .startDate(START_DATE)
                .endDate(null)
                .description("devam ediyor")
                .build();

        final Education result = this.educationService.updateEducation(command);

        assertThat(result.getInstitution()).isEqualTo("Bogazici Universitesi");
        assertThat(result.getDegree()).isEqualTo("Yuksek Lisans");
        assertThat(result.getEndDate()).isNull();
        verify(this.educationRepository).save(existing);

    }

    @Test
    @DisplayName("updateEducation() education bulunamadiginda CandidateResourceNotFoundException firlatir")
    void updateEducation_throwsCandidateResourceNotFoundException_whenEducationDoesNotExist() {

        when(this.educationRepository.findById(404L)).thenReturn(Optional.empty());

        final UpdateEducationCommand command = UpdateEducationCommand.builder()
                .candidateId(1L)
                .educationId(404L)
                .institution("Institution")
                .degree("Degree")
                .fieldOfStudy("Field")
                .startDate(START_DATE)
                .endDate(END_DATE)
                .build();

        assertThatThrownBy(() -> this.educationService.updateEducation(command))
                .isInstanceOf(CandidateResourceNotFoundException.class);

        verify(this.educationRepository, never()).save(any());

    }

    @Test
    @DisplayName("updateEducation() education baska candidate'e aitse CandidateResourceForbiddenException firlatir")
    void updateEducation_throwsCandidateResourceForbiddenException_whenNotOwnedByCandidate() {

        final Education existing = existingEducation(5L, 1L);
        when(this.educationRepository.findById(5L)).thenReturn(Optional.of(existing));

        final UpdateEducationCommand command = UpdateEducationCommand.builder()
                .candidateId(2L)
                .educationId(5L)
                .institution("Institution")
                .degree("Degree")
                .fieldOfStudy("Field")
                .startDate(START_DATE)
                .endDate(END_DATE)
                .build();

        assertThatThrownBy(() -> this.educationService.updateEducation(command))
                .isInstanceOf(CandidateResourceForbiddenException.class);

        verify(this.educationRepository, never()).save(any());

    }

    @Test
    @DisplayName("deleteEducation() candidate'e ait education'i siler")
    void deleteEducation_deletesEducation_whenOwnedByCandidate() {

        final Education existing = existingEducation(5L, 1L);
        when(this.educationRepository.findById(5L)).thenReturn(Optional.of(existing));

        this.educationService.deleteEducation(1L, 5L);

        verify(this.educationRepository).deleteById(5L);

    }

    @Test
    @DisplayName("deleteEducation() education baska candidate'e aitse CandidateResourceForbiddenException firlatir ve silmez")
    void deleteEducation_throwsCandidateResourceForbiddenException_whenNotOwnedByCandidate() {

        final Education existing = existingEducation(5L, 1L);
        when(this.educationRepository.findById(5L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> this.educationService.deleteEducation(2L, 5L))
                .isInstanceOf(CandidateResourceForbiddenException.class);

        verify(this.educationRepository, never()).deleteById(anyLong());

    }

    @Test
    @DisplayName("deleteEducation() education bulunamadiginda CandidateResourceNotFoundException firlatir")
    void deleteEducation_throwsCandidateResourceNotFoundException_whenEducationDoesNotExist() {

        when(this.educationRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.educationService.deleteEducation(1L, 404L))
                .isInstanceOf(CandidateResourceNotFoundException.class);

        verify(this.educationRepository, never()).deleteById(anyLong());

    }

    @Test
    @DisplayName("listEducations() candidateId ile repository'ye oldugu gibi iletir")
    void listEducations_delegatesToRepository() {

        final List<Education> expected = List.of(existingEducation(5L, 1L));
        when(this.educationRepository.findAllByCandidateId(1L)).thenReturn(expected);

        final List<Education> result = this.educationService.listEducations(1L);

        assertThat(result).isSameAs(expected);
        verify(this.educationRepository).findAllByCandidateId(1L);

    }

    private static Education existingEducation(final Long id, final Long candidateId) {
        return Education.builder()
                .id(id)
                .candidateId(candidateId)
                .institution("Istanbul Teknik Universitesi")
                .degree("Lisans")
                .fieldOfStudy("Bilgisayar Muhendisligi")
                .startDate(START_DATE)
                .endDate(END_DATE)
                .createdAt(LocalDateTime.now())
                .build();
    }

}
