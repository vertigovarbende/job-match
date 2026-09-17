package com.deveyk.jobmatch.job.domain.model;

import com.deveyk.jobmatch.job.domain.exception.InvalidJobStatusTransitionException;

public final class DraftStatus implements JobStatus {

    @Override
    public JobStatusType type() {
        return JobStatusType.DRAFT;
    }

    @Override
    public boolean canEdit() {
        return true;
    }

    @Override
    public JobStatus publish() {
        return new PublishedStatus();
    }

    @Override
    public JobStatus close() {
        throw new InvalidJobStatusTransitionException(JobStatusType.DRAFT, "close");
    }

    @Override
    public JobStatus archive() {
        return new ArchivedStatus();
    }

    @Override
    public JobStatus expire() {
        throw new InvalidJobStatusTransitionException(JobStatusType.DRAFT, "expire");
    }

}
