package com.deveyk.jobmatch.candidate.unit.application.service;

import com.deveyk.jobmatch.candidate.application.port.in.command.CreateCandidateProfileCommand;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateCandidateProfileCommand;
import com.deveyk.jobmatch.candidate.application.port.out.CandidateRepository;
import com.deveyk.jobmatch.candidate.application.service.CandidateProfileService;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateAlreadyExistsException;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateNotFoundException;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateRoleRequiredException;
import com.deveyk.jobmatch.candidate.domain.model.Candidate;
import com.deveyk.jobmatch.candidate.domain.model.WorkplacePreferences;
import com.deveyk.jobmatch.identity.domain.Role;
import com.deveyk.jobmatch.shared.domain.WorkplaceType;
import com.deveyk.jobmatch.shared.domain.model.Location;
import com.deveyk.jobmatch.shared.domain.model.Money;
import com.deveyk.jobmatch.shared.domain.model.SalaryRange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CandidateProfileService - Birim Testleri")
class CandidateProfileServiceTest {

    @Mock
    private CandidateRepository candidateRepository;

    @InjectMocks
    private CandidateProfileService candidateProfileService;

    @Test
    @DisplayName("createProfile() rol CANDIDATE ve profil yoksa candidate'i olusturup kaydeder")
    void createProfile_savesAndReturnsCandidate_whenRoleIsCandidateAndProfileDoesNotExist() {

        when(this.candidateRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(this.candidateRepository.save(any(Candidate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final CreateCandidateProfileCommand command = CreateCandidateProfileCommand.builder()
                .userId(1L)
                .role(Role.CANDIDATE)
                .build();

        final Candidate result = this.candidateProfileService.createProfile(command);

        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getId()).isNull();
        verify(this.candidateRepository).save(any(Candidate.class));

    }

    @Test
    @DisplayName("createProfile() rol CANDIDATE degilse CandidateRoleRequiredException firlatir ve kaydetmez")
    void createProfile_throwsCandidateRoleRequiredException_whenRoleIsNotCandidate() {

        final CreateCandidateProfileCommand command = CreateCandidateProfileCommand.builder()
                .userId(1L)
                .role(Role.EMPLOYER)
                .build();

        assertThatThrownBy(() -> this.candidateProfileService.createProfile(command))
                .isInstanceOf(CandidateRoleRequiredException.class);

        verify(this.candidateRepository, never()).save(any());

    }

    @Test
    @DisplayName("createProfile() userId icin profil zaten varsa CandidateAlreadyExistsException firlatir")
    void createProfile_throwsCandidateAlreadyExistsException_whenProfileAlreadyExists() {

        when(this.candidateRepository.findByUserId(1L)).thenReturn(Optional.of(existingCandidate(10L, 1L)));

        final CreateCandidateProfileCommand command = CreateCandidateProfileCommand.builder()
                .userId(1L)
                .role(Role.CANDIDATE)
                .build();

        assertThatThrownBy(() -> this.candidateProfileService.createProfile(command))
                .isInstanceOf(CandidateAlreadyExistsException.class);

        verify(this.candidateRepository, never()).save(any());

    }

    @Test
    @DisplayName("updateProfile() mevcut candidate'i gunceller ve kaydeder")
    void updateProfile_updatesAndSavesExistingCandidate() {

        final Candidate existing = existingCandidate(10L, 1L);
        when(this.candidateRepository.findByUserId(1L)).thenReturn(Optional.of(existing));
        when(this.candidateRepository.save(existing)).thenReturn(existing);

        final Location location = new Location("Turkiye", "Istanbul");
        final WorkplacePreferences preferences = new WorkplacePreferences(Set.of(WorkplaceType.REMOTE));

        final SalaryRange desiredSalary = new SalaryRange(
                new Money(BigDecimal.valueOf(40000), "USD"),
                new Money(BigDecimal.valueOf(60000), "USD")
        );

        final UpdateCandidateProfileCommand command = UpdateCandidateProfileCommand.builder()
                .userId(1L)
                .headline("Backend Developer")
                .summary("ozet")
                .location(location)
                .workplacePreferences(preferences)
                .desiredSalary(desiredSalary)
                .build();

        final Candidate result = this.candidateProfileService.updateProfile(command);

        assertThat(result.getHeadline()).isEqualTo("Backend Developer");
        assertThat(result.getSummary()).isEqualTo("ozet");
        assertThat(result.getLocation()).isEqualTo(location);
        verify(this.candidateRepository).save(existing);

    }

    @Test
    @DisplayName("updateProfile() profil bulunamadiginda CandidateNotFoundException firlatir")
    void updateProfile_throwsCandidateNotFoundException_whenProfileDoesNotExist() {

        when(this.candidateRepository.findByUserId(1L)).thenReturn(Optional.empty());

        final UpdateCandidateProfileCommand command = UpdateCandidateProfileCommand.builder()
                .userId(1L)
                .build();

        assertThatThrownBy(() -> this.candidateProfileService.updateProfile(command))
                .isInstanceOf(CandidateNotFoundException.class);

        verify(this.candidateRepository, never()).save(any());

    }

    @Test
    @DisplayName("getProfile() profil bulundugunda dondurur")
    void getProfile_returnsCandidate_whenExists() {

        final Candidate existing = existingCandidate(10L, 1L);
        when(this.candidateRepository.findByUserId(1L)).thenReturn(Optional.of(existing));

        final Candidate result = this.candidateProfileService.getProfile(1L);

        assertThat(result).isSameAs(existing);

    }

    @Test
    @DisplayName("getProfile() profil bulunamadiginda CandidateNotFoundException firlatir")
    void getProfile_throwsCandidateNotFoundException_whenNotFound() {

        when(this.candidateRepository.findByUserId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.candidateProfileService.getProfile(404L))
                .isInstanceOf(CandidateNotFoundException.class);

    }

    private static Candidate existingCandidate(final Long id, final Long userId) {
        return Candidate.builder()
                .id(id)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build();
    }

}
