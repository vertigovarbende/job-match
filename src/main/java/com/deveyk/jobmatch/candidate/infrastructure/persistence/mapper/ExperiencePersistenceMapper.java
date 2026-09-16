package com.deveyk.jobmatch.candidate.infrastructure.persistence.mapper;

import com.deveyk.jobmatch.candidate.domain.model.Experience;
import com.deveyk.jobmatch.candidate.infrastructure.persistence.entity.ExperienceEntity;
import com.deveyk.jobmatch.shared.domain.model.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExperiencePersistenceMapper {

    @Mapping(target = "locationCountry", source = "location.country")
    @Mapping(target = "locationCity", source = "location.city")
    ExperienceEntity toEntity(Experience experience);

    @Mapping(target = "location", source = ".")
    Experience toDomain(ExperienceEntity entity);

    default Location toLocation(final ExperienceEntity entity) {
        if (entity.getLocationCountry() == null && entity.getLocationCity() == null) {
            return null;
        }

        return new Location(entity.getLocationCountry(), entity.getLocationCity());
    }

}