package com.deveyk.jobmatch.job.domain.model;

import com.deveyk.jobmatch.job.domain.exception.InvalidJobStatusTransitionException;

public final class ArchivedStatus implements JobStatus {

    @Override
    public JobStatusType type() {
        return JobStatusType.ARCHIVED;
    }

    @Override
    public boolean canEdit() {
        return false;
    }

    @Override
    public JobStatus publish() {
        throw new InvalidJobStatusTransitionException(JobStatusType.ARCHIVED, "publish");
    }

    @Override
    public JobStatus close() {
        throw new InvalidJobStatusTransitionException(JobStatusType.ARCHIVED, "close");
    }

    @Override
    public JobStatus archive() {
        throw new InvalidJobStatusTransitionException(JobStatusType.ARCHIVED, "archive");
    }

    @Override
    public JobStatus expire() {
        throw new InvalidJobStatusTransitionException(JobStatusType.ARCHIVED, "expire");
    }

}
