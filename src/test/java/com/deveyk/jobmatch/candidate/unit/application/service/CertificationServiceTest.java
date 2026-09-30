package com.deveyk.jobmatch.candidate.unit.application.service;

import com.deveyk.jobmatch.candidate.application.port.in.command.AddCertificationCommand;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateCertificationCommand;
import com.deveyk.jobmatch.candidate.application.port.out.CertificationRepository;
import com.deveyk.jobmatch.candidate.application.service.CertificationService;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateResourceForbiddenException;
import com.deveyk.jobmatch.candidate.domain.exception.CandidateResourceNotFoundException;
import com.deveyk.jobmatch.candidate.domain.model.Certification;
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
@DisplayName("CertificationService - Birim Testleri")
class CertificationServiceTest {

    private static final LocalDate ISSUE_DATE = LocalDate.of(2020, 1, 15);
    private static final LocalDate EXPIRY_DATE = LocalDate.of(2023, 1, 15);

    @Mock
    private CertificationRepository certificationRepository;

    @InjectMocks
    private CertificationService certificationService;

    @Test
    @DisplayName("addCertification() certification'i olusturup kaydeder")
    void addCertification_savesAndReturnsCertification() {

        when(this.certificationRepository.save(any(Certification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final AddCertificationCommand command = new AddCertificationCommand(1L, "AWS Certified Solutions Architect",
                "Amazon Web Services", ISSUE_DATE, EXPIRY_DATE, "CRED-123", "https://aws.amazon.com/verify/CRED-123", "aciklama");

        final Certification result = this.certificationService.addCertification(command);

        assertThat(result.getCandidateId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("AWS Certified Solutions Architect");
        assertThat(result.getId()).isNull();
        verify(this.certificationRepository).save(any(Certification.class));

    }

    @Test
    @DisplayName("updateCertification() candidate'e ait certification'i gunceller ve kaydeder")
    void updateCertification_updatesAndSavesExistingCertification_whenOwnedByCandidate() {

        final Certification existing = existingCertification(5L, 1L);
        when(this.certificationRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(this.certificationRepository.save(existing)).thenReturn(existing);

        final UpdateCertificationCommand command = new UpdateCertificationCommand(1L, 5L, "AWS Certified Developer",
                "AWS", ISSUE_DATE, EXPIRY_DATE, "CRED-456", "https://aws.amazon.com/verify/CRED-456", "guncellendi");

        final Certification result = this.certificationService.updateCertification(command);

        assertThat(result.getName()).isEqualTo("AWS Certified Developer");
        assertThat(result.getIssuingOrganization()).isEqualTo("AWS");
        verify(this.certificationRepository).save(existing);

    }

    @Test
    @DisplayName("updateCertification() certification bulunamadiginda CandidateResourceNotFoundException firlatir")
    void updateCertification_throwsCandidateResourceNotFoundException_whenCertificationDoesNotExist() {

        when(this.certificationRepository.findById(404L)).thenReturn(Optional.empty());

        final UpdateCertificationCommand command = new UpdateCertificationCommand(1L, 404L, "Name",
                "Org", ISSUE_DATE, EXPIRY_DATE, null, null, null);

        assertThatThrownBy(() -> this.certificationService.updateCertification(command))
                .isInstanceOf(CandidateResourceNotFoundException.class);

        verify(this.certificationRepository, never()).save(any());

    }

    @Test
    @DisplayName("updateCertification() certification baska candidate'e aitse CandidateResourceForbiddenException firlatir")
    void updateCertification_throwsCandidateResourceForbiddenException_whenNotOwnedByCandidate() {

        final Certification existing = existingCertification(5L, 1L);
        when(this.certificationRepository.findById(5L)).thenReturn(Optional.of(existing));

        final UpdateCertificationCommand command = new UpdateCertificationCommand(2L, 5L, "Name",
                "Org", ISSUE_DATE, EXPIRY_DATE, null, null, null);

        assertThatThrownBy(() -> this.certificationService.updateCertification(command))
                .isInstanceOf(CandidateResourceForbiddenException.class);

        verify(this.certificationRepository, never()).save(any());

    }

    @Test
    @DisplayName("deleteCertification() candidate'e ait certification'i siler")
    void deleteCertification_deletesCertification_whenOwnedByCandidate() {

        final Certification existing = existingCertification(5L, 1L);
        when(this.certificationRepository.findById(5L)).thenReturn(Optional.of(existing));

        this.certificationService.deleteCertification(1L, 5L);

        verify(this.certificationRepository).deleteById(5L);

    }

    @Test
    @DisplayName("deleteCertification() certification baska candidate'e aitse CandidateResourceForbiddenException firlatir ve silmez")
    void deleteCertification_throwsCandidateResourceForbiddenException_whenNotOwnedByCandidate() {

        final Certification existing = existingCertification(5L, 1L);
        when(this.certificationRepository.findById(5L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> this.certificationService.deleteCertification(2L, 5L))
                .isInstanceOf(CandidateResourceForbiddenException.class);

        verify(this.certificationRepository, never()).deleteById(anyLong());

    }

    @Test
    @DisplayName("deleteCertification() certification bulunamadiginda CandidateResourceNotFoundException firlatir")
    void deleteCertification_throwsCandidateResourceNotFoundException_whenCertificationDoesNotExist() {

        when(this.certificationRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.certificationService.deleteCertification(1L, 404L))
                .isInstanceOf(CandidateResourceNotFoundException.class);

        verify(this.certificationRepository, never()).deleteById(anyLong());

    }

    @Test
    @DisplayName("listCertifications() candidateId ile repository'ye oldugu gibi iletir")
    void listCertifications_delegatesToRepository() {

        final List<Certification> expected = List.of(existingCertification(5L, 1L));
        when(this.certificationRepository.findAllByCandidateId(1L)).thenReturn(expected);

        final List<Certification> result = this.certificationService.listCertifications(1L);

        assertThat(result).isSameAs(expected);
        verify(this.certificationRepository).findAllByCandidateId(1L);

    }

    private static Certification existingCertification(final Long id, final Long candidateId) {
        return Certification.builder()
                .id(id)
                .candidateId(candidateId)
                .name("AWS Certified Solutions Architect")
                .issuingOrganization("Amazon Web Services")
                .issueDate(ISSUE_DATE)
                .expiryDate(EXPIRY_DATE)
                .credentialId("CRED-123")
                .credentialUrl("https://aws.amazon.com/verify/CRED-123")
                .createdAt(LocalDateTime.now())
                .build();
    }

}
