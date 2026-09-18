package com.deveyk.jobmatch.job.infrastructure.elasticsearch.mapper;

import com.deveyk.jobmatch.job.domain.model.Job;
import com.deveyk.jobmatch.job.domain.model.JobStatus;
import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.JobDocument;
import com.deveyk.jobmatch.shared.domain.model.Location;
import com.deveyk.jobmatch.shared.domain.model.Money;
import com.deveyk.jobmatch.shared.domain.model.SalaryRange;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface JobDocumentMapper {

    @Mapping(target = "locationCountry", source = "location.country")
    @Mapping(target = "locationCity", source = "location.city")
    @Mapping(target = "salaryMinAmount", source = "salaryRange.min.amount")
    @Mapping(target = "salaryMaxAmount", source = "salaryRange.max.amount")
    @Mapping(target = "salaryCurrency", source = "salaryRange.min.currency")
    @Mapping(target = "status", source = "status", qualifiedByName = "toJobStatusType")
    JobDocument toDocument(Job job);

    @Mapping(target = "location", source = "document")
    @Mapping(target = "salaryRange", source = "document")
    @Mapping(target = "status", source = "document.status", qualifiedByName = "toJobStatus")
    Job toDomain(JobDocument document);

    @Named("toJobStatusType")
    default JobStatusType toJobStatusType(final JobStatus status) {
        return status.type();
    }

    @Named("toJobStatus")
    default JobStatus toJobStatus(final JobStatusType type) {
        return JobStatus.of(type);
    }

    default Location toLocation(final JobDocument document) {
        if (document.getLocationCountry() == null && document.getLocationCity() == null) {
            return null;
        }

        return new Location(document.getLocationCountry(), document.getLocationCity());
    }

    default SalaryRange toSalaryRange(final JobDocument document) {
        if (document.getSalaryMinAmount() == null || document.getSalaryMaxAmount() == null) {
            return null;
        }

        return new SalaryRange(
                new Money(document.getSalaryMinAmount(), document.getSalaryCurrency()),
                new Money(document.getSalaryMaxAmount(), document.getSalaryCurrency())
        );
    }

}
