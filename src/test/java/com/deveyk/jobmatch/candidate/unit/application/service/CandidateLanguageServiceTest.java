package com.deveyk.jobmatch.candidate.unit.application.service;

import com.deveyk.jobmatch.candidate.application.port.in.command.AttachCandidateLanguageCommand;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateCandidateLanguageCommand;
import com.deveyk.jobmatch.candidate.application.port.out.CandidateLanguageRepository;
import com.deveyk.jobmatch.candidate.application.service.CandidateLanguageService;
import com.deveyk.jobmatch.candidate.domain.ProficiencyLevel;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateResourceNotFoundException;
import com.deveyk.jobmatch.candidate.domain.model.CandidateLanguage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CandidateLanguageService - Birim Testleri")
class CandidateLanguageServiceTest {

    @Mock
    private CandidateLanguageRepository candidateLanguageRepository;

    @InjectMocks
    private CandidateLanguageService candidateLanguageService;

    @Test
    @DisplayName("attachLanguage() candidateLanguage'i olusturup kaydeder")
    void attachLanguage_savesAndReturnsCandidateLanguage() {

        when(this.candidateLanguageRepository.save(any(CandidateLanguage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final AttachCandidateLanguageCommand command = new AttachCandidateLanguageCommand(1L, 200L, ProficiencyLevel.ADVANCED);

        final CandidateLanguage result = this.candidateLanguageService.attachLanguage(command);

        assertThat(result.getCandidateId()).isEqualTo(1L);
        assertThat(result.getLanguageId()).isEqualTo(200L);
        assertThat(result.getProficiencyLevel()).isEqualTo(ProficiencyLevel.ADVANCED);
        verify(this.candidateLanguageRepository).save(any(CandidateLanguage.class));

    }

    @Test
    @DisplayName("updateLanguageProficiency() mevcut candidateLanguage'in seviyesini gunceller ve kaydeder")
    void updateLanguageProficiency_updatesAndSavesExistingCandidateLanguage_whenFound() {

        final CandidateLanguage existing = existingCandidateLanguage(1L, 200L, ProficiencyLevel.BEGINNER);
        when(this.candidateLanguageRepository.findByCandidateIdAndLanguageId(1L, 200L)).thenReturn(Optional.of(existing));
        when(this.candidateLanguageRepository.save(existing)).thenReturn(existing);

        final UpdateCandidateLanguageCommand command = new UpdateCandidateLanguageCommand(1L, 200L, ProficiencyLevel.NATIVE);

        final CandidateLanguage result = this.candidateLanguageService.updateLanguageProficiency(command);

        assertThat(result.getProficiencyLevel()).isEqualTo(ProficiencyLevel.NATIVE);
        verify(this.candidateLanguageRepository).save(existing);

    }

    @Test
    @DisplayName("updateLanguageProficiency() candidateLanguage bulunamadiginda CandidateResourceNotFoundException firlatir")
    void updateLanguageProficiency_throwsCandidateResourceNotFoundException_whenNotFound() {

        when(this.candidateLanguageRepository.findByCandidateIdAndLanguageId(1L, 200L)).thenReturn(Optional.empty());

        final UpdateCandidateLanguageCommand command = new UpdateCandidateLanguageCommand(1L, 200L, ProficiencyLevel.NATIVE);

        assertThatThrownBy(() -> this.candidateLanguageService.updateLanguageProficiency(command))
                .isInstanceOf(CandidateResourceNotFoundException.class);

        verify(this.candidateLanguageRepository, never()).save(any());

    }

    @Test
    @DisplayName("detachLanguage() candidateLanguage bulundugunda siler")
    void detachLanguage_deletesCandidateLanguage_whenFound() {

        final CandidateLanguage existing = existingCandidateLanguage(1L, 200L, ProficiencyLevel.BEGINNER);
        when(this.candidateLanguageRepository.findByCandidateIdAndLanguageId(1L, 200L)).thenReturn(Optional.of(existing));

        this.candidateLanguageService.detachLanguage(1L, 200L);

        verify(this.candidateLanguageRepository).deleteByCandidateIdAndLanguageId(1L, 200L);

    }

    @Test
    @DisplayName("detachLanguage() candidateLanguage bulunamadiginda CandidateResourceNotFoundException firlatir ve silmez")
    void detachLanguage_throwsCandidateResourceNotFoundException_whenNotFound() {

        when(this.candidateLanguageRepository.findByCandidateIdAndLanguageId(1L, 200L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.candidateLanguageService.detachLanguage(1L, 200L))
                .isInstanceOf(CandidateResourceNotFoundException.class);

        verify(this.candidateLanguageRepository, never()).deleteByCandidateIdAndLanguageId(1L, 200L);

    }

    @Test
    @DisplayName("listLanguages() candidateId ile repository'ye oldugu gibi iletir")
    void listLanguages_delegatesToRepository() {

        final List<CandidateLanguage> expected = List.of(existingCandidateLanguage(1L, 200L, ProficiencyLevel.BEGINNER));
        when(this.candidateLanguageRepository.findAllByCandidateId(1L)).thenReturn(expected);

        final List<CandidateLanguage> result = this.candidateLanguageService.listLanguages(1L);

        assertThat(result).isSameAs(expected);
        verify(this.candidateLanguageRepository).findAllByCandidateId(1L);

    }

    private static CandidateLanguage existingCandidateLanguage(final Long candidateId, final Long languageId, final ProficiencyLevel proficiencyLevel) {
        return CandidateLanguage.builder()
                .id(60L)
                .candidateId(candidateId)
                .languageId(languageId)
                .proficiencyLevel(proficiencyLevel)
                .createdAt(LocalDateTime.now())
                .build();
    }

}
