package com.deveyk.jobmatch.job.presentation.rest.mapper;

import com.deveyk.jobmatch.job.application.port.in.command.ArchiveJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.CloseJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.CreateJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.PublishJobCommand;
import com.deveyk.jobmatch.job.application.port.in.command.UpdateJobCommand;
import com.deveyk.jobmatch.job.application.port.in.query.JobListCriteria;
import com.deveyk.jobmatch.job.application.port.in.query.JobSearchCriteria;
import com.deveyk.jobmatch.job.presentation.rest.request.CreateJobRequest;
import com.deveyk.jobmatch.job.presentation.rest.request.JobListRequest;
import com.deveyk.jobmatch.job.presentation.rest.request.JobSearchRequest;
import com.deveyk.jobmatch.job.presentation.rest.request.UpdateJobRequest;
import com.deveyk.jobmatch.shared.domain.model.Location;
import com.deveyk.jobmatch.shared.domain.model.Money;
import com.deveyk.jobmatch.shared.domain.model.SalaryRange;
import com.deveyk.jobmatch.shared.presentation.rest.request.LocationRequest;
import com.deveyk.jobmatch.shared.presentation.rest.request.MoneyRequest;
import com.deveyk.jobmatch.shared.presentation.rest.request.SalaryRangeRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobRequestMapper {

    CreateJobCommand toCommand(CreateJobRequest request);

    UpdateJobCommand toCommand(UpdateJobRequest request, Long id);

    PublishJobCommand toPublishCommand(Long id, String actorId);

    CloseJobCommand toCloseCommand(Long id, String actorId);

    ArchiveJobCommand toArchiveCommand(Long id, String actorId);

    JobSearchCriteria toCriteria(JobSearchRequest request);

    JobListCriteria toCriteria(JobListRequest request);

    default Location toLocation(final LocationRequest request) {
        if (request == null) {
            return null;
        }

        return new Location(request.country(), request.city());
    }

    default Money toMoney(final MoneyRequest request) {
        if (request == null) {
            return null;
        }

        return new Money(request.amount(), request.currency());
    }

    default SalaryRange toSalaryRange(final SalaryRangeRequest request) {
        if (request == null) {
            return null;
        }

        return new SalaryRange(this.toMoney(request.min()), this.toMoney(request.max()));
    }

}
