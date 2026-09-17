package com.deveyk.jobmatch.candidate.infrastructure.persistence.mapper;

import com.deveyk.jobmatch.shared.domain.WorkplaceType;
import com.deveyk.jobmatch.candidate.domain.model.Candidate;
import com.deveyk.jobmatch.shared.domain.model.Money;
import com.deveyk.jobmatch.shared.domain.model.SalaryRange;
import com.deveyk.jobmatch.candidate.domain.model.WorkplacePreferences;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.entity.CandidateEntity;
import com.deveyk.jobmatch.shared.domain.model.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.EnumSet;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface CandidatePersistenceMapper {

    @Mapping(target = "locationCountry", source = "location.country")
    @Mapping(target = "locationCity", source = "location.city")
    @Mapping(target = "desiredSalaryMinAmount", source = "desiredSalary.min.amount")
    @Mapping(target = "desiredSalaryMaxAmount", source = "desiredSalary.max.amount")
    @Mapping(target = "desiredSalaryCurrency", source = "desiredSalary.min.currency")
    @Mapping(target = "openToOnSite", source = "workplacePreferences", qualifiedByName = "openToOnSite")
    @Mapping(target = "openToRemote", source = "workplacePreferences", qualifiedByName = "openToRemote")
    @Mapping(target = "openToHybrid", source = "workplacePreferences", qualifiedByName = "openToHybrid")
    CandidateEntity toEntity(Candidate candidate);

    @Mapping(target = "location", source = ".")
    @Mapping(target = "desiredSalary", source = ".")
    @Mapping(target = "workplacePreferences", source = ".")
    Candidate toDomain(CandidateEntity entity);

    @Named("openToOnSite")
    default Boolean openToOnSite(final WorkplacePreferences workplacePreferences) {
        if (workplacePreferences != null) {
            return workplacePreferences.isOpenTo(WorkplaceType.ON_SITE);
        } else {
            return null;
        }

    }

    @Named("openToRemote")
    default Boolean openToRemote(final WorkplacePreferences workplacePreferences) {
        if (workplacePreferences != null) {
            return workplacePreferences.isOpenTo(WorkplaceType.REMOTE);
        } else {
            return null;
        }

    }

    @Named("openToHybrid")
    default Boolean openToHybrid(final WorkplacePreferences workplacePreferences) {
        if (workplacePreferences != null) {
            return workplacePreferences.isOpenTo(WorkplaceType.HYBRID);
        } else {
            return null;
        }

    }

    default Location toLocation(final CandidateEntity entity) {
        if (entity.getLocationCountry() == null && entity.getLocationCity() == null) {
            return null;
        }

        return new Location(entity.getLocationCountry(), entity.getLocationCity());
    }

    default SalaryRange toSalaryRange(final CandidateEntity entity) {
        if (entity.getDesiredSalaryMinAmount() == null || entity.getDesiredSalaryMaxAmount() == null) {
            return null;
        }

        return new SalaryRange(
                new Money(entity.getDesiredSalaryMinAmount(), entity.getDesiredSalaryCurrency()),
                new Money(entity.getDesiredSalaryMaxAmount(), entity.getDesiredSalaryCurrency())
        );
    }

    default WorkplacePreferences toWorkplacePreferences(final CandidateEntity entity) {

        final Set<WorkplaceType> acceptedTypes = EnumSet.noneOf(WorkplaceType.class);

        if (Boolean.TRUE.equals(entity.getOpenToOnSite())) {
            acceptedTypes.add(WorkplaceType.ON_SITE);
        }

        if (Boolean.TRUE.equals(entity.getOpenToRemote())) {
            acceptedTypes.add(WorkplaceType.REMOTE);
        }

        if (Boolean.TRUE.equals(entity.getOpenToHybrid())) {
            acceptedTypes.add(WorkplaceType.HYBRID);
        }

        if (acceptedTypes.isEmpty()) {
            return null;
        } else {
            return new WorkplacePreferences(acceptedTypes);
        }

    }
}