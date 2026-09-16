package com.deveyk.jobmatch.company.infrastructure.persistence.mapper;

import com.deveyk.jobmatch.company.domain.model.Company;
import com.deveyk.jobmatch.company.infrastructure.persistence.entity.CompanyEntity;
import com.deveyk.jobmatch.shared.domain.model.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyPersistenceMapper {

    @Mapping(target = "headquartersCity", source = "headquarters.city")
    @Mapping(target = "headquartersCountry", source = "headquarters.country")
    CompanyEntity toEntity(Company company);

    @Mapping(target = "headquarters", source = ".")
    Company toDomain(CompanyEntity entity);

    default Location toHeadquarters(final CompanyEntity entity) {
        if (entity.getHeadquartersCountry() == null && entity.getHeadquartersCity() == null) {
            return null;
        }

        return new Location(entity.getHeadquartersCountry(), entity.getHeadquartersCity());
    }

}
