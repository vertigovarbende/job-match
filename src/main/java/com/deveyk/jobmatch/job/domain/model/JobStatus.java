package com.deveyk.jobmatch.job.domain.model;

public interface JobStatus {

    JobStatusType type();

    boolean canEdit();

    JobStatus publish();

    JobStatus close();

    JobStatus archive();

    JobStatus expire();

    static JobStatus of(final JobStatusType type) {
        return type.toStatus();
    }

}
