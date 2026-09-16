package com.deveyk.jobmatch.company.infrastructure.persistence.mapper;

import com.deveyk.jobmatch.company.domain.model.CompanyMembership;
import com.deveyk.jobmatch.company.infrastructure.persistence.entity.CompanyMembershipEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyMembershipPersistenceMapper {

    CompanyMembershipEntity toEntity(CompanyMembership companyMembership);

    CompanyMembership toDomain(CompanyMembershipEntity entity);

}
