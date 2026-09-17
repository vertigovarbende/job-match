package com.deveyk.jobmatch.job.domain.model;

import java.util.function.Supplier;

public enum JobStatusType {

    DRAFT(DraftStatus::new),
    PUBLISHED(PublishedStatus::new),
    CLOSED(ClosedStatus::new),
    EXPIRED(ExpiredStatus::new),
    ARCHIVED(ArchivedStatus::new);

    private final Supplier<JobStatus> factory;

    JobStatusType(final Supplier<JobStatus> factory) {
        this.factory = factory;
    }

    JobStatus toStatus() {
        return this.factory.get();
    }

}
