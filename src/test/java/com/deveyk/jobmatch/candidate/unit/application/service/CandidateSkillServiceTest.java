package com.deveyk.jobmatch.candidate.unit.application.service;

import com.deveyk.jobmatch.candidate.application.port.in.command.AttachCandidateSkillCommand;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateCandidateSkillCommand;
import com.deveyk.jobmatch.candidate.application.port.out.CandidateSkillRepository;
import com.deveyk.jobmatch.candidate.application.service.CandidateSkillService;
import com.deveyk.jobmatch.candidate.domain.ProficiencyLevel;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateResourceNotFoundException;
import com.deveyk.jobmatch.candidate.domain.model.CandidateSkill;
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
@DisplayName("CandidateSkillService - Birim Testleri")
class CandidateSkillServiceTest {

    @Mock
    private CandidateSkillRepository candidateSkillRepository;

    @InjectMocks
    private CandidateSkillService candidateSkillService;

    @Test
    @DisplayName("attachSkill() candidateSkill'i olusturup kaydeder")
    void attachSkill_savesAndReturnsCandidateSkill() {

        when(this.candidateSkillRepository.save(any(CandidateSkill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final AttachCandidateSkillCommand command = new AttachCandidateSkillCommand(1L, 100L, ProficiencyLevel.ADVANCED);

        final CandidateSkill result = this.candidateSkillService.attachSkill(command);

        assertThat(result.getCandidateId()).isEqualTo(1L);
        assertThat(result.getSkillId()).isEqualTo(100L);
        assertThat(result.getProficiencyLevel()).isEqualTo(ProficiencyLevel.ADVANCED);
        verify(this.candidateSkillRepository).save(any(CandidateSkill.class));

    }

    @Test
    @DisplayName("updateSkillProficiency() mevcut candidateSkill'in seviyesini gunceller ve kaydeder")
    void updateSkillProficiency_updatesAndSavesExistingCandidateSkill_whenFound() {

        final CandidateSkill existing = existingCandidateSkill(1L, 100L, ProficiencyLevel.BEGINNER);
        when(this.candidateSkillRepository.findByCandidateIdAndSkillId(1L, 100L)).thenReturn(Optional.of(existing));
        when(this.candidateSkillRepository.save(existing)).thenReturn(existing);

        final UpdateCandidateSkillCommand command = new UpdateCandidateSkillCommand(1L, 100L, ProficiencyLevel.EXPERT);

        final CandidateSkill result = this.candidateSkillService.updateSkillProficiency(command);

        assertThat(result.getProficiencyLevel()).isEqualTo(ProficiencyLevel.EXPERT);
        verify(this.candidateSkillRepository).save(existing);

    }

    @Test
    @DisplayName("updateSkillProficiency() candidateSkill bulunamadiginda CandidateResourceNotFoundException firlatir")
    void updateSkillProficiency_throwsCandidateResourceNotFoundException_whenNotFound() {

        when(this.candidateSkillRepository.findByCandidateIdAndSkillId(1L, 100L)).thenReturn(Optional.empty());

        final UpdateCandidateSkillCommand command = new UpdateCandidateSkillCommand(1L, 100L, ProficiencyLevel.EXPERT);

        assertThatThrownBy(() -> this.candidateSkillService.updateSkillProficiency(command))
                .isInstanceOf(CandidateResourceNotFoundException.class);

        verify(this.candidateSkillRepository, never()).save(any());

    }

    @Test
    @DisplayName("detachSkill() candidateSkill bulundugunda siler")
    void detachSkill_deletesCandidateSkill_whenFound() {

        final CandidateSkill existing = existingCandidateSkill(1L, 100L, ProficiencyLevel.BEGINNER);
        when(this.candidateSkillRepository.findByCandidateIdAndSkillId(1L, 100L)).thenReturn(Optional.of(existing));

        this.candidateSkillService.detachSkill(1L, 100L);

        verify(this.candidateSkillRepository).deleteByCandidateIdAndSkillId(1L, 100L);

    }

    @Test
    @DisplayName("detachSkill() candidateSkill bulunamadiginda CandidateResourceNotFoundException firlatir ve silmez")
    void detachSkill_throwsCandidateResourceNotFoundException_whenNotFound() {

        when(this.candidateSkillRepository.findByCandidateIdAndSkillId(1L, 100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.candidateSkillService.detachSkill(1L, 100L))
                .isInstanceOf(CandidateResourceNotFoundException.class);

        verify(this.candidateSkillRepository, never()).deleteByCandidateIdAndSkillId(1L, 100L);

    }

    @Test
    @DisplayName("listSkills() candidateId ile repository'ye oldugu gibi iletir")
    void listSkills_delegatesToRepository() {

        final List<CandidateSkill> expected = List.of(existingCandidateSkill(1L, 100L, ProficiencyLevel.BEGINNER));
        when(this.candidateSkillRepository.findAllByCandidateId(1L)).thenReturn(expected);

        final List<CandidateSkill> result = this.candidateSkillService.listSkills(1L);

        assertThat(result).isSameAs(expected);
        verify(this.candidateSkillRepository).findAllByCandidateId(1L);

    }

    private static CandidateSkill existingCandidateSkill(final Long candidateId, final Long skillId, final ProficiencyLevel proficiencyLevel) {
        return CandidateSkill.builder()
                .id(50L)
                .candidateId(candidateId)
                .skillId(skillId)
                .proficiencyLevel(proficiencyLevel)
                .createdAt(LocalDateTime.now())
                .build();
    }

}
