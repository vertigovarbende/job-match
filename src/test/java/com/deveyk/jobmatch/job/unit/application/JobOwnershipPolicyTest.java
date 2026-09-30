package com.deveyk.jobmatch.job.unit.application;

import com.deveyk.jobmatch.company.application.CurrentCompanyFacade;
import com.deveyk.jobmatch.job.application.JobOwnershipPolicy;
import com.deveyk.jobmatch.job.application.port.out.JobRepository;
import com.deveyk.jobmatch.job.domain.model.Job;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JobOwnershipPolicy - Birim Testleri")
class JobOwnershipPolicyTest {

    @Mock
    private CurrentCompanyFacade currentCompanyFacade;

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobOwnershipPolicy jobOwnershipPolicy;

    @Test
    @DisplayName("isOwner() jobId null oldugunda false doner")
    void isOwner_returnsFalse_whenJobIdIsNull() {

        assertThat(this.jobOwnershipPolicy.isOwner(null)).isFalse();

    }

    @Test
    @DisplayName("isOwner() company membership yoksa false doner")
    void isOwner_returnsFalse_whenNoCurrentCompany() {

        when(this.currentCompanyFacade.resolveCurrentCompanyId()).thenReturn(Optional.empty());

        assertThat(this.jobOwnershipPolicy.isOwner(1L)).isFalse();

    }

    @Test
    @DisplayName("isOwner() job bulunamadiginda false doner")
    void isOwner_returnsFalse_whenJobNotFound() {

        when(this.currentCompanyFacade.resolveCurrentCompanyId()).thenReturn(Optional.of(1L));
        when(this.jobRepository.findById(5L)).thenReturn(Optional.empty());

        assertThat(this.jobOwnershipPolicy.isOwner(5L)).isFalse();

    }

    @Test
    @DisplayName("isOwner() job baska sirkete aitse false doner")
    void isOwner_returnsFalse_whenJobBelongsToAnotherCompany() {

        when(this.currentCompanyFacade.resolveCurrentCompanyId()).thenReturn(Optional.of(1L));
        when(this.jobRepository.findById(5L)).thenReturn(Optional.of(jobOwnedBy(2L)));

        assertThat(this.jobOwnershipPolicy.isOwner(5L)).isFalse();

    }

    @Test
    @DisplayName("isOwner() job mevcut sirkete aitse true doner")
    void isOwner_returnsTrue_whenJobBelongsToCurrentCompany() {

        when(this.currentCompanyFacade.resolveCurrentCompanyId()).thenReturn(Optional.of(1L));
        when(this.jobRepository.findById(5L)).thenReturn(Optional.of(jobOwnedBy(1L)));

        assertThat(this.jobOwnershipPolicy.isOwner(5L)).isTrue();

    }

    private static Job jobOwnedBy(final Long companyId) {
        return Job.builder()
                .id(5L)
                .companyId(companyId)
                .requiredSkillIds(Set.of())
                .preferredSkillIds(Set.of())
                .build();
    }

}
