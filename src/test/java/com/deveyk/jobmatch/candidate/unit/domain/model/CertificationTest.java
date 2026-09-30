package com.deveyk.jobmatch.candidate.unit.domain.model;

import com.deveyk.jobmatch.candidate.domain.exception.CandidateFieldInvalidException;
import com.deveyk.jobmatch.candidate.domain.model.Certification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Certification - Birim Testleri")
class CertificationTest {

    private static final Long CANDIDATE_ID = 1L;
    private static final LocalDate ISSUE_DATE = LocalDate.of(2020, 1, 15);
    private static final LocalDate EXPIRY_DATE = LocalDate.of(2023, 1, 15);

    @Test
    @DisplayName("create() tum alanlari set eder, createdAt'i doldurur ve id'yi null birakir")
    void create_setsAllFields_andLeavesIdNull() {

        final Certification certification = Certification.create(CANDIDATE_ID, "AWS Certified Solutions Architect",
                "Amazon Web Services", ISSUE_DATE, EXPIRY_DATE, "CRED-123", "https://aws.amazon.com/verify/CRED-123",
                "Associate seviyesi sertifika");

        assertThat(certification.getId()).isNull();
        assertThat(certification.getCandidateId()).isEqualTo(CANDIDATE_ID);
        assertThat(certification.getName()).isEqualTo("AWS Certified Solutions Architect");
        assertThat(certification.getIssuingOrganization()).isEqualTo("Amazon Web Services");
        assertThat(certification.getIssueDate()).isEqualTo(ISSUE_DATE);
        assertThat(certification.getExpiryDate()).isEqualTo(EXPIRY_DATE);
        assertThat(certification.getCredentialId()).isEqualTo("CRED-123");
        assertThat(certification.getCredentialUrl()).isEqualTo("https://aws.amazon.com/verify/CRED-123");
        assertThat(certification.getDescription()).isEqualTo("Associate seviyesi sertifika");
        assertThat(certification.getCreatedAt()).isNotNull();

    }

    @Test
    @DisplayName("create() expiryDate olmadan da olusturulabilir (sureli olmayan sertifika)")
    void create_allowsNullExpiryDate() {

        final Certification certification = certification(null);

        assertThat(certification.getExpiryDate()).isNull();

    }

    @Test
    @DisplayName("create() name bos oldugunda CandidateFieldInvalidException firlatir")
    void create_throwsCandidateFieldInvalidException_whenNameIsBlank() {

        assertThatThrownBy(() -> Certification.create(CANDIDATE_ID, " ", "Amazon Web Services",
                ISSUE_DATE, EXPIRY_DATE, null, null, null))
                .isInstanceOf(CandidateFieldInvalidException.class)
                .hasMessageContaining("name");

    }

    @Test
    @DisplayName("create() issuingOrganization bos oldugunda CandidateFieldInvalidException firlatir")
    void create_throwsCandidateFieldInvalidException_whenIssuingOrganizationIsBlank() {

        assertThatThrownBy(() -> Certification.create(CANDIDATE_ID, "AWS Certified Solutions Architect", null,
                ISSUE_DATE, EXPIRY_DATE, null, null, null))
                .isInstanceOf(CandidateFieldInvalidException.class)
                .hasMessageContaining("issuingOrganization");

    }

    @Test
    @DisplayName("create() issueDate null oldugunda CandidateFieldInvalidException firlatir")
    void create_throwsCandidateFieldInvalidException_whenIssueDateIsNull() {

        assertThatThrownBy(() -> Certification.create(CANDIDATE_ID, "AWS Certified Solutions Architect",
                "Amazon Web Services", null, EXPIRY_DATE, null, null, null))
                .isInstanceOf(CandidateFieldInvalidException.class)
                .hasMessageContaining("issueDate");

    }

    @Test
    @DisplayName("create() expiryDate issueDate'den once oldugunda CandidateFieldInvalidException firlatir")
    void create_throwsCandidateFieldInvalidException_whenExpiryDateBeforeIssueDate() {

        assertThatThrownBy(() -> Certification.create(CANDIDATE_ID, "AWS Certified Solutions Architect",
                "Amazon Web Services", ISSUE_DATE, ISSUE_DATE.minusDays(1), null, null, null))
                .isInstanceOf(CandidateFieldInvalidException.class)
                .hasMessageContaining("expiryDate");

    }

    @Test
    @DisplayName("update() tum alanlari gunceller, id ve candidateId degismez")
    void update_updatesAllFields_andLeavesIdAndCandidateIdUnchanged() {

        final Certification certification = certification(EXPIRY_DATE);
        final LocalDate newIssueDate = LocalDate.of(2021, 6, 1);
        final LocalDate newExpiryDate = LocalDate.of(2024, 6, 1);

        certification.update("AWS Certified Developer", "AWS", newIssueDate, newExpiryDate,
                "CRED-456", "https://aws.amazon.com/verify/CRED-456", "Guncellenmis aciklama");

        assertThat(certification.getName()).isEqualTo("AWS Certified Developer");
        assertThat(certification.getIssuingOrganization()).isEqualTo("AWS");
        assertThat(certification.getIssueDate()).isEqualTo(newIssueDate);
        assertThat(certification.getExpiryDate()).isEqualTo(newExpiryDate);
        assertThat(certification.getCredentialId()).isEqualTo("CRED-456");
        assertThat(certification.getCredentialUrl()).isEqualTo("https://aws.amazon.com/verify/CRED-456");
        assertThat(certification.getDescription()).isEqualTo("Guncellenmis aciklama");
        assertThat(certification.getId()).isNull();
        assertThat(certification.getCandidateId()).isEqualTo(CANDIDATE_ID);

    }

    @Test
    @DisplayName("update() name bos oldugunda CandidateFieldInvalidException firlatir")
    void update_throwsCandidateFieldInvalidException_whenNameIsBlank() {

        final Certification certification = certification(EXPIRY_DATE);

        assertThatThrownBy(() -> certification.update(" ", "AWS", ISSUE_DATE, EXPIRY_DATE, null, null, null))
                .isInstanceOf(CandidateFieldInvalidException.class)
                .hasMessageContaining("name");

    }

    @Test
    @DisplayName("isOwnedBy() candidateId eslestiginde true doner")
    void isOwnedBy_returnsTrue_whenCandidateIdMatches() {

        final Certification certification = certification(EXPIRY_DATE);

        assertThat(certification.isOwnedBy(CANDIDATE_ID)).isTrue();

    }

    @Test
    @DisplayName("isOwnedBy() candidateId eslesmediginde false doner")
    void isOwnedBy_returnsFalse_whenCandidateIdDiffers() {

        final Certification certification = certification(EXPIRY_DATE);

        assertThat(certification.isOwnedBy(99L)).isFalse();

    }

    @Test
    @DisplayName("isExpired() expiryDate gecmiste oldugunda true doner")
    void isExpired_returnsTrue_whenExpiryDateIsInThePast() {

        final Certification certification = certification(LocalDate.now().minusDays(1));

        assertThat(certification.isExpired()).isTrue();

    }

    @Test
    @DisplayName("isExpired() expiryDate gelecekte oldugunda false doner")
    void isExpired_returnsFalse_whenExpiryDateIsInTheFuture() {

        final Certification certification = certification(LocalDate.now().plusYears(1));

        assertThat(certification.isExpired()).isFalse();

    }

    @Test
    @DisplayName("isExpired() expiryDate null oldugunda false doner (suresiz sertifika)")
    void isExpired_returnsFalse_whenExpiryDateIsNull() {

        final Certification certification = certification(null);

        assertThat(certification.isExpired()).isFalse();

    }

    private static Certification certification(final LocalDate expiryDate) {
        return Certification.create(CANDIDATE_ID, "AWS Certified Solutions Architect", "Amazon Web Services",
                ISSUE_DATE, expiryDate, "CRED-123", "https://aws.amazon.com/verify/CRED-123", "Aciklama");
    }

}
