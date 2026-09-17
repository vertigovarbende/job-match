package com.deveyk.jobmatch.job.application.port.in;

import com.deveyk.jobmatch.job.application.port.in.command.ArchiveJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.CloseJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.CreateJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.PublishJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.UpdateJobCommand;
import com.deveyk.jobmatch.job.domain.model.Job;

public interface JobUseCase {

    Job createJob(CreateJobCommand command);

    Job updateJob(UpdateJobCommand command);

    Job getJobById(Long jobId);

    Job publishJob(PublishJobCommand command);

    Job closeJob(CloseJobCommand command);

    Job archiveJob(ArchiveJobCommand command);

    Job expireJob(Long jobId);

}
