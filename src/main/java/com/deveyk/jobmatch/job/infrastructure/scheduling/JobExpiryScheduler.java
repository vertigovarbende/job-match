package com.deveyk.jobmatch.job.infrastructure.scheduling;

import com.deveyk.jobmatch.job.application.port.in.JobUseCase;
import com.deveyk.jobmatch.job.application.port.out.JobRepository;
import com.deveyk.jobmatch.job.domain.model.Job;
import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobExpiryScheduler {

    private final JobRepository jobRepository;
    private final JobUseCase jobUseCase;

    @Scheduled(cron = "${jobmatch.job.expiry-scheduler.cron:0 */15 * * * *}")
    public void expirePublishedJobs() {

        final List<Job> expirable = this.jobRepository.findAllByStatusAndExpiresAtBefore(JobStatusType.PUBLISHED, LocalDateTime.now());

        if (expirable.isEmpty()) {
            log.debug("No published jobs to expire");
            return;
        }

        log.info("Expiring {} published job(s)", expirable.size());

        for (final Job job : expirable) {

            try {
                this.jobUseCase.expireJob(job.getId());
            } catch (final Exception exception) {
                log.error("Failed to expire jobId={}", job.getId(), exception);
            }

        }

    }

}
