package com.deveyk.jobmatch.job.infrastructure.persistence.mapper;

import com.deveyk.jobmatch.job.domain.model.Job;
import com.deveyk.jobmatch.job.domain.model.JobStatus;
import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import com.deveyk.jobmatch.job.infrastructure.persistence.entity.JobEntity;
import com.deveyk.jobmatch.shared.domain.model.Location;
import com.deveyk.jobmatch.shared.domain.model.Money;
import com.deveyk.jobmatch.shared.domain.model.SalaryRange;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface JobPersistenceMapper {

    @Mapping(target = "locationCountry", source = "location.country")
    @Mapping(target = "locationCity", source = "location.city")
    @Mapping(target = "salaryMinAmount", source = "salaryRange.min.amount")
    @Mapping(target = "salaryMaxAmount", source = "salaryRange.max.amount")
    @Mapping(target = "salaryCurrency", source = "salaryRange.min.currency")
    @Mapping(target = "status", source = "status", qualifiedByName = "toJobStatusType")
    JobEntity toEntity(Job job);

    @Mapping(target = "location", source = "entity")
    @Mapping(target = "salaryRange", source = "entity")
    @Mapping(target = "status", source = "entity.status", qualifiedByName = "toJobStatus")
    @Mapping(target = "requiredSkillIds", source = "requiredSkillIds")
    @Mapping(target = "preferredSkillIds", source = "preferredSkillIds")
    Job toDomain(JobEntity entity, Set<Long> requiredSkillIds, Set<Long> preferredSkillIds);

    @Named("toJobStatusType")
    default JobStatusType toJobStatusType(final JobStatus status) {
        return status.type();
    }

    @Named("toJobStatus")
    default JobStatus toJobStatus(final JobStatusType type) {
        return JobStatus.of(type);
    }

    default Location toLocation(final JobEntity entity) {
        if (entity.getLocationCountry() == null && entity.getLocationCity() == null) {
            return null;
        }

        return new Location(entity.getLocationCountry(), entity.getLocationCity());
    }

    default SalaryRange toSalaryRange(final JobEntity entity) {
        if (entity.getSalaryMinAmount() == null || entity.getSalaryMaxAmount() == null) {
            return null;
        }

        return new SalaryRange(
                new Money(entity.getSalaryMinAmount(), entity.getSalaryCurrency()),
                new Money(entity.getSalaryMaxAmount(), entity.getSalaryCurrency())
        );
    }

}
