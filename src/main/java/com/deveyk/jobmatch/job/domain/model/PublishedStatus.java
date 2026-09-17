package com.deveyk.jobmatch.job.domain.model;

import com.deveyk.jobmatch.job.domain.exception.InvalidJobStatusTransitionException;

public final class PublishedStatus implements JobStatus {

    @Override
    public JobStatusType type() {
        return JobStatusType.PUBLISHED;
    }

    @Override
    public boolean canEdit() {
        return false;
    }

    @Override
    public JobStatus publish() {
        throw new InvalidJobStatusTransitionException(JobStatusType.PUBLISHED, "publish");
    }

    @Override
    public JobStatus close() {
        return new ClosedStatus();
    }

    @Override
    public JobStatus archive() {
        throw new InvalidJobStatusTransitionException(JobStatusType.PUBLISHED, "archive");
    }

    @Override
    public JobStatus expire() {
        return new ExpiredStatus();
    }

}
