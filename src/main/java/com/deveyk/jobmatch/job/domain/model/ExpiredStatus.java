package com.deveyk.jobmatch.job.domain.model;

import com.deveyk.jobmatch.job.domain.exception.InvalidJobStatusTransitionException;

public final class ExpiredStatus implements JobStatus {

    @Override
    public JobStatusType type() {
        return JobStatusType.EXPIRED;
    }

    @Override
    public boolean canEdit() {
        return false;
    }

    @Override
    public JobStatus publish() {
        throw new InvalidJobStatusTransitionException(JobStatusType.EXPIRED, "publish");
    }

    @Override
    public JobStatus close() {
        throw new InvalidJobStatusTransitionException(JobStatusType.EXPIRED, "close");
    }

    @Override
    public JobStatus archive() {
        return new ArchivedStatus();
    }

    @Override
    public JobStatus expire() {
        throw new InvalidJobStatusTransitionException(JobStatusType.EXPIRED, "expire");
    }

}
