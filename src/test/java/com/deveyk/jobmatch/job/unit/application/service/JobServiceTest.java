package com.deveyk.jobmatch.job.unit.application.service;

import com.deveyk.jobmatch.company.application.CurrentCompanyFacade;
import com.deveyk.jobmatch.job.application.port.in.command.ArchiveJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.CloseJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.CreateJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.PublishJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.UpdateJobCommand;
import com.deveyk.jobmatch.job.application.port.in.query.JobListCriteria;
import com.deveyk.jobmatch.job.application.port.out.JobRepository;
import com.deveyk.jobmatch.job.application.service.JobService;
import com.deveyk.jobmatch.job.domain.event.JobArchivedEvent;
import com.deveyk.jobmatch.job.domain.event.JobClosedEvent;
import com.deveyk.jobmatch.job.domain.event.JobExpiredEvent;
import com.deveyk.jobmatch.job.domain.event.JobPublishedEvent;
import com.deveyk.jobmatch.job.domain.exception.CompanyMembershipRequiredException;
import com.deveyk.jobmatch.job.domain.exception.JobNotFoundException;
import com.deveyk.jobmatch.job.domain.exception.JobResourceForbiddenException;
import com.deveyk.jobmatch.job.domain.model.DraftStatus;
import com.deveyk.jobmatch.job.domain.model.Job;
import com.deveyk.jobmatch.job.domain.model.JobStatus;
import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import com.deveyk.jobmatch.job.domain.model.PublishedStatus;
import com.deveyk.jobmatch.job.testsupport.JobTestDataBuilder;
import com.deveyk.jobmatch.shared.domain.EmploymentType;
import com.deveyk.jobmatch.shared.domain.Seniority;
import com.deveyk.jobmatch.shared.domain.WorkplaceType;
import com.deveyk.jobmatch.shared.domain.model.JmPage;
import com.deveyk.jobmatch.shared.domain.model.Location;
import com.deveyk.jobmatch.shared.domain.model.Money;
import com.deveyk.jobmatch.shared.domain.model.SalaryRange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;

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
@DisplayName("JobService - Birim Testleri")
class JobServiceTest {

    private static final Location VALID_LOCATION = new Location("Turkiye", "Istanbul");
    private static final SalaryRange VALID_SALARY_RANGE = new SalaryRange(new Money(BigDecimal.valueOf(50000), "USD"), new Money(BigDecimal.valueOf(80000), "USD"));

    @Mock
    private JobRepository jobRepository;

    @Mock
    private CurrentCompanyFacade currentCompanyFacade;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private JobService jobService;

    @Test
    @DisplayName("createJob() company membership varsa job'u olusturup kaydeder")
    void createJob_savesJobForCurrentCompany_whenCompanyMembershipExists() {

        when(this.currentCompanyFacade.resolveCurrentCompanyId()).thenReturn(Optional.of(1L));
        when(this.jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final CreateJobCommand command = CreateJobCommand.builder()
                .title("Backend Developer")
                .description("description")
                .seniority(Seniority.MID_SENIOR)
                .employmentType(EmploymentType.FULL_TIME)
                .workplaceType(WorkplaceType.REMOTE)
                .location(VALID_LOCATION)
                .salaryRange(VALID_SALARY_RANGE)
                .minimumExperience(3)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .requiredSkillIds(Set.of(1L))
                .preferredSkillIds(Set.of(2L))
                .build();

        final Job result = this.jobService.createJob(command);

        assertThat(result.getCompanyId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Backend Developer");
        assertThat(result.getStatus().type()).isEqualTo(JobStatusType.DRAFT);
        verify(this.jobRepository).save(any(Job.class));

    }

    @Test
    @DisplayName("createJob() company membership yoksa CompanyMembershipRequiredException firlatir ve kaydetmez")
    void createJob_throwsCompanyMembershipRequiredException_whenNoCompanyMembership() {

        when(this.currentCompanyFacade.resolveCurrentCompanyId()).thenReturn(Optional.empty());

        final CreateJobCommand command = CreateJobCommand.builder()
                .title("Backend Developer")
                .description("description")
                .seniority(Seniority.MID_SENIOR)
                .employmentType(EmploymentType.FULL_TIME)
                .workplaceType(WorkplaceType.REMOTE)
                .location(VALID_LOCATION)
                .salaryRange(VALID_SALARY_RANGE)
                .minimumExperience(3)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .requiredSkillIds(Set.of())
                .preferredSkillIds(Set.of())
                .build();

        assertThatThrownBy(() -> this.jobService.createJob(command))
                .isInstanceOf(CompanyMembershipRequiredException.class);

        verify(this.jobRepository, never()).save(any());

    }

    @Test
    @DisplayName("updateJob() mevcut job'u gunceller ve kaydeder")
    void updateJob_updatesAndSavesExistingJob() {

        final Job existing = existingJob(5L, 1L, new DraftStatus());
        when(this.jobRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(this.jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(this.currentCompanyFacade.resolveCurrentCompanyId()).thenReturn(Optional.of(1L));

        final UpdateJobCommand command = UpdateJobCommand.builder()
                .id(5L)
                .title("Senior Backend Developer")
                .description("Updated description")
                .seniority(Seniority.DIRECTOR)
                .employmentType(EmploymentType.CONTRACT)
                .workplaceType(WorkplaceType.HYBRID)
                .location(VALID_LOCATION)
                .salaryRange(VALID_SALARY_RANGE)
                .minimumExperience(5)
                .expiresAt(LocalDateTime.now().plusDays(60))
                .requiredSkillIds(Set.of(3L))
                .preferredSkillIds(Set.of(4L))
                .build();

        final Job result = this.jobService.updateJob(command);

        assertThat(result.getTitle()).isEqualTo("Senior Backend Developer");
        assertThat(result.getDescription()).isEqualTo("Updated description");
        verify(this.jobRepository).save(existing);

    }

    @Test
    @DisplayName("updateJob() job bulunamadiginda JobNotFoundException firlatir")
    void updateJob_throwsJobNotFoundException_whenJobDoesNotExist() {

        when(this.jobRepository.findById(99L)).thenReturn(Optional.empty());

        final UpdateJobCommand command = UpdateJobCommand.builder()
                .id(99L)
                .title("Title")
                .description("description")
                .seniority(Seniority.MID_SENIOR)
                .employmentType(EmploymentType.FULL_TIME)
                .workplaceType(WorkplaceType.REMOTE)
                .location(VALID_LOCATION)
                .salaryRange(VALID_SALARY_RANGE)
                .minimumExperience(3)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .requiredSkillIds(Set.of())
                .preferredSkillIds(Set.of())
                .build();

        assertThatThrownBy(() -> this.jobService.updateJob(command))
                .isInstanceOf(JobNotFoundException.class);

    }

    @Test
    @DisplayName("updateJob() job baska sirkete aitse JobResourceForbiddenException firlatir")
    void updateJob_throwsJobResourceForbiddenException_whenJobBelongsToAnotherCompany() {

        final Job existing = existingJob(20L, 2L, new DraftStatus());
        when(this.jobRepository.findById(20L)).thenReturn(Optional.of(existing));
        when(this.currentCompanyFacade.resolveCurrentCompanyId()).thenReturn(Optional.of(1L));

        final UpdateJobCommand command = UpdateJobCommand.builder()
                .id(20L)
                .title("Title")
                .description("description")
                .seniority(Seniority.MID_SENIOR)
                .employmentType(EmploymentType.FULL_TIME)
                .workplaceType(WorkplaceType.REMOTE)
                .location(VALID_LOCATION)
                .salaryRange(VALID_SALARY_RANGE)
                .minimumExperience(3)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .requiredSkillIds(Set.of())
                .preferredSkillIds(Set.of())
                .build();

        assertThatThrownBy(() -> this.jobService.updateJob(command))
                .isInstanceOf(JobResourceForbiddenException.class);

        verify(this.jobRepository, never()).save(any());

    }

    @Test
    @DisplayName("getJobById() job bulundugunda dondurur")
    void getJobById_returnsJob_whenFound() {

        final Job existing = existingJob(5L, 1L, new DraftStatus());
        when(this.jobRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(this.currentCompanyFacade.resolveCurrentCompanyId()).thenReturn(Optional.of(1L));

        final Job result = this.jobService.getJobById(5L);

        assertThat(result).isSameAs(existing);

    }

    @Test
    @DisplayName("getJobById() job bulunamadiginda JobNotFoundException firlatir")
    void getJobById_throwsJobNotFoundException_whenNotFound() {

        when(this.jobRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.jobService.getJobById(404L))
                .isInstanceOf(JobNotFoundException.class);

    }

    @Test
    @DisplayName("getJobById() job baska sirkete aitse JobResourceForbiddenException firlatir")
    void getJobById_throwsJobResourceForbiddenException_whenJobBelongsToAnotherCompany() {

        final Job existing = existingJob(21L, 2L, new DraftStatus());
        when(this.jobRepository.findById(21L)).thenReturn(Optional.of(existing));
        when(this.currentCompanyFacade.resolveCurrentCompanyId()).thenReturn(Optional.of(1L));

        assertThatThrownBy(() -> this.jobService.getJobById(21L))
                .isInstanceOf(JobResourceForbiddenException.class);

    }

    @Test
    @DisplayName("publishJob() job'u PUBLISHED yapar ve JobPublishedEvent yayinlar")
    void publishJob_publishesJobAndEmitsJobPublishedEvent() {

        final Job existing = existingJob(7L, 1L, new DraftStatus());
        when(this.jobRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(this.jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(this.currentCompanyFacade.resolveCurrentCompanyId()).thenReturn(Optional.of(1L));

        final Job result = this.jobService.publishJob(new PublishJobCommand(7L, "actor-1"));

        assertThat(result.getStatus().type()).isEqualTo(JobStatusType.PUBLISHED);

        final ArgumentCaptor<JobPublishedEvent> captor = ArgumentCaptor.forClass(JobPublishedEvent.class);
        verify(this.applicationEventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().actorId()).isEqualTo("actor-1");
        assertThat(captor.getValue().targetId()).isEqualTo("7");

    }

    @Test
    @DisplayName("publishJob() job bulunamadiginda JobNotFoundException firlatir")
    void publishJob_throwsJobNotFoundException_whenNotFound() {

        when(this.jobRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.jobService.publishJob(new PublishJobCommand(404L, "actor-1")))
                .isInstanceOf(JobNotFoundException.class);

    }

    @Test
    @DisplayName("publishJob() job baska sirkete aitse JobResourceForbiddenException firlatir")
    void publishJob_throwsJobResourceForbiddenException_whenJobBelongsToAnotherCompany() {

        final Job existing = existingJob(22L, 2L, new DraftStatus());
        when(this.jobRepository.findById(22L)).thenReturn(Optional.of(existing));
        when(this.currentCompanyFacade.resolveCurrentCompanyId()).thenReturn(Optional.of(1L));

        assertThatThrownBy(() -> this.jobService.publishJob(new PublishJobCommand(22L, "actor-1")))
                .isInstanceOf(JobResourceForbiddenException.class);

        verify(this.jobRepository, never()).save(any());

    }

    @Test
    @DisplayName("closeJob() PUBLISHED job'u CLOSED yapar ve JobClosedEvent yayinlar")
    void closeJob_closesJobAndEmitsJobClosedEvent() {

        final Job existing = existingJob(8L, 1L, new PublishedStatus());
        when(this.jobRepository.findById(8L)).thenReturn(Optional.of(existing));
        when(this.jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(this.currentCompanyFacade.resolveCurrentCompanyId()).thenReturn(Optional.of(1L));

        final Job result = this.jobService.closeJob(new CloseJobCommand(8L, "actor-1"));

        assertThat(result.getStatus().type()).isEqualTo(JobStatusType.CLOSED);

        final ArgumentCaptor<JobClosedEvent> captor = ArgumentCaptor.forClass(JobClosedEvent.class);
        verify(this.applicationEventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().actorId()).isEqualTo("actor-1");
        assertThat(captor.getValue().targetId()).isEqualTo("8");

    }

    @Test
    @DisplayName("closeJob() job bulunamadiginda JobNotFoundException firlatir")
    void closeJob_throwsJobNotFoundException_whenNotFound() {

        when(this.jobRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.jobService.closeJob(new CloseJobCommand(404L, "actor-1")))
                .isInstanceOf(JobNotFoundException.class);

    }

    @Test
    @DisplayName("closeJob() job baska sirkete aitse JobResourceForbiddenException firlatir")
    void closeJob_throwsJobResourceForbiddenException_whenJobBelongsToAnotherCompany() {

        final Job existing = existingJob(23L, 2L, new PublishedStatus());
        when(this.jobRepository.findById(23L)).thenReturn(Optional.of(existing));
        when(this.currentCompanyFacade.resolveCurrentCompanyId()).thenReturn(Optional.of(1L));

        assertThatThrownBy(() -> this.jobService.closeJob(new CloseJobCommand(23L, "actor-1")))
                .isInstanceOf(JobResourceForbiddenException.class);

        verify(this.jobRepository, never()).save(any());

    }

    @Test
    @DisplayName("archiveJob() DRAFT job'u ARCHIVED yapar ve JobArchivedEvent yayinlar")
    void archiveJob_archivesJobAndEmitsJobArchivedEvent() {

        final Job existing = existingJob(9L, 1L, new DraftStatus());
        when(this.jobRepository.findById(9L)).thenReturn(Optional.of(existing));
        when(this.jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(this.currentCompanyFacade.resolveCurrentCompanyId()).thenReturn(Optional.of(1L));

        final Job result = this.jobService.archiveJob(new ArchiveJobCommand(9L, "actor-1"));

        assertThat(result.getStatus().type()).isEqualTo(JobStatusType.ARCHIVED);

        final ArgumentCaptor<JobArchivedEvent> captor = ArgumentCaptor.forClass(JobArchivedEvent.class);
        verify(this.applicationEventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().actorId()).isEqualTo("actor-1");
        assertThat(captor.getValue().targetId()).isEqualTo("9");

    }

    @Test
    @DisplayName("archiveJob() job bulunamadiginda JobNotFoundException firlatir")
    void archiveJob_throwsJobNotFoundException_whenNotFound() {

        when(this.jobRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.jobService.archiveJob(new ArchiveJobCommand(404L, "actor-1")))
                .isInstanceOf(JobNotFoundException.class);

    }

    @Test
    @DisplayName("archiveJob() job baska sirkete aitse JobResourceForbiddenException firlatir")
    void archiveJob_throwsJobResourceForbiddenException_whenJobBelongsToAnotherCompany() {

        final Job existing = existingJob(24L, 2L, new DraftStatus());
        when(this.jobRepository.findById(24L)).thenReturn(Optional.of(existing));
        when(this.currentCompanyFacade.resolveCurrentCompanyId()).thenReturn(Optional.of(1L));

        assertThatThrownBy(() -> this.jobService.archiveJob(new ArchiveJobCommand(24L, "actor-1")))
                .isInstanceOf(JobResourceForbiddenException.class);

        verify(this.jobRepository, never()).save(any());

    }

    @Test
    @DisplayName("expireJob() PUBLISHED job'u EXPIRED yapar ve actorId'siz/SYSTEM'li JobExpiredEvent yayinlar")
    void expireJob_expiresJobAndEmitsJobExpiredEventWithoutActorId() {

        final Job existing = existingJob(10L, 1L, new PublishedStatus());
        when(this.jobRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(this.jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final Job result = this.jobService.expireJob(10L);

        assertThat(result.getStatus().type()).isEqualTo(JobStatusType.EXPIRED);

        final ArgumentCaptor<JobExpiredEvent> captor = ArgumentCaptor.forClass(JobExpiredEvent.class);
        verify(this.applicationEventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().targetId()).isEqualTo("10");
        assertThat(captor.getValue().actorId()).isEqualTo("SYSTEM");

    }

    @Test
    @DisplayName("expireJob() job bulunamadiginda JobNotFoundException firlatir")
    void expireJob_throwsJobNotFoundException_whenNotFound() {

        when(this.jobRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.jobService.expireJob(404L))
                .isInstanceOf(JobNotFoundException.class);

    }

    @Test
    @DisplayName("listMyJobs() company membership varsa companyId ile repository'ye iletir")
    void listMyJobs_delegatesToRepositoryWithCurrentCompanyId() {

        when(this.currentCompanyFacade.resolveCurrentCompanyId()).thenReturn(Optional.of(1L));

        final JobListCriteria criteria = JobListCriteria.builder()
                .status(JobStatusType.PUBLISHED)
                .skillIds(Set.of())
                .build();
        final Pageable pageable = Pageable.ofSize(20);
        final JmPage<Job> expected = JmPage.<Job>builder().build();
        when(this.jobRepository.findAllForCompany(1L, criteria, pageable)).thenReturn(expected);

        final JmPage<Job> result = this.jobService.listMyJobs(criteria, pageable);

        assertThat(result).isSameAs(expected);

    }

    @Test
    @DisplayName("listMyJobs() company membership yoksa CompanyMembershipRequiredException firlatir")
    void listMyJobs_throwsCompanyMembershipRequiredException_whenNoCompanyMembership() {

        when(this.currentCompanyFacade.resolveCurrentCompanyId()).thenReturn(Optional.empty());

        final JobListCriteria criteria = JobListCriteria.builder()
                .skillIds(Set.of())
                .build();

        assertThatThrownBy(() -> this.jobService.listMyJobs(criteria, Pageable.ofSize(20)))
                .isInstanceOf(CompanyMembershipRequiredException.class);

    }

    private static Job existingJob(final Long id, final Long companyId, final JobStatus status) {
        return JobTestDataBuilder.aJob()
                .withId(id)
                .withCompanyId(companyId)
                .withStatus(status)
                .buildPersisted();
    }

}
