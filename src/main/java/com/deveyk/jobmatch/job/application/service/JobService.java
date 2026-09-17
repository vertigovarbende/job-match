package com.deveyk.jobmatch.job.application.service;

import com.deveyk.jobmatch.company.application.CurrentCompanyFacade;
import com.deveyk.jobmatch.job.application.port.in.JobUseCase;
import com.deveyk.jobmatch.job.application.port.in.command.ArchiveJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.CloseJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.CreateJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.PublishJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.UpdateJobCommand;
import com.deveyk.jobmatch.job.application.port.in.query.JobListCriteria;
import com.deveyk.jobmatch.job.application.port.in.query.JobSearchCriteria;
import com.deveyk.jobmatch.job.application.port.out.JobRepository;
import com.deveyk.jobmatch.job.domain.event.JobArchivedEvent;
import com.deveyk.jobmatch.job.domain.event.JobClosedEvent;
import com.deveyk.jobmatch.job.domain.event.JobExpiredEvent;
import com.deveyk.jobmatch.job.domain.event.JobPublishedEvent;
import com.deveyk.jobmatch.job.domain.exception.CompanyMembershipRequiredException;
import com.deveyk.jobmatch.job.domain.exception.JobNotFoundException;
import com.deveyk.jobmatch.job.domain.model.Job;
import com.deveyk.jobmatch.shared.domain.model.JmPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService implements JobUseCase {

    private final JobRepository jobRepository;
    private final CurrentCompanyFacade currentCompanyFacade;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public Job createJob(final CreateJobCommand command) {

        log.debug("Creating job: title={}", command.title());

        final Long companyId = this.currentCompanyFacade.resolveCurrentCompanyId()
                .orElseThrow(CompanyMembershipRequiredException::new);

        final Job job = Job.create(
                companyId,
                command.title(),
                command.description(),
                command.seniority(),
                command.employmentType(),
                command.workplaceType(),
                command.location(),
                command.salaryRange(),
                command.minimumExperience(),
                command.expiresAt(),
                command.requiredSkillIds(),
                command.preferredSkillIds()
        );

        final Job saved = this.jobRepository.save(job);

        log.info("Job created: jobId={}, companyId={}", saved.getId(), saved.getCompanyId());

        return saved;
    }

    @Override
    @Transactional
    @PreAuthorize("@jobOwnershipPolicy.isOwner(#command.id())")
    public Job updateJob(final UpdateJobCommand command) {

        log.debug("Updating job: id={}", command.id());

        final Job job = this.findByIdOrThrow(command.id());

        job.updateDetails(
                command.title(),
                command.description(),
                command.seniority(),
                command.employmentType(),
                command.workplaceType(),
                command.location(),
                command.salaryRange(),
                command.minimumExperience(),
                command.expiresAt(),
                command.requiredSkillIds(),
                command.preferredSkillIds()
        );

        final Job saved = this.jobRepository.save(job);

        log.info("Job updated: jobId={}", saved.getId());

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@jobOwnershipPolicy.isOwner(#jobId)")
    public Job getJobById(@P("jobId") final Long jobId) {

        log.debug("Fetching job: id={}", jobId);

        return this.findByIdOrThrow(jobId);
    }

    @Override
    @Transactional
    @PreAuthorize("@jobOwnershipPolicy.isOwner(#command.id())")
    public Job publishJob(final PublishJobCommand command) {

        log.debug("Publishing job: id={}", command.id());

        final Job job = this.findByIdOrThrow(command.id());
        job.publish();
        final Job saved = this.jobRepository.save(job);

        this.applicationEventPublisher.publishEvent(new JobPublishedEvent(command.actorId(), String.valueOf(saved.getId())));

        log.info("Job published: jobId={}", saved.getId());

        return saved;
    }

    @Override
    @Transactional
    @PreAuthorize("@jobOwnershipPolicy.isOwner(#command.id())")
    public Job closeJob(final CloseJobCommand command) {

        log.debug("Closing job: id={}", command.id());

        final Job job = this.findByIdOrThrow(command.id());
        job.close();
        final Job saved = this.jobRepository.save(job);

        this.applicationEventPublisher.publishEvent(new JobClosedEvent(command.actorId(), String.valueOf(saved.getId())));

        log.info("Job closed: jobId={}", saved.getId());

        return saved;
    }

    @Override
    @Transactional
    @PreAuthorize("@jobOwnershipPolicy.isOwner(#command.id())")
    public Job archiveJob(final ArchiveJobCommand command) {

        log.debug("Archiving job: id={}", command.id());

        final Job job = this.findByIdOrThrow(command.id());
        job.archive();
        final Job saved = this.jobRepository.save(job);

        this.applicationEventPublisher.publishEvent(new JobArchivedEvent(command.actorId(), String.valueOf(saved.getId())));

        log.info("Job archived: jobId={}", saved.getId());

        return saved;
    }

    @Override
    @Transactional
    public Job expireJob(final Long jobId) {

        log.debug("Expiring job: id={}", jobId);

        final Job job = this.findByIdOrThrow(jobId);
        job.expire();
        final Job saved = this.jobRepository.save(job);

        this.applicationEventPublisher.publishEvent(new JobExpiredEvent(String.valueOf(saved.getId())));

        log.info("Job expired: jobId={}", saved.getId());

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public JmPage<Job> searchPublishedJobs(final JobSearchCriteria criteria, final Pageable pageable) {

        log.debug("Searching published jobs");

        return this.jobRepository.findAllPublished(criteria, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public JmPage<Job> listMyJobs(final JobListCriteria criteria, final Pageable pageable) {

        final Long companyId = this.currentCompanyFacade.resolveCurrentCompanyId()
                .orElseThrow(CompanyMembershipRequiredException::new);

        log.debug("Listing jobs: companyId={}", companyId);

        return this.jobRepository.findAllForCompany(companyId, criteria, pageable);
    }

    private Job findByIdOrThrow(final Long id) {
        return this.jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException(id));
    }

}
