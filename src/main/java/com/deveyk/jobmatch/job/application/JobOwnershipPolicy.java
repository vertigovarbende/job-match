package com.deveyk.jobmatch.job.application;

import com.deveyk.jobmatch.company.application.CurrentCompanyFacade;
import com.deveyk.jobmatch.job.application.port.out.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobOwnershipPolicy {

    private final CurrentCompanyFacade currentCompanyFacade;
    private final JobRepository jobRepository;

    public boolean isOwner(final Long jobId) {

        if (jobId == null) {
            return false;
        }

        return this.currentCompanyFacade.resolveCurrentCompanyId()
                .flatMap(companyId -> this.jobRepository.findById(jobId)
                        .map(job -> job.isOwnedBy(companyId)))
                .orElse(false);

    }

}
